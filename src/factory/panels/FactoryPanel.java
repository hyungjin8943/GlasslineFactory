package factory.panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class FactoryPanel extends JPanel {

    public GraphicsPanel gpanel;
    Application app;

    public FactoryPanel(Application mainApp) {
	app = mainApp;
	setLayout(new BorderLayout());
	gpanel = new GraphicsPanel(app);
	add(gpanel,BorderLayout.CENTER);
    }

}
