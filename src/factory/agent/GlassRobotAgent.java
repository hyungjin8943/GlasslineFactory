package factory.agent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import agent.MessageTracePool;
import factory.controller.GlassRobotController;
import factory.controller.GlassRobotController.ConveyorID;
import factory.interfaces.ConveyorInteractor;
import factory.interfaces.Disaster;
import factory.interfaces.GlassRobot;
import factory.interfaces.ITruckInteractor;
import factory.interfaces.Truck;
import factory.mock.log.EventLog;

public class GlassRobotAgent extends GenericAgent<GlassRobotController> implements GlassRobot, ConveyorInteractor, ITruckInteractor, Disaster
{
	public enum State
	{
		IDLE,

		ROTATE_TO_BIN, PICK_UP_FROM_BIN, ROTATE_TO_CONVEYOR, CHECK_ON_CONVEYOR, DROP_OFF_AT_CONVEYOR,

		NOTIFY_CALLER, PICK_UP_FROM_CONVEYOR, CHECK_ON_TRUCK, ROTATE_TO_TRUCK, DROP_OFF_AT_TRUCK,

		ROTATE_TO_TRASH, DROP_OFF_AT_TRASH,

		ROTATE_TO_IDLE
	}

	// 1) IDLE
	// 2) ROTATE_TO_BIN
	// 3) PICK_UP_FROM_BIN
	// 4) ROTATE_TO_CONVEYOR
	// 5) CHECK_ON_CONVEYOR
	// 6) DROP_OFF_AT_CONVEYOR
	// 7) ROTATE_TO_IDLE
	// 8) IDLE

	// 1) IDLE
	// 2) msgCanYouTakeGlass() -> ROTATE_TO_CONVEYOR
	// 3) msgDoneRotateToConveyor() -> NOTIFY_CALLER
	// 4) NOTIFY_CALLER -> PICK_UP_FROM_CONVEYOR
	// 5) msgHereIsGlass() called and msgDonePickUpFromConveyor() called ->
	// ROTATE_TO_TRUCK
	// 6) CHECK_ON_TRUCK
	// 7) DROP_OFF_AT_TRUCK
	// 8) ROTATE_TO_IDLE

	public Queue<GlassOrder>	orders;
	public State				state;
	public String				name;

	public ConveyorAgent		conveyor_agent;
	public EventLog				log;
	public boolean				enable_debug						= true;

	public GlassOrder			glass_order_cache;
	public Truck				truck_agent;

	public State				last_action;
	public boolean				wait_for_callback					= false;

	public boolean				received_msgHereIsGlass				= false;
	public boolean				received_msgDonePickUpFromConveyor	= false;
	public boolean				caller_waiting						= false;
	public boolean				holding_glass						= false;
	private Semaphore			glass_check_pass;

	/*
	 * constructors, initializers
	 */

	public GlassRobotAgent( String name_ )
	{
		super();
		name = name_;
		orders = new LinkedList<GlassOrder>();
		log = new EventLog();
		state = State.IDLE;
		glass_check_pass = new Semaphore( 1, true );
	}

	@Override
	public void setConveyor( ConveyorAgent conveyor )
	{
		conveyor_agent = conveyor;
	}

	@Override
	public void setTruck( Truck truck )
	{
		truck_agent = truck;
	}

	/*
	 * messages (controller)
	 */

	@Override
	public void msgDoneRotateToBin()
	{
		addToLog( "received msgDoneRotateToBin()" );
		if ( state == State.ROTATE_TO_BIN && wait_for_callback )
		{
			wait_for_callback = false;
			addToLog( "GlassRobot finished rotating to face the bin." );
			state = orders.size() > 0 ? State.PICK_UP_FROM_BIN : State.ROTATE_TO_IDLE;
			stateChanged();
		}
	}

	@Override
	public synchronized void msgDonePickUpFromBin()
	{
		addToLog( "received msgDonePickUpFromBin()" );
		if ( state == State.PICK_UP_FROM_BIN && wait_for_callback )
		{
			wait_for_callback = false;
			addToLog( "Glass loaded onto GlassRobot." );
			state = State.ROTATE_TO_CONVEYOR;
			stateChanged();
		}
	}

	// go to our idle state
	private synchronized void recoverFromNonNormative()
	{
		addToLog( "Recovering from non-normative: " + state.name() );
		if ( ( state == State.ROTATE_TO_CONVEYOR && truck_agent == null ) || state == State.DROP_OFF_AT_CONVEYOR || state == State.DROP_OFF_AT_TRUCK || state == State.CHECK_ON_CONVEYOR
				|| state == State.CHECK_ON_TRUCK || state == State.ROTATE_TO_TRUCK ) orders.remove();
		checkOrders();
	}

	private synchronized boolean checkGlassState( boolean desired_result ) throws InterruptedException
	{
		addToLog( "Checking glass state" );
		if ( holding_glass != desired_result )
		{
			controller.doQueryDoYouHaveGlass();
			glass_check_pass.acquire();
			addToLog( "Thread unlocked" );
		}
		addToLog( "Done" );
		return holding_glass == desired_result;
	}

	@Override
	public void msgDoneRotateToConveyor()
	{
		addToLog( "received msgDoneRotateToConveyor()" );
		if ( state == State.ROTATE_TO_CONVEYOR && wait_for_callback )
		{
			wait_for_callback = false;
			addToLog( "GlassRobot finished rotating to conveyor." );
			// if we don't have a truck agent, we're dropping glass here;
			// otherwise, we're picking up glass
			state = truck_agent == null ? State.CHECK_ON_CONVEYOR : State.NOTIFY_CALLER;

			stateChanged();
		}
	}

	@Override
	public synchronized void msgDoneDropOffAtConveyor()
	{
		addToLog( "received msgDoneDropOffAtConveyor()" );
		if ( state == State.DROP_OFF_AT_CONVEYOR && wait_for_callback && orders.size() > 0 )
		{
			wait_for_callback = false;
			addToLog( "Dropping glass on conveyor..." );
			conveyor_agent.msgHereIsGlass( orders.remove() );
			addToLog( "GlassRobot finished dropping glass on conveyor." );

			state = orders.size() > 0 ? State.ROTATE_TO_BIN : State.ROTATE_TO_IDLE;
			stateChanged();
		}
	}

	@Override
	public void msgDonePickUpFromConveyor()
	{
		addToLog( "received msgDonePickUpFromConveyor()" );
		if ( state == State.PICK_UP_FROM_CONVEYOR && wait_for_callback )
		{
			received_msgDonePickUpFromConveyor = true;
			if ( received_msgDonePickUpFromConveyor && received_msgHereIsGlass )
			{
				wait_for_callback = false;
				state = State.CHECK_ON_TRUCK;
				addToLog( "received order from conveyor" );
				stateChanged();
			}
			else
			{
				addToLog( "still waiting on msgHereIsGlass()" );
			}
		}
	}

	@Override
	public void msgDoneDropOffAtTruck()
	{
		addToLog( "received msgDoneDropOffAtTruck()" );
		if ( state == State.DROP_OFF_AT_TRUCK && wait_for_callback && orders.size() > 0 )
		{
			wait_for_callback = false;
			addToLog( "Dropping glass at truck..." );
			truck_agent.msgHereIsGlass( orders.remove() );
			addToLog( "GlassRobot finished dropping glass on truck." );

			state = orders.size() > 0 || glass_order_cache != null ? State.ROTATE_TO_CONVEYOR : State.ROTATE_TO_IDLE;
			stateChanged();
		}

	}

	@Override
	public void msgDoneRotateToTruck()
	{
		addToLog( "received msgDoneRotateToTruck()" );

		if ( state == State.ROTATE_TO_TRUCK && wait_for_callback )
		{
			wait_for_callback = false;
			state = State.DROP_OFF_AT_TRUCK;
			stateChanged();
		}
	}

	@Override
	public void msgDoneMoveToIdlePosition()
	{
		addToLog( "received msgDoneMoveToIdlePosition()" );
		if ( state == State.ROTATE_TO_IDLE && wait_for_callback )
		{
			wait_for_callback = false;
			addToLog( "Idling..." );
			state = State.IDLE;
			stateChanged();
		}
	}

	/*
	 * messages (other agents)
	 */
	// called when transferring from bin to conveyor
	@Override
	public synchronized void msgNewGlassOrder( GlassOrder order )
	{
		addToLog( "received msgNewGlassOrder()" );
		orders.add( order );

		if ( state == State.IDLE || ( state == State.ROTATE_TO_IDLE && wait_for_callback ) )
		{
			wait_for_callback = false;
			addToLog( "processing new order" );
			state = State.ROTATE_TO_BIN;
			stateChanged();
		}
	}

	// called by conveyor if it's okay to pass it a piece of glass
	@Override
	public void msgHereIsMyAnswer( boolean can_pass_glass )
	{
		addToLog( "received msgHereIsMyAnswer()" );
		if ( state == State.CHECK_ON_CONVEYOR && can_pass_glass && wait_for_callback )
		{
			wait_for_callback = false;
			addToLog( "Conveyor says it's ready." );
			state = State.DROP_OFF_AT_CONVEYOR;
			stateChanged();
		}
		else
		{
			addToLog( "Conveyor says it's not ready." );
		}
		// otherwise our state is "check_on_conveyor" and we're waiting for the
		// go-ahead from the conveyor
	}

	// from ConveyorInteractor
	@Override
	public void msgCanYouTakeGlass( GlassOrder order )
	{
		addToLog( "received msgCanYouTakeGlass()" );
		glass_order_cache = order;
		// immediately say false; if we're in the right state, we'll reply with
		// true on the next cycle
		addToLog( "you can't pass me glass yet" );
		conveyor_agent.msgHereIsMyAnswer( order, false );

		if ( state == State.IDLE || ( state == State.ROTATE_TO_IDLE && wait_for_callback ) )
		{
			wait_for_callback = false;
			state = State.ROTATE_TO_CONVEYOR;
			stateChanged();
		}
	}

	// from ConveyorInteractor
	// called when passing from conveyor to truck
	@Override
	public void msgHereIsGlass( GlassOrder order )
	{
		addToLog( "received msgHereIsGlass()" );

		if ( state == State.PICK_UP_FROM_CONVEYOR )
		{
			received_msgHereIsGlass = true;
			orders.add( order );
			if ( received_msgDonePickUpFromConveyor && received_msgHereIsGlass )
			{
				wait_for_callback = false;
				state = State.CHECK_ON_TRUCK;
				addToLog( "received order from conveyor" );
				stateChanged();
			}
			else
			{
				addToLog( "still waiting on msgDonePickUpFromConveyor()" );
			}
		}
	}

	// from ITruckInteractor
	@Override
	public void msgOkToPassGlass( Truck caller )
	{
		addToLog( "received msgOkToPassGlass()" );
		if ( state == State.CHECK_ON_TRUCK && wait_for_callback )
		{
			wait_for_callback = false;
			state = State.ROTATE_TO_TRUCK;
			stateChanged();
		}
	}

	/*
	 * scheduler
	 */

	@Override
	public boolean pickAndExecuteAnAction()
	{
		try
		{
			// if we're waiting for a callback, we should continue to sleep
			if ( wait_for_callback ) return false;

			if ( state == State.ROTATE_TO_BIN )
			{
				rotateToBin();
				return true;
			}
			if ( state == State.PICK_UP_FROM_BIN )
			{
				pickUpFromBin();
				return true;
			}
			if ( state == State.ROTATE_TO_CONVEYOR )
			{
				if ( truck_agent == null && !checkGlassState( true ) ) recoverFromNonNormative();
				else rotateToConveyor();
				return true;
			}
			if ( state == State.CHECK_ON_CONVEYOR )
			{
				// we keep doing this until the conveyor is ready

				if ( !checkGlassState( true ) ) recoverFromNonNormative();
				else checkOnConveyor();
				return true;
			}
			if ( state == State.DROP_OFF_AT_CONVEYOR )
			{
				if ( !checkGlassState( true ) ) recoverFromNonNormative();
				else dropOffAtConveyor();
				return true;
			}
			if ( state == State.NOTIFY_CALLER )
			{
				notifyCaller();
				return true;
			}
			if ( state == State.PICK_UP_FROM_CONVEYOR )
			{
				pickUpFromConveyor();
				return true;
			}
			if ( state == State.ROTATE_TO_TRUCK )
			{
				if ( !checkGlassState( true ) ) recoverFromNonNormative();
				else rotateToTruck();
				return true;
			}
			if ( state == State.CHECK_ON_TRUCK )
			{
				if ( !checkGlassState( true ) ) recoverFromNonNormative();
				else if ( orders.peek().isBroken() ) state = State.ROTATE_TO_TRASH;
				else checkOnTruck();
				return true;
			}
			if ( state == State.DROP_OFF_AT_TRUCK )
			{

				if ( !checkGlassState( true ) ) recoverFromNonNormative();
				else dropOffAtTruck();

				return true;
			}
			if ( state == State.ROTATE_TO_IDLE )
			{
				rotateToIdlePosition();
				return true;
			}
			if ( state == State.ROTATE_TO_TRASH )
			{
				rotateToTrash();
				return true;
			}
			if ( state == State.DROP_OFF_AT_TRASH )
			{
				dropOffAtTrash();
				return true;
			}
			if ( state == State.IDLE )
			{
				checkOrders();
				return false;
			}
		}
		catch ( InterruptedException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private void dropOffAtTrash()
	{
		addToLog( "Dropping off at trash..." );
		wait_for_callback = true;
		controller.doDropOffAtTrash();
		stateChanged();
	}

	private void rotateToTrash()
	{
		addToLog( "Rotating to trash..." );
		wait_for_callback = true;
		controller.doRotateToTrash();
		stateChanged();
	}

	/*
	 * actions
	 */
	private void rotateToBin()
	{
		addToLog( "Rotating to face bin..." );
		wait_for_callback = true;

		// response is msgDoneRotateToBin()
		controller.doRotateToBin();
	}

	private void pickUpFromBin()
	{
		addToLog( "Attempting to load glass..." );
		if ( orders.size() == 0 )
		{
			addToLog( "There are no orders to process; idling" );
			state = State.ROTATE_TO_IDLE;
			stateChanged();
			return;
		}
		wait_for_callback = true;
		// response is msgDonePerformEndEffectorAction()
		System.out.println( "Do we get here" );
		controller.doPickUpFromBin();
		stateChanged();
	}

	private void rotateToConveyor()
	{
		addToLog( "Rotating to face conveyor..." );
		// if we have a truck agent, then we're picking up from the conveyor;
		// otherwise we're dropping off at the conveyor
		controller.doRotateToConveyor( truck_agent != null ? ConveyorID.PICKUP_CONVEYOR : ConveyorID.DROPOFF_CONVEYOR );
		wait_for_callback = true;
		stateChanged();
		// response is msgDoneRotateTo()
	}

	private void checkOnConveyor()
	{
		addToLog( "Checking conveyor state..." );
		wait_for_callback = true;
		// response is msgHereIsMyAnswer()
		conveyor_agent.msgCanYouTakeGlass();
		stateChanged();
	}

	private void dropOffAtConveyor()
	{
		addToLog( "Attempting to drop glass..." );
		if ( orders.size() == 0 )
		{
			addToLog( "There are no orders to process; idling" );
			state = State.ROTATE_TO_IDLE;
			stateChanged();
			return;
		}
		wait_for_callback = true;
		// response is msgDonePerformEndEffectorAction()
		controller.doDropOffAtConveyor();
		stateChanged();
	}

	// wait for incoming glass (from conveyor)
	private void notifyCaller()
	{
		addToLog( "Waiting for glass from conveyor..." );
		// here, we assume the caller is ready and we just tell the agent to do
		// the pickup animation
		state = State.PICK_UP_FROM_CONVEYOR;
		// we're not waiting on any type of callback; we just want to advance to
		// the next state
		wait_for_callback = false;
		// reset message receipt variables
		received_msgDonePickUpFromConveyor = false;
		received_msgHereIsGlass = false;
		addToLog( "you can pass me glass now" );
		conveyor_agent.msgHereIsMyAnswer( glass_order_cache, true );
		// discard our reference to this glass order since we've now done our
		// job to notify the conveyor
		glass_order_cache = null;
		stateChanged();
	}

	private void pickUpFromConveyor()
	{
		addToLog( "Picking up from conveyor..." );
		wait_for_callback = true;
		controller.doPickUpFromConveyor();
		stateChanged();
	}

	private void rotateToTruck()
	{
		addToLog( "Rotating to truck..." );
		wait_for_callback = true;
		controller.doRotateToTruck();
		stateChanged();
	}

	private void checkOnTruck()
	{
		addToLog( "Waiting on truck..." );
		wait_for_callback = true;
		truck_agent.msgCanIPassGlass( this );
		stateChanged();
	}

	private void dropOffAtTruck()
	{
		addToLog( "Dropping off at truck..." );
		wait_for_callback = true;
		controller.doDropOffAtTruck();
		stateChanged();

	}

	private void rotateToIdlePosition()
	{
		addToLog( "Rotating to idle position..." );
		wait_for_callback = true;
		// response is msgDoneMoveToIdlePosition()
		controller.doMoveToIdlePosition();
		stateChanged();
	}

	private void checkOrders()
	{
		if ( orders.size() > 0 )
		{
			// if we have orders, rotate to the appropriate pickup location
			state = truck_agent != null ? State.ROTATE_TO_CONVEYOR : State.ROTATE_TO_BIN;
			stateChanged();
		}
		else if( glass_order_cache != null )
		{
			state = State.ROTATE_TO_CONVEYOR;
			wait_for_callback = false;
			stateChanged();
		}
		else if ( state == State.IDLE )
		{
			wait_for_callback = false;
		}
		else
		{
			wait_for_callback = false;
			state = State.ROTATE_TO_IDLE;
			stateChanged();
		}
	}

	@Override
	public State getState()
	{
		return state;
	}

	public void setState( State state_ )
	{
		state = state_;
	}

	public String getName()
	{
		return name;
	}

	MessageTracePool	mtp	= MessageTracePool.getInstance();

	public void addToLog( String output )
	{
		// log.add( new LoggedEvent( ": " + output ) );
		mtp.add( output, this );
		if ( enable_debug ) System.out.println( output );
		// if ( enable_debug ) System.out.println(
		// log.getLastLoggedEvent().getMessage() );
	}

	@Override
	public void msgResponseDoYouHaveGlass( boolean has_glass )
	{
		addToLog( "Received msgResponseDoYouHaveGlass()" );
		// if we don't have glass and we're doing some behavior that requires us
		// to have glass, halt the current behavior and try to recover
		holding_glass = has_glass;
		if ( glass_check_pass.availablePermits() == 0 ) glass_check_pass.release();
	}

	@Override
	public void msgDoneRotateToTrash()
	{
		addToLog( "received msgDoneRotateToTrash()" );

		if ( state == State.ROTATE_TO_TRASH && wait_for_callback )
		{
			addToLog( "getting ready to drop off at trash" );
			wait_for_callback = false;
			state = State.DROP_OFF_AT_TRASH;
			stateChanged();
		}
	}

	@Override
	public void msgDoneDropOffAtTrash()
	{
		addToLog( "received msgDoneDropOffAtTrash()" );
		if ( state == State.DROP_OFF_AT_TRASH && wait_for_callback && orders.size() > 0 )
		{
			wait_for_callback = false;
			addToLog( "Dropping glass at trash..." );
			orders.remove();
			addToLog( "GlassRobot finished dropping glass in trash." );

			state = orders.size() > 0 || glass_order_cache != null ? State.ROTATE_TO_CONVEYOR : State.ROTATE_TO_IDLE;
			stateChanged();
		}
	}

	@Override
	public void msgBreakAllGlasses()
	{
		// TODO Auto-generated method stub
		
	}
}
