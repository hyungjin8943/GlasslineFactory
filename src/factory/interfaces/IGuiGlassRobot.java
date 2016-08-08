package factory.interfaces;

import factory.controller.GlassRobotController;
import factory.controller.GlassRobotController.ConveyorID;

public interface IGuiGlassRobot
{
	public abstract void setController( GlassRobotController controller_ );

	public abstract void playRotateToBin();

	public abstract void playPickUpFromBin();

	public abstract void playRotateToConveyor( ConveyorID conveyor_id );

	public abstract void playDropOffAtConveyor();

	public abstract void playMoveToIdlePosition();

	public abstract void playPickUpFromConveyor();

	public abstract void playDropOffAtTruck();

	public abstract void playRotateToTruck();

	public abstract void queryDoYouHaveGlass();

	public abstract void playDropOffAtTrash();

	public abstract void playRotateToTrash();
}
