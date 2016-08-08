package factory.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;

import agent.Agent;

public class AgentTracePanel extends JPanel //implements ActionListener
{
    //MessageTracePool.getInstance();

    //Timer		t			= new Timer(20, this);

    JPanel		display;
    JLabel		title;
    JTable		table;
    AgentTraceTableModel tableModel;
    JScrollPane	scrollPane;

    public AgentTracePanel()
    {
	setLayout(new BorderLayout());
	title			= new JLabel("Factory Agent Communication");

	tableModel = new AgentTraceTableModel();
	table = new JTable(tableModel);
	table.setFillsViewportHeight(true);
	table.setAutoCreateRowSorter(true);
	/*
	table.setAutoResizeMode(table.AUTO_RESIZE_OFF);
	table.getColumnModel().getColumn(0).setPreferredWidth(50);
	table.getColumnModel().getColumn(1).setPreferredWidth(120);
	table.getColumnModel().getColumn(2).setPreferredWidth(80);
	*/
	scrollPane = new JScrollPane(table);
	
	//scrollPane.setMinimumSize(new Dimension(1000, 150));
	//scrollPane.setPreferredSize(new Dimension(1000, 150));
	scrollPane.setMaximumSize(new Dimension(1000, 150));
	

	this.add(title, BorderLayout.NORTH);		
	this.add(scrollPane, BorderLayout.CENTER);
	this.invalidate();
	this.validate();

    }

    /**
     * Adds the message
     */
    public void addMessage(String agent, String message, String timestamp) 
    { 

	String[] info = new String[3];

	info[0] = new String(" ");
	info[1] = new String(" ");
	info[2] = new String(" ");


	info[0] = agent;
	info[1] = message;
	info[2] = timestamp;

	int dest = tableModel.getRowCount();
	//ttModel.getRowCount();

	try 
	{
	    tableModel.insertRow(dest, info);
	    this.invalidate();
	    this.validate();
	    table.scrollRectToVisible(table.getCellRect(table.getRowCount()-1, 0, true));

	}
	catch(Exception e) 
	{ 
	    return;
	}
    }

}
