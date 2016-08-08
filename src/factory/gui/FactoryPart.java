/**
 * @author Jay Whang
 * FactoryPart abstract class for V0.
 */

package factory.gui;

import java.awt.Graphics;
import java.awt.image.ImageObserver;

/**
 * Interface that all components in the factory should implement
 **/
public abstract class FactoryPart {

    // Saves references to the four neighboring FactoryParts
    protected FactoryPart[] adjacentParts;

    // Four directions to be used by GuiPopUp and the main panel
    // to represent four directions around a FactoryPart object.
    public static final int NORTH = 0, WEST = 1, SOUTH = 2, EAST = 3;

    private static int numFactoryParts = 0;
    private int partID;

    /**
     * Default blank constructor.
     * Automatically sets each value to null.
     */
    public FactoryPart() {
	numFactoryParts++;
	partID = numFactoryParts;
	adjacentParts = new FactoryPart[4];
    }

    /**
     * Render the part of this FactoryPart that is under glass.
     */
    public abstract void drawUnderGlass(Graphics g, ImageObserver c);

    /**
     * Render the part of this FactoryPart that is over glass.
     */
    public abstract void drawOverGlass(Graphics g, ImageObserver c); 

    /**
     * Adds a new neighboring FactoryPart to the given direction.
     * The value of "direction" parameter has to be one of the four directions defined above.
     */
    public void setPart(FactoryPart newPart, int direction) {
	adjacentParts[direction] = newPart;
    }

    /**
     * Getter method that returns a neighboring object in a given direction.
     * The value of "direction" parameter has to be one of the four directions defined above.
     */
    public FactoryPart getPart(int direction) {
	return adjacentParts[direction];
    }

    /**
     * Receive a glass object from the machine located "direction" to this one.
     * "direction" must be one of the four directions in FactoryPart
     */
    public abstract void receiveGlass(GuiGlass glass, int direction);

    /**
     * Gets called by the machine that receives glass from this one
     * to signify that the receiving machine finished receiving glass object
     * so that this component can call its agent's doneAnimation.
     */
    public abstract void finishPassing();

    // Setters
    public abstract void setX(int x);

    public abstract void setY(int y);

    /**
     * Returns a string that describes the current status of this FactoryPart
     */
    public abstract String getStatus();

    /**
     * Returns true if this FactoryPart has a glass.  False otherwise.
     */
    public abstract boolean hasGlass();

    /**
     * Returns the total number of FactoryParts instantiated
     */
    static public int getNumFactoryParts() {
	return numFactoryParts;
    }

    /**
     * Returns the unique ID of this FactoryPart
     */
    public int getID() {
	return partID;
    }

    // Getters
    public abstract int getX();

    public abstract int getY();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getRotation(); //in degree

    public abstract void setRotation(int degree); //in degree

    /**
     * Breaks the factory part if flag == true.
     * Fixes it otherwise.
     * @param flag true if this factory part is to be broken. false otherwise.
     */
    public abstract void setBreak(boolean flag);
    
    /**
     * Returns true if this factory part is broken.
     * Returns false otherwise.
     */
    public abstract boolean isBroken();

}
