package factory.controller;

import factory.agent.TruckAgent;
import factory.interfaces.IGuiTruck;

public class TruckController extends GenericController<TruckAgent, IGuiTruck>
{
	public TruckController( TruckAgent agent, IGuiTruck gui )
	{
		super( agent, gui );
	}
	
	public void doPickUpGlass()
	{
		// TODO: Add calls to GUI
		gui.playPickUpGlass();
	
	}
	
	public void doTransportGlass()
	{
		// TODO: Add calls to GUI
		gui.playTransportGlass();
	}
	
	public void donePickUpGlass()
	{
		agent.msgDonePickUpGlass();
	}
	
	public void doneTransportGlass()
	{
		agent.msgDoneTransportGlass();
	}
}
