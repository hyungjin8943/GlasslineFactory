package factory.agent;

//Agents
import agent.Agent;
import agent.MessageTracePool;
import factory.agent.OperatorAgent;

//Interfaces
import factory.interfaces.OperatorInteractor;
import factory.interfaces.Disaster;

//Mocks
//import factory.mock.MockOperator;

//Controllers
import factory.agent.GlassOrder.GlassTreatmentStatus;
import factory.agent.InlineMachineAgent.MachineState;
import factory.controller.StandAloneMachineController;

//General
import java.util.Timer;

//Tests
//import factory.mock.EventLog;
//import factory.mock.LoggedEvent;

/**
 * StandAloneMachines		Job Name
 * 
 * ManualBreakout			ManualBreakout
 * CrossSeamer				CrossSeaming
 * Grinder					Grinding
 * Drill					Drilling
 * Paint					Painting
 */

public class StandAloneMachineAgent extends Agent implements OperatorInteractor, Disaster{
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/////
	/////	DATA
	/////
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	
	public enum MachineState{	FREE,
								WORKING,
								ANIMATING,
								DONE,
								GIVING};
								
	public enum AnswerState{	NOT_TALKING, 
								TALKING};

	public MachineState machine_status;
	public AnswerState answer_status;
	
	public GlassOrder glass;
	public StandAloneMachineController controller;
	
	public Timer wait;
	
	public String job;
	public String name;
	
	//TODO
	//Take out testing stuff
	//public EventLog log;
	
	//TODO
	//Need to replace all the mocks with actual parts
	public OperatorAgent operator;

	public StandAloneMachineAgent(String n){
		machine_status = MachineState.FREE;
		answer_status = AnswerState.NOT_TALKING;
		
		this.name = n;
		
		//TODO
		//take out testing stuff
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
	 * Used to give glass to machine
	 */
	public void msgHereIsGlass(GlassOrder order){
		addToTrace(" [MESSAGE]: receives msgHereIsGlass");
		glass = order;
		
		machine_status = MachineState.WORKING;
		
		stateChanged();
	}
	
	/*
	 * Tell machine it's done animating
	 */
	public void msgDoneJob(){
		addToTrace(" [MESSAGE]: receives msgDoneJob");
		if(glass != null && (glass.getGlassTreatmentStatus(job) == GlassTreatmentStatus.INCOMPLETE)){
			//print(job + " receive msgDoneJob()");
			machine_status = MachineState.GIVING;
		
			//Change the state of the glass
			glass.setGlassTreatmentStatus(job, GlassTreatmentStatus.COMPLETE);
			
			stateChanged();
		}
		else if(glass != null && (glass.getGlassTreatmentStatus(job) == GlassTreatmentStatus.NOT_NEEDED)){
			machine_status = MachineState.GIVING;
			
			stateChanged();
		}	
	}
	
	public void msgDoneJob(String msg){
		addToTrace(" [MESSAGE]: receives msgDoneJob " + msg);
		if(glass != null && (msg.equals("BROKEN"))){
			//print(job + " receive msgDoneJob()");
			machine_status = MachineState.GIVING;
			
			glass.broken = true;
			
			stateChanged();
		}
		else if(glass != null && (glass.getGlassTreatmentStatus(job) == GlassTreatmentStatus.NOT_NEEDED)){
			machine_status = MachineState.GIVING;
			
			stateChanged();
		}	
	}
	
	public void msgDonePass(){
		//TODO
		//Tell gui guy to stop calling this
		/*
		addToTrace(" [MESSAGE]: receives msgDonePass");
		if(glass != null){
			operator.msgHereIsTreatedGlass(glass);
			
			machine_status = MachineState.FREE;
			glass = null;
			
			stateChanged();
		}*/
	}
	
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/////
	/////	SCHEDULER
	/////
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	public boolean pickAndExecuteAnAction(){
		/*
		 * Needs to work on glass if on machine
		 */
		if(machine_status == MachineState.WORKING){
			//TODO remove testing stuff
			/*log.add(new LoggedEvent(
					"Machine State: " + machine_status + " doJob(job) will be called"));*/

			doJob(job);
			return true;
		}
		
		/*
		 * Needs to give glass to operator
		 */
		if(machine_status == MachineState.GIVING){
			//TODO remove testing stuff
			/*log.add(new LoggedEvent(
					"Machine State: " + machine_status + " giveGlassToOperator() will be called"));*/
			
			giveGlassToOperator();
			return true;
		}
		
		/*
		 * Do Nothing
		 */
		//TODO remove testing stuff
		/*log.add(new LoggedEvent(
				"Machine State: " + machine_status + " nothing was done"));*/
		
		return false;
	}
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/////
	/////	ACTIONS
	/////
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	public void doJob(String job){
		//Don't cut if it does not need it
		if(glass.getGlassTreatmentStatus(job) == GlassTreatmentStatus.INCOMPLETE){	
			//Get the machine to ask conveyor_out if it can take glass
			machine_status = MachineState.ANIMATING;
			
			//TODO remove testing stuff
			/*log.add(new LoggedEvent(
					"Machine State: " + machine_status));*/
			
			//Do animation
			addToTrace(" [ANIMATION]: is " + job);
			controller.doJob(job, glass.getGlassDesign());
		}
		else{
			machine_status = MachineState.GIVING;
		}
				
		stateChanged();
	}
	
	public void giveGlassToOperator(){
		//machine_status = MachineState.ANIMATING;
		//controller.doPass();
		operator.msgHereIsTreatedGlass(glass);
		machine_status = MachineState.FREE;
		
		stateChanged();

	}
	
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	/////
	/////	SETTERS
	/////
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	public void setOperator(OperatorAgent o){
		operator = o;
	}
	
	public void setController(StandAloneMachineController c){
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
		mtp.add(msg,this);
	}
}
