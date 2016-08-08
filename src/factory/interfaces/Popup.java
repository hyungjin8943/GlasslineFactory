package factory.interfaces;

import factory.agent.GlassOrder;

public interface Popup {

	//Messages
	/**
	 * This message will notify the PopupAgent that a new piece of glass has been placed on the conveyor.
	 * @param glassOrder - New piece of glass
	 */
	public abstract void msgGlassOnTheWay(GlassOrder glassOrder);

	/**
	 * This message will sent by an operator notifying the popup of his answer to wether or not he needs to perform a treatment
	 * on a glass order
	 * @param answer - Operator's answer
	 * @param operator - Reference to the operator who messaged the popup
	 * @param glassOrder - The glass order that is in question
	 */
	public abstract void msgHereIsMyAnswer(boolean answer, Operator operator,
			GlassOrder glassOrder);

	/**
	 * This message will be sent from the conveyor asking the popup if he can accept a piece of glass.
	 * This message will be sent when the piece of glass is at the end of the conveyor, right before the popup
	 * @param glassOrder - The glass order in question
	 */
	public abstract void msgCanYouTakeGlass(GlassOrder glassOrder);

	/**
	 * This message will notify the popup that an operator is free to do work on a piece of glass
	 * @param operator - Reference to the operator that is sending this message
	 */
	public abstract void msgIamFree(Operator operator);

	/**
	 * This message will notify the popup that an operator is busy and cannot do work on a piece of glass
	 * @param operator - Reference to the operator that is sending this message
	 */
	public abstract void msgIamBusy(Operator operator);
	
	/**This message will notify the popup of an operator's job
	 * @param operator - the operator sending the message
	 * @param job - the job of the operator sending the message
	 */
	public void msgThisIsMyJob(Operator operator, String job);
	
	/**
	 * This message will notify the popup that a piece of glass has been placed on it
	 * @param glassOrder - The piece of glass now on the popup
	 */
	public abstract void msgHereIsGlass(GlassOrder glassOrder);

	/**
	 * This message will be called by an operator notifying the popup that it is done processing a piece of glass
	 * and would like permission to place it back on it
	 * @param glassOrder - Glass that is to be passed back
	 * @param operator - The operator calling this message
	 */
	public abstract void msgCanIPassTreatedGlass(GlassOrder glassOrder,
			Operator operator);

	/**
	 * This message will be called by an operator notifying the popup that it is passing it a piece of treated glass.
	 * This will unlock the multistep action semaphore.
	 * @param glassOrder - Glass order that is finished
	 */
	public abstract void msgHereIsTreatedGlass(GlassOrder glassOrder);

	/**
	 * This message will be sent by the toConveyor notifying the popup that there is space on it to unload glass
	 * @param answer
	 */
	public abstract void msgHereIsMyAnswer(boolean answer);

	/**
	 * This message will be called by the operator notifying the popup whether or not there is another operator later that performs
	 * the same job.
	 * @param answer - The operators answer
	 * @param operator - The operator who is answering
	 */
	public abstract void msgHereIsJobAvailability(boolean answer,
			Operator operator);

}