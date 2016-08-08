package factory.interfaces;

import factory.agent.GlassOrder;

public interface ConveyorInteractor {
	
	public void msgCanYouTakeGlass(GlassOrder order);

	public void msgHereIsGlass(GlassOrder order);
	
	public void msgHereIsMyAnswer(boolean b);

}
