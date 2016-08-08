package factory.gui;
//////////////////////////////////////////////////////////////
//Name: Hyung-Jin Kim
//File Name: GuiTransfer.java
//
// A gui animation class that have the drawing methods of the transfer
///////////////////////////////////////////////////////////////////////


//TODO: un-comment for integration 
//package factory.gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
//TODO: un-comment for integration 
import factory.controller.TransferController;


public class GuiTransfer extends FactoryPart implements AnimatedPart{ 
	
	final private int speed=1; // the speed of the glass movement 
	
	GuiGlass glass1; 
	private int x; // the X coordinate of the transfer 
	private int y;// the Y coordinate of  the transfer 
	private int beltX; // the top belt x location 
	private int beltY; // the top belt y location
	private int countTime=0; // the counter for the lifting the belts
	private int width; // width of transfer
	private int height; // width of height 
	private int beltOneX; // the X coordinate of top belt
	private int beltOneY; // the Y  coordinate of top belt  
	private int glassLimitX; // the X position of glass where the conveyor should stop
	private int glassLimitY; // the Y position where the glass should come to on the Transfer image  in order to move  
	private int stBeltWidth; // the standard Belt's width 
	private int stBeltHeight;// the standard Belt's height
	private int beltWidth; // the belts Height 
	private int beltHeight;// the belts Height
	private int lineX; //the X location of the first line on the belt
	private int sensorX;//X coordinate of the sensor 
	private int sensorY; // Y coordinate of the seonsor 
	private int degree ;// rotation angle
	ImageIcon transferBase; // image of base of the transfer
	ImageIcon transferLiftBelt1;// image of the conveyer belt 
	ImageIcon transferLiftBelt2;
	private int brokenCount;// the counter for the broken transfer 
	BufferedImage liftBeltImage; // buffered Image used for the lift image
	Rectangle beltLine= new Rectangle(2,16) ;
	
	Rectangle2D.Double beltBack;
	Ellipse2D.Double sensorLED ;// sensor LED
	boolean broken,pasedGlass, lowering,raising, moving ,receiving, done, sensorON,off;// boolean expresstions 
	
	private int countTime1,glassCount;
	
	TransferController controller;
	
	
	public GuiTransfer (int a, int b, int angle){
		transferBase = new ImageIcon("images/transfer_base_square.png");
		transferLiftBelt1 = new ImageIcon("images/transfer_liftbelt.png");
		transferLiftBelt2=  new ImageIcon("images/transfer_liftbelt.png");
		x=a;// 50 default X position of transfer  
		y=b; // 50 default Y position of transfer 
		degree=angle;
		beltX=x+18;  
		beltY=y+27;
		beltOneX=beltX; // 
		beltOneY=beltY-1;
		//beltTwoX=beltX;
		//beltTwoY=beltY+39;
		stBeltWidth=transferLiftBelt1.getIconWidth();
		stBeltHeight= transferLiftBelt1.getIconHeight();
		width = transferBase.getIconWidth();
		height = transferBase.getIconHeight();
		brokenCount=0;
		glassCount=0;

		if (degree == 0){
			glassLimitX= x+stBeltWidth/2;
			glassLimitY=beltY;// the Y position where the glass want to move up
			
		}
		if (degree == 90){
			glassLimitX= x+width*2/10;
			glassLimitY= beltY;
		}
		if (degree ==180){
			glassLimitX= x+width/5;
			glassLimitY= beltY;
			
		}
		if (degree ==270){
			glassLimitX= x+width*3/10;
			glassLimitY= beltY;
			
		}
		beltWidth = stBeltWidth;
		beltHeight=stBeltHeight;
		sensorX=x+57;
		sensorY=y+57;	
		lineX=0; // the X position of the line on the buffered image 
		broken=pasedGlass=lowering=raising=moving=receiving=done=sensorON=false; //set to false as default 
		countTime1=0;
		countTime=0;
		off=true;
		
		
	}
	
public void setup(int x,int y, int angle)
{
	setX(x);
	setY(y);
	setRotation(angle);
	pasedGlass=lowering=raising=moving=receiving=done=sensorON=false; //set to false as default 
	countTime1=0;
	countTime=0;
	off=true;
	
}
	
	public void setX(int a){ // sets  X location to a 
    	x = a;
    	beltX=a+18;
    	beltOneX=beltX;
    	sensorX=a+57;
			
    }
    public void setY(int b){// sets y location to b 
    	y = b;
    	beltY=b+27;
    	beltOneY=beltY-1;
    	sensorY=b+57;
    }
    public int getX() { // returns the x location
	return x;
    }
    
    public int getY() { // returns the y location 
	return y;
    }
    public int getBeltX(){ // returns the the X location of the top conveyer  
    return beltX;
    }
    public int getBeltY(){ //returns the the Y location of the top conveyer 
    return beltY;
    }
    public void setBeltX(int x1){ // sets the X location of the top conveyer to X1
    	beltX=x1;
    }
    public void setBeltY(int y1){// sets the Y location of the top conveyer to Y1
    	beltY=y1;
    }
    public void setWidth(int w){ //set the width of the transfer
     width = w;
    }
    public void setHeight(int h){ // set the height of the transfer
     height = h;
    }
    public void setBeltWidth(int w1){ //set the width of the transfer
        beltWidth = w1;
       }
    public void setBeltHeight(int h1){ // set the height of the transfer
    	beltHeight = h1;
       }
    public int getWidth(){ //get the width of the transfer 
    	return width;
    }
    public int getHeight(){ // get the height of the transfer 
    	return height;
    }
   
   
    public void update(){
    	// moving the glass and the conveyer in O degrees rotation 
    	//receiving the glass from the bottom of the transfer

    	if(receiving){
    		
    			off=false;
    		switch (degree){
    		case 0 : 
    	
    			if (glass1.getYCoord() > glassLimitY){
							glass1.moveLaterally(NORTH, speed);
								//glass1.update();	
						}
						else{
							receiving=false;
							 //adjacentParts[FactoryPart.SOUTH].finishPassing();
							 doneReceiveGlass();
						}	
    				 
    					break;
    		case 90 :			

    				if (glass1.getXCoord() < glassLimitX){
    					glass1.moveLaterally(EAST,speed);
    				}
    				else 
    				{
    					receiving=false;
    					 //adjacentParts[FactoryPart.WEST].finishPassing();
    					//raising= true;
    					 doneReceiveGlass();
    				}
    				break;
    		case 180 :		
    					//if(glass1 !=null)
						if (glass1.getYCoord() < glassLimitY){
							glass1.moveLaterally(SOUTH,speed);
						}
						else 
						{
							receiving=false;
							// adjacentParts[FactoryPart.NORTH].finishPassing();
							//raising=true;
							 doneReceiveGlass();
						}
						break;
    		case 270 :	
    			//if(glass1 !=null)
				if (glass1.getXCoord() > glassLimitX){
					glass1.moveLaterally(WEST,speed);
				}
				else 
				{
					receiving=false;
					// adjacentParts[FactoryPart.EAST].finishPassing();
					//raising= true;
					 doneReceiveGlass();
				}
				break;	
    		}
    		
    	}
/*
 *  the glass to the factory part
 */
    		if (moving){
    			off=false;
	    		switch (degree){
	    		case 0:
	    				if(!broken){
	    				if(lineX<12){  //each line moves up to 8 displacement
	    					lineX = lineX+1;
	    					if(!pasedGlass){
	    						glass1.moveLaterally(EAST,speed);
	    					}
	    					//glass1.update();	
	    					sensorON=true;
	    					if(!pasedGlass && glass1.getXCoord()>=x+width-62)
	    					{
	    						if (degree==0){
	    							adjacentParts[FactoryPart.EAST].receiveGlass( glass1, FactoryPart.WEST);
	    							System.out.println("calling recglass degree0");
	    							pasedGlass=true;
	    						}	
	    					}
	    				}
	    				
	    				else{
	    					lineX=0;	
	    					moving=true;
	    					lowering=false;	
	    				}	
	    				if(moving && glass1.getXCoord() >= glassLimitX+50){
	    					lowering=true;
	    					moving=false;
	    				}
	    				}
	    				// broken case 
	    				else{
	    					   
	    					 if(lineX>-1&&brokenCount<80){  
	 	    					lineX = lineX-1;
	 	    					brokenCount++;
	 	    					
	 	    					if(!pasedGlass){
	 	    						glass1.moveLaterally(WEST,speed);
	 	    					}
	 	    					sensorON=true;
	 	    					
	    					  }
	    					 if (brokenCount==80){
	 	    						if (glassCount<2){
	 	    							
	 	    							glassCount++;
	 	    							glass1.moveVertically(-1);
	 	    						}
	 	    						else{
	 	    							pasedGlass=true;
	 	    							glass1.breakGlass();
	 	    							glass1.setTreatmentCompleted(true);
	 	    							glass1=null;
	 	    							controller.doneBreaking();
	 	    							brokenCount=0;
	 	    							glassCount=0;
	 	    						}
	 	    					}
	 	    				else{
	 		    					lineX=11;	
	 		    					moving=true;
	 		    					lowering=false;	
	 		    				}
	    				}
	    				break;
	    		case 90:
	    			if(!broken){
	    			if(lineX<12){  //each line moves up to 8 displacement
    					lineX = lineX+1;
    					if(!pasedGlass){
    						glass1.moveLaterally(SOUTH,speed);
    					}
    						
    					sensorON=true;
    					if(!pasedGlass && glass1.getYCoord()>=y+height/10)
    					{	
    						if(degree==90){
    							adjacentParts[FactoryPart.SOUTH].receiveGlass( glass1, FactoryPart.SOUTH);
    							System.out.println("call recGlass");
    							pasedGlass=true;
    						}
    					}
    				}
	    			else{
    					lineX=0;	
    					moving=true;
    					lowering=false;	
    				}	
    				if(moving && glass1.getYCoord() >= glassLimitY+50){
    					lowering=true;
    					moving=false;
    				}
	    		}
	    			// broken case 
    				else{
    					   
    					 if(lineX>-1&&brokenCount<80){  
 	    					lineX = lineX-1;
 	    					brokenCount++;
 	    					
 	    					if(!pasedGlass){
 	    						glass1.moveLaterally(NORTH,speed);
 	    					}
 	    					sensorON=true;
 	    					
    					  }
    					 if (brokenCount==80){
 	    						if (glassCount<4){
 	    							glassCount++;
 	    							glass1.moveVertically(-1);
 	    						}
 	    						else{
 	    							pasedGlass=true;
 	    							glass1.breakGlass();
 	    							glass1.setTreatmentCompleted(true);
 	    							glass1=null;
 	    							controller.doneBreaking();
 	    							brokenCount=0;
 	    							glassCount=0;
 	    						}
 	    					}
 	    				else{
 		    					lineX=11;	
 		    					moving=true;
 		    					lowering=false;	
 		    				}
    				}
	    			break;
    				
	    		case 180:
	    			if(!broken){
	    			if(lineX<12){  //each line moves up to 8 displacement
    					lineX = lineX+1;
    					if(!pasedGlass){
    						glass1.moveLaterally(WEST,speed);
    					}
    						
    					sensorON=true;
    					if(!pasedGlass && glass1.getXCoord()<=x+8)
    					{	
    						if(degree==180){
    							adjacentParts[FactoryPart.WEST].receiveGlass( glass1, FactoryPart.EAST);
    							System.out.println("call recGlass");
    							
    							pasedGlass=true;
    						}
    					}
    				}
	    			else{
    					lineX=0;
    					moving=true;
    					lowering=false;	
    				}	
    				if(moving && glass1.getXCoord() <= glassLimitX-50){
    					lowering=true;
    					moving=false;
    				}
	    			}
	    			//broken case 
	    			else{
 					   
   					 if(lineX>-1&&brokenCount<80){  
	    					lineX = lineX-1;
	    					brokenCount++;
	    					
	    					if(!pasedGlass){
	    						glass1.moveLaterally(EAST,speed);
	    					}
	    					sensorON=true;
	    					
   					  }
   					 if (brokenCount==80){
	    						if (glassCount<4){
	    							glassCount++;
	    							glass1.moveVertically(-1);
	    						}
	    						else{
	    							pasedGlass=true;
	    							glass1.breakGlass();
	    							glass1.setTreatmentCompleted(true);
	    							glass1=null;
	    							controller.doneBreaking();
	    							brokenCount=0;
 	    							glassCount=0;
	    							}
	    					}
	    				else{
		    					lineX=11;	
		    					moving=true;
		    					lowering=false;	
		    				}
   				}
    				break;
	    
	    		case 270:
	    			if(!broken){
	    			if(lineX<12){  //each line moves up to 8 displacement
    					lineX = lineX+1;
    					if(!pasedGlass){
    						glass1.moveLaterally(NORTH,speed);
    					}
    						
    					sensorON=true;
    					if(!pasedGlass && glass1.getYCoord()<=y+8)
    					{	
    						if(degree==270){
    							adjacentParts[FactoryPart.NORTH].receiveGlass( glass1, FactoryPart.SOUTH);
    							System.out.println("call recGlass");
    							
    							pasedGlass=true;
    						}
    					}
    				}
	    			else{
    					lineX=0;
    					moving=true;
    					lowering=false;	
    				}	
    				if(moving && glass1.getYCoord() <= glassLimitY-50){
    					lowering=true;
    					moving=false;
    				}
	    			}

	    			//broken case 
	    			else{
 					   
   					 if(lineX>-1&&brokenCount<80){  
	    					lineX = lineX-1;
	    					brokenCount++;
	    					
	    					if(!pasedGlass){
	    						glass1.moveLaterally(SOUTH,speed);
	    					}
	    					sensorON=true;
	    					
   					  }
   					 if (brokenCount==80){
	    						if (glassCount<4){
	    							glassCount++;
	    							glass1.moveVertically(-1);
	    						}
	    						else{
	    							pasedGlass=true;
	    							glass1.breakGlass();
	    							glass1.setTreatmentCompleted(true);
	    							glass1=null;
	    							controller.doneBreaking();
	    							brokenCount=0;
 	    							glassCount=0;
	    							}
	    					}
	    				else{
		    					lineX=11;	
		    					moving=true;
		    					lowering=false;	
		    				}
   				}
    				break;
    	
	    		}	
    		}
	    	
	    	
    		// lowering the conveyer on the transfer 
    		if(lowering){
	    		sensorON=false;
	    		if(countTime>0){
	    			moveVertically(-1);
	    			countTime=countTime-1;
	    			
	    			}
	    		else{ 
	    			
	    			lowering=false;
	    			done=true;
	    			pasedGlass=false;
	    			}		
				
	    	}
	    	
	    	//raising  after raising the conveyer moves the glass
	    	if (raising)
	    	{	
	    		sensorON=true;
	    		if(countTime<2){
	    			moveVertically(1);
	    			countTime++;
	    			
	    		}
	    		else{ 
	    			raising=false;
	    			moving=true;
	    			}
	    	}
	    	
	    	if(done){
	    		//finishPassing();
	    		doTurnOff();
	    		off=true;
	    	}
	}	
    
    
    // draws the sensor 
    public void drawSensor(Graphics g,Component c){
    	Graphics2D g2 = (Graphics2D)g;
    	if(sensorON){
    	g2.setColor(Color.GREEN);
    	}
    	else
    	{
    		g2.setColor(Color.RED);	
    	}
    	sensorLED= new Ellipse2D.Double(sensorX,sensorY,5,5); 
    	g2.fill(sensorLED);
    }

    public void draw(Graphics g, Component c){
    	 
    	 Graphics2D g2 = (Graphics2D)g;
    	//Rotate the Graphics2D object so that the station will be drawn at it's angle.
		double	centerX	= (x + (width/2));				
		double	centerY	= (y + (height/2));				
		g2.rotate(Math.toRadians(degree), centerX, centerY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		g2.drawImage(transferBase.getImage(),x,y,c);
    	
    	
    	//making a buffered image of the belt 
    	 
    	 BufferedImage bi1 = new BufferedImage(beltWidth, beltHeight, BufferedImage.TYPE_INT_ARGB);
       	 // draws the belt first 
    	 bi1.getGraphics().drawImage(transferLiftBelt1.getImage(),0,0,beltWidth,beltHeight,null);
    	 BufferedImage bi2 = new BufferedImage(beltWidth, beltHeight, BufferedImage.TYPE_INT_ARGB);
    	 bi2.getGraphics().drawImage(transferLiftBelt2.getImage(),0,0,beltWidth,beltHeight,null);
    	 
    	 // draw the belt line on the buffered image
    	 drawBeltLines(bi1.getGraphics(),c);
    	 drawBeltLines(bi2.getGraphics(),c);
    	 // draw the increased image 
    	 g2.drawImage(bi1,beltOneX, beltOneY, beltWidth, beltHeight, null);
    	 g2.drawImage(bi2,beltOneX, beltOneY+40, beltWidth, beltHeight, null);
    	 drawSensor(g,c);
    	 g2.rotate(Math.toRadians(-1*degree), centerX, centerY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
 		 
    }
    public void passingDone(){
    	  controller.doneTransferGlass();
    }
    public void playPass(){
    	raising= true;
    	System.out.print("XXX");
    	
    }
  // Drawing the belt line on the conveyer 
  // this method gives the effect of belt moving
    private void drawBeltLines(Graphics g,Component c) {
    	
    	Graphics2D g2 = (Graphics2D)g;
    	g2.setColor(new Color(13, 13, 13));
    	for(int i=0; i<7;i++){
    		
    	beltLine.setLocation(lineX+i*13,5);
    	g2.fill(beltLine);	
    	beltLine.setLocation(lineX+i*9,beltY+39+6);
    	g2.fill(beltLine);	
    	}
    }
    // making the image scale up like it is going up 
    public void moveVertically(int zDisplacement)
    {
        double stdDimensionRatio = (double)stBeltWidth/(double)stBeltHeight;
                
        this.setBeltWidth((beltWidth + 2*zDisplacement));
        this.setBeltHeight((int)(beltWidth/stdDimensionRatio));
        beltOneX= beltOneX - zDisplacement;
        beltOneY= beltOneY - zDisplacement;
    }
// drawing the components under the glass
   public void drawUnderGlass(Graphics g, ImageObserver c){
	   Component c1= (Component)c;
	   draw(g,c1);
   }
   // drawing the components over the glass
   public void drawOverGlass(Graphics g, ImageObserver c){
	   
   }
   
   // receiving glass from the other factory parts 
   int i = 1;
   public void doneReceiveGlass(){
	   controller.readyToPass();
   }
   public void receiveGlass(GuiGlass glass, int direction){
	   glass1=glass;
	   i++;
	   System.out.println("[GuiTransfer] receiveGlass: " + i);
	   receiving=true;
	   off=false;
	   
	   // DO NOT CHANGE THIS THIS MUST BE HERE
	   // HARMAN DID THIS
	   switch(degree) { 
	   case 0:
		   adjacentParts[FactoryPart.SOUTH].finishPassing();
		   break;
	   case 90:
		   adjacentParts[FactoryPart.WEST].finishPassing();
		   break;
	   case 180:
		   adjacentParts[FactoryPart.NORTH].finishPassing();
		   break;
	   case 270:
		   adjacentParts[FactoryPart.EAST].finishPassing();
		   break;
	   }
	   
	   
	   
   }
   
   public void finishPassing() {
	 //TODO: un-comment for integration 
	 System.out.println("--------------------[GuiTransfer] finishPassing--------------------------------");
	 passingDone();
      receiving=false;
	  lowering = true;
      glass1 = null;

   }
   public int getRotation() {
		return degree;
		}
   
    public void setRotation(int d) {
		   degree=d;
			if (degree == 0){
				glassLimitX= x+stBeltWidth/2;
				glassLimitY=beltY;// the Y position where the glass want to move up
				
			}
			if (degree == 90){
				glassLimitX= x+width*2/10;
				glassLimitY= beltY;
			}
			if (degree ==180){
				glassLimitX= x+width/5;
				glassLimitY= beltY;
				
			}
			if (degree ==270){
				glassLimitX= x+width*2/10;
				glassLimitY= beltY;
				
			}
		   
	   }
   // TurnOn the gui 
   public void doTurnOn(){

	   moving=false;
	   done=false;
	   lowering=false;
	   raising=false;
	   sensorON=false;
	   pasedGlass=false;
	   off=false;
	   
   }
   public void doTurnOff(){
	   receiving=false;
	   moving=false;
	   done=false;
	   lowering=false;
	   raising=false;
	   sensorON=false;
	   pasedGlass=false;
	   off=true;
	   //broken=false;
   }
   public void setController(TransferController controller) { 
	   this.controller = controller;
   }
  public String getStatus(){
	  if (receiving)
		  return "ON, Receiving";
	  else if(moving)
		  return "ON, Passing";
	  else if(lowering)
		  return "ON, Lowering Belt"; 
	  else  if (raising)
    	  return"ON, Raising Belt";
       else
		return"OFF";

  }
 public boolean hasGlass(){
	  	if(off)
		 return false;
	 return true;
 }
 public void setDisabled(boolean x){
 	broken=x;
 	
 	brokenCount=0;
 }
 
 public void setBreak(boolean flag) { 
	 controller.setDisabled(flag);
 }
 
 public boolean isBroken() { return broken;}
 
}

