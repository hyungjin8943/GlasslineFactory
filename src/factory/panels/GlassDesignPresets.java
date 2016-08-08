package factory.panels;
import java.io.*;
import java.util.ArrayList;

public class GlassDesignPresets implements Serializable
{
	ArrayList<GlassDesign>	presetDesigns;
	
	public GlassDesignPresets()
	{
		presetDesigns	= new ArrayList<GlassDesign>();
		addDefaultGlassDesign();
	}
	
	public GlassDesignPresets(GlassDesignPresets otherPresets)
	{
		this.presetDesigns	= otherPresets.getPresetDesigns();
	}
	
	public ArrayList<GlassDesign> getPresetDesigns()
	{
		return presetDesigns;
	}
	
	public void addGlassDesign(GlassDesign newDesign)
	{
		presetDesigns.add(newDesign);
	}
	
	public void addDefaultGlassDesign()
    {
    	GlassDesign	newStdDesign	= new GlassDesign(); 
    	presetDesigns.add(newStdDesign);
    }
	
	public void writeToFile()
	{
		ObjectOutputStream outputStream = null;
    	
		try 
		{
        	outputStream = new ObjectOutputStream(new FileOutputStream("savedglassdesignpresets.txt"));
        	   
        	outputStream.writeObject(this);   
        	
        	System.out.println("These designs saved to file: ");
        	for(GlassDesign design : presetDesigns)
        	{
        		System.out.println(design.getDesignName() + "  ");
        	}
        }
		catch (Exception e)
		{
        		System.out.println("There was an error writing the game save file." + e);
        }

	}
}
