package factory.interfaces;

import factory.panels.GlassDesign;

public interface MachineControllerInteractor{
	
	//public void playJob(String job_type);
	
	public void playJob(String job_type, GlassDesign gd);
	
	public void playPass();
	
}