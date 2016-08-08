package factory.gui;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import factory.controller.*;
import factory.interfaces.*;
import factory.panels.GlassDesign;


import javax.swing.ImageIcon;

import factory.gui.FactoryPart;

/**
 *	CSCI 200 Factory Project
 *	Version v2
 *
 *	GuiManualBreakout class
 *
 *	Represents a physical station in the factory that operates on a pane of glass.
 *	This class represents a manual breakout station with a worker that manually completes the breakout operation when the inline breakout machine fails.
 *	
 *	Extends:	FactoryPart
 *	Implements:	AnimatedPart
 *
 *	@author 	Ryan McGee
 *	@version	v2
 */
public class GuiManualBreakout extends FactoryPart implements AnimatedPart 
{
	
	/********
	 * DATA *
	 ********/
	//The agent controller associated with this station
	// TODO Change the type of controller
	ManualBreakoutController	controller;		//Commented out because there is no agent code in my individual branch

	//The pane of glass at this station (perhaps none)
	GuiGlass	manualBreakoutGlass;									//The glass object held by this station
	int			glassDirection;									/**The direction the *glass* is *moving* when being *received* by the station */
	int			glassTargetMargin;								//Specifies a position at which the glass is operated upon. Glass must reach this position before the station can do anything to it.
	ArrayList<ArrayList<Integer>>	manualBreakoutPhysicalSchematic;
	
	//Station state flag variables
	boolean	hasGlass;											
	boolean	isReceivingGlass;
	boolean isWorkingOnGlass;
	boolean	isTrashingGlass;
	boolean	isPassingGlass;
	boolean	isGoingIdle;
	boolean	isOn;
	
	boolean	isBroken;
	
	public 	enum	Task {EXTENDING_ARM, HAMMERING, RETRACTING_ARM, STANDING_BY};
	private	Task	workerTask;
	private int		workerJobStep	= 0;
	
	//ImageIcon's for the station's images
	private ImageIcon				manualBreakoutBaseImage;			//Image of the rectangular base of the station	
	private ImageIcon				manualBreakoutPlowImage;			//Image of the plow arm that pushes the glass back to a popup or other component
	private	ImageIcon				manualBreakoutMachineImage;		//Image of the machine base that sits on the workmanualBreakout base
	
	private	ImageIcon				workerBodyImage;
	private	ImageIcon				workerHeadImage;
	private ImageIcon				workerRightArmImage;
	private	ImageIcon				workerRightArmHammerImage;
	private	ImageIcon				workerRightArmHandImage;
	private ImageIcon				workerLeftArmImage;
	private	ImageIcon				workerRightFootImage;
	private	ImageIcon				workerLeftFootImage;

	private int						manualBreakoutXCoord;				//Top left coordinate of the manualBreakout
	private int						manualBreakoutYCoord;				
	private int 					manualBreakoutAngle;				//Angle of the manualBreakout relative to the factory axes
	
	//Position and size of the individual components that are put together to make a station
	private int						baseXCoord;				
	private int						baseYCoord;				
	private int						baseWidth;
	private int						baseHeight;
	private int						plowXCoord;				
	private int						plowYCoord;				
	private int						plowWidth;
	private int						plowHeight;
	private	int						machineXCoord;
	private	int						machineYCoord;
	private	int						machineWidth;
	private	int						machineHeight;
	private int						workerXCoord;
	private int						workerYCoord;
	private int						workerBodyXCoord;
	private int						workerBodyYCoord;
	private int						workerHeadXCoord;
	private int						workerHeadYCoord;
	private int						workerRightArmXCoord;
	private int						workerRightArmYCoord;
	private int						workerLeftArmXCoord;
	private int						workerLeftArmYCoord;
	private int						workerRightFootXCoord;
	private int						workerRightFootYCoord;
	private int						workerLeftFootXCoord;
	private int						workerLeftFootYCoord;
	
	private int						workerRightArmWidth;
	private int						workerRightArmHeight;
	private int						workerRightArmAngle = 0;
	private int						workerLeftArmWidth;
	private int						workerLeftArmHeight;
	private int						workerLeftArmAngle = 0;
	
	//Constants to define the initial size of station components and establish the standard aspect ratios of the components' width and height
	private final int				stdBaseWidth		= 122;	//
	private final int				stdBaseHeight		= 132;	//
	private final int				stdPlowWidth		= 65;	//
	private final int				stdPlowHeight		= 50;	//
	private final int				stdPlowComponentHeight	= 14;
	private	final int				stdMachineWidth		= 95;
	private final int				stdMachineHeight	= 21;
	
	private final int				stdWorkerBodyWidth		= 36;
	private final int				stdWorkerBodyHeight		= 78;
	private final int				stdWorkerHeadWidth		= 44;
	private final int				stdWorkerHeadHeight		= 37;
	private final int				stdWorkerRightArmWidth	= 25;
	private final int				stdWorkerRightArmHeight	= 16;
	private final int				stdWorkerLeftArmWidth	= 25;
	private final int				stdWorkerLeftArmHeight	= 16;
	private final int				stdWorkerRightFootWidth	= 26;
	private final int				stdWorkerRightFootHeight= 18;
	private final int				stdWorkerLeftFootWidth	= 26;
	private final int				stdWorkerLeftFootHeight	= 18;
	
	private final int				workerRightArmExtendedWidth	= 75;
	
	//Constants to define the initial positions of station components relative to the position of the station base
	private final int				initPlowXMargin		= 29;
	private final int				initPlowYMargin		= 22;
	private final int				initMachineXMargin	= 13;
	private final int				initMachineYMargin	= 13;

	private final int				initWorkerXMargin			= -50;		//relative to base
	private final int				initWorkerYMargin			= 40;		//relative to base
	private final int				initWorkerBodyXMargin		= 3;		//all following relative to initWorker(X/Y)Margin
	private final int				initWorkerBodyYMargin		= 0;
	private final int				initWorkerHeadXMargin		= 0;
	private final int				initWorkerHeadYMargin		= 20;
	private final int				initWorkerRightArmXMargin	= 20;
	private final int				initWorkerRightArmYMargin	= 62;
	private final int				initWorkerLeftArmXMargin	= 20;
	private final int				initWorkerLeftArmYMargin	= 0;
	private final int				initWorkerRightFootXMargin	= 22;
	private final int				initWorkerRightFootYMargin	= 44;
	private final int				initWorkerLeftFootXMargin	= 22;
	private final int				initWorkerLeftFootYMargin	= 18;
	
	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructor takes three parameters: Initial x coordinate, Initial y coordinate, Initial angle
	 * @param initX, initY, initA
	 */
	public GuiManualBreakout(int initX, int initY, int initA)
	{
		//Initializes the manualBreakout state to idle (i.e. "is doing anything" = false)
		this.setState("Idle");
		this.workerTask	= Task.STANDING_BY;
		isBroken	= false;
		
		//Initialize the position and orientation of the factory according to constructor parameters
		manualBreakoutXCoord		= initX;
		manualBreakoutYCoord		= initY;
		manualBreakoutAngle		= initA;
		
		//Set the position and size of the base and placeHolding rectangle to match those of the overall manualBreakout
		baseXCoord			= manualBreakoutXCoord;
		baseYCoord			= manualBreakoutYCoord;
		baseWidth			= stdBaseWidth;
		baseHeight			= stdBaseHeight;

		//Call the convenience method that sets the position of the manualBreakout components so that they appear in the proper alignment
		buildGraphic();
		
		//Define the glassTargetMargin such that the glass 
		glassTargetMargin	= this.initPlowYMargin + 28;
				
		//Load the station component images
		manualBreakoutBaseImage		= new ImageIcon("images/manualbreakout_base2.png");
		manualBreakoutPlowImage		= new ImageIcon("images/manualbreakout_machine_plow.png");
		manualBreakoutMachineImage		= new ImageIcon("images/manualbreakout_machine_base.png");
		
		workerBodyImage				= new ImageIcon("images/manualbreakout_worker_body.png");
		workerHeadImage				= new ImageIcon("images/manualbreakout_worker_head.png");
		workerRightArmImage			= new ImageIcon("images/manualbreakout_worker_rightarm.png");
		workerRightArmHammerImage	= new ImageIcon("images/manualbreakout_worker_rightarmhammer.png");
		workerRightArmHandImage		= new ImageIcon("images/manualbreakout_worker_rightarm.png");
		workerLeftArmImage			= new ImageIcon("images/manualbreakout_worker_leftarm.png");
		workerRightFootImage		= new ImageIcon("images/manualbreakout_worker_shoe.png");
		workerLeftFootImage			= new ImageIcon("images/manualbreakout_worker_shoe.png");
		
		manualBreakoutPhysicalSchematic	= new ArrayList<ArrayList<Integer>>();
	}
	
	/**
	 *	Convenience method that sets the position and size of all components of the station according to the position of the base
	 *	Arguments:	None
	 *	Returns:	void
	 */
	public void buildGraphic()
	{
		plowXCoord			= baseXCoord + initPlowXMargin;
		plowYCoord			= baseYCoord + initPlowYMargin;
		plowWidth			= stdPlowWidth;
		plowHeight			= stdPlowHeight;
		
		machineXCoord		= baseXCoord + initMachineXMargin;
		machineYCoord		= baseYCoord + initMachineYMargin;
		machineWidth		= stdMachineWidth;
		machineHeight		= stdMachineHeight;
		
		workerXCoord		= baseXCoord + initWorkerXMargin + initWorkerBodyXMargin;
		workerYCoord		= baseYCoord + initWorkerYMargin + initWorkerBodyYMargin;
		workerBodyXCoord	= baseXCoord + initWorkerXMargin + initWorkerBodyXMargin;
		workerBodyYCoord	= baseYCoord + initWorkerYMargin + initWorkerBodyYMargin;
		workerHeadXCoord	= baseXCoord + initWorkerXMargin + initWorkerHeadXMargin;
		workerHeadYCoord	= baseYCoord + initWorkerYMargin + initWorkerHeadYMargin;
		workerRightArmXCoord	= baseXCoord + initWorkerXMargin + initWorkerRightArmXMargin;
		workerRightArmYCoord	= baseYCoord + initWorkerYMargin + initWorkerRightArmYMargin;
		workerLeftArmXCoord	= baseXCoord + initWorkerXMargin + initWorkerLeftArmXMargin;
		workerLeftArmYCoord	= baseYCoord + initWorkerYMargin + initWorkerLeftArmYMargin;
		workerRightFootXCoord	= baseXCoord + initWorkerXMargin + initWorkerRightFootXMargin;
		workerRightFootYCoord	= baseYCoord + initWorkerYMargin + initWorkerRightFootYMargin;
		workerLeftFootXCoord	= baseXCoord + initWorkerXMargin + initWorkerLeftFootXMargin;
		workerLeftFootYCoord	= baseYCoord + initWorkerYMargin + initWorkerLeftFootYMargin;
		
		workerRightArmWidth	= stdWorkerRightArmWidth;
		workerRightArmHeight	= stdWorkerRightArmHeight;
		workerLeftArmWidth	= stdWorkerLeftArmWidth;
		workerLeftArmHeight	= stdWorkerLeftArmHeight;
		
	}
	
	/**
	 *	AnimatedPart interface method
	 *	Updates size, position, and orientation of factory components according to its state.
	 *	In doing so, directs animation.
	 */
	public void update()
	{
		if(hasGlass)
		{
			if(isReceivingGlass)
			{
				//If the glass is at target position (i.e. ready to be worked on)
				if(manualBreakoutGlass.getYCoord() <= baseYCoord + glassTargetMargin)
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
					
					getPart(stationDirRelToPreviousPart).finishPassing();
					//this.playJob(""); //for branch testing
					
					this.setState("Idle");
			    }
				else 
				{
					manualBreakoutGlass.moveLaterally(glassDirection, 1);
				}
		    }
			else if(isWorkingOnGlass)
			{
				if(workerTask == Task.EXTENDING_ARM)
				{
					if(workerRightArmWidth < 75)//workerRightArmExtendedWidth)
					{
						workerRightArmWidth++;
					}
					else
					{
						this.workerTask	= Task.HAMMERING;
					}
				}
				else if(workerTask == Task.HAMMERING)
				{
					if(workerJobStep < 20)
					{
						if(workerJobStep %5 == 0)
						{
							double stdDimensionRatio	= (double)workerRightArmExtendedWidth/(double)stdWorkerRightArmHeight;
							
							int	zDisplacement		= -2;
							workerRightArmWidth		= ((workerRightArmWidth + 2*zDisplacement));			 
							workerRightArmHeight	= (int) (workerRightArmWidth/stdDimensionRatio);
							
							workerJobStep++;
						}
						else
						{
							workerRightArmWidth		= workerRightArmExtendedWidth;
							workerRightArmHeight	= stdWorkerRightArmHeight;
							
							workerJobStep++;
						}
					}
					else if(workerJobStep == 20)
					{
						manualBreakoutGlass.breakoutGlassCompletely(manualBreakoutPhysicalSchematic);
						this.workerTask	= Task.RETRACTING_ARM;
						workerJobStep	= 0;
					}
					else
					{
						//THIS SHOULDN'T HAPPEN
						this.workerTask = Task.STANDING_BY;
					}
				}
				else if(workerTask == Task.RETRACTING_ARM)
				{
					if(workerRightArmWidth > 25)//stdWorkerRightArmWidth)
					{
						workerRightArmWidth--;
					}
					else
					{
						this.setState("Idle");
						controller.doneJob();
						
						this.workerTask	= Task.STANDING_BY;
					}
				}
				else if(workerTask == Task.STANDING_BY)
				{
					workerJobStep	= 0;
					workerRightArmWidth		= stdWorkerRightArmWidth;
					workerRightArmHeight	= stdWorkerRightArmHeight;
				}
			}
			else if(isTrashingGlass)
			{
				if(workerJobStep == 0 && workerRightArmWidth < workerRightArmExtendedWidth)
				{
					workerRightArmWidth++;
					workerLeftArmWidth++;
				}
				else if (workerJobStep == 0 && workerRightArmWidth >= workerRightArmExtendedWidth)
				{
					workerJobStep++;
				}
				else if(workerJobStep == 1 && workerRightArmWidth > stdWorkerRightArmWidth)
				{
					workerRightArmWidth--;
					workerLeftArmWidth--;
					manualBreakoutGlass.moveLaterally(FactoryPart.WEST, 1);
					if(manualBreakoutGlass.getXCoord() <= this.baseXCoord)
					{
						manualBreakoutGlass.setWidth(manualBreakoutGlass.getWidth()-2);
					}
				}
				else if(workerJobStep == 1 && workerRightArmWidth <= workerRightArmExtendedWidth)
				{
					workerJobStep++;
				}
				else
				{
					manualBreakoutGlass.setTreatmentCompleted(true);
					manualBreakoutGlass	= null;
					
					this.hasGlass		= false;
					this.isOn			= false;
					this.setState("Idle");
					controller.doneJob();
					workerJobStep	= 0;
				}
			}
			else if(isPassingGlass)
			{
				if(manualBreakoutGlass.getYCoord() + manualBreakoutGlass.getHeight() >= manualBreakoutYCoord + baseHeight)
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
					//System.out.println("Going to pass glass in direction: " + glassDirection + " coming from direction: " + stationDirRelToAdjacentPart);
					//adjacentParts[glassDirection].receiveGlass(manualBreakoutGlass, stationDirRelToAdjacentPart);	//Commented out here because there is no agent code in my individual branch
					adjacentParts[stationDirRelToAdjacentPart].receiveGlass(manualBreakoutGlass, glassDirection); //RHYS changed this
					
					this.setState("Going Idle");
					manualBreakoutGlass			= null;
				}
				else
				{
					plowYCoord++;
					
					if(plowYCoord + stdPlowComponentHeight >= manualBreakoutGlass.getYCoord())
					{
						manualBreakoutGlass.moveLaterally(glassDirection, -1);
					}
				}
			}
			else if(isGoingIdle)
			{
				if(plowYCoord > baseYCoord + initPlowYMargin)
				{
					plowYCoord--;
				}
				else
				{
					this.setState("Idle");
//					controller.donePass();
					isOn	= false;
				}
			}
		}
	}
	
	public void playJob(String msg, GlassDesign jobDesign)
	{
		if(msg.equals("Trash Glass"))
		{
			isOn				= true;
			this.setState("Trashing Glass");
		}
		else
		{
			isOn				= true;
			this.setState("Manually Breaking Out Glass");
			this.workerTask	= Task.EXTENDING_ARM;
			manualBreakoutPhysicalSchematic	= jobDesign.getPhysicalSchematic();
		}
	}
	
	public void playPass()
	{
		this.setState("Passing Glass");
	}

	@Override
	public void drawUnderGlass(Graphics g, ImageObserver c)
	{		
		Graphics2D	g2	= (Graphics2D) g;
		
		//Rotate the Graphics2D object so that the manualBreakout will be drawn at it's angle.
		double	manualBreakoutCenterX	= (baseXCoord + (baseWidth/2));							//Find the manualBreakout's center x coordinate
		double	manualBreakoutCenterY	= (baseYCoord + (baseHeight/2));						//Find the manualBreakout's center y coordinate
		g2.rotate(Math.toRadians(manualBreakoutAngle), manualBreakoutCenterX, manualBreakoutCenterY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		if(hasGlass)
		{
			//stationGlass.setAngle(stationAngle);										//If there is glass at this station, rotate the glass to the station's angle so the animations match
		}	

		//Draw the manualBreakout images with the following layering. The images will be drawn to fill the box described by the method parameters. Movement or scaling of the image is done elsewhere by adjusting these variables.
		//g2.draw(stationBasePlaceholder);
		g2.drawImage(workerRightFootImage.getImage(), workerRightFootXCoord, workerRightFootYCoord, stdWorkerRightFootWidth, stdWorkerRightFootHeight, c);
		g2.drawImage(workerLeftFootImage.getImage(), workerLeftFootXCoord, workerLeftFootYCoord, stdWorkerLeftFootWidth, stdWorkerLeftFootHeight, c);
		g2.drawImage(manualBreakoutBaseImage.getImage(), baseXCoord, baseYCoord, baseWidth, baseHeight, c);
		g2.drawImage(manualBreakoutPlowImage.getImage(), plowXCoord, plowYCoord, plowWidth, plowHeight, c);
		g2.drawImage(manualBreakoutMachineImage.getImage(), machineXCoord, machineYCoord, machineWidth, machineHeight, c);
		
		
		//Return the Graphics2D to the default state so that other classes drawing will not be affected by changes made here.
		g2.rotate(Math.toRadians(-1*manualBreakoutAngle), manualBreakoutCenterX, manualBreakoutCenterY);
	}

	@Override
	public void drawOverGlass(Graphics g, ImageObserver c)
	{
		
		Graphics2D	g2	= (Graphics2D) g;
		
		//Rotate the Graphics2D object so that the station will be drawn at it's angle.
		double	stationCenterX	= (baseXCoord + (baseWidth/2));							//Find the station's center x coordinate
		double	stationCenterY	= (baseYCoord + (baseHeight/2));						//Find the station's center y coordinate
		g2.rotate(Math.toRadians(manualBreakoutAngle), stationCenterX, stationCenterY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		
		
		//Draw the station images with the following layering. The images will be drawn to fill the box described by the method parameters. Movement or scaling of the image is done elsewhere by adjusting these variables.
		g2.drawImage(workerRightArmImage.getImage(), workerRightArmXCoord, workerRightArmYCoord, workerRightArmWidth, workerRightArmHeight, c);
		g2.drawImage(workerLeftArmImage.getImage(), workerLeftArmXCoord, workerLeftArmYCoord, workerLeftArmWidth, workerLeftArmHeight, c);
		g2.drawImage(workerBodyImage.getImage(), workerBodyXCoord, workerBodyYCoord, stdWorkerBodyWidth, stdWorkerBodyHeight, c);
		g2.drawImage(workerHeadImage.getImage(), workerHeadXCoord, workerHeadYCoord, stdWorkerHeadWidth, stdWorkerHeadHeight, c);
		
		//Return the Graphics2D to the default state so that other classes drawing will not be affected by changes made here.
		//g2.rotate(Math.toRadians(-1*stationAngle), -1*stationCenterX, -1*stationCenterY);
	}

	/**
	 *	Receives and parses a message from the station's controller, setting the station's state and/or calling a station action according the to message received
	 *	Arguments:	String message from the conveyor describing a state or action
	 *	Returns:	void
	 * @param msg
	 */
	public void setState(String msg) 
	{
		if (msg.equals("Receiving Glass")) 
		{
			isReceivingGlass	= true;
			isWorkingOnGlass	= false;
			isTrashingGlass		= false;
			isPassingGlass		= false;
			isGoingIdle			= false;
		}
		else if( msg.equals("Manually Breaking Out Glass"))
		{
			workerRightArmImage			= new ImageIcon("images/manualbreakout_worker_rightarmhammer.png");
			isReceivingGlass	= false;
			isWorkingOnGlass	= true;
			isTrashingGlass		= false;
			isPassingGlass		= false;
			isGoingIdle			= false;
		}
		else if(msg.equals("Passing Glass"))
		{
			isReceivingGlass	= false;
			isWorkingOnGlass	= false;
			isTrashingGlass		= false;
			isPassingGlass		= true;
			isGoingIdle			= false;
		}
		else if(msg.equals("Trashing Glass"))
		{
			workerRightArmImage			= new ImageIcon("images/manualbreakout_worker_rightarm.png");
			isReceivingGlass	= false;
			isWorkingOnGlass	= false;
			isTrashingGlass		= true;
			isPassingGlass		= false;
			isGoingIdle			= false;
		}
		else if(msg.equals("Going Idle"))
		{
			isReceivingGlass	= false;
			isWorkingOnGlass	= false;
			isTrashingGlass		= false;
			isPassingGlass		= false;
			isGoingIdle			= true;
		}
		else if(msg.equals("Idle"))
		{
			isReceivingGlass	= false;
			isWorkingOnGlass	= false;
			isTrashingGlass		= false;
			isPassingGlass		= false;
			isGoingIdle			= false;
		}
		else
		{
			isReceivingGlass	= false;
			isWorkingOnGlass	= false;
			isTrashingGlass		= false;
			isPassingGlass		= false;
			isGoingIdle			= false;
		}
	}
	
	@Override
	public void receiveGlass(GuiGlass newGlass, int direction)
	{	
		//Set the station's glass object variable to the glass object it is receiving. Set hasGlass = true;
		manualBreakoutGlass = newGlass;
		hasGlass 			= true;
		isOn				= true;
		
		//Determine the direction the glass is moving as it is being received according to the NESW direction of the object passing the glass to the station
		switch(direction)
		{
		    case FactoryPart.WEST:	glassDirection  = FactoryPart.EAST;
									break;
		    case FactoryPart.EAST:	glassDirection  = FactoryPart.WEST;
									break;
		    case FactoryPart.NORTH:	glassDirection  = FactoryPart.SOUTH;
									break;
		    case FactoryPart.SOUTH:	glassDirection  = FactoryPart.NORTH;
									break;
		    default:				break;
		}
		
		this.setState("Receiving Glass");
	}
		
	@Override
	public void finishPassing()
	{
		//Sends a message to the station's controller that it is finished passing.
		controller.donePass();		//Commented out here because there is no agent code in my individual branch
	}

	/*************************
	 * ACCESSORS & MODIFIERS *
	 *************************/
	
	@Override
	public void setX(int x)
	{
		manualBreakoutXCoord	= x;
		baseXCoord		= manualBreakoutXCoord;
		buildGraphic();
	}

	@Override
	public void setY(int y)
	{
		manualBreakoutYCoord	= y;
		baseYCoord		= manualBreakoutYCoord;
		buildGraphic();
	}

	@Override
	public int getX()
	{
		return manualBreakoutXCoord;
	}

	@Override
	public int getY()
	{
		return manualBreakoutYCoord;
	}

	@Override
	public int getWidth()
	{
		return baseWidth;
	}

	@Override
	public int getHeight()
	{
		return baseHeight;
	}

	@Override
	public int getRotation()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	// COMMENTED OUT HERE BECAUSE THERE IS NO AGENT CODE IN MY INDIVIDUAL BRANCH
	public void setOperatorController(ManualBreakoutController op)
	{
		controller	= op;		
	}
	

	@Override
	public void setRotation(int degree)
	{
		manualBreakoutAngle	= degree;		
	}


	public String getStatus()
	{
		if(isOn)
		{
			if(isReceivingGlass)
			{
				return ("ON, Receiving glass.");
			}
			else if(isWorkingOnGlass)
			{
				return ("ON, Manually breaking out glass.");
			}
			else if(isPassingGlass)
			{
				return ("ON, Passing glass.");
			}
			else if(isGoingIdle)
			{
				return ("ON, Going idle.");
			}
		}
		else
		{
			return ("OFF.");
		}
		return " ";
	}

	public boolean hasGlass()
	{
		if(manualBreakoutGlass != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void setBreak(boolean flag)
	{
		isBroken	= flag;
		
	}

	@Override
	public boolean isBroken()
	{
		return isBroken;
	}

}