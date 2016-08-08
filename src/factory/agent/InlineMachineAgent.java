package factory.agent;

//Agents
import agent.Agent;
import agent.MessageTracePool;
import factory.agent.ConveyorAgent;

//Interfaces
import factory.interfaces.ConveyorInteractor;
import factory.interfaces.Disaster;

//Mocks
//import factory.mock.MockConveyor;

//Controllers
import factory.agent.GlassOrder.GlassTreatmentStatus;
import factory.controller.InlineMachineController;

//General
import java.util.*;

//Tests
//import factory.mock.EventLog;
//import factory.mock.LoggedEvent;


/**
 * @author Sean Foo
 * 
 * InLineMachines			Job Name
 * 
 * NC_Cutter				Cutting
 * Breakout					Breakout
 * Washer					Washing
 * UVLamp					UV
 * Oven						Baking
 */
public class InlineMachineAgent extends Agent implements ConveyorInteractor, Disaster{
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/////
	/////	DATA
	/////
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////

	public enum MachineState{	FREE,
								EXPECTING,
								WORKING,
								ANIMATING, STOP_ANIMATING,
								DONE,
								GIVING};
								
	public enum AnimationState{	NORMAL,
								STOP,
								CONTINUE};
								
	public enum AnswerStateIn{	NOT_TALKING,
								TALKING};
								
	public enum AnswerStateOut{	NOT_TALKING,
								TALKING};

	public MachineState machine_status;
	public AnswerStateIn answer_status_in;
	public AnswerStateOut answer_status_out;
	public AnimationState animation_status;
	
	public GlassOrder glass;
	public InlineMachineController controller;
	public String job;
	
	public Queue<GlassOrder> glasses;
	
	public Timer wait;
	
	private ConveyorAgent conveyor_out;
	private ConveyorAgent conveyor_in;
	
	private String name;
	
	//TODO remove
	//public EventLog log;

	public InlineMachineAgent(String n){
		machine_status = MachineState.FREE;
		answer_status_in = AnswerStateIn.NOT_TALKING;
		answer_status_out = AnswerStateOut.NOT_TALKING;
		animation_status = AnimationState.NORMAL;
	
		
		glasses = new LinkedList<GlassOrder>();
		this.name = n;
		//TODO remove
		//log = new EventLog();
	}
	

	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/////
	/////	MESSAGES
	/////
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/*
	 * Breaks its glass
	 */
	public void msgBreakAllGlasses(){
		print(" broke glass");
		addToTrace(" [MESSAGE]: breaking my Glass");
		if(glass != null){	
			glass.setBroken(true);
		}
	}
	
	/*
	 * See if machine can take glass
	 */
	@Override
	public void msgCanYouTakeGlass(GlassOrder order){
		addToTrace(" [MESSAGE]: receives msgCanYouTakeGlass");
		//print("receives msgCanYouTakeGlass");
		glasses.add(order);
		
		answer_status_in = AnswerStateIn.TALKING;

		stateChanged();
	}

	/*
	 * Give machine glass
	 */
	@Override
	public void msgHereIsGlass(GlassOrder order){
		addToTrace(" [MESSAGE]: receives msgHereIsGlass");
		//print(job + " receives msgHereIsGlass");
		glass = order;
		machine_status = MachineState.WORKING;

		stateChanged();
	} 

	/*
	 * See if machine can give glass
	 */
	
	@Override
	public void msgHereIsMyAnswer(boolean b){
		addToTrace(" [MESSAGE]: receives msgHereIsMyAnswer");
		//If Input is TRUE
		if(b){
			machine_status = MachineState.GIVING;
			
			answer_status_out = AnswerStateOut.NOT_TALKING;
			stateChanged();
		}
		//If Output is TRUE
		/*else{
			//do nothing if return false, however, sleeps thread
			//	for 3 seconds before asking again
			try{
				Thread.sleep(100);
			}
			catch(InterruptedException ie){
			}
		}*/
	}
	
	/*
	 * Tell machine it's done animating
	 */
	public void msgDoneJob(){
		addToTrace(" [MESSAGE]: receives msgDoneJob");
		if(glass != null && (glass.getGlassTreatmentStatus(job) == GlassTreatmentStatus.INCOMPLETE)){
			//print(job + " receive msgDoneJob()");
			machine_status = MachineState.DONE;
		
			//Change the state of the glass
			glass.setGlassTreatmentStatus(job, GlassTreatmentStatus.COMPLETE);
			
			stateChanged();
		}
		else if(glass != null && (glass.getGlassTreatmentStatus(job) == GlassTreatmentStatus.NOT_NEEDED)){
			machine_status = MachineState.DONE;
			
			stateChanged();
		}

	}
	
	//For broken glass
	public void msgDoneJob(String s){
		addToTrace(" [MESSAGE]: receives msgDoneJob " + s);
		if(glass != null && (s.equals("BROKEN"))){
			//print(job + " receive msgDoneJob()");
			machine_status = MachineState.DONE;
			
			glass.broken = true;
			
			stateChanged();
		}
		else if(glass != null && (s.equals("NEED MANUAL BREAKOUT"))){
			machine_status = MachineState.DONE;
			
			glass.setGlassTreatmentStatus("ManualBreakout", GlassTreatmentStatus.INCOMPLETE);
			glass.setGlassTreatmentStatus(job, GlassTreatmentStatus.COMPLETE);
			
			stateChanged();
		}
		else if(glass != null && (glass.getGlassTreatmentStatus(job) == GlassTreatmentStatus.NOT_NEEDED)){
			machine_status = MachineState.DONE;
			
			stateChanged();
		}

	}
	
	public void msgDonePass(){
		addToTrace(" [MESSAGE]: receives msgDonePass");
		if(glass != null){
			conveyor_out.msgHereIsGlass(glass);
			machine_status = MachineState.FREE;
			glass = null;
	
			stateChanged();
		}
	}
	
	/*
	 * Tell the animation to stop
	 */
	public void msgStopAnimating(){
		animation_status = AnimationState.STOP;
	}
	
	/*
	 * Tell the animation to continue
	 */
	public void msgContinueAnimating(){
		animation_status = AnimationState.CONTINUE;
	}

	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/////
	/////	SCHEDULER
	/////
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	@Override
	public boolean pickAndExecuteAnAction(){
		
		//System.out.println(machine_status);
		/*
		 * Turn off animation
		 */
		if(animation_status == AnimationState.STOP && machine_status == MachineState.ANIMATING){
			//TODO remove
			/*log.add(new LoggedEvent(
					"Agent will call StopAnimating()"));*/
			
			StopAnimating();
			return true;
		}
		
		/*
		 * Turn on animation
		 */
		if(animation_status == AnimationState.CONTINUE && machine_status == MachineState.STOP_ANIMATING){
			//TODO remove
			/*log.add(new LoggedEvent(
					"Agent will call ContinueAnimating()"));*/
			
			ContinueAnimating();
			return true;
		}
		
		/*
		 * If it can take glass, tell in-conveyor TRUE
		 */
		if(answer_status_in == AnswerStateIn.TALKING && machine_status == MachineState.FREE){
			//TODO remove
			/*log.add(new LoggedEvent(
					"Agent will call ICanTakeGlass()"));*/
			ICanTakeGlass();
			return true;
		}

		/*
		 * If it can not take glass, tell in-conveyor FALSE
		 */
		if(answer_status_in == AnswerStateIn.TALKING && machine_status != MachineState.FREE){
			//TODO remove
			/*log.add(new LoggedEvent(
					"Agent will call ICanNotTakeGlass()"));*/
			
			//ICanNotTakeGlass();
			//return false;
		}

		/*
		 * Works on Glass
		 */
		if(machine_status == MachineState.WORKING){
			//TODO remove
			/*log.add(new LoggedEvent(
					"Agent will call doJob()"));*/
			
			doJob(job);
			return true;
		}

		/*
		 * Ask out-conveyor if it can take finished glass
		 */
		if(answer_status_out != AnswerStateOut.TALKING && machine_status == MachineState.DONE){
			//TODO remove
			/*log.add(new LoggedEvent(
					"Agent will call AskNextConveyor()"));*/
			
			AskNextConveyor();
			return true;
		}

		/*
		 * Give glass to out-conveyor 
		 */
		if(machine_status == MachineState.GIVING){
			//TODO remove
			/*log.add(new LoggedEvent(
					"Agent will call GiveToNextConveyor()"));*/
			
			GiveToNextConveyor();
			return true;
		}

		/*
		 * Do Nothing
		 */
		return false;
	}

	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/////
	/////	ACTIONS
	/////
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	private void ICanTakeGlass(){
		conveyor_in.msgHereIsMyAnswer(glasses.peek(), true);
	
		//Removed the glass we just returned
		glasses.poll();
		
		//Continue talking if there are more glasses
		if(glasses.size() > 0){
			answer_status_in = AnswerStateIn.TALKING;
		}
		else{
			answer_status_in = AnswerStateIn.NOT_TALKING;
		}
		

		
		//Machine should be expecting glass now
		//machine_status = MachineState.EXPECTING;
	}

	private void ICanNotTakeGlass(){
		conveyor_in.msgHereIsMyAnswer(glasses.peek(), false);
		
		//Removed the glass we just returned
		glasses.poll();
		
		//Continue talking if there are more glasses
		if(glasses.size() > 0){
			answer_status_in = AnswerStateIn.TALKING;
		}
		else{
			answer_status_in = AnswerStateIn.NOT_TALKING;
		}
	}

	private void doJob(String job){
		//Don't cut if it does not need it
		if(glass.getGlassTreatmentStatus(job) == GlassTreatmentStatus.INCOMPLETE){	
			//Get the machine to ask conveyor_out if it can take glass
			machine_status = MachineState.ANIMATING;
			
			//Do animation
			addToTrace(" [ANIMATION]: is " + job);
			controller.doJob(job, glass.getGlassDesign());
		}
		else if(glass.getGlassTreatmentStatus(job) == GlassTreatmentStatus.NOT_NEEDED){
			machine_status = MachineState.ANIMATING;
			
			addToTrace(" [ANIMATION]: is doing nothing.");
			controller.doJob("NO ACTION", glass.getGlassDesign());
		}
		else{
			machine_status = MachineState.DONE;
		}
		stateChanged();
	}

	private void AskNextConveyor(){
		answer_status_out = AnswerStateOut.TALKING;
		conveyor_out.msgCanYouTakeGlass();
	}

	private void GiveToNextConveyor(){
		machine_status = MachineState.ANIMATING;
		controller.doPass();
		
		stateChanged();
	}
	
	private void StopAnimating(){
		controller.doStop("Stop");
		machine_status = MachineState.STOP_ANIMATING;
	}
	
	private void ContinueAnimating(){
		controller.doContinue("Continue");
		animation_status = AnimationState.NORMAL;
		machine_status = MachineState.ANIMATING;
	}

	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/////
	/////	SETTERS
	/////
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	//TODO
	//Switch out the mocks
	public void setConveyor(ConveyorAgent in, ConveyorAgent out){
		conveyor_out = out;
		conveyor_in = in;
	}
	
	public void setController(InlineMachineController c){
		controller = c;
	}
	
	public void setJob(String j){
		job = j;
	}
	
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/////
	/////	GETTERS
	/////
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	@Override
	public String getName(){
		return this.name;
	}
	
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/////
	/////	TRACE
	/////
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	public void addToTrace(String msg){
		MessageTracePool mtp = MessageTracePool.getInstance();
		MessageTracePool.add(msg,this);
	}
}