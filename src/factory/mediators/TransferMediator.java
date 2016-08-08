package factory.mediators;

import factory.agent.ConveyorAgent;
import factory.agent.TransferAgent;
import factory.controller.TransferController;
import factory.gui.FactoryPart;
import factory.gui.GuiTransfer;

public class TransferMediator {
	public TransferAgent agent;
	public GuiTransfer gui;
	public TransferController controller;

	/**
	 * Creates a new Agent/Gui/Controller set. This needs to be followed with setSource, setDestination, and the setup of the gui.
	 * @param name
	 */
	public TransferMediator(String name) { 
		this.agent = new TransferAgent(name);
		this.gui = new GuiTransfer(0, 0, 0);
		this.controller = new TransferController(agent,gui);
		gui.setController(this.controller);
		agent.setController(controller);
	}
	
	/**
	 * Sets the source feeder of the transfer
	 * @param source - source feeder of the transfer
	 * @param fp - it's equivalent GUI side
	 * @param direction - and the location of the gui in respect to the GUI
	 */
	public void setSource(ConveyorAgent source, FactoryPart fp, int direction) {
		gui.setPart(fp, direction);
		agent.setSource(source);
	}
	
	/**
	 * Sets the desired destination of the Transfer
	 * @param destination - destination of the Transfer
	 * @param fp - it's equivalent GUI side
	 * @param direction - location of the GUI
	 */
	public void setDestination(ConveyorAgent destination, FactoryPart fp, int direction) {
		gui.setPart(fp, direction);
		agent.setDestination(destination);
	}

}
