package factory.agent;

import agent.Agent;

public abstract class GenericAgent<_ControllerType> extends Agent
{
	public _ControllerType	controller;

	public GenericAgent()
	{
		//System.out.println( "Agent is not initialized!" );
	}

	public GenericAgent( _ControllerType controller_ )
	{
		setController( controller_ );
	}

	public void setController( _ControllerType controller_ )
	{
		//System.out.println( "Agent with controller " + controller_.getClass().toString() + " is now initialized." );
		controller = controller_;
	}

	@Override
	protected abstract boolean pickAndExecuteAnAction();
}
