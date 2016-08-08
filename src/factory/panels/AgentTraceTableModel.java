package factory.panels;

import javax.swing.table.DefaultTableModel;

public class AgentTraceTableModel extends DefaultTableModel
{ 
	int rows = 0;
	String[] columnNames = {"Agent", "Message", "Timestamp"};

	public AgentTraceTableModel()
	{
		setColumnIdentifiers(columnNames);
	}


	/**
	 * Adds a message to the table
	 * @param name name of kit
	 * @param amount amount wanted in kit
	 */
	public void newMessage(String agent, String message, String timestamp) 
	{ 
		addRow(new Object[]{agent, message, timestamp});	
	}
}
