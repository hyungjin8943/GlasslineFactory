package factory.interfaces;

import factory.agent.GlassOrder;

public interface Conveyor {

	// Can it take glass?
	public abstract void msgCanYouTakeGlass();

	public abstract void msgHereIsGlass(GlassOrder g);

	public abstract void msgHereIsMyAnswer(GlassOrder go, boolean answer);


	public abstract void msgEntrySensorStatus(boolean state);

	public abstract void msgGlassWantsToExit();

}