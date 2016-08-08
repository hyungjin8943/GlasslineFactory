package factory.panels;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class GlassGridPixel
{
	Rectangle2D.Double	pixel;
	Color				pixelColor;
	Color				outlineColor;
	boolean				isSelected;
	boolean				isEnabled;
	
	private final Color	enabledPixelColor		= new Color(227, 233, 240);
	private final Color	disabledPixelColor		= new Color(64, 64, 64);
	private final Color	enabledOutlineColor		= new Color(220, 220, 220);
	private final Color	disabledOutlineColor	= new Color(64, 64, 64);
	
	
	GlassGridPixel(int x, int y, int w, int h)
	{
		pixel			= new Rectangle2D.Double(x, y, w, h);
		pixelColor		= enabledPixelColor;
		outlineColor	= enabledOutlineColor;
		isSelected		= false;
		isEnabled		= true;
	}
	
	public Rectangle2D.Double getPixel()
	{
		return pixel;
	}
	
	public int getPixelXCoord()
	{
		return (int)pixel.getX();
	}
	
	public int getPixelYCoord()
	{
		return (int)pixel.getY();
	}
	
	public int getPixelWidth()
	{
		return (int)pixel.getWidth();
	}
	
	public int getPixelHeight()
	{
		return (int)pixel.getHeight();
	}
	
	public Color getPixelColor()
	{
		return pixelColor;
	}
	
	public Color getOutlineColor()
	{
		return outlineColor;
	}
	
	public Color getEnabledPixelColor()
	{
		return enabledPixelColor;
	}
	
	public Color getDisabledPixelColor()
	{
		return disabledPixelColor;
	}
	
	public boolean getIsSelected()
	{
		return isSelected;
	}
	
	public boolean getIsEnabled()
	{
		return isEnabled;
	}
	
	public void setPixel(Rectangle2D.Double rect)
	{
		pixel	= rect;
	}
	
	public void setPixel(int x, int y, int w, int h)
	{
		pixel	= new Rectangle2D.Double(x, y, w, h);
	}
	
	public void setPixelColor(Color c)
	{
		pixelColor	= c;
	}
	
	public void setOutlineColor(Color c)
	{
		outlineColor	= c;
	}
	
	public void setIsSelected(boolean b)
	{
		isSelected	= b;
	}
	
	public void setIsEnabled(boolean b)
	{
		isEnabled	= b;
		
		if(!isEnabled)
		{
			pixelColor		= disabledPixelColor;
			outlineColor	= disabledOutlineColor;
		}
		else
		{
			pixelColor		= enabledPixelColor;
			outlineColor	= enabledOutlineColor;
		}
	}
}
