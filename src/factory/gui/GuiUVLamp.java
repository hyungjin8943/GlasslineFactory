package factory.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import factory.interfaces.*;
import factory.panels.GlassDesign;
import factory.controller.*;

public class GuiUVLamp extends FactoryPart implements AnimatedPart, MachineControllerInteractor{
		
	int baseXCoord; 
	int baseYCoord;
	
	int rotation;
	
	int width;
	int height;
	
	//Following variables are instances of gui glass
    GuiGlass myGlass; 
    
    //to control the motion of the glass panel
    boolean continueGlassMoving = false; 
    boolean isBreaking;
    
	//margins
	private final int lampTopXMargin		= 6;
	private final int lampTopYMargin		= 12;
	
	
	//Following variables are images
	    ImageIcon uvlampOffBase = new ImageIcon("images/uvlamp_off_base.png");
	    ImageIcon uvlampOffTop = new ImageIcon("images/uvlamp_off_top.png");
	    ImageIcon uvlampOnBase = new ImageIcon("images/uvlamp_on_base.png");
	    ImageIcon uvlampOnTop = new ImageIcon("images/uvlamp_on_top.png");
	    
	//Following variables are for boolean values to control the machine
	    boolean isOn;
	    
	int dX;
    int dY;
	int direction;
    int tdirection;
    //instance of controller
	public InlineMachineController controller;
	
public GuiUVLamp(int x, int y, int degree)
{
	baseXCoord = x; 
	baseYCoord = y; 
	
	isOn = false;
    isBreaking = false;
    
	width = uvlampOffBase.getIconWidth();
    height = uvlampOffBase.getIconHeight();
    
	rotation = degree;
}

public void update() {
	// TODO Auto-generated method stub
	if(myGlass != null && continueGlassMoving == true) 
	myGlass.moveLaterally(direction, 1);
    
	if(myGlass != null && continueGlassMoving == true)
	{
	 switch (direction)
	 {
	   case FactoryPart.SOUTH: if(myGlass.getYCoord() + myGlass.getHeight() >= baseYCoord + height)  
	                          {controller.doneJob();
		                       continueGlassMoving = false;} break;
	   case FactoryPart.NORTH: if(myGlass.getYCoord() <= baseYCoord)  
                              {controller.doneJob();
                              continueGlassMoving = false;} break;
	   case FactoryPart.WEST:  if(myGlass.getXCoord() <=  baseXCoord)  
                              {controller.doneJob();
                              continueGlassMoving = false;} break;
       case FactoryPart.EAST: if(myGlass.getXCoord() + myGlass.getWidth()  >= baseXCoord + width)  
                              {controller.doneJob();
                              continueGlassMoving = false;} break;                        
     
	 }
	}
	if(isBreaking == true && myGlass != null)
	{
		myGlass.breakGlass();
	}
}

public void drawUnderGlass(Graphics g, ImageObserver c) {
	// TODO Auto-generated method stub
	Graphics2D g2 = (Graphics2D) g;
	int centerXCoord = baseXCoord + width / 2;
	int centerYCoord = baseYCoord + height / 2;
	g2.rotate(Math.toRadians(rotation), centerXCoord, centerYCoord);
	if(isOn == true)
		g.drawImage(uvlampOnBase.getImage(),baseXCoord, baseYCoord, c);
	else 
		g.drawImage(uvlampOffBase.getImage(),baseXCoord, baseYCoord, c);
	g2.rotate(Math.toRadians(-rotation), centerXCoord, centerYCoord);
}

public void drawOverGlass(Graphics g, ImageObserver c) {
	// TODO Auto-generated method stub
	Graphics2D g2 = (Graphics2D) g;
	int centerXCoord = baseXCoord + width / 2;
	int centerYCoord = baseYCoord + height / 2;
	g2.rotate(Math.toRadians(rotation), centerXCoord, centerYCoord);
	if(isOn == true)
		g.drawImage(uvlampOnTop.getImage(),baseXCoord+lampTopXMargin , baseYCoord+lampTopYMargin, c);
	else 
		g.drawImage(uvlampOffTop.getImage(),baseXCoord+lampTopXMargin, baseYCoord+lampTopYMargin, c);
	g2.rotate(Math.toRadians(-rotation), centerXCoord, centerYCoord);
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
	//return 0;
	return height;
}


public int getRotation() {
	// TODO Auto-generated method stub
	//return 0;
	return rotation;
}


public void setRotation(int degree) {
	// TODO Auto-generated method stub
	rotation = degree;
}


public void receiveGlass(GuiGlass glass, int direction) {
	// TODO Auto-generated method stubglass = newGlass;

	
    myGlass = glass;

	switch (direction) {
	    case FactoryPart.SOUTH:
		{
			this.direction  = FactoryPart.NORTH;
			this.tdirection = FactoryPart.SOUTH;
			dX = 0;
			dY = 1;
			getPart(FactoryPart.SOUTH).finishPassing();
		}
		break;
	    case FactoryPart.EAST:
		this.direction  = FactoryPart.WEST;
		{
			this.tdirection = FactoryPart.EAST;
			
			dX = 1;
			dY = 0;
			getPart(FactoryPart.EAST).finishPassing();
		} 
		break;
	    case FactoryPart.NORTH:
	    { this.tdirection = FactoryPart.NORTH;
		
		  this.direction  = FactoryPart.SOUTH;
		  dX = 0;
		  dY = 1;
		  getPart(FactoryPart.NORTH).finishPassing();
	    }
		break;
	    case FactoryPart.WEST:
	    {
	    this.direction  = FactoryPart.EAST;
	    this.tdirection = FactoryPart.WEST;
		
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
public void finishPassing() {
	// TODO Auto-generated method stub
	//System.out.println("DOEUWEHEUSHEGTE");
	isOn = false;
	myGlass = null;
	controller.donePass();
	
	
	
	
	
}
public void playPass()
{
	getPart(this.direction).receiveGlass(myGlass, tdirection);
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

@Override
public void setBreak(boolean flag) {
	// TODO Auto-generated method stub
	isBreaking = flag;
}

@Override
public boolean isBroken() {
	// TODO Auto-generated method stub
	return isBreaking;
}




}
