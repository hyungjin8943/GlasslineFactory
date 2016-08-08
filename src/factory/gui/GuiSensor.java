package factory.gui;

import java.awt.Graphics2D;

import java.awt.geom.AffineTransform;

import java.awt.image.ImageObserver;
import java.util.HashSet;

import javax.swing.ImageIcon;

public class GuiSensor {

    static final int ENTRY = 0, EXIT = 1;

    static final int ENTRY_MARGIN = 5;
    static final int EXIT_MARGIN = 5;

    static private final int SENSOR_MARGIN = 17;
    static private final int CONVEYOR_MARGIN = 6;

    // Is the sensor on?
    boolean isSensorOn;

    // Either ENTER or EXIT
    int sensorType;

    // References to the glass currently underneath the sensor
    HashSet<GuiGlass> detectedGlasses;

    // Images of the sensor
    ImageIcon sensorOff = new ImageIcon("images/sensor_off.png"),
	      sensorOn = new ImageIcon("images/sensor_on.png"),
	      sensorShadow = new ImageIcon("images/sensor_shadow.png");

    // Reference to the conveyor holding this sensor
    GuiConveyor conveyor;

    // Transform used to render the sensor
    AffineTransform at;

    //XXX: remove these
    // Coordinates of this sensor used for rendering
    int xImgPos, yImgPos;

    // Real coordinates of this sensor 
    int xPos, yPos;

    // Width and height of the sensor
    int width, height;

    // Orientation of the sensor
    // Uses same direction as the conveyor (default: EAST)
    int orientation;

    public GuiSensor(GuiConveyor newConveyor, int type) {
	initialize(newConveyor, type);
    }

    public void initialize(GuiConveyor newConveyor, int type) {
	this.conveyor = newConveyor;
	at = new AffineTransform();
	orientation = conveyor.getOrientation();
	sensorType = type;
	detectedGlasses = new HashSet<GuiGlass>();

	if (sensorType == ENTRY) {
	    switch (orientation) {
		case FactoryPart.EAST:
		    width = sensorOff.getIconHeight();
		    height = sensorOff.getIconWidth() - SENSOR_MARGIN;

		    xPos = conveyor.getX() + conveyor.END_INSET + ENTRY_MARGIN;
		    yPos = conveyor.getY() + CONVEYOR_MARGIN;

		    // Then determine the transform for rendering
		    at.setToIdentity();
		    at.translate(xPos - sensorOff.getIconWidth(), conveyor.getY() + CONVEYOR_MARGIN);
		    at.rotate(-Math.PI / 2.0, sensorOff.getIconWidth(), 0);
		    break;

		case FactoryPart.WEST:
		    width = sensorOff.getIconHeight();
		    height = sensorOff.getIconWidth() - SENSOR_MARGIN;

		    xPos = conveyor.getX() + conveyor.getLength() - conveyor.END_INSET - ENTRY_MARGIN - width;
		    yPos = conveyor.getY() + conveyor.THICKNESS - CONVEYOR_MARGIN - height;

		    // Then determine the transform for rendering
		    at.setToIdentity();
		    at.translate(xPos - sensorOff.getIconWidth(), yPos + height - sensorOff.getIconHeight());
		    at.rotate(Math.PI / 2.0, sensorOff.getIconWidth(), sensorOff.getIconHeight());
		    break;

		case FactoryPart.SOUTH:
		    width = sensorOff.getIconWidth() - SENSOR_MARGIN;
		    height = sensorOff.getIconHeight();

		    xPos = conveyor.getX() + conveyor.THICKNESS - CONVEYOR_MARGIN - width;
		    yPos = conveyor.getY() + conveyor.END_INSET + ENTRY_MARGIN;

		    // Then determine the transform for rendering
		    at.setToIdentity();
		    at.translate(xPos - SENSOR_MARGIN, yPos);
		    break;

		case FactoryPart.NORTH:
		    width = sensorOff.getIconWidth() - SENSOR_MARGIN;
		    height = sensorOff.getIconHeight();

		    xPos= conveyor.getX() + CONVEYOR_MARGIN;
		    yPos = conveyor.getY() + conveyor.getLength() - conveyor.END_INSET - ENTRY_MARGIN - height;

		    // Then determine the transform for rendering
		    at.setToIdentity();
		    at.translate(xPos + sensorOff.getIconWidth(), yPos);
		    at.scale(-1.0, 1.0);
		    break;
	    }
	}
	else {
	    switch (orientation) {
		case FactoryPart.EAST:
		    width = sensorOff.getIconHeight();
		    height = sensorOff.getIconWidth() - SENSOR_MARGIN;

		    xPos = conveyor.getX() + conveyor.getLength() - conveyor.END_INSET - EXIT_MARGIN - width;
		    yPos = conveyor.getY() + CONVEYOR_MARGIN;

		    // Then determine the transform for rendering
		    at.setToIdentity();
		    at.translate(xPos - sensorOff.getIconWidth(), conveyor.getY() + CONVEYOR_MARGIN);
		    at.rotate(-Math.PI / 2.0, sensorOff.getIconWidth(), 0);
		    break;

		case FactoryPart.WEST:
		    width = sensorOff.getIconHeight();
		    height = sensorOff.getIconWidth() - SENSOR_MARGIN;

		    xPos = conveyor.getX() + conveyor.END_INSET + EXIT_MARGIN;
		    yPos = conveyor.getY() + conveyor.THICKNESS - CONVEYOR_MARGIN - height;

		    // Then determine the transform for rendering
		    at.setToIdentity();
		    at.translate(xPos - sensorOff.getIconWidth(), yPos + height - sensorOff.getIconHeight());
		    at.rotate(Math.PI / 2.0, sensorOff.getIconWidth(), sensorOff.getIconHeight());
		    break;

		    //XXX
		case FactoryPart.SOUTH:
		    width = sensorOff.getIconWidth() - SENSOR_MARGIN;
		    height = sensorOff.getIconHeight();

		    // Set coordinates and dimension
		    xPos = conveyor.getX() + conveyor.THICKNESS - CONVEYOR_MARGIN - width;
		    yPos = conveyor.getY() + conveyor.getLength() - conveyor.END_INSET - height - EXIT_MARGIN;

		    // Then determine the transform for rendering
		    at.setToIdentity();
		    at.translate(xPos - SENSOR_MARGIN, yPos);
		    break;

		    //XXX
		case FactoryPart.NORTH:
		    width = sensorOff.getIconWidth() - SENSOR_MARGIN;
		    height = sensorOff.getIconHeight();

		    // Set coordinates and dimension
		    xPos = conveyor.getX() + CONVEYOR_MARGIN;
		    //yPos = conveyor.getY() + CONVEYOR_MARGIN + EXIT_MARGIN;
		    yPos = conveyor.getY() + conveyor.END_INSET + EXIT_MARGIN;

		    // Then determine the transform for rendering
		    at.setToIdentity();
		    at.translate(xPos + sensorOff.getIconWidth(), yPos);
		    at.scale(-1.0, 1.0);
		    break;
	    }

	}

    }

    /**XXX
     * Returns true if ...
     */
    public boolean detectGlass(GuiGlass newGlass) {
	boolean newSensorStatus;
	if ( isOverGlass(newGlass) ) {
	    newSensorStatus = true;
	    if (!detectedGlasses.contains(newGlass) && sensorType == EXIT) {
		detectedGlasses.add(newGlass);
		System.out.println("[GuiSensor] GLASS EXITING: " + this);
		conveyor.exitingGlass();
	    }	
	}
	else
	    newSensorStatus = false;

	return newSensorStatus;
    }

    /**
     * Returns true if the given glass is underneath the sensor
     */
    public boolean isOverGlass(GuiGlass targetGlass) {
	int glassX = targetGlass.getXCoord(),
	    glassY = targetGlass.getYCoord(),
	    glassH = targetGlass.getHeight(),
	    glassW = targetGlass.getWidth(),
	    sensorH = sensorOn.getIconHeight(); 

	return ( orientation == FactoryPart.EAST
		&& glassX <= xPos + sensorH
		&& xPos <= glassX + glassW 
		|| orientation == FactoryPart.WEST 
		&& xPos + sensorH >= glassX 
		&& glassX + glassW >= xPos
		|| orientation == FactoryPart.SOUTH 
		&& yPos <= glassY + glassH
		&& glassY <= yPos + sensorH
		|| orientation == FactoryPart.NORTH 
		&& yPos + sensorH >= glassY 
		&& glassY + glassH >= yPos );
    }

    /**
     * Draws sensor image
     */
    public void drawSensor(Graphics2D g2, ImageObserver c) {
	//XXX: SHADOW IMAGE NOT TRANSPARENT
	//g2.drawImage(sensorShadow.getImage(), at, null);
	if (isSensorOn)
	    g2.drawImage(sensorOn.getImage(), at, null);
	else
	    g2.drawImage(sensorOff.getImage(), at, null);
    }

    /**
     * Called by the conveyor to remove the reference to a glass that just left the conveyor
     * from detectedGlass set.
     * @param leftGlass glass to be removed from the list of already detected glasses
     */
    public void glassLeft(GuiGlass leftGlass) {
	if (sensorType == EXIT)
	    detectedGlasses.remove(leftGlass);
    }

    /**
     * Turns on/off the sensor
     */
    public void setSensor(boolean power) {
	isSensorOn = power;
    }

    // Setters and getters
    public int getX() {
	return xPos;
    }

    public int getY() {
	return yPos;
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }
}
