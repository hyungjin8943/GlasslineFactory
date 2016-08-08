//Controller for machines, need to implement
package factory.controller;

import agent.Agent;
//import factory.gui.GuiMachine;
//import factory.mock.MockGuiInlineMachine;
import factory.agent.InlineMachineAgent;
import factory.panels.GlassDesign;

import factory.interfaces.MachineControllerInteractor;

//Tests
//import factory.mock.EventLog;
//import factory.mock.LoggedEvent;

public class InlineMachineController{
	
	/**
	 * Data
	 */
	public InlineMachineAgent inline;
	public MachineControllerInteractor gui;
	
	//public EventLog log;
	
	public InlineMachineController(){
		//log = new EventLog();
	}
	
	/**
	 * Actions
	 */

	/*
	 * From the Agent
	 */
	public void doJob(String job_type){
		//TODO remove
		/*log.add(new LoggedEvent(
				"Controller received doJob from Agent for " + job_type));*/
		//System.out.println("InlineMachineController is calling playJob");
		//gui.playJob(job_type);
	}
	
	public void doJob(String job_type, GlassDesign gd){
		gui.playJob(job_type, gd);
	}
	
	public void doPass(){
		gui.playPass();
	}
	
	public void doStop(String action){
		//gui.stopAnimation();
	}
	
	public void doContinue(String action){
		//gui.continueAnimation();
	}
	
	/*
	 * From the Gui
	 */
	public void doneJob(){
		//TODO remove
		/*log.add(new LoggedEvent(
				"Controller received doneAnimating from Gui."));*/
		inline.msgDoneJob();
	}
	
	public void doneJob(String s){
		inline.msgDoneJob(s);
	}
	
	public void donePass(){
		inline.msgDonePass();
	}
	
	/**
	 * Setters
	 */
	public void setAgent(InlineMachineAgent a){
		inline = a;
	}
	public void setGui(MachineControllerInteractor g){
		gui = g;
	}
}