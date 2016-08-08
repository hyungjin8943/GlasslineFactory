package factory.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.awt.*;

import java.util.ArrayList;
import javax.swing.*;

import factory.agent.ConveyorAgent;
import factory.gui.AnimatedPart;
import factory.gui.FactoryPart;
import factory.gui.GuiGlass;
import factory.mediators.ConveyorMediator;
import factory.mediators.TransferMediator;


import java.awt.geom.*;

public class ConveyorGraphicsPanel extends JPanel implements ActionListener {

    javax.swing.Timer timer;
    private final int TIMER_INTERVAL = 1;
    public ArrayList<GuiGlass> glassList = new ArrayList<GuiGlass>();
    public ArrayList<FactoryPart> machines = new ArrayList<FactoryPart>();
    public ArrayList<AnimatedPart> animation = new ArrayList<AnimatedPart>();
    ConveyorMenuPanel mp;

    //AGENT stuff
    ConveyorMediator cmSouth;
    ConveyorMediator cmEast;
    ConveyorMediator cmWest;
    ConveyorMediator cmNorth;
    
    TransferMediator tmSouthWest;
    TransferMediator tmSouthEast;
    TransferMediator tmNorthEast;
    TransferMediator tmNorthWest;
    
    
    public ConveyorGraphicsPanel() {
    
    

	timer = new javax.swing.Timer(TIMER_INTERVAL, this); 



	timer.start();

	// to avoid null pointer exceptions
	cmSouth = new ConveyorMediator("Conveyor_SOUTH");
	cmEast = new ConveyorMediator("Conveyor_EAST");
	cmWest = new ConveyorMediator("Conveyor_WEST");
	cmNorth = new ConveyorMediator("Conveyor_NORTH");
	
	tmSouthWest = new TransferMediator("Transfer_SOUTHWEST");
	tmSouthEast = new TransferMediator("Transfer_SOUTHEAST");
	tmNorthEast = new TransferMediator("Transfer_NORTHEAST");
	tmNorthWest = new TransferMediator("Transfer_NORTHWEST");
	
	tmSouthWest.agent.startThread();
	tmSouthWest.gui.setup(70, 700, 270);
	machines.add(tmSouthWest.gui);
	animation.add(tmSouthWest.gui);
	
	cmWest.agent.startThread();
	cmWest.gui.setup(tmSouthWest.gui.getX(), tmSouthWest.gui.getY()-600, 600, FactoryPart.NORTH);
	cmWest.agent.debug = true;
	machines.add(cmWest.gui);
	animation.add(cmWest.gui);
	
	tmNorthWest.agent.startThread();
	tmNorthWest.gui.setup(cmWest.gui.getX() , 0, 0);
	machines.add(tmNorthWest.gui);
	animation.add(tmNorthWest.gui);
	
	cmNorth.agent.startThread();
	cmNorth.gui.setup(tmNorthWest.gui.getX()+tmNorthWest.gui.getWidth(), tmNorthEast.gui.getY(), 1000, FactoryPart.EAST);
	cmNorth.agent.debug = true;
	machines.add(cmNorth.gui);
	animation.add(cmNorth.gui);
	
	tmNorthEast.agent.startThread();
	tmNorthEast.gui.setup(cmNorth.gui.getX()+cmNorth.gui.getWidth() , 0, 90);
	machines.add(tmNorthEast.gui);
	animation.add(tmNorthEast.gui);
	
	cmEast.agent.startThread();
	cmEast.gui.setup(tmNorthEast.gui.getX(),tmNorthEast.gui.getY() + tmNorthEast.gui.getHeight(), 580, FactoryPart.SOUTH);
	cmEast.agent.debug = true;
	machines.add(cmEast.gui);
	animation.add(cmEast.gui);
	
	tmSouthEast.agent.startThread();
	tmSouthEast.gui.setup(cmEast.gui.getX(), cmEast.gui.getY() + cmEast.gui.getHeight(), 180);
	machines.add(tmSouthEast.gui);
	animation.add(tmSouthEast.gui);
	
	cmSouth.agent.startThread();
	cmSouth.gui.setup(tmSouthWest.gui.getX()+tmSouthWest.gui.getWidth(), tmSouthWest.gui.getY(), 1000, FactoryPart.WEST);
	cmSouth.agent.debug = true;
	machines.add(cmSouth.gui);
	animation.add(cmSouth.gui);
	

	
	


	


	

	

	// Set their source and destination
	tmSouthWest.setSource(cmSouth.agent, cmSouth.gui, FactoryPart.EAST);
	tmSouthWest.setDestination(cmWest.agent, cmWest.gui, FactoryPart.NORTH);
	
	cmWest.setSource(tmSouthWest.agent, tmSouthWest.gui, FactoryPart.SOUTH);
	cmWest.setDestination(tmNorthWest.agent, tmNorthWest.gui, FactoryPart.NORTH);
	
	tmNorthWest.setSource(cmWest.agent, cmWest.gui, FactoryPart.SOUTH);
	tmNorthWest.setDestination(cmNorth.agent, cmNorth.gui, FactoryPart.EAST);
	
	cmNorth.setSource(tmNorthWest.agent, tmNorthWest.gui, FactoryPart.WEST);
	cmNorth.setDestination(tmNorthEast.agent, tmNorthEast.gui, FactoryPart.EAST);
	
	tmNorthEast.setSource(cmNorth.agent, cmNorth.gui, FactoryPart.WEST);
	tmNorthEast.setDestination(cmEast.agent, cmEast.gui, FactoryPart.SOUTH);
	
	cmEast.setSource(tmNorthEast.agent, tmNorthEast.gui, FactoryPart.NORTH);
	cmEast.setDestination(tmSouthEast.agent, tmSouthEast.gui, FactoryPart.SOUTH);
	
	tmSouthEast.setSource(cmEast.agent, cmEast.gui, FactoryPart.NORTH);
	tmSouthEast.setDestination(cmSouth.agent, cmSouth.gui, FactoryPart.WEST);
	
	cmSouth.setSource(tmSouthEast.agent, tmSouthEast.gui, FactoryPart.EAST);
	cmSouth.setDestination(tmSouthWest.agent, tmSouthWest.gui, FactoryPart.WEST);

	
    }

    public void paint(Graphics g) {
    	
    	Graphics2D g2 = (Graphics2D)g;
    	
        AffineTransform tx2 = new AffineTransform();
        tx2.scale(0.5, 0.5);
        g2.setTransform(tx2);

	Color backgroundColor= new Color (79, 129, 189);
	g.setColor(backgroundColor);
	g.fillRect(0, 0, this.getWidth(), this.getHeight());
	
    for(FactoryPart fp: machines) { 
    	fp.drawUnderGlass(g, this);
    }

	for (GuiGlass gl : glassList) {
		gl.update();
		gl.draw(g2, this);
	    
	}

    for(FactoryPart fp: machines) { 
    	fp.drawOverGlass(g, this);
    }

    }
    
    
    

    public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == timer) {
			
			if(tmSouthWest !=null && mp!=null) {
				 if(!(tmSouthWest.agent.order==null)) { 
		    		 mp.btnGlass.setEnabled(false);
		    	 }
		    	 else { 
		    		mp.btnGlass.setEnabled(true);
		    		// mp.createGlass();
		    	 }
			}
			
			for (int i = 0; i < animation.size(); i++) {
				animation.get(i).update();
		    
		    }
			
			if(mp!=null) {
			     mp.tm.setText((tmSouthWest.agent.order !=null)?("1"):("0"));
			     mp.cm2.setText(cmWest.agent.orders.size() + "");
			     mp.tm3.setText((tmNorthWest.agent.order !=null)?("1"):("0"));
			     mp.cm3.setText(cmNorth.agent.orders.size() + "");
			     mp.tm2.setText((tmNorthEast.agent.order !=null)?("1"):("0"));
			     mp.cm1.setText(cmEast.agent.orders.size() + "");
			     mp.tm1.setText((tmSouthEast.agent.order !=null)?("1"):("0"));
			     mp.cm.setText(cmSouth.agent.orders.size() + "");
	
			   
				
				
				
			}
			
			
	
		    repaint();
		}

    }
    
    public void setMenuPanel(ConveyorMenuPanel mp) { 
    	this.mp = mp;
    }
}
