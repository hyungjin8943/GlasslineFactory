package factory.agent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import factory.panels.GlassDesign;

/**
 * This class is the data structure that contains the information for a piece of glass as it goes through the glass line.
 * It will be passed from one agent to the next and each agent can make changes to the GlassOrder if it modifies the glass in anyway.
 * @author Rhys Yahata
 */
public class GlassOrder {
	/** Name of the glass order */
	String name;
	/** boolean value for if the glass is broken or not. Defaulted at creation to false */
	boolean broken;
	/** Unique ID number for each glass order */
	int ID;
	/** Static number to keep track of the next usable ID number */
	static int nextID = 0;
	/** boolean value for if the glass order is done */
	private Boolean complete;
	
	private	GlassDesign	glassDesign;

	/**
	 * This is a private inner class used for keep track of different treatments that need to be can/done on a glass and the
	 * status of such a treatment.
	 * @author Rhys Yahata
	 */
	private class GlassTreatment {
		/** Status of the glass treatment */
		public GlassTreatmentStatus status;
			
		/**
		 * Constructor for a glass treatment. Will set the status of the treatment to the inputed status
		 * @param status Status of treatment
		 */
		public GlassTreatment(GlassTreatmentStatus status) {
			this.status = status;
		}
		
		
		/**
		 * Setter for the status of a GlassTreatment
		 * @param status New status of GlassTreatment
		 */
		public void setStatus(GlassTreatmentStatus status) {
			this.status = status;
		}
	}
	
	/** A map for the statuses of the various treatments that can/need to be done on a GlassOrder */
	HashMap<String, GlassTreatment> treatmentList = new HashMap<String, GlassTreatment>();

	/** An enum to keep track of the status of the different treatments that can be done on a piece of glass */
	public enum GlassTreatmentStatus {INCOMPLETE, COMPLETE, NOT_NEEDED};

	/**
	 * This is the constructor for a GlassOrder. It will set the name of the order to the inputed string.
	 * Each new piece of glass will contain a unique ID number.
	 * @param name
	 */
	public GlassOrder(String name) {
		//Initialize map
		this.treatmentList.put("Cutting", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));
		this.treatmentList.put("Breakout", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));
		this.treatmentList.put("ManualBreakout", new GlassTreatment(GlassTreatmentStatus.NOT_NEEDED));
		this.treatmentList.put("CrossSeaming", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));
		this.treatmentList.put("Grinding", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));
		this.treatmentList.put("Drilling", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));
		this.treatmentList.put("Washing", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));
		this.treatmentList.put("Painting", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));
		this.treatmentList.put("UV", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));
		this.treatmentList.put("Baking", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));
		//Initialize other variables
		this.name = name;
		this.broken = false;
		this.ID = GlassOrder.nextID++;
	}
	
	public GlassOrder(GlassDesign design)
	{
		this.treatmentList.put("Cutting", new GlassTreatment(GlassTreatmentStatus.NOT_NEEDED));
		this.treatmentList.put("Breakout", new GlassTreatment(GlassTreatmentStatus.NOT_NEEDED));
		this.treatmentList.put("ManualBreakout", new GlassTreatment(GlassTreatmentStatus.NOT_NEEDED));
		this.treatmentList.put("CrossSeaming", new GlassTreatment(GlassTreatmentStatus.NOT_NEEDED));
		this.treatmentList.put("Grinding", new GlassTreatment(GlassTreatmentStatus.NOT_NEEDED));
		this.treatmentList.put("Drilling", new GlassTreatment(GlassTreatmentStatus.NOT_NEEDED));
		this.treatmentList.put("Washing", new GlassTreatment(GlassTreatmentStatus.NOT_NEEDED));
		this.treatmentList.put("Painting", new GlassTreatment(GlassTreatmentStatus.NOT_NEEDED));
		this.treatmentList.put("UV", new GlassTreatment(GlassTreatmentStatus.NOT_NEEDED));
		this.treatmentList.put("Baking", new GlassTreatment(GlassTreatmentStatus.NOT_NEEDED));
		
		if(design.getTreatmentSchematic().contains("Cut"))
		{
			this.treatmentList.put("Cutting", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));	
		}
		if(design.getTreatmentSchematic().contains("Breakout"))
		{
			this.treatmentList.put("Breakout", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));	
		}
		if(design.getTreatmentSchematic().contains("Cross-Seam"))
		{
			this.treatmentList.put("CrossSeaming", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));	
		}
		if(design.getTreatmentSchematic().contains("Drill"))
		{
			this.treatmentList.put("Drilling", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));	
		}
		if(design.getTreatmentSchematic().contains("Grind"))
		{
			this.treatmentList.put("Grinding", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));	
		}
		if(design.getTreatmentSchematic().contains("Paint"))
		{
			this.treatmentList.put("Painting",new GlassTreatment( GlassTreatmentStatus.INCOMPLETE));	
		}
		if(design.getTreatmentSchematic().contains("Wash"))
		{
			this.treatmentList.put("Washing", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));	
		}
		if(design.getTreatmentSchematic().contains("UV Treat"))
		{
			this.treatmentList.put("UV", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));	
		}
		if(design.getTreatmentSchematic().contains("Bake"))
		{
			this.treatmentList.put("Baking", new GlassTreatment(GlassTreatmentStatus.INCOMPLETE));	
		}
		
		//Initialize other variables
		this.glassDesign = design;
		this.name = design.getDesignName();
		this.broken = false;
		this.ID = GlassOrder.nextID++;
	}
	
	/**
	 * This method will return the status of the inputed treatment for a GlassOrder.
	 * @param treatment The treatment who's status you would like to know about
	 * @return The status of the GlassTreatment
	 */
	public GlassTreatmentStatus getGlassTreatmentStatus(String treatment) {
		return this.treatmentList.get(treatment).status;
	}
	
	/**
	 * This method will set the status of a treatment for a GlassOrder
	 * @param treatment The treatment who's status you would like to set
	 * @param status The status of the treatment
	 */
	public void setGlassTreatmentStatus(String treatment, GlassTreatmentStatus status) {
		this.treatmentList.get(treatment).setStatus(status);
	}
	
	/**
	 * This method will iterate through the treatment list and return false if any of the treatments is of
	 * status incomplete. If there are none then it will return true.
	 * @return boolean - value for whether or not the GlassOrder is complete
	 */
	public boolean isFinished() {
		//Temporary boolean value for completeness
		boolean isDone = true;
		
		//Collection and iterator to iterate through HashMap
		Collection<GlassTreatment> collection = this.treatmentList.values();
		Iterator<GlassTreatment> iterator = collection.iterator();
		
		//Iterate through the HashMap
		while(iterator.hasNext()) {
			//if(((GlassTreatment)(iterator.next())).status == GlassTreatmentStatus.INCOMPLETE) {
			//If a GlassTreatment is incomplete then set completeness to false and exit loop
			if(iterator.next().status == GlassTreatmentStatus.INCOMPLETE) {
				isDone = false;
				break;
			}
		}
		if(this.complete == null) {
			complete = new Boolean(isDone);
		}
		else {
			complete = isDone;
		}
		return this.complete;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(name);
		buffer.append(new String(" ID#"));
		buffer.append(ID);
		return buffer.toString();
	}
	
	public boolean isBroken() {
		return broken;
	}
	
	public void setBroken(boolean broken) {
		this.broken = broken;
	}
	
	public GlassDesign getGlassDesign()
	{
		return glassDesign;
	}
	
	public boolean isNyanCat() {
		return false;
	}
}

