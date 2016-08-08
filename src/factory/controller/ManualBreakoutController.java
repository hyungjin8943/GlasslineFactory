package factory.controller;

import agent.MessageTracePool;
import factory.agent.GlassOrder;
import factory.agent.ManualBreakoutOperatorAgent;
import factory.agent.OperatorAgent;
import factory.gui.GuiGrinder;
import factory.gui.GuiManualBreakout;
import factory.gui.GuiOperator;
import factory.interfaces.OperatorControllerInteractor;
import factory.mock.log.EventLog;
import factory.mock.log.LoggedEvent;

public class ManualBreakoutController {
	ManualBreakoutOperatorAgent agent;
	GuiManualBreakout gui;
	
	public EventLog log = new EventLog();
	
	public void returnGlass() {
		log.add(new LoggedEvent ("Returning Glass To Popup"));
		MessageTracePool.add("Returning Glass To Popup", agent);
		gui.playPass();
		//System.out.println("Manual Breakout: calling returnGlass");
	}
	
	public void breakoutGlass(GlassOrder order) {
		log.add(new LoggedEvent("Manually Breaking Out Glass"));
		MessageTracePool.add("Manually Breaking Out Glass", agent);
		gui.playJob("Breakout Glass", order.getGlassDesign());
		//System.out.println("Manual Breakout: calling breakoutGlass");
	}
	
	public void trashGlass(GlassOrder order) {
		log.add(new LoggedEvent("Throwing Glass Away"));
		MessageTracePool.add("Throwing Glass Away", agent);
		gui.playJob("Trash Glass", order.getGlassDesign());
		//System.out.println("Manual Breakout: calling trashGlass");
	}
	
	public void doneJob() {
		log.add(new LoggedEvent ("Calling doneJob"));
		MessageTracePool.add("Calling doneJob", agent);
		System.out.println("Manual Breakout: calling doneJob");
		agent.animationDone();
	}
	
	public void donePass() {
		log.add(new LoggedEvent ("Calling donePass"));
		MessageTracePool.add("Calling donePass", agent);
		//System.out.println("Manual Breakout: calling donePass");
		agent.animationDone();
	}
	
	//public void animationDone() {
	//	log.add(new LoggedEvent ("Calling animationDone"));
	//	MessageTracePool.add("Calling animationDone", agent);
	//	agent.animationDone();
	//}
	
	public void setOperatorAgent(ManualBreakoutOperatorAgent agent) {
		this.agent = agent;
	}
	
	public void setGui(GuiManualBreakout gui) {
		this.gui = gui;
	}
}
