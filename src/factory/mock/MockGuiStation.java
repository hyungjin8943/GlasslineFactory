package factory.mock;

import java.util.Timer;
import java.util.TimerTask;

import factory.controller.OperatorController;
import factory.gui.GuiStation;
import factory.mock.log.EventLog;
import factory.mock.log.LoggedEvent;

public class MockGuiStation extends GuiStation {
	public EventLog log = new EventLog();
	public String state;
	public OperatorController controller;
	public Timer t = new Timer();
	
	public void setState(String state) {
		this.state = state;
		log.add(new LoggedEvent("Setting state to " + this.state));
		log.add(new LoggedEvent("Setting timer task to call animationDone"));
		//t.schedule(new GuiTimerTask(controller), 2000);
	}
	
	public void setController(OperatorController controller) {
		this.controller = controller;
	}
	
	public class GuiTimerTask extends TimerTask {
		OperatorController opController;
		public GuiTimerTask(OperatorController oc) {
			opController = oc;
		}
		public void run() {
			opController.animationDone();
		}
		
	}
}
