package factory.panels;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;
import javax.swing.Timer;

import factory.agent.GlassOrder;
import factory.agent.GlassOrder.GlassTreatmentStatus;
import factory.agent.GlassRobotAgent;
import factory.agent.InlineMachineAgent;
import factory.agent.ManualBreakoutOperatorAgent;
import factory.agent.OperatorAgent;
import factory.agent.PopupAgent;
import factory.agent.StandAloneMachineAgent;
import factory.agent.TransferAgent;
import factory.agent.TruckAgent;
import factory.controller.*;
import factory.gui.*;

import factory.interfaces.Disaster;
import factory.mediators.ConveyorMediator;
import factory.mediators.TransferMediator;

public class GraphicsPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    Application app;

    // Total number of glasses removed from screen/factory
    int numGlasses = 0;

    // For dragging stuff
    Point startingPoint;
    int dx = 0, dy = 0;

    // For earthquake
    int earthquakeCounter = 0;
    ArrayList<Disaster> disasterList;
    
    // For Spin
    int spinCounter = 0;
    int spinAngle = 0;

    // Main timer
    javax.swing.Timer timer;

    // Transform used for the entire screen
    AffineTransform tx2 = new AffineTransform();
    
    //Background image for factory
    private ImageIcon floorImage	= new ImageIcon("images/factoryflooroceanspray.png");

    // Panels
    JPanel mainPanel;

    // Glass array list
    ArrayList<GuiGlass> glassList;

    // Used for refreshing glassList
    int refreshCounter = 0;

    boolean flag = true;
    boolean start = true;
    //For Scale
    private double scaleX;
    private double scaleY;

    //For speed
    private int SPEED;
    //PART 1 DECLERATIONS

    /*
     *[[=====================================================]]
     *[[
     *[[	Part 1 BEGINS Here 
     *[[
     *[[=====================================================]]
     */

    //for positioning
    private int part1_X;
    private int part1_Y;

    //Bins
    private GuiBin bin;
    private GuiBin bin2;

    //Glass Robot
    private GlassRobotAgent glass_robot_agent;
    private GlassRobotController glass_robot_controller;
    private GuiGlassRobot glass_robot_gui;

    //Conveyor
    public ConveyorMediator conveyor_1a;

    //NC_Cutter
    private InlineMachineAgent nc_cutter_agent;
    private InlineMachineController nc_cutter_controller;
    private GuiNCCutter nc_cutter_gui;

    //Conveyor 2
    public ConveyorMediator conveyor_1b;

    //Breakout
    private InlineMachineAgent breakout_agent;
    private InlineMachineController breakout_controller;
    private GuiBreakout breakout_gui;

    //Conveyor 3
    public ConveyorMediator conveyor_1c;

    //Transfer
    public TransferAgent transfer_1_agent;
    private TransferController transfer_1_controller;
    private GuiTransfer transfer_1_gui;

    /*
     *[[=====================================================]]
     *[[
     *[[	Part 1 ENDS Here 
     *[[
     *[[=====================================================]]
     */

    //END PART 1
    //PART 2 ETc.
    /*
     *[[=====================================================]]
     *[[
     *[[	Part 2 STARTS Here 
     *[[
     *[[=====================================================]]
     */
    //Agent
    //Coveyors x3
    public ConveyorMediator cmPart2_1;
    public ConveyorMediator cmPart2_5; //RHYS-ManualBreakout
    public ConveyorMediator cmPart2_2;
    public ConveyorMediator cmPart2_3;

    //Popup x2
    PopupAgent popupPart2_4; //RHYS-ManualBreakout
    PopupAgent popupPart2_1;
    PopupAgent popupPart2_2;

    //Operator x4
    OperatorAgent operatorPart2_6; //RHYS-ManualBreakout
    OperatorAgent operatorPart2_1;
    OperatorAgent operatorPart2_2;
    OperatorAgent operatorPart2_3;
    OperatorAgent operatorPart2_4;

    //Truck x4
    TruckAgent truckPart2_6; //RHYS-ManualBreakout
    TruckAgent truckPart2_1;
    TruckAgent truckPart2_2;
    TruckAgent truckPart2_3;
    TruckAgent truckPart2_4;

    //Standalone x4
    StandAloneMachineAgent grinderPart2_6; //RHYS-ManualBreakout
    StandAloneMachineAgent crossSeamerPart2_1;
    StandAloneMachineAgent drillPart2_2;
    StandAloneMachineAgent grinderPart2_3;
    StandAloneMachineAgent paintPart2_4;
    
    //ManualBreakout
    public ManualBreakoutOperatorAgent manualBreakoutAgent; //RHYS-ManualBreakout

    //Transfer
    TransferAgent transferPart2To3;

    //Gui
    //Conveyors made in ConveyorMeiator

    //Popups
    GuiPopup guiPopupPart2_4; //RHYS-ManualBreakout
    GuiPopup guiPopupPart2_1;
    GuiPopup guiPopupPart2_2;

    //Operators
    GuiOperator guiOperatorPart2_6; //RHYS-ManualBreakout
    GuiOperator guiOperatorPart2_1;
    GuiOperator guiOperatorPart2_2;
    GuiOperator guiOperatorPart2_3;
    GuiOperator guiOperatorPart2_4;

    //Stations
    GuiStation guiStationPart2_1;
    GuiStation guiStationPart2_2;
    GuiStation guiStationPart2_3;
    GuiStation guiStationPart2_4;

    //Trucks
    GuiTruck guiTruckPart2_6; //RHYS-ManualBreakout
    GuiTruck guiTruckPart2_1;
    GuiTruck guiTruckPart2_2;
    GuiTruck guiTruckPart2_3;
    GuiTruck guiTruckPart2_4;

    //Standalones
    GuiGrinder guiGrinderPart2_6; //RHYS-ManualBreakout
    GuiCrossSeamer guiCrossSeamerPart2_1;
    GuiDrill guiDrillPart2_2;
    GuiGrinder guiGrinderPart2_3;
    GuiPaint guiPaintPart2_4;
    
    //ManualBreakout
    GuiManualBreakout guiManualBreakout; //RHYS-ManualBreakout

    //Transfer
    GuiTransfer guiTransferPart2To3;

    //Controller
    //Popups
    PopupController popupControllerPart2_4; //RHYS-ManualBreakout
    PopupController popupControllerPart2_1;
    PopupController popupControllerPart2_2;

    //Operators
    OperatorController operatorControllerPart2_6; //RHYS-ManualBreakout
    OperatorController operatorControllerPart2_1;
    OperatorController operatorControllerPart2_2;
    OperatorController operatorControllerPart2_3;
    OperatorController operatorControllerPart2_4;

    //Trucks
    TruckController truckControllerPart2_6; //RHYS-ManualBreakout
    TruckController truckControllerPart2_1;
    TruckController truckControllerPart2_2;
    TruckController truckControllerPart2_3;
    TruckController truckControllerPart2_4;

    //StandAlones
    StandAloneMachineController grinderControllerPart2_6; //RHYS-ManualBreakout
    StandAloneMachineController crossSeamerControllerPart2_1;
    StandAloneMachineController drillControllerPart2_2;
    StandAloneMachineController grinderControllerPart2_3;
    StandAloneMachineController paintControllerPart2_4;
    
    //ManualBreakout
    ManualBreakoutController manualBreakoutController; //RHYS-ManualBreakout

    //Transfer
    TransferController transferControllerPart2To3;

    //Array list for all the Gui stuff
    ArrayList<FactoryPart> guiList = new ArrayList<FactoryPart>();
    ArrayList<AnimatedPart> animatedList = new ArrayList<AnimatedPart>();
    //END PART 2
    /*
     *[[=====================================================]]
     *[[
     *[[	Part 2 ENDS Here 
     *[[
     *[[=====================================================]]
     */

    //PART 3

    /*
     *[[=====================================================]]
     *[[
     *[[	Part 3 BEGINS Here 
     *[[
     *[[=====================================================]]
     */

    /*
     * QUANTITY		--		TYPE
     * 
     * 		3		--		Conveyor					
     * 		2		--		InlineMachine -- Washer, UVLamp
     * 		1		--		Transfer
     */	

    //Part 3 Location
    private int part3_X;
    private int part3_Y;

    //Conveyor DATA
    public ConveyorMediator conveyor_3a;
    public ConveyorMediator conveyor_3b;
    public ConveyorMediator conveyor_3c;

    //Washer DATA
    private InlineMachineAgent washer_Agent;
    private InlineMachineController washer_Controller;
    private GuiWasher washer_Gui;

    private InlineMachineAgent uvLamp_Agent;
    private InlineMachineController uvLamp_Controller;
    private GuiUVLamp uvLamp_Gui;

    public TransferMediator tmPart3;

    /*
     *[[=====================================================]]
     *[[
     *[[	Part 3 ENDS Here 
     *[[
     *[[=====================================================]]
     */

    /**
     * XXX
     * PART4
     */


    public ConveyorMediator cmPart4_1 = new ConveyorMediator("Conveyor4_1");
    public ConveyorMediator cmPart4_2 = new ConveyorMediator("Conveyor4_2");
    // ConveyorMediator cmPart4_3;

    //InlineMachineAgent uvLampPart4;
    //InlineMachineController uvLampControllerPart4;
    //GuiUVLamp uvLampGuiPart4;

    InlineMachineAgent ovenPart4;
    InlineMachineController ovenControllerPart4;
    GuiOven ovenGuiPart4;

    TruckAgent truckPart4;
    TruckController truckControllerPart4;
    GuiTruck truckGuiPart4;

    //TODO
    GlassRobotAgent glassRobotPart4;
    GlassRobotController glassRobotControllerPart4;
    GuiGlassRobot glassRobotGuiPart4;

    //END PART 4

    public GraphicsPanel(Application mainApp) {

	app = mainApp;

	// Add mouse listeners
	this.addMouseListener(this);
	this.addMouseMotionListener(this);

	glassList = new ArrayList<GuiGlass>();
	//transfer = new GuiTransfer(100,100);
	//PART 1 CONSTRUCTORS

	/*
	 *[[=====================================================]]
	 *[[
	 *[[	Part 1 BEGINS Here 
	 *[[
	 *[[=====================================================]]
	 */

	// Default scaling factors

	SPEED = 1;

	//Coordinates for parts
	part1_X = 175;
	part1_Y = 1100;

	//Glass Robot
	glass_robot_agent = new GlassRobotAgent("Glass Robot");
	glass_robot_gui = new GuiGlassRobot();
	glass_robot_controller = new GlassRobotController(glass_robot_agent, glass_robot_gui);

	glass_robot_gui.setX(part1_X+125);
	glass_robot_gui.setY(part1_Y+200);

	glass_robot_agent.setController(glass_robot_controller);
	glass_robot_gui.setController(glass_robot_controller);

	//Bins
	bin = new GuiBin();
	bin.setX(glass_robot_gui.getX() - (bin.getWidth() + 55));
	bin.setY(part1_Y+200);

	bin2 = new GuiBin();
	bin2.setX(glass_robot_gui.getX() + (glass_robot_gui.getWidth() + 25));
	bin2.setY(part1_Y-180);

	//Conveyor 1a
	conveyor_1a = new ConveyorMediator("Conveyor - 1A");
	conveyor_1a.gui.setup(part1_X-25, part1_Y-50, 200, FactoryPart.NORTH);

	//NC_Cutter
	nc_cutter_agent = new InlineMachineAgent("NC_Cutter");
	nc_cutter_gui = new GuiNCCutter(part1_X-25, part1_Y-150, 0);
	nc_cutter_controller = new InlineMachineController();

	nc_cutter_controller.setAgent(nc_cutter_agent);
	nc_cutter_controller.setGui(nc_cutter_gui);

	nc_cutter_gui.setController(nc_cutter_controller);
	nc_cutter_agent.setController(nc_cutter_controller);
	nc_cutter_agent.setJob("Cutting");

	//Conveyor 1b 
	conveyor_1b = new ConveyorMediator("Conveyor - 1B");
	conveyor_1b.gui.setup(part1_X-25, part1_Y-350, 200, FactoryPart.NORTH);

	//Breakout
	breakout_agent = new InlineMachineAgent("Breakout");
	breakout_gui = new GuiBreakout(part1_X-55, part1_Y-448, 0);
	breakout_controller = new InlineMachineController();

	breakout_controller.setAgent(breakout_agent);
	breakout_controller.setGui(breakout_gui);

	breakout_gui.setController(breakout_controller);
	breakout_agent.setController(breakout_controller);
	breakout_agent.setJob("Breakout");

	//Conveyor 1c
	conveyor_1c = new ConveyorMediator("Conveyor - 1C");
	conveyor_1c.gui.setup(part1_X-25, part1_Y-640, 200, FactoryPart.NORTH);

	//Transfer
	transfer_1_agent = new TransferAgent("Transfer 1");
	transfer_1_gui = new GuiTransfer(part1_X-25, part1_Y-751, 0);
	transfer_1_controller = new TransferController(transfer_1_agent, transfer_1_gui);

	transfer_1_agent.setController(transfer_1_controller);
	//transfer_1_agent.debug = true;
	transfer_1_gui.setController(transfer_1_controller);

	/**
	 * Add stuff to GuiList so Jay can do something
	 */
	guiList.add(glass_robot_gui);
	guiList.add(bin);
	//guiList.add(bin2);
	guiList.add(conveyor_1a.gui);
	guiList.add(nc_cutter_gui);
	guiList.add(conveyor_1b.gui);
	guiList.add(breakout_gui);
	guiList.add(conveyor_1c.gui);
	guiList.add(transfer_1_gui);

	/**
	 * Set Dependencies
	 */
	glass_robot_agent.setConveyor(conveyor_1a.agent);
	glass_robot_gui.setPart(conveyor_1a.gui, FactoryPart.NORTH);
	glass_robot_gui.setBins(bin, bin2);
	glass_robot_gui.setConveyor(conveyor_1a.gui);

	//conveyor coordinates hack
	//glass_robot_gui.setConveyorCoordinates(false);

	conveyor_1a.setSource(glass_robot_agent, glass_robot_gui, FactoryPart.SOUTH);
	conveyor_1a.setDestination(nc_cutter_agent, nc_cutter_gui, FactoryPart.NORTH);

	nc_cutter_agent.setConveyor(conveyor_1a.agent, conveyor_1b.agent);
	nc_cutter_gui.setPart(conveyor_1a.gui, FactoryPart.SOUTH);
	nc_cutter_gui.setPart(conveyor_1b.gui, FactoryPart.NORTH);

	conveyor_1b.setSource(nc_cutter_agent, nc_cutter_gui, FactoryPart.SOUTH);
	conveyor_1b.setDestination(breakout_agent, breakout_gui, FactoryPart.NORTH);

	breakout_agent.setConveyor(conveyor_1b.agent, conveyor_1c.agent);
	breakout_gui.setPart(conveyor_1b.gui, FactoryPart.SOUTH);
	breakout_gui.setPart(conveyor_1c.gui, FactoryPart.NORTH);

	conveyor_1c.setSource(breakout_agent, breakout_gui, FactoryPart.SOUTH);
	conveyor_1c.setDestination(transfer_1_agent, transfer_1_gui, FactoryPart.NORTH);

	/**
	 * Start Threads
	 */
	//glass_robot_agent.enable_debug = true;
	//conveyor_1a.agent.debug = true;
	//conveyor_1b.agent.debug = true;
	//conveyor_1c.agent.debug = true;

	glass_robot_agent.startThread();
	nc_cutter_agent.startThread();
	breakout_agent.startThread();
	transfer_1_agent.startThread();

	conveyor_1a.agent.startThread();
	conveyor_1b.agent.startThread();
	conveyor_1c.agent.startThread();

	/*
	 *[[=====================================================]]
	 *[[
	 *[[	Part 1 ENDS Here 
	 *[[
	 *[[=====================================================]]
	 */

	//END PART 1

	/*
	 *[[=====================================================]]
	 *[[
	 *[[	Part 2 START Here 
	 *[[
	 *[[=====================================================]]
	 */
	//PART 2 ETc.
	cmPart2_1 = new ConveyorMediator("ConveyorPart2_1");
	cmPart2_5 = new ConveyorMediator("ConveyorPart2_5"); //RHYS-ManualBreakout
	cmPart2_2 = new ConveyorMediator("ConveyorPart2_2");
	cmPart2_3 = new ConveyorMediator("ConveyorPart2_3");

	//cmPart2_1.agent.debug = true;
	//cmPart2_2.agent.debug = true;
	//cmPart2_3.agent.debug = true;

	//Popup x2
	popupPart2_4 = new PopupAgent("PopupPart2_4"); //RHYS-ManualBreakout
	popupPart2_1 = new PopupAgent("PopupPart2_1");
	popupPart2_2 = new PopupAgent("PopupPart2_2");

	//Operator x4
	operatorPart2_6 = new OperatorAgent("OperatorPart2_6"); //RHYS-ManualBreakout
	operatorPart2_6.setJob("Grinding"); //RHYS-ManualBreakout
	operatorPart2_1 = new OperatorAgent("OperatorPart2_1");
	operatorPart2_1.setJob("CrossSeaming");
	operatorPart2_2 = new OperatorAgent("OperatorPart2_2");
	operatorPart2_2.setJob("Drilling");
	operatorPart2_3 = new OperatorAgent("OperatorPart2_3");
	operatorPart2_3.setJob("Grinding");
	operatorPart2_4 = new OperatorAgent("OperatorPart2_4");
	operatorPart2_4.setJob("Painting");

	//Truck x4
	truckPart2_6 = new TruckAgent("TruckPart2_6"); //RHYS-ManualBreakout
	truckPart2_1 = new TruckAgent("TruckPart2_1");
	truckPart2_2 = new TruckAgent("TruckPart2_2");
	truckPart2_3 = new TruckAgent("TruckPart2_3");
	truckPart2_4 = new TruckAgent("TruckPart2_4");

	//Standalone x4
	grinderPart2_6 = new StandAloneMachineAgent("Grinder 2"); //RHYS-ManualBreakout
	grinderPart2_6.setJob("Grinding"); //RHYS-ManualBreakout
	crossSeamerPart2_1 = new StandAloneMachineAgent("Cross Seamer");
	crossSeamerPart2_1.setJob("CrossSeaming");
	drillPart2_2 = new StandAloneMachineAgent("Drill");
	drillPart2_2.setJob("Drilling");
	grinderPart2_3 = new StandAloneMachineAgent("Grinder");
	grinderPart2_3.setJob("Grinding");
	paintPart2_4 = new StandAloneMachineAgent("Paint");
	paintPart2_4.setJob("Painting");
	
	//ManualBreakout
	manualBreakoutAgent = new ManualBreakoutOperatorAgent("Manual Breakout"); //RHYS-ManualBreakout

	//Transfer
	transferPart2To3 = new TransferAgent("TransferPart2To3");

	//XXX Gui
	//Conveyors made in ConveyorMeiator

	//Popups
	guiPopupPart2_4 = new GuiPopup(); //RHYS-ManualBreakout
	guiPopupPart2_1 = new GuiPopup();
	guiPopupPart2_2 = new GuiPopup();

	//Operators
	guiOperatorPart2_6 = new GuiOperator(0, 0, 0); //RHYS-ManualBreakout
	guiOperatorPart2_1 = new GuiOperator(0,0,0);
	guiOperatorPart2_2 = new GuiOperator(0,0,0);
	guiOperatorPart2_3 = new GuiOperator(0,0,0);
	guiOperatorPart2_4 = new GuiOperator(0,0,0);

	//Stations
	guiStationPart2_1 = new GuiStation();
	guiStationPart2_2 = new GuiStation();
	guiStationPart2_3 = new GuiStation();
	guiStationPart2_4 = new GuiStation();

	//Trucks
	guiTruckPart2_6 = new GuiTruck(0,0,0); //RHYS-ManualBreakout
	guiTruckPart2_1 = new GuiTruck(0,0,0);
	guiTruckPart2_2 = new GuiTruck(0,0,0);
	guiTruckPart2_3 = new GuiTruck(0,0,0);
	guiTruckPart2_4 = new GuiTruck(0,0,0);

	//Standalones
	guiGrinderPart2_6 = new GuiGrinder(); //RHYS-ManualBreakout
	guiCrossSeamerPart2_1 = new GuiCrossSeamer();
	guiDrillPart2_2 = new GuiDrill();
	guiGrinderPart2_3 = new GuiGrinder();
	guiPaintPart2_4 = new GuiPaint();
	
	//ManualBreakout
	guiManualBreakout = new GuiManualBreakout(0, 0, 0); //RHYS-ManualBreakout

	//Transfer
	guiTransferPart2To3 = new GuiTransfer(0,0,0);

	//Add to the list
	guiList.add(guiPopupPart2_1);
	guiList.add(guiPopupPart2_2);
	guiList.add(guiPopupPart2_4); //RHYS-ManualBreakout
	guiList.add(guiTruckPart2_1);
	guiList.add(guiTruckPart2_2);
	guiList.add(guiTruckPart2_3);
	guiList.add(guiTruckPart2_4);
	guiList.add(guiTruckPart2_6); //RHYS-ManualBreakout
	guiList.add(guiCrossSeamerPart2_1);
	guiList.add(guiDrillPart2_2);
	guiList.add(guiGrinderPart2_3);
	guiList.add(guiPaintPart2_4);
	guiList.add(guiGrinderPart2_6); //RHYS-ManualBreakout
	guiList.add(guiTransferPart2To3);
	guiList.add(cmPart2_1.gui);
	guiList.add(cmPart2_2.gui);
	guiList.add(cmPart2_3.gui);
	guiList.add(cmPart2_5.gui); //RHYS-ManualBreakout
	guiList.add(guiOperatorPart2_1);
	guiList.add(guiOperatorPart2_2);
	guiList.add(guiOperatorPart2_3);
	guiList.add(guiOperatorPart2_4);
	guiList.add(guiOperatorPart2_6); //RHYS-ManualBreakout
	guiList.add(guiManualBreakout); //RHYS-ManualBreakout

	animatedList.add(guiPopupPart2_1);
	animatedList.add(guiPopupPart2_2);
	animatedList.add(guiPopupPart2_4); //RHYS-ManualBreakout
	animatedList.add(guiTruckPart2_1);
	animatedList.add(guiTruckPart2_2);
	animatedList.add(guiTruckPart2_3);
	animatedList.add(guiTruckPart2_4);
	animatedList.add(guiTruckPart2_6); //RHYS-ManualBreakout
	animatedList.add(guiTransferPart2To3);
	animatedList.add(cmPart2_1.gui);
	animatedList.add(cmPart2_2.gui);
	animatedList.add(cmPart2_3.gui);
	animatedList.add(cmPart2_5.gui); //RHYS-ManualBreakout
	animatedList.add(guiCrossSeamerPart2_1);
	animatedList.add(guiDrillPart2_2);
	animatedList.add(guiGrinderPart2_3);
	animatedList.add(guiPaintPart2_4);
	animatedList.add(guiGrinderPart2_6); //RHYS-ManualBreakout
	animatedList.add(guiOperatorPart2_1);
	animatedList.add(guiOperatorPart2_2);
	animatedList.add(guiOperatorPart2_3);
	animatedList.add(guiOperatorPart2_4);
	animatedList.add(guiOperatorPart2_6); //RHYS-ManualBreakout
	animatedList.add(guiManualBreakout); //RHYS-ManualBreakout


	//XXX Controller
	//Popups
	popupControllerPart2_1 = new PopupController(popupPart2_1, guiPopupPart2_1);
	popupControllerPart2_2 = new PopupController(popupPart2_2, guiPopupPart2_2);
	popupPart2_1.setController(popupControllerPart2_1);
	guiPopupPart2_1.setController(popupControllerPart2_1);
	popupPart2_2.setController(popupControllerPart2_2);
	guiPopupPart2_2.setController(popupControllerPart2_2);
	popupControllerPart2_4 = new PopupController(popupPart2_4, guiPopupPart2_4); //RHYS-ManualBreakout
	popupPart2_4.setController(popupControllerPart2_4); //RHYS-ManualBreakout
	guiPopupPart2_4.setController(popupControllerPart2_4); //RHYS-ManualBreakout


	//Operators
	operatorControllerPart2_1 = new OperatorController();
	operatorControllerPart2_1.setGuiOperator(guiOperatorPart2_1);
	operatorControllerPart2_1.setGuiStation(guiCrossSeamerPart2_1);
	operatorControllerPart2_1.setOperatorAgent(operatorPart2_1);
	guiCrossSeamerPart2_1.setOperatorController(operatorControllerPart2_1);
	operatorControllerPart2_2 = new OperatorController();
	operatorControllerPart2_2.setGuiOperator(guiOperatorPart2_2);
	operatorControllerPart2_2.setGuiStation(guiDrillPart2_2);
	operatorControllerPart2_2.setOperatorAgent(operatorPart2_2);
	guiDrillPart2_2.setOperatorController(operatorControllerPart2_2);
	operatorControllerPart2_3 = new OperatorController();
	operatorControllerPart2_3.setGuiOperator(guiOperatorPart2_3);
	operatorControllerPart2_3.setGuiStation(guiGrinderPart2_3);
	operatorControllerPart2_3.setOperatorAgent(operatorPart2_3);
	guiGrinderPart2_3.setOperatorController(operatorControllerPart2_3);
	operatorControllerPart2_4 = new OperatorController();
	operatorControllerPart2_4.setGuiOperator(guiOperatorPart2_4);
	operatorControllerPart2_4.setGuiStation(guiPaintPart2_4);
	operatorControllerPart2_4.setOperatorAgent(operatorPart2_4);
	guiPaintPart2_4.setOpController(operatorControllerPart2_4);
	operatorPart2_1.setOperatorController(operatorControllerPart2_1);
	guiOperatorPart2_1.setController(operatorControllerPart2_1);
	operatorPart2_2.setOperatorController(operatorControllerPart2_2);
	guiOperatorPart2_2.setController(operatorControllerPart2_2);
	operatorPart2_3.setOperatorController(operatorControllerPart2_3);
	guiOperatorPart2_3.setController(operatorControllerPart2_3);
	operatorPart2_4.setOperatorController(operatorControllerPart2_4);
	guiOperatorPart2_4.setController(operatorControllerPart2_4);
	operatorControllerPart2_6 = new OperatorController(); //RHYS-ManualBreakout
	operatorControllerPart2_6.setGuiOperator(guiOperatorPart2_6); //RHYS-ManualBreakout
	operatorControllerPart2_6.setGuiStation(guiGrinderPart2_6); //RHYS-ManualBreakout
	operatorControllerPart2_6.setOperatorAgent(operatorPart2_6); //RHYS-ManualBreakout
	guiGrinderPart2_6.setOperatorController(operatorControllerPart2_6); //RHYS-ManualBreakout
	guiOperatorPart2_6.setController(operatorControllerPart2_6); //RHYS-ManualBreakout
	operatorPart2_6.setOperatorController(operatorControllerPart2_6); //RHYS-ManualBreakout

	//StandAlones
	crossSeamerControllerPart2_1 = new StandAloneMachineController();
	crossSeamerControllerPart2_1.setAgent(crossSeamerPart2_1);
	crossSeamerControllerPart2_1.setGui(guiCrossSeamerPart2_1);
	drillControllerPart2_2 = new StandAloneMachineController();
	drillControllerPart2_2.setAgent(drillPart2_2);
	drillControllerPart2_2.setGui(guiDrillPart2_2);
	grinderControllerPart2_3 = new StandAloneMachineController();
	grinderControllerPart2_3.setAgent(grinderPart2_3);
	grinderControllerPart2_3.setGui(guiGrinderPart2_3);
	paintControllerPart2_4 = new StandAloneMachineController();
	paintControllerPart2_4.setAgent(paintPart2_4);
	paintControllerPart2_4.setGui(guiPaintPart2_4);
	crossSeamerPart2_1.setController(crossSeamerControllerPart2_1);
	guiCrossSeamerPart2_1.setController(crossSeamerControllerPart2_1);
	drillPart2_2.setController(drillControllerPart2_2);
	guiDrillPart2_2.setController(drillControllerPart2_2);
	grinderPart2_3.setController(grinderControllerPart2_3);
	guiGrinderPart2_3.setController(grinderControllerPart2_3);
	paintPart2_4.setController(paintControllerPart2_4);
	guiPaintPart2_4.setController(paintControllerPart2_4);
	grinderControllerPart2_6 = new StandAloneMachineController(); //RHYS-ManualBreakout
	guiGrinderPart2_6.setController(grinderControllerPart2_6); //RHYS-ManualBreakout
	grinderControllerPart2_6.setGui(guiGrinderPart2_6); //RHYS-ManualBreakout
	grinderControllerPart2_6.setAgent(grinderPart2_6); //RHYS-ManualBreakout
	grinderPart2_6.setController(grinderControllerPart2_6); //RHYS-ManualBreakout

	//Transfer
	transferControllerPart2To3 = new TransferController(transferPart2To3, guiTransferPart2To3);
	transferPart2To3.setController(transferControllerPart2To3);
	guiTransferPart2To3.setController(transferControllerPart2To3);

	//Truck
	truckControllerPart2_1 = new TruckController(truckPart2_1, guiTruckPart2_1);
	truckControllerPart2_2 = new TruckController(truckPart2_2, guiTruckPart2_2);
	truckControllerPart2_3 = new TruckController(truckPart2_3, guiTruckPart2_3);
	truckControllerPart2_4 = new TruckController(truckPart2_4, guiTruckPart2_4);
	truckControllerPart2_6 = new TruckController(truckPart2_6, guiTruckPart2_6); //RHYS-ManualBreakout
	truckPart2_1.setController(truckControllerPart2_1);
	guiTruckPart2_1.setController(truckControllerPart2_1);

	truckPart2_2.setController(truckControllerPart2_2);
	guiTruckPart2_2.setController(truckControllerPart2_2);

	truckPart2_3.setController(truckControllerPart2_3);
	guiTruckPart2_3.setController(truckControllerPart2_3);

	truckPart2_4.setController(truckControllerPart2_4);
	guiTruckPart2_4.setController(truckControllerPart2_4);
	
	truckPart2_6.setController(truckControllerPart2_6); //RHYS-ManualBreakout
	guiTruckPart2_6.setController(truckControllerPart2_6); //RHYS-ManualBreakout
	
	//ManualBreakout
	manualBreakoutController = new ManualBreakoutController(); //RHYS-ManualBreakout
	manualBreakoutController.setGui(guiManualBreakout); //RHYS-ManualBreakout
	manualBreakoutController.setOperatorAgent(manualBreakoutAgent); //RHYS-ManualBreakout
	manualBreakoutAgent.setOperatorController(manualBreakoutController); //RHYS-ManualBreakout
	guiManualBreakout.setOperatorController(manualBreakoutController); //RHYS-ManualBreakout


	// Initialize Connections
	//Agents
	//XXX
	// GuiPart tempStarter = new GuiPart();
	cmPart2_5.setSource(transfer_1_agent, transfer_1_gui, FactoryPart.WEST); //RHYS-ManualBreakout
	cmPart2_5.setDestination(popupPart2_4, guiPopupPart2_4, FactoryPart.EAST); //RHYS-ManualBreakout
	popupPart2_4.setFromConveyor(cmPart2_5.agent); //RHYS-ManualBreakout
	popupPart2_4.setToConveyor(cmPart2_1.agent); //RHYS-ManualBreakout
	popupPart2_4.setOperator1(manualBreakoutAgent); //RHYS-ManualBreakout
	popupPart2_4.setOperator2(operatorPart2_6); //RHYS-ManualBreakout
	operatorPart2_6.addOperatorDownLine(operatorPart2_3); //RHYS-ManualBreakout
	operatorPart2_6.setPopup(popupPart2_4); //RHYS-ManualBreakout
	operatorPart2_6.setTruck(truckPart2_6); //RHYS-ManualBreakout
	operatorPart2_6.setMachine(grinderPart2_6); //RHYS-ManualBreakout
	manualBreakoutAgent.setPopup(popupPart2_4); //RHYS-ManualBreakout
	grinderPart2_6.setOperator(operatorPart2_6); //RHYS-ManualBreakout
	guiOperatorPart2_6.setPart(guiGrinderPart2_6, FactoryPart.EAST); //RHYS-ManualBreakout
	guiOperatorPart2_6.setPart(guiTruckPart2_6, FactoryPart.SOUTH); //RHYS-ManualBreakout
	guiTruckPart2_6.setPart(guiOperatorPart2_6, FactoryPart.NORTH); //RHYS-ManualBreakout
	guiPopupPart2_4.setPart(guiGrinderPart2_6, FactoryPart.SOUTH); //RHYS-ManualBreakout
	guiPopupPart2_4.setPart(guiManualBreakout, FactoryPart.NORTH); //RHYS-ManualBreakout
	guiPopupPart2_4.setPart(cmPart2_5.gui, FactoryPart.WEST); //RHYS-ManualBreakout
	guiPopupPart2_4.setPart(cmPart2_1.gui, FactoryPart.EAST); //RHYS-ManualBreakout
	guiGrinderPart2_6.setPart(guiOperatorPart2_6, FactoryPart.WEST); //RHYS-ManualBreakout
	guiManualBreakout.setPart(guiPopupPart2_4, FactoryPart.SOUTH); //RHYS-ManualBreakout
	guiGrinderPart2_6.setPart(guiPopupPart2_4, FactoryPart.NORTH); //RHYS-ManualBreakout

	
	
	//cmPart2_1.setSource(transfer_1_agent, transfer_1_gui, FactoryPart.WEST);
	cmPart2_1.setSource(popupPart2_4, guiPopupPart2_4, FactoryPart.WEST);
	cmPart2_1.setDestination(popupPart2_1, guiPopupPart2_1, FactoryPart.EAST);
	popupPart2_1.setFromConveyor(cmPart2_1.agent);
	popupPart2_1.setToConveyor(cmPart2_2.agent);
	popupPart2_1.setOperator1(operatorPart2_1);
	popupPart2_1.setOperator2(operatorPart2_2);
	operatorPart2_1.setPopup(popupPart2_1);
	operatorPart2_2.setPopup(popupPart2_1);
	operatorPart2_1.setTruck(truckPart2_1);
	operatorPart2_2.setTruck(truckPart2_2);
	operatorPart2_1.setMachine(crossSeamerPart2_1);
	operatorPart2_2.setMachine(drillPart2_2);
	crossSeamerPart2_1.setOperator(operatorPart2_1);
	drillPart2_2.setOperator(operatorPart2_2);
	guiOperatorPart2_1.setPart(guiCrossSeamerPart2_1, FactoryPart.EAST);
	guiOperatorPart2_1.setPart(guiTruckPart2_1, FactoryPart.NORTH);
	guiOperatorPart2_2.setPart(guiTruckPart2_2, FactoryPart.SOUTH);
	guiOperatorPart2_2.setPart(guiDrillPart2_2, FactoryPart.WEST);
	guiTruckPart2_1.setPart(guiOperatorPart2_1, FactoryPart.SOUTH);
	guiTruckPart2_2.setPart(guiOperatorPart2_2, FactoryPart.NORTH);
	guiPopupPart2_1.setPart(guiCrossSeamerPart2_1, FactoryPart.NORTH);
	guiPopupPart2_1.setPart(guiDrillPart2_2, FactoryPart.SOUTH);
	guiPopupPart2_1.setPart(cmPart2_1.gui, FactoryPart.WEST);
	guiPopupPart2_1.setPart(cmPart2_2.gui, FactoryPart.EAST);
	guiCrossSeamerPart2_1.setPart(guiOperatorPart2_1, FactoryPart.WEST);
	guiCrossSeamerPart2_1.setPart(guiPopupPart2_1, FactoryPart.SOUTH);
	guiDrillPart2_2.setPart(guiOperatorPart2_2, FactoryPart.EAST);
	guiDrillPart2_2.setPart(guiPopupPart2_1, FactoryPart.NORTH);
	cmPart2_2.setSource(popupPart2_1, guiPopupPart2_1, FactoryPart.WEST);
	cmPart2_2.setDestination(popupPart2_2, guiPopupPart2_2, FactoryPart.EAST);
	popupPart2_2.setFromConveyor(cmPart2_2.agent);
	popupPart2_2.setToConveyor(cmPart2_3.agent);
	popupPart2_2.setOperator1(operatorPart2_3);
	popupPart2_2.setOperator2(operatorPart2_4);
	guiPopupPart2_2.setPart(cmPart2_2.gui, FactoryPart.WEST);
	guiPopupPart2_2.setPart(cmPart2_3.gui, FactoryPart.EAST);
	guiPopupPart2_2.setPart(guiGrinderPart2_3, FactoryPart.NORTH);
	guiPopupPart2_2.setPart(guiPaintPart2_4, FactoryPart.SOUTH);
	operatorPart2_3.setMachine(grinderPart2_3);
	operatorPart2_3.setPopup(popupPart2_2);
	operatorPart2_3.setTruck(truckPart2_3);
	operatorPart2_4.setMachine(paintPart2_4);
	operatorPart2_4.setPopup(popupPart2_2);
	operatorPart2_4.setTruck(truckPart2_4);
	grinderPart2_3.setOperator(operatorPart2_3);
	paintPart2_4.setOperator(operatorPart2_4);
	guiOperatorPart2_3.setPart(guiGrinderPart2_3, FactoryPart.EAST);
	guiOperatorPart2_3.setPart(guiTruckPart2_3, FactoryPart.NORTH);
	guiOperatorPart2_4.setPart(guiPaintPart2_4, FactoryPart.EAST);
	guiOperatorPart2_4.setPart(guiTruckPart2_4, FactoryPart.SOUTH);
	guiTruckPart2_3.setPart(guiOperatorPart2_3, FactoryPart.SOUTH);
	guiTruckPart2_4.setPart(guiOperatorPart2_4, FactoryPart.NORTH);
	guiGrinderPart2_3.setPart(guiOperatorPart2_3, FactoryPart.WEST);
	guiGrinderPart2_3.setPart(guiPopupPart2_2, FactoryPart.SOUTH);
	guiPaintPart2_4.setPart(guiOperatorPart2_4, FactoryPart.WEST);
	guiPaintPart2_4.setPart(guiPopupPart2_2, FactoryPart.NORTH);
	cmPart2_3.setSource(popupPart2_2, guiPopupPart2_2, FactoryPart.WEST);
	cmPart2_3.setDestination(transferPart2To3, guiTransferPart2To3, FactoryPart.EAST);

	//XXX
	cmPart2_1.agent.startThread();
	cmPart2_2.agent.startThread();
	cmPart2_3.agent.startThread();

	popupPart2_1.startThread();
	popupPart2_2.startThread();

	operatorPart2_1.startThread();
	operatorPart2_2.startThread();
	operatorPart2_3.startThread();
	operatorPart2_4.startThread();

	truckPart2_1.startThread();
	truckPart2_2.startThread();
	truckPart2_4.startThread();
	truckPart2_3.startThread();
	truckPart2_3.enable_debug = true;

	crossSeamerPart2_1.startThread();
	drillPart2_2.startThread();
	grinderPart2_3.startThread();
	paintPart2_4.startThread();
	
	//RHYS-ManualBreakout
	truckPart2_6.startThread();
	manualBreakoutAgent.startThread();
	cmPart2_5.agent.startThread();
	operatorPart2_6.startThread();
	grinderPart2_6.startThread();
	popupPart2_4.startThread();

	transferPart2To3.startThread();

	//teuhsateo
	//XXX
	//Set up coordinates
	cmPart2_5.gui.setup(transfer_1_gui.getX() + transfer_1_gui.getWidth(), transfer_1_gui.getY(), 320, FactoryPart.EAST);
	guiPopupPart2_4.setX(cmPart2_5.gui.getX() + cmPart2_5.gui.getLength());
	guiPopupPart2_4.setY(cmPart2_5.gui.getY());
	guiGrinderPart2_6.setX(guiPopupPart2_4.getX()-(guiGrinderPart2_6.getWidth()-guiPopupPart2_4.getWidth())/2);
	guiGrinderPart2_6.setY(guiPopupPart2_4.getY()+guiPopupPart2_4.getHeight());
	guiGrinderPart2_6.setRotation(180);
	guiManualBreakout.setX(guiPopupPart2_4.getX()-(guiManualBreakout.getWidth()-guiPopupPart2_4.getWidth())/2);
	guiManualBreakout.setY(guiPopupPart2_4.getY()-guiManualBreakout.getHeight());
	guiOperatorPart2_6.setX(guiGrinderPart2_6.getX() - guiOperatorPart2_6.getWidth());
	guiOperatorPart2_6.setY(guiGrinderPart2_6.getY() + guiGrinderPart2_6.getHeight()/2 - guiOperatorPart2_6.getHeight());
	guiTruckPart2_6.setX(guiOperatorPart2_6.getX() - guiTruckPart2_6.getWidth());
	guiTruckPart2_6.setY(guiOperatorPart2_6.getY() + guiOperatorPart2_6.getHeight());
	guiTruckPart2_6.setRotation(180);
	
	//cmPart2_1.gui.setup(transfer_1_gui.getX() + transfer_1_gui.getWidth(), transfer_1_gui.getY(), 720, FactoryPart.EAST);
	cmPart2_1.gui.setup(guiPopupPart2_4.getWidth()+guiPopupPart2_4.getX(), cmPart2_5.gui.getY(), 320, FactoryPart.EAST);
	guiPopupPart2_1.setX(cmPart2_1.gui.getX() + cmPart2_1.gui.getLength());
	guiPopupPart2_1.setY(cmPart2_1.gui.getY());
	cmPart2_2.gui.setup(guiPopupPart2_1.getWidth()+guiPopupPart2_1.getX(), cmPart2_1.gui.getY(), 320, FactoryPart.EAST);
	guiPopupPart2_2.setX(cmPart2_2.gui.getX() + cmPart2_2.gui.getLength());
	guiPopupPart2_2.setY(cmPart2_2.gui.getY());
	cmPart2_3.gui.setup(guiPopupPart2_2.getWidth()+guiPopupPart2_2.getX(), cmPart2_2.gui.getY(), 320, FactoryPart.EAST);
	guiTransferPart2To3.setX(cmPart2_3.gui.getX() + cmPart2_3.gui.getLength());
	guiTransferPart2To3.setY(cmPart2_3.gui.getY());
	guiTransferPart2To3.setRotation(90);

	guiCrossSeamerPart2_1.setX(guiPopupPart2_1.getX()-(guiCrossSeamerPart2_1.getWidth()-guiPopupPart2_1.getWidth())/2);
	guiCrossSeamerPart2_1.setY(guiPopupPart2_1.getY()-guiCrossSeamerPart2_1.getHeight());
	guiDrillPart2_2.setX(guiPopupPart2_1.getX()-(guiDrillPart2_2.getWidth()-guiPopupPart2_1.getWidth())/2);
	guiDrillPart2_2.setY(guiPopupPart2_1.getY()+guiPopupPart2_1.getHeight());
	guiDrillPart2_2.setRotation(180);
	guiGrinderPart2_3.setX(guiPopupPart2_2.getX()-(guiGrinderPart2_3.getWidth()-guiPopupPart2_2.getWidth())/2);
	guiGrinderPart2_3.setY(guiPopupPart2_2.getY()-guiGrinderPart2_3.getHeight());
	guiPaintPart2_4.setX(guiPopupPart2_2.getX()-(guiPaintPart2_4.getWidth()-guiPopupPart2_2.getWidth())/2);
	guiPaintPart2_4.setY(guiPopupPart2_2.getY()+guiPopupPart2_2.getHeight());
	guiPaintPart2_4.setRotation(180);

	guiOperatorPart2_1.setX(guiCrossSeamerPart2_1.getX() - guiOperatorPart2_1.getWidth());
	guiOperatorPart2_1.setY(guiCrossSeamerPart2_1.getY() + guiCrossSeamerPart2_1.getHeight()/2);
	guiOperatorPart2_2.setX(guiDrillPart2_2.getX() - guiOperatorPart2_2.getWidth());
	guiOperatorPart2_2.setY(guiDrillPart2_2.getY() + guiDrillPart2_2.getHeight()/2 - guiOperatorPart2_2.getHeight());
	guiOperatorPart2_3.setX(guiGrinderPart2_3.getX() - guiOperatorPart2_3.getWidth());
	guiOperatorPart2_3.setY(guiGrinderPart2_3.getY() + guiGrinderPart2_3.getHeight()/2);
	guiOperatorPart2_4.setX(guiPaintPart2_4.getX() - guiOperatorPart2_4.getWidth());
	guiOperatorPart2_4.setY(guiPaintPart2_4.getY() + guiPaintPart2_4.getHeight()/2 - guiOperatorPart2_4.getHeight());

	guiTruckPart2_1.setX(guiOperatorPart2_1.getX() - guiTruckPart2_1.getWidth());
	guiTruckPart2_1.setY(guiOperatorPart2_1.getY() - guiTruckPart2_1.getHeight());
	guiTruckPart2_2.setX(guiOperatorPart2_2.getX() - guiTruckPart2_2.getWidth());
	guiTruckPart2_2.setY(guiOperatorPart2_2.getY() + guiOperatorPart2_2.getHeight());
	guiTruckPart2_2.setRotation(180);
	guiTruckPart2_3.setX(guiOperatorPart2_3.getX() - guiTruckPart2_3.getWidth());
	guiTruckPart2_3.setY(guiOperatorPart2_3.getY() - guiTruckPart2_3.getHeight());
	guiTruckPart2_4.setX(guiOperatorPart2_4.getX() - guiTruckPart2_4.getWidth());
	guiTruckPart2_4.setY(guiOperatorPart2_4.getY() + guiOperatorPart2_4.getHeight());
	guiTruckPart2_4.setRotation(180);
	//END PART 2
	/*
	 *[[=====================================================]]
	 *[[
	 *[[	Part 2 ENDS Here 
	 *[[
	 *[[=====================================================]]
	 */
	//PART 3

	//Part 3

	/*
	 *[[=====================================================]]
	 *[[
	 *[[	Part 3 BEGINS Here 
	 *[[
	 *[[=====================================================]]
	 */

	part3_X = guiTransferPart2To3.getX();//1000;
	part3_Y = guiTransferPart2To3.getY() + guiTransferPart2To3.getHeight();//25;

	/**
	 * Initialization
	 */
	//Conveyor Initialization
	conveyor_3a = new ConveyorMediator("Conveyor - 3A");
	conveyor_3b = new ConveyorMediator("Conveyor - 3B");
	conveyor_3c = new ConveyorMediator("Conveyor - 3C");

	conveyor_3a.gui.setup(part3_X, part3_Y, 200, FactoryPart.SOUTH);
	conveyor_3b.gui.setup(part3_X, part3_Y + 296, 200, FactoryPart.SOUTH);
	conveyor_3c.gui.setup(part3_X, part3_Y + 593, 200, FactoryPart.SOUTH);

	//Washer Initialization
	washer_Agent = new InlineMachineAgent("Washer");
	washer_Controller = new InlineMachineController();
	washer_Gui = new GuiWasher(part3_X + 11, part3_Y + 164,270);

	washer_Agent.setController(washer_Controller);
	washer_Agent.setJob("Washing");
	washer_Gui.setController(washer_Controller);

	washer_Controller.setAgent(washer_Agent);
	washer_Controller.setGui(washer_Gui);

	uvLamp_Agent = new InlineMachineAgent("UV Lamp");
	uvLamp_Controller = new InlineMachineController();
	uvLamp_Gui = new GuiUVLamp(part3_X + 11,part3_Y + 485,90);

	uvLamp_Agent.setController(uvLamp_Controller);
	uvLamp_Agent.setJob("UV");
	uvLamp_Gui.setController(uvLamp_Controller);

	uvLamp_Controller.setAgent(uvLamp_Agent);
	uvLamp_Controller.setGui(uvLamp_Gui);

	//Transfer 3 Initialization
	// transfer_3_Agent = new TransferAgent("Transfer3Agent");
	// transfer_3_Gui = new GuiTransfer(241,474,180);
	// transfer_3_Controller = new TransferController(transfer_3_Agent, transfer_3_Gui);
	tmPart3 = new TransferMediator("TransferMediator3");

	tmPart3.gui.setup(part3_X,part3_Y + 780,180);


	/**
	 * Add stuff to GuiList so Jay can do something
	 */
	guiList.add(conveyor_3a.gui);
	guiList.add(washer_Gui);
	guiList.add(conveyor_3b.gui);
	guiList.add(uvLamp_Gui);
	guiList.add(conveyor_3c.gui);
	guiList.add(tmPart3.gui);

	/**
	 * Set Dependencies
	 */
	//Conveyor Dependencies

	/*
	 * 	CONVEYOR 3A
	 * 	Transfer 2 -> Conveyor 3A -> Washer
	 * 
	 */
	conveyor_3a.setSource(transferPart2To3, guiTransferPart2To3, FactoryPart.NORTH);
	conveyor_3a.setDestination(washer_Agent, washer_Gui, FactoryPart.SOUTH);

	conveyor_3a.gui.setPart(washer_Gui, FactoryPart.SOUTH);

	/*
	 *  CONVEYOR 3B
	 * 	Washer -> Conveyor 3B -> UVLamp
	 * 
	 */
	conveyor_3b.setSource(washer_Agent, washer_Gui, FactoryPart.NORTH);
	conveyor_3b.setDestination(uvLamp_Agent, uvLamp_Gui, FactoryPart.SOUTH);

	conveyor_3b.gui.setPart(washer_Gui, FactoryPart.NORTH);
	conveyor_3b.gui.setPart(uvLamp_Gui, FactoryPart.SOUTH);

	/*
	 *  CONVEYOR 3C
	 *  UVLamp -> Conveyor 3C -> Transfer 3
	 * 
	 */
	conveyor_3c.setSource(uvLamp_Agent, uvLamp_Gui, FactoryPart.NORTH);
	conveyor_3c.setDestination(tmPart3.agent, tmPart3.gui, FactoryPart.SOUTH);

	conveyor_3c.gui.setPart(uvLamp_Gui, FactoryPart.NORTH);
	conveyor_3c.gui.setPart(tmPart3.gui, FactoryPart.SOUTH);

	//Washer Dependencies
	washer_Agent.setConveyor(conveyor_3a.agent, conveyor_3b.agent);

	washer_Gui.setPart(conveyor_3a.gui, FactoryPart.NORTH);
	washer_Gui.setPart(conveyor_3b.gui, FactoryPart.SOUTH);

	//System.out.println("GuiConveyor 1: " + conveyor_3a.gui);
	//System.out.println("GuiConveyor 2: " + conveyor_3b.gui);

	//UVLamp Dependencies
	uvLamp_Agent.setConveyor(conveyor_3b.agent, conveyor_3c.agent);

	uvLamp_Gui.setPart(conveyor_3b.gui, FactoryPart.NORTH);
	uvLamp_Gui.setPart(conveyor_3c.gui, FactoryPart.SOUTH);


	// Transfer Dependencies
	tmPart3.setSource(conveyor_3c.agent, conveyor_3c.gui, FactoryPart.NORTH);
	tmPart3.setDestination(cmPart4_1.agent,cmPart4_1.gui, FactoryPart.WEST);

	//RHYS-ManualBreakout
	//transfer_1_agent.setSource(conveyor_1c.agent);
	//transfer_1_agent.setDestination(cmPart2_1.agent);
	//transfer_1_gui.setPart(conveyor_1c.gui, FactoryPart.SOUTH);
	//transfer_1_gui.setPart(cmPart2_1.gui, FactoryPart.EAST);
	
	transfer_1_agent.setSource(conveyor_1c.agent);
	transfer_1_agent.setDestination(cmPart2_5.agent);
	transfer_1_gui.setPart(conveyor_1c.gui, FactoryPart.SOUTH);
	transfer_1_gui.setPart(cmPart2_5.gui, FactoryPart.EAST);

	transferPart2To3.setSource(cmPart2_3.agent);
	guiTransferPart2To3.setPart(cmPart2_3.gui, FactoryPart.WEST);
	transferPart2To3.setDestination(conveyor_3a.agent);
	guiTransferPart2To3.setPart(conveyor_3a.gui, FactoryPart.SOUTH);

	/**
	 * START AGENTS
	 */
	conveyor_3a.agent.startThread();
	conveyor_3b.agent.startThread();
	conveyor_3c.agent.startThread();
	//conveyor_3b.agent.debug = true;
	washer_Agent.startThread();
	uvLamp_Agent.startThread();
	tmPart3.agent.startThread();

	/*
	 *[[=====================================================]]
	 *[[
	 *[[	Part 3 ENDS Here 
	 *[[
	 *[[=====================================================]]
	 */

	/**
	 * XXX
	 * [PART4]
	 */
	//PART 4

	cmPart4_1.gui.setup(tmPart3.gui.getX() - 200, tmPart3.gui.getY(), 200, FactoryPart.WEST);
	//TODO XXX
	cmPart4_1.setSource(tmPart3.agent, tmPart3.gui, FactoryPart.EAST);

	// x,y, rotation
	ovenPart4 = new InlineMachineAgent("Oven");
	ovenControllerPart4 = new InlineMachineController();
	ovenGuiPart4 = new GuiOven(0,0 ,0);
	ovenGuiPart4.setX(cmPart4_1.gui.getX() - ovenGuiPart4.getWidth());
	ovenGuiPart4.setY(cmPart4_1.gui.getY()-25);
	ovenPart4.setConveyor(cmPart4_1.agent, cmPart4_2.agent);
	ovenPart4.setJob("Baking");
	ovenPart4.setController(ovenControllerPart4);
	ovenGuiPart4.setController(ovenControllerPart4);
	ovenControllerPart4.setAgent(ovenPart4);
	ovenControllerPart4.setGui(ovenGuiPart4);
	ovenGuiPart4.setPart(cmPart4_1.gui, FactoryPart.EAST);
	ovenGuiPart4.setPart(cmPart4_2.gui, FactoryPart.WEST);

	cmPart4_1.setDestination(ovenPart4, ovenGuiPart4, FactoryPart.WEST);
	cmPart4_2.setSource(ovenPart4, ovenGuiPart4, FactoryPart.EAST);


	cmPart4_2.gui.setup(ovenGuiPart4.getX()-ovenGuiPart4.getWidth()/2-10, ovenGuiPart4.getY()+25, 200, FactoryPart.WEST);

	truckPart4 = new TruckAgent("Truck");
	truckGuiPart4 = new GuiTruck(cmPart4_2.gui.getX()-200, cmPart4_2.gui.getY(), 180);
	truckControllerPart4 = new TruckController(truckPart4,truckGuiPart4);

	truckGuiPart4.setController(truckControllerPart4);

	truckPart4.setController(truckControllerPart4);

	glassRobotPart4 = new GlassRobotAgent("End");
	glassRobotGuiPart4 = new GuiGlassRobot();
	glassRobotControllerPart4 = new GlassRobotController(glassRobotPart4, glassRobotGuiPart4);
	glassRobotPart4.setController(glassRobotControllerPart4);
	glassRobotGuiPart4.setController(glassRobotControllerPart4);
	glassRobotGuiPart4.setX(cmPart4_2.gui.getX() - truckGuiPart4.getWidth());//-cmPart4_2.gui.getWidth());
	glassRobotGuiPart4.setY(cmPart4_2.gui.getY()- 200);
	glassRobotGuiPart4.setTruck(truckGuiPart4);
	glassRobotGuiPart4.setConveyor(cmPart4_2.gui);
	glassRobotPart4.setConveyor(cmPart4_2.agent);
	glassRobotPart4.setTruck(truckPart4);
	glassRobotGuiPart4.setConveyor(cmPart4_2.gui);

	//setconveyor hack
	//glassRobotGuiPart4.setConveyorCoordinates(true);

	glassRobotGuiPart4.setPart(truckGuiPart4, FactoryPart.SOUTH);
	truckGuiPart4.setPart(glassRobotGuiPart4, FactoryPart.NORTH);
	truckGuiPart4.setPart(glassRobotGuiPart4, FactoryPart.EAST);

	//XXX
	cmPart4_2.setDestination(glassRobotPart4, glassRobotGuiPart4, FactoryPart.WEST);

	glassRobotPart4.enable_debug = true;

	guiList.add(cmPart4_1.gui);
	guiList.add(ovenGuiPart4);
	guiList.add(cmPart4_2.gui);
	guiList.add(truckGuiPart4);
	guiList.add(glassRobotGuiPart4);

	// XXX 201 
	cmPart4_1.agent.startThread();
	cmPart4_2.agent.startThread();
	ovenPart4.startThread();

	truckPart4.startThread();
	glassRobotPart4.startThread();

	//END PART 4
	
	// Miscellaneous stuff
	// Trash cans
	GuiTrash trash1 = new GuiTrash(), trash2 = new GuiTrash();
	trash1.setX(conveyor_1a.gui.getX() + GuiConveyor.THICKNESS);
	trash1.setY(conveyor_1a.gui.getY() + conveyor_1a.gui.getLength() - trash1.getHeight() - 30);
	trash2.setX(cmPart4_2.gui.getX() + 30);
	trash2.setY(cmPart4_2.gui.getY() - trash2.getHeight());
	guiList.add(trash1);
	guiList.add(trash2);
	getStartRobot().setTrashX(trash1.getCenterX());
	getStartRobot().setTrashY(trash1.getCenterY());
	getEndRobot().setTrashX(trash2.getCenterX());
	getEndRobot().setTrashY(trash2.getCenterY());

	// ADD STUFF TO disasterList
	disasterList = new ArrayList<Disaster>();
	//disasterList.add(glass_robot_agent);
	disasterList.add(nc_cutter_agent);
	disasterList.add(breakout_agent);
	disasterList.add(transfer_1_agent);
	disasterList.add(conveyor_1a.agent);
	disasterList.add(conveyor_1b.agent);
	disasterList.add(conveyor_1c.agent);
	disasterList.add(cmPart2_1.agent);
	disasterList.add(cmPart2_2.agent);
	disasterList.add(cmPart2_3.agent);
	//disasterList.add(popupPart2_1);
	//disasterList.add(popupPart2_2);
	//disasterList.add(operatorPart2_1);
	//disasterList.add(operatorPart2_2);
	//disasterList.add(operatorPart2_3);
	//disasterList.add(operatorPart2_4);
	//disasterList.add(truckPart2_1);
	//disasterList.add(truckPart2_2);
	//disasterList.add(truckPart2_4);
	//disasterList.add(truckPart2_3);
	disasterList.add(crossSeamerPart2_1);
	disasterList.add(drillPart2_2);
	disasterList.add(grinderPart2_3);
	disasterList.add(paintPart2_4);
	//disasterList.add(truckPart2_6);
	//disasterList.add(manualBreakoutAgent);
	disasterList.add(cmPart2_5.agent);
	//disasterList.add(operatorPart2_6);
	disasterList.add(grinderPart2_6);
	//disasterList.add(popupPart2_4);
	disasterList.add(transferPart2To3);
	disasterList.add(conveyor_3a.agent);
	disasterList.add(conveyor_3b.agent);
	disasterList.add(conveyor_3c.agent);
	disasterList.add(washer_Agent);
	disasterList.add(uvLamp_Agent);
	disasterList.add(tmPart3.agent);
	disasterList.add(cmPart4_1.agent);
	disasterList.add(cmPart4_2.agent);
	disasterList.add(ovenPart4);
	//disasterList.add(truckPart4);
	//disasterList.add(glassRobotPart4);
	timer = new Timer(20, this);

	setVisible(true);

    }

    public void startTimer() {
	timer.start();
    }

    public void setSpeed(int s){
	System.out.println(SPEED);
	SPEED = s;
    }
    int i =0;
    public void createGlass()
    {
	/** Create GuiGlass **/
	GuiGlass g = new GuiGlass(0,0,0);
	bin.stockBin(g);

	//Set the glass here
	g.setXCoord(bin.getX() + ( (bin.getWidth() - g.getWidth())/2) );
	g.setYCoord(bin.getY() + ( (bin.getHeight() - g.getHeight())/2) );

	//test without robot

	g.update();

	glassList.add(g);


	/*
	 * *** Create Glass Order ***
	 */
	//TODO
	//Needs code to prevent user from spam clicking the button
	GlassOrder glass_order = new GlassOrder("Glass #" + i++);
	glass_robot_agent.msgNewGlassOrder(glass_order);

    }

    //int i=0;
    public void actionPerformed(ActionEvent ae) {
	// Disable/enable transfer buttons
	for (int i = 1; i <= 3; i++ ) {
	    if (getTransfer(i).hasGlass())
		app.controlPanel.getOperationPanel().disableTransferButton(i);
	    else
		app.controlPanel.getOperationPanel().enableTransferButton(i);
	}

	// Earthquake animation
	if (earthquakeCounter-- > 0) {
	    int ddx = (int)(Math.random() * 61) - 30;
	    int ddy = (int)(Math.random() * 61) - 30;
	    scrollScreen(ddx, ddy);
	}
	else {
	    app.controlPanel.getInfoPanel().earthquakeDone();
	}
	
	if(spinCounter-- > 0){
		spinAngle += 12;
		//Normalize angle
		if(spinAngle >= 360){
			spinAngle -= 360;
		}
		
		spinScreen(spinAngle);
	}
	else{
		app.controlPanel.getInfoPanel().spinDone();
	}

	if (ae.getSource() == timer && start == true) {
		//XXX
	    for(int i = 0; i < 4; i++){

		//PART 1 ACTIONS

		/*
		 *[[=====================================================]]
		 *[[
		 *[[	Part 1 BEGINS Here 
		 *[[
		 *[[=====================================================]]
		 */
		//System.out.println(timer.getDelay());

		//Bins
		bin.update();
		bin2.update();

		//Glass Robot
		glass_robot_gui.update();

		//Conveyor 1a
		conveyor_1a.gui.update();

		//NC_Cutter
		nc_cutter_gui.update();

		//Conveyor 1b
		conveyor_1b.gui.update();

		//Breakout
		breakout_gui.update();

		//Conveyor 1c
		conveyor_1c.gui.update();

		//Transfer
		transfer_1_gui.update();

		for(AnimatedPart gui:animatedList) {
		    gui.update();
		}

		//Conveyor
		conveyor_3a.gui.update();
		conveyor_3b.gui.update();
		conveyor_3c.gui.update();

		//Washer
		washer_Gui.update();

		//UVLamp
		uvLamp_Gui.update();

		//Transfer
		tmPart3.gui.update();


		//Part 4
		/**
		 * PART4
		 * XXX
		 */
		glassRobotGuiPart4.update();
		cmPart4_1.gui.update();
		cmPart4_2.gui.update();
		ovenGuiPart4.update();
		truckGuiPart4.update();


		for (GuiGlass gl : glassList) {
		    //gl.draw((Graphics2D)g, this);
		    gl.update();

		}

		//END PART 4
		repaint();

	    }

	}

	if (refreshCounter >= 5) {
	    refreshGlassList();
	    refreshCounter = 0;
	}
	else {
	    refreshCounter++;
	}

    }

    // Go through the list of glasses every second
    // and remove all the finished ones.
    public void refreshGlassList() {
	for (int i = glassList.size() - 1; i >= 0; i--) {
	    if (glassList.get(i).getTreatmentCompleted()) {
		glassList.remove(i);
		numGlasses++;
	    }
	}
	app.controlPanel.getInfoPanel().setNumGlassesRemoved(numGlasses);
    }

    public void paint(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;

	//This is the black background
	g2.setColor(new Color(0,0,0));
	g2.fillRect(0, 0, this.getWidth(), this.getHeight());
	
	g2.setTransform(tx2);

	//This is the factory floor background
	g2.drawImage(floorImage.getImage(), 0, 0, 2200, 1650, null);

	for(FactoryPart gui:guiList) {
	    gui.drawUnderGlass(g2, this);
	}

	for (GuiGlass gl : glassList) {
	    gl.draw((Graphics2D)g, this);

	}

	for(FactoryPart gui:guiList) {
	    // Skip trucks to draw trucks last
	    if ( !(gui instanceof GuiTruck) )
		gui.drawOverGlass(g2, this);
	}

	// Now draw trucks
	for(FactoryPart gui:guiList) {
	    // Draw trucks last
	    if ( gui instanceof GuiTruck ) 
		gui.drawOverGlass(g2, this);
	}
    }

    /*
     * [[===========================================]]
     * [[
     * [[	    PANEL STUFF
     * [[
     * [[===========================================]]
     */
    public GlassRobotAgent getStartRobotAgent()
    {
    	return this.glass_robot_agent;
    }
    
    public GuiGlassRobot getStartRobot() {
	return glass_robot_gui;
    }

    public GuiGlassRobot getEndRobot() {
	return glassRobotGuiPart4;
    }

    public ArrayList<FactoryPart> getFactoryParts() {
	return guiList;
    }

    public ArrayList<GuiGlass> getGlassList() {
	return glassList;
    }

    public GuiBin getGuiBin() {
	return bin;
    }

    public GuiNCCutter getCutter() {
	return nc_cutter_gui;
    }

    public GuiBreakout getBreakout() {
	return breakout_gui;
    }	

    public GuiCrossSeamer getCrossSeamer() {
	return guiCrossSeamerPart2_1;
    }

    public GuiDrill getDrill() {
	return guiDrillPart2_2;
    }

    public GuiGrinder getGrinder() {
	return guiGrinderPart2_6;
    }

    public GuiGrinder getGrinder2() {
	return guiGrinderPart2_3;
    }

    public GuiPaint getPaint() {
	return guiPaintPart2_4;
    }

    public GuiWasher getWasher() {
	return washer_Gui;
    }

    public GuiUVLamp getUVLamp() {
	return uvLamp_Gui;
    }

    public GuiOven getOven() {
	return ovenGuiPart4;
    }

    public GuiPopup getPopup(int id) {
	switch (id) {
	    case 1:
		return guiPopupPart2_4;
	    case 2:
		return guiPopupPart2_1;
	    case 3:
		return guiPopupPart2_2;
	    default:
		System.err.println("[GraphicsPanel] Wrong getPopup id");
		return null;
	}
    }
    
    /**
     * Returns the transfer object specified by id
     * @param id the number of transfer object to be returned
     */
    public GuiTransfer getTransfer(int id) {
	switch (id) {
	    case 1:
		return transfer_1_gui;
	    case 2:
		return guiTransferPart2To3;
	    case 3:
		return tmPart3.gui;
	    // This shouldn't happen.
	    default:
		System.err.println("[GraphicsPanel] Wrong getTransfer id");
		return null;
	}
    }

    public void createSpecialGlass() {
	/*
	 * 
	 * *** Create GuiGlass ***
	 * 
	 */
	GuiGlass g = new GuiGlass(0,0,0);
	bin.stockBin(g);


	//Set the glass here
	g.setXCoord(bin.getX() + ( (bin.getWidth() - g.getWidth())/2) );
	g.setYCoord(bin.getY() + ( (bin.getHeight() - g.getHeight())/2) );

	g.update();
	glassList.add(g);
	
	/*
	 * 
	 * *** Create Glass Order ***
	 * 
	 */
	//TODO
	//Needs code to prevent user from spam clicking the button
	GlassOrder glass_order = new GlassOrder("Special Glass");

	glass_order.setGlassTreatmentStatus("CrossSeaming", GlassTreatmentStatus.NOT_NEEDED);
	glass_order.setGlassTreatmentStatus("Drilling", GlassTreatmentStatus.NOT_NEEDED);
	glass_order.setGlassTreatmentStatus("Washing", GlassTreatmentStatus.NOT_NEEDED);
	glass_order.setGlassTreatmentStatus("Painting", GlassTreatmentStatus.NOT_NEEDED);
	glass_order.setGlassTreatmentStatus("UV", GlassTreatmentStatus.NOT_NEEDED);
	glass_order.setGlassTreatmentStatus("Baking", GlassTreatmentStatus.NOT_NEEDED);

	glass_robot_agent.msgNewGlassOrder(glass_order);

	//test without glass robot
	//conveyor_1a.agent.msgHereIsGlass(glass_order);
    }
    
    public void createGlass(GlassOrder newGlassOrder)//, int quantity)
    {
    	   	GuiGlass g = new GuiGlass(0,0,0);
	    	bin.stockBin(g);
	
	    	//Set the glass here
	    	g.setXCoord(bin.getX() + ( (bin.getWidth() - g.getWidth())/2) );
	    	g.setYCoord(bin.getY() + ( (bin.getHeight() - g.getHeight())/2) );
	
	    	g.update();
	
	    	glassList.add(g);
	
	    	glass_robot_agent.msgNewGlassOrder(newGlassOrder);
    }

    /**
     * Updates the affine transformation
     */
    public void updateTransform() {
	tx2.setToIdentity();
	
	//tx2.rotate(spinAngle * (3.14/180));

	
	tx2.scale(scaleX, scaleY);
	tx2.translate(dx / scaleX, dy / scaleY);
	
	//tx2.rotate(spinAngle * (3.14/180), part1_X+500, part1_Y-500);
	


    }
   

    /**
     * Set the scale factor
     */
    public void setScaleFactor(int percentage) {
	scaleX = percentage / 100.0;
	scaleY = percentage / 100.0;
	updateTransform();
    }

    /**
     * Scrolls the screen, given the ending point.
     */
    public void scrollScreen(Point end) {
	dx += (int)(end.getX() - startingPoint.getX());
	dy += (int)(end.getY() - startingPoint.getY());
	startingPoint = end;
	updateTransform();
    }

    /**
     * Scrolls the screen, given dx and dy
     * @param dx x displacement
     * @param dy y displacement
     */
    public void scrollScreen(int dx, int dy) {
	this.dx += dx;
	this.dy += dy;
	updateTransform();
    }
    
    /**
     * Spins the screen
     */
    public void spinScreen(int sA){
    	updateTransform();
    }

    /*** MouseListener stuff ***/

    // Finds out which factory part has been clicked on
    public void mouseClicked(MouseEvent me) {
	int x = (int)((me.getX() - dx) / scaleX), 
		y = (int)((me.getY() - dy) / scaleY);

	for (FactoryPart fp : guiList) {
	    if (fp instanceof GuiConveyor) {
		// If the cursor is on a machine
		if ( fp.getX() < x &&
		     fp.getX() + fp.getWidth() > x &&
		     fp.getY() < y &&
		     fp.getY() + fp.getHeight() > y ) {

		    // Toggle fp
		    // Right click?
		    if ( (me.getModifiers() & InputEvent.BUTTON3_MASK) 
			    == InputEvent.BUTTON3_MASK ) {
			// Don't break the sensor if the conveyor is off
			if ( ((GuiConveyor)fp).isMoving() ) {
				((GuiConveyor)fp).setBreak(!((GuiConveyor)fp).isBroken());
			}
		    }
		    else {
			((GuiConveyor)fp).toggleMoving();
		    }
		}
	    }
	}
    }

    /**
     * Returns the total number of glasses removed from the factory
     */
    public int getNumGlasses() {
	return numGlasses;
    }

    // Mouse stuff
    public void mouseEntered(MouseEvent me) {}
    public void mouseExited(MouseEvent me) {}
    public void mouseReleased(MouseEvent me) {
	startingPoint = me.getPoint();
    }
    public void mousePressed(MouseEvent me) {
	startingPoint = me.getPoint();
    }

    // MouseMotionListener stuff
    public void mouseMoved(MouseEvent me) {};
    public void mouseDragged(MouseEvent me) {
	scrollScreen(me.getPoint());
    }

    /***** DISASTERS *****/
    // EARTHQUAKE SCENARIO 
    public void startEarthquake() {
	earthquakeCounter = 60;

	for (GuiGlass gg : glassList)
	    gg.breakGlass();

	for (Disaster agent : disasterList) {
	    agent.msgBreakAllGlasses();
	}
    }
    public void stopEarthquake() {
    	earthquakeCounter = 0;
    }

    // DEATHSTAR SCENARIO
    public GuiTruck getFinalTruck() {
	return truckGuiPart4;
    }
    
    // SPIN SCENARIO
    public void startSpin(){
    	spinCounter = 30;
    }
    public void stopSpin(){
    	spinCounter = 0;
    	
    	spinAngle = 0;
    }
}
