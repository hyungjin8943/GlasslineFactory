package factory.gui;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import factory.controller.OperatorController;
import factory.controller.StandAloneMachineController;
import factory.interfaces.MachineControllerInteractor;
import factory.interfaces.OperatorControllerInteractor;
import factory.panels.GlassDesign;

/**
 * CSCI 200 Factory Project
 * Version: v2
 * 
 * GuiCrossSeamer class
 * 
 * Stand alone machine cross seamer goes around the edge of the glass and cross seams it.
 * 
 * @author Elias Shanaa
 * @version v2
 *
 */

public class GuiCrossSeamer extends FactoryPart implements AnimatedPart, MachineControllerInteractor, OperatorControllerInteractor {
	final String baseURL = "images/";
	
	GuiGlass myGlass;						//GuiGlass reference stored globally
	int	glassDirection;						//GuiGlass direction
	int dir;								//Opposite of GuiGlass direction
	
	//ImageIcons for each station part
	ImageIcon base = new ImageIcon(baseURL + "station_base.png");
	ImageIcon machineBase = new ImageIcon(baseURL + "station_machine_base.png");
	ImageIcon armBase = new ImageIcon(baseURL + "station_machine_arm_base.png");
	ImageIcon extenderArm = new ImageIcon(baseURL + "station_machine_extenderarm.png");
	ImageIcon liftArm = new ImageIcon(baseURL + "station_machine_liftarm.png");
	ImageIcon endOfArm = new ImageIcon(baseURL + "station_machine_endofarm.png");
	ImageIcon actuatorIdle = new ImageIcon(baseURL + "crossseamer_actuator_idle.png");
	ImageIcon actuatorActive1 = new ImageIcon(baseURL + "crossseamer_actuator_active1.png");
	ImageIcon actuatorActive2 = new ImageIcon(baseURL + "crossseamer_actuator_active2.png");
	ImageIcon actuatorActive3 = new ImageIcon(baseURL + "crossseamer_actuator_active3.png");
	ImageIcon plow = new ImageIcon(baseURL + "station_machine_plow.png");
	
	//ArrayList of the actuator images. Used to cycle between them when "cross seaming"
	ArrayList<ImageIcon> actuator = new ArrayList<ImageIcon>();
	
	//ArrayList of target points used in crossSeamAt() and goTo(). Edge of the glass
	ArrayList<Point2D.Double> myTargetPoints = new ArrayList<Point2D.Double>();
	
	boolean idle = true;				//true while idle
	boolean cross = false;				//true while cross seaming
	boolean plowAnimate = false;		//true while the plow is animating
	boolean plowBack = false;			//true while the plow is moving back
	boolean broken = false;				//true while the station is broken
	boolean isReceivingGlass = false;	//true while the station is receiving glass
	
	int i = 0;
	int j = 0;							//j and i are used in the animation of the actuator as counters
	int targetArrayCounter = 0;			//counter for the target array
	int glassTargetMargin;				//calculates the amount the glass has to move onto the station
	int angle;							//angle of the station
	
	double endOfArmYFake;				//used to keep track of y coordinate of the actuator
	
	//Coordinates and width of the ImageIcons. If Width/Height is not final, assume it needs to be extended/retracted
	double baseX, baseY;
	final int BASE_WIDTH = base.getIconWidth();
	final int BASE_HEIGHT = base.getIconHeight();
	
	double plowX, plowY;
	final int PLOW_WIDTH = plow.getIconWidth();
	final int PLOW_HEIGHT = plow.getIconHeight();
	
	double machineBaseX, machineBaseY;
	final int MACHINE_BASE_WIDTH = machineBase.getIconWidth();
	final int MACHINE_BASE_HEIGHT = machineBase.getIconHeight();
	
	double actuatorX, actuatorY;
	int actuatorWidth = actuatorIdle.getIconWidth();
	int actuatorHeight = actuatorIdle.getIconHeight();
		
	double liftArmX, liftArmY;
	int liftArmWidth = liftArm.getIconWidth();
	int liftArmHeight = liftArm.getIconHeight();
	
	double armBaseX, armBaseY;
	int armBaseWidth = armBase.getIconWidth();
	int armBaseHeight = armBase.getIconHeight();
	
	double extenderArmX, extenderArmY;
	int extenderArmWidth = extenderArm.getIconWidth();
	int extenderArmHeight = extenderArm.getIconHeight();
	
	double endOfArmX, endOfArmY;
	int endOfArmWidth = endOfArm.getIconWidth();
	int endOfArmHeight = endOfArm.getIconHeight();
	
	//StandAloneMachineController reference
	StandAloneMachineController controller;
	//OperatorController reference
	OperatorController opController;
	

	public GuiCrossSeamer() {
		setPlow(baseX+28,baseY+21);
		setMachineBase(baseX+13,baseY+13);
		setArmBase(machineBaseX+6, machineBaseY+9);
		setLiftArm(armBaseX + 4, armBaseY + 9);
		setExtenderArm(liftArmX+7, liftArmY-10);
		setActuator(extenderArmX-4, extenderArmY+extenderArmHeight+7);
		setEndOfArm(extenderArmX-2, extenderArmY+extenderArmHeight);
		
		glassTargetMargin = (int)(liftArmY + liftArmHeight + 5);
		
		actuator.add(0, actuatorActive1);
		actuator.add(1, actuatorActive2);
		actuator.add(2, actuatorActive3);
	}
	public void drawStation(Graphics2D g, Component c) {
		g.rotate(Math.toRadians(angle), baseX+(BASE_WIDTH/2), baseY+(BASE_HEIGHT/2));
		base.paintIcon(c, g, (int)baseX, (int)baseY);
		plow.paintIcon(c, g, (int)plowX, (int)plowY);
		machineBase.paintIcon(c, g, (int)machineBaseX, (int)machineBaseY);
		g.rotate(Math.toRadians(-angle),(baseX+(BASE_WIDTH/2)),(baseY+(BASE_HEIGHT/2)));
	}
	public void update() {
		if (cross) {
			crossSeamAt();
		}
		if (plowAnimate) {
			plowAnimation();
		}
		else if (isReceivingGlass) {
			//If the glass is at target position (i.e. ready to be worked on)
			if(glassDirection == FactoryPart.NORTH && myGlass.getYCoord() <= (baseY + (glassTargetMargin)) 
					|| glassDirection == FactoryPart.SOUTH && myGlass.getYCoord() >= (baseY + (BASE_HEIGHT - glassTargetMargin - myGlass.getHeight()) ) )
			    {
			    	//Stop receiving glass
			    	isReceivingGlass = false;
		
			    	//Determine the direction from which the glass was passed
			    	dir = 0;
			    	switch(glassDirection) 
			    	{
			    		case FactoryPart.NORTH:	dir = FactoryPart.SOUTH;
			    								break;
			    		case FactoryPart.SOUTH:	dir = FactoryPart.NORTH;
			    								break;
			    		case FactoryPart.WEST:	dir = FactoryPart.EAST;
			    								break;
			    		case FactoryPart.EAST:	dir = FactoryPart.WEST;
			    								break;
			    		default:				break;
			    	}
		
			    	//Tell the FactoryPart that passed the glass to finish passing.
			    	if (getPart(dir) != null) {
			    		getPart(dir).finishPassing();
			    		idle = true;
			    	}
			}
			else {
			   myGlass.moveLaterally(glassDirection, 1);
			}
		}
	}
	public void draw(Graphics2D g, Component c) {
		g.rotate(Math.toRadians(angle),(baseX+(BASE_WIDTH/2)), (baseY+(BASE_HEIGHT/2)));
		//draw the idle cross seamer
		if (idle || isReceivingGlass) {
			g.drawImage(actuatorIdle.getImage(), (int)actuatorX-(actuatorWidth%16/2), (int)actuatorY-(actuatorHeight%16/2)+extenderArmHeight-60, actuatorWidth, actuatorHeight, c);
		}
		//Draw the cross seamer actuator spinning
		else {
			g.drawImage(actuator.get(i).getImage(), (int)actuatorX-(actuatorWidth%16/2), (int)actuatorY+-(actuatorHeight%16/2)+extenderArmHeight-60, actuatorWidth, actuatorHeight, c);
			if (j%10==0) {
				i++;
			}
			if (i == 2) {
				i = 0;
			}
			j++;
		}
		
		g.drawImage(extenderArm.getImage(), (int)extenderArmX, (int)extenderArmY, extenderArmWidth, extenderArmHeight, c);
		
		armBase.paintIcon(c, g, (int)armBaseX, (int)armBaseY);
		liftArm.paintIcon(c, g, (int)liftArmX, (int)liftArmY);
		endOfArm.paintIcon(c, g, (int)(actuatorX+2), (int)endOfArmY+extenderArmHeight-60);
	
		g.rotate(Math.toRadians(-angle),(baseX+(BASE_WIDTH/2)),(baseY+(BASE_HEIGHT/2)));
	}
	/**
	 * Scales the actuator's width and height up by 1 pixel each when called.
	 */
	public void scaleUp() {
		actuatorWidth++;
		actuatorHeight++;
	}
	/**
	 * Scales the actuator's width and height down by 1 pixel each when called.
	 */
	public void scaleDown() {
		actuatorWidth--;
		actuatorHeight--;
	}
	/**
	 * This method is called in update while plowAnimate is true, it animates the plow pushing the glass
	 * off of the station, and sets plowAnimate to true and calls getPart.receiveGlass() when it is
	 * finished.
	 */
	public void plowAnimation() {
		if (plowY <= baseY+60 && !plowBack) {
			plowY++;
			if (myGlass != null && plowY >= baseY + 32) {
				
				if (angle == 0) {
					myGlass.moveLaterally(SOUTH, 1);
				}
				else if (angle == 90) {
					myGlass.moveLaterally(WEST, 1);
				}
				else if (angle == 180) {
					myGlass.moveLaterally(NORTH, 1);
				}
				else if (angle == 270) {
					myGlass.moveLaterally(EAST, 1);
				}
			}
		}
		else if (plowY > baseY+60 && !plowBack) {
			plowBack = true;
		}
		else if (plowBack && plowY >= baseY+22) {
			plowY--;
		}
		else {
			if (myGlass != null) {
				plowAnimate = false;
				idle = true;
				getPart(dir).receiveGlass(myGlass, glassDirection);
			}
		}
	}
	/**
	 * This sets the mahine controller.
	 * @param c
	 */
	public void setController(StandAloneMachineController c){
		controller = c;
	}
	/**
	 * This sets the operator controller.
	 * @param oc
	 */
	public void setOperatorController(OperatorController oc) {
		opController = oc;
	}
	/**
	 * This controller call tells the machine to perform its task, which causes the cross flag
	 * to be set to true. It also sends it the GlassDesign, from which the machine takes
	 * the ArrayList it needs. 
	 */
	public void playJob(String job_type, GlassDesign gd) {
		cross = true;
		idle = false;
		myTargetPoints = gd.getBorderPoints();
	}
	/**
	 * Constructor call that causes the machine to pass glass back to the
	 * popup. It sets the plowAnimate flag to true, which causes the
	 * plowAnimation() method to be called.
	 */
	public void playPass() {
		idle = false;
		this.setPlowAnimate();
	}
	/**
	 * crossSeamAt() is called while cross is true. It calls goTo and that sends the machine
	 * to the proper point according to the machine's respective ArrayList. When the global counter
	 * (targetArrayCounter) has reached the size of the array, it sets cross to false, resets the
	 * counter, sets idle to true, and calls the done animation. It will call the normal doneJob()
	 * method if it is not broken and doneJob("BROKEN") if it is broken.
	 */
	public void crossSeamAt() {
		if (targetArrayCounter < myTargetPoints.size()) {
			goTo(myTargetPoints.get(targetArrayCounter).getX() + myGlass.getXCoord(), myTargetPoints.get(targetArrayCounter).getY() + myGlass.getYCoord());
		}
		else {
			cross = false;
			targetArrayCounter = 0;
			idle = true;
			if (!broken) {
				controller.doneJob();
			}
			else {
				myGlass.breakGlass();
				controller.doneJob("BROKEN");
			}
		}
	}
	/**
	 * goTo is called in crossSeamAt(), which sends the arm the the parameters x and y.
	 * When it reaches the coordinates, it scales down (if it isn't already), calls
	 * the crossSeamedAtPoint() method, and increments the counter.
	 * @param x
	 * @param y
	 */
	public void goTo(double x, double y) {
		if (endOfArmX+(endOfArmWidth/2) < x) {
			armBaseX++;
			liftArmX++;
			extenderArmX++;
			actuatorX++;
			endOfArmX++;
		}
		else if (endOfArmX+(endOfArmWidth/2) > x) {
			armBaseX--;
			liftArmX--;
			extenderArmX--;
			actuatorX--;
			endOfArmX--;
		}
		else if (endOfArmYFake+(endOfArmHeight/2) < y) {
			extenderArmHeight++;
			endOfArmYFake++;
		}
		else if (endOfArmYFake+(endOfArmHeight/2) > y) {
			extenderArmHeight--;
			endOfArmYFake--;
		}
		else {
			if (actuatorWidth > 16) {
				scaleDown();
			}
			else {
				if (targetArrayCounter < myTargetPoints.size() && !broken) {
					myGlass.crossSeamGlassAtPoint(myTargetPoints.get(i));
				}
				targetArrayCounter++;
			}
		}
	}
	public void drawUnderGlass(Graphics g, ImageObserver c) {
		drawStation((Graphics2D)g, (Component)c);
	}
	public void drawOverGlass(Graphics g, ImageObserver c) {
		draw((Graphics2D)g, (Component)c);
	}
	public void receiveGlass(GuiGlass glass, int direction) {
		myGlass = glass;
		
		isReceivingGlass	= true;
		idle = cross = plowAnimate = plowBack = false;
				
		//Determine the direction the glass is moving as it is being received according to the NESW direction of the object passing the glass to the station
		switch(direction)
		{
		    case FactoryPart.WEST:	glassDirection  = FactoryPart.EAST;
									break;
		    case FactoryPart.EAST:	glassDirection  = FactoryPart.WEST;
									break;
		    case FactoryPart.NORTH:	glassDirection  = FactoryPart.SOUTH;
									break;
		    case FactoryPart.SOUTH:	glassDirection  = FactoryPart.NORTH;
									break;
		    default:				break;
		}
	}
	public void finishPassing() {
		myGlass = null;
		opController.animationDone();
		controller.donePass();
	}
	public void giveGlassToOperator() {
		if(getPart(FactoryPart.NORTH) instanceof GuiOperator) {
			getPart(FactoryPart.NORTH).receiveGlass(myGlass, FactoryPart.SOUTH);
			myGlass = null;
		}
		else if(getPart(FactoryPart.EAST) instanceof GuiOperator) {
			getPart(FactoryPart.EAST).receiveGlass(myGlass, FactoryPart.WEST);
			myGlass = null;
		}
		else if(getPart(FactoryPart.SOUTH) instanceof GuiOperator) {
			getPart(FactoryPart.SOUTH).receiveGlass(myGlass, FactoryPart.NORTH);
			myGlass = null;
		}
		else if(getPart(FactoryPart.WEST) instanceof GuiOperator) {
			getPart(FactoryPart.WEST).receiveGlass(myGlass, FactoryPart.EAST);
			myGlass = null;
		}
		else {
			System.out.println("COULD NOT FIND OPERATOR");
		}
	}
	
	//SETTERS
	
	/**
	 * setBreak takes in a flag from the graphics  panel which tells the machine whether it is
	 * broken or not.
	 */
	public void setBreak(boolean flag) {
		broken = flag;
	}
	public void setActuator(double x, double y) {
		actuatorX = x;
		actuatorY = y;
	}
	public void setBase(double x, double y) {
		baseX = x;
		baseY = y;
	}
	public void setMachineBase(double x, double y) {
		machineBaseX = x;
		machineBaseY = y;
	}
	public void setLiftArm(double x, double y) {
		liftArmX = x;
		liftArmY = y;
	}
	public void setArmBase(double x, double y) {
		armBaseX = x; 
		armBaseY = y;
	}
	public void setExtenderArm(double x, double y) {
		extenderArmX = x;
		extenderArmY = y;
	}
	public void setEndOfArm(double x, double y) {
		endOfArmX = x;
		endOfArmY = y;
	}
	public void setPlow(double x, double y) {
		plowX = x;
		plowY = y;
	}
	public void setPlowAnimate() {
		plowAnimate = true;
	}
	public void setX(int x) {
		baseX = x;
		setCrossSeamer();
	}
	public void setY(int y) {
		baseY = y;
		setCrossSeamer();
	}
	public void setRotation(int degree) {
		angle = degree;
	}
	public void setCrossSeamer() {
		setPlow(baseX+28,baseY+21);
		setMachineBase(baseX+13,baseY+13);
		setArmBase(machineBaseX+6, machineBaseY+9);
		setLiftArm(armBaseX + 4, armBaseY + 9);
		setExtenderArm(liftArmX+7, liftArmY-10);
		setActuator(extenderArmX-4, extenderArmY+extenderArmHeight+7);
		setEndOfArm(extenderArmX-2, extenderArmY+extenderArmHeight);
		endOfArmYFake = endOfArmY;
	}
	
	//GETTERS
	
	/**
	 * isBroken returns a flag telling whether or not the machine is broken
	 */
	public boolean isBroken() {
		return broken;
	}
	public int getX() {
		return (int)baseX;
	}
	public int getY() {
		return (int)baseY;
	}
	public int getWidth() {
		return BASE_WIDTH;
	}
	public int getHeight() {
		return BASE_HEIGHT;
	}
	public int getRotation() {
		return angle;
	}
	public boolean hasGlass() {
		if (myGlass != null) {
			return true;
		}
		return false;
	}
	public String getStatus() {
		if (idle) {
			return "OFF, idle";
		}	
		else if (isReceivingGlass) {
			return "ON, receiving glass";
		}
		else if (cross) {
			return "ON, cross seaming";
		}
		else {
			return "ON, plow animating";
		}
	}	
}
