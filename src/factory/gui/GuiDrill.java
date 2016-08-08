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
 * GuiDrill class
 * 
 * The stand alone machine GuiDrill drills the glass at the points it is given.
 * 
 * @author Elias Shanaa
 * @version v2
 *
 */

public class GuiDrill extends FactoryPart implements AnimatedPart, MachineControllerInteractor, OperatorControllerInteractor {
	final String baseURL = "images/";
	
	GuiGlass myGlass;						//GuiGlass reference stored globally
	int	glassDirection;						//GuiGlass direction
	int dir;								//Opposite of GuiGlass direction
	
	//ImageIcons for each station part
	ImageIcon base = new ImageIcon(baseURL + "station_base.png");
	ImageIcon liftArm = new ImageIcon(baseURL + "station_machine_liftarm.png");
	ImageIcon armBase = new ImageIcon(baseURL + "station_machine_arm_base.png");
	ImageIcon extenderArm = new ImageIcon(baseURL + "station_machine_extenderarm.png");
	ImageIcon machineBase = new ImageIcon(baseURL + "station_machine_base.png");
	ImageIcon drill = new ImageIcon(baseURL + "drill_actuator.png");
	ImageIcon plow = new ImageIcon(baseURL + "station_machine_plow.png");
	
	//ArrayList of target points used in crossSeamAt() and goTo(). Edge of the glass
	ArrayList<Point2D.Double> myTargetPoints;
	int targetArrayCounter = 0;				//counter for the target array
	
	boolean idle = true;					//true while machine is idle
	boolean isReceivingGlass = false;		//true while station is receiving glass
	boolean drillingGlass = false;			//true while the drill is actually drilling glass
	boolean drilling = false;				//true while the drill is moving to the position it needs to drill at
	boolean plowBack = false;				//true while the plow is moving back
	boolean plowAnimate = false;			//true while the plow is animating
	boolean scalingUp = false;				//true while the drill is scaling up
	boolean broken = false;					//true while the machine is broken

	int glassTargetMargin;					//amount the glass needs to move to go onto the station from the popup
	int i = 0;								//counter used to slow down the drill animation
	int angle;								//angle of the station
	
	double drillYFake;			//used to keep track of y coordinate of the actuator
	
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
	
	double liftArmX, liftArmY;
	int liftArmWidth = liftArm.getIconWidth();
	int liftArmHeight = liftArm.getIconHeight();
	
	double armBaseX, armBaseY;
	int armBaseWidth = armBase.getIconWidth();
	int armBaseHeight = armBase.getIconHeight();
	
	double extenderArmX, extenderArmY;
	int extenderArmWidth = extenderArm.getIconWidth();
	int extenderArmHeight = extenderArm.getIconHeight();
	
	double drillX, drillY;
	int drillWidth = drill.getIconWidth();
	int drillHeight = drill.getIconHeight();
	
	//StandAloneMachineController reference
	StandAloneMachineController controller;
	//OperatorController reference
	OperatorController opController;
	
	/**
	 * Constructor
	 */
	public GuiDrill() {
		setPlow(baseX+28,baseY+21);
		setMachineBase(baseX+13,baseY+13);
		setArmBase(machineBaseX+6, machineBaseY+9);
		setLiftArm(armBaseX + 4, armBaseY + 9);
		setExtenderArm(liftArmX+7, liftArmY-7);
		setDrill(extenderArmX-2, extenderArmY+extenderArmHeight);
		
		glassTargetMargin = (int)(liftArmY + liftArmHeight + 5);
		
	}
	/*
	 * setters
	 */
	public void drawStation(Graphics2D g, Component c) {
		g.rotate(Math.toRadians(angle),(baseX+(BASE_WIDTH/2)), (baseY+(BASE_HEIGHT/2)));
		base.paintIcon(c, g, (int)baseX, (int)baseY);
		plow.paintIcon(c, g, (int)plowX, (int)plowY);
		machineBase.paintIcon(c, g, (int)machineBaseX, (int)machineBaseY);
		g.rotate(Math.toRadians(-angle),(baseX+(BASE_WIDTH/2)),(baseY+(BASE_HEIGHT/2)));
	}
	public void update() {
		if (drilling) {
			drillAt();
		}
		if (plowAnimate) {
			plowAnimation();
		}
		if (isReceivingGlass) {
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
		
		g.drawImage(extenderArm.getImage(), (int)extenderArmX, (int)extenderArmY, extenderArmWidth, extenderArmHeight, c);
		armBase.paintIcon(c, g, (int)armBaseX, (int)armBaseY);
		liftArm.paintIcon(c, g, (int)liftArmX, (int)liftArmY);
		g.drawImage(drill.getImage(), (int)drillX-(drillWidth%12/2), (int)drillY-(drillHeight%19/2)+extenderArmHeight-60, drillWidth, drillHeight,c);
		
		g.rotate(Math.toRadians(-angle),(baseX+(BASE_WIDTH/2)), (baseY+(BASE_HEIGHT/2)));
	}
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
				getPart(dir).receiveGlass(myGlass, glassDirection);
			}
		}
	}
	/**
	 * Scales the drill's width and height up by 1 pixel each when called.
	 */
	public void scaleDrillUp() {
		drillWidth++;
		drillHeight++;
	}
	/**
	 * Scales the drill's width and height down by 1 pixel each when called.
	 */
	public void scaleDrillDown() {
		drillWidth--;
		drillHeight--;
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
	 * This controller call tells the machine to perform its task, which causes the drilling
	 * flag to be set to true. It also sends it the GlassDesign, from which the machine takes
	 * the ArrayList it needs. 
	 */
	public void playJob(String job_type, GlassDesign gd) {
		if (drillWidth <= 20) {
			scaleDrillUp();
		}
		drilling = true;
		myTargetPoints = gd.getDrillPoints();
	}
	/**
	 * drillAt() is called while drilling is true. It calls goTo and that sends the machine
	 * to the proper point according to the machine's respective ArrayList. When the global counter
	 * (targetArrayCounter) has reached the size of the array, it sets drilling to false, resets the
	 * counter, sets idle to true, and calls the done animation. It will call the normal doneJob()
	 * method if it is not broken and doneJob("BROKEN") if it is broken.
	 */
	public void drillAt() {
		if (targetArrayCounter < myTargetPoints.size()) {
			goTo(baseX + baseX + BASE_WIDTH - (myTargetPoints.get(targetArrayCounter).getX() + myGlass.getXCoord()), baseY + baseY + BASE_HEIGHT - (myTargetPoints.get(targetArrayCounter).getY() + myGlass.getYCoord()));
			drillingGlass = true;
		}
		else {
			drilling = false;
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
	 * goTo is called in drillAt(), which sends the arm the the parameters x and y.
	 * When it reaches the coordinates, it calls the drillGlass() method
	 * @param x
	 * @param y
	 */
	public void goTo(double x, double y) {
		if (drillX+(drillWidth/2) < x) {
			armBaseX++;
			liftArmX++;
			extenderArmX++;
			drillX++;
		}
		else if (drillX+(drillWidth/2) > x) {
			armBaseX--;
			liftArmX--;
			extenderArmX--;
			drillX--;
		}
		else if (drillYFake+(drillHeight/2) < y) {
			extenderArmHeight++;
			drillYFake++;
		}
		else if (drillYFake+(drillHeight/2) > y) {
			extenderArmHeight--;
			drillYFake--;
		}
		else {
			if (drillingGlass) {
				drillGlass();
			}
		}
	}
	/**
	 * This method animates the drill scaling down, and then up.
	 * When it is finished, it calls the method drillGlassAtPoint()
	 * and increments the array counter.
	 */
	public void drillGlass() {
		if (drillWidth > 12 && !scalingUp) {
			if (i%5 == 0) {
				scaleDrillDown();
			}
			i++;
			if (drillWidth <= 12) {
				scalingUp = true;
				i = 0;
				
			}
		}
		else if (scalingUp) {
			if (drillWidth <= 18) {	
				if (i % 5 == 0) {
					scaleDrillUp();
				}
				i++;
				if (drillWidth >= 17) {
					i = 0;
					drillingGlass = false;
					scalingUp = false;
					if (targetArrayCounter < myTargetPoints.size() && !broken) {
						myGlass.drillGlassAtPoint(myTargetPoints.get(targetArrayCounter));
					}
					targetArrayCounter++;
				}
			}
		}
	}
	//station gets drawn under the glass
	public void drawUnderGlass(Graphics g, ImageObserver c) {
		drawStation((Graphics2D)g, (Component)c);
	}
	//arm parts get drawn above the glass
	public void drawOverGlass(Graphics g, ImageObserver c) {
		draw((Graphics2D)g, (Component)c);
	}
	/**
	 * Called when another machine wants to send this machine glass.
	 */
	public void receiveGlass(GuiGlass glass, int direction) {
		myGlass = glass;
		isReceivingGlass = true;
		idle = drilling = scalingUp = plowAnimate = plowBack = false;
		
		//Determine the direction the glass is moving as it is being received according to the NESW 
		//direction of the object passing the glass to the station
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
	/**
	 * When called, it calls animationDone() and donePass(), and sets myGlass
	 * to null.
	 */
	public void finishPassing() {
		opController.animationDone();
		controller.donePass();
		myGlass = null;
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
	
	public void setController(StandAloneMachineController c){
		controller = c;
	}
	public void setOperatorController(OperatorController oc) {
		opController = oc;
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
	public void setDrill(double x, double y) {
		drillX = x;
		drillY = y;
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
		setDrill();
	}
	public void setY(int y) {
		baseY = y;
		setDrill();
	}
	public void setRotation(int degree) {
		angle = degree;
	}
	public void setDrill() {
		setPlow(baseX+28,baseY+21);
		setMachineBase(baseX+13,baseY+13);
		setArmBase(machineBaseX+6, machineBaseY+9);
		setLiftArm(armBaseX + 4, armBaseY + 9);
		setExtenderArm(liftArmX+7, liftArmY-7);
		setDrill(extenderArmX-2, extenderArmY+extenderArmHeight);
		
		drillYFake = drillY;
	}
	public void setBreak(boolean flag) {
		broken = flag;
	}
	
	//GETTERS
	
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
		else if (drilling || scalingUp || drillingGlass) {
			return "ON, drilling";
		}
		else {
			return "ON, plow animating";
		}
	}
}
