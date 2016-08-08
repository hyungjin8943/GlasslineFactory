package factory.mediators;

import factory.agent.ConveyorAgent;
import factory.controller.ConveyorController;
import factory.gui.FactoryPart;
import factory.gui.GuiConveyor;
import factory.interfaces.ConveyorInteractor;

/**
 * A ConveyorSet in the GlassLine Factory has the following properties: 
 * (1) an Agent model
 * (2) a GUI view
 * (3) a controller.
 * 
 * By using the Mediator modeling structure, for the top level GlassLine factory to access the slots of this set would be:
 * (1) ConveyorSet.agent;
 * (2) ConveyorSet.gui;
 * (3) ConveyorSet.controller;
 * 
 * This is a proposal to reduce naming errors caused when setting Controllers to GUI, GUI to controllers.
 * 
 * @author Harman
 */
public class ConveyorMediator { 
	public ConveyorAgent agent;
	public GuiConveyor gui;
	public ConveyorController controller;
	
	/**
	 * Default constructor for ConveyorSet. Automatically links the Controller to the Agent, GUI to the Controller, and Controller to both Agent and GUI
	 * @param name
	 */
	public ConveyorMediator(String name) {
		this.agent = new ConveyorAgent(name);
		this.gui = new GuiConveyor();
		this.controller =  new ConveyorController(agent, gui);
		agent.setController(controller);
		gui.setController(controller);
	}
	
	public ConveyorMediator(String name, GuiConveyor mock) {
		this.agent = new ConveyorAgent(name);
		this.gui = mock;
		this.controller =  new ConveyorController(agent, gui);
		agent.setController(controller);
		gui.setController(controller);
	}
	
	/**
	 * Sets the source feeder of the Conveyor.
	 * @param source - source feeder of the conveyor
	 * @param fp - it's equivalent GUI side
	 * @param direction - and the location of the gui in respect to the GUI
	 */
	public void setSource(ConveyorInteractor source, FactoryPart fp, int direction) {
		gui.setPart(fp, direction);
		agent.setSource(source);
	}
	
	/**
	 * Sets the desired destination of the Conveyor
	 * @param destination - destination endpoint of the conveyor
	 * @param fp - it's equivalent GUI side
	 * @param direction - location of the GUI
	 */
	public void setDestination(ConveyorInteractor destination, FactoryPart fp, int direction) {
		gui.setPart(fp, direction);
		agent.setDestination(destination);
	}
}