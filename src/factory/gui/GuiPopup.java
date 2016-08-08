/**
 * @author Jay Whang
 * GuiPopUp object for V1.
 */

package factory.gui;

import java.awt.*;
import java.awt.image.*;
import javax.swing.ImageIcon;

import factory.controller.PopupController;

public class GuiPopup extends FactoryPart implements AnimatedPart {

    // Total number of frames needed in a full lowering/raising animation.
    // Set for 20ms timer interval.
    final int NUM_FRAMES = 20; // 20ms * 20 = 0.4s for a full animation

    // One animation update every FRAMES_CYCLE frames.
    final int FRAME_CYCLE = 5;

    // Number of pixels between belt lines + 1 (to include the line itself)
    final int BELT_SPACE = 10;

    // Speed (== number of pixels) of the lift belt
    final int beltSpeed = 1;

    // Speed (== number of pixels) of the glass 
    final int glassSpeed = 1;

    // Boolean values that represent current status of the popup
    boolean isLowering, isRaising, isBeltMoving;

    // Orientation of the Popup 
    // EAST & WEST have the same image
    // SOUTH & NORTH have the same image 
    int orientation;

    // Counter variable for number of frames.
    int frameCount;

    // Counter variable for the belt lines
    int beltCount;

    // Pixel position of the popup
    int xPos, yPos;

    // Belt line
    Rectangle beltLine = new Rectangle(15, 2);

    // Images for popup
    static ImageIcon imgBase = new ImageIcon("images/popup_off.png"),
		     imgSensorBase = new ImageIcon("images/popup_off_sensor.png"),
		     imgBelt = new ImageIcon("images/popup_liftbelt.png");

    // Temporary image for scaling
    BufferedImage beltImage;

    // Direction of lift belt
    // True = up, False = down
    boolean beltDirection;
    
    // true if the sensor is on.  false if not.
    boolean isSensorOn;

    // PopupController for communication with the agent
    PopupController controller;

    // TODO: change the method maybe?
    GuiGlass glass;
    int glassDirection, targetX, targetY;
    int receivingDirection;
    final int GLASS_WIDTH = 50, GLASS_HEIGHT = 67;
    boolean isReceivingGlass, isPassingGlass, hasGlass;

    /*** Non-normative scenario ***/
    private final double shootingSpeed = 1.2;
    boolean isBroken = false;
    boolean isShootingUpGlass = false;
    // Keeps track of the number of frames passed for shooting animation
    int glassShootingCount = 0; 

    public GuiPopup() {
	this(0, 0, FactoryPart.EAST, null);
    }

    /**
     * Constructor
     * @param newX x-coordinate of the popup
     * @param newY y-coordinate of the popup
     * @param direction direction of exiting glass. the orientation of the popup is determined by this.
     * @param popupController reference to the popup controller associated with this popup.
     */
    public GuiPopup(int newX, int newY, int direction, PopupController popupController) {
	frameCount = beltCount = 0;
	xPos = newX;
	yPos = newY;
	beltDirection = true;

	// Target position for glass
	targetX = xPos + (this.getWidth() - GLASS_WIDTH) / 2;
	targetY = yPos + (this.getHeight() - GLASS_HEIGHT) / 2;
	controller = popupController;
	isLowering = isRaising = false; 
	isBeltMoving = false;
	isReceivingGlass = isPassingGlass = hasGlass = isSensorOn = false;
	orientation = direction;
    }

    public void update() {
	// Move belt lines
	if ( isBeltMoving ) {
	    moveBelt(beltDirection);
	}

	/*** Non-normative scenario: Shooting up glass ***/
	if ( isShootingUpGlass ) {
	    if (glassShootingCount >= 40) {
		glassShootingCount = 0;
		isShootingUpGlass = false;
		hasGlass = false;
		glass.setTreatmentCompleted(true);
		glass = null;
		doneShooting();
	    }
	    else {
		// Raises the glass exponentially
		glass.moveVertically((int)(Math.pow(shootingSpeed, glassShootingCount - 1) * (shootingSpeed - 1)));
		//glass.moveVertically(glassShootingCount);
		glassShootingCount++;
	    }
	}
	

	if ( isLowering ) {
	    frameCount--;
	    if (frameCount % FRAME_CYCLE == 0 && hasGlass)
		glass.moveVertically(-1);
	    if ( frameCount <= 0 ) {
		frameCount = 0;
		isLowering = false;
		doneLowering();
	    }
	}

	if ( isRaising ) {
	    frameCount++;
	    if (frameCount % FRAME_CYCLE == 0 && hasGlass) {
		// Don't raise if the popup is shooting glass
		if ( !isShootingUpGlass )
		    glass.moveVertically(1);
	    }
	    if ( frameCount >= NUM_FRAMES ) {
		frameCount = NUM_FRAMES;
		isRaising = false;
		// Don't call animationDone() here when the popup is shooting glass
		if ( !isShootingUpGlass )
		    doneRaising();
	    }
	}

	if ( isReceivingGlass ) {
	    //System.out.println("### targetX == " + targetX + " @@@ GLASS == " + glass + ", $$$ glass.X == " + glass.getXCoord());
	    // If the glass is at target position
	    if ( glassDirection == FactoryPart.NORTH && glass.getYCoord() < targetY 
		    || glassDirection == FactoryPart.SOUTH && glass.getYCoord() > targetY 
		    || glassDirection == FactoryPart.EAST && glass.getXCoord() > targetX
		    || glassDirection == FactoryPart.WEST && glass.getXCoord() < targetX ) {
		// Stop receiving glass
		isReceivingGlass = false;
		// And stop lift belt
		isBeltMoving = false;

		// And determine the direction from which the glass was passed
		/*
		int dir = 0;
		switch (glassDirection) {
		    case FactoryPart.NORTH:
			dir = FactoryPart.SOUTH;
			break;
		    case FactoryPart.SOUTH:
			dir = FactoryPart.NORTH;
			break;
		    case FactoryPart.WEST:
			dir = FactoryPart.EAST;
			break;
		    case FactoryPart.EAST:
			dir = FactoryPart.WEST;
			break;
		}
		*/

		// Then tell the FactoryPart that passed the glass to finish passing.
		if (getPart(receivingDirection) != null) {
		    System.out.println("[GuiPopup] calling finishPassing to dir = " + receivingDirection);
		    getPart(receivingDirection).finishPassing();
		}
		    
	    }

	    else {
		glass.moveLaterally(glassDirection, glassSpeed);

		// If the glass is coming from stations
		if (glassDirection == FactoryPart.NORTH || glassDirection == FactoryPart.SOUTH)
		    isBeltMoving = true;
		else
		    isBeltMoving = false;
	    }
	}

	if ( isPassingGlass ) {

	    switch (glassDirection) {
		case FactoryPart.EAST:
		    // If the glass already reached the end
		    if ( glass.getXCoord() + glass.getWidth() >= getX() + getWidth() ) {
			isPassingGlass = false;
			hasGlass = false;
			
			// Finally pass the glass
			System.out.println("[GuiPopup] Calling EAST's receiveGlass with glassID = " + glass);
			adjacentParts[FactoryPart.EAST].receiveGlass(glass, FactoryPart.WEST);

			// And set the glass reference to null
			System.out.println("[GuiPopup] GLASS SET TO NULL! glassDir = EAST, id = " + this);
			glass = null;
		    }
		    // Otherwise move the glass
		    else
			glass.moveLaterally(glassDirection, glassSpeed);
		    break;

		    // Don't really need this case, but just in case
		case FactoryPart.WEST:
		    // If the glass already reached the end
		    if ( glass.getXCoord() <= getX() ) {
			isPassingGlass = false;
			hasGlass = false;

			System.out.println("[GuiPopup] Calling WEST's receiveGlass with glassID = " + glass);
			adjacentParts[FactoryPart.WEST].receiveGlass(glass, FactoryPart.EAST);
			System.out.println("[GuiPopup] GLASS SET TO NULL! glassDir = WEST, id = " + this);
			glass = null;
		    }
		    else
			glass.moveLaterally(glassDirection, glassSpeed);
		    break;

		    //XXX
		case FactoryPart.NORTH:
		    if ( glass.getYCoord() <= getY() ) {
			isPassingGlass = false;
			hasGlass = false;

			System.out.println("[GuiPopup] Calling NORTH's receiveGlass with glassID = " + glass);
			adjacentParts[FactoryPart.NORTH].receiveGlass(glass, FactoryPart.SOUTH);
			System.out.println("[GuiPopup] GLASS SET TO NULL! glassDir = NORTH, id = " + this);
			glass = null;
		    }
		    else
			glass.moveLaterally(glassDirection, glassSpeed);
		    break;

		    //XXX
		case FactoryPart.SOUTH:
		    // If the glass already reached the end
		    if ( glass.getYCoord() + glass.getHeight() >= getY() + getHeight() ) {
			isPassingGlass = false;
			hasGlass = false;

			System.out.println("[GuiPopup] Calling SOUTH's receiveGlass with glassID = " + glass);
			adjacentParts[FactoryPart.SOUTH].receiveGlass(glass, FactoryPart.NORTH);
			System.out.println("[GuiPopup] GLASS SET TO NULL! glassDir = SOUTH, id = " + this);
			glass = null;
		    }
		    else glass.moveLaterally(glassDirection, glassSpeed);
		    break;

		default:
		    // This shouldn't happen.
	    }
	}
    }

    /**
     * If up is true, then the lift belt moves up.
     * If up is false, then the lift belt moves down.
     **/
    private void moveBelt(boolean up) {
	// If the belt isn't moving, do nothing.
	if ( !isBeltMoving )
	    return;

	if ( up ) {
	    beltCount -= beltSpeed;
	    if ( beltCount < 0 ) {
		beltCount = 9;
	    }
	}
	else {
	    // Increment beltCount
	    beltCount += beltSpeed;
	    if ( beltCount > 9 ) {
		beltCount = 0;
	    }
	}
    }

    /**
     * Draws belt lines
     **/
    private void drawBeltLines(Graphics2D g2) {

	Color tempColor = g2.getColor();
	g2.setColor(new Color(13, 13, 13));
	for (int i = 0; i < 7; i++) {
	    beltLine.setLocation( (imgBelt.getIconWidth() - (int)beltLine.getWidth()) / 2,
		    i * BELT_SPACE + beltCount );
	    g2.fill(beltLine);
	}

	// Adjust belt line height for the last one
	if ( beltCount == 9 )
	    beltLine.setSize((int)beltLine.getWidth(), 1);
	beltLine.setLocation( (imgBelt.getIconWidth() - (int)beltLine.getWidth()) / 2,
		7 * BELT_SPACE + beltCount );

	g2.fill(beltLine);	
	beltLine.setSize((int)beltLine.getWidth(), 2);

	g2.setColor(tempColor);
    }

    private void drawLiftBelt(Graphics2D g2, ImageObserver c) {
	beltImage = new BufferedImage(imgBelt.getIconWidth(), imgBelt.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
	Graphics2D beltG2 = beltImage.createGraphics();
	beltG2.drawImage(imgBelt.getImage(), 0, 0, null);
	drawBeltLines(beltG2);

	int verticalStretch = (int)(frameCount / FRAME_CYCLE * (double)beltImage.getHeight() / beltImage.getWidth());
	g2.drawImage(beltImage, 
		xPos + ( imgBase.getIconWidth() - (beltImage.getWidth() + 2 * frameCount / FRAME_CYCLE) ) / 2, 
		yPos + ( imgBase.getIconHeight() - (beltImage.getHeight() + 2 * verticalStretch) ) / 2,
		beltImage.getWidth() + 2 * frameCount / FRAME_CYCLE,
		beltImage.getHeight() + 2 * verticalStretch,
		c);

    }

    public void draw(Graphics g, ImageObserver c) {
 
	Graphics2D g2 = (Graphics2D)g;

	// Draw the base image
	if ( isSensorOn )
		g2.drawImage(imgSensorBase.getImage(), xPos, yPos, c);
	else
		g2.drawImage(imgBase.getImage(), xPos, yPos, c);

	// Draw the scaled lift belt image 
	drawLiftBelt(g2, c);

    }


    public void drawUnderGlass(Graphics g, ImageObserver c) {
	draw(g, c);   	
    }

    public void drawOverGlass(Graphics g, ImageObserver c) {
	// Nothing to draw over glass
    }

    public void doneLowering() {
	controller.animationDone();
    }

    public void doneRaising() {
	controller.animationDone();
    }

    public void doneShooting() {
	controller.animationDone();
    }

    public void lowerPopup() {
	isLowering = true;
	isRaising = false;
    }

    public void raisePopup() {
	isRaising = true;
	isLowering = false;
    }

    //XXX
    public void passToConveyor() {
	System.out.println("[GuiPopup] Popup is now PASSING glass to CONVEYOR, glass = " + glass);
	isPassingGlass = true;
	glassDirection = FactoryPart.EAST;
    }

    //XXX
    public void passToNorth() {
	System.out.println("[GuiPopup] Popup is now PASSING glass to NORTH, glass = " + glass);
	isPassingGlass = true;
	glassDirection = FactoryPart.NORTH;
	beltDirection = true; // UP
	isBeltMoving = true;
    }

    //XXX
    public void passToSouth() {
	System.out.println("[GuiPopup] Popup is now PASSING glass to SOUTH, glass = " + glass);
	isPassingGlass = true;
	glassDirection = FactoryPart.SOUTH;
	beltDirection = false; // DOWN
	isBeltMoving = true;
    }

    public void setState(String msg) {

	if ( msg.equals("Raising Popup") ) {
	    raisePopup();
	}

	else if ( msg.equals("Lowering Popup") ) {
	    lowerPopup();
	}

	else if ( msg.equals("Passing to Station 1") ) {
	    passToNorth();
	}

	else if ( msg.equals("Passing to Station 2") ) {
	    passToSouth();
	}

	else if ( msg.equals("Passing to Conveyor") ) {
	    passToConveyor();
	}

	else if ( msg.equals("Shooting Glass Up") ) {
	    shootUpGlass();
	}

	else {
	    // Shouldn't happen
	}

    }

    // TODO
    public void receiveGlass(GuiGlass newGlass, int direction) {
	System.out.println("[GuiPopup] receiveGlass(), id = " + this + ", glassID = " + newGlass + ", dir = " + direction);
	receivingDirection = direction;
	glass = newGlass;
	isReceivingGlass = hasGlass = isSensorOn = true;
	
	// Adjust the target position according to the glass size
	int coeff = 0;
	if (frameCount == 30)
	    coeff = 1;

	//targetX = xPos + (this.getWidth() - (glass.getWidth() + coeff * 2 * NUM_FRAMES / FRAME_CYCLE)) / 2;
	targetX = xPos + (this.getWidth() - glass.getWidth()) / 2;
	System.out.println("[GuiPopup] relative new targetX = " + (targetX - xPos) + " , glass.getWidth() = " + glass.getWidth());
	//targetY = yPos + (this.getHeight() - (glass.getHeight() + coeff * 2 * NUM_FRAMES / FRAME_CYCLE)) / 2;
	targetY = yPos + (this.getHeight() - glass.getHeight()) / 2;

	switch (direction) {
	    case FactoryPart.WEST:
		glassDirection  = FactoryPart.EAST;
		break;
	    case FactoryPart.EAST:
		glassDirection  = FactoryPart.WEST;
		break;
	    case FactoryPart.NORTH:
		glassDirection  = FactoryPart.SOUTH;
		beltDirection = false;
		break;
	    case FactoryPart.SOUTH:
		glassDirection  = FactoryPart.NORTH;
		beltDirection = true;
		break;
	    default:
		// This shouldn't happen.
		break;
	}
    }

    public void shootUpGlass() {
	System.out.println("[GuiPopup] SHOOTING UP GLASS");
	// If the popup doesn't have any glass, do nothing.
	if ( !hasGlass() )
	    return;

	isShootingUpGlass = true;
	isRaising = true;
    }

    public void finishPassing() { isBeltMoving = false;
	isSensorOn = false;
	controller.animationDone();
    }

    // Getters
    public void setX(int x) {
	targetX += x - xPos;
	xPos = x;
    }

    public void setY(int y) {
	targetY += y - yPos;
	yPos = y;
    }

    public void setRotation(int degree) {
	// TODO
    }

    public int getOrientation() {
	return orientation;
    }

    public void setController(PopupController newPC) {
    	controller = newPC;
    }

    // Setters
    public int getX() {
	return xPos;
    }

    public int getY() {
	return yPos;
    }

    public int getHeight() {
	return imgBase.getIconHeight();
    }

    public int getWidth() {
	return imgBase.getIconWidth();
    }

    public int getRotation() {
	// TODO
	return 0;
    }

    public boolean isBeltMoving() {
	return isBeltMoving;
    }

    public boolean isRaised() {
	return (frameCount == 30);
    }

    public String getStatus() {
	if (isSensorOn) {
	    if (isPassingGlass && isReceivingGlass)
		return "On, Passing & Receiving";
	    else if (isPassingGlass)
		return "On, Passing";
	    else if (isReceivingGlass)
		return "On, Receiving";
	    else if (isLowering)
		return "On, Lowering";
	    else if (isRaising)
		return "On, Raising";
	    else
		return "On, Idle";
	}
	else
	    return "On, Idle";
    }

    public boolean hasGlass() {
	return hasGlass;
    }

    /**
     * Overrides FactoryPart's setBreak
     */
    public void setBreak(boolean flag) {
	isBroken = flag;
	controller.setBreak(isBroken);
    }

    /**
     * Overrides FactoryPart's isBroken
     */
    public boolean isBroken() {
	return isBroken;
    }
}

