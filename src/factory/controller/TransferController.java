package factory.controller;

//TEE HEE HEE
import factory.agent.TransferAgent;
import factory.gui.GuiTransfer;

public class TransferController {
	
	TransferAgent agent;
	GuiTransfer gui;

	public TransferController(TransferAgent agent, GuiTransfer gui) {
		this.agent = agent;
		this.gui = gui;
	}
	
	public void transferGlass()
	{
		gui.doTurnOn();
	}

	public void doneTransferGlass()
	{
		agent.msgDoneTransferGlass();
	}
	
	public void turnTransferOn() { 
		gui.doTurnOn();
	}
	
	public void turnTransferOff() { 
		gui.doTurnOff();
	}
	
	public void playPass() { 
		gui.playPass();
	}
	
	public void readyToPass() { 
		//XXX
		agent.msgReadyToPass();
		
	}
	boolean state = false;
	public void setDisabled(boolean x) {
		state = x;
		gui.setDisabled(state);
		agent.msgBreakTransfer(state);
	}
	
	public boolean getDisabled() { return state; }
	
	public void doneBreaking() { 
		agent.doneBreaking();
	}

}
