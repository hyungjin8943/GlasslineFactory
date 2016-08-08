package agent;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import factory.panels.AgentTracePanel;

/**
 * This class follows the singleton coding pattern. It is a log of messages from the agents.
 * @author Rhys Yahata
 */
public class MessageTracePool {
	/** Static copy of itself for the singleton coding pattern */
	private static MessageTracePool instance;
	/** Log of all the messages */
	private static List<TraceMessage> log = Collections.synchronizedList(new ArrayList<TraceMessage>());
	/** Reference to a trace panel */
	private static AgentTracePanel panel;
	
	private MessageTracePool() {}
	
	public static MessageTracePool getInstance() {
		if(instance == null) {
			instance = new MessageTracePool();
		}
		return instance;
	}
	
	public static void add(String message, Agent agent) {
		TraceMessage newMessage = new TraceMessage(message, agent);
		//log.add(new TraceMessage(message, agent));
		if(panel != null)
		panel.addMessage(newMessage.getAgent().getName(), newMessage.getMessage(), DateFormat.getTimeInstance(DateFormat.FULL).format(newMessage.getTimeStamp()));
		else { 
			System.out.println(agent.getName() + ": "+ message);
		}
	}
	
	public static void setTracePanel(AgentTracePanel p) {
		panel = p;
	}
	
	//TODO Need to add a way for the MessageTracePool to update the list of traces in the panel
}
