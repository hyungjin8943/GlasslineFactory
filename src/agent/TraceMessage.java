package agent;

import java.text.DateFormat;
import java.util.Date;

public class TraceMessage {
	/** The message of the logged event */
	private String message;
	/** The time of occurance */
	private Date timestamp;
	/** Agent to left the message */
	private Agent agent;
	
	/**
	 * Constructor for a LoggedEvent
	 * @param message The message of the event
	 */
	public TraceMessage(String message, Agent agent) {
		this.timestamp = new Date();
		this.message = message;
		this.agent = agent;
	}
	
	public Date getTimeStamp() {
		return this.timestamp;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public Agent getAgent() {
		return this.agent;
	}
	
	public String getAgentName() {
		return this.agent.getName();
	}
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.agent.getName() + " ");
		buffer.append("(@ " + DateFormat.getTimeInstance(DateFormat.FULL).format(this.timestamp) + "): ");
		buffer.append(this.message);
		return buffer.toString();
	}
}
