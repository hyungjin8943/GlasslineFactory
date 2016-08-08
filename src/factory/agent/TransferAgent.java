package factory.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import factory.controller.TransferController;
import factory.interfaces.Conveyor;
import factory.interfaces.ConveyorInteractor;
import factory.interfaces.Disaster;
import factory.mock.log.EventLog;
import factory.mock.log.LoggedEvent;
import agent.Agent;
import agent.MessageTracePool;
/**
 * [TRUNK] TransferAgent
 * @version 2.0
 * @author Harman
 */
public class TransferAgent extends Agent implements ConveyorInteractor, Disaster {
	public EventLog log = new EventLog();
	public String name;	
	Conveyor source;
	Conveyor destination;
	TransferController  controller;
	
	public boolean needsFeeding = false; 
	
	
	enum GlassState { TRANSFER_FROM_CONVEYOR, NEED_TRANSFER, TRANSFERRING, NO_ACTION, GOING_TO_BREAK }
	
	class MyGlassOrder { 
		public GlassOrder order;
		public GlassState state;
		
		public MyGlassOrder(GlassOrder order) { 
			this.order = order;
			state = GlassState.TRANSFER_FROM_CONVEYOR;
		}
	}
	
	//public List<MyGlassOrder> orders = Collections.synchronizedList(new LinkedList<MyGlassOrder>());
	public MyGlassOrder order;
	public GlassOrder waiting;
	
	public TransferAgent(String name) { 
		this.name = name;
	}
	
	public String getName() { 
		return this.name;
	}
	
	public void setSource(Conveyor source) { 
		this.source = source;
	}
	
	public void setDestination(Conveyor destination) { 
		this.destination = destination;
	}

	public void setController(TransferController controller) { 
		this.controller = controller;
	}
	
	/**
	 * Conveyor will ask, can you take glass, because it has a glass on its exit sensor, and it needs to exit.
	 * @param order
	 */
	public void msgCanYouTakeGlass(GlassOrder order) {
		// TODO Auto-generated method stub
		
		//log("Received msgCanYouTakeGlass(" + order + ")");
		needsFeeding = true;
		waiting = order;
		stateChanged();

	}

	
	public void msgHereIsGlass(GlassOrder order) {
		// TODO Auto-generated method stub
		log("recieved msgHereIsGlass(" + order + ")");
		this.order = new MyGlassOrder(order);
		stateChanged();
	}
	
	public void msgReadyToPass() { 
		log("recieved msgReadyToPass()");
			if(order.state == GlassState.TRANSFER_FROM_CONVEYOR) {
				if(!isBroken) {
					order.state = GlassState.NEED_TRANSFER;
					
				}
				else { 
					order.state = GlassState.GOING_TO_BREAK;
				}
				log("msgReadyToPass():" + order.state);
				stateChanged();
			}
			else { 
				log("INCORRECT MESSAGE GIVEN @ WRONG TIME");
				System.exit(1);
			}
	}

	
	public void msgHereIsMyAnswer(boolean answer) {
				if(answer) { 
						// we change the state to transferring
						order.state = GlassState.TRANSFERRING;
				}
				else { 
					// we set the state to NEED_TRANSFER still
						order.state = GlassState.NEED_TRANSFER;
				}	
				log("msgHereIsMyAnswer("+answer+"):" + order.state);

					stateChanged();
					return;
	}
		
	
	
	public void msgDoneTransferGlass() {
		log("Received msgDoneTransferGlass()");
		animating.release();
		animate = false;
	}
	
	
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if(needsFeeding) { 
			answer();
		}
		if(order !=null){ 
			if(order.state == GlassState.NEED_TRANSFER) {
				decideTransferring(order);
				return true;
			}
			if(order.state == GlassState.TRANSFERRING && !isBroken) {
				transfer(order);
				return true;
			}
			if(order.state == GlassState.GOING_TO_BREAK && isBroken) { 
				breakGlass(order);
				return true;
			}
			
		}
		
		return false;
	}

	public void answer() {
		// if we dont have any orders, gladly accept the orders.
		if(order==null && !animate) {
			source.msgHereIsMyAnswer(waiting, true);
			needsFeeding = false;	
		}
		else {
			needsFeeding = true;
		}
		stateChanged();
	}
	Semaphore spamLock = new Semaphore(0);
	public void decideTransferring(MyGlassOrder go) {
		// ask whether or not the conveyor can take glass.
		Timer t = new Timer();
		t.schedule(new TimerTask() { 
			public void run() { 
				spamLock.release();
			}
		},200);
		try { 
			spamLock.acquire();
		}
		catch(InterruptedException e) { 
			e.printStackTrace();
		}
		destination.msgCanYouTakeGlass();
	//	go.state = GlassState.NO_ACTION;
		//log("decideTransferring(" + go + "=" + go.state + ")");
	}
	boolean animate;
	Semaphore animating = new Semaphore(0, true);
	public void transfer(MyGlassOrder go) {
		print("transfer(" + go + "): Begin Transferring animation");
		controller.playPass();
		animate = true;
	
		try {
			animating.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log("transfer(" + go + "): Animation is now completed");
		controller.turnTransferOff();
		destination.msgHereIsGlass(go.order);
		order = null;
		stateChanged();
	}
	
	/**
	 * Logs an event and adds it to the log of the Agent.
	 */
	public boolean debug;
	
	public void log(String description) {
		if(log != null) {
			MessageTracePool.add(description, this);
		}
		
	}
	
	/**
	 * NON-NORMATIVES
	 */
	boolean isBroken = false;
	public void msgBreakTransfer(boolean state) { 
		isBroken = state;
		stateChanged();
	}
	
	public void breakGlass(MyGlassOrder mg) {
		controller.playPass();
		try {
			animating.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller.turnTransferOff();
		order = null;
	}
	
	public void doneBreaking() { 
		animating.release();
	}
	
	public String getCurrentGlassOrderName() { 
		if(order !=null)
		return order.order.toString();
		else return "None";
	}
	
	public GlassState getCurrentGlassOrderState() { 
		if(order !=null)
		return order.state;
		else return GlassState.NO_ACTION;
	}

	@Override
	public void msgBreakAllGlasses() {
		// TODO Auto-generated method stub
		if(order !=null)
		order.order.setBroken(true);
		
	}



}
