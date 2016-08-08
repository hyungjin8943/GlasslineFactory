package factory.mock;

import factory.agent.GlassOrder;
import factory.interfaces.Conveyor;
import factory.interfaces.ConveyorInteractor;
import factory.interfaces.Operator;
import factory.interfaces.Popup;
import factory.agent.*;
import factory.mock.log.EventLog;
import factory.mock.log.LoggedEvent;

public class MockConveyorInteractor implements ConveyorInteractor {

	Conveyor source;
	public MockConveyorInteractor(String name, Conveyor conveyor) {
		source = conveyor;
	}
	@Override
	public void msgCanYouTakeGlass(GlassOrder order) {
		// TODO Auto-generated method stub
		source.msgHereIsMyAnswer(order, true);
	}
	@Override
	public void msgHereIsGlass(GlassOrder order) {
		// TODO Auto-generated method stub
		System.out.println("Glass completed");
	}
	@Override
	public void msgHereIsMyAnswer(boolean b) {
		// TODO Auto-generated method stub
		
	}



}
