/**
 * @author Jay Whang
 * GuiPart object for V0.
 */

package factory.gui;

import java.awt.*;
import java.awt.image.*;

public class GuiPart extends FactoryPart implements AnimatedPart {

    final int NUM_FRAMES = 40;

    int frameCount;
    int glassDirection;

    boolean isReceiving, isPassing, hasGlass;

    Rectangle underGlass, overGlass;

    GuiGlass glass;

    int xpos, ypos;
    int width, height;

    public GuiPart() {
	this(0, 0, 80, 80);
    }

    public GuiPart(int n_xpos, int n_ypos, int n_width, int n_height) {
	hasGlass = false;
	xpos = n_xpos;
	ypos = n_ypos;
	width = n_width;
	height = n_height;
	frameCount = 0;
	underGlass = new Rectangle(xpos, ypos, width, height);
	overGlass = new Rectangle(xpos + (width - (width / 3)) / 2, ypos + (height - (height / 3)) / 2, width / 3, height / 3);
    }

    public void update() {
	if ( isReceiving ) {
	    // If the glass is at target position
	    if ( frameCount >= NUM_FRAMES ) {
		frameCount = 0;

		// Stop receiving glass
		isReceiving = false;

		// And determine the direction from which the glass was passed
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

		// Then tell the FactoryPart that passed the glass to finish passing.
		if (getPart(dir) != null) {
		    getPart(dir).finishPassing();
		}
	    }
	    else {
		frameCount++;
	    }
	}

	if ( isPassing ) {
	    int dx, dy;
	    dx = dy = 0;

	    switch (glassDirection) {
		case FactoryPart.EAST:
		    // If the glass already reached the end
		    if ( frameCount >= NUM_FRAMES ) {
			adjacentParts[FactoryPart.EAST].receiveGlass(glass, FactoryPart.WEST);
			frameCount = 0;
			isPassing = false;
		    }
		    else {
			frameCount++;
		    }
		    break;

		    // Don't really need this case, but just in case
		case FactoryPart.WEST:
		    // If the glass already reached the end
		    if ( frameCount >= NUM_FRAMES ) {
			adjacentParts[FactoryPart.WEST].receiveGlass(glass, FactoryPart.EAST);
			frameCount = 0;
			isPassing = false;
		    }
		    else {
			frameCount++;
		    }
		    break;

		case FactoryPart.NORTH:
		    // If the glass already reached the end
		    if ( frameCount >= NUM_FRAMES ) {
			adjacentParts[FactoryPart.NORTH].receiveGlass(glass, FactoryPart.SOUTH);
			frameCount = 0;
			isPassing = false;
		    }
		    else {
			frameCount++;
		    }
		    break;

		case FactoryPart.SOUTH:
		    // If the glass already reached the end
		    if ( frameCount >= NUM_FRAMES ) {
			adjacentParts[FactoryPart.SOUTH].receiveGlass(glass, FactoryPart.NORTH);
			frameCount = 0;
			isPassing = false;
		    }
		    else {
			frameCount++;
		    }
		    break;

		default:
		    // This shouldn't happen.
	    }
	    glass.moveLaterally(dx, dy);
	}
    }

    public void drawUnderGlass(Graphics g, ImageObserver c) {
	Graphics2D g2 = (Graphics2D)g;
	if ( isReceiving || isPassing )
	    g2.setColor(Color.PINK);
	else
	    g2.setColor(Color.LIGHT_GRAY);
	g2.fill(underGlass);
	g2.setColor(Color.BLUE);
	g2.drawRect(xpos, ypos, width, height);
    }


    public void drawOverGlass(Graphics g, ImageObserver c) {/*
	Graphics2D g2 = (Graphics2D)g;
	if ( isReceiving || isPassing )
	    g2.setColor(Color.RED);
	else
	    g2.setColor(Color.BLACK);
	g2.fill(overGlass);
*/
    }

    public void receiveGlass(GuiGlass newGlass, int direction) {
	hasGlass = true;
	isReceiving = true;
	glass = newGlass;

	glass.setXCoord(xpos + (width - glass.getWidth()) / 2);
	glass.setYCoord(ypos + (height - glass.getHeight()) / 2);

	switch (direction) {
	    case FactoryPart.WEST:
		glassDirection  = FactoryPart.EAST;
		break;
	    case FactoryPart.EAST:
		glassDirection  = FactoryPart.WEST;
		break;
	    case FactoryPart.NORTH:
		glassDirection  = FactoryPart.SOUTH;
		break;
	    case FactoryPart.SOUTH:
		glassDirection  = FactoryPart.NORTH;
		break;
	    default:
		// This shouldn't happen.
		break;
	} 
    }

    public void finishPassing() {
	hasGlass = false;
    }

    // Pass the glass to the specified direction
    public void passTo(int dir) {
	glassDirection = dir;
	isPassing = true;
	switch (dir) {
	    case FactoryPart.EAST:
		break;
	    case FactoryPart.WEST:
		break;
	    case FactoryPart.NORTH:
		break;
	    case FactoryPart.SOUTH:
		break;
	    default:
		break;
	}
    }

    public void setWidth(int w) {
	width = w;
	underGlass = new Rectangle(xpos, ypos, width, height);
	overGlass = new Rectangle(xpos + (width - (width / 3)) / 2, ypos + (height - (height / 3)) / 2, width / 3, height / 3);
    }

    public void setHeight(int h) {
	height = h;
	underGlass = new Rectangle(xpos, ypos, width, height);
	overGlass = new Rectangle(xpos + (width - (width / 3)) / 2, ypos + (height - (height / 3)) / 2, width / 3, height / 3);
    }

    public void setX(int x) {
	xpos = x;
	underGlass = new Rectangle(xpos, ypos, width, height);
	overGlass = new Rectangle(xpos + (width - (width / 3)) / 2, ypos + (height - (height / 3)) / 2, width / 3, height / 3);
    }

    public void setY(int y) {
	ypos = y;
	underGlass = new Rectangle(xpos, ypos, width, height);
	overGlass = new Rectangle(xpos + (width - (width / 3)) / 2, ypos + (height - (height / 3)) / 2, width / 3, height / 3);
    }

    public int getX() {
	return xpos;
    }

    public int getY() {
	return ypos;
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public int getRotation() {
	// TODO
	return 0;
    }

    public void setRotation(int degree) {
	// TODO
    }

    public String getStatus() {
	if (isPassing && isReceiving) 
	    return "On, Passing & Receiving";
	else if (isPassing)
	    return "On, Passing";
	else if (isReceiving)
	    return "On, Receiving";
	else
	    return "On, Idle";
    }

    public boolean hasGlass() {
	return hasGlass;
    }

    public void setState(boolean flag) {
    }

    public boolean isBroken() {
	return false;
    }

	@Override
	public void setBreak(boolean flag) {
		// TODO Auto-generated method stub
		
	}
}
