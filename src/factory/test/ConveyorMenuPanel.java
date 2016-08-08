package factory.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import factory.agent.GlassOrder;
import factory.gui.GuiGlass;

public class ConveyorMenuPanel extends JPanel implements ActionListener {

    ConveyorGraphicsPanel graphicsPanel;

    JButton btnGlass;
    JButton makeBusy;
    JButton makeTransferBroken;
    JButton entrySensorDisabled;
    public JLabel tm = new JLabel("");
    public JLabel cm2= new JLabel("");
    public JLabel tm3= new JLabel("");
    public JLabel cm3= new JLabel("");
    public JLabel tm2= new JLabel("");
    public JLabel cm1= new JLabel("");
    public JLabel tm1= new JLabel("");
    public JLabel cm= new JLabel("");
    public JLabel orientation= new JLabel("");
    
    /*
     * 	tm.setSource(cm.agent, cm.gui, FactoryPart.EAST);
	tm.setDestination(cm2.agent, cm2.gui, FactoryPart.NORTH);
	
	cm2.setSource(tm.agent, tm.gui, FactoryPart.SOUTH);
	cm2.setDestination(tm3.agent, tm3.gui, FactoryPart.NORTH);
	
	tm3.setSource(cm2.agent, cm2.gui, FactoryPart.SOUTH);
	tm3.setDestination(cm3.agent, cm3.gui, FactoryPart.EAST);
	
	cm3.setSource(tm3.agent, tm3.gui, FactoryPart.WEST);
	cm3.setDestination(tm2.agent, tm2.gui, FactoryPart.EAST);
	
	tm2.setSource(cm3.agent, cm3.gui, FactoryPart.WEST);
	tm2.setDestination(cm1.agent, cm1.gui, FactoryPart.SOUTH);
	
	cm1.setSource(tm2.agent, tm2.gui, FactoryPart.NORTH);
	cm1.setDestination(tm1.agent, tm1.gui, FactoryPart.SOUTH);
	
	tm1.setSource(cm1.agent, cm1.gui, FactoryPart.NORTH);
	tm1.setDestination(cm.agent, cm.gui, FactoryPart.WEST);
	
	cm.setSource(tm1.agent, tm1.gui, FactoryPart.EAST);
	cm.setDestination(tm.agent, tm.gui, FactoryPart.WEST);
     */
    
    

    public ConveyorMenuPanel() {
	//this(null);
    }

    public ConveyorMenuPanel(ConveyorGraphicsPanel newPanel) {
	graphicsPanel = newPanel;

	btnGlass = new JButton("Add Glass");
	makeBusy = new JButton("Make North Busy");
	makeTransferBroken = new JButton("Break Transfer");
	entrySensorDisabled = new JButton("Break North Entry Sensor");

	btnGlass.addActionListener(this);
	makeBusy.addActionListener(this);
	makeTransferBroken.addActionListener(this);
	entrySensorDisabled.addActionListener(this);
	
	// Add buttons
	 add(btnGlass);
	    add(tm);
	    add(cm2);
	    add(tm3);
	    add(cm3);
	    add(tm2);
	    add(cm1);
	    add(tm1);
	    add(cm);
	    add(orientation);
	    add(makeBusy);
	    add(makeTransferBroken);
	    add(entrySensorDisabled);
    }

    int i=0;
    Timer t = new Timer();
    
    public void createGlass() { 
	    GuiGlass g = new GuiGlass(0, 0, 0);
	    g.setXCoord(graphicsPanel.tmSouthWest.gui.getX()+25);
	    g.setYCoord(graphicsPanel.tmSouthWest.gui.getY() +20 );

	    graphicsPanel.glassList.add(g);
	   
	    graphicsPanel.tmSouthWest.agent.msgHereIsGlass(new GlassOrder("GlassOrder" + i++));
	    
	    graphicsPanel.tmSouthWest.gui.receiveGlass(g,0);

    }
    
    public void actionPerformed(ActionEvent ae) {
    	
	
    JButton button = (JButton)ae.getSource();

    if (ae.getSource() == btnGlass) {
    	createGlass();
	}
    
    if (ae.getSource() == makeBusy) {
    	if(button.getText()=="Make North Busy") {
	    	graphicsPanel.cmNorth.agent.conveyorSwitch = false;
	    	graphicsPanel.cmNorth.agent.pickAndExecuteAnAction();
	    	button.setText("Make North Unbusy");
    	}
    	else { 
    		graphicsPanel.cmNorth.agent.conveyorSwitch = true;
    		graphicsPanel.cmNorth.agent.pickAndExecuteAnAction();
    		button.setText("Make North Busy");
    	}
	}
    if (ae.getSource() == entrySensorDisabled) {
    	if(button.getText()=="Break North Entry Sensor") {
    		graphicsPanel.cmNorth.controller.entrySensorStatus(true);
	    	button.setText("Fix North Entry Sensor");
    	}
    	else { 
    		graphicsPanel.cmNorth.controller.entrySensorStatus(false);
    		button.setText("Break North Entry Sensor");
    	}
	}
    if(ae.getSource() == makeTransferBroken) { 
    	if(button.getText()=="Break Transfer") { 
    		graphicsPanel.tmNorthWest.controller.setDisabled(true);
    		button.setText("Fix Transfer");
    	}
    	else { 
    		graphicsPanel.tmNorthWest.controller.setDisabled(false);
    	}
    }
    
    
	
    

    }

}
