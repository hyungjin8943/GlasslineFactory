package factory.panels;

import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import factory.gui.GuiTruck;
import factory.gui.WavePlayer;

public class InfoPanel extends JPanel implements ActionListener{

    Application app;
    GraphicsPanel graphicsPanel;

    JButton btnGlass;
    JButton btnSpeed1, btnSpeed2, btnSpeed3, btnSpeed4, btnPause, btnStart;
    JButton btnEarthquake, btnNyan, btnDeathStar, btnSpin;

    JPanel glassButtons, glassButtonsContainer, 
	   disasterButtons, disasterButtonsContainer, 
	   speedButtons, speedButtonsContainer;

    JLabel glassNum;

    // For nyan cat sound
    static WavePlayer musicPlayer = new WavePlayer("misc/nyan.wav");
    boolean isMusicPlaying = false;

    // For Earthquake
    boolean isEarthquakeOn = false;
    
    //for spin
    boolean isSpinOn = false;
    boolean spinMusicOn = false;
    static WavePlayer spinPlayer = new WavePlayer("misc/spin.wav");

    public InfoPanel(Application mainApp) 
    {   
	app = mainApp; 
	graphicsPanel = app.factoryPanel.gpanel;
	glassNum = new JLabel("Total glasses removed: 0");

	// Create panels
	glassButtons = new JPanel();
	glassButtonsContainer = new JPanel();
	disasterButtons = new JPanel();
	disasterButtonsContainer = new JPanel();
	speedButtons = new JPanel();
	speedButtonsContainer = new JPanel();

	glassButtons.setLayout(new GridLayout(3, 0));
	glassButtonsContainer.setLayout(new BoxLayout(glassButtonsContainer, BoxLayout.LINE_AXIS));
	glassButtonsContainer.setBorder(BorderFactory.createCompoundBorder(
		    BorderFactory.createTitledBorder("Glass Status"),
		    BorderFactory.createEmptyBorder(5, 5, 5, 5)) );
	//glassButtonsContainer.setMinimumSize(new Dimension(150, 150));
	//glassButtonsContainer.setPreferredSize(new Dimension(150, 150));
	//glassButtonsContainer.setMaximumSize(new Dimension(150, 150));

	disasterButtons.setLayout(new GridLayout(3, 0));
	disasterButtonsContainer.setLayout(new BoxLayout(disasterButtonsContainer, BoxLayout.LINE_AXIS));
	disasterButtonsContainer.setBorder(BorderFactory.createCompoundBorder(
		    BorderFactory.createTitledBorder("Disasters"),
		    BorderFactory.createEmptyBorder(5, 5, 5, 5)) );
	//disasterButtonsContainer.setMinimumSize(new Dimension(150, 150));
	//disasterButtonsContainer.setPreferredSize(new Dimension(150, 150));
	//disasterButtonsContainer.setMaximumSize(new Dimension(150, 150));

	speedButtons.setLayout(new GridLayout(3, 0));
	speedButtonsContainer.setLayout(new BoxLayout(speedButtonsContainer, BoxLayout.LINE_AXIS));
	speedButtonsContainer.setBorder(BorderFactory.createCompoundBorder(
		    BorderFactory.createTitledBorder("Speed Controls"),
		    BorderFactory.createEmptyBorder(5, 5, 5, 5)) );
	//speedButtonsContainer.setMinimumSize(new Dimension(250, 150));
	//speedButtonsContainer.setPreferredSize(new Dimension(250, 150));
	//speedButtonsContainer.setMaximumSize(new Dimension(250, 150));

	this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

	// Create and add buttons
	btnGlass = new JButton("Create Glass");

	btnSpeed1 = new JButton("Speed: 1");
	btnSpeed2 = new JButton("Speed: 2");
	btnSpeed3 = new JButton("Speed: 3");
	btnSpeed4 = new JButton("Speed: 4");
	btnPause = new JButton("Pause");
	btnStart = new JButton("Start");

	btnEarthquake = new JButton("Earthquake");
	btnNyan = new JButton("nyan :3");
	btnDeathStar = new JButton("Deathstar");
	btnSpin = new JButton("Spin!");

	btnGlass.addActionListener(this);

	btnSpeed1.addActionListener(this);
	btnSpeed2.addActionListener(this);
	btnSpeed3.addActionListener(this);
	btnSpeed4.addActionListener(this);
	btnPause.addActionListener(this);
	btnStart.addActionListener(this);

	btnEarthquake.addActionListener(this);
	btnNyan.addActionListener(this);
	btnDeathStar.addActionListener(this);
	btnSpin.addActionListener(this);

	glassButtons.add(btnGlass);
	glassButtons.add(glassNum);
	glassButtonsContainer.add(glassButtons);

	speedButtons.add(btnSpeed1);
	speedButtons.add(btnSpeed2);
	speedButtons.add(btnSpeed3);
	speedButtons.add(btnSpeed4);
	//speedButtons.add(btnPause);
	//speedButtons.add(btnStart);
	speedButtonsContainer.add(speedButtons);

	disasterButtons.add(btnEarthquake);
	disasterButtons.add(btnNyan);
	disasterButtons.add(btnDeathStar);
	//disasterButtons.add(btnSpin);
	disasterButtonsContainer.add(disasterButtons);

	this.add(glassButtonsContainer);
	this.add(speedButtonsContainer);
	this.add(disasterButtonsContainer);
	setVisible(true);
    }

    /**
     * Sets the number of glasses removed (for displaying purpose)
     * @param num number of glasses removed so far
     */
    public void setNumGlassesRemoved(int num) {
	glassNum.setText("Total glasses removed: " + num);
    }

    public void actionPerformed(ActionEvent ae) {

	if (ae.getSource() == btnGlass) {
	    //graphicsPanel.createGlass();
	    app.controlPanel.getTabs().setSelectedIndex(4);
	}

	if(ae.getSource() == btnSpeed1){
	    graphicsPanel.setSpeed(1);
	}

	if(ae.getSource() == btnSpeed2){
	    graphicsPanel.setSpeed(2);
	}

	if(ae.getSource() == btnSpeed3){
	    graphicsPanel.setSpeed(3);
	}
	if(ae.getSource() == btnSpeed4){
	    graphicsPanel.setSpeed(4);
	}
	if(ae.getSource() == btnStart){
	    graphicsPanel.start = true;
	}
	if(ae.getSource() == btnPause){
	    graphicsPanel.start = false;
	} 	
	if(ae.getSource() == btnEarthquake) {
	    if (isEarthquakeOn) {
		graphicsPanel.stopEarthquake();
		btnEarthquake.setText("Earthquake");
	    }
	    else {
		graphicsPanel.startEarthquake();
		btnEarthquake.setText("Stop Earthquake");
	    }
	    isEarthquakeOn = !isEarthquakeOn;
	}
	if(ae.getSource() == btnNyan) {
	    if (isMusicPlaying) {
		btnNyan.setText("nyan :3");
		musicPlayer.stop();
		app.stopNyanCat();
	    }
	    else {
		btnNyan.setText("NYAN NYAN!!");
		musicPlayer.play();
		app.startNyanCat();
	    }
	    isMusicPlaying = !isMusicPlaying;
	}
	if (ae.getSource() == btnDeathStar) {
	    graphicsPanel.getFinalTruck().setBreak(true);
	}

	/*
	if(ae.getSource() == btnSpin){
		if(isSpinOn){
			//btnSpin.setText("Spin!");
			//graphicsPanel.stopSpin();
			//spinPlayer.stop();
		}
		else{
			btnSpin.setText("YOU SPIN");
			graphicsPanel.startSpin();
			spinPlayer.play();
			spinMusicOn = true;
			isSpinOn = !isSpinOn;
		}

	}
	*/
    }

    public void earthquakeDone() {
	isEarthquakeOn = false;
	btnEarthquake.setText("Earthquake");
    }

    public void spinDone(){
    	isSpinOn = false;
    	btnSpin.setText("Spin!");
    	
    	if(spinMusicOn){
    		spinPlayer.stop();
    	}
    	spinMusicOn = false;
    }
}

