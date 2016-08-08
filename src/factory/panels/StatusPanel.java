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

public class StatusPanel extends JPanel implements ActionListener {

    // GraphicsPanel reference
    GraphicsPanel gPanel;
    Application app;

    ArrayList<FactoryPart> partList; 

    JScrollPane scrollPane;

    // Column headers
    private final String[] columnNames = {
	"ID", "Type", "Glass", "Status"};
    private Object[][] status;
    int numParts;
    JTable statusTable;
    Timer timer;

    public StatusPanel() {
	this(null);
    }

    public StatusPanel(Application mainApp) {
	// Create timer, set up panels, and other components
	timer = new Timer(500, this);
	app = mainApp;
	gPanel = app.factoryPanel.gpanel;
	partList = gPanel.getFactoryParts();
	numParts = partList.size();
	status = new String[numParts][columnNames.length];

	// Set layout
	this.setLayout(new BorderLayout());

	for (int i = 0; i < numParts; i++) {
	    // Fill data
	    status[i][0] = "" + partList.get(i).getID();
	    status[i][1] = extractClassName((partList.get(i).getClass().toString()));
	    status[i][3] = partList.get(i).getStatus();

	    // Add color depending on hasGlass()
	    if (partList.get(i).hasGlass())
		status[i][2] = "Have Glass";
	    else
		status[i][2] = "No Glass";

	}
	statusTable = new JTable(status, columnNames);
	statusTable.setFillsViewportHeight(true);
	statusTable.setAutoCreateRowSorter(true);

	scrollPane = new JScrollPane(statusTable);
	scrollPane.setVisible(true);

	this.add(scrollPane, BorderLayout.CENTER);
	timer.start();
	
    }

    public void setup() {
    	for (int i = 0; i < numParts; i++) {
	    // Find correct row based using ID
	    int id, row;
	    row = id = partList.get(i).getID();
	    for (int j = 0; j < numParts; j++) {
		if (Integer.parseInt((String)statusTable.getValueAt(j, 0)) == (Integer)id) {
		    row = j;
		    break;
		}
	    }

    	    // Fill data
	    //statusTable.setValueAt(partList.get(i).getID(), i, 0);
	    //statusTable.setValueAt(extractClassName(partList.get(i).getClass().toString()), i, 1);
	    //statusTable.setValueAt(partList.get(i).getStatus(), i, 3);
	    statusTable.setValueAt("" + partList.get(i).getID(), row, 0);
	    statusTable.setValueAt(extractClassName(partList.get(i).getClass().toString()), row, 1);
	    statusTable.setValueAt(partList.get(i).getStatus(), row, 3);

    	    // Add color depending on hasGlass()
    	    if (partList.get(i).hasGlass())
    	    	statusTable.setValueAt("Have Glass", row, 2);
    	    else
    	    	statusTable.setValueAt("No Glass", row, 2);
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


