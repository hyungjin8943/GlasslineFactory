package factory.interfaces;

import factory.agent.GlassOrder;

// TODO: Decide whether there should be a request for the truck to come to a pickup location
public interface Truck
{
	public abstract String getName();
	
	// called by the controller when the pick up animation is done
	public abstract void msgDonePickUpGlass();

	// called by the controller when the transport animation is done
	public abstract void msgDoneTransportGlass();

	// check to see if the truck can accept a glass order
	public abstract void msgCanIPassGlass( ITruckInteractor caller );

	// responds with caller.msgOkToPassGlass()

	// give the truck a new glass order (it will ignore this unless it's ready
	// to take an order; check via msgCanIPassGlass()
	public abstract void msgHereIsGlass( GlassOrder order );
}
