//Controller for machines, need to implement
package factory.controller;

import agent.Agent;
import factory.gui.FactoryPart;
//import factory.mock.MockGuiStandAloneMachine;
import factory.agent.StandAloneMachineAgent;

import factory.interfaces.MachineControllerInteractor;
import factory.panels.GlassDesign;

//Test
//import factory.mock.EventLog;
//import factory.mock.LoggedEvent;

public class StandAloneMachineController{
	
	/**
	 * Data
	 */
	public StandAloneMachineAgent stand_alone;
	public MachineControllerInteractor gui;
	
	//TODO remove
	//public EventLog log;
	
	public StandAloneMachineController(){
		//log = new EventLog();
	}
	
	/**
	 * Actions
	 */

	public void doJob(String job_type){
		//TODO remove
		/*log.add(new LoggedEvent(
				"Controller received doJob from Agent for " + job_type));*/
		
		//gui.playJob(job_type);
	}
	
	public void doJob(String job_type, GlassDesign gd){
		gui.playJob(job_type, gd);
	}
	
	public void doPass(){
		gui.playPass();
	}
	
	public void doneJob(){
		//TODO remove
		/*log.add(new LoggedEvent(
				"Controller received doneAnimating from Gui"));*/
		
		stand_alone.msgDoneJob();
	}
	
	public void doneJob(String msg){
		//TODO remove
		/*log.add(new LoggedEvent(
				"Controller received doneAnimating from Gui"));*/
		
		stand_alone.msgDoneJob(msg);
	}

	public void donePass(){
		stand_alone.msgDonePass();
	}
	
	/**
	 * Setters
	 */
	public void setAgent(StandAloneMachineAgent a){
		stand_alone = a;
	}
	public void setGui(MachineControllerInteractor g){
		gui = g;
	}
}