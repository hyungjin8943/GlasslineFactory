package factory.gui;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

import factory.interfaces.*;
import factory.panels.GlassDesign;
import factory.controller.*;

public class GuiWasher extends FactoryPart implements AnimatedPart, MachineControllerInteractor{
	
	int baseXCoord;
	int baseYCoord;
	
	int counter = 0; //needed for the washer cyles
	
	int height;
	int width ;
	int rotation;
	 
	//Following variables are instances of gui glass
    GuiGlass myGlass;
    GlassDesign myGlassDesign;
    //to control the motion of the glass panel
    boolean continueGlassMoving = true;
	
	//Following variables are images
	    ImageIcon washerBase = new ImageIcon("images/washer_base.png");
	    ImageIcon washerBaseCyleLoop1 = new ImageIcon("images/washer_base_cycle_loop1.png");
	    ImageIcon washerBaseCyleLoop2 = new ImageIcon("images/washer_base_cycle_loop2.png");
	    ImageIcon washerBaseCyleLoop3 = new ImageIcon("images/washer_base_cycle_loop3.png");
	    ImageIcon washerTopCyleEnd = new ImageIcon("images/washer_top_cycle_end.png");
	    ImageIcon washerTopCyleLoop1 = new ImageIcon("images/washer_top_cycle_loop1.png");
	    ImageIcon washerTopCyleLoop2 = new ImageIcon("images/washer_top_cycle_loop2.png");
        ImageIcon washerTopCyleLoop3 = new ImageIcon("images/washer_top_cycle_loop3.png");
	    ImageIcon washerTopIdle = new ImageIcon("images/washer_top_idle.png");
	    ImageIcon washerTopStart1 = new ImageIcon("images/washer_top_start1.png");
	    ImageIcon washerTopStart2 = new ImageIcon("images/washer_top_start2.png");
	    ImageIcon washerTopStart3 = new ImageIcon("images/washer_top_start3.png");
	   
	    
	//Following variables are for boolean values to control the machine
	    boolean isIdle;
	    boolean isReceving;
	    boolean isWashing;
	    boolean isPassing; 
	    boolean isOn; 
	    boolean isBreaking;
	    String status = "";
	    
	    int dX;
	    int dY;
		int direction;

    //instance of controller
	public InlineMachineController controller;
public GuiWasher(int x, int y, int degree)
{
	baseXCoord = x;
	baseYCoord = y;
	
	height = washerBase.getIconHeight();
	width = washerBase.getIconWidth();
	
	isIdle = true;
	isReceving = false;
	isWashing = false;
	isPassing = false; 
	isOn = true; 
    isBreaking = false;
    
	rotation = degree;
}

public void update() {
	// TODO Auto-generated method stub
	if(myGlass != null && continueGlassMoving == true) 	
	{myGlass.moveLaterally(FactoryPart.SOUTH, 1);
	if(myGlass != null && isBreaking == true)
	{
		myGlass.breakGlass();
	}
	if(myGlass != null)
    {      
    		
    		if(myGlass.getYCoord() == baseYCoord)
    		{
    			status = "";
    			if(isOn == true)
    	        status = "ON, Receiving";
    			else
    		    status = "OFF, Receiving";
    			
    		 isIdle = false;
    		 isReceving = true;
    		 isWashing = false; 
    		 isPassing = false; 
    		}
    	
    	if(myGlass.getYCoord() > baseYCoord && myGlass.getYCoord() < (baseYCoord + 50) )
    		{
    		status = "";
    		if(isOn == true)
	         status = "ON, Washing";
    		else
    		 status = "OFF";
    		isIdle = false;
    		isReceving = false;
    		if(isOn == true)
    		  isWashing = true;
    		else
    		  isWashing = false;
    		
    		isPassing = false;
    		}
    	
    	if(myGlass.getYCoord() == baseYCoord + myGlass.getHeight() + 30)
    		{
    		status = "";
    		if(isOn == true)
	          status = "ON, Passing";
    		else
    	      status = "OFF";
    		
    		isIdle = true;
    		isReceving = false;
    		isWashing = false; 
    		isPassing = true;
    		if(isOn == true)
    		{myGlass.setEffectsState("washed");
    		 myGlass.fitEffectsImages(myGlassDesign.getPhysicalSchematic());
    		}
    		continueGlassMoving = false;
    		status = "ON, Idle";
    		controller.doneJob();
    		
    		//getPart(FactoryPart.SOUTH).receiveGlass(myGlass, FactoryPart.NORTH);
    		
    		}
    	
    	if(myGlass.getYCoord() < baseYCoord || myGlass.getYCoord() >= (baseYCoord + 97) )
    		{
    		status = "";
	        status = "ON, Idle";
    		isIdle = true;
    		isReceving = false;
    		isWashing = false; 
    		isPassing = false;
    		//finishPassing();
    		}
    	
    	/*if(myGlass != null)
    	{
    	 if(myGlass.getYCoord()  +  myGlass.getHeight()<= baseYCoord)
    	 {
    		getPart(FactoryPart.SOUTH).receiveGlass(myGlass, FactoryPart.SOUTH);
    		continueGlassMoving = false;
    	 }
    	}*/
    	
    }
    else
    		{
    	status = "";
        status = "ON, Idle";
    		isIdle = true;
    		isReceving = false;
    		isWashing = false; 
    		isPassing = false;
    		}
	}
}

public void drawUnderGlass(Graphics g, ImageObserver c) {
	// TODO Auto-generated method stub
	double stationCenterX = baseXCoord + width/2 ;
	double stationCenterY = baseYCoord + height/2 ;
	Graphics2D g2 = (Graphics2D) g;
	g2.rotate(Math.toRadians(rotation), stationCenterX, stationCenterY);
	
	if( isIdle == true || isReceving == true || isOn == false)
		g2.drawImage(washerBase.getImage(),baseXCoord, baseYCoord, c);
	else if(isWashing == true && isOn == true)
		{
		if(counter < 3)
			{
			g2.drawImage(washerBaseCyleLoop1.getImage(),baseXCoord, baseYCoord, c);
			counter++; 
			}
		else if(counter >= 3 && counter < 6)
			{
			g2.drawImage(washerBaseCyleLoop2.getImage(),baseXCoord, baseYCoord, c);
			counter++;
			}
		else if(counter >= 6 && counter < 9)
			{
			g2.drawImage(washerBaseCyleLoop3.getImage(),baseXCoord, baseYCoord, c);
			counter++;
			if(counter == 9)
				counter = 0; 
			}	
		}
	g2.rotate(Math.toRadians(-1 * rotation), stationCenterX, stationCenterY);
}

public void drawOverGlass(Graphics g, ImageObserver c) {
	// TODO Auto-generated method stub
	double stationCenterX = baseXCoord + width/2 ;
	double stationCenterY = baseYCoord + height/2 ;
	Graphics2D g2 = (Graphics2D) g;
	g2.rotate(Math.toRadians(rotation), stationCenterX, stationCenterY);
	if(isIdle == true || isOn == false)
		g2.drawImage(washerTopIdle.getImage(),baseXCoord, baseYCoord, c);
	else if(isReceving == true && isOn == true)
		{
		g2.drawImage(washerTopStart1.getImage(),baseXCoord, baseYCoord, c);
		g2.drawImage(washerTopStart2.getImage(),baseXCoord, baseYCoord, c);
		g2.drawImage(washerTopStart3.getImage(),baseXCoord, baseYCoord, c);
		}
	else if(isWashing == true && isOn == true)
	{
		if(counter < 3)
			{
			g2.drawImage(washerTopCyleLoop1.getImage(),baseXCoord, baseYCoord, c);
			counter++; 
			}
		else if(counter >= 3 && counter < 6)
			{
			g2.drawImage(washerTopCyleLoop2.getImage(),baseXCoord, baseYCoord, c);
			counter++;
			}
		else if(counter >= 6 && counter < 9)
			{
			g2.drawImage(washerTopCyleLoop3.getImage(),baseXCoord, baseYCoord, c);
			counter++;
			if(counter == 9)
				counter = 0;
			}	
	}
	else if(isPassing == true && isOn == true)
		g2.drawImage(washerTopCyleEnd.getImage(),baseXCoord, baseYCoord, c);
	g2.rotate(Math.toRadians(-1 * rotation), stationCenterX, stationCenterY);	
	
	
	
	
}
	
public void setX(int x) {
	// TODO Auto-generated method stub
	baseXCoord = x;
}


public void setY(int y) {
	// TODO Auto-generated method stub
	baseYCoord = y;
}


public int getX() {
	// TODO Auto-generated method stub
	return baseXCoord;
}


public int getY() {
	// TODO Auto-generated method stub
	return baseYCoord;
}


public int getWidth() {
	// TODO Auto-generated method stub
	return width;
}


public int getHeight() {
	// TODO Auto-generated method stub
	return height;
}


public int getRotation() {
	// TODO Auto-generated method stub
	return rotation;
}


public void setRotation(int degree) {
	// TODO Auto-generated method stub
	rotation = degree;
}


public void receiveGlass(GuiGlass glass, int direction) {
	
	System.out.println("Washer direction: " + direction);
	// TODO Auto-generated method stub
	myGlass = glass;
	//System.out.println(myGlass);

	switch (direction) {
	    case FactoryPart.SOUTH:
		{
			this.direction  = FactoryPart.NORTH;
			dX = 0;
			dY = -1;
			 getPart(FactoryPart.SOUTH).finishPassing();
		}
		break;
	    case FactoryPart.EAST:
		direction  = FactoryPart.WEST;
		{
			
			dX = -1;
			dY = 0;
			 getPart(FactoryPart.EAST).finishPassing();
		} 
		break;
	    case FactoryPart.NORTH:
	    { 
	    	this.direction  = FactoryPart.SOUTH;
		  dX = 0;
		  dY = 1;
		  getPart(FactoryPart.NORTH).finishPassing();
	    }
		break;
	    case FactoryPart.WEST:
	    {
	    	this.direction  = FactoryPart.EAST;
	    dX = 1;
		dY = 0;
		 getPart(FactoryPart.WEST).finishPassing();
	    }
		break;
	    default:
		// This shouldn't happen.
		break;
	    }
}

public void setController(InlineMachineController c)
{
	controller = c;
}

public void playJob(String job_type, GlassDesign gd) {
	// TODO Auto-generated method stub
	continueGlassMoving = true; 
	isReceving = true;
	if(!job_type.equals("NO ACTION"))
	 {
		isOn = true;
		myGlassDesign	= gd;
	 }
	else
     isOn = false;
}
public void playPass()
{
	getPart(FactoryPart.SOUTH).receiveGlass(myGlass, FactoryPart.NORTH);
	
}
public void finishPassing() {
	// TODO Auto-generated method stub
	myGlass = null;
	isReceving = false;
	isOn = false;
	controller.donePass();
	//getPart(FactoryPart.SOUTH).receiveGlass(myGlass, FactoryPart.NORTH);
	
	
}
public String getStatus()
{
	return status;
}
public boolean hasGlass()
{
	if(myGlass == null)  return false;
	else                 return true;
}


public void setBreak(boolean flag) {
	// TODO Auto-generated method stub
	isBreaking = flag;
}


public boolean isBroken() {
	// TODO Auto-generated method stub
	return isBreaking;
}



}
