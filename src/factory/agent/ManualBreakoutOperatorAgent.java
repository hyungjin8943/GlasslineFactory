package factory.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import agent.Agent;
import agent.MessageTracePool;
import factory.agent.GlassOrder.GlassTreatmentStatus;
import factory.agent.OperatorAgent.MyGlassOrderStatus;
import factory.agent.OperatorAgent.MyStatus;
import factory.agent.TransferAgent.GlassState;
import factory.controller.ManualBreakoutController;
import factory.controller.OperatorController;
import factory.interfaces.Disaster;
import factory.interfaces.Operator;
import factory.interfaces.Popup;
import factory.interfaces.Truck;
import factory.mock.log.EventLog;

public class ManualBreakoutOperatorAgent extends Agent implements Operator, Disaster{
	/**********************************************************************
	 * 
	 *Data
	 *
	 *********************************************************************/
	/** List of operators further down the production line */
	List<OperatorAgent> operatorsDownLine = Collections.synchronizedList(new ArrayList<OperatorAgent>());
	
	/** Process that the operator can perform */
	String job;
	
	/** Status of the operator */
	MyStatus status;
	public enum MyStatus {FREE, BUSY};
	
	/** Glass that the operator is working on */
	MyGlassOrder order;
	
	/** Multistep semaphore lock */
	//Semaphore actionLock = new Semaphore(0);
	
	/** Spam preventing semaphore */
	Semaphore spamLock = new Semaphore(0);
	
	/** Reference to popup */
	Popup popup;
	
	/** Animation Lock */
	Semaphore animationLock = new Semaphore(0);
	
	/** Reference to controller */
	ManualBreakoutController controller;
	
	/** Reference to the machine */
	//StandAloneMachineAgent machine;
	
	/** Reference to truck */
	//Truck truck;
	
	/** Log for debug */
	public EventLog log = new EventLog();
	
	private class MyGlassOrder {
		GlassOrder glassOrder;
		MyGlassOrderStatus status;
		
		public MyGlassOrder(GlassOrder order) {
			glassOrder = order;
			status = MyGlassOrderStatus.RECEIVED_FROM_POPUP;
		}
	}
	public enum MyGlassOrderStatus {RECEIVED_FROM_POPUP, NEED_BREAKOUT, DONE_PROCESSING, READY_TO_RETURN_TO_POPUP, READY_TO_TRASH, NO_ACTION};
	
	String name;
	
	public ManualBreakoutOperatorAgent(String name) {
		super();
		this.name = name;
		status = MyStatus.FREE;
		job = "ManualBreakout";
	}
	
	/**********************************************************************
	 * 
	 *Messages
	 *
	 *********************************************************************/
	
	/* (non-Javadoc)
	 * @see factory.agent.Operator#msgDoYouNeedGlass(factory.agent.GlassOrder)
	 */
	public void msgDoYouNeedGlass(GlassOrder glassOrder) {
		//print("Received msgDoYouNeedGlass for glassOrder " + glassOrder.getName());
		addMsgToTracePanel("Received msgDoYouNeedGlass for glassOrder " + glassOrder);
		//log.add(new LoggedEvent("Received msgDoYouNeedGlass for glassOrder " + glassOrder.getName()));
		//System.out.println("GLASSORDER NAME:" + glassOrder.getName());
		//System.out.println("OPERATOR JOB: " + job);
		//System.out.println("STATUS " + glassOrder.getGlassTreatmentStatus(job));
		
		if(glassOrder.getGlassTreatmentStatus(job) == GlassTreatmentStatus.INCOMPLETE) {
			popup.msgHereIsMyAnswer(true, this, glassOrder);
		}
		else {
			popup.msgHereIsMyAnswer(false, this, glassOrder);
		}
		stateChanged();
	}

	/* (non-Javadoc)
	 * @see factory.agent.Operator#msgAreYouFree()
	 */
	public void msgAreYouFree() {
		//print("Received msgAreYouFree");
		addMsgToTracePanel("Received msgAreYouFree");
		//log.add(new LoggedEvent("Received msgAreYouFree"));
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
		if(status == MyStatus.FREE) {
			popup.msgIamFree(this);
		}
		else {
			popup.msgIamBusy(this);
		}
		stateChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see factory.interfaces.Operator#msgWhatIsYourJob()
	 */
	public void msgWhatIsYourJob() {
		//print("Received msgWhatIsYourJob");
		addMsgToTracePanel("Received msgWhatIsYourJob");
		//log.add(new LoggedEvent("Received msgWhatIsYourJob"));
		popup.msgThisIsMyJob(this, job);
	}

	/*
	 * (non-Javadoc)
	 * @see factory.interfaces.Operator#msgIsJobAvailableLater()
	 */
	public void msgIsJobAvailableLater() {
		//print("Received msgIsJobAvailableLater");
		addMsgToTracePanel("Received msgIsJobAvailableLater");
		//log.add(new LoggedEvent("Received msgIsJobAvailableLater"));
		boolean answer = false;
		for(OperatorAgent operator:operatorsDownLine) {
			if(operator.job.equals(this.job)) {
				answer = true;
				break;
			}
		}
		popup.msgHereIsJobAvailability(answer, this);
		stateChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see factory.interfaces.Operator#msgHereIsGlass(factory.agent.GlassOrder)
	 */
	public void msgHereIsGlass(GlassOrder glassOrder) {
		//print("Received msgHereIsGlass for glassOrder " + glassOrder.toString());
		addMsgToTracePanel("Received msgHereIsGlass for glassOrder " + glassOrder.toString());
		//log.add(new LoggedEvent("Received msgHereIsGlass for glassOrder " + glassOrder.getName()));
		status = MyStatus.BUSY;
		order = new MyGlassOrder(glassOrder);
		stateChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see factory.interfaces.Operator#msgYesYouCan()
	 */
	public void msgYesYouCan() {
		//print("Received msgYesYouCan");
		addMsgToTracePanel("Received msgYesYouCan");
		//log.add(new LoggedEvent("Received msgYesYouCan"));
		order.status = MyGlassOrderStatus.READY_TO_RETURN_TO_POPUP;
		stateChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see factory.interfaces.Operator#msgNoYouCannot()
	 */
	public void msgNoYouCannot() {
		//print("Received msgNoYouCannot");
		addMsgToTracePanel("Received msgNoYouCannot");
		//log.add(new LoggedEvent("Received msgNoYouCannot"));
		order.status = MyGlassOrderStatus.DONE_PROCESSING;
		stateChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see factory.interfaces.Operator#msgHereIsTreatedGlass(factory.agent.GlassOrder)
	 */
	public void msgHereIsTreatedGlass(GlassOrder glassOrder) {
		//print("Received msgHereIsTreatedGlass for glassOrder " + glassOrder.getName());
		addMsgToTracePanel("Received msgHereIsTreatedGlass for glassOrder " + glassOrder.getName());
		//log.add(new LoggedEvent("Received msgHereIsTreatedGlass for glassOrder " + glassOrder.getName()));
		if(glassOrder == order.glassOrder) {
			order.glassOrder = glassOrder;
			order.status = MyGlassOrderStatus.DONE_PROCESSING;
		}
		else {
			print("ERROR! RECEIVED GLASS FROM MACHINE THAT WAS NOT THE GLASS IT EXPECTED!");
			System.exit(1);
		}
		stateChanged();
	}

	/*
	 * (non-Javadoc)
	 * @see factory.interfaces.Operator#msgOkToPassGlass()
	 */
	public void msgOkToPassGlass() {
		// TODO Auto-generated method stub
		//NOT USED!!!
	}
	
	public void msgBreakAllGlasses() {
		if(order != null) {
			order.glassOrder.setBroken(true);
		}
	}

	/**********************************************************************
	 * 
	 *Scheduler
	 *
	 *********************************************************************/
	
	@Override
	protected boolean pickAndExecuteAnAction() {
		if(order == null) {
			return false;
		}
		
		else {
			if(order.status == MyGlassOrderStatus.RECEIVED_FROM_POPUP) {
				decideNextAction();
				return true;
			}
			
			if(order.status == MyGlassOrderStatus.NEED_BREAKOUT) {
				breakoutGlass();
				return true;
			}
			
			if(order.status == MyGlassOrderStatus.DONE_PROCESSING) {
				decideWhereToSendGlass();
				return true;
			}
			
			if(order.status == MyGlassOrderStatus.READY_TO_RETURN_TO_POPUP) {
				returnToPopup();
				return true;
			}
			
			if(order.status == MyGlassOrderStatus.READY_TO_TRASH) {
				trashGlass();
				return true;
			}
		}
		
		return false;
	}
	
	/**********************************************************************
	 * 
	 *Actions
	 *
	 *********************************************************************/
	
	/**
	 * This action will decide what next action the manual break out will do.
	 * If the glass is broken it will dump it.
	 * If not, then it will break it out.
	 */
	private void decideNextAction() {
		//print("Performing action decideNextAction");
		addMsgToTracePanel("Performing action decideNextAction");
		//print("Log size before add: " + log.size());
		//log.add(new LoggedEvent("Performing action giveToMachine"));
		//print("Log size after add: " + log.size());
		order.status = MyGlassOrderStatus.NO_ACTION;
		if(order.glassOrder.isBroken()) {
			order.status = MyGlassOrderStatus.READY_TO_TRASH;
		}
		else {
			order.status = MyGlassOrderStatus.NEED_BREAKOUT;
		}
		stateChanged();
	}
	
	/**
	 * This action will figure out where to pass the glass next. If the GlassOrder is completed it will
	 * pass it to the truck. If not, then it will pass it back to the popup.
	 */
	private void decideWhereToSendGlass() {
		//log.add(new LoggedEvent("Performing action decideWhereToSendGlass"));
		order.status = MyGlassOrderStatus.NO_ACTION;
		/*
		controller.moveGlassToStation();
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		//If all the process are complete or not needed on the glass order then ok to pass to truck
		//System.out.println("Glass Order Complete: " + order.glassOrder.isFinished());
		//XXX This is where it differs!
		if(order.glassOrder.isBroken()) {
			order.status = MyGlassOrderStatus.READY_TO_TRASH;
			addMsgToTracePanel("Set my order to READY_TO_TRASH");
		}
		//Else pass to popup
		else {
			popup.msgCanIPassTreatedGlass(order.glassOrder, this);
			addMsgToTracePanel("Asked popup if can pass treated glass. order is NO_ACTION");
		}
		stateChanged();
	}
	
	/**
	 * This action will make the gui breakout the glass
	 */
	private void breakoutGlass() {
		//log.add(new LoggedEvent("Performing action breakoutGlass"));
		addMsgToTracePanel("Performing action breakoutGlass");
		//print("Performing action breakoutGlass");
		order.status = MyGlassOrderStatus.NO_ACTION;
		controller.breakoutGlass(order.glassOrder);
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		order.glassOrder.setGlassTreatmentStatus("ManualBreakout", GlassTreatmentStatus.COMPLETE);
		order.status = MyGlassOrderStatus.DONE_PROCESSING;
		stateChanged();
	}
	
	/**
	 * This action will return the glass to the popup
	 */
	private void returnToPopup() {
		//log.add(new LoggedEvent("Performing action returnToPopup"));
		//print("Performing action returnToPopup");
		order.status = MyGlassOrderStatus.NO_ACTION;
		controller.returnGlass();
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		popup.msgHereIsTreatedGlass(order.glassOrder);
		order = null;
		status = MyStatus.FREE;
		stateChanged();
	}
	
	/**
	 * This action will trash the glass
	 */
	private void trashGlass() {
		//log.add(new LoggedEvent("Performing action returnToPopup"));
		//print("Performing action returnToPopup");
		order.status = MyGlassOrderStatus.NO_ACTION;
		controller.trashGlass(order.glassOrder);
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		order = null;
		status = MyStatus.FREE;
		stateChanged();
	}
	
	/**********************************************************************
	 * 
	 *Extra
	 *
	 *********************************************************************/
	public void addOperatorDownLine(OperatorAgent operator) {
		operatorsDownLine.add(operator);
	}
	
	//public void setJob(String job) {
	//	this.job = job;
	//}
	
	public void setPopup(Popup popup) {
		this.popup = popup;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getJob() {
		return this.job;
	}
	
	public void setOperatorController(ManualBreakoutController controller) {
		this.controller = controller;
	}
	
	public void animationDone() {
		addMsgToTracePanel("Calling Animation Done from gui");
		animationLock.release();
	}
	
	//public void setMachine(StandAloneMachineAgent machine) {
	//	this.machine = machine;
	//}
	
	//public void setTruck(Truck truck) {
	//	this.truck = truck;
	//}
	
	private void addMsgToTracePanel(String message) {
		MessageTracePool.add(message, this);
	}
	
	/**
	 * HARMAN ADDED THIS FOR DEBUG
	 */
	public String getCurrentGlassOrderName() { 
		if(order !=null)
		return order.glassOrder.toString();
		else return "None";
	}
	
	public MyGlassOrderStatus getCurrentGlassOrderState() { 
		if(order !=null)
		return order.status;
		else return MyGlassOrderStatus.NO_ACTION;
	}

}
