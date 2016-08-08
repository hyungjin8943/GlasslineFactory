package factory.gui;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;

public class GuiTrash extends FactoryPart implements AnimatedPart {
	
	// Images for the trasn bin 
	ImageIcon imgTrash;

	// Location
	int xPos, yPos;

	// True if the trash bin is empty
	boolean isEmpty = true;
	
	public GuiTrash() {
		this(0, 0);
	}
	
	public GuiTrash(int x, int y) {
		imgTrash = new ImageIcon("images/trashbin.png");
		xPos = x;
		yPos = y;
	}
	
	public void update() {
		// No animation for GuiTrash (yet)
	}

	@Override
	public void drawUnderGlass(Graphics g, ImageObserver c) {
		g.drawImage(imgTrash.getImage(), xPos, yPos, getWidth(), getHeight(), c);
	}

	@Override
	public void drawOverGlass(Graphics g, ImageObserver c) {
		// Nothing to be drawn over glass
	}

	@Override
	public void receiveGlass(GuiGlass glass, int direction) {
		// GuiTrash doesn't receive any glass
	}

	@Override
	public void finishPassing() {
		// GuiTrash doesn't need to do anything
	}

	@Override
	public void setX(int x) {
		xPos = x;
	}

	@Override
	public void setY(int y) {
		yPos = y;
	}

	@Override
	public int getX() {
		return xPos;
	}

	@Override
	public int getY() {
		return yPos;
	}

	@Override
	public int getWidth() {
		return imgTrash.getIconWidth();
	}

	@Override
	public int getHeight() {
		return imgTrash.getIconHeight();
	}

	public int getCenterX() {
		return (xPos + getWidth() / 2);
	}
	
	public int getCenterY() {
		return (yPos + getHeight() / 2);
	}

	@Override
	public int getRotation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRotation(int degree) {
		// TODO Auto-generated method stub

	}

	public void receiveGlass(GuiGlass glass) {
	}

	public String getStatus() {
	    if (isEmpty)
		return "On, Empty";
	    else
		return "On, Not empty";
	}

	public boolean hasGlass() {
	    return !isEmpty;
	}

	public void setBreak(boolean flag) {
	}

	public boolean isBroken() {
	    return false;
	}

}
