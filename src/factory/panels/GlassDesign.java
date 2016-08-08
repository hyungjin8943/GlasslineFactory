package factory.panels;
import java.util.*;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.Serializable;

import javax.swing.ImageIcon;

import factory.gui.GuiGlass;

public class GlassDesign implements Serializable 
{
	private	String							designName;
	
	private	ImageIcon						previewImage;
	
	private	ArrayList<ArrayList<Integer>>	physicalSchematic	= new ArrayList<ArrayList<Integer>>();
	private	ArrayList<ArrayList<Color>>		paintSchematic		= new ArrayList<ArrayList<Color>>();
	private ArrayList<String>				treatmentSchematic	= new ArrayList<String>();
	private	ArrayList<Point2D.Double>		borderPoints		= new ArrayList<Point2D.Double>();
	private	ArrayList<Point2D.Double>		drillPoints			= new ArrayList<Point2D.Double>();
	private	int								stdPixelWidth;
	private int								stdPixelHeight;
	private boolean							validShape;
	
	public GlassDesign()
	{	
		physicalSchematic	= new ArrayList<ArrayList<Integer>>();
		paintSchematic		= new ArrayList<ArrayList<Color>>();
		treatmentSchematic	= new ArrayList<String>();
		
		designName	= "Default";
		
		previewImage	= new ImageIcon("images/glass_opaque.png");
		
		//treatmentSchematic.add("Cut");
		//treatmentSchematic.add("Breakout");
		treatmentSchematic.add("Drill");
		treatmentSchematic.add("Grind");
		treatmentSchematic.add("Cross-Seam");
		treatmentSchematic.add("Paint");
		treatmentSchematic.add("Wash");
		treatmentSchematic.add("UV Treat");
		treatmentSchematic.add("Bake");
	}
	
	public GlassDesign(ArrayList<ArrayList<Integer>> pixelMatrix) throws NoClosedShapeException, MultipleClosedShapeException
	{
		initializePixelSchematic(pixelMatrix);
		//loadPixelSchematic(pixelMatrix);
		
		//DEEP COPY pixelMatrix into physical schematic
		for(int r = 0; r < pixelMatrix.size(); r++)
		{
			this.physicalSchematic.add(new ArrayList<Integer>());
			for(int c = 0; c < pixelMatrix.get(0).size(); c++)
			{
				Integer	val	= new Integer( pixelMatrix.get(r).get(c).intValue() );
				this.physicalSchematic.get(r).add(c, val);
			}
		}
		
		//physicalSchematic	= pixelMatrix;
		paintSchematic		= new ArrayList<ArrayList<Color>>();
		treatmentSchematic	= new ArrayList<String>();
		
		try
		{
			checkClosedShape();
			cleanUpBorder();
			checkOneContiguousShape();
			validShape	= true;
		}
		catch (NoClosedShapeException e)
		{
			System.out.println("No closed shape containing at least one pixel.");
			validShape	= false;
			throw e;			
		}
		catch(MultipleClosedShapeException e)
		{
			System.out.println("Multiple closed shapes containing at least one pixel.");
			validShape	= false;
			throw e;
		}
	}
	
	public void initializePixelSchematic(ArrayList<ArrayList<Integer>> pixelMatrix)
	{
		stdPixelHeight	= pixelMatrix.size();
		stdPixelWidth	= -1;
		for(int i = 0; i < pixelMatrix.size(); i++)
		{
			if(pixelMatrix.get(i).size() > stdPixelWidth)
			{
				stdPixelWidth	= pixelMatrix.get(i).size();
			}
		}
		
		/*
		for(int r = 0; r < stdPixelHeight; r++)
		{
			pixelSchematic.add(new ArrayList<Integer>());
			
			for (int c = 0; c < stdPixelWidth; c++)
			{
				pixelSchematic.get(r).add(new Integer(0));
			}
		}
		*/
	}
	
	public void validateInputPixelSchematic()
	{
		
	}
	
	public void checkClosedShape() throws NoClosedShapeException
	{
	  /////////////////////////////
	  // BUILD FLOOD FILL MATRIX //
	  /////////////////////////////
		
		ArrayList<ArrayList<Integer>>	floodMatrix	= new ArrayList<ArrayList<Integer>>();
				
		for(int r = 0; r < stdPixelHeight+2; r++)
		{
			floodMatrix.add(new ArrayList<Integer>());
			
			if(r == 0 || r == (stdPixelHeight+1) )
			{
				for(int c = 0; c < stdPixelWidth+2; c++)
				{
					floodMatrix.get(r).add(c, new Integer(8));
				}
			}
			else
			{
				for(int c = 0; c < stdPixelWidth+2; c++)
				{
					if(c == 0 || c == stdPixelWidth+1)
					{
						floodMatrix.get(r).add(c, new Integer(8));
					}
					else
					{
						floodMatrix.get(r).add(c, physicalSchematic.get(r-1).get(c-1));
					}
				}
			}
		}
		
		//PRINT FLOOD MATRIX FOR DEBUGGING
		//System.out.println("FLOOD MATRIX:");
		//for(int r = 0; r < stdPixelHeight+2; r++)
		//{
		//	for(int c = 0; c < stdPixelWidth+2; c++)
		//	{
		//		System.out.print(floodMatrix.get(r).get(c) + " ");
		//	}
		//	System.out.print('\n');
		//}
		
	  ////////////////////////////////////////
	  // PERFORM FLOOD FILL ON FLOOD MATRIX //
	  ////////////////////////////////////////
		//System.out.println("Not broken yet 1");
		floodFill(floodMatrix, 0, 0, 0, floodMatrix.size()/2, floodMatrix.get(0).size());
		floodFill(floodMatrix, 0, floodMatrix.size()/2, 0, floodMatrix.size(), floodMatrix.get(0).size());
		
		//PRINT FLOOD MATRIX FOR DEBUGGING
		//System.out.println("FLOODED MATRIX:");
		//for(int r = 0; r < stdPixelHeight+2; r++)
		//{
		//	for(int c = 0; c < stdPixelWidth+2; c++)
		//	{
		//		System.out.print(floodMatrix.get(r).get(c) + " ");
		//	}
		//	System.out.print('\n');
		//}
		
	  /////////////////////////////////////////////
	  // CHECK INTERIOR PIXELS EXIST AFTER FLOOD //
	  /////////////////////////////////////////////
		boolean	interiorFound	= false;
		for(int r = 0; r < floodMatrix.size(); r++)
		{
			for(int c = 0; c < floodMatrix.get(0).size(); c++)
			{
				if((int)floodMatrix.get(r).get(c) == 8)
				{
					interiorFound	= true;
				}
			}
		}
		if(!interiorFound)
		{
			throw new NoClosedShapeException();
		}
		
	  /////////////////////////////////////////////
	  // CHECK INTERIOR PIXELS EXIST AFTER FLOOD //
	  /////////////////////////////////////////////
		for(int floodR = 1, schemR = 0; schemR < physicalSchematic.size(); floodR++, schemR++)
		{
			for(int floodC = 1, schemC = 0; schemC < physicalSchematic.get(0).size(); floodC++, schemC++)
			{
				physicalSchematic.get(schemR).set(schemC, floodMatrix.get(floodR).get(floodC));
			}
		}
		
	}
	
	public void cleanUpBorder()
	{
		for(int r = 0; r < physicalSchematic.size(); r++)
		{
			for(int c = 0; c < physicalSchematic.get(0).size(); c++)
			{
				if((int)physicalSchematic.get(r).get(c) == 2)
				{
					//if this 2 pixel does not border an interior (8) pixel, make it an exterior (0) pixel
					boolean	extraneousBorderPixel	= true;
					if((r-1) >= 0 && (c-1) >= 0)
					{
						if((int)physicalSchematic.get(r-1).get(c-1) == 8)
						{
							extraneousBorderPixel	= false;
						}
					}
					if((r-1) >= 0)
					{
						if((int)physicalSchematic.get(r-1).get(c) == 8)
						{
							extraneousBorderPixel	= false;
						}
					}
					if((r-1) >= 0 && (c+1) < physicalSchematic.get(0).size())
					{
						if((int)physicalSchematic.get(r-1).get(c+1) == 8)
						{
							extraneousBorderPixel	= false;
						}
					}
					if((c-1) >= 0)
					{
						if((int)physicalSchematic.get(r).get(c-1) == 8)
						{
							extraneousBorderPixel	= false;
						}
					}
					if((c+1) < physicalSchematic.get(0).size())
					{
						if((int)physicalSchematic.get(r).get(c+1) == 8)
						{
							extraneousBorderPixel	= false;
						}
					}
					if((r+1) < physicalSchematic.size() && (c-1) >= 0)
					{
						if((int)physicalSchematic.get(r+1).get(c-1) == 8)
						{
							extraneousBorderPixel	= false;
						}
					}
					if((r+1) < physicalSchematic.size())
					{
						if((int)physicalSchematic.get(r+1).get(c) == 8)
						{
							extraneousBorderPixel	= false;
						}
					}
					if((r+1) < physicalSchematic.size() && (c+1) < physicalSchematic.get(0).size())
					{
						if((int)physicalSchematic.get(r+1).get(c+1) == 8)
						{
							extraneousBorderPixel	= false;
						}
					}
					
					if(extraneousBorderPixel)
					{
						physicalSchematic.get(r).set(c, new Integer(0));
					}
				}
			}
		}
	}
	
	public void checkOneContiguousShape() throws MultipleClosedShapeException
	{
		//Find an interior pixel and flood fill this first encountered interior shape.
		boolean	firstShapeFilled	= false;
		for(int r = 0; r < physicalSchematic.size(); r++)
		{
			for(int c = 0; c < physicalSchematic.get(0).size(); c++)
			{
				if(physicalSchematic.get(r).get(c) == 8 && !firstShapeFilled)
				{
					//System.out.println("Not broken yet 2");
					floodFill(physicalSchematic, 1, r, c, physicalSchematic.size(), physicalSchematic.get(0).size());
					firstShapeFilled	= true;
				}
				else if(physicalSchematic.get(r).get(c) == 8 && firstShapeFilled)
				{
					throw new MultipleClosedShapeException();
				}
			}
		}		
	}
	
	public void floodFill(ArrayList<ArrayList<Integer>> floodMatrix, int floodVal, int curRow, int curCol, int boundRow, int boundCol)
	{
		//System.out.println("Flood fill at point ("+curRow + ", "+ curCol+ ")");
		if(curRow < 0 || curRow >= boundRow || curCol < 0 || curCol >= boundCol)
		{
			return;
		}
		else
		{
			if((int)floodMatrix.get(curRow).get(curCol) != 2 && (int)floodMatrix.get(curRow).get(curCol) != floodVal)
			{
				floodMatrix.get(curRow).set(curCol, new Integer(floodVal));
				//System.out.print(": Filled this coord.\n");
				floodFill(floodMatrix, floodVal, curRow, curCol + 1, boundRow, boundCol);
				floodFill(floodMatrix, floodVal, curRow + 1, curCol, boundRow, boundCol);
				floodFill(floodMatrix, floodVal, curRow - 1, curCol, boundRow, boundCol);
				floodFill(floodMatrix, floodVal, curRow, curCol - 1, boundRow, boundCol);
				
			}
			else
			{
				return;
			}	
		}		
	}
	
	public void findBorderPoints()
	{
		Point2D.Double	startBorderPoint	= new Point2D.Double();
		Point2D.Double	previousBorderPoint	= new Point2D.Double();
		Point2D.Double	tailBorderPoint		= new Point2D.Double();
		Point2D.Double	cabooseBorderPoint	= new Point2D.Double();
		Point2D.Double	currentBorderPoint	= new Point2D.Double(-1, -1);
		Point2D.Double	nextBorderPoint		= new Point2D.Double();
		
		//find top-leftmost border point
		boolean	startFound	 = false;
		for(int r = 0; r < physicalSchematic.size() && !startFound; r++)
		{
			for(int c = 0; c < physicalSchematic.get(0).size() && !startFound; c++)
			{
				if(physicalSchematic.get(r).get(c) == 2)
				{
					startBorderPoint	= new Point2D.Double(c, r);
					startFound	= true;
					//System.out.println("ADDING START POINT (" + startBorderPoint.getX() + "," + startBorderPoint.getY() + ")");
				}
			}
		}
		
		currentBorderPoint	= new Point2D.Double(startBorderPoint.getX(), startBorderPoint.getY());
		borderPoints.add(currentBorderPoint);
		nextBorderPoint		= findNextBorderPoint(currentBorderPoint, startBorderPoint, startBorderPoint, startBorderPoint);
		//System.out.println("1st NEXT POINT: " + nextBorderPoint);
		cabooseBorderPoint	= new Point2D.Double(startBorderPoint.getX(), startBorderPoint.getY());
		tailBorderPoint		= new Point2D.Double(startBorderPoint.getX(), startBorderPoint.getY());
		previousBorderPoint	= new Point2D.Double(currentBorderPoint.getX(), currentBorderPoint.getY());
		currentBorderPoint	= new Point2D.Double(nextBorderPoint.getX(), nextBorderPoint.getY());
		//System.out.println("1st TAIL POINT: " + tailBorderPoint);
		//System.out.println("1st PREV POINT: " + previousBorderPoint);
		//System.out.println("1st CURRENT POINT: " + currentBorderPoint);

		/*
		borderPoints.add(currentBorderPoint);
		nextBorderPoint		= findNextBorderPoint(currentBorderPoint, previousBorderPoint, startBorderPoint);
		System.out.println("2nd NEXT POINT: " + nextBorderPoint);
		tailBorderPoint		= new Point2D.Double(previousBorderPoint.getX(), previousBorderPoint.getY())
		previousBorderPoint	= new Point2D.Double(currentBorderPoint.getX(), currentBorderPoint.getY());
		currentBorderPoint	= new Point2D.Double(nextBorderPoint.getX(), nextBorderPoint.getY());
		System.out.println("2nd PREV POINT: " + previousBorderPoint);
		System.out.println("2nd CURRENT POINT: " + currentBorderPoint);
		*/
		
		int	doIt	= 0;
		while(!currentBorderPoint.equals(startBorderPoint))
		//while(doIt < 6)
		{
			//System.out.println("ADDING NEXT POINT (" + currentBorderPoint.getX() + "," + currentBorderPoint.getY() + ")");
			borderPoints.add(currentBorderPoint);
			nextBorderPoint		= findNextBorderPoint(currentBorderPoint, previousBorderPoint, tailBorderPoint, cabooseBorderPoint);
			
			cabooseBorderPoint	= new Point2D.Double(tailBorderPoint.getX(), tailBorderPoint.getY());
			tailBorderPoint		= new Point2D.Double(previousBorderPoint.getX(), previousBorderPoint.getY());
			previousBorderPoint	= new Point2D.Double(currentBorderPoint.getX(), currentBorderPoint.getY());
			currentBorderPoint	= new Point2D.Double(nextBorderPoint.getX(), nextBorderPoint.getY());
			//System.out.println("UPDATED POINTS: CURRENT (" + currentBorderPoint.getX() + "," + currentBorderPoint.getY() + ") PREVIOUS (" + previousBorderPoint.getX() + "," + previousBorderPoint.getY() + ")  TAIL (" + tailBorderPoint.getX() + "," + tailBorderPoint.getY() + ")  CABOOSE (" + cabooseBorderPoint.getX() + "," + cabooseBorderPoint.getY() + ")");
		
			doIt++;
		}
		
		
		//System.out.println("BORDER POINTS: " + borderPoints);
	}
	
	public Point2D.Double findNextBorderPoint(Point2D.Double currPt, Point2D.Double prevPt, Point2D.Double tailPt, Point2D.Double caboosePt)
	{
		//System.out.println("looking for next point...");
		
		Point2D.Double	adjPt;
		int	adjC, adjR;
		int	c	= (int)currPt.getX();
		int	r	= (int)currPt.getY();
		
		adjC	= c - 1;
		adjR	= r;
		if(adjC >= 0)
		{
			if(physicalSchematic.get(adjR).get(adjC) == 2)
			{
				adjPt	= new Point2D.Double(adjC, adjR);
				if(!adjPt.equals(prevPt) && !adjPt.equals(tailPt) && !adjPt.equals(caboosePt))
				{
					//System.out.println("...found next w: " + adjPt);
					return	adjPt;
				}
			}
		}
		adjC	= c;
		adjR	= r + 1;
		if(adjR < physicalSchematic.size())
		{
			if(physicalSchematic.get(adjR).get(adjC) == 2)
			{
				adjPt	= new Point2D.Double(adjC, adjR);
				if(!adjPt.equals(prevPt) && !adjPt.equals(tailPt) && !adjPt.equals(caboosePt))
				{
					//System.out.println("...found next s: " + adjPt);
					return	adjPt;
				}
			}
		}
		adjC	= c + 1;
		adjR	= r;
		if(adjC < physicalSchematic.get(0).size())
		{
			if(physicalSchematic.get(adjR).get(adjC) == 2)
			{
				adjPt	= new Point2D.Double(adjC, adjR);
				if(!adjPt.equals(prevPt) && !adjPt.equals(tailPt) && !adjPt.equals(caboosePt))
				{
					//System.out.println("...found next e: " + adjPt);
					return	adjPt;
				}
			}
		}
		adjC	= c;
		adjR	= r - 1;
		if(adjR >= 0)
		{
			if(physicalSchematic.get(adjR).get(adjC) == 2)
			{
				adjPt	= new Point2D.Double(adjC, adjR);
				if(!adjPt.equals(prevPt) && !adjPt.equals(tailPt) && !adjPt.equals(caboosePt))
				{
					//System.out.println("...found next n: " + adjPt);
					return	adjPt;
				}
			}
		}
		adjC	= c - 1;
		adjR	= r + 1;
		if(adjC >= 0 && adjR < physicalSchematic.size())
		{
			if(physicalSchematic.get(adjR).get(adjC) == 2)
			{
				adjPt	= new Point2D.Double(adjC, adjR);
				if(!adjPt.equals(prevPt) && !adjPt.equals(tailPt) && !adjPt.equals(caboosePt))
				{
					//System.out.println("...found next sw: " + adjPt);
					return	adjPt;
				}
			}
		}
		adjC	= c + 1;
		adjR	= r + 1;
		if(adjC < physicalSchematic.get(0).size() && adjR < physicalSchematic.size())
		{
			if(physicalSchematic.get(adjR).get(adjC) == 2)
			{
				adjPt	= new Point2D.Double(adjC, adjR);
				if(!adjPt.equals(prevPt) && !adjPt.equals(tailPt) && !adjPt.equals(caboosePt))
				{
					//System.out.println("...found next se: " + adjPt);
					return	adjPt;
				}
			}
		}
		adjC	= c - 1;
		adjR	= r - 1;
		if(adjC >= 0 && adjR >= 0)
		{
			if(physicalSchematic.get(adjR).get(adjC) == 2)
			{
				adjPt	= new Point2D.Double(adjC, adjR);
				if(!adjPt.equals(prevPt) && !adjPt.equals(tailPt) && !adjPt.equals(caboosePt))
				{
					//System.out.println("...found next nw: " + adjPt);
					return	adjPt;
				}
			}
		}
		adjC	= c + 1;
		adjR	= r - 1;
		if(adjC < physicalSchematic.get(0).size() && adjR >= 0)
		{
			if(physicalSchematic.get(adjR).get(adjC) == 2)
			{
				adjPt	= new Point2D.Double(adjC, adjR);
				if(!adjPt.equals(prevPt) && !adjPt.equals(tailPt) && !adjPt.equals(caboosePt))
				{
					//System.out.println("...found next ne: " + adjPt);
					return	adjPt;
				}
			}
		}
		
		return new Point2D.Double(-99, -99);
	}
	
	public void findDrillPoints()
	{
		for(int r = 0; r < physicalSchematic.size(); r++)
		{
			for(int c = 0; c < physicalSchematic.get(0).size(); c++)
			{
				if(physicalSchematic.get(r).get(c) == 3)
				{
					drillPoints.add(new Point2D.Double(c, r));
				}
			}
		}
	}	
	
	/*
	public void loadPixelSchematic(ArrayList<ArrayList<Integer>> pixelMatrix)
	{
		for(int r = 0; r < stdPixelHeight; r++)
		{
			for (int c = 0; c < stdPixelWidth; c++)
			{
				pixelSchematic.get(r).set(c, pixelMatrix.get(r).get(c));
			}
		}
	}
	*/
	
	public void printPixelSchematic()
	{
		for(int r = 0; r < stdPixelHeight; r++)
		{
			for(int c = 0; c < stdPixelWidth; c++)
			{
				System.out.print(physicalSchematic.get(r).get(c) + " ");
			}
			System.out.print('\n');
		}
	}
	
	class NoClosedShapeException extends Exception
	{
		public NoClosedShapeException()
		{	}
	}
	
	class MultipleClosedShapeException extends Exception
	{
		public MultipleClosedShapeException()
		{	}
	}
	
	public ArrayList<ArrayList<Integer>> getPhysicalSchematic()
	{
		return physicalSchematic;
	}
	
	public void setPhysicalSchematic(ArrayList<ArrayList<Integer>> schem)
	{
		physicalSchematic	= schem;
	}
	
	public ArrayList<ArrayList<Color>> getPaintSchematic()
	{
		return paintSchematic;
	}
	
	public ArrayList<String> getTreatmentSchematic()
	{
		return treatmentSchematic;
	}
	
	public ArrayList<Point2D.Double> getBorderPoints()
	{
		return borderPoints;
	}
	
	public ArrayList<Point2D.Double> getDrillPoints()
	{
		return drillPoints;
	}
	
	public void setDesignName(String str)
	{
		designName	= str;
	}
	
	public String getDesignName()
	{
		return designName;
	}
	
	public boolean getValidShape()
	{
		return validShape;
	}

	public void setPreviewImage(ImageIcon img)
	{
		previewImage	= img;
	}
	
	public ImageIcon getPreviewImage()
	{
		return this.previewImage;
	}
}
