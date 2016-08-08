package factory.mock;

import factory.gui.GuiPopup;
import factory.controller.PopupController;
import factory.mock.log.EventLog;
import factory.mock.log.LoggedEvent;

public class MockGuiPopup extends GuiPopup {
	public EventLog log = new EventLog();
	public String state;
	PopupController controller;
	
	public void setState(String state) {
		this.state = state;
		log.add(new LoggedEvent("Setting state to " + this.state));
		log.add(new LoggedEvent("Setting timer task to call animationDone"));
		controller.animationDone();
	}
	
	public void setController(PopupController controller) {
		this.controller = controller;
	}
}
