package factory.gui;
//////////////////////////////////////////////////////////////
//Name: Hyung-Jin Kim
//File Name: GuiTruck.java
//
// A gui animation class that have the drawing methods of the truck
///////////////////////////////////////////////////////////////////////


//TODO: un-comment for integration 
//package factory.gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import factory.controller.TruckController;
import factory.interfaces.IGuiTruck;


public class GuiTruck  extends FactoryPart implements AnimatedPart, IGuiTruck{ 
	final private int speed=1;
	final static int WINDOWX=700;
	final static int WINDOWY=1500; 
	GuiGlass glass1;
	TruckController controller;
	ArrayList<GuiGlass> glasses;
	private int x; // the X coordinate of the truck 
	private int y;// the Y coordinate of  the truck
	private int originalX;
	private int originalY;
	private int originalAngle;
	private int width; // width of the truck 
	private int height;// height of the truck
	private int stWidth; // the standard truck's width 
	private int stHeight;// the standard truck's height
	private int glassLimitY;// the target position on y on the truck
	private int glassLimitX; // the target position on x on the truck
	ImageIcon truckIdle; // image of idle truck
	ImageIcon truckHover;// image of hover truck 
	ImageIcon truckBoost;// image of boosted truck
	private int degree;
	private int distance;
	BufferedImage bufHover; 
	BufferedImage bufBooest;
	private String direct;
	
	
	boolean broken,pasedGlass,doneReceiving,off,rotate,lowering,raising, moving ,done;// boolean expresstions
	private int countTime;
	
	private enum StarWars {NORMAL, ENTER_DS, FACE_DS, WINGS_OUT, LASERS, DESTROY_DS, WINGS_IN, BACK_TO_WORK};
	private StarWars nonNormState;
	private ImageIcon truckRWingImage, truckLWingImage, deathStarImage;
	private int deathStarXCoord, deathStarYCoord, deathStarWidth, deathStarHeight;
	private final int deathStarTargetXCoord = 600;
	private final int deathStarTargetYCoord = -50;
	private int truckRWingXCoord, truckRWingYCoord, truckLWingXCoord, truckLWingYCoord, truckWingWidth, truckWingHeight;
	private int laserCounter, boomCounter;
	private ArrayList<Rectangle2D.Double> lasers = new ArrayList<Rectangle2D.Double>();
	
	public GuiTruck (int a, int b, int angle){
		 x=a;
		 y=b;
		 originalX=x;
		 originalY=y;
		 degree=angle;
		 originalAngle=degree;
		 truckIdle= new ImageIcon("images/truck_idle.png");
		 truckHover = new ImageIcon("images/truck_hover.png");
		 truckBoost= new ImageIcon("images/truck_boosters.png");
		 broken = false;
		 pasedGlass=doneReceiving=rotate=raising=lowering=moving=done=false; //set to false as default 
		
		 nonNormState	= StarWars.NORMAL;
		 truckRWingImage = new ImageIcon("images/truck_wing_right.png");
		 truckLWingImage = new ImageIcon("images/truck_wing_left.png");
		 deathStarImage = new ImageIcon("images/death_star.gif");
		 laserCounter = boomCounter = 0;
		 //lasers	= new ArrayList<Rectangle2D.Double>();
		 deathStarXCoord = -500;
		 deathStarYCoord = -500;
		 deathStarWidth = 3*deathStarImage.getIconWidth();
		 deathStarHeight = 3*deathStarImage.getIconHeight();
		 truckWingWidth = truckRWingImage.getIconWidth();
		 truckWingHeight = truckRWingImage.getIconHeight();
			truckRWingXCoord = this.x;// + (this.stWidth-truckWingWidth);
			truckRWingYCoord = this.y;
			truckLWingXCoord = this.x;
			truckLWingYCoord = this.y;
		
		 off=true;
		 width = truckIdle.getIconWidth();
		 height = truckIdle.getIconHeight();
		 stWidth=truckIdle.getIconWidth();
		 stHeight=truckIdle.getIconHeight();
		  glasses = new ArrayList<GuiGlass>();
		   // sets the glassLimitx and LimitY to the appropriate degree values
		
		 	if (degree == 0){
				glassLimitX=x+48;
				glassLimitY=y+58;
			}
			if (degree == 90){
				glassLimitX= x+5;
				glassLimitY= y+30;
			}
			if (degree ==180){
				glassLimitX= x+20;
				glassLimitY= y+35;
				
			}
			if (degree ==270){
				glassLimitX= x+27;
				glassLimitY= y+20;
				
			}
	     
		 countTime=0;
		 distance=0;	
	}
	public void setX(int a){ // sets  X location to a 
    	x = a;
    }
    public void setY(int b){// sets y location to b 
    	y = b;
    }
    public int getX() { // returns the x location
	return x;
    }
    
    public int getY() { // returns the y location 
	return y;
    }
    public int getWidth(){ //get the width of the truck 
    	return width;
    }
    public int getHeight(){ // get the height of the truck 
    	return height;
    }
    public void setWidth(int a ){
    	width=a;
    }
    public void setHeight(int b){
    	height=b;
    }
    public void moveLaterally(int xDisplacement, int yDisplacement)
	{
		x = x + xDisplacement;
		y= y  + yDisplacement;
	}
	public void update() {
		// receiving the glass 
		// the degree determine the values
		
		
		
		if (raising)//&&!broken)
    	{	
			//System.out.println("!!!!!!!!!!!");
			
    		if(countTime<8){
    			moveVertically(1);
    			for (GuiGlass gl : glasses) {
				    gl.moveVertically(1);
				}
    			countTime++;
    			
    		}
    		if(countTime>=8)
    		{
    				if(broken)
    				{
    					nonNormState	= StarWars.ENTER_DS;
    					raising=false;
    				}
    				else
    				{
    					raising=true;
            			moving=true;
    				}
    		}
    		if (rotate)
    		{
    			raising=true;
    			moving=false;
    		}
    	}
		
		if (moving){
    		switch (degree){
    		case 0:	
    					if(y+height<0&&!pasedGlass)
    					{	
    						pasedGlass=true;
    						setRotation(180);
    						moving=true;
    						for (GuiGlass gl : glasses) {
    							gl.setTreatmentCompleted(true);
    						}
    						glasses.clear();
    				
    					}
    					
    					if(pasedGlass){
    						
    						moveLaterally(0,-speed);
    						distance--;
    								
    						if (distance==-2)
    						{
    						moving=false;	
    						raising=true;
    						rotate=true;
    						distance=0;
    						
    						}

    					}		
    					if(moving&&!pasedGlass){	
    						moveLaterally(0,-speed);
    						for (GuiGlass gl : glasses) {
    							gl.moveLaterally(NORTH,speed);
    						}
    						distance++;
    						lowering=false;	
    					}
    					 break;
    		case 90:	
						if(x>WINDOWX+16&&!pasedGlass)
						{	
							pasedGlass=true;
							setRotation(270);
							moving=true;
							for (GuiGlass gl : glasses) {
    							gl.setTreatmentCompleted(true);
    						}
							glasses.clear();
						}
				
						if(pasedGlass){
							moveLaterally(speed,0);
							distance--;
					
							if (distance==-2)
							{
								moving=false;	
								raising=true;
								rotate=true;
								distance=0;
							}

						}		

						if(moving&&!pasedGlass){
							moveLaterally(speed,0);
							for (GuiGlass gl : glasses) {
	    					    gl.moveLaterally(EAST,speed);
	    					}
							distance++;
							lowering=false;	
						}
						break;
				     			 
    		case 180:	
						if(y>WINDOWY&&!pasedGlass)
						{	
							pasedGlass=true;
							setRotation(0);
							moving=true;
							for (GuiGlass gl : glasses) {
    							gl.setTreatmentCompleted(true);
    						}
							glasses.clear();
						}
				
						if(pasedGlass){
							moveLaterally(0,speed);
							distance--;
					
						if (distance==-2)
						{
							moving=false;	
							raising=true;
							rotate=true;
							distance=0;
						}

						}		

						if(moving&&!pasedGlass){
							moveLaterally(0,speed);
							//System.out.println(glasses.size());
							for (GuiGlass gl : glasses) {
								if(gl !=null)
	    					    gl.moveLaterally(SOUTH,speed);
	    					}
							distance++;
							lowering=false;	
						}
						break;
    		
    		case 270:	
						if(x<-height+15&&!pasedGlass)
						{	
							pasedGlass=true;
							setRotation(90);
							moving=true;
							for (GuiGlass gl : glasses) {
    							gl.setTreatmentCompleted(true);
    						}
							glasses.clear();
						}
				
						if(pasedGlass){
							moveLaterally(-speed,0);
							distance--;
					
							if (distance==-2)
							{
								moving=false;	
								raising=true;
								rotate=true;
								distance=0;
							}
						}		
						if(moving&&!pasedGlass){
							moveLaterally(-speed,0);
							for (GuiGlass gl : glasses) {
	    					    gl.moveLaterally(WEST,speed);
	    					}
							distance++;
							lowering=false;	
						}
						break;     					 			 
    		}
		}

		if(rotate){
			switch (originalAngle){
			case 0:
			case 90:	
					if(degree>originalAngle){
					degree = degree-speed;
					setRotation(degree);
					}
					else 
					{
						rotate=false;
						raising=false;
						lowering=true;
					}
				break;
			case 180:
			case 270:	
				if(degree<originalAngle){
				degree = degree+speed;
				setRotation(degree);
				}
				else 
				{
					rotate=false;
					raising=false;
					lowering=true;
				}
				break;
			
			}
			
		}
		if(lowering){
			if(countTime>0){
    			moveVertically(-1);
    			countTime--;
    			
    			}
    		else{ 
    			
    			lowering=false;
    			done=true;
    			pasedGlass=false;
    			done=true;
    			}		
			
		}
		
		if(done){
			finishTransport();
			glasses.clear();
			doTurnOff();
			off=true;
			done=false;
			
		}
		if(doneReceiving){
			
			finishReceiving();
			doneReceiving=false;
			
		}
		
		if(nonNormState != StarWars.NORMAL)
		{
			if(nonNormState == StarWars.ENTER_DS)
			{
				if(deathStarXCoord < deathStarTargetXCoord)
				{
					deathStarXCoord += 5;
				}
				if(deathStarYCoord < deathStarTargetYCoord)
				{
					deathStarYCoord += 5;
				}
				if((deathStarXCoord >= deathStarTargetXCoord) && (deathStarYCoord >= deathStarTargetYCoord))
				{
					nonNormState = StarWars.FACE_DS;
				}
			}
			else if(nonNormState == StarWars.FACE_DS)
			{
				if(this.degree != 0)
				{
					this.degree	= degree -1;
					for(GuiGlass aGlass : glasses)
					{
						aGlass.setAngle(aGlass.getAngle()-1);
					}
				}
				if(truckRWingXCoord < this.x + this.stWidth + 15)
				{
					truckRWingXCoord += 2;
				}
				if(truckLWingXCoord > this.x - truckWingWidth)
				{
					truckLWingXCoord -= 2;
				}
				if(this.degree == 0 && (truckRWingXCoord >= this.x + this.stWidth) && (truckLWingXCoord <= (this.x - truckWingWidth)) )
				{
					nonNormState = StarWars.LASERS;
				}
			}
			else if(nonNormState == StarWars.WINGS_OUT)
			{/*
				if(truckRWingXCoord < this.x + this.width)
				{
					truckRWingXCoord += 3;
				}
				if(truckLWingXCoord > this.x - truckWingWidth)
				{
					truckRWingXCoord -= 3;
				}
			*/}
			else if(nonNormState == StarWars.LASERS)
			{
				int	newLaserX = 0;
				
				if(laserCounter < 150)
				{
					if(laserCounter%7 == 0)
					{
						if(laserCounter%2 == 0)
						{
							newLaserX = truckLWingXCoord + truckWingWidth/3;
						}
						else
						{
							newLaserX = truckRWingXCoord + 2*truckWingWidth/3;
						}
						//Rectangle2D.Double() = new Rectangle2D.Double()
						lasers.add(new Rectangle2D.Double(newLaserX, this.y, 5, 37));
					}
					
					for(int i = lasers.size()-1; i >= 0; i--)
					{
						if(lasers.size() > 0)
						{
							Rectangle2D.Double	pew = lasers.get(i);
						
							pew.setFrame(pew.getX(), pew.getY()-5, pew.getWidth(), pew.getHeight());
							if(pew.getY() <= deathStarYCoord + (deathStarHeight/2))
							{
								lasers.remove(pew);
							}
						}
					}
					
					laserCounter++;
				}
				else
				{
					for(int i = lasers.size()-1; i >= 0; i--)
					{
						if(lasers.size() > 0)
						{
							Rectangle2D.Double	pew = lasers.get(i);
						
							lasers.remove(pew);
						}
					}
					nonNormState	= StarWars.DESTROY_DS;
				}
				
			}
			else if(nonNormState == StarWars.DESTROY_DS)
			{
				if(boomCounter < 15)
				{
					deathStarImage	= new ImageIcon("images/death_star_boom.png");
				}
				else
				{
					deathStarImage	= new ImageIcon("images/nonexistentimage.png");
					nonNormState	= StarWars.WINGS_IN;
				}				
				boomCounter++;
			}
			else if(nonNormState == StarWars.WINGS_IN)
			{
				if(this.degree != 180)
				{
					degree++;
					for(GuiGlass aGlass : glasses)
					{
						aGlass.setAngle(aGlass.getAngle()+1);
					}
				}
				if(truckRWingXCoord < this.x + this.stWidth)
				{
					truckRWingXCoord += 2;
				}
				if(truckLWingXCoord > this.x - truckWingWidth)
				{
					truckRWingXCoord -= 2;
				}
				if(this.degree == 180 && (truckRWingXCoord >= this.x + this.stWidth) && (truckLWingXCoord <= (this.x - truckWingWidth)) )
				{
					nonNormState = StarWars.BACK_TO_WORK;
				}
			}
			else if(nonNormState == StarWars.BACK_TO_WORK)
			{
				raising=true;
    			moving=true;
    			nonNormState = StarWars.NORMAL;
    			broken = false;
			}
		}
		
	}

	// draw the animation 
 public void draw(Graphics g, Component c){
    	 
    	 Graphics2D g2 = (Graphics2D)g;
    	//Rotate the Graphics2D object so that the station will be drawn at it's angle.
		double	centerX	= (x + (width/2));				
		double	centerY	= (y + (height/2));				
		
		g2.rotate(Math.toRadians(degree), centerX, centerY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		//g2.drawImage(truckIdle.getImage(),x,y,c);
		
		if(nonNormState != StarWars.NORMAL)
		 {	
			g2.drawImage(truckRWingImage.getImage(), truckRWingXCoord, truckRWingYCoord, truckWingWidth, truckWingHeight, null);
			g2.drawImage(truckLWingImage.getImage(), truckLWingXCoord, truckLWingYCoord, truckWingWidth, truckWingHeight, null);
			g2.drawImage(truckHover.getImage(),x, y, width, height, null);
		 }
		 
		//image of the truck
		else if(raising&&!moving){
			//g2.rotate(Math.toRadians(degree), centerX, centerY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
			//BufferedImage bi1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			//bi1.getGraphics().drawImage(truckHover.getImage(),0,0,width,height,null);
			g2.drawImage(truckHover.getImage(),x, y, width, height, null);
			//g2.rotate(Math.toRadians(-1*degree), centerX, centerY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		}
		// image of the boostered truck 
		else if(moving){
			//g2.rotate(Math.toRadians(degree), centerX, centerY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		    //BufferedImage bi1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		    //bi1.getGraphics().drawImage(truckBoost.getImage(),0,0,width,height,null);
		    g2.drawImage(truckBoost.getImage(),x, y, width, height, null);
    	    //g2.rotate(Math.toRadians(-1*degree), centerX, centerY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		 }
		// the image of the hover truck  when it is scaling up 
		else if(lowering){
			//g2.rotate(Math.toRadians(degree), centerX, centerY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		    //BufferedImage bi1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	        //bi1.getGraphics().drawImage(truckHover.getImage(),0,0,width,height,null);
	    	g2.drawImage(truckHover.getImage(),x, y, width, height, null);
	    	//g2.rotate(Math.toRadians(-1*degree),centerX, centerY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		 }
		else
		{
			g2.drawImage(truckIdle.getImage(),x,y,c);
		}
		
		g2.rotate(Math.toRadians(-1*degree), centerX, centerY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		 
 }
 
 
 // making the image scale up like it is going up 
 public void moveVertically(int zDisplacement)
 {
     double stdDimensionRatio = (double)stWidth/(double)stHeight;
             
     setWidth((width + 2*zDisplacement));
     setHeight((int)(width/stdDimensionRatio));
     x= x - zDisplacement;
     y= y - zDisplacement;
     /*
     truckWingWidth = ((truckWingWidth + 2*zDisplacement));
     truckWingHeight = ((int)(truckWingWidth/stdDimensionRatio));
     truckRWingXCoord = truckRWingXCoord - zDisplacement;
     truckRWingYCoord = truckRWingYCoord - zDisplacement;
     truckLWingXCoord = truckRWingXCoord - zDisplacement;
     truckLWingYCoord = truckRWingYCoord - zDisplacement;
     */
 }
 
	//draws the under glass 
 	public void drawUnderGlass(Graphics g, ImageObserver c) {
		 Component c1= (Component)c;
		   draw(g,c1);
	}

	@Override
	
	public void drawOverGlass(Graphics g, ImageObserver c) 
	{
		Graphics2D g2 = (Graphics2D)g;
		if(nonNormState != StarWars.NORMAL)
		{
			g2.drawImage(deathStarImage.getImage(), deathStarXCoord, deathStarYCoord, deathStarWidth, deathStarHeight, null);
			
			g2.setColor(Color.orange);
			for(Rectangle2D.Double pewpew : lasers)
			{
				g2.fill(pewpew);
			}
		}		
	}

	
	
	
	// it is called by the factory part that had the glass priviously 
	public void receiveGlass(GuiGlass glass, int direction) {
		glass1=glass;
		glasses.add(glass);
		
		
		if(direction == EAST){
			
			adjacentParts[FactoryPart.EAST].finishPassing();
			
		}
		if(direction == WEST){
			
			adjacentParts[FactoryPart.WEST].finishPassing();
			
		}
		if(direction == SOUTH){
			
			adjacentParts[FactoryPart.SOUTH].finishPassing();
			
		}
		if(direction == NORTH){
			
			adjacentParts[FactoryPart.NORTH].finishPassing();
			
		}
		
		
		//receiving=true;
		//doneReceiving=false;
		// raising=true;
		
		
	}
	//TODO: it will be used to tell the agent that gui is done for the truk 
	
	public void finishTransport(){
		controller.doneTransportGlass();
		
	}
	public void finishPassing() {
		// TODO Auto-generated method stub
		
	}
	// it is called when the receiving glass animation is done. 
	public void finishReceiving(){
		 controller.donePickUpGlass();
		
	}
	

	@Override
	public int getRotation() {
		return degree;
	}

	@Override
	public void setRotation(int d) {
		//  TODO Auto-generated method stub
		degree=d;
		if (done){
			if (degree == 0){
				glassLimitX=x+48;
				glassLimitY=y+58;
			}
			if (degree == 90){
				glassLimitX= x+5;
				glassLimitY= y+30;
			}
			if (degree ==180){
				glassLimitX= x+20;
				glassLimitY= y+40;
				
			}
			if (degree ==270){
				glassLimitX= x+27;
				glassLimitY= y+20;
			}	
		  }

	} 
	 public void doTurnOn(){
		   
		   moving=false;
		   done=false;
		   raising=false;
		   pasedGlass=false;
		   lowering=false;
		   doneReceiving=false;
	   }
	 public void doTurnOff(){
		 pasedGlass=doneReceiving=rotate=raising=lowering=moving=done=false;
	 }
	 public void recevie(){
		
		 doneReceiving=true;
	 }
	 public void raise(){
		 raising=true;
	 }
	 int i = 1;
	@Override
	public void playPickUpGlass() {
		// TODO Auto-generated method stub
		System.out.println(i + "-[GuiTruck]: Recieved playPickUpGlass");
		 recevie();
		// finishReceiving();
	}
	@Override
	public void playTransportGlass() {
		// TODO Auto-generated method stub
		raise();
	}
	public void setController(TruckController controller) {
		// TODO Auto-generated method stub
		this.controller = controller;
	}
	public String getStatus(){
		 
		   if(moving)
			  return "ON, Passing";
		   if(lowering)
			  return "ON, Lowering ";
	       if (raising)
	    	  return"ON, Raising ";
	        if(done)
	    	   return "ON, Done";
		   else
	       return"OFF";
	  }
	 public boolean hasGlass(){
		  	if(off)
			 return false;
		 return true;
	 }
	 public void setBreak(boolean flag) { 
		 broken=flag;
	 }
	 public boolean isBroken(){
		 return broken;
	 }

}
