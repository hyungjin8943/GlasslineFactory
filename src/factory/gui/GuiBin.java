package factory.gui;

import java.awt.*;
import java.awt.image.*;
import javax.swing.ImageIcon;
import java.util.*;

public class GuiBin extends FactoryPart implements AnimatedPart {
	
	// Images for pallet and glass
	ImageIcon imgPallet, imgGlass;
	GuiGlass myGlass;
	// Location
	int xPos, yPos;

	// Insets for imgGlass
	int xInset, yInset;
	
	Queue<GuiGlass> glassList;
	
	public GuiBin() {
		this(0, 0);
	}
	
	public GuiBin(int x, int y) {
		imgPallet = new ImageIcon("images/pallet.png");
		imgGlass = new ImageIcon("images/glass_opaque.png");
		xPos = x;
		yPos = y;
		xInset = (imgPallet.getIconWidth() - imgGlass.getIconWidth()) / 2;
		yInset = (imgPallet.getIconHeight() - imgGlass.getIconHeight()) / 2;
		glassList = new LinkedList<GuiGlass>();
	}
	
	public void update() {
		// No animation for GuiBin (yet)
	}

	@Override
	public void drawUnderGlass(Graphics g, ImageObserver c) {
		g.drawImage(imgPallet.getImage(), xPos, yPos, getWidth(), getHeight(), c);
		//g.drawImage(imgGlass.getImage(), xPos + xInset, yPos + yInset,imgGlass.getIconWidth(), imgGlass.getIconHeight(), c);
	}

	@Override
	public void drawOverGlass(Graphics g, ImageObserver c) {
		// Nothing to be drawn over glass
	}

	@Override
	public void receiveGlass(GuiGlass glass, int direction) {
		// GuiBin doesn't receive any glass
	}

	@Override
	public void finishPassing() {
		// GuiBin doesn't need to do anything
	}

	@Override
	public void setX(int x) {
		xPos = x;
		xInset = (imgPallet.getIconWidth() - imgGlass.getIconWidth()) / 2;
	}

	@Override
	public void setY(int y) {
		yPos = y;
		yInset = (imgPallet.getIconHeight() - imgGlass.getIconHeight()) / 2;
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
		return imgPallet.getIconWidth();
	}

	@Override
	public int getHeight() {
		return imgPallet.getIconHeight();
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

	public void giveGlassToRobot(GuiGlassRobot ggr) {
		//System.out.println("Giving Robot Glass");
		
		ggr.receiveGlass(glassList.peek(), SOUTH);
		glassList.poll();
		
	}
	
	public void stockBin(GuiGlass glass){
		
		//System.out.println("Bin stocked");
		
		glassList.add(glass);
	}

	public void receiveGlass(GuiGlass glass) {
		myGlass = glass;
	}

	public String getStatus() {
	    if (glassList.isEmpty())
		return "On, Empty";
	    else
		return "On, Ready";
	}

	public boolean hasGlass() {
	    return (glassList.size() > 0);
	}

	public void setBreak(boolean flag) {
	}

	public boolean isBroken() {
	    return false;
	}

}
