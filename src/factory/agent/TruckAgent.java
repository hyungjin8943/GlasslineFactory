package factory.agent;

import java.util.LinkedList;
import java.util.Queue;

import factory.controller.TruckController;
import factory.interfaces.Disaster;
import factory.interfaces.ITruckInteractor;
import factory.interfaces.Truck;
import factory.mock.log.EventLog;
import factory.mock.log.LoggedEvent;

public class TruckAgent extends GenericAgent<TruckController> implements Truck, Disaster
{
	public enum State
	{
		IDLE, NOTIFY_CALLER, PICK_UP_GLASS, TRANSPORT_GLASS
	}

	public Queue<GlassOrder>	orders;
	public State				state;
	public String				name;

	public boolean				enable_debug			= true;
	public boolean				wait_for_callback		= false;
	public EventLog				log;

	// TODO: initialize conveyor_agent after integration

	public ITruckInteractor		caller_queue;
	public final int			GLASS_ORDER_CAPACITY	= 2;
	public boolean				caller_waiting			= false;

	public TruckAgent( String name_ )
	{
		super();
		name = name_;
		orders = new LinkedList<GlassOrder>();
		log = new EventLog();
		state = State.IDLE;
	}

	@Override
	public synchronized void msgDonePickUpGlass()
	{
		addToLog( "received msgDonePickUpGlass()" );
		if ( state == State.PICK_UP_GLASS && wait_for_callback )
		{
			wait_for_callback = false;
			if ( orders.size() == GLASS_ORDER_CAPACITY )
			{
				addToLog( "transporting glass" );
				state = State.TRANSPORT_GLASS;
			}
			else
			{
				addToLog( "capacity: " + orders.size() + "/" + GLASS_ORDER_CAPACITY );
				state = State.IDLE;
			}
			stateChanged();
		}
		else
		{
			addToLog( "no action taken: " + state.name() + ", " + wait_for_callback );
		}
	}

	public synchronized void msgDoneTransportGlass()
	{
		// conveyor_agent.msgHereIsGlass( order );

		addToLog( "received msgDoneTransportGlass()" );
		if ( state == State.TRANSPORT_GLASS && wait_for_callback )
		{
			wait_for_callback = false;
			addToLog( "finished transporting and returning to pickup location" );
			orders.clear();
			state = caller_queue != null ? State.NOTIFY_CALLER : State.IDLE;
			stateChanged();
		}
	}

	/*
	 * messages (other agents)
	 */
	public synchronized void msgHereIsGlass( GlassOrder order )
	{
		addToLog( "received msgHereisGlass()" );
		if ( state == State.NOTIFY_CALLER && wait_for_callback )
		{
			wait_for_callback = false;
			orders.add( order );

			addToLog( "picking up glass" );
			state = State.PICK_UP_GLASS;

			stateChanged();
		}
	}

	/*
	 * scheduler
	 */
	@Override
	public boolean pickAndExecuteAnAction()
	{
		if ( wait_for_callback ) return false;

		if ( state == State.NOTIFY_CALLER )
		{
			notifyCaller();
			return true;
		}
		if ( state == State.PICK_UP_GLASS )
		{
			pickUpGlass();
			return true;
		}
		if ( state == State.TRANSPORT_GLASS )
		{
			transportGlass();
			return true;
		}
		if ( state == State.IDLE )
		{
			// do nothing
		}

		return false;
	}

	/*
	 * actions
	 */

	private void notifyCaller()
	{
		addToLog( "notifying caller" );

		wait_for_callback = true;
		caller_queue.msgOkToPassGlass( this );
		caller_queue = null;
		stateChanged();
	}

	private void pickUpGlass()
	{
		// TODO: add proper state transition

		addToLog( "starting pickup animation" );
		wait_for_callback = true;
		controller.doPickUpGlass();
		stateChanged();
	}

	private void transportGlass()
	{
		// TODO: add proper state transition

		addToLog( "starting transport animation" );

		wait_for_callback = true;
		controller.doTransportGlass();
		stateChanged();
	}

	@Override
	public synchronized void msgCanIPassGlass( ITruckInteractor caller )
	{
		addToLog( "received msgCanIPassGlass" );
		caller_queue = caller;
		// if we're idle or waiting for glass from our last caller, overwrite the last request
		if ( state == State.IDLE || state == State.NOTIFY_CALLER )
		{
			state = State.NOTIFY_CALLER;
			wait_for_callback = false;
			stateChanged();
		}
	}

	public void addToLog( String output )
	{
		log.add( new LoggedEvent( getName() + ": " + output ) );
		if ( enable_debug ) System.out.println( log.getLastLoggedEvent().getMessage() );
	}

	public String getName()
	{
		return name;
	}

	@Override
	public void msgBreakAllGlasses()
	{
		// TODO Auto-generated method stub
		
	}
}
