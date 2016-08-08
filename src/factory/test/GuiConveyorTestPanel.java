/**
  * @author Jay Whang
 * GuiConveyorTestPanel object for V1 testing.
 */

package factory.test;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GuiConveyorTestPanel extends JFrame {
    
    // Panels
    ConveyorGraphicsPanel mainPanel;
    ConveyorMenuPanel menuPanel;


    public GuiConveyorTestPanel() {
    	super("GuiConveyor Test Window");
		setLayout(new BorderLayout());
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		// Make panels
		mainPanel = new ConveyorGraphicsPanel();
		menuPanel = new ConveyorMenuPanel(mainPanel);	
		
		mainPanel.setMenuPanel(menuPanel);

		add(mainPanel, BorderLayout.CENTER);
		add(menuPanel, BorderLayout.SOUTH);
	
		//mainPanel.setDoubleBuffered(true);
		setVisible(true);
	
    }

    public static void main(String[] args) {
    	GuiConveyorTestPanel window = new GuiConveyorTestPanel();
    }  
}
