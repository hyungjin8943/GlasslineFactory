package factory.controller;

import factory.agent.PopupAgent;
import factory.gui.GuiPopup;

public class PopupController {
	GuiPopup gui;
	PopupAgent agent;
	
	public PopupController(PopupAgent agent, GuiPopup gui) {
		this.agent = agent;
		this.gui = gui;
	}
	
	public void raisePopup() {
		gui.setState("Raising Popup");
	}
	
	public void lowerPopup() {
		gui.setState("Lowering Popup");
	}
	
	public void passGlassToStation1() {
		gui.setState("Passing to Station 1");
	}
	
	public void passGlassToStation2() {
		gui.setState("Passing to Station 2");
	}
	
	public void passGlassToConveyor() {
		gui.setState("Passing to Conveyor");
	}
	
	public void shootGlass() {
		gui.setState("Shooting Glass Up");
	}
	
	public void setBreak(boolean b) {
		agent.isBroken = b;
	}
	
	public void animationDone() {
		agent.animationDone();
	}

}
