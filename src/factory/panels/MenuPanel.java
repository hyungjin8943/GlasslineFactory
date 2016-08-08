package factory.panels;

//import java.util.*;
//import java.awt.*;
import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.*;

//import factory.agent.ConveyorAgent;
//import factory.agent.GlassOrder;
//import factory.agent.OperatorAgent;
//import factory.agent.PopupAgent;
//import factory.controller.ConveyorController;
//import factory.controller.PopupController;
//import factory.mock.MockInteractorG;
public class MenuPanel extends JPanel implements ActionListener{

	JButton btnLower, btnRaise, btnGlass, btnPass;
	JButton btnSpeed1, btnSpeed2, btnSpeed3, btnSpeed4;
	JButton btnPause, btnStart;
	JButton btnSpecialGlass;
	JPanel menuPanel = new JPanel();
	GraphicsPanel graphicsPanel;
	public MenuPanel(GraphicsPanel gpanel) 
	{   
		graphicsPanel = gpanel;
		
		menuPanel.setLayout(new GridLayout(0,1));
		
		btnGlass = new JButton("Create Glass");
		btnSpecialGlass = new JButton("Special Glass");
		btnSpeed1 = new JButton("Speed: 1");
		btnSpeed2 = new JButton("Speed: 2");
		btnSpeed3 = new JButton("Speed: 3");
		btnSpeed4 = new JButton("Speed: 4");
		btnPause = new JButton("Pause");
		btnStart = new JButton("Start");
	
		btnGlass.addActionListener(this);
		btnSpeed1.addActionListener(this);
		btnSpeed2.addActionListener(this);
		btnSpeed3.addActionListener(this);
		btnSpeed4.addActionListener(this);
		btnPause.addActionListener(this);
		btnStart.addActionListener(this);
		btnSpecialGlass.addActionListener(this);

	
		menuPanel.add(btnGlass);
		menuPanel.add(btnSpecialGlass);
		menuPanel.add(btnSpeed1);
		menuPanel.add(btnSpeed2);
		menuPanel.add(btnSpeed3);
		menuPanel.add(btnSpeed4);
		menuPanel.add(btnPause);
		menuPanel.add(btnStart);
		
        add(menuPanel);
        setVisible(true);
	}
	public void actionPerformed(ActionEvent ae) {
		
    	 if (ae.getSource() == btnGlass) {
    	    graphicsPanel.createGlass();
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
     	 if(ae.getSource() == btnSpecialGlass) {
     		 graphicsPanel.createSpecialGlass();
     	 }
    	    //popup.receiveGlass(g, FactoryPart.WEST);		 
	}
}

