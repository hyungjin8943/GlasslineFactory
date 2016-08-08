package factory.mock.log;

import java.text.DateFormat;
import java.util.Date;

public class LoggedEvent {
	/** The message of the logged event */
	private String message;
	/** The time of occurance */
	private Date timestamp;
	
	/**
	 * Constructor for a LoggedEvent
	 * @param message The message of the event
	 */
	public LoggedEvent(String message) {
		this.timestamp = new Date();
		this.message = message;
	}
	
	public Date getTimeStamp() {
		return this.timestamp;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public String toString() {
		return DateFormat.getTimeInstance(DateFormat.FULL).format(this.timestamp) + ": " + this.message;
	}
}
