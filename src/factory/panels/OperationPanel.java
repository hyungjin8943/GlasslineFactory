package factory.panels;

import java.awt.BorderLayout;
import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OperationPanel extends JPanel implements ActionListener {

    Application app;
    GraphicsPanel gpanel;

    JPanel containerPanel;

    /***** BUTTONS *****/
    // robot arms
    JPanel robots;
    JButton startRobot, endRobot; 

    // inline machines
    JPanel inlines;
    JButton cutter, breakout, washer, lamp, oven; 

    // transfers
    JPanel transfers;
    JButton tf1, tf2, tf3; 

    // popups
    JPanel popups;
    JButton pu1, pu2, pu3;

    // stations
    JPanel stations;
    JButton seamer, drill, grinder, grinder2, paint; 

    public OperationPanel(Application mainApp) {
	app = mainApp;
	gpanel = app.factoryPanel.gpanel;
	init();
    }

    private void init() {

	// Robot arms
	startRobot = new JButton("Break Start Robot");
	startRobot.addActionListener(this);
	endRobot= new JButton("Break End Robot");
	endRobot.addActionListener(this);
	robots = new JPanel(); 
	robots.setLayout(new BoxLayout(robots, BoxLayout.PAGE_AXIS));
	robots.add(startRobot); 
	robots.add(endRobot);

	// Inline machines
	cutter = new JButton("Break Cutter");
	cutter.addActionListener(this);
	breakout = new JButton("Break Breakout");
	breakout.addActionListener(this);
	washer = new JButton("Break Washer");
	washer.addActionListener(this);
	lamp = new JButton("Break Lamp");
	lamp.addActionListener(this);
	oven = new JButton("Break Oven");
	oven.addActionListener(this);
	inlines = new JPanel();
	inlines.setLayout(new BoxLayout(inlines, BoxLayout.PAGE_AXIS));
	//inlines.add(cutter); 
	inlines.add(breakout); 
	//inlines.add(washer); 
	//inlines.add(lamp); 
	//inlines.add(oven); 
	
	// Transfers
	tf1 = new JButton("Break Transfer1");
	tf1.addActionListener(this);
	tf2 = new JButton("Break Transfer2");
	tf2.addActionListener(this);
	tf3 = new JButton("Break Transfer3");
	tf3.addActionListener(this);
	transfers = new JPanel();
	transfers.setLayout(new BoxLayout(transfers, BoxLayout.PAGE_AXIS));
	transfers.add(tf1);
	transfers.add(tf2);
	transfers.add(tf3);

	// Popups
	pu1 = new JButton("Break Popup1");
	pu1.addActionListener(this);
	pu2 = new JButton("Break Popup2");
	pu2.addActionListener(this);
	pu3 = new JButton("Break Popup3");
	pu3.addActionListener(this);
	popups = new JPanel();
	popups.setLayout(new BoxLayout(popups, BoxLayout.PAGE_AXIS));
	popups.add(pu1);
	popups.add(pu2);
	popups.add(pu3);

	// Stations
	seamer = new JButton("Break Cross Seamer");
	seamer.addActionListener(this);
	drill = new JButton("Break Drill");
	drill.addActionListener(this);
	grinder = new JButton("Break Grinder1");
	grinder.addActionListener(this);
	grinder2 = new JButton("Break Grinder2");
	grinder2.addActionListener(this);
	paint = new JButton("Break Paint");
	paint.addActionListener(this);
	stations = new JPanel();
	stations.setLayout(new BoxLayout(stations, BoxLayout.PAGE_AXIS));
	stations.add(seamer);
	stations.add(drill);
	stations.add(grinder);
	stations.add(grinder2);
	stations.add(paint);

	// Add borders
	robots.setBorder(BorderFactory.createCompoundBorder(
		    BorderFactory.createTitledBorder("Robots"),
		    BorderFactory.createEmptyBorder(5, 5, 5, 5)) );
	inlines.setBorder(BorderFactory.createCompoundBorder(
		    BorderFactory.createTitledBorder("Inlines Machines"),
		    BorderFactory.createEmptyBorder(5, 5, 5, 5)) );
	transfers.setBorder(BorderFactory.createCompoundBorder(
		    BorderFactory.createTitledBorder("Transfers"),
		    BorderFactory.createEmptyBorder(5, 5, 5, 5)) );
	popups.setBorder(BorderFactory.createCompoundBorder(
		    BorderFactory.createTitledBorder("Popups"),
		    BorderFactory.createEmptyBorder(5, 5, 5, 5)) );
	stations.setBorder(BorderFactory.createCompoundBorder(
		    BorderFactory.createTitledBorder("Work Stations"),
		    BorderFactory.createEmptyBorder(5, 5, 5, 5)) );

	// Align panels
	robots.setAlignmentY(Component.TOP_ALIGNMENT);
	inlines.setAlignmentY(Component.TOP_ALIGNMENT);
	transfers.setAlignmentY(Component.TOP_ALIGNMENT);
	popups.setAlignmentY(Component.TOP_ALIGNMENT);
	stations.setAlignmentY(Component.TOP_ALIGNMENT);

	// Create a container panel
	containerPanel = new JPanel();
	containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.LINE_AXIS));
	
	// Add panels
	containerPanel.add(robots);
	containerPanel.add(inlines);
	containerPanel.add(transfers);
	containerPanel.add(popups);
	containerPanel.add(stations);
	this.add(containerPanel, BorderLayout.CENTER);
	this.add(new JLabel("<html>Clicking on a conveyor stops it from moving. "
		+ "Right-clicking on a conveyor while it is moving breaks the entry sensor.</html>"), 
		BorderLayout.SOUTH);

	//this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	//this.add(robots);
	//this.add(inlines);
	//this.add(transfers);
	//this.add(popups);
	//this.add(stations);

	this.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {

	Object s = ae.getSource();

	// Robot arms
	if (s == startRobot) {
	    if (gpanel.getStartRobot().isBroken()) {
		gpanel.getStartRobot().setBreak(false);
		startRobot.setText("Break Start Robot");
	    }
	    else {
		gpanel.getStartRobot().setBreak(true);
		startRobot.setText("Fix Start Robot");
	    }
	}
	else if (s == endRobot) {
	    if (gpanel.getEndRobot().isBroken()) {
		gpanel.getEndRobot().setBreak(false);
		endRobot.setText("Break End Robot");
	    }
	    else {
		gpanel.getEndRobot().setBreak(true);
		endRobot.setText("Fix End Robot");
	    }
	}

	// Transfers
	else if (s == tf1) {
	    if (gpanel.getTransfer(1).isBroken()) {
	    	if(gpanel.transfer_1_agent.order == null) {
				gpanel.getTransfer(1).setBreak(false);
				tf1.setText("Break Transfer1");
	    	}
	    }
	    else {
	    	if(gpanel.transfer_1_agent.order == null) {
				gpanel.getTransfer(1).setBreak(true);
				tf1.setText("Fix Transfer1");
	    	}
	    }
	}
	else if (s == tf2) {
	    if (gpanel.getTransfer(2).isBroken()) {
		    if(gpanel.transferPart2To3.order ==null) {
				gpanel.getTransfer(2).setBreak(false);
				tf2.setText("Break Transfer2");
		    }
	    }
	    else {
	    	if(gpanel.transferPart2To3.order ==null) {
				gpanel.getTransfer(2).setBreak(true);
				tf2.setText("Fix Transfer2");
	    	}
	    }

	}
	else if (s == tf3) {
	    if (gpanel.getTransfer(3).isBroken()) {
	    	if(gpanel.tmPart3.agent.order ==null) {
				gpanel.getTransfer(3).setBreak(false);
				tf3.setText("Break Transfer3");
	    	}
	    }
	    else {
	    	if(gpanel.tmPart3.agent.order ==null) {
				gpanel.getTransfer(3).setBreak(true);
				tf3.setText("Fix Transfer3");
	    	}
	    }

	}

	// Inline machines 
	else if (s == cutter) {
	    if (gpanel.getCutter().isBroken()) {
		gpanel.getCutter().setBreak(false);
		cutter.setText("Break Cutter");
	    }
	    else {
		gpanel.getCutter().setBreak(true);
		cutter.setText("Fix Cutter");
	    }
	}
	else if (s == breakout) {
	    if (gpanel.getBreakout().isBroken()) {
		gpanel.getBreakout().setBreak(false);
		breakout.setText("Break Breakout");
	    }
	    else {
		gpanel.getBreakout().setBreak(true);
		breakout.setText("Fix Breakout");
	    }
	}

	// Stations
	else if (s == seamer) {
	    if (gpanel.getCrossSeamer().isBroken()) {
		gpanel.getCrossSeamer().setBreak(false);
		seamer.setText("Break Cross seamer");
	    }
	    else {
		gpanel.getCrossSeamer().setBreak(true);
		seamer.setText("Fix Cross seamer");
	    }
	}
	else if (s == drill) {
	    if (gpanel.getDrill().isBroken()) {
		gpanel.getDrill().setBreak(false);
		drill.setText("Break Drill");
	    }
	    else {
		gpanel.getDrill().setBreak(true);
		drill.setText("Fix Drill");
	    }
	}
	else if (s == grinder2) {
	    if (gpanel.getGrinder2().isBroken()) {
		gpanel.getGrinder2().setBreak(false);
		grinder2.setText("Break Grinder2");
	    }
	    else {
		gpanel.getGrinder2().setBreak(true);
		grinder2.setText("Fix Grinder2");
	    }
	}
	else if (s == paint) {
	    if (gpanel.getPaint().isBroken()) {
		gpanel.getPaint().setBreak(false);
		paint.setText("Break Paint");
	    }
	    else {
		gpanel.getPaint().setBreak(true);
		paint.setText("Fix Paint");
	    }
	}
	else if (s == grinder) {
	    if (gpanel.getGrinder().isBroken()) {
		gpanel.getGrinder().setBreak(false);
		grinder.setText("Break Grinder1");
	    }
	    else {
		gpanel.getGrinder().setBreak(true);
		grinder.setText("Fix Grinder1");
	    }
	}

	// Inline machines
	else if (s == washer) {
	    if (gpanel.getWasher().isBroken()) {
		gpanel.getWasher().setBreak(false);
		washer.setText("Break Washer");
	    }
	    else {
		gpanel.getWasher().setBreak(true);
		washer.setText("Fix Washer");
	    }
	}
	else if (s == lamp) {
	    if (gpanel.getUVLamp().isBroken()) {
		gpanel.getUVLamp().setBreak(false);
		lamp.setText("Break Lamp");
	    }
	    else {
		gpanel.getUVLamp().setBreak(true);
		lamp.setText("Fix Lamp");
	    }
	}
	else if (s == oven) {
	    if (gpanel.getOven().isBroken()) {
		gpanel.getOven().setBreak(false);
		oven.setText("Break Oven");
	    }
	    else {
		gpanel.getOven().setBreak(true);
		oven.setText("Fix Oven");
	    }
	}

	// Popups
	else if (s == pu1) {
	    if (gpanel.getPopup(1).isBroken()) {
		gpanel.getPopup(1).setBreak(false);
		pu1.setText("Break Popup1");
	    }
	    else {
		gpanel.getPopup(1).setBreak(true);
		pu1.setText("Fix Popup1");
	    }
	}
	else if (s == pu2) {
	    if (gpanel.getPopup(2).isBroken()) {
		gpanel.getPopup(2).setBreak(false);
		pu2.setText("Break Popup2");
	    }
	    else {
		gpanel.getPopup(2).setBreak(true);
		pu2.setText("Fix Popup2");
	    }
	}
	else if (s == pu3) {
	    if (gpanel.getPopup(3).isBroken()) {
		gpanel.getPopup(3).setBreak(false);
		pu3.setText("Break Popup3");
	    }
	    else {
		gpanel.getPopup(3).setBreak(true);
		pu3.setText("Fix Popup3");
	    }
	}
    }

    public void disableTransferButton(int id) {
	switch (id) {
	    case 1:
		tf1.setEnabled(false);
		break;
	    case 2:
		tf2.setEnabled(false);
		break;
	    case 3:
		tf3.setEnabled(false);
		break;
	}
    }

    public void enableTransferButton(int id) {
	switch (id) {
	    case 1:
		tf1.setEnabled(true);
		break;
	    case 2:
		tf2.setEnabled(true);
		break;
	    case 3:
		tf3.setEnabled(true);
		break;
	}
    }
}
