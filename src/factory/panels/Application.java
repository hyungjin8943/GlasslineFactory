package factory.panels;

import java.awt.GraphicsEnvironment;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class Application extends JFrame implements ComponentListener {

    static ImageIcon imgNyanCat = new ImageIcon("images/nyancat.gif");
    JLabel nyanCatLabel = new JLabel(imgNyanCat);
    public FactoryPanel factoryPanel;
    public ControlPanel controlPanel;
    ToolPanel toolPanel;
    JLayeredPane layeredPane;

    boolean controlWindowVisible;

    public Application() {

	controlWindowVisible = true;

	// Set up JFrame window 
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	// Create layered pane
	layeredPane = new JLayeredPane();

	// Create panels
	factoryPanel = new FactoryPanel(this);
	controlPanel = new ControlPanel(this);
	toolPanel = new ToolPanel(this);
	//this.setSize(factoryPanel.getWidth(), factoryPanel.getHeight());
	this.setSize(1000, 800);

	// Add panels
	layeredPane.add(factoryPanel, JLayeredPane.DEFAULT_LAYER);
	layeredPane.add(controlPanel, JLayeredPane.PALETTE_LAYER);
	layeredPane.add(toolPanel, JLayeredPane.MODAL_LAYER);
	this.add(layeredPane);

	this.addComponentListener(this);

	// Control panel invisible at start
	controlPanel.setVisible(true);
	setVisible(true);

	// Start timer
	factoryPanel.gpanel.startTimer();

	// Maximize the screen
	maximize();

	// Set up the window sizes and positions
	resizeAction();

	/**** NYAN CAT ****/
	nyanCatLabel.setOpaque(false);
	nyanCatLabel.setSize(470, 200);
	nyanCatLabel.setLocation(this.getWidth() / 2 - 470 / 2, this.getHeight() / 2 - 200 / 2);
	nyanCatLabel.setVisible(false);
	layeredPane.add(nyanCatLabel, JLayeredPane.POPUP_LAYER);
    }

    /**
     * Resizes control panel and tool panel accordingly.
     */
    private void resizeAction() {
	// Resize the graphics panel to the main JFrame's size
	factoryPanel.setSize(this.getWidth(), this.getHeight());

	// And relocate/resize control panel accordingly
	controlPanel.setSize(800, 220);
	controlPanel.setLocation(5, this.getHeight() - toolPanel.getHeight() - controlPanel.getHeight());

	// Relocate tool panel
	toolPanel.setLocation(0, this.getHeight() - toolPanel.getHeight() + 5);

	nyanCatLabel.setLocation(this.getWidth() / 2 - 470 / 2, this.getHeight() / 2 - 200 / 2);

	this.validate();
    }

    /**
     * Toggles the visibility of controlPanel
     */
    public void toggleControls() {
	controlWindowVisible = !controlWindowVisible;
	controlPanel.setVisible(controlWindowVisible);
	resizeAction();
    }

    /**
     * Returns true if control window is visible
     * Returns false otherwise.
     */
    public boolean isControlVisible() {
	return controlWindowVisible;
    }

    public static void main (String [] args) {
	new Application();
    }

    // All the ComponentListener stuff
    public void componentHidden(ComponentEvent ce) {};
    public void componentMoved(ComponentEvent ce) {};
    public void componentShown(ComponentEvent ce) {};
    public void componentResized(ComponentEvent ce) {
	resizeAction();
    };

    /**
     * Maximizes this JFrame
     */
    public void maximize() {
	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	this.setMaximizedBounds(env.getMaximumWindowBounds());
	this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public void startNyanCat() {
	nyanCatLabel.setVisible(true);
    }

    public void stopNyanCat() {
	nyanCatLabel.setVisible(false);
    }
}
