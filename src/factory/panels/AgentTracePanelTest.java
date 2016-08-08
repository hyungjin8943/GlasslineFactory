package factory.panels;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import factory.agent.GlassOrder;
import factory.agent.OperatorAgent;

import agent.MessageTracePool;


public class AgentTracePanelTest
{

/*

    public static void main(String[] args)
    {
	JFrame	testFrame	= new JFrame();
	testFrame.setSize(350, 500);
	testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	testFrame.setVisible(true);

	final int count = 1;

	final AgentTracePanel	tracePanel	= new AgentTracePanel();
	testFrame.add(tracePanel);
	Timer t = new Timer();
	t.schedule(new TimerTask(){

	    @Override
	    public void run()
	{
		JFrame	testFrame	= new JFrame();
		testFrame.setSize(350, 500);
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testFrame.setVisible(true);
				
		final AgentTracePanel	tracePanel	= new AgentTracePanel();
		testFrame.add(tracePanel);
		
		MessageTracePool.getInstance();
		MessageTracePool.setTracePanel(tracePanel);
		
		OperatorAgent	testAgent	= new OperatorAgent("OpAgent");
		testAgent.msgDoYouNeedGlass(new GlassOrder("TestGlassOrder"));
		
		
		
		/*
		Timer t = new Timer();
		t.schedule(new TimerTask(){

			@Override
			public void run()
			{
				tracePanel.addMessage("Conveyor", "I'm a conveyor", "06:34:69");
				
				
			}}, 500,50);
		*/
	/*	
	}
*/
}
