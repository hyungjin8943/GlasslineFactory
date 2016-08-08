package factory.interfaces;

import factory.agent.GlassOrder;

public interface Operator {

	//Messages
	/**
	 * This message will be called by the popup to figure out whether or not the operator needs to do work on the inputed glass order
	 * @param glassOrder - Glass order in question
	 */
	public abstract void msgDoYouNeedGlass(GlassOrder glassOrder);

	/**
	 * This message will be called by the popup to figure out whether or not the operator is busy.
	 */
	public abstract void msgAreYouFree();
	
	/**
	 * This message will be called by the popup to keep track of what job the operator has.
	 */
	public abstract void msgWhatIsYourJob();

	/**
	 * This message will be called by the popup to figure out whether or not there is an operator down the line who can perform
	 * the same job as it.
	 */
	public abstract void msgIsJobAvailableLater();

	/**
	 * This message will be called by the popup to notify the operator that it has passed him a new piece of glass
	 * @param glassOrder
	 */
	public abstract void msgHereIsGlass(GlassOrder glassOrder);

	/**
	 * This message will be called by the popup to notify the operator that he can return the piece of glass that he has
	 */
	public abstract void msgYesYouCan();

	/**
	 * This message will be called by the popup to notify the operator that he cannot return a piece of glass.
	 * The operator will need to ask again.
	 */
	public abstract void msgNoYouCannot();

	/**
	 * This message will be called by a StandAloneMachine to notify the operator that it is handing it treated glass
	 * @param glassOrder The glass order being handed to the operator
	 */
	public abstract void msgHereIsTreatedGlass(GlassOrder glassOrder);
	
	/**
	 * This message will be called by a TruckAgent notifying the operator that it is ok to pass it glass
	 */
	public abstract void msgOkToPassGlass();
}