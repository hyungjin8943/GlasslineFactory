package factory.agent;
import factory.agent.GlassOrder;
import factory.controller.ConveyorController;
import factory.interfaces.Conveyor;
import factory.interfaces.ConveyorInteractor;
import factory.interfaces.Disaster;
import factory.interfaces.Popup;
import factory.mock.log.EventLog;
import factory.mock.log.LoggedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import agent.Agent;
import agent.MessageTracePool;

/**
 * [TRUNK] Class ConveyorAgent
 * @author Harman
 * @since 2.0
 * @category Agents
 */
public class ConveyorAgent extends Agent implements Conveyor, Disaster {
	//TODO remove debug when finalized Agent
	public boolean debug;
	public boolean exiting;
	
	//TODO Testing
	public EventLog log = new EventLog();
	
	/**
	 * ==============================================
	 * [DATA] ConveyorAgent Data
	 * ==============================================
	 */
	
	/**
	 * enum GlassState
	 * NO_ACTION: Nothing happening to the Glass
	 * ENTERED: Glass hits the conveyor's entry sensor. Invoked by (@link ConveyorInteractor)
	 * WANT_TO_LEAVE: Glass hits the conveyor's exit sensor. Invoked by (@link GuiConveyor)
	 * CANT_LEAVE: A result of {@link ConveyorInteractor#msgHereIsMyAnswer(boolean) where the boolean is false. 
	 */
	enum GlassState { NO_ACTION, ENTERED, WANT_TO_LEAVE, ON_CONVEYOR, CANT_LEAVE, LEAVING, EXITING, EXITED}	
	
	private class MyGlassOrder { 
		GlassOrder order;
		GlassState state;
		public MyGlassOrder(GlassOrder order) { 
			this.order = order;
			state = GlassState.ENTERED;
		}
	}
	
	// Interactions with the Agent
	ConveyorInteractor destination;
	ConveyorInteractor source;
	
	// API
	ConveyorController controller;
	public Semaphore animating = new Semaphore(0,true);
	
	// sensors
	public boolean entrySensor;

	public boolean receiving; 
	public boolean conveyorSwitch = true;
	
	//Other
	public String name;

	
	
	/**
	 * Synchronized List of MyGlassOrder orders
	 * Synchronized since the source can feed the Agent a glass while a glass desires to exit. 
	 */
	public List<MyGlassOrder> orders = Collections.synchronizedList(new ArrayList<MyGlassOrder>());
	
	
	/**
	 * Default constructor
	 * @param name sets the Agents name
	 */
	public ConveyorAgent(String name) {
		super();
		this.name = name;
	}
	
	
	
	
	/**
	 * ==============================================
	 * [MESSAGES]
	 * ==============================================
	 */
	
	/**
	 * (0 of 3 in Conveyor Source Interaction Diagram)
	 * [MESSAGE]: A message sent by the GuiConveyor informing the Agent that a glass has hit the entry sensor.
	 * May interrupt the Conveyor's source from feeding glasses //TODO to the conveyor.
	 * @see Conveyor#msgEntrySensorStatus(boolean)
	 */
	public void msgEntrySensorStatus(boolean state) { 
		this.entrySensor = state;
		stateChanged();
	}
	
	/**
	 * (1 of 3 in Conveyor Source Interaction Diagram)
	 * [MESSAGE]: A message sent by the ConveyorInteractor source. Should be invoked when a source needs to feed an Agent.
	 * Invokes {@link #sendStatus()}, sends {@link ConveyorInteractor#msgHereIsMyAnswer(GlassOrder, boolean) to source.
	 * @see Conveyor#msgCanYouTakeGlass()
	 */
	public void msgCanYouTakeGlass() { 
		receiving = true;
		stateChanged();	
	}
	
	/**
	 * (3 of 3 in Conveyor Source Interaction Diagram)
	 * [MESSAGE] : A message sent by ConveyorInteractor source as a result of {@link ConveyorInteractor#msgHereIsMyAnswer(GlassOrder, boolean)}
	 * @param GlassOrder the glassOrder that is received.
	 * @see Conveyor#msgCanYouTakeGlass()
	 */
	public void msgHereIsGlass(GlassOrder g) {
		log("Recieved msgHereIsAGlass");
		orders.add(new MyGlassOrder(g));
		stateChanged();
	}
	
	
	/**
	 * (1 of 4 in Conveyor Destination Interaction Diagram)
	 * [MESSAGE]: A message sent by the GUI when a glass wants to exit the Conveyor.
	 * 
	 */
	public void msgGlassWantsToExit() {
		exiting = true;
		log("Recieved msgGlassWantsToExit");
		synchronized(orders) { 
			for(MyGlassOrder mg: orders) {
				if(mg.state == GlassState.ON_CONVEYOR && mg.state != GlassState.WANT_TO_LEAVE) { 
					log("[MESSAGE msgExitSensorStatus]: " + mg.order.getName() + ".state = GlassState.WANT_TO_LEAVE");	
					mg.state = GlassState.WANT_TO_LEAVE;	
					stateChanged();
					return;
				}
			}
		}

	}
	
	
	
	/**
	 * (3 of 4 in Conveyor Destination Interaction Diagram)
	 * [MESSAGE]: A message sent by the ConveyorInteractor destination as a result of {@link #msgCanYouTakeGlass()} from the Agent's scheduler
	 * @param GlassOrder the GlassOrder that needs to be sent out
	 * @param answer if true, then GlassState.LEAVE is set, causes Agent's scheduler to be invoked
	 * @see Conveyor#msgHereIsMyAnswer(GlassOrder, boolean)
	 */
	public void msgHereIsMyAnswer(GlassOrder go, boolean answer) {
		synchronized(orders) {
			for(MyGlassOrder mg: orders) {
				// if(there exists an order where go == mg.order in orders)
				if(go == mg.order) {
					if(answer && orders.get(0).state != GlassState.EXITING) { // yes 
						log("Recieved msgHereIsMyAnswer(GlassOrder " + go.getName() + "," + answer + ")");
						conveyorSwitch = true;
						mg.state = GlassState.LEAVING;
					}
					else {
						// we cant leave yet.
						mg.state = GlassState.WANT_TO_LEAVE;
						conveyorSwitch = false;
					}
					// this should be the 1st one. 
					stateChanged();
					return;
				}
			}
		}

			
	}
	
	/**
	 * (4+ of 4 in Conveyor Destination Interaction Diagram)
	 * [MESSAGE]: If we got the [OK] to transfer, then it causes a semaphore lock. This is the release mechanism. 
	 * This is invoked when the GUI is finished passing to the next GUIPart. 
	 */
	public void msgAnimationDone() {
		log("Received msgAnimationDone()");
		if(destination instanceof GlassRobotAgent) { 
			animating.release();
		}
		else {
			synchronized(orders) { 
				for(MyGlassOrder mg: orders) {
					if(mg.state == GlassState.EXITING) {
						mg.state = GlassState.EXITED;
						log(mg.order.getName() + "- has EXITED state");
					}
				}
			}
		}
		
		stateChanged();
	}
	

	
	
	
/**
 * [SCHEDULER]  Agents must implement this scheduler to perform any actions appropriate for the
 * current state.  Will be called whenever a state change has occurred, and will be called repeated 
 * as long as it returns true.
 * @return true iff some action was executed that might have changed the state.
 */
	public boolean pickAndExecuteAnAction() {
		// At all times, we determine whether or not the Conveyor should be moving or not.
			setGUIState(conveyorSwitch);

		// we are trying to receive at this moment, send the status to the receiver.
		if(receiving) { 
			//log("[SCHEDULER]: Sending Status of Conveyor");
			sendStatus();
			// dont need to return true because we might want to give away at the same time
			//return true;
		}
		
		
		if(!orders.isEmpty()) {
			
			synchronized(orders) {	
				// the glass is leaving to the destination
				for(MyGlassOrder mg: orders) {
					//System.out.println(mg.order.getName() + "-" +mg.state);
					if(mg.state == GlassState.LEAVING) {
						//log("[SCHEDULER]: (GlassOrder " + mg.order.getName() + "," + mg.state + ") invokes leavingConveyor(" + mg.order.getName() +")");
						if(destination instanceof GlassRobotAgent) { 
							leavingToGlassRobot(mg);
						}
						else {
							leavingConveyor(mg);
						}
						
						return true;
					}
				}
			}
			
	
			synchronized(orders) {
				for(MyGlassOrder mg: orders) { 
					if(mg.state == GlassState.EXITED) {
						//log("[SCHEDULER]: (GlassOrder " + mg.order.getName() + "," + mg.state + ") invokes leftConveyor(" + mg.order.getName() +")");
						leftConveyor(mg);
						return true;
					}
				}
			}
	
			synchronized(orders) {
				// the glass wants to leave, but needs to ask the destination
				for(MyGlassOrder mg: orders) { 
					if(mg.state == GlassState.WANT_TO_LEAVE) {
						//log("[SCHEDULER]: (GlassOrder " + mg.order.getName() + "," + mg.state + ") invokes ask(" + mg.order.getName() +")");
						ask(mg);
						return true;
					}
				}
			}
		}
		
		synchronized(orders) {
					for(MyGlassOrder mg: orders) { 
						if(mg.state == GlassState.ENTERED) {
							//log("[SCHEDULER]: (GlassOrder " + mg.order.getName() + "," + mg.state + ") invokes enter(" + mg.order.getName() +")");
							enter(mg);
							return true;
						}
					}
				
			}
			
		
		return false;
	}

	
	/**
	 * ==============================================
	 * [ACTIONS]
	 * ==============================================
	 */
	
	/**
	 * [ACTION]: turns off the GUI
	 */
	public void setGUIState(boolean conveyorSwitch) {
		if(conveyorSwitch) { 
			controller.turnConveyorOn();
		}
		else { 
			controller.turnConveyorOff();
		}
	}

	/**
	 * [ACTION]: sends status to source
	 */
	public void sendStatus() {
		//log("[ACTION sendStatus()]: invoked " + source + ".msgHereIsMyAnswer(" + conveyorSwitch +"), receiving="+receiving);
		// if it's on and nothing is on the entry sensor
		if(conveyorSwitch && !entrySensor) { 
			source.msgHereIsMyAnswer(true);
			receiving = false;
			stateChanged();
		}
	}

	/**
	 * [ACTION] Turns on the Conveyor after Glass has entered. If destination instanceof Popup invoke popup.msgGlassOnTheWay(mg.order)
	 * @param MyGlassOrder
	 */
	public void enter(MyGlassOrder mg) {
		log("[ACTION enter( "+mg.order.getName()+")]: Glass is now on Conveyor");
		mg.state = GlassState.ON_CONVEYOR;
		if(destination instanceof Popup) {
			log("[ACTION enter( "+mg.order.getName()+")]: popup.msgGlassOnTheWay");
			Popup popup = (Popup)destination;
			popup.msgGlassOnTheWay(mg.order);
		}
		//stateChanged();
	}
	

	/**
	 * [ACTION] Asks destination if it could take glass
	 * @param MyGlassOrder
	 */
	public void ask(MyGlassOrder mg) {
	//	log("[ACTION ask(GlassOrder "+mg.order.getName()+")]");
		conveyorSwitch = false;
		controller.turnConveyorOff();
		destination.msgCanYouTakeGlass(mg.order);
		mg.state = GlassState.NO_ACTION;
	}
	/**
	 * [ACTION] Result of destination saying [OK], it could take glass; causes glass to leave and does a semaphore lock.
	 * @param MyGlassOrder
	 */
	public void leavingConveyor(MyGlassOrder mg) {
		log("[ACTION leavingConveyor(GlassOrder "+mg.order.getName()+")]: now has EXITING state");
			mg.state = GlassState.EXITING;
			controller.passGlass();
	}
	
	
	public void leavingToGlassRobot(MyGlassOrder mg) { 
		log("[ACTION leavingConveyor(GlassOrder "+mg.order.getName()+")]: now has EXITED state");
		mg.state = GlassState.EXITED;
		controller.passToRobotArm();
		
		try {
			animating.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		leftConveyor(mg);
	}
	
	
	/**
	 * Separation of leavingConveyor. This tells me that the GuiConveyor doesn't have it either, and it has passed on to the next person.
	 * @param mg
	 */
	public void leftConveyor(MyGlassOrder mg) {
		exiting = false;
		log("[ACTION leavingConveyor(GlassOrder "+mg.order.getName()+")]: Gave destination glass & removed glass from list");
		destination.msgHereIsGlass(mg.order);
		orders.remove(mg);
		stateChanged();
	}
	
	/**
	 * ==============================================
	 * [OTHER]
	 * ==============================================
	 */
	
	
	/**
	 * Sets the Conveyor's source
	 * @param source
	 */
	public void setSource(ConveyorInteractor source) { 
		this.source = source;
	}
	
	/**
	 * Sets the Conveyor's destination
	 * @param destination
	 */
	public void setDestination(ConveyorInteractor destination) { 
		this.destination = destination;
	}
	
	/**
	 * Sets the Controller
	 * @param controller
	 */
	public void setController(ConveyorController controller) { 
		this.controller = controller;
	}


	/**
	 * Gets the name of the Conveyor.
	 * @overrides Agent.getName()
	 */
	public String getName() { 
		return this.name;
	}
	
	/**
	 * TESTING
	 */
	
	/**
	 * Logs an event and adds it to the log of the Agent.
	 */
	public void log(String description) {
		if(log != null) {
			MessageTracePool.add(description, this);
			//LoggedEvent event = new LoggedEvent(description);
			//log.add(event);
			//if(debug) print(event.getMessage());
		}
		
	}
	
	/**
	 * NON-NORMATIVES
	 */
	
	/**
	 * Conveyor ON or OFF
	 */
	public void setConveyorState(boolean x) {
		if(!exiting) {
    	conveyorSwitch = x;
		}
    	stateChanged();
	}




	public void msgBreakAllGlasses() {
		// TODO Auto-generated method stub
		synchronized(orders) { 
			for(MyGlassOrder mg: orders) { 
				mg.order.setBroken(true);
			}
		}
	}
	
	/**
	 * Suppose exit sensor was ON all the time. What does this mean?
	 * If exit sensor was ON, then msgGlassWantsToExit() will be invoked and the 1st glass that arrived on
	 * the conveyor will be set to WANT_TO_LEAVE.
	 * It will then ask the destination, msgCanYouTakeGlass().
	 * Afterward, msgHereIsMyAnswer(state) will be the state at the time it answered.
	 * If it is the 1st glass, and they said true, it is fine.
	 * if it is the 2nd glass, and they said true, 
	 * 		- this will always happen for the 1st glass but not the glass after it.
	 * 			why? well after the 1st glass was accepted as TRUE upon first ENTRY, it will always question about
	 *          the first glass.
	 *          When that glas
	 * If they said false, answer again.
	 * 
	 * 
	 */

}
