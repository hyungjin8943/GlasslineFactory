package factory.controller;

/*
 * simply stores a generic agent and gui
 */

public class GenericController<_AgentType, _GuiType>
{
	protected _AgentType	agent;
	protected _GuiType		gui;

	public GenericController( _AgentType agent_, _GuiType gui_ )
	{
		agent = agent_;
		gui = gui_;
	}
}
