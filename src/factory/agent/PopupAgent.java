package factory.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import factory.controller.PopupController;
import factory.interfaces.*;
import factory.mock.log.EventLog;
//import factory.mock.log.LoggedEvent;

import agent.Agent;
import agent.MessageTracePool;

public class PopupAgent extends Agent implements Popup, ConveyorInteractor, Disaster {
	//Testing
	public EventLog log;
	
	//Data
	private class MyGlassOrder {
		GlassOrder glassOrder;
		//OrderState state;
		/** Boolean value for whether or not the glass order needs process 1 done to it */
		boolean needsJob1;
		/** Boolean value for whether or not the glass order needs process 2 done to it */
		boolean needsJob2;
		/** Location of the glass order */
		MyGlassOrderStatus status;
	
		
		//enum OrderState {};
	}
	enum MyGlassOrderStatus {ON_POPUP, ON_THE_WAY, READY_FOR_TRANSFER_FROM_CONVEYOR, READY_FOR_TRANSFER_FROM_WORKSTATION1,
		READY_FOR_TRANSFER_FROM_WORKSTATION2, NO_ACTION};
	
	/**
	 * Boolean value for whether or not there is another machine further down the glass line that can accomplish the same
	 * process as job1
	 */
	Boolean job1AvailableLater;
	/**
	 * Boolean value for whether or not there is another machine further down the glass line that can accomplish the same
	 * process as job2
	 */
	Boolean job2AvailableLater;
	
	/** String representation of the agent's name */
	public String name;
	
	/** String used to represent operator1's job */
	String operator1Job;
	
	/** String used to represent operator2's job */
	String operator2Job;
	
	/** List of glass orders */
	public List<MyGlassOrder> glassOrders = Collections.synchronizedList(new ArrayList<MyGlassOrder>());
	
	/** Reference to Operator 1 */
	Operator operator1;
	/** Reference to Operator 2 */
	Operator operator2;
	/** Status of operator1 */
	OperatorStatus operator1Status;
	/** Status of operator2 */
	OperatorStatus operator2Status;
	
	enum OperatorStatus {FREE, BUSY};
	
	/** Reference to PopupController */
	PopupController controller;
	
	/** Reference to conveyor before popup */
	Conveyor fromConveyor;
	/** Reference to conveyor after popup */
	Conveyor toConveyor;
	
	/** Popup status for if it is raised or lowered */
	public PopupStatus popupStatus;
	public enum PopupStatus {LOWERED, RAISED}
	
	/** Animation semaphore */
	public Semaphore animationLock = new Semaphore(0);
	
	/** Multistep action semaphore */
	public Semaphore actionLock = new Semaphore(0);
	
	/** Spam blocking semaphore */
	public Semaphore spamLock = new Semaphore(0);
	
	/** Used to determine if the popup is broken */
	public boolean isBroken;
	
	/** Constructor */
	public PopupAgent(String name) {
		super();
		this.name = name;
		popupStatus = PopupStatus.LOWERED;
		this.log = new EventLog();
	}
	
	//Messages
	/* (non-Javadoc)
	 * @see factory.agent.Popup#msgGlassOnTheWay(factory.agent.GlassOrder)
	 */
	public void msgGlassOnTheWay(GlassOrder glassOrder) {
		//print("Recieved msgGlassOnTheWay");
		addToTracePanel("Received msgGlassOnTheWay");
		MyGlassOrder order = new MyGlassOrder();
		order.glassOrder = glassOrder;
		order.status = MyGlassOrderStatus.ON_THE_WAY;
		glassOrders.add(order);
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Popup#msgHereIsMyAnswer(boolean, factory.interfaces.Operator, factory.agent.GlassOrder)
	 */
	public void msgHereIsMyAnswer(boolean answer, Operator operator, GlassOrder glassOrder) {
		//print("Received msgHereIsMyAnswer with answer of " + answer + " from Operator " + operator.toString() + " for glassOrder " + glassOrder.toString());
		
		addToTracePanel(glassOrder.getName());
		addToTracePanel("Received msgHereIsMyAnswer with answer of " + answer + " from Operator " + operator.toString() + " for glassOrder " + glassOrder.toString());
		synchronized(glassOrders) {
			for(MyGlassOrder myGlassOrder:glassOrders) {
				if(myGlassOrder.glassOrder == glassOrder) {
					if(operator == operator1) {
						myGlassOrder.needsJob1 = answer;
					}
					else { //if(operator == operator2) {
						myGlassOrder.needsJob2 = answer;
					}
					stateChanged();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Popup#msgCanYouTakeGlass(factory.agent.GlassOrder)
	 */
	public void msgCanYouTakeGlass(GlassOrder glassOrder) {
		//print("Received msgCanYouTakeGlass from conveyor for glassOrder " + glassOrder.toString());
		//addToTracePanel("Received msgCanYouTakeGlass from conveyor for glassOrder " + glassOrder.toString());
		//Timer t = new Timer();
		//t.schedule(new TimerTask(){
			//public void run() {
				//actionLock.release();
			//}
		//}, 100);
		//try {
			//actionLock.acquire();
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
		synchronized(glassOrders) {
			for(MyGlassOrder myGlassOrder:glassOrders) {
				if(myGlassOrder.glassOrder == glassOrder) {
					myGlassOrder.status = MyGlassOrderStatus.READY_FOR_TRANSFER_FROM_CONVEYOR;
					stateChanged();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Popup#msgIamFree(factory.interfaces.Operator)
	 */
	public void msgIamFree(Operator operator) {
		//print("Received msgIamFree from operator " + operator.toString());
		//addToTracePanel("Received msgIamFree from operator " + operator.toString()); //XXX Commented this out to speed things up
		if(operator == operator1) {
			operator1Status = OperatorStatus.FREE;
		}
		else {
			operator2Status = OperatorStatus.FREE;
		}
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Popup#msgIamBusy(factory.interfaces.Operator)
	 */
	public void msgIamBusy(Operator operator) {
		//print("Received msgIamBusy from operator " + operator.toString());
		//addToTracePanel("Received msgIamBusy from operator " + operator.toString()); //XXX Commented this out to speed things up
		if(operator == operator1) {
			operator1Status = OperatorStatus.BUSY;
		}
		else {
			operator2Status = OperatorStatus.BUSY;
		}
	}
	
	public void msgThisIsMyJob(Operator operator, String job) {
		//print("Received msgThisIsMyJob from operator " + operator.toString() + " with job " + job);
		addToTracePanel("Received msgThisIsMyJob from operator " + operator.toString() + " with job " + job);
		if(operator == operator1) {
			operator1Job = job;
		}
		else {
			operator2Job = job;
		}
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Popup#msgHereIsGlass(factory.agent.GlassOrder)
	 */
	public void msgHereIsGlass(GlassOrder glassOrder) {
		//print("Received msgHereIsGlass with glassOrder " + glassOrder.toString());
		addToTracePanel("Received msgHereIsGlass with glassOrder " + glassOrder.toString());
		synchronized(glassOrders) {
			for(MyGlassOrder myGlassOrder:glassOrders) {
				if(myGlassOrder.glassOrder == glassOrder) {
					myGlassOrder.status = MyGlassOrderStatus.ON_POPUP;
				}
			}
		}
		actionLock.release();
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Popup#msgCanIPassTreatedGlass(factory.agent.GlassOrder, factory.interfaces.Operator)
	 */
	public void msgCanIPassTreatedGlass(GlassOrder glassOrder, Operator operator) {
		//print("Received msgCanIPassTreatedGlass from operator " + operator.toString() + " for glassOrder " + glassOrder.toString());
		//Timer t = new Timer();
		//t.schedule(new TimerTask(){
			//public void run() {
				//actionLock.release();
			//}
		//}, 100);
		//try {
			//actionLock.acquire();
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
		addToTracePanel("Received msgCanIPassTreatedGlass from operator " + operator.toString() + " for glassOrder " + glassOrder.toString());
		synchronized(glassOrders) {
			for(MyGlassOrder myGlassOrder:glassOrders) {
				if(myGlassOrder.glassOrder == glassOrder) {
					if(operator == operator1) {
						myGlassOrder.status = MyGlassOrderStatus.READY_FOR_TRANSFER_FROM_WORKSTATION1;
					}
					else {
						myGlassOrder.status = MyGlassOrderStatus.READY_FOR_TRANSFER_FROM_WORKSTATION2;	
					}
				}
			}
		}
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Popup#msgHereIsTreatedGlass(factory.agent.GlassOrder)
	 */
	public void msgHereIsTreatedGlass(GlassOrder glassOrder) {
		//print("Received msgHereIsTreatedGlass for glassOrder " + glassOrder.toString());
		addToTracePanel("Received msgHereIsTreatedGlass for glassOrder " + glassOrder.toString());
		synchronized(glassOrders) {
			for(MyGlassOrder myGlassOrder:glassOrders) {
				if(myGlassOrder.glassOrder == glassOrder) {
					myGlassOrder.status = MyGlassOrderStatus.ON_POPUP;
				}
			}
		}
		actionLock.release();
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Popup#msgHereIsMyAnswer(boolean)
	 */
	public void msgHereIsMyAnswer(boolean answer) {
		//print("Received msgHereIsMyAnswer with answer " + answer);
		addToTracePanel("Received msgHereIsMyAnswer with answer " + answer);
		//If the toConveyor is able to receive glass, unlock the multistep action semaphore
		if(answer) {
			actionLock.release();
		}
		//Otherwise, ask the toConveyor again if it can recieve glass
		else {
			toConveyor.msgCanYouTakeGlass();
		}
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Popup#msgHereIsJobAvailability(boolean, factory.interfaces.Operator)
	 */
	public void msgHereIsJobAvailability(boolean answer, Operator operator) {
		//print("Received msgHereIsJobAvailability from operator " + operator.toString() + " with answer " + answer);
		addToTracePanel("Received msgHereIsJobAvailability from operator " + operator.toString() + " with answer " + answer);
		if(operator == operator1) {
			job1AvailableLater = new Boolean(answer);
		}
		else {
			job2AvailableLater = new Boolean(answer);
		}
	}
	
	/**
	 * This message will tell the popup to set all of the glass pieces that it is holding to broken
	 */
	public void msgBreakAllGlasses() {
		synchronized(glassOrders) {
			for(MyGlassOrder glassOrder:glassOrders) {
				if(glassOrder.status == MyGlassOrderStatus.ON_POPUP) {
					glassOrder.glassOrder.setBroken(true);
				}
			}
		}
		
	}
	
	
	//Scheduler
	public boolean pickAndExecuteAnAction() {
		boolean flag = false;
		MyGlassOrder orderWithAction = null;
		
		// HARMAN DID THIS
		// THIS FIXES SKIPPING POPUP PROBLEM
		
		synchronized(glassOrders) {
			for(MyGlassOrder glassOrder:glassOrders) {
				if(glassOrder.status == MyGlassOrderStatus.ON_THE_WAY) {
					//LoggedEvent event = new LoggedEvent("Scheduler was called with glass order on the middle of the conveyor");
					//log.add(event);
					flag = true;
					orderWithAction = glassOrder;
					break;
				}
			}
		}
		//Action call pulled out of synchronized block
		if(flag == true) {
			checkWithOperators(orderWithAction);
			//TODO up for discussion
			/**
			 * This might be a problem if many glasses are incoming from the conveyor.
			 * As of this moment, this works fine.
			 * The only possible reason why I would want to comment this out and 
			 * set flag = false and orderWithAction = null is because when a glass is incoming, it does
			 * checkWithOperators(MyGlassOrder).
			 * - checkWithOperators invokes stateChanged() afterward, which then calls the scheduler to
			 * be active.
			 */
			flag = false;
			orderWithAction = null;
			return true;
		}
		synchronized(glassOrders) {
			for(MyGlassOrder glassOrder:glassOrders) {
				if(glassOrder.status == MyGlassOrderStatus.ON_POPUP && isBroken && popupStatus == PopupStatus.LOWERED) {
					//LoggedEvent event = new LoggedEvent("Scheduler was called with glass order on the popup");
					//log.add(event);
					flag = true;
					orderWithAction = glassOrder;
					break;
				}
			}
		}
		//Action call pulled out of synchronized block
		if(flag == true) {
			shootGlassUp(orderWithAction);
			return true;
		}
		synchronized(glassOrders) {
			for(MyGlassOrder glassOrder:glassOrders) {
				if(glassOrder.status == MyGlassOrderStatus.ON_POPUP && !(isBroken && popupStatus == PopupStatus.LOWERED)) {
					//LoggedEvent event = new LoggedEvent("Scheduler was called with glass order on the popup");
					//log.add(event);
					flag = true;
					orderWithAction = glassOrder;
					break;
				}
			}
		}
		//Action call pulled out of synchronized block
		if(flag == true) {
			decideOnWhereToPass(orderWithAction);
			return true;
		}
		synchronized(glassOrders) {
			for(MyGlassOrder glassOrder:glassOrders) {
				if(glassOrder.status == MyGlassOrderStatus.READY_FOR_TRANSFER_FROM_WORKSTATION1
						|| glassOrder.status == MyGlassOrderStatus.READY_FOR_TRANSFER_FROM_WORKSTATION2) {
					//LoggedEvent event = new LoggedEvent("Scheduler was called with glass order in a workstation");
					//log.add(event);
					flag = true;
					orderWithAction = glassOrder;
					break;
				}
			}
		}
		//Action call pulled out of synchronized block
		if(flag == true) {
			decideOnReceivingGlassFromOperator(orderWithAction, orderWithAction.status);
			return true;
		}
		synchronized(glassOrders) {
			for(MyGlassOrder glassOrder:glassOrders) {
				if(glassOrder.status == MyGlassOrderStatus.READY_FOR_TRANSFER_FROM_CONVEYOR) {
					//LoggedEvent event = new LoggedEvent("Scheduler was called with glass order ready for transfer from the conveyor");
					//log.add(event);
					flag = true;
					orderWithAction = glassOrder;
					break;
				}
			}
		}
		//Action call pulled out of synchronized block
		if(flag == true) {
			decideOnReceivingGlassFromConveyor(orderWithAction);
			return true;
		}

		return false;
	}
	
	//Actions
	/**
	 * This action will check whether or not either of the operators need to process the inputed glass
	 * @param glassOrder - The glass order that you want check
	 */
	private void checkWithOperators(MyGlassOrder glassOrder) {
		//LoggedEvent event = new LoggedEvent("Action checkWithOperators was called");
		//log.add(event);
		glassOrder.status = MyGlassOrderStatus.NO_ACTION;
		operator1.msgDoYouNeedGlass(glassOrder.glassOrder);
		operator2.msgDoYouNeedGlass(glassOrder.glassOrder);
		stateChanged();
	}
	
	/**
	 * This action will check if a glass order needs processing by an operator, and whether or not that operator is free.
	 * Basically determines whether or not a popup is able to receive a piece of glass.
	 * preconditions - Assumes the popup has nothing on it 
	 * @param glassOrder - Glass order in question
	 */
	private void decideOnReceivingGlassFromConveyor(MyGlassOrder glassOrder) {
		//LoggedEvent event = new LoggedEvent("Action decideOnReceivingGlassFromConveyor was called");
		//log.add(event);
		Timer t = new Timer();
		t.schedule(new TimerTask(){
			public void run() {
				spamLock.release();
			}
		}, 100);
		try {
			spamLock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		glassOrder.status = MyGlassOrderStatus.NO_ACTION;
		if(job1AvailableLater == null) {
			operator1.msgIsJobAvailableLater();
			operator1.msgWhatIsYourJob();
			operator2.msgIsJobAvailableLater();
			operator2.msgWhatIsYourJob();
		}
		//If the popup is raised then don't accept new glass
		//if(popupStatus == PopupStatus.RAISED) {
			//fromConveyor.msgHereIsMyAnswer(glassOrder.glassOrder, false);
		//}
		//If the piece coming in needs a job that is busy and not available down the line, don't accept new glass
		//If the glass needs job 1 check if operator 1 is free
		if(glassOrder.needsJob1) {
			operator1.msgAreYouFree();
			//If operator 1 is busy, check if his job can be performed later
			if(operator1Status == OperatorStatus.BUSY) {
				
				//If operator 1's job cannot be performed later, then need to stop conveyor and wait for the operator 1
				//to be free
				if(!job1AvailableLater) {
					fromConveyor.msgHereIsMyAnswer(glassOrder.glassOrder, false);
				}
				//If operator 1's job can be performed later, then check whether or not glass needs job 2
				else if(glassOrder.needsJob2) {
					operator2.msgAreYouFree();
					//If operator 2 is busy , check if his job can be performed later
					if(operator2Status == OperatorStatus.BUSY) {
						//If operator 2's job cannot be performed later, then need to stop conveyor and wait for the operator 2
						//to be free
						if(!job2AvailableLater) {
							fromConveyor.msgHereIsMyAnswer(glassOrder.glassOrder, false);
						}
						//If operator 1's job is available later and operator 1 is busy and
						//If operator 2's job is available later and operator 2 is busy then accept glass
						else{
							if(popupStatus == PopupStatus.RAISED) {
								DoLowerPopup();
							}
							fromConveyor.msgHereIsMyAnswer(glassOrder.glassOrder, true);
							//Wait for response from conveyor
							try {
								actionLock.acquire();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					//If operator 2 is free then popup can accept the glass
					else if(operator2Status == OperatorStatus.FREE) {
						if(popupStatus == PopupStatus.RAISED) {
							DoLowerPopup();
						}
						fromConveyor.msgHereIsMyAnswer(glassOrder.glassOrder, true);
						//Wait for response from conveyor
						try {
							actionLock.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			//If operator 1 is free then popup can accept the glass
			else if(operator1Status == OperatorStatus.FREE){
				if(popupStatus == PopupStatus.RAISED) {
					DoLowerPopup();
				}
				fromConveyor.msgHereIsMyAnswer(glassOrder.glassOrder, true);
				//Wait for response from conveyor
				try {
					actionLock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		//If glass does not need operator 1's job then check whether or not glass needs job 2
		else if(glassOrder.needsJob2) {
			operator2.msgAreYouFree();
			//If operator 2 is busy, check if his job can be performed later
			if(operator2Status == OperatorStatus.BUSY) {
				//If operator 2's job cannot be performed later, then need to stop conveyor and wait for the operator 2
				//to be free
				if(!job2AvailableLater) {
					fromConveyor.msgHereIsMyAnswer(glassOrder.glassOrder, false);
				}
				//If operator 2's job is available later and operator 2 is busy then accept glass
				else{
					if(popupStatus == PopupStatus.RAISED) {
						DoLowerPopup();
					}
					fromConveyor.msgHereIsMyAnswer(glassOrder.glassOrder, true);
					//Wait for response from conveyor
					try {
						actionLock.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			//If operator 2 is free then popup can accept the glass
			else if(operator2Status == OperatorStatus.FREE) {
				if(popupStatus == PopupStatus.RAISED) {
					DoLowerPopup();
				}
				fromConveyor.msgHereIsMyAnswer(glassOrder.glassOrder, true);
				//Wait for response from conveyor
				try {
					actionLock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		//Needs neither jobs and popup is lowered so it can take it
		else {
			if(popupStatus == PopupStatus.RAISED) {
				DoLowerPopup();
			}
			fromConveyor.msgHereIsMyAnswer(glassOrder.glassOrder, true);
			//Wait for response from conveyor
			try {
				actionLock.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		stateChanged();
	}
	
	/**
	 * This action will decide whether or not a piece of glass needs to have further processing done on it or if it will be
	 * passed on to the next conveyor.
	 * precondition - Glass is on the popup
	 * @param glassOrder - Glass order in question
	 */
	private void decideOnWhereToPass(MyGlassOrder glassOrder) {
		//LoggedEvent event = new LoggedEvent("Action decideOnWhereToPass was called");
		//log.add(event);
		//System.out.println("decideOnWhereToPass");
		glassOrder.status = MyGlassOrderStatus.NO_ACTION;
		//If the glass needs job 1
		if(glassOrder.needsJob1) {
			//System.out.println("decideOnWhereToPass1");
			//Check if operator 1 is busy or not
			operator1.msgAreYouFree();
			if(operator1Status == OperatorStatus.FREE) {
				if(popupStatus == PopupStatus.LOWERED) {
					DoRaisePopup();
				}
				DoPassToStation1();
				glassOrder.needsJob1 = false;
				if(operator1Job.equals(operator2Job)) {
					glassOrder.needsJob2 = false;
				}
				operator1.msgHereIsGlass(glassOrder.glassOrder);
			}
			//If the glass needs job 2
			else if(glassOrder.needsJob2) {
				//System.out.println("decideOnWhereToPass2");
				//Check if operator 2 is busy or not
				operator2.msgAreYouFree();
				if(operator2Status == OperatorStatus.FREE) {
					if(popupStatus == PopupStatus.LOWERED) {
						DoRaisePopup();
					}
					DoPassToStation2();
					glassOrder.needsJob2 = false;
					if(operator1Job.equals(operator2Job)) {
						glassOrder.needsJob1 = false;
					}
					operator2.msgHereIsGlass(glassOrder.glassOrder);
				}
				//If the glass order does not need job 1 and 2, or the jobs it needs are available down the line then 
				//it can be passed to the next conveyor
				else {
					//Lower popup
					if(popupStatus == PopupStatus.RAISED) {
						DoLowerPopup();
					}
					//Check if the conveyor can receive glass
					toConveyor.msgCanYouTakeGlass();
					//Wait for response
					try {
						actionLock.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//Hand the glass to the conveyor
					DoPassToConveyor();
					toConveyor.msgHereIsGlass(glassOrder.glassOrder);
				}
			}
			//If the glass order does not need job 1 and 2, or the jobs it needs are available down the line then 
			//it can be passed to the next conveyor
			else {
				//Lower popup
				if(popupStatus == PopupStatus.RAISED) {
					DoLowerPopup();
				}
				//Check if the conveyor can receive glass
				toConveyor.msgCanYouTakeGlass();
				//Wait for response
				try {
					actionLock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//Hand the glass to the conveyor
				DoPassToConveyor();
				toConveyor.msgHereIsGlass(glassOrder.glassOrder);
			}
		}
		//If the glass needs job 2
		else if(glassOrder.needsJob2) {
			//Check if operator 2 is busy or not
			operator2.msgAreYouFree();
			if(operator2Status == OperatorStatus.FREE) {
				if(popupStatus == PopupStatus.LOWERED) {
					DoRaisePopup();
				}
				DoPassToStation2();
				glassOrder.needsJob2 = false;
				if(operator1Job.equals(operator2Job)) {
					glassOrder.needsJob1 = false;
				}
				operator2.msgHereIsGlass(glassOrder.glassOrder);
			}
			//If the glass order does not need job 1 and 2, or the jobs it needs are available down the line then 
			//it can be passed to the next conveyor
			else {
				//Lower popup
				if(popupStatus == PopupStatus.RAISED) {
					DoLowerPopup();
				}
				//Check if the conveyor can receive glass
				toConveyor.msgCanYouTakeGlass();
				//Wait for response
				try {
					actionLock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//Hand the glass to the conveyor
				DoPassToConveyor();
				toConveyor.msgHereIsGlass(glassOrder.glassOrder);
			}
		}
		//If the glass order does not need job 1 and 2, or the jobs it needs are available down the line then 
		//it can be passed to the next conveyor
		else {
			//Lower popup
			if(popupStatus == PopupStatus.RAISED) {
				DoLowerPopup();
			}
			//Check if the conveyor can receive glass
			toConveyor.msgCanYouTakeGlass();
			//Wait for response
			try {
				actionLock.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Hand the glass to the conveyor
			DoPassToConveyor();
			toConveyor.msgHereIsGlass(glassOrder.glassOrder);
		}
		stateChanged();
	}
	
	/**
	 * This action will decided on whether or not the popup is able to receive a piece of glass from an operator
	 * @param glassOrder - Glass order in question
	 * @param status - Status of the glass order before the action. Used to figure out which operator is trying to give glass
	 */
	private void decideOnReceivingGlassFromOperator(MyGlassOrder glassOrder, MyGlassOrderStatus status) {
		//LoggedEvent event = new LoggedEvent("Action decideOnReceivingGlassFromOperator was called");
		//log.add(event);
		Timer t = new Timer();
		t.schedule(new TimerTask(){
			public void run() {
				spamLock.release();
			}
		}, 100);
		try {
			spamLock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		glassOrder.status = MyGlassOrderStatus.NO_ACTION;
		//If the glass needs job 1
		if(glassOrder.needsJob1) {
			//Check if operator 1 is busy or not
			operator1.msgAreYouFree();
			if(operator1Status == OperatorStatus.FREE) {
				if(popupStatus == PopupStatus.LOWERED) {
					DoRaisePopup();
				}
				//Message the operator to let him know that its ok to pass glass
				operator2.msgYesYouCan();
				//Wait for response from operator2
				try {
					actionLock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//If operator1 is not free then check if his process is available down the line
			else {
				//If operator1's job is available later then take glass and pass to conveyor
				if(job1AvailableLater) {
					if(popupStatus == PopupStatus.LOWERED) {
						DoRaisePopup();
					}
					//Message the operator to let him know that its ok to pass glass
					operator2.msgYesYouCan();
					//Wait for response from operator2
					try {
						actionLock.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else {
					operator2.msgNoYouCannot();
				}
			}
		}
		//If glass needs job 2
		else if(glassOrder.needsJob2) {
			//Check if operator 2 is busy or not
			operator2.msgAreYouFree();
			if(operator2Status == OperatorStatus.FREE) {
				if(popupStatus == PopupStatus.LOWERED) {
					DoRaisePopup();
				}
				//Message the operator to let him know that its ok to pass glass
				operator1.msgYesYouCan();
				//Wait for response from operator1
				try {
					actionLock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//If operator2 is not free then check if his process is available down the line
			else {
				//If operator2's job is available later then take glass and pass to conveyor
				if(job2AvailableLater) {
					if(popupStatus == PopupStatus.LOWERED) {
						DoRaisePopup();
					}
					//Message the operator to let him know that its ok to pass glass
					operator1.msgYesYouCan();
					//Wait for response from operator1
					try {
						actionLock.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else {
					operator1.msgNoYouCannot();
				}
			}
		}
		//Else if glass needs neither jobs
		else {
			if(status == MyGlassOrderStatus.READY_FOR_TRANSFER_FROM_WORKSTATION1) {
				//XXX 9:40 4/21 added this to fix not raising bug
				if(popupStatus == PopupStatus.LOWERED) {
					DoRaisePopup();
				}
				//Message the operator to let him know that its ok to pass glass
				operator1.msgYesYouCan();
				//Wait for response from operator1
				try {
					actionLock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else {
				//XXX 9:40 4/21 added this to fix not raising bug
				if(popupStatus == PopupStatus.LOWERED) {
					DoRaisePopup();
				}
				//Message the operator to let him know that its ok to pass glass
				operator2.msgYesYouCan();
				//Wait for response from operator2
				try {
					actionLock.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		stateChanged();
	}
	
	/**
	 * This action will cause the broken animation to occur, shooting the glass up
	 * @param order - the glass to be shot up
	 */
	private void shootGlassUp(MyGlassOrder order) {
		DoBrokenAnimation();
		glassOrders.remove(order);
	}
	
	//Animation calls
	private void DoRaisePopup() {
		//LoggedEvent event = new LoggedEvent("Animation DoRaisePopup was called");
		//log.add(event);
		//print("DoRaisePopup");
		addToTracePanel("Raise Popup");
		//Tell the controller to raise the popup
		controller.raisePopup();
		//Set status to raised
		popupStatus = PopupStatus.RAISED;
		//Wait for animation to complete
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("Done Raising Popup");
	}
	
	private void DoLowerPopup() {
		//LoggedEvent event = new LoggedEvent("Animation DoLowerPopup was called");
		//log.add(event);
		//print("DoLowerPopup");
		addToTracePanel("Lower Popup");
		//Tell the controller to lower the popup
		controller.lowerPopup();
		//Set status to lowered
		popupStatus = PopupStatus.LOWERED;
		//Wait for animation to complete
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void DoPassToStation1() {
		//LoggedEvent event = new LoggedEvent("Animation DoPassToStation1 was called");
		//log.add(event);
		//print("DoPassToStation1");
		addToTracePanel("Pass To Station 1");
		//Tell the controller to pass glass to station1
		controller.passGlassToStation1();
		//Wait for animation to complete
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("Done passing to station1");
	}
	
	private void DoPassToStation2() {
		//LoggedEvent event = new LoggedEvent("Animation DoPassToStation2 was called");
		//log.add(event);
		addToTracePanel("Pass To Station 2");
		//Tell the controller to pass glass to station2
		controller.passGlassToStation2();
		//Wait for animation to complete
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void DoPassToConveyor() {
		//LoggedEvent event = new LoggedEvent("Animation DoPassToConveyor was called");
		//log.add(event);
		addToTracePanel("Pass To Conveyor");
		//Tell the controller to pass glass to the conveyor
		controller.passGlassToConveyor();
		//Wait for animation to complete
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void DoBrokenAnimation() {
		addToTracePanel("Shoot Glass");
		//Tell the controller to shoot the glass up
		controller.shootGlass();
		//Wait for animation to complete
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//Since the animation stays raised when it shoots the glass
		popupStatus = PopupStatus.RAISED;
	}
	
	/**
	 * Release the animation semaphore
	 */
	public void animationDone() {
		animationLock.release();
	}
	
	//Extra
	public void setOperator1(Operator operator) {
		this.operator1 = operator;
	}
	
	public void setOperator2(Operator operator) {
		this.operator2 = operator;
	}
	
	public void setToConveyor(Conveyor conveyor) {
		this.toConveyor = conveyor;
	}
	
	public void setFromConveyor(Conveyor conveyor) {
		this.fromConveyor = conveyor;
	}
	
	public void setController(PopupController controller) {
		this.controller = controller;
	}
	
	public String getName(){
		return name;
	}
	
	public void addToTracePanel(String message) {
		MessageTracePool.add(message, this);
	}
	
	//Testing methods
	public boolean getNeedsJob1() {
		return glassOrders.get(0).needsJob1;
	}
	
	public boolean getNeedsJob2() {
		return glassOrders.get(0).needsJob2;
	}
	
	public boolean getJob1AvailableLater() {
		return job1AvailableLater;
	}
	
	public boolean getJob2AvailableLater() {
		return job2AvailableLater;
	}
	
	public boolean getIfOperator1Busy() {
		if(operator1Status == OperatorStatus.BUSY) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean getIfOperator2Busy() {
		if(operator2Status == OperatorStatus.BUSY) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
