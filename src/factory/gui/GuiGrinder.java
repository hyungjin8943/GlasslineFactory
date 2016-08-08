package factory.gui;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
 * GuiGrinder class
 * 
 * Stand alone machine grinder goes over all of the glass and grinds it.
 * 
 * @author Elias Shanaa
 * @version v2
 *
 */

public class GuiGrinder extends FactoryPart implements AnimatedPart, MachineControllerInteractor, OperatorControllerInteractor {
	final String baseURL = "images/";
	
	GuiGlass myGlass;						//GuiGlass reference stored globally
	int	glassDirection;						//GuiGlass direction
	int dir;								//Opposite of GuiGlass direction
	
	int angle;								//angle of the station
	
	//ImageIcons for each station part
	ImageIcon actuatorIdle = new ImageIcon(baseURL + "grinder_actuator_idle.png");
	ImageIcon actuatorActive1 = new ImageIcon(baseURL + "grinder_actuator_active1.png");
	ImageIcon actuatorActive2 = new ImageIcon(baseURL + "grinder_actuator_active2.png");
	ImageIcon actuatorActive3 = new ImageIcon(baseURL + "grinder_actuator_active3.png");
	ImageIcon base = new ImageIcon(baseURL + "station_base.png");
	ImageIcon liftArm = new ImageIcon(baseURL + "station_machine_liftarm.png");
	ImageIcon armBase = new ImageIcon(baseURL + "station_machine_arm_base.png");
	ImageIcon extenderArm = new ImageIcon(baseURL + "station_machine_extenderarm.png");
	ImageIcon machineBase = new ImageIcon(baseURL + "station_machine_base.png");
	ImageIcon endOfArm = new ImageIcon(baseURL + "station_machine_endofarm.png");
	ImageIcon plow = new ImageIcon(baseURL + "station_machine_plow.png");
	
	//Array for the grinder coordinates
	ArrayList<ArrayList<Integer>> physicalSchematic	= new ArrayList<ArrayList<Integer>>();
	
	boolean idle = true;					//true while idle
	boolean isReceivingGlass = false;		//true while receiving glass
	boolean preparing = false;				//true while preparing to grind glass	
	boolean grinding = false;				//true while grinding glass
	boolean left = true;					//true when hits the left position
	boolean right = false;					//true when hits the right position
	boolean moving = false;					//true while moving
	boolean plowAnimate = false;			//true while plow is animating
	boolean plowBack = false;				//true while the plow is moving back
	boolean retract = false;				//true while the arm is retracting
	boolean broken = false;					//true while the station is broken
	
	//ArrayList of the actuator images. Used to cycle between them when grinding
	ArrayList<ImageIcon> actuator;
	int i = 0;								//i and j used to slow down animation of the grinder actuator
	int j = 0;
	int backup = 0;							//used to move the grinder back
	
	int glassTargetMargin;					//calculates the amount the glass has to move onto the station

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
	
	//Communication with Agent
	public StandAloneMachineController controller;
	OperatorController opController;
	
	/**
	 * Constructor
	 */
	public GuiGrinder() {
		setPlow(baseX+28,baseY+21);
		setMachineBase(baseX+13,baseY+13);
		setArmBase(machineBaseX+6, machineBaseY+9);
		setLiftArm(armBaseX + 4, armBaseY + 9);
		setExtenderArm(liftArmX+7, liftArmY-9);
		setActuator(extenderArmX-11, extenderArmY+extenderArmHeight);
		setEndOfArm(extenderArmX-2, extenderArmY+extenderArmHeight);
	
		glassTargetMargin	= (int) (liftArmY + liftArmHeight + 5);
		
		actuator = new ArrayList<ImageIcon>();
		actuator.add(0, actuatorActive1);
		actuator.add(1, actuatorActive2);
		actuator.add(2, actuatorActive3);
	}
	public void drawStation(Graphics2D g, Component c) {
		g.rotate(Math.toRadians(angle),(baseX+(BASE_WIDTH/2)), (baseY+(BASE_HEIGHT/2)));
		base.paintIcon(c, g, (int)baseX, (int)baseY);
		plow.paintIcon(c, g, (int)plowX, (int)plowY);
		machineBase.paintIcon(c, g, (int)machineBaseX, (int)machineBaseY);
		g.rotate(Math.toRadians(-angle),(baseX+(BASE_WIDTH/2)),(baseY+(BASE_HEIGHT/2)));
	}
	public void update() {
		if (idle) {
			//empty
		}
		else if (preparing) {
			prepareToGrind();
		}
		else if (grinding) {
			grind();
		}
		else if (retract) {
			retractToIdle();
		}
		if (plowAnimate) {
			plowAnimation();
		}
		else if (isReceivingGlass)
		{
			//If the glass is at target position (i.e. ready to be worked on)
			if(glassDirection == FactoryPart.NORTH && myGlass.getYCoord() <= (baseY + (glassTargetMargin)) 
				|| glassDirection == FactoryPart.SOUTH && myGlass.getYCoord() >= (baseY + (BASE_HEIGHT - glassTargetMargin - myGlass.getHeight()) ) ) {
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
		if (idle || isReceivingGlass || preparing || plowAnimate) {
			if (preparing) {
				g.drawImage(actuatorIdle.getImage(), (int)actuatorX-(actuatorWidth%30/2), (int)actuatorY-(actuatorHeight%30)+extenderArmHeight-56, actuatorWidth, actuatorHeight, c);
			}
			else {
				g.drawImage(actuatorIdle.getImage(), (int)actuatorX-(actuatorWidth%30/2), (int)actuatorY-(actuatorHeight%30)+extenderArmHeight-60, actuatorWidth, actuatorHeight, c);
			}
		}
		//draw the grinding actuator
		else {
			g.drawImage(actuator.get(i).getImage(), (int)actuatorX-(actuatorWidth%30/2), (int)actuatorY-(actuatorHeight%30/2)+extenderArmHeight-60, actuatorWidth, actuatorHeight, c);
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
		endOfArm.paintIcon(c, g, (int)endOfArmX, (int)endOfArmY+extenderArmHeight-60);
		
		g.rotate(Math.toRadians(-angle),(baseX+(BASE_WIDTH/2)), (baseY+(BASE_HEIGHT/2)));
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
	 * prepareToGrind() method is called while preparing flag is true, and it moves the arm to
	 * the initial grinding spot.
	 */
	public void prepareToGrind() {
		//scale up first
		if (actuatorWidth < 41) {
			scaleUp();
		}
		//move down to bottom of glass
		else if (extenderArmHeight < 100) {
			extenderArmHeight++;
		}
		//exit preparing method
		else {
			preparing = false;
			grinding = true;
		}
	}
	/**
	 * grind() method is called while the grinding flag is true, it causes the grinder to go over the
	 * glass.
	 */
	public void grind() {
		//scale down, start actuator
		if (actuatorWidth > 31) {
			scaleDown();
		}
		else {
			moveRight();
		}
	}
	public void moveRight() {
		
		if (endOfArmX+6 < machineBaseX+70 && left) {
			moving = true;
			armBaseX++;
			liftArmX++;
			extenderArmX++;
			endOfArmX++;
			actuatorX++;
		}
		else {
			moving = false;
			right = true;
			left = false;
			moveBack();
		}
	}
	public void moveBack() {
		
		if (backup < 15 && !moving) {
			extenderArmHeight--;
			backup++;
		}
		if (extenderArmHeight <= 60) {
			grinding = false;
			retract = true;
		}
		else if (left) {
			moving = true;
			moveRight();
		}
		else {
			moving = true;
			moveLeft();
		}
	}
	public void moveLeft() {
		if (endOfArmX+6 > machineBaseX+20 && right) {
			armBaseX--;
			liftArmX--;
			extenderArmX--;
			endOfArmX--;
			actuatorX--;
		}
		else {
			moving = false;
			backup = 0;
			left = true;
			right = false;
			moveBack();
		}
	}
	/**
	 * retractToIdle() method is called while the retract flag is true, it moves the
	 * grinder back to its initial position.
	 */
	public void retractToIdle() {
		if (endOfArmX+6 > machineBaseX+20) {
			armBaseX--;
			liftArmX--;
			extenderArmX--;
			endOfArmX--;
			actuatorX--;
		}
		else {
			idle = true;
			retract = false;
			
			if (myGlass != null) {
				if (broken) {
					myGlass.breakGlass();
					controller.doneJob("BROKEN");
				}
				else {
					myGlass.setEffectsState("ground");
					myGlass.fitEffectsImages(physicalSchematic);
					controller.doneJob();
				}
			}
		}
	}
	/**
	 * This method is called in update while plowAnimate is true, it animates the plow pushing the glass
	 * off of the station, and sets plowAnimate to true and calls getPart.receiveGlass() when it is
	 * finished.
	 */
	public void plowAnimation() {
		if (plowY <= baseY+50 && !plowBack) {
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
		else if (plowY >= baseY+50 && !plowBack){
			plowBack = true;
		}
		else if (plowBack && plowY >= baseY+22){
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
	 * Constructor call that causes the machine to pass glass back to the
	 * popup. It sets the plowAnimate flag to true, which causes the
	 * plowAnimation() method to be called.
	 */
	public void playPass() {
		this.setPlowAnimate();
	}
	/**
	 * This controller call tells the machine to perform its task, which causes the preparing 
	 * flag to be set to true. It also sends it the GlassDesign, from which the machine takes
	 * the ArrayList it needs. 
	 */
	public void playJob(String job_type, GlassDesign gd) {
		idle = false;
		preparing = true;
		physicalSchematic = gd.getPhysicalSchematic();
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
		idle = preparing = grinding = moving = plowAnimate = plowBack = false;
				
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
		opController.animationDone();
		controller.donePass();
		myGlass = null;
	}
	
	//SETTERS

	/**
	 * setBreak takes in a flag from the graphics  panel which tells the machine whether it is
	 * broken or not.
	 */
	public void setBreak(boolean flag) {
		broken = flag;
	}
	/**
	 * isBroken returns a flag telling whether or not the machine is broken
	 */
	public boolean isBroken() {
		return broken;
	}
	
	public void setController(StandAloneMachineController c){
		controller = c;
	}
	public void setOperatorController(OperatorController oc) {
		opController = oc;
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
		setGrinder();
	}
	public void setY(int y) {
		baseY = y;
		setGrinder();
	}
	public void setRotation(int degree) {
		angle = degree;
	}
	
	//GETTERS
	
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
	
	public void setGrinder() {
		setPlow(baseX+28,baseY+21);
		setMachineBase(baseX+13,baseY+13);
		setArmBase(machineBaseX+6, machineBaseY+9);
		setLiftArm(armBaseX + 4, armBaseY + 9);
		setExtenderArm(liftArmX+7, liftArmY-9);
		setActuator(extenderArmX-11, extenderArmY+extenderArmHeight);
		setEndOfArm(extenderArmX-2, extenderArmY+extenderArmHeight);
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
		else if (preparing || grinding || left || right) {
			return "ON, grinding";
		}
		else if (plowAnimate) {
			return "ON, plow animating";
		}
		else {
			return "SHOULD NOT BE HERE!";
		}
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
}
