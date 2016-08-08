package factory.gui;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import factory.interfaces.*;
import factory.panels.GlassDesign;
import factory.controller.*;

import factory.gui.FactoryPart;

public class GuiNCCutter extends FactoryPart implements AnimatedPart, MachineControllerInteractor
{
	/********
	 * DATA *
	 ********/
	InlineMachineController                   controller;                         //The agent controller associated with this cutter

	//The pane of glass at this cutter
	private GuiGlass                        cutterGlass;                      //The glass object being operated on by the cutter
	private int                             glassDirection;                  /**The direction the *glass* is *moving* when being *received* by the cutter */
	
	private	GlassDesign						cutterGlassDesign;
	private	ArrayList<Point2D.Double>		cutPoints;
	int										cutPointCounter;

	//cutter state flag variables
	private boolean                         hasGlass;    
	private	boolean							isOn;
	private boolean							isReceivingGlass;
	private boolean                         isGoingOnline;
	private boolean                         isCutting;
	private boolean                         isGoingIdle;
	private boolean							isPassingGlass;
	
	private boolean							isBroken;

	private ImageIcon                       cutterBaseImage;          //The image for the base of the NC Cutter situated on the sides of the conveyor
	private ImageIcon                       cutterMachineImage;    //The image for the crossbar of the NC Cutter that spans the conveyor and lies over the conveyor.
	private ImageIcon						cutterShadowImage;
	private ImageIcon                       cutterLaserHeadImage;     
	//private ImageIcon                       cutterLaserHeadOffImage;
	//private ImageIcon                       cutterLaserHeadOnlineImage; 
	//private ImageIcon                       cutterLaserHeadCuttingImage;

	private int                             cutterXCoord;                  //Top left coordinate of the NC Cutter
	private int                             cutterYCoord;               
	private int                             cutterAngle;                    //Angle of the NC CUtter relative to the factory axes

	private int                             baseXCoord;                  
	private int                             baseYCoord;
	private int								baseWidth;
	private int								baseHeight;
	private int                             machineXCoord;
	private int                             machineYCoord;
	private int                             laserHeadXCoord;
	private int                             laserHeadYCoord;
	private int                             laserPointXCoord;
	private int                             laserPointYCoord;

	private final int                       stdBaseWidth 				= 118;
	private final int                       stdBaseHeight				= 98;
	private final int                       stdMachineWidth				= 111;
	private final int                       stdMachineHeight			= 19;
	private final int						stdShadowWidth				= 111;
	private final int						stdShadowHeight				= 19; 
	private final int                       stdLaserHeadWidth			= 7;
	private final int                       stdLaserHeadHeight			= 12;;

	private final int                       initMachineXMargin        	= 4;
	private final int                       initMachineYMargin        	= 2;
	private final int                       initShadowXMargin        	= 4;
	private final int                       initShadowYMargin        	= 2;
	private final int                       initLaserHeadXMargin	    = 15;
	private final int                       initLaserHeadYMargin    	= 11;
	
	private final int						receiveTargetYMargin		= 24;
		
	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructor takes three parameters: Initial x coordinate, Initial y coordinate, Initial angle
	 * @param initX, initY, initA
	 */
	public GuiNCCutter(int initX, int initY, int initA)
	{
		//Initializes the cutter state to idle (i.e. "is doing anything" = false)
		this.setState("Idle");
		
		//Initialize the position and orientation of the factory according to constructor parameters
		cutterXCoord		= initX;
		cutterYCoord		= initY;
		cutterAngle			= initA;
		
		//Set the position and size of the base and placeHolding rectangle to match those of the overall cutter
		baseXCoord			= cutterXCoord;
		baseYCoord			= cutterYCoord;
		baseWidth			= stdBaseWidth;
		baseHeight			= stdBaseHeight;

		//Call the convenience method that sets the position of the cutter components so that they appear in the proper alignment
		buildGraphic();
		
		//Load the cutter component images
		cutterBaseImage				= new ImageIcon("images/nccutter_custom_base.png");
		cutterMachineImage			= new ImageIcon("images/nccutter_custom_machine.png");  
		cutterShadowImage		= new ImageIcon("images/nccutter_custom_shadow.png");
		cutterLaserHeadImage		= new ImageIcon("images/nccutter_custom_laserhead.png");    
		//cutterLaserHeadOnlineImage	= new ImageIcon("images/nccutter_laserhead_yellow.png");    
		//cutterLaserHeadCuttingImage	= new ImageIcon("images/nccutter_laserhead_red.png");
		//cutterLaserHeadOffImage		= cutterLaserHeadOffImage;
	}
	
	/**
	 *	Convenience method that sets the position and size of all components of the NC Cutter according to the position of the base
	 *	Arguments:	None
	 *	Returns:	void
	 */
	public void buildGraphic()
	{
		machineXCoord			= baseXCoord + initMachineXMargin;
		machineYCoord			= baseYCoord + initMachineYMargin;
		
		laserHeadXCoord		= baseXCoord + initLaserHeadXMargin;
		laserHeadYCoord		= baseYCoord + initLaserHeadYMargin;
		
		laserPointXCoord	= laserHeadXCoord + (stdLaserHeadWidth / 2);		
		laserPointYCoord	= laserHeadYCoord + (stdLaserHeadHeight);
	}
		
	public void update()
	{
		//System.out.println(isOn);
		if(hasGlass)
		{
			if(isReceivingGlass)
			{
				if(cutterGlass.getYCoord() == laserPointYCoord)
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
					cutterGlass.moveLaterally(glassDirection, 1);
				}
			}
			else if(isGoingOnline)
			{
				//cutterLaserHeadImage	= cutterLaserHeadOnlineImage;
				if(laserPointXCoord < cutterGlass.getXCoord() + cutPoints.get(0).getX())
				{
					moveLaserHead(1);
				}
				if(laserPointYCoord < cutterGlass.getYCoord() + cutPoints.get(0).getY())
				{
					moveLaserMachine(1);
				}
				else
				{
					this.setState("Cutting Glass");
				}
			}
			else if(isCutting)
			{
				//SUPER HACKED TO GET IT INTEGRATED FOR OTHERS
				if(this.cutPointCounter < this.cutPoints.size()-1)
				{
					cutterGlass.cutGlassAtPoint(cutPoints.get(cutPointCounter));
					moveLaserHead((int)(cutterGlass.getXCoord() + cutPoints.get(cutPointCounter+1).getX()) - laserPointXCoord);
					moveLaserMachine((int)(cutterGlass.getYCoord() + cutPoints.get(cutPointCounter+1).getY()) - laserPointYCoord);
					cutPointCounter++;
				}
				else
				{
					cutterGlass.cutGlassAtPoint(cutPoints.get(cutPointCounter));
					this.setState("Finishing Cutting Glass");
					cutPointCounter	= 0;
				}
			}
			else if(isGoingIdle)
			{
				//cutterLaserHeadImage	= cutterLaserHeadOffImage;
				
				if(laserHeadXCoord > baseXCoord + initLaserHeadXMargin)
				{
					moveLaserHead(-1);
				}
				if(machineYCoord > cutterYCoord + initMachineYMargin)
				{
					moveLaserMachine(-1);
				}
				if( (laserHeadXCoord == baseXCoord + initLaserHeadXMargin) && (machineYCoord == cutterYCoord + initMachineYMargin))//&& (laserRightHeadXCoord == baseXCoord + initLaserRightHeadXMargin) )
				{
					this.setState("Idle");
					controller.doneJob();
					//this.playPass(); for branch testing
				}
			}
			else if(isPassingGlass)
			{
				if(cutterGlass.getYCoord() == cutterYCoord)
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
					adjacentParts[glassDirection].receiveGlass(cutterGlass, stationDirRelToAdjacentPart);	//Commented out here because there is no agent code in my individual branch
					
					this.setState("Idle");
					cutterGlass			= null;
				}
				else
				{
					cutterGlass.moveLaterally(glassDirection, 1);
				}
			}
			else 
			{
				//cutterLaserHeadImage	= cutterLaserHeadOffImage;
			}
		}
	}

	@Override
	public void drawUnderGlass(Graphics g, ImageObserver c)
	{
		Graphics2D	g2	= (Graphics2D) g;
		
		//Rotate the Graphics2D object so that the cutter will be drawn at it's angle.
		double	cutterCenterX	= (baseXCoord + (baseWidth/2));							//Find the station's center x coordinate
		double	cutterCenterY	= (baseYCoord + (baseHeight/2));						//Find the station's center y coordinate
		g2.rotate(Math.toRadians(cutterAngle), cutterCenterX, cutterCenterY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		
		//Draw the cutter images with the following layering. The images will be drawn to fill the box described by the method parameters. Movement or scaling of the image is done elsewhere by adjusting these variables.
		//g2.draw(cutterBasePlaceholder);
		g2.drawImage(cutterBaseImage.getImage(), baseXCoord, baseYCoord, baseWidth, baseHeight, c);

		//Return the Graphics2D to the default state so that other classes drawing will not be affected by changes made here.
		g2.rotate(Math.toRadians(-1*cutterAngle), cutterCenterX, cutterCenterY);
	}

	@Override
	public void drawOverGlass(Graphics g, ImageObserver c)
	{
		Graphics2D	g2	= (Graphics2D) g;
		
		//Rotate the Graphics2D object so that the cutter will be drawn at it's angle.
		double	cutterCenterX	= (baseXCoord + (baseWidth/2));							//Find the station's center x coordinate
		double	cutterCenterY	= (baseYCoord + (baseHeight/2));						//Find the station's center y coordinate
		g2.rotate(Math.toRadians(cutterAngle), cutterCenterX, cutterCenterY);		//Rotate the Graphics2D object. Rotates about the glass's center coordinate.
		
		//Draw the cutter images with the following layering. The images will be drawn to fill the box described by the method parameters. Movement or scaling of the image is done elsewhere by adjusting these variables.
		g2.drawImage(cutterMachineImage.getImage(), machineXCoord, machineYCoord, stdMachineWidth, stdMachineHeight, c);
		g2.drawImage(cutterLaserHeadImage.getImage(), laserHeadXCoord, laserHeadYCoord, stdLaserHeadWidth, stdLaserHeadHeight, c);
		//g2.drawImage(cutterLaserHeadImage.getImage(), laserRightHeadXCoord, laserRightHeadYCoord, stdLaserHeadWidth, stdLaserHeadHeight, c);
		
		//Return the Graphics2D to the default state so that other classes drawing will not be affected by changes made here.
		g2.rotate(Math.toRadians(-1*cutterAngle), cutterCenterX, cutterCenterY);		
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
			cutterGlassDesign	= jobDesign;
			cutPoints			= cutterGlassDesign.getBorderPoints();
			
			isOn				= true;
			this.setState("Preparing to Cut Glass");
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
			isReceivingGlass		= true;
			isGoingOnline			= false;
			isCutting				= false;
			isGoingIdle				= false;
			isPassingGlass			= false;
		}
		
		if(msg.equals("Preparing to Cut Glass"))
		{
			isReceivingGlass		= false;
			isGoingOnline			= true;
			isCutting				= false;
			isGoingIdle				= false;
			isPassingGlass			= false;
		}
		
		else if (msg.equals("Cutting Glass")) 
		{
			isReceivingGlass		= false;
			isGoingOnline			= false;
			isCutting				= true;
			isGoingIdle				= false;
			isPassingGlass			= false;
		}
		else if(msg.equals("Finishing Cutting Glass"))
		{
			isReceivingGlass		= false;
			isGoingOnline			= false;
			isCutting				= false;
			isGoingIdle				= true;
			isPassingGlass			= false;
		}
		else if(msg.equals("Passing Glass"))
		{
			isReceivingGlass		= false;
			isGoingOnline			= false;
			isCutting				= false;
			isGoingIdle				= false;
			isPassingGlass			= true;
		}
		else if(msg.equals("Idle"))
		{
			isReceivingGlass		= false;
			isGoingOnline			= false;
			isCutting				= false;
			isGoingIdle				= false;
			isPassingGlass			= false;
		}
	}

	@Override
	public void receiveGlass(GuiGlass glass, int direction)
	{
		hasGlass			= true;
		cutterGlass			= glass;
		isOn				= true;
		this.setState("Receiving Glass");
		
		//System.out.println(isOn);
		//if(isOn)
		//{
		//	isGoingOnline	= true;
		//}
		
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
		
		//getPart(direction).finishPassing();		
	}

	@Override
	public void finishPassing()
	{
		//Sends a message to the station's controller that it is finished passing.
		controller.donePass();		//Commented out here because there is no agent code in my individual branch
		this.setState("Idle");
		isOn	= false;
	}

	public void moveLaserHead(int headXDisplacement)
	{
		laserHeadXCoord 	+= headXDisplacement;
		laserPointXCoord	+= headXDisplacement;
	}
	
	public void moveLaserMachine(int machineYDisplacement)
	{
		machineYCoord	+= machineYDisplacement;
		laserHeadYCoord	+= machineYDisplacement;
		laserPointYCoord += machineYDisplacement;
	}
	
	public void moveRightLaserHead(int rightHeadDisplacement)
	{
		//laserRightHeadXCoord 	+= rightHeadDisplacement;
		//laserRightPointXCoord	+= rightHeadDisplacement;
	}

	@Override
	public void setX(int x)
	{
		cutterXCoord	= x;
		baseXCoord		= cutterXCoord;
		buildGraphic();
	}



	@Override
	public void setY(int y)
	{
		cutterYCoord	= y;
		baseYCoord		= cutterYCoord;
		buildGraphic();		
	}



	@Override
	public int getX()
	{
		// TODO Auto-generated method stub
		return this.cutterXCoord;
	}



	@Override
	public int getY()
	{
		// TODO Auto-generated method stub
		return this.cutterYCoord;
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
			else if(isGoingOnline)
			{
				return ("ON, Going online.");
			}
			if(isCutting)
			{
				return ("ON, Cutting.");
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
		if(cutterGlass != null)
		{
			return true;
		}
		else
		{
			return false;
		}
		//return hasGlass;
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
	
//END CLASS
}
