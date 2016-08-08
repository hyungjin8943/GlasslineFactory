package factory.controller;

//Agent
import factory.agent.ConveyorAgent;
import factory.gui.GuiConveyor;

/**
 * Class ConveyorController
 * The middle-man between the Agent and GUI
 * @author Harman Goei 
 * @version 1.0
 * @since 1.6
 */
public class ConveyorController{
	public ConveyorAgent agent;
	public GuiConveyor gui;
	
	//Need to pass agent and gui
	/**
	 * Sets the Agent and the Gui to the controller.
	 * @param ConveyorAgent agent (model)
	 * @param GuiConveyor gui (view)
	 */
	public ConveyorController(ConveyorAgent a, GuiConveyor gui){
		//initialization
		agent = a;
		this.gui = gui;
	}
	
	/**
	 * [AGENT:PUSH] : Turns on the GUI
	 */
	public void turnConveyorOn(){
		if(gui != null)
		gui.doTurnOn();
	}
	/**
	 * [AGENT:PUSH] : Turns off the GUI
	 */
	public void turnConveyorOff(){
		if(gui != null){	
			gui.doTurnOff();
		}
	}
	
	/**
	 * [AGENT:PULL] : Informs Agent that a Glass has hit the Sensor
	 * - Agent should take action leavingConveyor() in respect to this
	 */
	public void glassExiting() { 
		agent.msgGlassWantsToExit();
	}
	
	/**
	 * [AGENT:PULL] : Informs Agent that the Animation by the Conveyor is done.
	 * It should be called when {@link GuiConveyor#finishPassing()} is called.
	 */
	public void animationDone() {
		agent.msgAnimationDone();
	}

	public void entrySensorStatus(boolean b) {
		// TODO Auto-generated method stub
		agent.msgEntrySensorStatus(b);
		
	}

	public void passGlass() {
		// TODO Auto-generated method stub
		if(gui !=null)
		gui.passGlass();
	}
	
	public void passToRobotArm() { 
		if(gui !=null)
			gui.passToRobotArm();
	}
	boolean sensorState = false;
	public void setEntrySensorDisabled(boolean flag) {
		sensorState = flag;
		agent.msgEntrySensorStatus(sensorState);
		gui.setSensorBroken(sensorState);
	}
	
	boolean state = true;
	public void toggleConveyorState() { 
		state = !state;
		agent.setConveyorState(state);
	}

}
