package factory.interfaces;

import factory.agent.ConveyorAgent;
import factory.agent.GlassOrder;
import factory.agent.GlassRobotAgent;

public interface GlassRobot
{	
	public abstract GlassRobotAgent.State getState();
	
	public abstract String getName();
	
	public abstract void setConveyor( ConveyorAgent conveyor );
	
	public abstract void setTruck( Truck truck );
	
	public abstract void msgCanYouTakeGlass( GlassOrder order );
	
	public abstract void msgNewGlassOrder( GlassOrder order );
	
	public abstract void msgHereIsMyAnswer( boolean answer );
	
	public abstract void msgDoneRotateToBin();

	public abstract void msgDonePickUpFromBin();

	public abstract void msgDoneRotateToConveyor();

	public abstract void msgDoneDropOffAtConveyor();

	public abstract void msgDoneMoveToIdlePosition();
	
	public abstract void msgDonePickUpFromConveyor();
	
	public abstract void msgDoneDropOffAtTruck();
	
	public abstract void msgDoneRotateToTruck();
	
	public abstract void msgDoneRotateToTrash();
	
	public abstract void msgDoneDropOffAtTrash();

	public abstract void msgResponseDoYouHaveGlass( boolean has_glass );
}
