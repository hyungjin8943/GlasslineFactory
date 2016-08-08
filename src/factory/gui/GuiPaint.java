package factory.gui;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import  factory.interfaces.*;
import factory.panels.GlassDesign;
import factory.controller.*;


public class GuiPaint extends FactoryPart implements AnimatedPart, MachineControllerInteractor, OperatorControllerInteractor{
    //Variables
	  //Following variables are for basic features of GuiPaint
	private final int				initPlowXMargin		= 29;
	private final int				initPlowYMargin		= 22;
	private final int				initMachineXMargin	= 13;
	private final int				initMachineYMargin	= 13;
	
	int height;
	int width ;
	int rotation;
	  //Following variables are for boolean values to control the machine
    boolean isPainting;
    boolean isReceiving;
    boolean isDoneOne;
    boolean isDoneTwo;
    boolean isDoneThree;
    boolean isPreparing;
    boolean isPushingBack;
    boolean isBreaking;
    String status;
    
    int count;
	int dX;
	int dY;
	int direction;
	int tdirection;
	int armdX;
	int armdY;
	int plowdX;
	int plowdY;
	int dHeight;
	
      //Following variables are instances of gui glass
    GuiGlass myGlass;
    
     
      //Following variabels are images
    ImageIcon paintBase = new ImageIcon("images/paint_base.png");
    ImageIcon paintArm = new ImageIcon("images/paint_machine_arm.png");
    ImageIcon paintMachineBase = new ImageIcon("images/paint_machine_base.png");
    ImageIcon paintPlow = new ImageIcon("images/paint_machine_plow.png");
    Rectangle2D.Double colorRect = new Rectangle2D.Double();
    
     //data of distances in the graph
    int plowXCoord;
    int plowYCoord;
    int baseXCoord;
    int baseYCoord;
    int mbaseXCoord;
    int mbaseYCoord;
    int armXCoord;
    int armYCoord;
    
    int armbaseX;
    int armbaseY;
    int plowbaseX;
    int plowbaseY;
   
    //color ArrayList 
    private	ArrayList<ArrayList<Color>>		paintSchematic		= new ArrayList<ArrayList<Color>>();
    
    //instance of controller
    public StandAloneMachineController controller;
    public OperatorController opcontroller;
    public GuiPaint()
    {
    	baseXCoord = 0;
    	baseYCoord = 0;
    	
    	mbaseXCoord = baseXCoord + initMachineXMargin;
    	mbaseYCoord = baseYCoord + initMachineYMargin;
    	
    	plowXCoord = baseXCoord + initPlowXMargin;
    	plowYCoord = baseYCoord + initPlowYMargin;
    	
    	plowbaseX = plowXCoord;
    	plowbaseY = plowYCoord;
    	
    	armXCoord = mbaseXCoord + 3;
    	armYCoord = plowYCoord + 40;
    	
    	armbaseX = armXCoord;
    	armbaseY = armYCoord;
    	
    	height = paintBase.getIconHeight();
    	width = paintBase.getIconWidth();
    	
    	
    	isReceiving = false;
    	isPainting = false;
    	isDoneOne = false;
    	isDoneTwo = false;
    	isDoneThree = false;
    	isPreparing = false;
        isPushingBack  = false;
        isBreaking = false;
    	status = "";
        
    	rotation = 0;
    	
    	armdX = 0;
    	armdY = 0;
    	plowdX = 0;
    	plowdY = 0;
    	dHeight = 0;
    }
    
    public GuiPaint(int x, int y, int degree)
    {
    	baseXCoord = x;
    	baseYCoord = y;
    	
    	mbaseXCoord = baseXCoord + initMachineXMargin;
    	mbaseYCoord = baseYCoord + initMachineYMargin;
    	
    	plowXCoord = baseXCoord + initPlowXMargin;
    	plowYCoord = baseYCoord + initPlowYMargin;
    	
    	plowbaseX = plowXCoord;
    	plowbaseY = plowYCoord;
    	
    	armXCoord = mbaseXCoord + 3;
    	armYCoord = plowYCoord + 40;
    	
    	armbaseX = armXCoord;
    	armbaseY = armYCoord;
    	
    	height = paintBase.getIconHeight();
    	width = paintBase.getIconWidth();
    	
    	
    	isReceiving = false;
    	isPainting = false;
    	isDoneOne = false;
    	isDoneTwo = false;
    	isDoneThree = false;
    	isPreparing = false;
    	rotation = degree;
    	
    	armdX = 0;
    	armdY = 0;
    	plowdX = 0;
    	plowdY = 0;
    	dHeight = 0;
    }
	public void update() {
		// TODO Auto-generated method stub
		
	 if(myGlass != null  || isDoneThree == true)
	 {	 
		 if((isReceiving == false) && (isPainting == false) && (isDoneOne == false) && (isDoneTwo == false) && (isDoneThree == false))
	      { status = "";
		    status = "ON, Preparing"; 
		    switch(tdirection)
		    {
		    case FactoryPart.NORTH:
		     if(myGlass.getYCoord() <= baseYCoord + height)
		        isReceiving  = true;
             break;
		    case FactoryPart.SOUTH:
		     if(myGlass.getYCoord() + myGlass.getHeight() >= baseYCoord)
		    	 isReceiving = true;
		      break;
		  
		    }
		    myGlass.moveLaterally(tdirection, dY);
		   
		  }
	    else if(isReceiving == true)
	      { status = "";
		    status = "ON, Receiving";  
	    	if(direction == FactoryPart.SOUTH ) 
	         {  
	    	    myGlass.moveLaterally(NORTH, dY);
	         }
	     else if(direction == FactoryPart.NORTH)
	         {  
	    	    myGlass.moveLaterally(SOUTH, dY);
	         }
	     
	        if(direction == FactoryPart.SOUTH)
	        {
	        	if(myGlass.getYCoord() <= (armYCoord + 10))
	    	    {
	    		 isReceiving = false;
	    		 
	    		 isPainting = true;
	    		 getPart(this.direction).finishPassing();
	    		 return;
	    	    }
	        }
	        else if(direction == FactoryPart.NORTH)
	         { 
	        	if(myGlass.getYCoord() >= height + baseYCoord - 70 - myGlass.getHeight())
	        	    {
		    		 isReceiving = false;
		    		 isPainting = true;
		    		 getPart(this.direction).finishPassing();
		    		 return;
		    	    } 
             }

	     }
	 else   if(isPainting == true && isPreparing == true)
		{   
		    status = "";
		    status = "ON, Painting";
			 armdX = 0;
    		 armdY = -1;
		 	 dHeight -= armdY;
		 	 
    		 armXCoord += armdX;
    		 armYCoord -= armdY;
    		 
    		 //colorRect.setFrame(myGlass.getXCoord() , myGlass.getYCoord() + 40, myGlass.getWidth(), dHeight);
    		 int baseX = myGlass.getXCoord();
    		 int baseY = myGlass.getYCoord();
    		 
    		
    		 //This part is to draw colors pixel by pixel
    		 if(dHeight <= myGlass.getStdHeight())
    		 {    if(isBreaking != true)
    			  {
    			   for(int j = 0; j< paintSchematic.get(myGlass.getStdHeight()-dHeight).size();j++)
    		       {  
    				  myGlass.paintGlassAtPoint(new Point2D.Double(j,myGlass.getStdHeight()-dHeight), paintSchematic.get(myGlass.getStdHeight()-dHeight).get(j));
    			   }
    			  }
    		      if(isBreaking == true)
    		      {
    		    	  for(int j = 0; j< paintSchematic.get(myGlass.getStdHeight()-dHeight).size();j++)
       		       {  if(paintSchematic.get(myGlass.getStdHeight()-dHeight).get(j).equals(new Color(64,64,64)))
       				   myGlass.paintGlassAtPoint(new Point2D.Double(j,myGlass.getStdHeight()-dHeight), paintSchematic.get(myGlass.getStdHeight()-dHeight).get(j));
       		          else
       		          { Random r = new Random();
       		            int x1 = Math.abs((r.nextInt()) % 255);
       		            int x2 = Math.abs((r.nextInt()) % 255);
       		            int x3 = Math.abs((r.nextInt()) % 255);
       		            
       		            myGlass.paintGlassAtPoint(new Point2D.Double(j,myGlass.getStdHeight()-dHeight),new Color(x1,x2,x3));
       		          }
       			   }
    		      }
    		    }
    		 
    		 if(dHeight >= myGlass.getHeight())
    		 {
    			 isPainting = false;
    			 isDoneOne = true;
    			 return;
    		 }
    	}
	 else  if(isDoneOne == true )
		  {   status = "";
		    status = "ON, Painted";
		      //myGlass.setEffectsState("painted");
		 
			  armdX = 0;
    		  armdY = 1;
    		  
    		  
    		  
			 armXCoord += armdX;
			 armYCoord -= armdY;
			 //colorRect.setFrame(myGlass.getXCoord(), myGlass.getYCoord(), myGlass.getWidth(), myGlass.getHeight()
			
			 if(armYCoord <= armbaseY)
			 {
				 isDoneOne = false;
				 isDoneTwo = true;
				 

	    		 
	    		  controller.doneJob();
	    		  
			     ///////////////////////
				 
			 }
			     
			}   
		 
	 else   if(isDoneTwo == true  && isPushingBack == true)
		{   status = "";
	        status = "ON, Pushing Back";
			plowdX = 0;
			plowdY = 1;
			dX = 0;
			dY = 1  * (int)Math.cos(Math.toRadians(rotation));
	

	        
			if(plowYCoord - paintPlow.getIconHeight() <= plowbaseY - 15 )
			{
			 plowXCoord += plowdX;
			 plowYCoord += 1;
			}
			if(direction == FactoryPart.SOUTH)
			 myGlass.moveLaterally(NORTH, dY);
			if(direction == FactoryPart.NORTH)
			 myGlass.moveLaterally(SOUTH, dY);
			 //colorRect.setFrame(myGlass.getXCoord(), myGlass.getYCoord(), myGlass.getWidth(), myGlass.getHeight());
			if(myGlass.getYCoord()  <=  baseYCoord )
			{
				isDoneTwo = false;
				isDoneThree = true;
				isPushingBack = false;
				isPreparing = false;
				getPart(direction).receiveGlass(myGlass, tdirection);
				/////////////\\\\\\\\\\\\\\\\
				
				//ELIAS ADDED THIS\\
				//controller.donePass();
				
				///////////////\\\\\\\\\\\\\\\\\\
				
				
				myGlass = null;
				
				
				//ELIAS COMMENTED THIS OUT \\
				
				
				
			}
			
			
		}
	 else   if(isDoneThree == true)
		{   status = "";
	        status = "ON, Reseting";
			plowdX = 0;
			plowdY = -1 * (int)Math.cos(Math.toRadians(rotation));
			plowXCoord += plowdX;
			plowYCoord -= plowdY;
			 //colorRect.setFrame(myGlass.getXCoord(), myGlass.getYCoord(), myGlass.getWidth(), myGlass.getHeight());
			if(plowYCoord <= plowbaseY)
			{   
				isDoneThree = false;
				isReceiving = false;
				isPainting = false;
				isDoneOne = false;
				isDoneTwo = false;
				dHeight = 0;
				status = "";
			    status = "OFF";
				
				
			}
		}
	  }
	}

	
	public void drawUnderGlass(Graphics g, ImageObserver c) {
		// TODO Auto-generated method stub
		Graphics2D g2 = (Graphics2D) g;
		int centerXCoord = baseXCoord + width / 2;
		int centerYCoord = baseYCoord + height / 2;
		//g2.translate(centerXCoord, centerYCoord);
		//g2.rotate(Math.toRadians(rotation));
		//g2.translate(-centerXCoord, -centerYCoord);
		g2.rotate(Math.toRadians(rotation), centerXCoord, centerYCoord);
		
		g2.drawImage(paintBase.getImage(),baseXCoord, baseYCoord, c);
		
		g2.drawImage(paintPlow.getImage(), plowXCoord, plowYCoord, c);
		g2.drawImage(paintMachineBase.getImage(), mbaseXCoord, mbaseYCoord, c);
		
		g2.rotate(-Math.toRadians(rotation), centerXCoord, centerYCoord);
		//g2.translate(-centerXCoord, -centerYCoord);
		//g2.rotate(-Math.toRadians(rotation));
	}
  
	
	public void drawOverGlass(Graphics g, ImageObserver c) {
		// TODO Auto-generated method stub
		Graphics2D g2 = (Graphics2D) g;
		int centerXCoord = baseXCoord + width / 2;
		int centerYCoord = baseYCoord + height / 2;
		//g2.translate(centerXCoord, centerYCoord);
		//g2.rotate(Math.toRadians(rotation));
		//g2.translate(-centerXCoord, -centerYCoord);
		g2.rotate(Math.toRadians(rotation), centerXCoord, centerYCoord);

		//if(isPainting == true )
		//{
		 //Graphics2D g2 = (Graphics2D) g;
		 //g2.setColor(Color.RED);
		/// g2.fill(colorRect);
		//}
		g2.drawImage(paintArm.getImage(), armXCoord + armdX, armYCoord + armdY,c);
		
		g2.rotate(-Math.toRadians(rotation), centerXCoord, centerYCoord);
		//g2.translate(-centerXCoord, -centerYCoord);
		//g2.rotate(-Math.toRadians(rotation));
	}

	
	public void receiveGlass(GuiGlass glass, int direction) {
		// TODO Auto-generated method stubglass = newGlass;
		
	    myGlass = glass;
		

		switch (direction) {
		    case FactoryPart.SOUTH:
			{
				this.direction  = FactoryPart.SOUTH;
				this.tdirection = FactoryPart.NORTH;
				dX = 0;
				dY = 1;
				//getPart(FactoryPart.SOUTH).finishPassing();
			}
			break;
		    case FactoryPart.EAST:
			{
				this.direction  = FactoryPart.EAST;
				this.tdirection = FactoryPart.WEST;
				dX = 1;
				dY = 0;
				//getPart(FactoryPart.EAST).finishPassing();
			} 
			break;
		    case FactoryPart.NORTH:
		    { 
			  this.direction  = FactoryPart.NORTH;
			  this.tdirection = FactoryPart.SOUTH;
			  dX = 0;
			  dY = 1;
			  //getPart(FactoryPart.NORTH).finishPassing();
			  
		    }
			break;
		    case FactoryPart.WEST:
		    {
		    this.direction  = FactoryPart.WEST;
		    this.tdirection = FactoryPart.EAST;
		    dX = 1;
			dY = 0;
			//getPart(FactoryPart.WEST).finishPassing();
		    }
			break;
		    default:
			// This shouldn't happen.
			break;
		}
	}

	
	

	
	public void setX(int x) {
		// TODO Auto-generated method stub
		baseXCoord = x;

    	mbaseXCoord = baseXCoord + initMachineXMargin;
    	//mbaseYCoord = baseYCoord + initMachineYMargin;
    	
    	plowXCoord = baseXCoord + initPlowXMargin;
    	//plowYCoord = baseYCoord + initPlowYMargin;
    	
    	plowbaseX = plowXCoord;
    	//plowbaseY = plowYCoord;
    	
    	armXCoord = mbaseXCoord + 3;
    	//armYCoord = plowYCoord + 40;
    	
    	armbaseX = armXCoord;
    	//armbaseY = armYCoord;
    	
    	
		
	}

	
	public void setY(int y) {
		// TODO Auto-generated method stub
		baseYCoord = y;

    	//mbaseXCoord = baseXCoord + initMachineXMargin;
    	mbaseYCoord = baseYCoord + initMachineYMargin;
    	
    	//plowXCoord = baseXCoord + initPlowXMargin;
    	plowYCoord = baseYCoord + initPlowYMargin;
    	
    	//plowbaseX = plowXCoord;
    	plowbaseY = plowYCoord;
    	
    	//armXCoord = mbaseXCoord + 3;
    	armYCoord = plowYCoord + 40;
    	
    	//armbaseX = armXCoord;
    	armbaseY = armYCoord;
    	
    	
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
	
	public void setController(StandAloneMachineController c)
	{
		controller = c;
	}
	public void setOpController(OperatorController c)
	{
		opcontroller = c;
	}

    public void playJob(String job_type)
    {
    	
    	//System.out.println("OK!");
    }
    public void playJob(String job_type, GlassDesign gd) {
		// TODO Auto-generated method stub
    	isPreparing = true;
    	paintSchematic = gd.getPaintSchematic();
	}
    public void playPass()
    {

    	//getPart(direction).receiveGlass(myGlass, tdirection);
    	this.setPlowAnimate();
    	
    	//getPart(direction).receiveGlass(myGlass, tdirection);
    }
    public void finishPassing() {
		// TODO Auto-generated method stub
		//controller.donePainting();
		//isPainting = false;
    	
    	//ELIAS COMMENTED THIS OUT
		//controller.donePass();
    	//////////////\\\\\\\\\\\\\\
	    

    	opcontroller.animationDone();
    	controller.donePass();
		
	}
    public void setPlowAnimate() {
		// TODO Auto-generated method stub
		isPushingBack   = true;
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

	public void giveGlassToOperator() {
        if(getPart(FactoryPart.NORTH) instanceof GuiOperator) {
            //System.out.println("[GuiGrinder]: Operator to the north");
            getPart(FactoryPart.NORTH).receiveGlass(myGlass, FactoryPart.SOUTH);
            myGlass = null;
            isDoneTwo = false;
            
        }
        else if(getPart(FactoryPart.EAST) instanceof GuiOperator) {
            //System.out.println("[GuiGrinder]: Operator to the east");
            getPart(FactoryPart.EAST).receiveGlass(myGlass, FactoryPart.WEST);
            myGlass = null;
            isDoneTwo = false;
        }
        else if(getPart(FactoryPart.SOUTH) instanceof GuiOperator) {
            //System.out.println("[GuiGrinder]: Operator to the south");
            getPart(FactoryPart.SOUTH).receiveGlass(myGlass, FactoryPart.NORTH);
            myGlass = null;
            isDoneTwo = false;
        }
        else if(getPart(FactoryPart.WEST) instanceof GuiOperator) {
            //System.out.println("[GuiGrinder]: Operator to the west");
            getPart(FactoryPart.WEST).receiveGlass(myGlass, FactoryPart.EAST);
            myGlass = null;
            isDoneTwo = false;
        }
        else {
            System.out.println("COULD NOT FIND OPERATOR");
        }
         
    	 dHeight =  0;
    	 
       
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
