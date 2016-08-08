package factory.panels;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.Timer;
import javax.swing.text.JTextComponent;

public class GlassGridPanel extends JPanel implements MouseListener, MouseMotionListener
{
	ArrayList<ArrayList<GlassGridPixel>>	pixels	= new ArrayList<ArrayList<GlassGridPixel>>();
	ArrayList<ArrayList<Integer>>			setupPixelSchematic	= new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>>			inputPixelSchematic	= new ArrayList<ArrayList<Integer>>();
	String									editorType;
	private boolean									wasEditted;
	
	int										numXPixels;
	int										numYPixels;
	int										pixelWidth;
	int										pixelHeight;
	Rectangle2D.Double						backgroundRectangle;
	Color									bgColor		= new Color(79, 129, 189);
	boolean									mouseIsPressed;
	
	Color									cutColor;
	Color									brushColor;
	int										brushWidth;
	public final static int					PAINTBRUSH_WIDTH	= 5;
	public final static int					PENCIL_WIDTH		= 1;
	
	public GlassGridPanel(String type, int panelWidth, int panelHeight, int numX, int numY)
	{
		editorType	= type;
		
		numXPixels	= numX;
		numYPixels	= numY;
		pixelWidth	= panelWidth/numXPixels;
		pixelHeight	= panelHeight/numYPixels;
		
		cutColor	= new Color(85, 110, 145);
		brushColor	= new Color(255, 0, 0);
		brushWidth	= PENCIL_WIDTH;
		
		for(int r = 0; r < numYPixels; r++)
		{
			pixels.add(new ArrayList<GlassGridPixel>());
			inputPixelSchematic.add(new ArrayList<Integer>());
			
			for(int c = 0; c < numXPixels; c++)
			{
				pixels.get(r).add(new GlassGridPixel(c*pixelWidth, r*pixelHeight, pixelWidth, pixelHeight));
				inputPixelSchematic.get(r).add(new Integer(8));
			}
		}
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void clearPixels()
	{
		for(int r = 0; r < numYPixels; r++)
		{
			for(int c = 0; c < numXPixels; c++)
			{
				if(editorType.equals("Cut"))
				{
					pixels.get(r).get(c).setPixelColor(pixels.get(r).get(c).getEnabledPixelColor());
					inputPixelSchematic.get(r).set(c, new Integer(8));
				}
				else if(editorType.equals("Drill"))
				{
					if(setupPixelSchematic.get(r).get(c) != 0 && setupPixelSchematic.get(r).get(c) != 2)
					{
						pixels.get(r).get(c).setIsEnabled(true);
						pixels.get(r).get(c).setPixelColor(pixels.get(r).get(c).getEnabledPixelColor());
						inputPixelSchematic.get(r).set(c, new Integer(1));
					}
				}
				else if(editorType.equals("Paint"))
				{
					if(setupPixelSchematic.get(r).get(c) == 1)
					{
						pixels.get(r).get(c).setIsEnabled(true);
						pixels.get(r).get(c).setPixelColor(pixels.get(r).get(c).getEnabledPixelColor());
					}
				}
			}
		}
		
		wasEditted	= false;
	}
	
	public void setSetupPixelSchematic(ArrayList<ArrayList<Integer>> setupMatrix)
	{
		setupPixelSchematic	= setupMatrix;
		for(int r = 0; r < setupMatrix.size(); r++)
		{
			for(int c = 0; c < setupMatrix.get(0).size(); c++)
			{
				if((int)setupMatrix.get(r).get(c) == 0 || (int)setupMatrix.get(r).get(c) == 2 || (int)setupMatrix.get(r).get(c) == 3 || (int)setupMatrix.get(r).get(c) == 4)
				{
					pixels.get(r).get(c).setIsEnabled(false);
				}
			}
		}
		
		inputPixelSchematic	= setupPixelSchematic;
	}
	
	public void setBrushColor(Color c)
	{
		brushColor	= c;
	}
	
	public void setBrushWidth(int w)
	{
		brushWidth	= w;
	}
	
	public ArrayList<ArrayList<GlassGridPixel>> getPixels()
	{
		return pixels;
	}
	
	public ArrayList<ArrayList<Integer>> getInputPixelSchematic()
	{
		return inputPixelSchematic;
	}
	
	public boolean getWasEditted()
	{
		return wasEditted;
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		for(int r = 0; r < pixels.size(); r++)
		{
			for(GlassGridPixel p : pixels.get(r))
			{
				g2.setColor(p.getPixelColor());
				g2.fill(p.getPixel());
				g2.setColor(p.getOutlineColor());
				g2.draw(p.getPixel());
			}
		}
	}

	/******************
	 * MOUSE LISTENER *
	 ******************/
	
	public void mouseClicked(MouseEvent e)
	{
		wasEditted	= true;
		
		if(editorType.equals("Cut"))
		{
			int	x	= e.getX()/pixelWidth;
			int y	= e.getY()/pixelHeight;
			if(x >= 0 && x < numXPixels && y >= 0 && y < numYPixels)
			{
				if(pixels.get(y).get(x).getIsEnabled())
				{
					if(inputPixelSchematic.get(y).get(x) != 2)
					{
						pixels.get(y).get(x).setPixelColor(cutColor);
						inputPixelSchematic.get(y).set(x, new Integer(2));
					}
					else
					{
						pixels.get(y).get(x).setPixelColor(pixels.get(y).get(x).getEnabledPixelColor());
						inputPixelSchematic.get(y).set(x, new Integer(8));
					}
					
				}
			}
		}
		else if(editorType.equals("Drill"))
		{
			System.out.println("DRILL CLICKED");
			
			int	x	= e.getX()/pixelWidth;
			int y	= e.getY()/pixelHeight;
			if(x >= 0 && x < numXPixels && y >= 0 && y < numYPixels)
			{
				if(pixels.get(y).get(x).getIsEnabled())
				{
					//CHECK THE SELECTED DRILL LOCATION IS NOT NEXT TO EDGE OF GLASS
					boolean	validHoleLocation	= true;
					for(int r = y - 3; r <= y + 3; r++)
					{
						if(r < 0 || r >= numYPixels)
						{
							validHoleLocation	= false;
						}
						for(int c = x - 3; c <= x + 3; c++)
						{
							if(c < 0 || c >= numXPixels)
							{
								validHoleLocation	= false;
							}
							
							if((int)setupPixelSchematic.get(r).get(c) != 1)
							{
								validHoleLocation	= false;
							}
						}
					}
					
					if(validHoleLocation)
					{
						for(int r = y - 2, c = x - 1; c <= x + 1; c++)
						{
							inputPixelSchematic.get(r).set(c, new Integer(4));
							pixels.get(r).get(c).setPixelColor(pixels.get(r).get(c).getDisabledPixelColor());
						}	
						for(int r = y - 1; r <= y + 1; r++)
						{
							for(int c = x - 2; c <= x + 2; c++)
							{
								inputPixelSchematic.get(r).set(c, new Integer(4));
								pixels.get(r).get(c).setPixelColor(pixels.get(r).get(c).getDisabledPixelColor());
							}
						}
						for(int r = y + 2, c = x - 1; c <= x + 1; c++)
						{
							inputPixelSchematic.get(r).set(c, new Integer(4));
							pixels.get(r).get(c).setPixelColor(pixels.get(r).get(c).getDisabledPixelColor());
						}
						
						inputPixelSchematic.get(y).set(x, new Integer(3));
						
						for(int r = 0; r < inputPixelSchematic.size(); r++)
						{
							for(int c = 0; c < inputPixelSchematic.get(0).size(); c++)
							{
								System.out.print(inputPixelSchematic.get(r).get(c) + " ");
							}
							System.out.print('\n');
						}
					}
				}
			}
		}
		else if(editorType.equals("Paint"))
		{
			System.out.println("PAINT CLICKED " + brushWidth);
			
			int	x	= e.getX()/pixelWidth;
			int y	= e.getY()/pixelHeight;
			if(x >= 0 && x < numXPixels && y >= 0 && y < numYPixels)
			{
				if(pixels.get(y).get(x).getIsEnabled())
				{
					if(brushWidth == PAINTBRUSH_WIDTH)
					{
						for(int r = y - 2, c = x - 1; c <= x + 1; c++)
						{
							if(r >= 0 && c >= 0 && r < pixels.size() && c < pixels.get(r).size())
							{
								if(pixels.get(r).get(c).getIsEnabled())
								{
									pixels.get(r).get(c).setPixelColor(brushColor);
								}
							}
						}	
						for(int r = y - 1; r <= y + 1; r++)
						{
							for(int c = x - 2; c <= x + 2; c++)
							{
								if(r >= 0 && c >= 0 && r < pixels.size() && c < pixels.get(r).size())
								{
									if(pixels.get(r).get(c).getIsEnabled())
									{
										pixels.get(r).get(c).setPixelColor(brushColor);
									}
								}
							}
						}
						for(int r = y + 2, c = x - 1; c <= x + 1; c++)
						{
							if(r >= 0 && c >= 0 && r < pixels.size() && c < pixels.get(r).size())
							{
								if(pixels.get(r).get(c).getIsEnabled())
								{
									pixels.get(r).get(c).setPixelColor(brushColor);
								}
							}
						}
					}
					else if(brushWidth == PENCIL_WIDTH)
					{
						pixels.get(y).get(x).setPixelColor(brushColor);
					}
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e)
	{	}
	public void mouseExited(MouseEvent e)
	{	}
	public void mousePressed(MouseEvent e)
	{	}
	public void mouseReleased(MouseEvent e)
	{	}
	
	public void mouseDragged(MouseEvent e)
	{
		wasEditted	= true;
	
		if(editorType.equals("Cut"))
		{
			int	x	= e.getX()/pixelWidth;
			int y	= e.getY()/pixelHeight;
			if(x >= 0 && x < numXPixels && y >= 0 && y < numYPixels)
			{
				if(pixels.get(y).get(x).getIsEnabled())
				{
						pixels.get(y).get(x).setPixelColor(cutColor);
						inputPixelSchematic.get(y).set(x, new Integer(2));
				}
			}
		}
		else if(editorType.equals("Drill"))
		{
			
		}
		else if(editorType.equals("Paint"))
		{
			System.out.println("PAINT DRAGGED");
			
			int	x	= e.getX()/pixelWidth;
			int y	= e.getY()/pixelHeight;
			if(x >= 0 && x < numXPixels && y >= 0 && y < numYPixels)
			{
				if(pixels.get(y).get(x).getIsEnabled())
				{
					if(brushWidth == PAINTBRUSH_WIDTH)
					{
						for(int r = y - 2, c = x - 1; c <= x + 1; c++)
						{
							if(r >= 0 && c >= 0 && r < pixels.size() && c < pixels.get(r).size())
							{
								if(pixels.get(r).get(c).getIsEnabled())
								{
									pixels.get(r).get(c).setPixelColor(brushColor);
								}
							}
						}	
						for(int r = y - 1; r <= y + 1; r++)
						{
							for(int c = x - 2; c <= x + 2; c++)
							{
								if(r >= 0 && c >= 0 && r < pixels.size() && c < pixels.get(r).size())
								{
									if(pixels.get(r).get(c).getIsEnabled())
									{
										pixels.get(r).get(c).setPixelColor(brushColor);
									}
								}
							}
						}
						for(int r = y + 2, c = x - 1; c <= x + 1; c++)
						{
							if(r >= 0 && c >= 0 && r < pixels.size() && c < pixels.get(r).size())
							{
								if(pixels.get(r).get(c).getIsEnabled())
								{
									pixels.get(r).get(c).setPixelColor(brushColor);
								}
							}
						}
					}
					else if(brushWidth == PENCIL_WIDTH)
					{
						pixels.get(y).get(x).setPixelColor(brushColor);
					}
				}
			}
		}
	}

	public void mouseMoved(MouseEvent e)
	{	}
	
}
