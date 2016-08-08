package factory.controller;

import factory.agent.GlassRobotAgent;
import factory.interfaces.GlassRobot;
import factory.interfaces.IGuiGlassRobot;

// every controller has an agent and gui object that are inherited from GenericController

public class GlassRobotController extends GenericController<GlassRobot, IGuiGlassRobot>
{
	public enum ConveyorID
	{
		PICKUP_CONVEYOR,
		DROPOFF_CONVEYOR
	};
	
	public GlassRobotController( GlassRobotAgent agent, IGuiGlassRobot gui )
	{
		super( agent, gui );
	}

	/*
	 * calls to the GUI
	 */

	public void doRotateToBin()
	{
		System.out.println( "Received playRotateToBin" );
		gui.playRotateToBin();
	}

	public void doPickUpFromBin()
	{
		System.out.println( "Received playPickUpFromBin" );
		gui.playPickUpFromBin();
	}

	public void doRotateToConveyor( ConveyorID conveyor_id )
	{
		System.out.println( "Received playRotateToConveyor: " + conveyor_id.name() );
		gui.playRotateToConveyor( conveyor_id );
	}

	public void doDropOffAtConveyor()
	{
		System.out.println( "Received playDropOffAtConveyor" );
		gui.playDropOffAtConveyor();
	}

	public void doMoveToIdlePosition()
	{
		System.out.println( "Received playMoveToIdlePosition" );
		gui.playMoveToIdlePosition();
	}

	public void doPickUpFromConveyor()
	{
		System.out.println( "Received playPickUpFromConveyor" );
		gui.playPickUpFromConveyor();
	}

	public void doDropOffAtTruck()
	{
		System.out.println( "Received playDropOffAtTruck" );
		gui.playDropOffAtTruck();
	}

	public void doRotateToTruck()
	{
		System.out.println( "Received playRotateToTruck" );
		gui.playRotateToTruck();
	}
	
	public void doQueryDoYouHaveGlass()
	{
		System.out.println( "Received queryDoYouHaveGlass" );
		gui.queryDoYouHaveGlass();
	}
	
	public void doDropOffAtTrash()
	{
		System.out.println( "Received playDropOffAtTrash");
		gui.playDropOffAtTrash();
	}
	
	public void doRotateToTrash()
	{
		System.out.println( "Received playRotateToTrash");
		gui.playRotateToTrash();
	}

	/*
	 * responses to the agent
	 */

	public void doneRotateToBin()
	{
		System.out.println( "Received doneRotateToBin" );
		agent.msgDoneRotateToBin();
	}

	public void donePickUpFromBin()
	{
		System.out.println( "Received donePickUpFromBin" );
		agent.msgDonePickUpFromBin();
	}

	public void doneRotateToConveyor()
	{
		System.out.println( "Received doneRotateToConveyor" );
		agent.msgDoneRotateToConveyor();
	}

	public void doneDropOffAtConveyor()
	{
		System.out.println( "Received doneDropOffAtConveyor" );
		agent.msgDoneDropOffAtConveyor();
	}

	public void doneMoveToIdlePosition()
	{
		System.out.println( "Received doneMoveToIdlePosition" );
		agent.msgDoneMoveToIdlePosition();
	}

	public void donePickUpFromConveyor()
	{
		System.out.println( "Received donePickUpFromConveyor" );
		agent.msgDonePickUpFromConveyor();
	}

	public void doneDropOffAtTruck()
	{
		System.out.println( "Received doneDropOffAtTruck" );
		agent.msgDoneDropOffAtTruck();
	}

	public void doneRotateToTruck()
	{
		System.out.println( "Received doneRotateToTruck" );
		agent.msgDoneRotateToTruck();
	}
	
	public void doneQueryDoYouHaveGlass( boolean has_glass )
	{
		System.out.println( "Received doneQueryDoYouHaveGlass");
		agent.msgResponseDoYouHaveGlass( has_glass );
	}

	public void doneDropOffAtTrash()
	{
		System.out.println( "Received doneDropOffAtTrash");
		agent.msgDoneDropOffAtTrash();
	}

	public void doneRotateToTrash()
	{
		System.out.println( "Received doneRotateToTrash");
		agent.msgDoneRotateToTrash();
	}

}
