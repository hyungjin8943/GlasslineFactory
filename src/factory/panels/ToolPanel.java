package factory.panels;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ToolPanel extends JPanel implements ActionListener, ChangeListener {

    private Application app;
    private JButton btnMenu, btnPause;
    private JSlider zoomSlider;
    
    public ToolPanel(Application mainApp) {
	app = mainApp;

	// Use left-alignment
	this.setLayout(new FlowLayout(FlowLayout.LEFT));

	// Make this panel transparent
	this.setOpaque(false);

	// Create and add buttons
	//btnMenu = new JButton("<html><center>" + "Show" + "<br>" + "Menu" + "</center></html>");
	btnMenu = new JButton("Menu");
	btnMenu.addActionListener(this);
	btnPause = new JButton("Pause");
	btnPause.addActionListener(this);
	this.add(btnMenu);
	this.add(btnPause);

	// Create slider
	zoomSlider = new JSlider(0, 100, 65);
	zoomSlider.addChangeListener(this);
	zoomSlider.setOpaque(false);
	this.add(zoomSlider);
	// Initial scaling
	app.factoryPanel.gpanel.setScaleFactor(zoomSlider.getValue());

	this.setSize(800, 70);
	this.setVisible(true);
    }

    /**
     * Implements stateChanged for ChangeListener interface
     */
    public void stateChanged(ChangeEvent ce) {
	if (ce.getSource() == zoomSlider) {
	    if (zoomSlider.getValueIsAdjusting()) {
		// XXX fix this accessing issue
		app.factoryPanel.gpanel.setScaleFactor(zoomSlider.getValue());
	    }
	}
    }

    /**
     * Implements actionPerformed for ActionListener interface
     */
    public void actionPerformed(ActionEvent ae) {
	if (ae.getSource() == btnMenu) {
	    app.toggleControls();
	}
    }

}
