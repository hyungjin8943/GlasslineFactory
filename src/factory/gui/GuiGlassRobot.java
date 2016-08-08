package factory.gui;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

import factory.controller.GlassRobotController;
import factory.controller.GlassRobotController.ConveyorID;
import factory.interfaces.IGuiGlassRobot;

/**
 * CSCI 200 Factory Project
 * Version: v2
 * 
 * GuiGlassRobot class
 * 
 * The glass robot is responsible for transporting glass from the bin to the conveyor,
 * and from the conveyor to the truck.
 * 
 * @author Elias Shanaa
 * @version v2
 */

public class GuiGlassRobot extends FactoryPart implements AnimatedPart, IGuiGlassRobot {
	final String baseURL = "images/";
	
	ImageIcon base = new ImageIcon(baseURL+"glassrobot_base.png");
	ImageIcon extenderArm = new ImageIcon(baseURL+"glassrobot_extenderarm.png");
	ImageIcon rotatorArm = new ImageIcon(baseURL+"glassrobot_rotatorarm.png");
	ImageIcon endOfArm = new ImageIcon(baseURL+"glassrobot_endofarm.png");
	ImageIcon effector = new ImageIcon(baseURL+"glassrobot_effector.png");
	
	int expanding = 0;			//1 if scaling up, 0 if not scaling, -1 if scaling down
	/*
	 * x and y coordinates for the above ImageIcons (all doubles)
	 * width and height for the above ImageIcons (all final ints)
	 */
	double baseX, baseY; 
	final int BASE_WIDTH = base.getIconWidth();
	final int BASE_HEIGHT = base.getIconHeight(); 
	
	double rotatorArmX, rotatorArmY;
	final int ROTATOR_ARM_WIDTH = rotatorArm.getIconWidth();
	final int ROTATOR_ARM_HEIGHT = rotatorArm.getIconHeight();
	
	double extenderArmX, extenderArmY;
	final int EXTENDER_ARM_HEIGHT = extenderArm.getIconHeight();
	int extenderArmWidth = extenderArm.getIconWidth();		//not final because the width of the extenderArm changes
	
	double endOfArmX, endOfArmY;
	final int END_OF_ARM_WIDTH = endOfArm.getIconWidth();
	final int END_OF_ARM_HEIGHT = endOfArm.getIconHeight();
	
	double effectorX, effectorY;
	int effectorWidth = effector.getIconWidth();
	int effectorHeight = effector.getIconHeight();
	
	/*
	 * All angles measured in degrees.
	 * robotAngle - angle of the robot as a whole (90 degrees points down, 0 to the right)
	 * rotatorArmAngle - angle of the rotatorArm relative to the robot (same convention as robotAngle)
	 */
	double robotAngle;
	double rotatorArmAngle = 0;
	
	/*
	 * Coordinates of the glass (set as the center of the effector)
	 */
	double currentX;
	double currentY;
	
	/*
	 * Coordinates where the glass needs to be placed.
	 * targetAngle is the angle the rotatorArm needs to reach in order to get to the 
	 * final coordinates
	 */
	double targetX;
	double targetY;
	double targetAngle;	
	
	/*
	 * Rate at which the arm extends
	 * Rate at which the angle of the robotArm moves
	 */
	final int EXTEND_MOVE = 1;
	final int ANGLE_MOVE = 1;
	
	/*
	 * d - distance between center of base and target
	 * currentLength - amount traveled between center of base and target
	 */
	double d;						//distance between target and current coordinates
	double currentLength = 0;		//amount the robot has extended
	double retractLength = 0;		//amount the robot has retracted
	
	/*
	 * Other Factory related members
	 */

	GuiGlass myGlass;				//glass object
	int glassDirection;				//glass's direction
	double glassEffectorX;
	double glassEffectorY;
	
	GuiTruck myTruck;				//truck
	GuiBin b1;						//bin 1
	GuiBin b2;						//bin 2
	GuiConveyor myConveyor;			//conveyor
	//GuiTrash myTrash;				//trash
	
	int binXCoord, binYCoord;		
	int convXCoord, convYCoord;
	int truckXCoord, truckYCoord;
	int trashXCoord, trashYCoord;
	
	int brokenCounter = 0;			//keeps track of how long the glass has been flying
									//off screen for
	
	GlassRobotController controller;
	
	//api calls
	boolean dropOffAtConveyor = false;
	boolean pickUpFromBin = false;
	boolean pickUpFromConveyor = false;
	boolean rotateToTruck = false;
	boolean rotateToConveyor = false;
	boolean rotateToTrash = false;
	boolean dropOffAtTrash = false;
	boolean scaleDownDropTrash = false;
	boolean rotateToCoord = false;				//true while robot is rotating
	boolean extendToCoord = false;				//true while robot is extending extenderArm
	boolean retractToCoord = false;				//true while retracting extenderArm
	boolean idle = false;						//true while not doing any of the above
	boolean clockwise = false;					//true while rotating clockwise
	boolean counterClockwise = false;			//true while rotating counterClockwise
	boolean brokenRotateToBin = false;			//true while the robot rotates to the bin to pick up glass
	boolean binFrisbeeGlass = false;			//true if the non normative method is called
	boolean scaleDownPickBin = false;			//true while placing glass down
	boolean scaleDownDropConveyor = false;
	boolean scaleDownPickConveyor = false;
	boolean scaleDownDropTruck = false;
	boolean dropOffAtTruck = false;	
	boolean brokenScaleDown = false;
	boolean brokenAnimation = false;
	boolean brokenRotateToConveyor = false;
	boolean brokenBin = false;
	boolean brokenConveyor = false;
	boolean conveyorFrisbeeGlass = false;
	/**
	 * CONSTRUCTOR
	 */
	public GuiGlassRobot() {
		/*
		 * Set currentX and currentY relative to the effector's coordinates
		 */
		currentX = effectorX+(effectorWidth/2);
		currentY = effectorY+(effectorHeight/2);
		
		/*
		 * Initialize the target coordinates to the current coordinates
		 */
		targetX = currentX;
		targetY = currentY;
	}
	
	/**
	 * Method called in update which rotates the robot to the targetAngle in the clockwise direction
	 */
	public void rotateToFinalAngleClockwise() {
		//normalize rotatorArmAngle
		if ((int)rotatorArmAngle == (int)targetAngle) {
			//NON NORMATIVE CASE
			if (binFrisbeeGlass) {
				if (myGlass != null) {
					myGlass.moveLaterally(EAST, 8);
					myGlass.setAngle(brokenCounter);
					brokenCounter += 5;
				}
				if (brokenCounter >= 1200) {
					binFrisbeeGlass = false;
					brokenBin = false;
					myGlass = null;
					controller.doneRotateToConveyor();
					brokenCounter = 0;
					clockwise = false;
				}
			}
			else if (conveyorFrisbeeGlass) {
				if (myGlass != null) {
					myGlass.moveLaterally(WEST, 8);
					myGlass.setAngle(brokenCounter);
					brokenCounter += 5;
				}
				if (brokenCounter >= 1500) {
					conveyorFrisbeeGlass = false;
					brokenConveyor = false;
					myGlass = null;
					controller.doneRotateToTruck();
					brokenCounter = 0;
					counterClockwise = false;
				}
			}
			else {
				clockwise = false;
				
			}
				
		}
		else if (rotatorArmAngle >= 360) {
			rotatorArmAngle %= 360;
		}
		else {
			//Add rotational velocity
			rotatorArmAngle += ANGLE_MOVE;
			
			// Update the value of currentX (center of effector)
			// Set the effector to follow the rotatorArm
			effectorX = baseX + 7 + ( ROTATOR_ARM_WIDTH + extenderArmWidth - 40 ) * Math.cos( Math.toRadians( rotatorArmAngle ) );
			effectorY = baseY + ( ROTATOR_ARM_WIDTH + extenderArmWidth - 40 ) * Math.sin( Math.toRadians( rotatorArmAngle ) );
			currentX = effectorX + 23;
			currentY = effectorY + 31;
			
			if (myGlass != null) {
				myGlass.setXCoord((int) glassEffectorX);
				myGlass.setYCoord((int) glassEffectorY);
			}
		}
	}
	/**
	 * Method which rotates the rotatorArm to the target angle in the counter-clockwise direction
	 */
	public void rotateToFinalAngleCounterclockwise() {
		if ((int)rotatorArmAngle == (int)targetAngle) {
			//NON NORMATIVE CASE
			if (binFrisbeeGlass) {
				if (myGlass != null) {
					myGlass.moveLaterally(WEST, 8);
					myGlass.setAngle(brokenCounter);
					brokenCounter += 5;
				}
				if (brokenCounter >= 1200) {
					binFrisbeeGlass = false;
					myGlass = null;
					controller.doneRotateToConveyor();
					brokenCounter = 0;
					counterClockwise = false;
				}
			}
			else {
				counterClockwise = false;
			}
		}
		else if (rotatorArmAngle > 360) {
			rotatorArmAngle %= 360;
		}
		else if (rotatorArmAngle < 0) {
			rotatorArmAngle += 360;
		}
		else {
			if (rotatorArmAngle == 0) {
				rotatorArmAngle = 360;
			}
			//Add rotational velocity
			rotatorArmAngle -= ANGLE_MOVE;
			
			//Set the effector to follow the rotatorArm
			effectorX = baseX + 9 + ( ROTATOR_ARM_WIDTH + extenderArmWidth - 40 ) * Math.cos( Math.toRadians( rotatorArmAngle ) );
			effectorY = baseY + ( ROTATOR_ARM_WIDTH + extenderArmWidth - 40 ) * Math.sin( Math.toRadians( rotatorArmAngle ) );
			// Update the value of currentX (center of effector)
			currentX = effectorX + 23;
			currentY = effectorY + 31;
			
			//Move glass
			if (myGlass != null) {
				myGlass.setXCoord((int) glassEffectorX);
				myGlass.setYCoord((int) glassEffectorY);
			}
		}	
	}
	/**
	 * Method called in update which retracts arm to the target coordinates
	 */
	public void retractArm() {
		extenderArmWidth -= EXTEND_MOVE;
		
		if (retractLength < d) {
			retractLength += EXTEND_MOVE;
		}
		else {
			retractToCoord = false;
			retractLength = 0;
		}
		
		// Set the effector to follow the rotatorArm
		effectorX -= EXTEND_MOVE * Math.cos( Math.toRadians( rotatorArmAngle ) );
		effectorY -= EXTEND_MOVE * Math.sin( Math.toRadians( rotatorArmAngle ) );
		
		
		if (myGlass != null) {
			myGlass.setXCoord((int) glassEffectorX);
			myGlass.setYCoord((int) glassEffectorY);
		}
	}
	/**
	 * Method called in update which extends arm to the target coordinates
	 */
	public void extendArm() {
		extenderArmWidth += EXTEND_MOVE;
		
		if (currentLength < d) {
			currentLength += EXTEND_MOVE;
		}
		else {
			extendToCoord = false;
			currentLength = 0;
		}
		
		if (myGlass != null) {
			myGlass.setXCoord((int) glassEffectorX);
			myGlass.setYCoord((int) glassEffectorY);
		}
		
		// Set the effector to follow the rotatorArm
		effectorX += EXTEND_MOVE * Math.cos( Math.toRadians( rotatorArmAngle ) );
		effectorY += EXTEND_MOVE * Math.sin( Math.toRadians( rotatorArmAngle ) );
		
	}
	
	/**
	 * Update method causes robot to rotate when necessary, and to extend
	 * to the final coordinates. Also takes care of controller calls. 
	 */
	public void update() {
		/*
		 * Rotate robotArm to target angle
		 */
		if (rotateToCoord) {
			//scale up first
			if (effectorWidth <= 53) {
				effectorScaleUp();
			}
			else if (clockwise) {
				rotateToFinalAngleClockwise();
			}
			else if (counterClockwise) {
				rotateToFinalAngleCounterclockwise();
			}
			else {
				rotateToCoord = false;
				
				//calculates distance between current coordinates and target coordinates
				d = Math.sqrt(Math.pow(targetX-currentX, 2) + Math.pow(targetY-currentY, 2));
				
				//figure out if need to extend or retract
				//quadrants 1 and 4
				if (rotatorArmAngle >= 270 || rotatorArmAngle <= 90) {
					if (targetX > currentX) {
						extendToCoord = true;
					}
					else {
						retractToCoord = true;
					}
				}
				//quadrants 2 and 3
				else {
					if (targetX < currentX) {
						extendToCoord = true;
					}
					else {
						retractToCoord = true;
					}
				}
				
			}
		}		
		//Extend rotatorArm
		else if (extendToCoord) {
			extendArm();
			
		}
		//Retract rotatorArm
		else if (retractToCoord) {
			retractArm();
		}
		
		/*
		 * Scales down in the proper scenario
		 */
		else if (scaleDownPickBin) {
			if (effectorWidth >= 46) {
				effectorScaleDown();
			}
			else {
				b1.giveGlassToRobot(this);
				controller.donePickUpFromBin();
				scaleDownPickBin = false;
				if (brokenScaleDown) {
					brokenScaleDown = false;
					controller.doneQueryDoYouHaveGlass(false);
				}
			}
		}
		else if (scaleDownDropConveyor) {
			if (effectorWidth >= 46) {
				effectorScaleDown();
			}
			else {
				myConveyor.receiveGlass(myGlass, glassDirection);
				scaleDownDropConveyor = false;
			}
		}
		else if (scaleDownPickConveyor) {
			if (effectorWidth >= 46) {
				effectorScaleDown();
			}
			else {
				myConveyor.finishPassing();
				controller.donePickUpFromConveyor();
				scaleDownPickConveyor = false;
				if (brokenScaleDown) {
					brokenScaleDown = false;
					controller.doneQueryDoYouHaveGlass(false);
				}
			}
		}
		else if (scaleDownDropTruck) {
			if (effectorWidth >= 46) {
				effectorScaleDown();
			}
			else {
				myTruck.receiveGlass(myGlass, NORTH);
				scaleDownDropTruck = false;
			}
		}
		else if (scaleDownDropTrash) {
			if (effectorWidth >= 46) {
				effectorScaleDown();
			}
			else {
				//myTrash.receiveGlass(myGlass,glassDirection);
				controller.doneDropOffAtTrash();
				myGlass = null;
				scaleDownDropTrash = false;
			}
		}
		//done animating
		else {
			
			//done rotating to the conveyor
			if (rotateToConveyor || brokenRotateToConveyor) {
				controller.doneRotateToConveyor();
				if (brokenRotateToConveyor) {
					brokenScaleDown = true;
					brokenRotateToConveyor = false;
				}
				rotateToConveyor = false;
			}
			//done picking up from the bin (in either normal or non normative state)
			else if (pickUpFromBin || brokenRotateToBin) {
				controller.doneRotateToBin();
				if (brokenRotateToBin) {
					brokenScaleDown = true;
					brokenRotateToBin = false;
				}
				pickUpFromBin = false;
			}
			//done rotating to the truck
			else if (rotateToTruck) {
				controller.doneRotateToTruck();
				rotateToTruck = false;
			}
			//done rotating to the trash
			else if (rotateToTrash) {
				controller.doneRotateToTrash();
				rotateToTrash = false;
			}
			//done moving to the idle position
			else if (idle) {
				controller.doneMoveToIdlePosition();
				idle = false;
			}
			else {
				//do nothing
			}
		}
	}
	/**
	 * Draw method paints the icons onto the screen, adjusting for rotation
	 * by translating the axis of rotation to the center of the base
	 */
	public void draw(Graphics2D g, Component c) {
		
		if (myGlass != null) {
			glassEffectorX = effectorX - (myGlass.getWidth()/5);
			glassEffectorY = effectorY - (myGlass.getHeight()/6);
		}
		//paint base and effector before translating since they don't rotate
		base.paintIcon(c, g, (int)baseX, (int)baseY);
		g.drawImage(effector.getImage(), (int)effectorX, (int)effectorY, effectorWidth, effectorHeight, c);
		
		//translate center of rotation to center of base, draw extenderArm,
		//rotatorArm, and endOfArm (which are rotated)
	
		g.rotate(Math.toRadians(rotatorArmAngle), baseX+(BASE_WIDTH/2), baseY+(BASE_HEIGHT/2));
		
		g.drawImage(extenderArm.getImage(), (int)extenderArmX, (int)extenderArmY, extenderArmWidth, EXTENDER_ARM_HEIGHT, c);
		rotatorArm.paintIcon(c, g, (int)rotatorArmX, (int)rotatorArmY);
		endOfArm.paintIcon(c, g, (int)endOfArmX+extenderArmWidth-55, (int)endOfArmY);
		
		g.rotate(Math.toRadians(-rotatorArmAngle), baseX+(BASE_WIDTH/2), baseY+(BASE_HEIGHT/2));
	}
	/**
	 * goTo(int, int) method is called in the playRotateTo* calls from the controller.
	 * It sets the target angle between the arm and the target coordinates, and figures
	 * out whether the robot should rotate clockwise or counter-clockwise. It also figures
	 * out if the robot needs to extend or retract to those coordinates.
	 */
	public void goTo(int x, int y) {
		targetX=x;
		targetY=y;
		currentLength = 0;
		setFinalAngle();
		
		//Set rotation booleans
		if (targetAngle != rotatorArmAngle) {
			rotateToCoord = true;
			//quadrants 3 and 4
			if (rotatorArmAngle >= 180) {
				if (rotatorArmAngle - 180 < targetAngle && targetAngle < rotatorArmAngle) {
					counterClockwise = true;
				}
				else {
					clockwise = true;
				}
			}
			//quadrants 1 and 2
			else {
				if (rotatorArmAngle + 180 > targetAngle && targetAngle > rotatorArmAngle) {
					clockwise = true;
				}
				else {
					counterClockwise = true;
				}
			}
			System.out.println("Going to rotate to angle " + targetAngle);
		}
		//Extend or retract to coordinates
		else {
			//quadrants 1 and 4
			if (rotatorArmAngle >= 270 || rotatorArmAngle <= 90) {
				if (targetX > currentX) {
					extendToCoord = true;
				}
				else {
					retractToCoord = true;
				}
			}
			//quadrants 2 and 3
			else {
				if (targetX < currentX) {
					extendToCoord = true;
				}
				else {
					retractToCoord = true;
				}
			}
		}
	}
	/**
	 * setFinalAngle method calculates the angle between the center of the base and the 
	 * target coordinates. It is called in the goTo method
	 */
	public void setFinalAngle() {
		targetAngle = (int)Math.toDegrees(Math.atan(((double)targetY-(double)(baseY+32))/((double)targetX-(double)(baseX+32))));
		
		if (targetX < (baseX+32)) {
			targetAngle += 180;
		}
		else if (targetY < (baseY+32) && targetX > (baseX+32)) {
			targetAngle += 360;
		}
		if (targetAngle == -90) {
			targetAngle = 270;
		}
		if (targetAngle == 360) {
			targetAngle = 0;
		}
	}
	/**
	 * effectorScaleUp scales the effector up, along with the glass, if it is holding glass.
	 */
	public void effectorScaleUp() {
		expanding = 1;
		effectorWidth++;
		effectorHeight++;
		if (myGlass != null) {
			myGlass.moveVertically(expanding);
		}
	}
	/**
	 * effectorScaleDown() scales the effector down, along with glass, if it is holding glass.
	 */
	public void effectorScaleDown() {
		expanding = -1;
		effectorWidth--;
		effectorHeight--;
		if (myGlass != null && !scaleDownPickConveyor) {
			myGlass.moveVertically(expanding);
		}
	}
	/**
	 * Sets coordinates for rotatorArm. Called in setGlassRobot().
	 * @param x
	 * @param y
	 */
	public void setRotatorArm(double x, double y) {
		rotatorArmX = x;
		rotatorArmY = y;
	}
	/**
	 * Sets coordinates for extenderArm. Called in setGlassRobot().
	 * @param x
	 * @param y
	 */
	public void setExtenderArm(double x, double y) {
		extenderArmX = x;
		extenderArmY = y;
	}
	/**
	 * Sets coordinates for endOfArm. Called in setGlassRobot().
	 * @param x
	 * @param y
	 */
	public void setEndOfArm(double x, double y) {
		endOfArmX = x;
		endOfArmY = y;
	}
	/**
	 * Sets coordinates for effector. Called in setGlassRobot().
	 * @param x
	 * @param y
	 */
	public void setEffector(double x, double y) {
		effectorX = x;
		effectorY = y;
	}
	/**
	 * playNonNormative() method is called when the glass robot is "broken".
	 */
	public void setBreak(boolean flag) {
		
		brokenAnimation = flag;
		
	}
	public boolean isBroken() {
		return brokenAnimation;
	}
	/**
	 * frisbeeGlass() method is called after the playNonNormative() method is finished.
	 * It is resposible for rotating the arm so that it flings the glass.
	 */
	public void frisbeeGlass() {
		goTo((int)baseX+(BASE_WIDTH/2), (int)baseY-100);
	}
	/**
	 * Constructor call to rotate to the idle position.
	 */
	public void playMoveToIdlePosition() {
		System.out.println("[GuiGlassRobot] Received message moveToIdlePosition");
		goTo((int)baseX+130,(int)baseY+(BASE_HEIGHT/2));
		idle = true;
	}
	/**
	 * Constructor call to rotate to the bin.
	 */
	public void playRotateToBin() {
		System.out.println("[GuiGlassRobot] Received message rotateToBin");
		binXCoord = b1.getX()+(b1.getWidth()/2);
		binYCoord = b1.getY() + (b1.getHeight()/2);
		goTo(binXCoord,binYCoord);
		if (!brokenAnimation) {
			pickUpFromBin = true;
		}
		else {
			brokenRotateToBin = true;
			brokenBin = true;
		}
	}
	/**
	 * Constructor call to pick up glass from the bin.
	 */
	public void playPickUpFromBin() {
		System.out.println("[GuiGlassRobot] Received message pick up from bin.");
		scaleDownPickBin = true;
	}
	/**
	 * Constructor call to rotate to the conveyor. The ConveyorID tells me if I'm rotating
	 * to the conveyor to pick up or drop off glass, and I go to the appropriate coordinates.
	 */
	public void playRotateToConveyor( ConveyorID conveyor_id ) {
		System.out.println("[GuiGlassRobot] Received message rotateToConveyor");
				
		switch( conveyor_id ) {
		case DROPOFF_CONVEYOR:
			convXCoord = myConveyor.getX() + myConveyor.getWidth()/2;
			convYCoord = myConveyor.getY() + myConveyor.getLength() + 5;
			break;
		case PICKUP_CONVEYOR:
			if (brokenAnimation) {
				brokenConveyor = true;
			}
			convXCoord = myConveyor.getX()+myConveyor.getWidth()/3-5;
			convYCoord = myConveyor.getY()+myConveyor.getHeight()/2;
			break;
		}
		
		if (brokenAnimation && brokenBin) {
			controller.doneQueryDoYouHaveGlass(false);
			binFrisbeeGlass = true;
		}
		else if (brokenAnimation && brokenConveyor) {
			brokenRotateToConveyor = true;
		}
		else {
			rotateToConveyor = true;
		}
		
		goTo(convXCoord,convYCoord);
	}
	/**
	 * Constructor call to pick up glass from the conveyor.
	 */
	public void playPickUpFromConveyor() {
		System.out.println("[GuiGlassRobot] Received message pickUpFromConveyor");
		scaleDownPickConveyor = true;
	}
	/**
	 * Constructor call to drop off glass at the conveyor.
	 */
	public void playDropOffAtConveyor() {
		System.out.println("[GuiGlassRobot] Received message dropOffAtConveyor.");
		scaleDownDropConveyor = true;
		dropOffAtConveyor = true;
	}
	/**
	 * Constructor call to rotate to the truck.
	 */
	public void playRotateToTruck() {
		System.out.println("[GuiGlassRobot] Received message rotateToTruck");
		
		truckXCoord = myTruck.getX()+(myTruck.getWidth()/2+5);
		truckYCoord = myTruck.getY()+(myTruck.getHeight()/3+20);
		goTo(truckXCoord, truckYCoord);
		
		if (brokenAnimation == true && brokenConveyor) {
			controller.doneQueryDoYouHaveGlass(false);
			conveyorFrisbeeGlass = true;
		}
		else {
			rotateToTruck = true;
		}
	}
	/**
	 * 
	 */
	public void playRotateToTrash() {
		System.out.println("[GuiGlassRobot] Received message rotateToTrash.");
				
		goTo(trashXCoord,trashYCoord);
		rotateToTrash = true;
	}
	
	/**
	 * 
	 */
	public void playDropOffAtTrash() {
		System.out.println("[GuiGlassRobot] Received message dropOffAtTrash.");
		
		dropOffAtTrash = true;
		scaleDownDropTrash = true;
	}
	
	/**
	 * Constructor call to drop off glass at the truck.
	 */
	public void playDropOffAtTruck() {
		System.out.println("[GuiGlassRobot] Received message dropOffAtTruck.");
		scaleDownDropTruck = true;
		dropOffAtTruck = true;
	}
	/**
	 * Sets the bin reference(s) in GuiGlassRobot.
	 * @param bin1
	 * @param bin2
	 */
	public void setBins(GuiBin bin1, GuiBin bin2) {
		b1 = bin1;
		b2 = bin2;
	}
	/**
	 * Returns the reference to the first bin
	 * @return bin1
	 */
	public GuiBin getBin1() {
		return b1;
	}
	/**
	 * Returns the reference to the second bin
	 * @return bin2
	 */
	public GuiBin getBin2() {
		return b2;
	}
	/**
	 * Returns the reference to my glass
	 * @return GuiGlass
	 */
	public GuiGlass getGlass() {
		return myGlass;
	}
	/**
	 * Sets my reference to the GuiConveyor.
	 * @param gc
	 */
	public void setConveyor(GuiConveyor gc) {
		myConveyor = gc;
	}
	/**
	 * Sets my reference to the GuiTruck
	 * @param t
	 */
	public void setTruck(GuiTruck t) {
		myTruck = t;
	}
	public void setTrashX(int x) {
		trashXCoord = x;
	}
	public void setTrashY(int y) {
		trashYCoord = y;
	}
	/**
	 * Returns "GuiGlassRobot: "
	 */
	public String getName()	{
		return "GuiGlassRobot: ";
	}
	public void sendToDestination(GuiGlass glass, int direction) {
		if(getPart(FactoryPart.NORTH) != null) { 
			getPart(FactoryPart.NORTH).receiveGlass( glass, direction );
		}
		else if(getPart(FactoryPart.SOUTH) != null) { 
			getPart(FactoryPart.SOUTH).receiveGlass( glass, direction );
		}
		else if(getPart(FactoryPart.EAST) != null) { 
			getPart(FactoryPart.EAST).receiveGlass( glass, direction );
		}
		else if(getPart(FactoryPart.WEST) != null) { 
			getPart(FactoryPart.WEST).receiveGlass( glass, direction );
		}
	}
	/**
	 * Sets the GlassRobotController
	 */
	public void setController( GlassRobotController gr ) {
		controller = gr;
	}
	public void drawUnderGlass( Graphics g, ImageObserver c ) {
		//empty
	}
	public void drawOverGlass( Graphics g, ImageObserver c ) {
		draw((Graphics2D)g, (Component) c);
	}
	public void receiveGlass( GuiGlass glass, int direction ) {
		myGlass = glass;
		glassDirection = direction;
	}
	public void finishPassing() {
		myGlass = null;
		if (dropOffAtConveyor) {
			controller.doneDropOffAtConveyor();
			dropOffAtConveyor = false;
		}
		else if (dropOffAtTruck){
			controller.doneDropOffAtTruck();
			dropOffAtTruck = false;
		}
	}
	/**
	 * Sets the base's x coordinate, which the rest of the robot is based upon
	 */
	public void setX(int x) {
		baseX = x;
		setGlassRobot();
	}
	/**
	 * Sets the base's y coordinate, which the rest of the robot is based upon
	 */
	public void setY( int y ) {
		baseY = y;
		setGlassRobot();
	}
	/**
	 * Returns the base x coordinate
	 */
	public int getX() {
		return (int)baseX;
	}
	/**
	 * Returns the base y coordinate
	 */
	public int getY() {
		return (int)baseY;
	}
	/**
	 * Returns the base width
	 */
	public int getWidth() {
		return BASE_WIDTH;
	}
	/**
	 * Returns the base height
	 */
	public int getHeight() {
		return BASE_HEIGHT;
	}
	/**
	 * Returns the robot rotation (not robot arm angle)
	 */
	public int getRotation() {
		return (int)robotAngle;
	}
	/**
	 * Sets the robot's rotation
	 */
	public void setRotation(int degree) {
		robotAngle = degree;
	}
	/**
	 * Sets the rest of the robot components relative to the base.
	 */
	public void setGlassRobot() {
		setRotatorArm(baseX+(BASE_WIDTH/2-ROTATOR_ARM_HEIGHT/2),baseY+(BASE_WIDTH/2-ROTATOR_ARM_HEIGHT/2));
		setEffector(baseX+(ROTATOR_ARM_WIDTH+20),baseY+(effectorHeight/2-30));
		setExtenderArm(baseX+(BASE_WIDTH/2-ROTATOR_ARM_HEIGHT/2)+60,baseY+(BASE_WIDTH/2-ROTATOR_ARM_HEIGHT/2)+14);
		setEndOfArm(baseX+ROTATOR_ARM_WIDTH+41,baseY+ROTATOR_ARM_HEIGHT/2+4);
	}
	/**
	 * Returns true if it has a glass reference, false otherwise
	 */
	public boolean hasGlass() {
		if (myGlass != null) {
			return true;
		}
		return false;
	}
	/**
	 * Returns glass robot status
	 */
	public String getStatus() {
		if (scaleDownDropConveyor) 
			return "ON, dropping off at conveyor.";
		else if (scaleDownPickBin) 
			return "ON, picking up from bin";
		else if (scaleDownPickConveyor) 
			return "ON, picking up from conveyor";
		else if (rotateToTruck) 
			return "ON, rotating to truck";
		else if (rotateToConveyor) 
			return "ON, rotating to conveyor";
		else if (idle)
			return "ON, rotating to idle position";
		else if (brokenRotateToBin) 
			return "ON, rotating to bin (broken!)";
		else if (binFrisbeeGlass) 
			return "ON, flinging glass!";
		else if (scaleDownDropTruck) 
			return "ON, dropping off at truck";
		else 
			return "OFF";
	}
	public void queryDoYouHaveGlass() {
		controller.doneQueryDoYouHaveGlass( myGlass != null );
	}

	
}
