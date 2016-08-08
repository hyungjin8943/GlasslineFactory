package factory.agent;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import factory.agent.GlassOrder.GlassTreatmentStatus;
import factory.controller.OperatorController;
import factory.interfaces.Disaster;
import factory.interfaces.ITruckInteractor;
import factory.interfaces.Operator;
import factory.interfaces.Popup;
import factory.interfaces.Truck;
import factory.mock.log.EventLog;
import factory.mock.log.LoggedEvent;

import agent.Agent;
import agent.MessageTracePool;

public class OperatorAgent extends Agent implements Operator, ITruckInteractor, Disaster{
	//Data
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
	
	/** Reference to popup */
	Popup popup;
	
	/** Animation Lock */
	Semaphore animationLock = new Semaphore(0);
	Semaphore spamLock = new Semaphore(0);
	
	/** Reference to controller */
	OperatorController controller;
	
	/** Reference to the machine */
	StandAloneMachineAgent machine;
	
	/** Reference to truck */
	Truck truck;
	
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
	public enum MyGlassOrderStatus {RECEIVED_FROM_POPUP, GIVEN_TO_MACHINE, DONE_PROCESSING, NEED_JOB, READY_TO_RETURN_TO_POPUP, READY_TO_TRASH, READY_TO_GIVE_TO_TRUCK, NO_ACTION};
	
	String name;
	
	public OperatorAgent(String name) {
		super();
		this.name = name;
		status = MyStatus.FREE;
	}
	
	//Messages
	/* (non-Javadoc)
	 * @see factory.agent.Operator#msgDoYouNeedGlass(factory.agent.GlassOrder)
	 */
	public void msgDoYouNeedGlass(GlassOrder glassOrder) {
		//print("Received msgDoYouNeedGlass for glassOrder " + glassOrder.getName());
		addMsgToTracePanel("Received msgDoYouNeedGlass for glassOrder " + glassOrder.getName());
		//log.add(new LoggedEvent("Received msgDoYouNeedGlass for glassOrder " + glassOrder.getName()));
	//	System.out.println("GLASSORDER NAME:" + glassOrder.getName());
	//	System.out.println("OPERATOR JOB: " + job);
	//	System.out.println("STATUS " + glassOrder.getGlassTreatmentStatus(job));
		
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
		addMsgToTracePanel("Received msgAreYouFree");
		//log.add(new LoggedEvent("Received msgAreYouFree"));
		if(status == MyStatus.FREE) {
			popup.msgIamFree(this);
		}
		else {
			popup.msgIamBusy(this);
		}
		stateChanged();
	}
	
	public void msgWhatIsYourJob() {
		//print("Received msgWhatIsYourJob");
		addMsgToTracePanel("Received msgWhatIsYourJob");
		//log.add(new LoggedEvent("Received msgWhatIsYourJob"));
		popup.msgThisIsMyJob(this, job);
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Operator#msgIsJobAvailableLater()
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
	
	/* (non-Javadoc)
	 * @see factory.agent.Operator#msgHereIsGlass(factory.agent.GlassOrder)
	 */
	public void msgHereIsGlass(GlassOrder glassOrder) {
		//print("Received msgHereIsGlass for glassOrder " + glassOrder.toString());
		addMsgToTracePanel("Received msgHereIsGlass for glassOrder " + glassOrder.toString());
		//log.add(new LoggedEvent("Received msgHereIsGlass for glassOrder " + glassOrder.getName()));
		status = MyStatus.BUSY;
		order = new MyGlassOrder(glassOrder);
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Operator#msgYesYouCan()
	 */
	public void msgYesYouCan() {
		//print("Received msgYesYouCan");
		addMsgToTracePanel("Received msgYesYouCan");
		//log.add(new LoggedEvent("Received msgYesYouCan"));
		order.status = MyGlassOrderStatus.READY_TO_RETURN_TO_POPUP;
		stateChanged();
	}
	
	/* (non-Javadoc)
	 * @see factory.agent.Operator#msgNoYouCannot()
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
	
	
	//XXX This is old
	/*
	 * (non-Javadoc)
	 * @see factory.interfaces.Operator#msgOkToPassGlass()
	 */
	public void msgOkToPassGlass() {
		print("Received msgOkToPassGlass");
		//log.add(new LoggedEvent("Received msgOkToPassGlass"));
		order.status = MyGlassOrderStatus.READY_TO_GIVE_TO_TRUCK;
		stateChanged();
	}
	
	/*
	 * (non-Javadoc)
	 * @see factory.interfaces.ITruckInteractor#msgOkToPassGlass(factory.interfaces.Truck)
	 */
	public void msgOkToPassGlass(Truck caller) {
		//print("Received msgOkToPassGlass");
		//log.add(new LoggedEvent("Received msgOkToPassGlass"));
		addMsgToTracePanel("Received msgOkToPassGlass from " + caller);
		if(order.glassOrder.isBroken()) {
			order.status = MyGlassOrderStatus.READY_TO_TRASH;
		}
		else {
			order.status = MyGlassOrderStatus.READY_TO_GIVE_TO_TRUCK;
		}
		stateChanged();
	}
	
	public void msgBreakAllGlasses() {
		if(order != null) {
			order.glassOrder.setBroken(true);
		}
	}
	
	//Scheduler
	public boolean pickAndExecuteAnAction() {
		if(order == null) {
			return false;
		}
		
		else {
			if(order.status == MyGlassOrderStatus.RECEIVED_FROM_POPUP) {
				decideNextAction(); //XXX modification
				return true;
			}
			
			//XXX modification
			if(order.status == MyGlassOrderStatus.NEED_JOB) {
				giveToMachine();
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
			
			if(order.status == MyGlassOrderStatus.READY_TO_GIVE_TO_TRUCK) {
				giveToTruck();
				return true;
			}
			
			if(order.status == MyGlassOrderStatus.READY_TO_TRASH) {
				trashGlass();
				return true;
			}
		}
		
		return false;
	}
	
	//Actions
	/**
	 * This action will give the machine the glass order so that it can process it.
	 * v0 note - This action does sets the status of the glass order to done
	 */
	private void giveToMachine() {
		//print("Performing action giveToMachine");
		addMsgToTracePanel("Performing action giveToMachine");
		//print("Log size before add: " + log.size());
		//log.add(new LoggedEvent("Performing action giveToMachine"));
		//print("Log size after add: " + log.size());
		order.status = MyGlassOrderStatus.NO_ACTION;
		machine.msgHereIsGlass(order.glassOrder);
		/*
		controller.moveGlassToMachine();
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		//This is a hack for v0
		//order.glassOrder.setGlassTreatmentStatus(job, GlassTreatmentStatus.COMPLETE);
		//order.status = MyGlassOrderStatus.DONE_PROCESSING;
		stateChanged();
	}
	
	/**
	 * This action will decide what action to perform on the glass.
	 * If the glass is broken it will dump it.
	 * If not, it will give it to the machine
	 */
	private void decideNextAction() {
		log.add(new LoggedEvent("Performing action decideNextAction"));
		addMsgToTracePanel("Performing action decideNextAction");
		order.status = MyGlassOrderStatus.NO_ACTION;
		if(order.glassOrder.isBroken()) {
			order.status = MyGlassOrderStatus.READY_TO_TRASH;
		}
		else {
			order.status = MyGlassOrderStatus.NEED_JOB;
		}
		stateChanged();
	}
	
	/**
	 * This action will make the gui trash the glass.
	 */
	private void trashGlass() {
		log.add(new LoggedEvent("Performing action trashGlass"));
		addMsgToTracePanel("Performing action trashGlass");
		order.status = MyGlassOrderStatus.NO_ACTION;
		controller.trashGlass();
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		order = null;
		status = MyStatus.FREE;
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
		if(order.glassOrder.isBroken()) {
			order.status = MyGlassOrderStatus.READY_TO_TRASH;
			addMsgToTracePanel("Setting or to READY_TO_TRASH");
		}
		else if(order.glassOrder.isFinished()) {
			truck.msgCanIPassGlass(this); //XXX Made changes here for Ed's interface
			addMsgToTracePanel("Asking truck if I can pass glass");
		}
		//Else pass to popup
		else {
			popup.msgCanIPassTreatedGlass(order.glassOrder, this);
			addMsgToTracePanel("Asking popup if I can pass glass");
		}
		stateChanged();
	}
	
	/**
	 * This action will return the glass to the popup
	 */
	private void returnToPopup() {
		//log.add(new LoggedEvent("Performing action returnToPopup"));
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
	 * This action will give the glass to the truck
	 */
	private void giveToTruck() {
		//log.add(new LoggedEvent("Performing action giveToTruck"));
		order.status = MyGlassOrderStatus.NO_ACTION;
		controller.putGlassOnTruck();
		try {
			animationLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		truck.msgHereIsGlass(order.glassOrder);
		order = null;
		status = MyStatus.FREE;
		stateChanged();
	}
	
	//Extra
	public void addOperatorDownLine(OperatorAgent operator) {
		operatorsDownLine.add(operator);
	}
	
	public void setJob(String job) {
		this.job = job;
	}
	
	public void setPopup(Popup popup) {
		this.popup = popup;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getJob() {
		return this.job;
	}
	
	public void setOperatorController(OperatorController controller) {
		this.controller = controller;
	}
	
	public void animationDone() {
		animationLock.release();
	}
	
	public void setMachine(StandAloneMachineAgent machine) {
		this.machine = machine;
	}
	
	public void setTruck(Truck truck) {
		this.truck = truck;
	}
	
	//Testing function
	/**
	 * This is test version of returnToPopup. It is an exact copy of the function, except certain parts are commented out
	 * that do not make sense for testing purposes.
	 */
	public void returnToPopupTest() {
		order.status = MyGlassOrderStatus.NO_ACTION;
		//popup.msgHereIsTreatedGlass(order.glassOrder);
		order = null;
		status = MyStatus.FREE;
		//stateChanged();
	}
	
	public GlassOrder getOrder() {
		return this.order.glassOrder;
	}
	
	public MyStatus getStatus() {
		return this.status;
	}
	
	public Popup getPopup() {
		return this.popup;
	}
	
	public MyGlassOrderStatus getMyGlassOrderStatus() {
		return this.order.status;
	}
	
	private void addMsgToTracePanel(String message) {
		MessageTracePool.add(message, this);
	}
}
