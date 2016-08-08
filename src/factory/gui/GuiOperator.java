package factory.gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.util.*;
import javax.swing.*;

import factory.controller.OperatorController;

import java.io.*;
public class GuiOperator extends FactoryPart implements AnimatedPart{
    //Following variables are basic features of Gui Operator
	 int XCoord;
	 int YCoord;
	 int rotation;
	 int height;
	 int width ;
	 double theta ;
	//Following variables are to control the operator
	 boolean isFetching;
	 boolean isComingBack;
	 boolean isPassing;
	 boolean DoneOperating;
	 boolean isIdle;
	 boolean isPreparing;
	 boolean isMoving; 
	 boolean isTrash;
	 
	 String status;
	 
	 int dX;
	 int dY;
	 double dTheta;
	 int Xcenter;
	 int Ycenter;
	 int direction;
	 int tdirection;
	//Gui Glass
	 GuiGlass myGlass;
	 
	//Images
	 ImageIcon operatorBody = new ImageIcon("images/operator_body.png");
	 ImageIcon operatorHead = new ImageIcon("images/operator_head.png");
	 ImageIcon operatorLeftArm = new ImageIcon("images/operator_leftarm.png");
	 ImageIcon operatorRightArm = new ImageIcon("images/operator_rightarm.png");
	 ImageIcon operatorLeftShoe = new ImageIcon("images/operator_shoe.png");
	 ImageIcon operatorRightShoe = new ImageIcon("images/operator_shoe.png");
	 
	// 
	 private  int bodyXCoord;
	 private  int bodyYCoord;
	 private  int headXCoord;
	 private  int headYCoord;
	 private  int leftarmXCoord;
	 private  int leftarmYCoord;
	 private  int rightarmXCoord;
	 private  int rightarmYCoord;
	 private  int leftshoeXCoord;
	 private  int leftshoeYCoord;
	 private  int rightshoeXCoord;
	 private  int rightshoeYCoord;
	 
	 private int armHeight;
	 private int armWidth;
	 
	 private int armBaseHeight;
	 private int armBaseWidth;
	 
	 private int bodyBaseXCoord;
	 private int bodyBaseYCoord;
	 private int leftarmBaseXCoord;
	 private int leftarmBaseYCoord;
	 private int rightarmBaseXCoord;
	 private int rightarmBaseYCoord;
	 private int headBaseXCoord;
	 private int headBaseYCoord;
	 private int leftshoeBaseXCoord;
	 private int leftshoeBaseYCoord;
	 private int rightshoeBaseXCoord;
	 private int rightshoeBaseYCoord;
	 private int truckXCoord = 0;
	 private int truckYCoord = 100;
	 
	 private OperatorController controller;
	 
	 
	public GuiOperator(int x, int y, int degree)
	{
		XCoord = x;
		YCoord = y;
		rotation = degree;
		//width = XCoord + operatorBody.getIconWidth() / 2 + operatorLeftArm.getIconWidth();
		
		dX = 0;
		dY = 0;
		dTheta = 0.1;
		
		
		
		bodyXCoord = x;
		bodyYCoord = y;
		
		headXCoord = bodyXCoord ;
		headYCoord = bodyYCoord + operatorBody.getIconHeight()/2 - 20;
		
		leftarmXCoord = bodyXCoord + operatorBody.getIconWidth()/2;
		leftarmYCoord = bodyYCoord;
		
		rightarmXCoord = bodyXCoord + operatorBody.getIconWidth()/2;
		rightarmYCoord = bodyYCoord + operatorBody.getIconHeight() - operatorRightArm.getIconHeight();
		
		leftshoeXCoord = bodyXCoord + operatorBody.getIconWidth()/2 + 10;
		leftshoeYCoord = bodyYCoord + operatorBody.getIconHeight() - 55;
		
		rightshoeXCoord = bodyXCoord + operatorBody.getIconWidth()/2 + 10;
		rightshoeYCoord = bodyYCoord + operatorBody.getIconHeight()/2 + 5;
		
		armHeight  = operatorLeftArm.getIconHeight();
		armWidth = operatorLeftArm.getIconWidth();
		armWidth = 15;
		armBaseHeight = armHeight;
		armBaseWidth = armWidth;
		
		//
		
		bodyBaseXCoord = bodyXCoord;
		bodyBaseYCoord = bodyYCoord;
	    headBaseXCoord = headXCoord;
	    headBaseYCoord = headYCoord;
	    leftarmBaseXCoord = leftarmXCoord;
	    leftarmBaseYCoord = leftarmYCoord;
	    rightarmBaseXCoord = rightarmXCoord;
	    rightarmBaseYCoord = rightarmYCoord;
	    leftshoeBaseXCoord = leftshoeXCoord;
	    leftshoeBaseYCoord = leftshoeYCoord;
	    rightshoeBaseXCoord = rightshoeXCoord;
	    rightshoeBaseYCoord = rightshoeYCoord;
		
	    width =  operatorBody.getIconWidth()/2 + 10 + operatorLeftShoe.getIconWidth();
	    height = operatorBody.getIconHeight();
	    
	    Xcenter = x + width /  2;
		Ycenter = y + height / 2;
	    
		isIdle = true;
		isFetching = false;
		//isRotating = false;
		isPassing = false;
		isComingBack = false;
		DoneOperating = false;
		isPreparing = false;
		isMoving = false;
		isTrash = false;
		status= "";
	}
	 
	public void update() {
		// TODO Auto-generated method stub
	   if(myGlass != null || isComingBack == true || isMoving == true)
	   { if(isIdle == true && isPreparing == true)
		 { //System.out.println("PREPARING"); 
		   status = "";
		    status = "ON, Preparing";
			 dX = 2;
			 dY = 0;
			armWidth += dX;
			armHeight += dY;
			
			if(leftarmXCoord + armWidth >= myGlass.getXCoord()+5)
			{
				isIdle = false;
				isFetching = true;
			}
			
		}
         if(isFetching == true)
         {   status = "";
		    status = "ON, Fetching";
        	 myGlass.moveLaterally(WEST, dX);
        	 armWidth -= dX;
        	 armHeight -= dY;
        	 
             if(myGlass.getXCoord() <= leftshoeXCoord + operatorLeftShoe.getIconWidth()+3)
             { isFetching = false;
               isMoving = true;
               //System.out.println("Going to Move!");
             }
         }
         
         if(isMoving == true)
         {   status = "";
		    status = "ON, Moving";
		    
        	 if(myGlass !=null)
        	 {
             myGlass.moveLaterally(WEST, dX);
           	 headXCoord += -dX;
        	 headYCoord += -dY;
        	 
        	 bodyXCoord += -dX;
        	 bodyYCoord += -dY;
        	 
        	 leftarmXCoord += -dX;
        	 leftarmYCoord += -dY;
        	 
        	 rightarmXCoord += -dX;
        	 rightarmYCoord += -dY;
        	 
        	 leftshoeXCoord += -dX;
        	 leftshoeYCoord += -dY;
        	 
        	 rightshoeXCoord += -dX;
        	 rightshoeYCoord += -dY;
        	 if(myGlass.getXCoord() <= getPart(tdirection).getX() + 10)
        	    {
        		 if(isTrash != true)
        		 {
        		  isMoving = false;
        		  isPassing = true;
        		 }
        		 if(isTrash == true)
                 {  System.out.println("TRASH");
                	TrashAnimation(); 
                 }
        		 //getPart(EAST).finishPassing();
        	    }
        	} 
        	
        	 else
        	 {   dX = 2;
        	     
        		 headXCoord += dX;
            	 headYCoord += dY;
            	 
            	 bodyXCoord += dX;
            	 bodyYCoord += dY;
            	 
            	 leftarmXCoord += dX;
            	 leftarmYCoord += dY;
            	 
            	 rightarmXCoord += dX;
            	 rightarmYCoord += dY;
            	 
            	 leftshoeXCoord += dX;
            	 leftshoeYCoord += dY;
            	 
            	 rightshoeXCoord += dX;
            	 rightshoeYCoord += dY;
            	 if(armWidth > armBaseWidth)
            	 { armWidth -= dX;
            	   armHeight -= dY;
            	 }
            	 if(bodyXCoord >= bodyBaseXCoord)
            	    {
            		 isMoving = false;
            		 isComingBack = false;
            		 DoneOperating = true;
            		 isIdle = true;
            		 isPreparing = false;
            		 
            		 status = "";
         		     status = "ON, Idle";
            	    }
        	 }
         }
        
         if(isPassing == true)
         {   status = "";
		    status = "ON, Passing";
		   
        	 if(getPart(FactoryPart.NORTH) != null)
        	  {
        		 myGlass.moveLaterally(NORTH, 2);
        		 dX = 2;
        	  }
        	 else
        	  {
        		 myGlass.moveLaterally(SOUTH, 2);
        		 dX = -2;
        	  }
        	 
        	 headXCoord += 0;
        	 headYCoord += -dX;
        	 
        	 bodyXCoord += 0;
        	 bodyYCoord += -dX;
        	 
        	 leftarmXCoord += 0;
        	 leftarmYCoord += -dX;
        	 
        	 rightarmXCoord += 0;
        	 rightarmYCoord += -dX;
        	 
        	 leftshoeXCoord += 0;
        	 leftshoeYCoord += -dX;
        	 
        	 rightshoeXCoord += 0;
        	 rightshoeYCoord += -dX;
        	
        	 if(getPart(FactoryPart.NORTH)!=null  && myGlass != null)
           { 
        	 if(myGlass.getYCoord() <= getPart(NORTH).getY() + 50)
        	 {   
        		 isPassing = false;
        		 isComingBack = true;
        		 System.out.println("TRUCK RECEVING!");
        		 
        		 getPart(NORTH).receiveGlass(myGlass, SOUTH);
        		 
        	 }
           }
        	 else  if(getPart(FactoryPart.SOUTH) != null && myGlass != null)
           {
        		  if(myGlass.getYCoord() >= getPart(SOUTH).getY() + 30)
             	 {
             		 isPassing = false;
             		 isComingBack = true;
             		 getPart(SOUTH).receiveGlass(myGlass,NORTH);
             		
             	 } 
           }
        	 //System.out.println(theta);
         }
         if(isComingBack == true)
         {  status = "";
		    status = "ON, Backing";
        	 headXCoord += 0;
        	 headYCoord += dX;
        	 
        	 bodyXCoord += 0;
        	 bodyYCoord += dX;
        	 
        	 leftarmXCoord += 0;
        	 leftarmYCoord += dX;
        	 
        	 rightarmXCoord += 0;
        	 rightarmYCoord += dX;
        	 
        	 leftshoeXCoord += 0;
        	 leftshoeYCoord += dX;
        	 
        	 rightshoeXCoord += 0;
        	 rightshoeYCoord += dX;
        	 
        	 if(getPart(NORTH) != null && bodyYCoord >= bodyBaseYCoord)
        	 {
        		 isComingBack = false;
        		 isMoving = true;
             }
        	 else if(getPart(SOUTH) != null && bodyYCoord <= bodyBaseYCoord)
        	 {
        		 isComingBack = false;
        		 isMoving = true;
        	 }
         }
	   }
	}
	 
	public void drawUnderGlass(Graphics g, ImageObserver c) {
		// TODO Auto-generated method stub
		Graphics2D g2 = (Graphics2D) g;
		int centerXCoord = bodyXCoord + width / 2;
		int centerYCoord = bodyYCoord + height /2;
		g2.rotate(Math.toRadians(rotation), centerXCoord, centerYCoord);
		g2.drawImage(operatorLeftShoe.getImage(), leftshoeXCoord, leftshoeYCoord, c);
		g2.drawImage(operatorRightShoe.getImage(), rightshoeXCoord, rightshoeYCoord, c);
		g2.drawImage(operatorLeftArm.getImage(), leftarmXCoord, leftarmYCoord,armWidth,armHeight,c);
		g2.drawImage(operatorRightArm.getImage(), rightarmXCoord, rightarmYCoord,armWidth,armHeight, c);
		g2.drawImage(operatorBody.getImage(), bodyXCoord, bodyYCoord, c);
		g2.drawImage(operatorHead.getImage(), headXCoord, headYCoord, c);
		g2.rotate(Math.toRadians(-rotation), centerXCoord, centerYCoord);
	}
	 
	public void drawOverGlass(Graphics g, ImageObserver c) {
		// TODO Auto-generated method stub
		
	}
	 
	public void receiveGlass(GuiGlass glass, int direction) {
        //isFetching = true; 
		myGlass = glass;
		isPreparing = true;
		System.out.println("GuiGrinder: receivedGlass");

		switch (direction) {
		    case FactoryPart.WEST:
			{
				this.direction  = FactoryPart.WEST;
				dX = -2;
				dY =  0;
			}
			break;
		    case FactoryPart.EAST:
		    {
			this.direction  = FactoryPart.EAST;
			dX = 2;
			dY = 0;
		    }
			break;
		    
		    case FactoryPart.NORTH:
			{
				this.direction  = FactoryPart.NORTH;
				dX = 0;
				dY = 2;
			}

			break;
		    case FactoryPart.SOUTH:
			{
				this.direction  = FactoryPart.SOUTH;
				dX = 0;
				dY = 2;
			}

			break;
		    default:
			// This shouldn't happen.
			break;
		}
		if(getPart(SOUTH) != null)
		 {
			this.tdirection = SOUTH;
		 }
		else if(getPart(NORTH) != null) 
		 {
			this.tdirection = NORTH;
		 }
	}
	 
	public void finishPassing() {
		DoneOperating = false;
		//isIdle = true;
		myGlass = null;
		
		controller.animationDone();

		
	}
	 
	public void setX(int x) {
		// TODO Auto-generated method stub
		bodyXCoord = x;
		headXCoord = bodyXCoord ;
		//headYCoord = bodyYCoord + operatorBody.getIconHeight()/2 - 20;
		
		leftarmXCoord = bodyXCoord + operatorBody.getIconWidth()/2;
		//leftarmYCoord = bodyYCoord;
		
		rightarmXCoord = bodyXCoord + operatorBody.getIconWidth()/2;
		//rightarmYCoord = bodyYCoord + operatorBody.getIconHeight() - operatorRightArm.getIconHeight();
		
		leftshoeXCoord = bodyXCoord + operatorBody.getIconWidth()/2 + 10;
		//leftshoeYCoord = bodyYCoord + operatorBody.getIconHeight() - 55;
		
		rightshoeXCoord = bodyXCoord + operatorBody.getIconWidth()/2 + 10;
		//rightshoeYCoord = bodyYCoord + operatorBody.getIconHeight()/2 + 5;
		
		armHeight  = operatorLeftArm.getIconHeight();
		//armWidth = operatorLeftArm.getIconWidth();
		armWidth = 15;
		armBaseHeight = armHeight;
		//armBaseWidth = armWidth;
		
		//
		
		bodyBaseXCoord = bodyXCoord;
		//bodyBaseYCoord = bodyYCoord;
	    headBaseXCoord = headXCoord;
	    //headBaseYCoord = headYCoord;
	    leftarmBaseXCoord = leftarmXCoord;
	    //leftarmBaseYCoord = leftarmYCoord;
	    rightarmBaseXCoord = rightarmXCoord;
	    //rightarmBaseYCoord = rightarmYCoord;
	    leftshoeBaseXCoord = leftshoeXCoord;
	    //leftshoeBaseYCoord = leftshoeYCoord;
	    rightshoeBaseXCoord = rightshoeXCoord;
	    //rightshoeBaseYCoord = rightshoeYCoord;
	    width =  operatorBody.getIconWidth()/2 + 10 + operatorLeftShoe.getIconWidth();
	    //height = bodyBaseYCoord + operatorBody.getIconHeight() / 2;
	    
	    Xcenter = x + width /  2;
		//Ycenter = y + height / 2;
	}
	 
	public void setY(int y) {
		// TODO Auto-generated method stub
		bodyYCoord = y;
		//headXCoord = bodyXCoord ;
		headYCoord = bodyYCoord + operatorBody.getIconHeight()/2 - 20;
		
		//leftarmXCoord = bodyXCoord + operatorBody.getIconWidth()/2;
		leftarmYCoord = bodyYCoord;
		
		//rightarmXCoord = bodyXCoord + operatorBody.getIconWidth()/2;
		rightarmYCoord = bodyYCoord + operatorBody.getIconHeight() - operatorRightArm.getIconHeight();
		
		//leftshoeXCoord = bodyXCoord + operatorBody.getIconWidth()/2 + 10;
		leftshoeYCoord = bodyYCoord + operatorBody.getIconHeight() - 55;
		
		//rightshoeXCoord = bodyXCoord + operatorBody.getIconWidth()/2 + 10;
		rightshoeYCoord = bodyYCoord + operatorBody.getIconHeight()/2 + 5;
		
		armHeight  = operatorLeftArm.getIconHeight();
		armWidth = operatorLeftArm.getIconWidth();
		armWidth = 15;
		armBaseHeight = armHeight;
		armBaseWidth = armWidth;
		
		//
		
		//bodyBaseXCoord = bodyXCoord;
		bodyBaseYCoord = bodyYCoord;
	    //headBaseXCoord = headXCoord;
	    headBaseYCoord = headYCoord;
	    //leftarmBaseXCoord = leftarmXCoord;
	    leftarmBaseYCoord = leftarmYCoord;
	    //rightarmBaseXCoord = rightarmXCoord;
	    rightarmBaseYCoord = rightarmYCoord;
	    //leftshoeBaseXCoord = leftshoeXCoord;
	    leftshoeBaseYCoord = leftshoeYCoord;
	    //rightshoeBaseXCoord = rightshoeXCoord;
	    rightshoeBaseYCoord = rightshoeYCoord;
	    
	    //width = bodyBaseXCoord +  operatorBody.getIconWidth() / 2 + armBaseWidth;
	    height = operatorBody.getIconHeight();
	    
	    //Xcenter = x + width /  2;
		Ycenter = y + height / 2;
	}
	 
	public int getX() {
		// TODO Auto-generated method stub
		return bodyXCoord;
	}
	 
	public int getY() {
		// TODO Auto-generated method stub
		return bodyYCoord;
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
		rotation  = degree;
	}

	public void setController(OperatorController controller) {
		this.controller = controller;
	}
	public void trashGlass()
	{
		isTrash = true;
		isPreparing = true;
    }
	public void setState(String state) {
		if(state.equals("Putting Glass On Truck")) {
			isPreparing = true;
		}
		
		
	}
	public void TrashAnimation()
	{   int dZ = 1;
		for(int i = 1; i < 30 ; i++)
		 {
	 		myGlass.moveVertically(-dZ);
		 }
		isMoving = true;
		myGlass.setTreatmentCompleted(true);
		myGlass = null;
		isTrash  =false;
		
		controller.animationDone();
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
		
	}


	public boolean isBroken() {
		// TODO Auto-generated method stub
		return false;
	}
}
