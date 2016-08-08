package factory.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import factory.gui.FactoryPart;

import javax.swing.Timer;
import javax.swing.table.TableModel;

public class DebugPanel extends JPanel implements ActionListener {

    // GraphicsPanel reference
    GraphicsPanel gPanel;
    Application app;

    JScrollPane scrollPane;

    // Column headers
    private final String[] columnNames = {
	"Agent", "GlassOrder", "GlassStatus"};
    private Object[][] status;
    int numParts;
    JTable statusTable;
    Timer timer;

    public DebugPanel() {
	this(null);
    }

    public DebugPanel(Application mainApp) {
	// Create timer, set up panels, and other components
	timer = new Timer(500, this);
	app = mainApp;
	gPanel = app.factoryPanel.gpanel;

	// Set layout
	this.setLayout(new BorderLayout());
	status = new Object[4][3];
	
	statusTable = new JTable(status, columnNames);
	statusTable.setFillsViewportHeight(true);

	scrollPane = new JScrollPane(statusTable);
	scrollPane.setVisible(true);

	this.add(scrollPane, BorderLayout.CENTER);
	timer.start();
	
    }

    public void setup() {
		//gp.transferPart2To3
		//gp.tmPart3
		//gp.transfer_1_agent
		//gp.manualBreakoutAgent
    	if(gPanel !=null) {
    	statusTable.setValueAt(gPanel.transfer_1_agent.getName(), 0,0);
    	statusTable.setValueAt(gPanel.transfer_1_agent.getCurrentGlassOrderName(), 0,1);
    	statusTable.setValueAt(gPanel.transfer_1_agent.getCurrentGlassOrderState(), 0,2);
    	
    	statusTable.setValueAt(gPanel.transferPart2To3.getName(), 1,0);
    	statusTable.setValueAt(gPanel.transferPart2To3.getCurrentGlassOrderName(), 1,1);
    	statusTable.setValueAt(gPanel.transferPart2To3.getCurrentGlassOrderState(), 1,2);
 
    	statusTable.setValueAt(gPanel.tmPart3.agent.getName(), 2,0);
    	statusTable.setValueAt(gPanel.tmPart3.agent.getCurrentGlassOrderName(), 2,1);
    	statusTable.setValueAt(gPanel.tmPart3.agent.getCurrentGlassOrderState(), 2,2);
    	
    	statusTable.setValueAt(gPanel.manualBreakoutAgent.getName(), 3,0);
    	statusTable.setValueAt(gPanel.manualBreakoutAgent.getCurrentGlassOrderName(), 3,1);
    	statusTable.setValueAt(gPanel.manualBreakoutAgent.getCurrentGlassOrderState(), 3,2);
    	}
    
    
	
    }

    private String extractClassName(String str) {
	int i = str.length() - 1;
	while (i-- >= 0) {
	    if (str.charAt(i) == '.')
		break;
	}
	return str.substring(i+1);
    }

    public void actionPerformed(ActionEvent ae) {
	if (ae.getSource() == timer) {
	    setup();
	}
    }
}
