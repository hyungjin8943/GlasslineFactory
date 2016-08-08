/**
 * @author: Jay Whang
 * GuiConveyor class for V1
 */
package factory.gui;

import java.awt.*;

import java.awt.geom.AffineTransform;

import java.awt.image.*;
import javax.swing.ImageIcon;
import java.util.*;

import factory.controller.ConveyorController;

public class GuiConveyor extends FactoryPart implements AnimatedPart {

    // Width/height of the conyveyor
    // Also needed by sensor
    static public final int THICKNESS = 118;
    static public final int END_INSET = 22;

    // XXX
    private final int BELT_SPACE = 40;
    private final int BELT_INSET = 17;
    private final int BELT_END_INSET = 6;
    private final int BELT_THICKNESS = 1;

    // XXX
    private final int SENSOR_INSET = 10;

    // ConveyorController
    ConveyorController controller; 

    // XXX
    int beltCount = 0;

    // Belt line
    Rectangle beltLine;

    // Sensors
    GuiSensor entrySensor, exitSensor;

    // X and Y coordinates
    int xPos, yPos;

    // True if the conveyor is moving
    boolean isMoving;

    // True if the conveyor is passing
    boolean isPassing;

    // XXX
    boolean isReceiving;

    // Flags used for non-normative scenario
    boolean isSensorBroken;

    // ArrayList of glasses being handled by the conveyor
    Queue<GuiGlass> glassList;

    // List of glasses already been passed
    // Used to determine when to turn off the exit sensor
    ArrayList<GuiGlass> passedGlassList;

    // Receiving glass
    GuiGlass receivingGlass;

    // Images for the conveyor
    static ImageIcon imgEnd = new ImageIcon("images/conveyor_end.png"),
		     imgEndshadow = new ImageIcon("images/conveyor_endshadow.png"),
		     imgMid = new ImageIcon("images/conveyor_mid.png");
    Image leftEndImage = imgEnd.getImage(), rightEndImage = imgEnd.getImage();

    // Rotated image
    BufferedImage imgConveyor;

    // Orientation of the conveyor. Designated by the glass' moving direction
    int orientation;

    // Length of the conveyor
    int length;

    // Blank constructor
    public GuiConveyor() {
	this(0, 0, 120, FactoryPart.EAST);
    }

    // Constructor with xcoord, ycoord, length, and the direction
    public GuiConveyor(int newX, int newY, int len, int newDir) {
	setup(newX, newY, len, newDir);
    }

    // Constructor with xcoord, ycoord, length, and the direction
    public void setup(int newX, int newY, int len, int newDir) {
	isSensorBroken = false; 
	orientation = newDir;
	xPos = newX;
	yPos = newY;
	length = len;
	isReceiving = isPassing = false;
	glassList = new LinkedList<GuiGlass>();
	passedGlassList = new ArrayList<GuiGlass>();

	// Create sensors
	entrySensor = new GuiSensor(this, GuiSensor.ENTRY);
	exitSensor = new GuiSensor(this, GuiSensor.EXIT);

    }

    private void drawBase() {
	Graphics2D g2;
	AffineTransform at = new AffineTransform();

	switch (orientation) {

	    // 0 degrees
	    case FactoryPart.EAST:
		imgConveyor = new BufferedImage(length, THICKNESS,
			BufferedImage.TYPE_4BYTE_ABGR);
		g2 = imgConveyor.createGraphics();

		// Body
		at.setToIdentity();
		at.translate(imgEnd.getIconHeight(), THICKNESS);
		at.rotate(-Math.PI / 2.0);
		at.scale(1.0, (double) length / imgMid.getIconHeight());
		g2.drawImage(imgMid.getImage(), at, null);

		// Left end
		at.setToIdentity();
		at.translate(0, THICKNESS);
		at.rotate(-Math.PI / 2.0);
		g2.drawImage(leftEndImage, at, null);

		// Right end
		at.setToIdentity();
		at.translate(length, 0);
		at.rotate(Math.PI / 2.0);
		g2.drawImage(rightEndImage, at, null);
		break;

		// 180 degrees
	    case FactoryPart.WEST:
		imgConveyor = new BufferedImage(length, THICKNESS,
			BufferedImage.TYPE_4BYTE_ABGR);
		g2 = imgConveyor.createGraphics();

		// Body
		at.setToIdentity();
		at.translate(imgEnd.getIconHeight(), THICKNESS);
		at.rotate(-Math.PI / 2.0);
		at.scale(1.0, (double) length / imgMid.getIconHeight());
		g2.drawImage(imgMid.getImage(), at, null);

		// Left end
		at.setToIdentity();
		at.translate(0, THICKNESS);
		at.rotate(-Math.PI / 2.0);
		g2.drawImage(rightEndImage, at, null);

		// Right end
		at.setToIdentity();
		at.translate(length, 0);
		at.rotate(Math.PI / 2.0);
		g2.drawImage(leftEndImage, at, null);
		break;

		// 270 degrees
	    case FactoryPart.SOUTH:
		imgConveyor = new BufferedImage(THICKNESS, length,
			BufferedImage.TYPE_4BYTE_ABGR);
		g2 = imgConveyor.createGraphics();

		// Body
		at.setToIdentity();
		at.scale(1.0, (double) length / imgMid.getIconHeight());
		g2.drawImage(imgMid.getImage(), at, null);

		// Top end
		g2.drawImage(leftEndImage, 0, 0, null);

		// Bottom end
		at.setToIdentity();
		at.translate(0, length);
		at.scale(1.0, -1.0);
		g2.drawImage(rightEndImage, at, null);
		break;

		// 90 degrees
	    case FactoryPart.NORTH:
		imgConveyor = new BufferedImage(THICKNESS, length,
			BufferedImage.TYPE_4BYTE_ABGR);
		g2 = imgConveyor.createGraphics();

		// Body
		at.setToIdentity();
		at.scale(1.0, (double) length / imgMid.getIconHeight());
		g2.drawImage(imgMid.getImage(), at, null);

		// Top end
		g2.drawImage(rightEndImage, 0, 0, null);

		// Bottom end
		at.setToIdentity();
		at.translate(0, length);
		at.scale(1.0, -1.0);
		g2.drawImage(leftEndImage, at, null);
		break;
	}

    }

    private void changeEndImage() {
	// Change the end image from endshadow to end
	// when new things are added to one end of the conveyor
	if (orientation == FactoryPart.EAST) {
	    if (getPart(FactoryPart.EAST) == null)
		rightEndImage = imgEndshadow.getImage();
	    else
		rightEndImage = imgEnd.getImage();

	    if (getPart(FactoryPart.WEST) == null)
		leftEndImage = imgEndshadow.getImage();
	    else
		leftEndImage = imgEnd.getImage();
	} else if (orientation == FactoryPart.WEST) {
	    if (getPart(FactoryPart.WEST) == null)
		rightEndImage = imgEndshadow.getImage();
	    else
		rightEndImage = imgEnd.getImage();

	    if (getPart(FactoryPart.EAST) == null)
		leftEndImage = imgEndshadow.getImage();
	    else
		leftEndImage = imgEnd.getImage();
	} else if (orientation == FactoryPart.SOUTH) {
	    if (getPart(FactoryPart.SOUTH) == null)
		rightEndImage = imgEndshadow.getImage();
	    else
		rightEndImage = imgEnd.getImage();

	    if (getPart(FactoryPart.NORTH) == null)
		leftEndImage = imgEndshadow.getImage();
	    else
		leftEndImage = imgEnd.getImage();
	} else {
	    if (getPart(FactoryPart.NORTH) == null)
		rightEndImage = imgEndshadow.getImage();
	    else
		rightEndImage = imgEnd.getImage();

	    if (getPart(FactoryPart.SOUTH) == null)
		leftEndImage = imgEndshadow.getImage();
	    else
		leftEndImage = imgEnd.getImage();
	}
    }

    private void drawBeltLines(BufferedImage image) {
	Graphics2D g2 = image.createGraphics();
	Color tempColor = g2.getColor();
	g2.setColor(new Color(13, 13, 13));

	int enter, exit;
	enter = exit = 0;
	switch (orientation) {
	    case FactoryPart.EAST:
		beltLine = new Rectangle(BELT_THICKNESS, THICKNESS - 2 * BELT_INSET);
		if (getPart(FactoryPart.EAST) == null)
		    exit++;
		if (getPart(FactoryPart.WEST) == null)
		    enter++;
		break;
	    case FactoryPart.WEST:
		beltLine = new Rectangle(BELT_THICKNESS, THICKNESS - 2 * BELT_INSET);
		if (getPart(FactoryPart.WEST) == null)
		    exit++;
		if (getPart(FactoryPart.EAST) == null)
		    enter++;
		break;

	    case FactoryPart.SOUTH:
		beltLine = new Rectangle(THICKNESS - 2 * BELT_INSET, BELT_THICKNESS);
		if (getPart(FactoryPart.SOUTH) == null)
		    exit++;
		if (getPart(FactoryPart.NORTH) == null)
		    enter++;
		break;

	    case FactoryPart.NORTH:
		beltLine = new Rectangle(THICKNESS - 2 * BELT_INSET, BELT_THICKNESS);
		if (getPart(FactoryPart.NORTH) == null)
		    exit++;
		if (getPart(FactoryPart.SOUTH) == null)
		    enter++;
		break;
	}

	int linePos; // X or Y (depending on the orientation) of the current belt line being drawn
	linePos = beltCount;
	while (linePos + (BELT_THICKNESS - 1) < length - BELT_END_INSET * (enter + exit)) {

	    switch (orientation) {
		case FactoryPart.EAST:
		    beltLine.setLocation(linePos + enter * BELT_END_INSET, BELT_INSET);
		    break;
		case FactoryPart.WEST:
		    beltLine.setLocation(linePos + exit  * BELT_END_INSET, BELT_INSET);
		    break;
		case FactoryPart.SOUTH:
		    beltLine.setLocation(BELT_INSET, linePos + enter * BELT_END_INSET);
		    break;
		case FactoryPart.NORTH:
		    beltLine.setLocation(BELT_INSET, linePos + exit * BELT_END_INSET);
		    break;

	    }
	    g2.fill(beltLine);
	    linePos += BELT_SPACE;
	}
	g2.setColor(tempColor);
    }

    public void update() {
	// Update conveyor end images
	changeEndImage();

	// If the conveyor is not moving, exit
	if ( !isMoving ) {
	    return;
	}

	// Move belt lines
	if (orientation == FactoryPart.EAST
		|| orientation == FactoryPart.SOUTH) {
	    beltCount++;
	    if (beltCount >= BELT_SPACE)
		beltCount = 0;
	} else {
	    beltCount--;
	    if (beltCount <= 0)
		beltCount = BELT_SPACE;
	}

	// If done receiving, then signal the previous FactoryPart
	if (isReceiving) { 
	    switch (orientation) {
		case FactoryPart.EAST:
		    if (receivingGlass.getXCoord() >= entrySensor.getX() + entrySensor.getWidth() 
			    + SENSOR_INSET - glassList.peek().getWidth()) {
			getPart(FactoryPart.WEST).finishPassing();
			isReceiving = false;
			    }
		    break;
		case FactoryPart.WEST:
		    if (receivingGlass.getXCoord() <= entrySensor.getX() - SENSOR_INSET) {
			getPart(FactoryPart.EAST).finishPassing();
			isReceiving = false;
		    }
		    break;
		case FactoryPart.SOUTH:
		    if (receivingGlass.getYCoord() >= entrySensor.getY() + entrySensor.getWidth()
			    + SENSOR_INSET - glassList.peek().getHeight()) {
			getPart(FactoryPart.NORTH).finishPassing();
			isReceiving = false;
			    }
		    break;
		case FactoryPart.NORTH:
		    if (receivingGlass.getYCoord() <= entrySensor.getY() - SENSOR_INSET) {
			getPart(FactoryPart.SOUTH).finishPassing();
			isReceiving = false;
		    }
		    break;
	    }
	}

	if (isPassing) {
	    Rectangle collisionRect = null;
	    // Create a temporary bounding box for collision detection
	    switch (orientation) {
		case FactoryPart.EAST: 
		    collisionRect = new Rectangle(xPos + length, yPos, 2, THICKNESS);
		    break;
		case FactoryPart.WEST:
		    collisionRect = new Rectangle(xPos - 2, yPos, 2, THICKNESS);
		    break;
		case FactoryPart.SOUTH:
		    collisionRect = new Rectangle(xPos, yPos + length, THICKNESS, 2);
		    break;
		case FactoryPart.NORTH:
		    collisionRect = new Rectangle(xPos, yPos - 2, THICKNESS, 2);
		    break;
	    }
	    if ( glassList.peek() == null )
	    	System.out.println("[GuiConveyor] SOMETHING IS WRONG! glassList is empty!");
	    if ( glassList.peek().getGlassPlaceholder().intersects(collisionRect) ) {
		isPassing = false;
		GuiGlass tempGlass = glassList.remove();
		passedGlassList.add(tempGlass);

		int dir = 0;
		switch (orientation) {
		    case FactoryPart.SOUTH: dir = FactoryPart.NORTH; break; 
		    case FactoryPart.NORTH: dir = FactoryPart.SOUTH; break; 
		    case FactoryPart.WEST: dir = FactoryPart.EAST; break; 
		    case FactoryPart.EAST: dir = FactoryPart.WEST; break; 
		}

		System.out.println("[GuiConveyor] calling my direction's receiveGlass() with glassID = " + tempGlass);
		getPart(orientation).receiveGlass(tempGlass, dir);
		exitSensor.glassLeft(tempGlass);
	    }
	}

	// Move glasses and refresh sensors
	boolean isEntrySensorOn = false;
	boolean isExitSensorOn = false;

	// XXX Concurrent modification exception.  WHY?
	//for (GuiGlass g : glassList) {
	for (int i = 0; i < glassList.size(); i++ ) {
	    GuiGlass g = ((LinkedList<GuiGlass>)glassList).get(i);
	    g.moveLaterally(orientation, 1);
	    if (entrySensor.detectGlass(g))
		isEntrySensorOn = true;
	    if (exitSensor.detectGlass(g))
		isExitSensorOn = true;
	}

	// Remove useless glasses
	for (int i = passedGlassList.size() - 1; i >= 0; i--) {
	    if (exitSensor.isOverGlass(passedGlassList.get(i))) 
		isExitSensorOn = true;

	    if (entrySensor.isOverGlass(passedGlassList.get(i))) 
		isEntrySensorOn = true;

	    if ( !(isExitSensorOn || isEntrySensorOn) )
		passedGlassList.remove(i);
	}

	// Finally change the sensor status
	exitSensor.setSensor(isExitSensorOn);

	// Non-normative scenario for entry sensor
	if (isSensorBroken) {
	    entrySensor.setSensor(true);
	}
	else {
	    entrySensor.setSensor(isEntrySensorOn);
	}
    }

    public void finishPassing() {
    	System.out.println("[GuiConveyor] id = " + this + ", FINISHPASSING() RECEIVED");
    	
	controller.animationDone();


    }

    // Pass the glass to robot arm
    public void passToRobotArm() {
	GuiGlass tempGlass = glassList.remove();

	getPart(orientation).receiveGlass(tempGlass, orientation);

    }

    public void drawUnderGlass(Graphics g, ImageObserver c) {
	Graphics2D g2 = (Graphics2D) g;

	// Draw base image
	drawBase();

	// Draw belt lines
	drawBeltLines(imgConveyor);

	// Then finally render the completed image to the main panel
	g2.drawImage(imgConveyor, xPos, yPos, c);
	
    }

    public void drawOverGlass(Graphics g, ImageObserver c) {
	// Draw sensor shadows
	entrySensor.drawSensor((Graphics2D)g, c);
	exitSensor.drawSensor((Graphics2D)g, c);
    }

    public void setRotation(int newDegree) {
	// TODO
    }

    public int getRotation() {
	// TODO
	return 0;
    }

    // Returns the length of the conveyor
    public int getLength() {
	return length;
    }

    // Setters and getters
    
    public boolean isMoving() {
	return isMoving;
    }

    public void setX(int x) {
	xPos = x;
	entrySensor.initialize(this, GuiSensor.ENTRY);
	exitSensor.initialize(this, GuiSensor.EXIT);
    }

    public void setY(int y) {
	yPos = y;
	entrySensor.initialize(this, GuiSensor.ENTRY);
	exitSensor.initialize(this, GuiSensor.EXIT);
    }

    public void setLength(int newLength) {
	length = newLength;
    }

    public int getX() {
	return xPos;
    }

    public int getY() {
	return yPos;
    }

    public int getHeight() {
	if (orientation == FactoryPart.EAST || orientation == FactoryPart.WEST)
	    return THICKNESS;
	else
	    return length;
    }

    public int getWidth() {
	if (orientation == FactoryPart.EAST || orientation == FactoryPart.WEST)
	    return length;
	else
	    return THICKNESS;
    }

    public int getNumGlasses() {
	return glassList.size();
    }

    // For sensor
    public int getOrientation() {
	return orientation;
    }

    // Controller communication
    public void doTurnOn() {
	isMoving = true;
    }

    public void doTurnOff() {
	isMoving = false;
    }

    public void receiveGlass(GuiGlass glass, int direction) {
    	System.out.println("[GuiConveyor] RECEIVED GLASS, id = " + this);
	glassList.add(glass);
	receivingGlass = glass;
	isReceiving = true;
    }

    public void exitingGlass() {
	controller.glassExiting();
    }

    /**
     * Sets the controller for this GuiConveyor
     */
    public void setController(ConveyorController controller) {
	// TODO Auto-generated method stub
	this.controller = controller;
    }

    /**
     * Returns the controller object for this GuiConveyor
     */
    public ConveyorController getController() {
	return this.controller;
    }

    /**
     * Pass glass to next machine
     */
    public void passGlass() {
    System.out.println("GUI CONVEYOR PASSGLASS CALLED");
	isPassing = true;
    }

    public String getStatus() {
	if (isMoving) {
	    if (isReceiving && isPassing) 
		return "On, Passing & Receiving";
	    else if (isReceiving)
		return "On, Receiving";
	    else if (isPassing)
		return "On, Passing";
	    else
		return "On, Moving";
	}
	else
	    return "Off, Idle";
    }

    public boolean hasGlass() {
	return !glassList.isEmpty();
    }

    public void setBreak(boolean flag) {
	controller.setEntrySensorDisabled(flag);
    }

    public void toggleMoving() {
	controller.toggleConveyorState();
    }

    /**
     * [Non-normative]
     * Force turn off/on the entry sensor 
     * 
     * @param power true if broken; false otherwise.
     */
    public void setSensorBroken(boolean flag) {
	isSensorBroken = flag;
    }

    /**
     * [Non-normative]
     * Returns true if the entry sensor of this conveyor is broken.
     * Returns false otherwise.
     */
    public boolean isBroken() {
	return isSensorBroken;
    }

}
