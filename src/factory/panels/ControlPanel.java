package factory.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import agent.MessageTracePool;

public class ControlPanel extends JPanel {

    JTabbedPane tabs;
    JPanel infoPanel, statusPanel, agentPanel, glassPanel, operationPanel;
    JPanel graphicsPanel;
    DebugPanel debugPanel;
    Application app;

    // XXX
    // Should not be used!!
    public ControlPanel() {
	this(null);
    }

    public ControlPanel(Application mainApp) {
	app = mainApp;
	graphicsPanel = app.factoryPanel.gpanel;

	// Create tabs and panels
	tabs = new JTabbedPane();
	infoPanel = new InfoPanel(app);
	statusPanel = new StatusPanel(app);
	agentPanel = new AgentTracePanel();
	glassPanel = new GlassPanel(app);
	operationPanel = new OperationPanel(app);
	debugPanel = new DebugPanel(app);

	// Add tabs
	tabs.add("Factory Controls", infoPanel);
	tabs.add("Machine Controls", operationPanel);
	tabs.add("Status Panel", statusPanel);
	tabs.add("Agent Panel", agentPanel);
	tabs.add("Glass Panel", glassPanel);
	tabs.add("Debug Panel", debugPanel);
	
	
	MessageTracePool.getInstance();
	MessageTracePool.setTracePanel((AgentTracePanel)agentPanel);

	this.setLayout(new BorderLayout());
	add(tabs, BorderLayout.CENTER);

	// Make the panel opaque
	setOpaque(false);
    }

    // Getter
    public JTabbedPane getTabs() {
	return tabs;
    }

    public InfoPanel getInfoPanel() {
	return (InfoPanel)infoPanel;
    }

    public OperationPanel getOperationPanel() {
	return (OperationPanel)operationPanel;
    }
}
