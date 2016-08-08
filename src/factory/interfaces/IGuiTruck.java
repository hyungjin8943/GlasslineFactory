package factory.interfaces;

public interface IGuiTruck
{	
	// pick up one piece of glass
	public abstract void playPickUpGlass();
	
	// move offscreen, then return to pickup location
	public abstract void playTransportGlass();
}
