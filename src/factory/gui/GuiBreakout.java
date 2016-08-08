package factory.gui;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import factory.controller.*;
import factory.interfaces.*;
import factory.panels.GlassDesign;

public class GuiBreakout extends FactoryPart implements AnimatedPart, MachineControllerInteractor
{
	/********
	 * DATA *
	 ********/
	InlineMachineController					controller;                         //The agent controller associated with this station

	//The pane of glass at this station
	private GuiGlass						breakoutGlass;                      //The glass object being operated on by the cutter
	private int								glassDirection;                  /**The direction the *glass* is *moving* when being *received* by the station */
	private	int								breakingPointYCoord;
	
	private	GlassDesign						breakoutGlassDesign;
	private	ArrayList<ArrayList<Integer>>	breakoutPhysicalSchematic;

	//Station state flag variables
	private boolean							hasGlass;           
	private	boolean							isOn;
	private boolean							isReceivingGlass;
	private boolean							isBreakingOut;
	private boolean							isGoingIdle;
	private boolean							isPassingGlass;
	
	private final double					probIncompleteBreakout	= 0.2;
	private boolean							isBroken;
	private	boolean							pistonsDown;
	private boolean							incompleteBreakout;

	private ImageIcon                       breakoutBaseImage;          //The image for the base of the Breakout situated on the sides of the conveyor
	private ImageIcon						breakoutTopImage;			//The image for the top part of the Breakout machine that lies above the conveyor
	private ImageIcon						breakoutPistonImage;    //The image for the piston of the Breakout that actually breaks the glass
	

	private int								breakoutXCoord;                  //Top left coordinate of the Breakout
	private int								breakoutYCoord;               
	private int								breakoutAngle;                    //Angle of the Breakout relative to the factory axes

	private int								baseXCoord;                  
	private int								baseYCoord;
	private int								baseWidth;
	private int								baseHeight;
	private int								topXCoord;
	private	int								topYCoord;
	private	int								topWidth;
	private	int								topHeight;
	private int								piston1XCoord;
	private int								piston1YCoord;
	private int								piston1Width;
	private int								piston1Height;
	private int								piston2XCoord;
	private int								piston2YCoord;
	private int								piston2Width;
	private int								piston2Height;
	private int								piston3XCoord;
	private int								piston3YCoord;
	private int								piston3Width;
	private int								piston3Height;
	private int								piston4XCoord;
	private int								piston4YCoord;
	private int								piston4Width;
	private int								piston4Height;
	private int								piston5XCoord;
	private int								piston5YCoord;
	private int								piston5Width;
	private int								piston5Height;
	private int								piston6XCoord;
	private int								piston6YCoord;
	private int								piston6Width;
	private int								piston6Height;
	private int								piston7XCoord;
	private int								piston7YCoord;
	private int								piston7Width;
	private int								piston7Height;
	private int								piston8XCoord;
	private int								piston8YCoord;
	private int								piston8Width;
	private int								piston8Height;
	private int								piston9XCoord;
	private int								piston9YCoord;
	private int								piston9Width;
	private int								piston9Height;

	private final int						stdBaseWidth			= 178;
	private final int						stdBaseHeight			= 97;
	private final int						stdTopWidth				= 116;
	private final int						stdTopHeight		 	= 77;
	private final int						stdPistonWidth			= 9;
	private final int						stdPistonHeight			= 19;

	private final int						initTopXMargin			= 31;
	private final int						initTopYMargin			= 10;
	private final int						initPiston1XMargin		= 60;
	private final int						initPiston1YMargin		= 15;
	private final int						initPiston2XMargin		= 84;
	private final int						initPiston2YMargin		= 15;
	private final int						initPiston3XMargin		= 109;
	private final int						initPiston3YMargin		= 15;
	
	private final int						initPiston4XMargin		= 60;
	private final int						initPiston4YMargin		= 39;
	private final int						initPiston5XMargin		= 84;
	private final int						initPiston5YMargin		= 39;
	private final int						initPiston6XMargin		= 109;
	private final int						initPiston6YMargin		= 39;
	
	private final int						initPiston7XMargin		= 60;
	private final int						initPiston7YMargin		= 64;
	private final int						initPiston8XMargin		= 84;
	private final int						initPiston8YMargin		= 64;
	private final int						initPiston9XMargin		= 109;
	private final int						initPiston9YMargin		= 64;
	
	private final int						receiveTargetYMargin	= 14;
	
	private	int								shardCounter;
	
		
	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructor takes three parameters: Initial x coordinate, Initial y coordinate, Initial angle
	 * @param initX, initY, initA
	 */
	public GuiBreakout(int initX, int initY, int initA)
	{
		//Initializes the cutter state to idle (i.e. "is doing anything" = false)
		this.setState("Idle");
		incompleteBreakout	= false;
		
		//Initialize the position and orientation of the factory according to constructor parameters
		breakoutXCoord		= initX;
		breakoutYCoord		= initY;
		breakoutAngle		= initA;
		
		//Set the position and size of the base and placeHolding rectangle to match those of the overall cutter
		baseXCoord			= breakoutXCoord;
		baseYCoord			= breakoutYCoord;
		baseWidth			= stdBaseWidth;
		baseHeight			= stdBaseHeight;
		
		topWidth			= stdTopWidth;
		topHeight			= stdTopHeight;
		piston1Width		= stdPistonWidth;
		piston1Height		= stdPistonHeight;
		piston2Width		= stdPistonWidth;
		piston2Height		= stdPistonHeight;
		piston3Width		= stdPistonWidth;
		piston3Height		= stdPistonHeight;
		piston4Width		= stdPistonWidth;
		piston4Height		= stdPistonHeight;
		piston5Width		= stdPistonWidth;
		piston5Height		= stdPistonHeight;
		piston6Width		= stdPistonWidth;
		piston6Height		= stdPistonHeight;
		piston7Width		= stdPistonWidth;
		piston7Height		= stdPistonHeight;
		piston8Width		= stdPistonWidth;
		piston8Height		= stdPistonHeight;
		piston9Width		= stdPistonWidth;
		piston9Height		= stdPistonHeight;
		
		//Call the convenience method that sets the position of the cutter components so that they appear in the proper alignment
		buildGraphic();
		
		breakingPointYCoord	= topYCoord + (topHeight/20);
		//System.out.println(breakingPointYCoord);
		
		//Load the cutter component images
		breakoutBaseImage	= new ImageIcon("images/breakout_base.png");
		breakoutTopImage	= new ImageIcon("images/breakout_top.png");
		breakoutPistonImage	= new ImageIcon("images/breakout_piston.png");
		
		shardCounter		= 0;
	}
	
	/**
	 *	Convenience method that sets the position and size of all components of the NC Cutter according to the position of the base
	 *	Arguments:	None
	 *	Returns:	void
	 */
	public void buildGraphic()
	{
		topXCoord		= baseXCoord + initTopXMargin;
		topYCoord	 	= baseYCoord + initTopYMargin;
		
		piston1XCoord	= baseXCoord + initPiston1XMargin;
		piston1YCoord	= baseYCoord + initPiston1YMargin;
		piston2XCoord	= baseXCoord + initPiston2XMargin;
		piston2YCoord	= baseYCoord + initPiston2YMargin;
		piston3XCoord	= baseXCoord + initPiston3XMargin;
		piston3YCoord	= baseYCoord + initPiston3YMargin;
		piston4XCoord	= baseXCoord + initPiston4XMargin;
		piston4YCoord	= baseYCoord + initPiston4YMargin;
		piston5XCoord	= baseXCoord + initPiston5XMargin;
		piston5YCoord	= baseYCoord + initPiston5YMargin;
		piston6XCoord	= baseXCoord + initPiston6XMargin;
		piston6YCoord	= baseYCoord + initPiston6YMargin;
		piston7XCoord	= baseXCoord + initPiston7XMargin;
		piston7YCoord	= baseYCoord + initPiston7YMargin;
		piston8XCoord	= baseXCoord + initPiston8XMargin;
		piston8YCoord	= baseYCoord + initPiston8YMargin;
		piston9XCoord	= baseXCoord + initPiston9XMargin;
		piston9YCoord	= baseYCoord + initPiston9YMargin;
	}
		
	public void update()
	{
		if(hasGlass)//if(isBreakingOut)
		{
			if(isReceivingGlass)
			{
				if(breakoutGlass.getYCoord() == breakoutYCoord + receiveTargetYMargin)
				{				
					int	stationDirRelToPreviousPart	= 0;
					switch(glassDirection) 
					{
					    case FactoryPart.WEST:	stationDirRelToPreviousPart	= FactoryPart.EAST;	
					    						break;
						case FactoryPart.EAST:	stationDirRelToPreviousPart	= FactoryPart.WEST;
												break;
						case FactoryPart.NORTH:	stationDirRelToPreviousPart	= FactoryPart.SOUTH;
						   						break;
					    case FactoryPart.SOUTH:	stationDirRelToPreviousPart	= FactoryPart.NORTH;
												break;
					    default:				break;
					}
					
					this.setState("Idle");
					getPart(stationDirRelToPreviousPart).finishPassing();
					//this.playJob(""); //for branch testing
					

				}
				else
				{
					breakoutGlass.moveLaterally(glassDirection, 1);
				}
			}
			else if(isBreakingOut)
			{
				double	randomBehavior	= Math.random();
				if(!isBroken)
				{
					if(randomBehavior < probIncompleteBreakout)
					{
						breakoutGlass.breakoutGlassPartially(breakoutPhysicalSchematic);
						incompleteBreakout	= true;
					}
					else
					{
						breakoutGlass.breakoutGlassCompletely(breakoutPhysicalSchematic);
						incompleteBreakout	= false;
					}
				}
				else
				{
					breakoutGlass.breakGlass();
				}
				
				shardCounter++;
				if(shardCounter == 5)
				{
					shardCounter	= 0;
				}
				
				movePistonVertically(1, -1);
				movePistonVertically(4, -1);
				movePistonVertically(7, -1);
				movePistonVertically(3, -1);
				movePistonVertically(6, -1);
				movePistonVertically(9, -1);
				
				pistonsDown	= true;
				
				this.setState("Finishing Breaking Out Glass");
			}
			else if(isGoingIdle)
			{
				if(pistonsDown)
				{
					movePistonVertically(1, 1);
					movePistonVertically(4, 1);
					movePistonVertically(7, 1);
					movePistonVertically(3, 1);
					movePistonVertically(6, 1);
					movePistonVertically(9, 1);
				}
				
				pistonsDown	= false;
				this.setState("Idle");
				if(isBroken)
				{
					controller.doneJob("BROKEN");
				}
				else if(incompleteBreakout)
				{
					controller.doneJob("NEED MANUAL BREAKOUT");
				}
				else
				{
					controller.doneJob();	
				}				
			}
			else if(isPassingGlass)
			{
				if(breakoutGlass.getYCoord() == breakoutYCoord)
				{				
					int	stationDirRelToAdjacentPart	= 0;
					switch(glassDirection) 
					{
					    case FactoryPart.WEST:	stationDirRelToAdjacentPart	= FactoryPart.EAST;	
					    						break;
						case FactoryPart.EAST:	stationDirRelToAdjacentPart	= FactoryPart.WEST;
												break;
						case FactoryPart.NORTH:	stationDirRelToAdjacentPart	= FactoryPart.SOUTH;
						   						break;
					    case FactoryPart.SOUTH:	stationDirRelToAdjacentPart	= FactoryPart.NORTH;
												break;
					    default:				break;
					}
										
					//Pass the glass to the adjacent factory part from which the station received the glass
					adjacentParts[glassDirection].receiveGlass(breakoutGlass, stationDirRelToAdjacentPart);	//Commented out here because there is no agent code in my individual branch
					
					this.setState("Idle");
					breakoutGlass			= null;
				}
				else
				{
					breakoutGlass.moveLaterally(glassDirection, 1);
				}
			}
			else
			{
				//do nothing
			}
		}
		else
		{
			//do nothing
		}
	}

	@Override
	public void drawUnderGlass(Graphics g, ImageObserver c)
	{
		Graphics2D	g2	= (Graphics2D) g;
		
		//Rotate the Graphics2D object so that the cutter will be drawn at it's angle.
		double	breakoutCenterX	= (baseXCoord + (baseWidth/2));							//Find the station's center x coordinate
		double	breakoutCenterY	= (baseYCoord + (baseHeight/2));						//Find the station's center y coordinate
		g2.rotate(Math.toRadians(breakoutAngle), breakoutCenterX, breakoutCenterY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		
		//Draw the breakout images with the following layering. The images will be drawn to fill the box described by the method parameters. Movement or scaling of the image is done elsewhere by adjusting these variables.
		switch(shardCounter)
		{
			case	0:	breakoutBaseImage	= new ImageIcon("images/breakout_base.png");
						break;
			case	1:	breakoutBaseImage	= new ImageIcon("images/breakout_base_shards1.png");
						break;
			case	2:	breakoutBaseImage	= new ImageIcon("images/breakout_base_shards2.png");
						break;
			case	3:	breakoutBaseImage	= new ImageIcon("images/breakout_base_shards3.png");
						break;
			case	4:	breakoutBaseImage	= new ImageIcon("images/breakout_base_shards4.png");
						break;
			default:	breakoutBaseImage	= new ImageIcon("images/breakout_base.png");
						break;
		}
		g2.drawImage(breakoutBaseImage.getImage(), baseXCoord, baseYCoord, baseWidth, baseHeight, c);
						
		//Return the Graphics2D to the default state so that other classes drawing will not be affected by changes made here.
		g2.rotate(Math.toRadians(-1*breakoutAngle), breakoutCenterX, breakoutCenterY);
	}

	@Override
	public void drawOverGlass(Graphics g, ImageObserver c)
	{
		Graphics2D	g2	= (Graphics2D) g;
		
		//Rotate the Graphics2D object so that the cutter will be drawn at it's angle.
		double	breakoutCenterX	= (baseXCoord + (baseWidth/2));							//Find the station's center x coordinate
		double	breakoutCenterY	= (baseYCoord + (baseHeight/2));						//Find the station's center y coordinate
		g2.rotate(Math.toRadians(breakoutAngle), breakoutCenterX, breakoutCenterY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.		

		//Draw the breakout images with the following layering. The images will be drawn to fill the box described by the method parameters. Movement or scaling of the image is done elsewhere by adjusting these variables.
		g2.drawImage(breakoutTopImage.getImage(), topXCoord, topYCoord, topWidth, topHeight, c);
		g2.drawImage(breakoutPistonImage.getImage(), piston1XCoord, piston1YCoord, piston1Width, piston1Height, c);
		g2.drawImage(breakoutPistonImage.getImage(), piston2XCoord, piston2YCoord, piston2Width, piston2Height, c);
		g2.drawImage(breakoutPistonImage.getImage(), piston3XCoord, piston3YCoord, piston3Width, piston3Height, c);
		g2.drawImage(breakoutPistonImage.getImage(), piston4XCoord, piston4YCoord, piston4Width, piston4Height, c);
		g2.drawImage(breakoutPistonImage.getImage(), piston5XCoord, piston5YCoord, piston5Width, piston5Height, c);
		g2.drawImage(breakoutPistonImage.getImage(), piston6XCoord, piston6YCoord, piston6Width, piston6Height, c);
		g2.drawImage(breakoutPistonImage.getImage(), piston7XCoord, piston7YCoord, piston7Width, piston7Height, c);
		g2.drawImage(breakoutPistonImage.getImage(), piston8XCoord, piston8YCoord, piston8Width, piston8Height, c);
		g2.drawImage(breakoutPistonImage.getImage(), piston9XCoord, piston9YCoord, piston9Width, piston9Height, c);
		
		//Return the Graphics2D to the default state so that other classes drawing will not be affected by changes made here.
		g2.rotate(Math.toRadians(-1*breakoutAngle), breakoutCenterX, breakoutCenterY);		
	}
	
	public void playJob(String msg, GlassDesign jobDesign)
	{
		if(msg.equals("NO ACTION"))
		{
			isOn				= true;
			this.setState("Idle");
			controller.doneJob();
		}
		else
		{
			breakoutGlassDesign	= jobDesign;
			breakoutPhysicalSchematic	= breakoutGlassDesign.getPhysicalSchematic();
			
			isOn				= true;
			this.setState("Breaking Out Glass");
		}
	}
	
	public void playPass()
	{
		this.setState("Passing Glass");
	}


	public void setState(String msg) 
	{
		if(msg.equals("Receiving Glass"))
		{
			isReceivingGlass	= true;
			isBreakingOut		= false;
			isGoingIdle			= false;
			isPassingGlass		= false;
		}
		else if (msg.equals("Breaking Out Glass")) 
		{
			isReceivingGlass	= false;
			isBreakingOut		= true;
			isGoingIdle			= false;
			isPassingGlass		= false;
		}
		else if(msg.equals("Finishing Breaking Out Glass"))
		{
			isReceivingGlass	= false;
			isBreakingOut		= false;
			isGoingIdle			= true;
			isPassingGlass		= false;
		}
		else if(msg.equals("Passing Glass"))
		{
			isReceivingGlass	= false;
			isBreakingOut		= false;
			isGoingIdle			= false;
			isPassingGlass		= true;
		}
		else
		{
			isReceivingGlass	= false;
			isBreakingOut		= false;
			isGoingIdle			= false;
			isPassingGlass		= false;
		}
	}

	@Override
	public void receiveGlass(GuiGlass glass, int direction)
	{
		hasGlass		= true;
		breakoutGlass	= glass;
		isOn			= true;
		this.setState("Receiving Glass");
		
		switch(direction)
		{
			case	FactoryPart.NORTH:	glassDirection	= FactoryPart.SOUTH;
										break;
			case	FactoryPart.SOUTH:	glassDirection	= FactoryPart.NORTH;
										break;
			case	FactoryPart.EAST:	glassDirection	= FactoryPart.WEST;
										break;
			case	FactoryPart.WEST:	glassDirection	= FactoryPart.EAST;
										break;
			default:					break;
		}		
	}

	@Override
	public void finishPassing()
	{
		//Sends a message to the station's controller that it is finished passing.
		controller.donePass();		//Commented out here because there is no agent code in my individual branch
		this.setState("Idle");
		
	}

	public void movePistonVertically(int pistonNumber, int zDisplacement)
	{
		double stdDimensionRatio	= (double)stdPistonWidth/(double)stdPistonHeight;
		
		switch(pistonNumber)
		{
			case	1:		piston1Width	= piston1Width + 2*zDisplacement; 
							piston1Height	= (int)(piston1Width/stdDimensionRatio);		
							piston1XCoord	= piston1XCoord - zDisplacement;
							piston1YCoord 	= piston1YCoord - zDisplacement;
							break;
			case	2:		piston2Width	= piston2Width + 2*zDisplacement; 
							piston2Height	= (int)(piston2Width/stdDimensionRatio);		
							piston2XCoord	= piston2XCoord - zDisplacement;
							piston2YCoord 	= piston2YCoord - zDisplacement;
							break;
			case	3:		piston3Width	= piston3Width + 2*zDisplacement; 
							piston3Height	= (int)(piston1Width/stdDimensionRatio);		
							piston3XCoord	= piston3XCoord - zDisplacement;
							piston3YCoord 	= piston3YCoord - zDisplacement;
							break;
			case	4:		piston4Width	= piston4Width + 2*zDisplacement; 
							piston4Height	= (int)(piston4Width/stdDimensionRatio);		
							piston4XCoord	= piston4XCoord - zDisplacement;
							piston4YCoord 	= piston4YCoord - zDisplacement;
							break;
			case	5:		piston5Width	= piston5Width + 2*zDisplacement; 
							piston5Height	= (int)(piston5Width/stdDimensionRatio);		
							piston5XCoord	= piston5XCoord - zDisplacement;
							piston5YCoord 	= piston5YCoord - zDisplacement;
							break;
			case	6:		piston6Width	= piston6Width + 2*zDisplacement; 
							piston6Height	= (int)(piston6Width/stdDimensionRatio);		
							piston6XCoord	= piston6XCoord - zDisplacement;
							piston6YCoord 	= piston6YCoord - zDisplacement;
							break;
			case	7:		piston7Width	= piston7Width + 2*zDisplacement; 
							piston7Height	= (int)(piston7Width/stdDimensionRatio);		
							piston7XCoord	= piston7XCoord - zDisplacement;
							piston7YCoord 	= piston7YCoord - zDisplacement;
							break;
			case	8:		piston8Width	= piston8Width + 2*zDisplacement; 
							piston8Height	= (int)(piston8Width/stdDimensionRatio);		
							piston8XCoord	= piston8XCoord - zDisplacement;
							piston8YCoord 	= piston8YCoord - zDisplacement;
							break;
			case	9:		piston9Width	= piston9Width + 2*zDisplacement; 
							piston9Height	= (int)(piston9Width/stdDimensionRatio);		
							piston9XCoord	= piston9XCoord - zDisplacement;
							piston9YCoord 	= piston9YCoord - zDisplacement;
							break;
			default:
		}
	}

	@Override
	public void setX(int x)
	{
		breakoutXCoord	= x;
		baseXCoord		= breakoutXCoord;
		buildGraphic();		
	}



	@Override
	public void setY(int y)
	{
		breakoutYCoord	= y;
		baseYCoord		= breakoutYCoord;
		buildGraphic();		
	}



	@Override
	public int getX()
	{
		// TODO Auto-generated method stub
		return this.breakoutXCoord;
	}



	@Override
	public int getY()
	{
		// TODO Auto-generated method stub
		return this.breakoutYCoord;
	}



	@Override
	public int getWidth()
	{
		// TODO Auto-generated method stub
		return this.baseWidth;
	}



	@Override
	public int getHeight()
	{
		// TODO Auto-generated method stub
		return this.baseHeight;
	}



	@Override
	public int getRotation()
	{
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setRotation(int degree)
	{
		// TODO Auto-generated method stub
		
	}
	
	
	public void setController(InlineMachineController cont)
	{
		controller	= cont;
	}
	
	public String getStatus()
	{        
		if(isOn)
		{
			if(isReceivingGlass)
			{
				return ("ON, Receiving glass.");
			}
			else if(isBreakingOut)
			{
				return ("ON, Breaking Out.");
			}
			else if(isGoingIdle)
			{
				return ("ON, Going idle.");
			}
			else if(isPassingGlass)
			{
				return ("ON, Passing glass.");
			}
			else
			{
				return ("ON, Idle.");
			}
		}
		else
		{
			return ("OFF.");
		}
	}
	
	public boolean hasGlass()
	{
		if(breakoutGlass != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void setBreak(boolean b)
	{
		isBroken	= b;
	}
	
	public boolean isBroken()
	{
		return isBroken;
	}

//END CLASS	
}
