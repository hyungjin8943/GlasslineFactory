package factory.gui;

import java.awt.Graphics;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

import factory.interfaces.*;
import factory.panels.GlassDesign;
import factory.controller.*;


public class GuiOven extends FactoryPart implements AnimatedPart, MachineControllerInteractor{
	
	int baseXCoord;
	int baseYCoord;
	
	int height;
	int width;
	int rotation;
	 
	//Following variables are instances of gui glass
    GuiGlass myGlass;
    
    //to control the motion of the glass panel
    boolean continueGlassMoving = false;
    boolean isBreaking;
    
	//Following variables are images
	    ImageIcon ovenOffBase = new ImageIcon("images/oven_off_base.png");
	    ImageIcon ovenOnBase = new ImageIcon("images/oven_on_base.png");
	    ImageIcon ovenTop = new ImageIcon("images/oven_top.png");
	    
	//Following variables are for boolean values to control the machine
	    boolean isOn;
	    
	    int dX;
	    int dY;
		int direction;
		
	//Controller
	InlineMachineController controller;


public GuiOven(int x, int y, int degree)
{
	baseXCoord = x;
	baseYCoord = y;
	
	height = ovenOffBase.getIconHeight();
	width = ovenOffBase.getIconWidth();
	
	isOn = false;
    isBreaking = false;
	rotation = degree;
}

public void update() {
	// TODO Auto-generated method stub
	if(myGlass != null && continueGlassMoving == true) 
		myGlass.moveLaterally(FactoryPart.WEST, 1);
	if(myGlass != null && isBreaking == true)
	{
		myGlass.breakGlass();
	}
    if(myGlass != null && myGlass.getXCoord() /*+ myGlass.getWidth()*/ <= baseXCoord && continueGlassMoving == true)
     {
    	isOn = false;
    	continueGlassMoving = false;
    	//getPart(FactoryPart.WEST).receiveGlass(myGlass,FactoryPart.EAST);
    	controller.doneJob();
    	
    	
     }
}

public void drawUnderGlass(Graphics g, ImageObserver c) {
	// TODO Auto-generated method stub
	if(isOn == true)
		g.drawImage(ovenOnBase.getImage(),baseXCoord, baseYCoord, c);
	else 
		g.drawImage(ovenOffBase.getImage(),baseXCoord, baseYCoord, c);
	
}

public void drawOverGlass(Graphics g, ImageObserver c) {
	// TODO Auto-generated method stub
	g.drawImage(ovenTop.getImage(), baseXCoord, baseYCoord,c);
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
	//return myOven.getWidth();
}


public int getHeight() {
	// TODO Auto-generated method stub
	return height;
}


public int getRotation() {
	// TODO Auto-generated method stub
	return rotation;
	//return rotation;
}


public void setRotation(int degree) {
	// TODO Auto-generated method stub
	rotation = degree;
}


public void receiveGlass(GuiGlass glass, int direction) {
    System.out.println("[GuiOven] received glass");
	// TODO Auto-generated method stub
	myGlass = glass;

	switch (direction) {
	    case FactoryPart.SOUTH:
		{
			this.direction  = FactoryPart.NORTH;
			dX = 0;
			dY = 1;
			getPart(FactoryPart.SOUTH).finishPassing();
		}
		break;
	    case FactoryPart.EAST:
		{
		    this.direction  = FactoryPart.WEST;
			dX = 1;
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
	 if(!job_type.equals("NO ACTION"))
		{isOn = true;
		 continueGlassMoving = true;
		}
	   else
	   {
		 isOn = false;
		 continueGlassMoving = true;
	   }
}
public void playPass()
{
	getPart(FactoryPart.WEST).receiveGlass(myGlass,FactoryPart.EAST);
	
}
public void finishPassing() {
	// TODO Auto-generated method stub
	
	myGlass = null;
    controller.donePass();
   // System.out.println("done animating");
}	
public String getStatus()
{
	if(isOn == true) return "ON";
	else             return "OFF";
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

