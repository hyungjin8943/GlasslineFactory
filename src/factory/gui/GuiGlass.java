package factory.gui;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.*;

/**
 *	CSCI 200 Factory Project
 *	Version v0
 *
 *	GuiGlass class
 *
 *	Represents a physical pane of glass.
 *	This pane can be moved in three dimensions and rotated.
 *	Other components of the factory will perform operations on the glass that will change the physical and effects state of the glass pane.
 *	
 *	@author 	Ryan McGee
 *	@version	v0
 */
public class GuiGlass
{
	/********
	 * DATA *
	 ********/
	
	private ImageIcon				glassImage;					//The visible image of a pane of glass in the GUI panel
	private Rectangle2D.Double		glassPlaceholder;			//An invisible GUI Rectangle that lies directly under the image to utilize Rectangle methods for collision detection.
	private ArrayList<ImageIcon>	effectsImages	= new ArrayList<ImageIcon>();				//A list of images to be laid over the glassImage to convey effects (e.g. shiny glints after washing, a color after painting)
	private	ImageIcon				effectsImage	= new ImageIcon();
	BufferedImage					buff, buffFx;
	
	private	int						leftCutLineXMargin;
	private	int						rightCutLineXMargin;
	
	private int[][]					glassBlueprint;				//A two-dimensional array of integers that indicate where operations are to be performed on a piece of glass by machines.

	private int						xCoord;						//The current position of the image and placeholder along the x axis
	private int						yCoord;						//The current position of the image and placeholder along the y axis
	private int 					angle;						//The current angle of the image and placeholder respective to the x axis
	private int						width;						//The current width of the image and placeholder
	private int						height;						//The current height of the image and placeholder
	private final int				stdWidth	= 70; //50			//The standard width of the image and placeholder when not being scaled
	private final int				stdHeight	= 70; //67			//The standard height of the image and placeholder when not being scaled
	private final float				transparency = (float)0.6;	//The transparency value for the glass image. Will be used to adjust the alpha value of the graphics object when drawing the glass.

	private boolean[]				physicalState = new boolean[4];     //An array of booleans representing the physical state of the base pane of glass.
																		//Indices: 0 = is initial, 1 = is partially broken-out, 2 = is completely broken-out, 3 = is drilled
																		//Indices are mutually exclusive, exactly one can be true at any given time
	private boolean[]				effectsState = new boolean[7];		//An array of booleans representing the effects currently in effect on this pane of glass
																		//Indices: 0 = is cut, 1 = is cross-seamed, 2 = is grinded, 3 = is painted, 4 = is washed, 5 = is UV treated, 5 = is baked
																		//Indices are not mutually exclusive, all, none, or any number can be true at any given time
	private	boolean					treatmentCompleted;
	
	private	ImageIcon				standardGlassImage	= new ImageIcon("images/glass_opaque.png");
	
	/****************
	 * CONSTRUCTORS *
	 ****************/
	
	/**
	 *	Constructor takes three parameters: Initial x coordinate, Initial y coordinate, Initial angle
	 */
	public GuiGlass(int initX, int initY, int initA)
	{
		//Initializing glass orientation variables
		xCoord				= initX;
		yCoord				= initY;
		angle				= initA;
		width				= stdWidth;
		height				= stdHeight;
		
		//Loading the initial glass images
		glassImage			= new ImageIcon("images/glass_opaque.png");
		glassPlaceholder	= new Rectangle2D.Double(xCoord, yCoord, width, height);
		buff	= new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		buffFx	= new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		
		//Initializes the glass state
		physicalState[0]	= true;
		
		leftCutLineXMargin	= 10;
		rightCutLineXMargin	= 60;
		
		//effectsImages.add(new ImageIcon("images/glass_opaque.png"));
		//effectsImages.add(new ImageIcon("images/glass_crossseamed.png"));
		//effectsImages.add(new ImageIcon("images/glass_ground.png"));
		//effectsImages.add(new ImageIcon("images/glass_paint0_red.png"));
		//effectsImages.add(new ImageIcon("images/glass_washed.png"));
		
		treatmentCompleted	= false;
	}
	
	/***********
	 * METHODS *
	 ***********/
	
	/**	
	 * Convenience method for other classes to change the display representing this pane of glass after an operation is completed on the glass.
	 * Arguments: none
	 * Returns: void	
	 */
	public void update()
	{
		//Updates the glassPlaceholder Rectangle2D to the glass's current coordinates (i.e. to match coordinates if they have been changed since last call to update())
		glassPlaceholder.setFrame(this.xCoord, this.yCoord, this.width, this.height);
		
		//Update the appearance of this pane of glass to reflect any changes in state since the last call to update()
		//this.updateGlassImage();
		//this.updateEffectsImages();		
	}
	
	/**	
	 * Displays the glass onscreen by drawing its images to a Graphics2D object with various effects.
	 * Arguments: Graphics2D object to which to draw, The component initiating the draw
	 * Returns: void	
	 * @param g, c
	 */
	public void draw(Graphics2D g, Component c)
	{		
		//Rotate the Graphics2D object so that glass will be drawn at it's angle.
		double	glassCenterX	= (xCoord + (width/2));					//Find the glass's center y coordinate
		double	glassCenterY	= (yCoord + (height/2));				//Find the glass's center x coordinate
		g.rotate(Math.toRadians(angle), glassCenterX, glassCenterY);	//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		
		//Change the composite of the Graphics2D to allow for alpha overlay to make the glass appear transparent.
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));		//glass's transparency constant is used as the alpha parameter
		//Draw the glas image. The image will be drawn to fill the box described by the method parameters. Movement or scaling of the glass image is done elsewhere by adjusting these variables.
		g.drawImage(glassImage.getImage(), xCoord, yCoord, width, height, null);
		
		//FOLLOWING BLOCK IS NEW
		//Change the composite of the Graphics2D to allow for alpha overlay to make the glass effects transparent.
		//g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)0.35));		//glass's transparency constant is used as the alpha parameter
		//Draw the glass effects images. The image will be drawn to fill the box described by the method parameters. Movement or scaling of the glass image is done elsewhere by adjusting these variables.
		
//		for(int i = 0; i < effectsState.length; i++)
//		{
//			if(effectsState[i] == true)
//			{
//				g.drawImage(effectsImages.get(i).getImage(), xCoord, yCoord, width, height, null);
//			}
//		}

//		for(ImageIcon fxImg : effectsImages)
//		{
//			g.drawImage(fxImg.getImage(), xCoord, yCoord, width, height, null);
//		}
		
		g.drawImage(effectsImage.getImage(), xCoord, yCoord, width, height, null);
		
		//Return the Graphics2D to the default state so that other classes drawing will not be affected by changes made here.
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1.0));	//Reset the alpha value to 1.0 (completely opaque)
		g.rotate(Math.toRadians(-1*angle), glassCenterX, glassCenterY);				//Rotate the Graphics2D back to the default orientation (about glass's center coordinate)
	}	
	
	/**	
	 * Moves the piece of glass in the x and y dimensions (left and right on screen). Adjusts the position of both the glassImage and the underlying glassPlaceHolder.
	 * Arguments: Integers that tell how many pixels along each axis for the glass to move. (Convention: UP = North, DOWN = South, LEFT = West X, RIGHT = East)
	 * Returns: void	
	 * @param xDisplacement, yDisplacement
	 */
	public void moveLaterally(int direction, int displacement)
	{
		//Set the glass's coordinates to those that correspond to it's current position and the displacement passed to this method.
		switch(direction)
		{
			case	FactoryPart.NORTH:	this.setYCoord(this.yCoord - displacement);
										break;
			case	FactoryPart.SOUTH:	this.setYCoord(this.yCoord + displacement);
										break;
			case	FactoryPart.EAST:	this.setXCoord(this.xCoord + displacement);
										break;
			case	FactoryPart.WEST:	this.setXCoord(this.xCoord - displacement);
										break;
			default:					break;
		}
	}
	
	public void moveLaterally(int direction1, int displacement1, int direction2, int displacement2)
	{
		//Set the glass's coordinates to those that correspond to it's current position and the displacement passed to this method.
		switch(direction1)
		{
			case	FactoryPart.NORTH:	this.setYCoord(this.yCoord - displacement1);
										break;
			case	FactoryPart.SOUTH:	this.setYCoord(this.yCoord + displacement1);
										break;
			case	FactoryPart.EAST:	this.setXCoord(this.xCoord + displacement1);
										break;
			case	FactoryPart.WEST:	this.setXCoord(this.xCoord - displacement1);
										break;
			default:					break;
		}
		
		switch(direction2)
		{
			case	FactoryPart.NORTH:	this.setYCoord(this.yCoord - displacement2);
										break;
			case	FactoryPart.SOUTH:	this.setYCoord(this.yCoord + displacement2);
										break;
			case	FactoryPart.EAST:	this.setXCoord(this.xCoord + displacement2);
										break;
			case	FactoryPart.WEST:	this.setXCoord(this.xCoord - displacement2);
										break;
			default:					break;
		}
	}
	
	/**
	 *	Moves the piece of glass in the z dimension (towards and away from user). Adjusts the size and position of both the glassImage and the underlying glassPlaceHolder for a scaling zoom effect.
	 *	Arguments: Integer that tell how many "pixels" along z axis for the glass to move. (Convention: AWAY FROM USER = NEG Y, TOWARDS USER = POS Y)
	 *	Returns: void	
	 *	@param zDisplacement
	 */
	public void moveVertically(int zDisplacement)
	{	//Determine the standard ratio between width and height of the glass image in order to preserve this aspect ratio in resizing the image
		//double stdDimensionRatio	= (double)this.width/(double)this.height;

		//Scale the image by an amount corresponding to the zDisplacemenet passed in. 
		this.setWidth(this.width + 2* zDisplacement);
		this.setHeight(this.height + 2* zDisplacement);
		//this.setWidth((this.width + 2*zDisplacement));				//Adds a pixel to each horizontal edge of the glass for each zDisplacement 
		//this.setHeight((int)(this.width/stdDimensionRatio));		//Add a number of pixels to each vertical edge of the glass such that the standard dimension ratio is maintained		
		this.setXCoord(this.xCoord - zDisplacement);				//Translate the glass's position to compensate for it's change in size to make it appear centered on the same coordinate.
		this.setYCoord(this.yCoord - zDisplacement);
	}
	
	public void cutGlassAtPoint(Point2D.Double cutPt)
	{
		Graphics 		gTemp	= buff.createGraphics();
		gTemp.drawImage(this.glassImage.getImage(), 0, 0, this.stdWidth, this.stdHeight, null);
		gTemp.dispose();
		
		int	xL	= (int)cutPt.getX()-1;
		int	x	= (int)cutPt.getX();
		int	xR	= (int)cutPt.getX()+1;
		int	yU	= (int)cutPt.getY()-1;
		int	y	= (int)cutPt.getY();
		int	yD	= (int)cutPt.getY()+1;
		
		if(xL < 0)				xL	= x;
		if(xR >= this.width)	xR	= x;
		if(yU < 0)				yU	= y;
		if(yD >= this.height)	yD	= y;
		
		buff.setRGB(x, y, 0xFF87C4D8);//0xCCA2D0E4);
		/*
		buff.setRGB(xL, yU, 0xFF87C4D8);
		buff.setRGB(xL, y, 0xFF87C4D8);
		buff.setRGB(xL, yD, 0xFF87C4D8);
		buff.setRGB(x, yU, 0xFF87C4D8);
		buff.setRGB(xR, yD, 0xFF87C4D8);
		buff.setRGB(xR, y, 0xFF87C4D8);
		buff.setRGB(xR, yU, 0xFF87C4D8);
		buff.setRGB(x, yU, 0xFF87C4D8);
		*/
		
		Image			img		= Toolkit.getDefaultToolkit().createImage(buff.getSource());
		ImageIcon		imgIcon	= new ImageIcon(img);
		this.setGlassImage(imgIcon);
	}
	
	public void	breakoutGlassCompletely(ArrayList<ArrayList<Integer>> physSchem)
	{
		Graphics 		gTemp	= buff.createGraphics();
		gTemp.drawImage(this.glassImage.getImage(), 0, 0, this.stdWidth, this.stdHeight, null);
		gTemp.dispose();
		
		for(int r = 0; r < physSchem.size(); r++)
		{
			for(int c = 0 ; c < physSchem.get(0).size(); c++)
			{
				if(physSchem.get(r).get(c) == 0)
				{
					//System.out.println("IM DOING THIS!!!!!!!!!!!!@*%)@$&%)@*$)@*$)@(*%)@(*$)@(*$%");
					buff.setRGB(c, r, 0x00000000);
				}
			}
		}
	
		Image			img		= Toolkit.getDefaultToolkit().createImage(buff.getSource());
		ImageIcon		imgIcon	= new ImageIcon(img);
		this.setGlassImage(imgIcon);
	}
	
	public void	breakoutGlassPartially(ArrayList<ArrayList<Integer>> physSchem)
	{
		Graphics 		gTemp	= buff.createGraphics();
		gTemp.drawImage(this.glassImage.getImage(), 0, 0, this.stdWidth, this.stdHeight, null);
		gTemp.dispose();
		
		for(int r = 0; r < physSchem.size(); r++)
		{
			for(int c = 0 ; c < physSchem.get(0).size(); c++)
			{
				if(physSchem.get(r).get(c) == 0)
				{
					if(r < (physSchem.size()/2) || c > (physSchem.get(0).size()/2))
					{
						buff.setRGB(c, r, 0x00000000);
					}
				}
			}
		}
	
		Image			img		= Toolkit.getDefaultToolkit().createImage(buff.getSource());
		ImageIcon		imgIcon	= new ImageIcon(img);
		this.setGlassImage(imgIcon);
	}
	
	public void drillGlassAtPoint(Point2D.Double drillPt)
	{
		System.out.println("drillglassatpoint");
		
		Graphics 		gTemp	= buff.createGraphics();
		gTemp.drawImage(this.glassImage.getImage(), 0, 0, this.stdWidth, this.stdHeight, null);
		gTemp.dispose();
		
		int	x	= (int)drillPt.getX();
		int	y	= (int)drillPt.getY();
		
		for(int r = y - 2, c = x - 1; c <= x + 1; c++)
		{
			System.out.println("settin pixel stuff");
			buff.setRGB(c, r, 0x00000000);
		}	
		for(int r = y - 1; r <= y + 1; r++)
		{
			for(int c = x - 2; c <= x + 2; c++)
			{
				buff.setRGB(c, r, 0x00000000);
			}
		}
		for(int r = y + 2, c = x - 1; c <= x + 1; c++)
		{
			buff.setRGB(c, r, 0x00000000);
		}
		
		Image			img		= Toolkit.getDefaultToolkit().createImage(buff.getSource());
		ImageIcon		imgIcon	= new ImageIcon(img);
		this.setGlassImage(imgIcon);
	}
	
	public void crossSeamGlassAtPoint(Point2D.Double seamPt)
	{
		Graphics 		gTemp	= buff.createGraphics();
		gTemp.drawImage(this.glassImage.getImage(), 0, 0, this.stdWidth, this.stdHeight, null);
		gTemp.dispose();
		
		buff.setRGB((int)seamPt.getX(), (int)seamPt.getY(), 0xFFFFFFFF);//0xCCA2D0E4);
				
		Image			img		= Toolkit.getDefaultToolkit().createImage(buff.getSource());
		ImageIcon		imgIcon	= new ImageIcon(img);
		this.setGlassImage(imgIcon);
	}
	
	public void paintGlassAtPoint(Point2D.Double paintPt, Color color)
	{
		Graphics 		gTemp	= buff.createGraphics();
		gTemp.drawImage(this.glassImage.getImage(), 0, 0, this.stdWidth, this.stdHeight, null);
		gTemp.dispose();
		
		if(color.equals(new Color(64, 64, 64)))
		{
			buff.setRGB((int)paintPt.getX(), (int)paintPt.getY(), 0x00000000);
		}
		else
		{
			buff.setRGB((int)paintPt.getX(), (int)paintPt.getY(), color.getRGB());
		}
		
		
		Image			img		= Toolkit.getDefaultToolkit().createImage(buff.getSource());
		ImageIcon		imgIcon	= new ImageIcon(img);
		this.setGlassImage(imgIcon);
	}
	
	public void breakoutGlassAtPoint(Point2D.Double breakPt)
	{
		Graphics 		gTemp	= buff.createGraphics();
		gTemp.drawImage(this.glassImage.getImage(), 0, 0, this.stdWidth, this.stdHeight, null);
		gTemp.dispose();
		
		int	xL	= (int)breakPt.getX()-1;
		int	x	= (int)breakPt.getX();
		int	xR	= (int)breakPt.getX()+1;
		int	yU	= (int)breakPt.getY()-1;
		int	y	= (int)breakPt.getY();
		int	yD	= (int)breakPt.getY()+1;
		
		if(xL < 0)				xL	= x;
		if(xR >= this.stdWidth)	xR	= x;
		if(yU < 0)				yU	= y;
		if(yD >= this.stdHeight)	yD	= y;
		
		buff.setRGB(x, y, 0x00000000);
		buff.setRGB(xL, yU, 0x00000000);
		buff.setRGB(xL, y, 0x00000000);
		buff.setRGB(xL, yD, 0x00000000);
		buff.setRGB(x, yU, 0x00000000);
		buff.setRGB(xR, yD, 0x00000000);
		buff.setRGB(xR, y, 0x00000000);
		buff.setRGB(xR, yU, 0x00000000);
		buff.setRGB(x, yU, 0x00000000);
		
		
		Image			img		= Toolkit.getDefaultToolkit().createImage(buff.getSource());
		ImageIcon		imgIcon	= new ImageIcon(img);
		this.setGlassImage(imgIcon);
	}
	
	public void breakGlass()
	{
		for(int r = 2*this.stdHeight/3, c = 0; r >= 0 && c < this.stdWidth; c++)
		{
			breakoutGlassAtPoint(new Point2D.Double(c, r));
			
			if(c%3 == 0)
			{
				r--;
			}
		}
		for(int r = 1, c = 0; r < this.stdHeight -1 && c < this.stdWidth; r++, c++)
		{
			if(c%2 == 0)
			{
				breakoutGlassAtPoint(new Point2D.Double(c, r));
			}
			else
			{
				breakoutGlassAtPoint(new Point2D.Double(c, r-1));
			}
		}
		for(int r = 0, c = this.stdWidth/2; r < this.stdHeight; r++)
		{
			breakoutGlassAtPoint(new Point2D.Double(c, r));
		}
		for(int r = this.stdHeight-1, c = 0; r >= 0 && c < this.stdWidth; c++)
		{
			breakoutGlassAtPoint(new Point2D.Double(c, r));
			if(c%7 != 0)
			{
				r--;
			}
		}
		for(int r = this.stdHeight/3, c = 0; r < this.stdHeight && c < this.stdWidth; c++)
		{
			breakoutGlassAtPoint(new Point2D.Double(c, r));
			if(c%2 != 0)
			{
				r++;
			}
		}
	}
	
	public void fitEffectsImages(ArrayList<ArrayList<Integer>> physSchem)
	{
		//for(ImageIcon img : effectsImages)
		//{
			Graphics 		gTemp	= buffFx.createGraphics();
			gTemp.drawImage(effectsImage.getImage(), 0, 0, this.width, this.height, null);
			gTemp.dispose();
			
			for(int r = 0; r < physSchem.size(); r++)
			{
				for(int c = 0 ; c < physSchem.get(0).size(); c++)
				{
					if(physSchem.get(r).get(c) == 0)
					{
						buffFx.setRGB(c, r, 0x00000000);
						//System.out.println("IM DOING THISSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSsssssss");
					}
				}
			}
		
			Image			newImg		= Toolkit.getDefaultToolkit().createImage(buffFx.getSource());
			effectsImage	= new ImageIcon(newImg);
			//this.setGlassImage(imgIcon);		
		//}
	}
	
	/***************************
	 * MODIFIERS AND ACCESSORS *
	 ***************************/
	
	public void setGlassImage(ImageIcon pic)
	{
		glassImage	= pic;
	}
	
	public void setGlassPlaceholder(Rectangle2D.Double rect)
	{
		glassPlaceholder	= rect;
	}
	
	public void setEffectsImages(ArrayList<ImageIcon> pics)
	{
		effectsImages	= pics;
	}
	
	public void setEffectsImage(int index, ImageIcon pic)
	{
		//effectsImages.at(index)	= pic;		//Implementation not used in v0
	}
	
	public void setXCoord(int x)
	{
		xCoord	= x; 
	}
	
	public void setYCoord(int y)
	{
		yCoord	= y;
	}
	
	public void setAngle(int a)
	{
		angle	= a;
	}
	
	public void setWidth(int w)
	{
		width	= w;
	}
	
	public void setHeight(int h)
	{
		height = h;
	}
	
	public void setPhysicalState(String description)
	{
		int	restore	= 0;
		
		for(int i = 0; i < physicalState.length; i++)
		{
			if(physicalState[i])
			{
				restore	= i;
			}
			physicalState[i]	= false;
		}
		
		if(description.equals("initial"))
		{
			physicalState[0]	= true;
			glassImage			= new ImageIcon("images/glass_opaque.png");
		}
		else if(description.equals("partially broken out"))
		{
			physicalState[1]	= true;
		}
		else if(description.equals("broken out"))
		{
			physicalState[2]	= true; 	
		}
		else if (description.equals("drilled"))
		{
			physicalState[3]	= true;
			glassImage			= new ImageIcon("images/glass_drilled1.png");
			effectsImages.set(3, new ImageIcon("images/glass_paint1_red.png"));
			
		}
		else
		{
			physicalState[restore]	= true;
		}
	}
	
	public void setEffectsState(String description)
	{
		if(description.equals("cut"))
		{
			effectsState[0]	= true;
		}
		else if(description.equals("cross seamed"))
		{
			effectsState[1]	= true;
			System.out.println("Supposedly seamed");
		}
		else if(description.equals("ground"))
		{
			effectsImage	= new ImageIcon("images/glass_ground.png");
			buffFx			= new BufferedImage(this.stdWidth, this.stdHeight, BufferedImage.TYPE_INT_ARGB);
			//effectsImages.add(new ImageIcon("images/glass_ground.png"));
			effectsState[2]	= true;
			System.out.println("Supposedly ground");
		}
		else if(description.equals("washed"))
		{
			effectsImage	= new ImageIcon("images/glass_washed.png");
			buffFx			= new BufferedImage(this.stdWidth, this.stdHeight, BufferedImage.TYPE_INT_ARGB);
			//effectsImages.add(new ImageIcon("images/glass_washed.png"));
			effectsState[4]	= true;
			effectsState[2]	= false;
		}
		else if(description.equals("painted"))
		{
			effectsState[3]	= true;
		}
		else if(description.equals("uv treated"))
		{
			effectsState[5]	= true;
		}
		else if(description.equals("baked"))
		{
			effectsState[6]	= true;
		}
		else
		{
			//do nothing
		}
	}
		
	public ImageIcon getGlassImage()
	{
		return glassImage;
	}
	
	public Rectangle2D.Double getGlassPlaceholder()
	{
		return glassPlaceholder;
	}
	
	public ArrayList<ImageIcon> getEffectsImages()
	{
		return effectsImages;
	}
	
	public int getXCoord()
	{
		return xCoord;
	}
	
	public int getYCoord()
	{
		return yCoord;
	}
	
	public int getAngle()
	{
		return angle;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getStdWidth()
	{
		return stdWidth;
	}
	
	public int getStdHeight()
	{
		return stdHeight;
	}
	
	public boolean[] getPhysicalState()
	{
		return physicalState;
	}
	
	public boolean[] getEffectsState()
	{
		return effectsState;
	}
	
	public int getLeftCutLineXMargin()
	{
		return this.leftCutLineXMargin;
	}
	
	public int getRightCutLineXMargin()
	{
		return this.rightCutLineXMargin;
	}
	
	public ImageIcon getStandardGlassImage()
	{
		return this.standardGlassImage;
	}
	
	public void setTreatmentCompleted(boolean b)
	{
		this.treatmentCompleted	= b;
	}
	
	public boolean getTreatmentCompleted()
	{
		return this.treatmentCompleted;
	}
	
	
}