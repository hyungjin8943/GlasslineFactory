package factory.controller;

import agent.MessageTracePool;
import factory.agent.OperatorAgent;
import factory.gui.GuiGrinder;
import factory.gui.GuiOperator;
import factory.gui.GuiStation;
import factory.interfaces.OperatorControllerInteractor;
//import factory.gui.GuiStation;
import factory.mock.log.EventLog;
import factory.mock.log.LoggedEvent;

public class OperatorController {
	OperatorAgent agent;
	OperatorControllerInteractor guiStation;
	GuiOperator guiOperator;
	
	public EventLog log = new EventLog();
	
	/*
	public void moveGlassToMachine() {
		log.add(new LoggedEvent("Moving Glass To Machine"));
		gui.setState("Moving Glass To Machine");
	}*/
	
	/*
	public void moveGlassToStation() {
		gui.setState("Moving Glass To Workstation");
	}*/
	
	public void returnGlass() {
		log.add(new LoggedEvent ("Returning Glass To Popup"));
		MessageTracePool.add("Returning Glass To Popup", agent);
		//guiStation.setState("Returning Glass To Popup");
		guiStation.setPlowAnimate();
	}
	
	public void putGlassOnTruck() {
		log.add(new LoggedEvent ("Putting Glass On Truck"));
		MessageTracePool.add("Putting Glass On Truck", agent);
		guiStation.giveGlassToOperator();
		guiOperator.setState("Putting Glass On Truck");
	}
	
	public void trashGlass() {
		log.add(new LoggedEvent ("Trashing Glass"));
		MessageTracePool.add("Trashing Glass", agent);
		guiStation.giveGlassToOperator();
		guiOperator.trashGlass();
	}
	
	public void animationDone() {
		log.add(new LoggedEvent ("Calling animationDone"));
		MessageTracePool.add("Calling animationDone", agent);
		agent.animationDone();
	}
	
	public void setOperatorAgent(OperatorAgent agent) {
		this.agent = agent;
	}
	

	public void setGuiStation(OperatorControllerInteractor gui) {
		this.guiStation = gui;
	}
	
	public void setGuiOperator(GuiOperator gui) {
		this.guiOperator = gui;
	}
}