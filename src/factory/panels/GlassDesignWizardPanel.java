package factory.panels;
import java.awt.event.ActionEvent;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.Timer;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import factory.panels.GlassDesign.MultipleClosedShapeException;
import factory.panels.GlassDesign.NoClosedShapeException;

public class GlassDesignWizardPanel extends JPanel implements ActionListener, ChangeListener, ItemListener
{
	Timer					t	= new Timer(10, this);
	
	GlassPanel					parentPanel;
	
	GlassDesign				newGlassDesign;
	
	boolean					edittingCut, edittingDrill, edittingPaint, edittingTreatment;
	
	JPanel					infoPanel;
	GlassPreviewPanel			glassPreviewPanel;
	JPanel					optionsPanel;
	GlassGridPanel				cutPixelGridPanel;
	GlassGridPanel				drillPixelGridPanel;
	JPanel						cutEditorPanel;
	JPanel						drillEditorPanel;
	JPanel						paintEditorPanel;
	JTabbedPane						paintTabs;
	GlassGridPanel					paintPixelGridPanel;
	JPanel							paintBrushSettingsPanel;
	JLabel							brushTypeLabel;
	JRadioButton						paintbrushButton;
    JRadioButton						pencilButton;
	JColorChooser						palette;
	JPanel									palettePreviewPanel;
	JPanel						treatmentEditorPanel; 
	JPanel							treatmentCheckBoxPanel;
	JLabel								enterDesignNameLabel;
	JTextField							enterDesignNameField;
	JLabel								selectTreatmentsLabel;
	JCheckBox							cutCheckBox;
	JCheckBox							breakoutCheckBox;
	JCheckBox							crossseamCheckBox;
	JCheckBox							drillCheckBox;
	JCheckBox							grindCheckBox;
	JCheckBox							paintCheckBox;
	JCheckBox							washCheckBox;
	JCheckBox							uvCheckBox;
	JCheckBox							ovenCheckBox;
	JPanel					controlPanel;
	JPanel						buttonPanel;
	
	JLabel					infoText;
	
	JLabel					buttonMessage;
	JButton					quitButton;
	JButton					clearButton;
	JButton					previewButton;
	JButton					continueButton;
	JButton					finishButton;
	
	private	final	int		WINDOWX	= 500;
	private	final	int		WINDOWY	= 600;	
	private	final	int		GRIDW	= 350;
	private	final	int		GRIDH	= 350;
	
	public GlassDesignWizardPanel(GlassPanel instantiator)
	{
		parentPanel			= instantiator;
		
		newGlassDesign		= new GlassDesign();
		
		edittingCut			= true;
		edittingDrill 		= false;
		edittingPaint		= false;
		edittingTreatment	= false;	
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		infoPanel			= new JPanel();
		glassPreviewPanel	= new GlassPreviewPanel(100, 100);
		optionsPanel		= new JPanel();
		cutPixelGridPanel		= new GlassGridPanel("Cut", GRIDW, GRIDH, glassPreviewPanel.getPreviewGlass().getWidth(), glassPreviewPanel.getPreviewGlass().getHeight());
		drillPixelGridPanel		= new GlassGridPanel("Drill", GRIDW, GRIDH, glassPreviewPanel.getPreviewGlass().getWidth(), glassPreviewPanel.getPreviewGlass().getHeight());
		paintPixelGridPanel		= new GlassGridPanel("Paint", GRIDW, GRIDH, glassPreviewPanel.getPreviewGlass().getWidth(), glassPreviewPanel.getPreviewGlass().getHeight());
		cutEditorPanel		= new JPanel();
		drillEditorPanel	= new JPanel();
		paintEditorPanel	= new JPanel();
		paintBrushSettingsPanel	= new JPanel();
		treatmentEditorPanel = new JPanel();
		controlPanel		= new JPanel();
		buttonPanel			= new JPanel();
		
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.LINE_AXIS));
		optionsPanel.setLayout(new CardLayout());
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.LINE_AXIS));
		cutEditorPanel.setLayout(new BoxLayout(cutEditorPanel, BoxLayout.LINE_AXIS));
		drillEditorPanel.setLayout(new BoxLayout(drillEditorPanel, BoxLayout.LINE_AXIS));
		paintBrushSettingsPanel.setLayout(new BoxLayout(paintBrushSettingsPanel, BoxLayout.PAGE_AXIS));
		treatmentEditorPanel.setLayout(new BoxLayout(treatmentEditorPanel, BoxLayout.LINE_AXIS));
		
		// SET UP THE TOP PORTION OF THE WIZARD SCREEN //
		infoText			= new JLabel();
		infoText.setText("<html>Draw the shape to cut out of this piece of glass.<BR>Your shape should be exactly one closed loop.</html>");
		infoText.setMaximumSize(new Dimension(300, 100));
		infoText.setPreferredSize(new Dimension(300, 100));
		infoText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Custom-Cut Glass"), BorderFactory.createEmptyBorder(5,5,5,5)));
		glassPreviewPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Preview"), BorderFactory.createEmptyBorder(5,5,5,5)));
		glassPreviewPanel.setMinimumSize(new Dimension(100, 100));
		glassPreviewPanel.setPreferredSize(new Dimension(100, 100));
		glassPreviewPanel.setMaximumSize(new Dimension(100, 100));
		infoPanel.setMinimumSize(new Dimension(400, 100));
		infoPanel.setPreferredSize(new Dimension(400, 100));
		infoPanel.setMaximumSize(new Dimension(400, 100));
		infoPanel.add(infoText);
		infoPanel.add(glassPreviewPanel);
		
		// SET UP THE EDITOR PORTION OF THE WIZARD SCREEN //
		
		cutEditorPanel.add(Box.createRigidArea(new Dimension(12, 390)));
		cutEditorPanel.add(cutPixelGridPanel);
		cutEditorPanel.add(Box.createRigidArea(new Dimension(12, 390)));
		
		drillEditorPanel.add(Box.createRigidArea(new Dimension(12, 390)));
		drillEditorPanel.add(drillPixelGridPanel);
		drillEditorPanel.add(Box.createRigidArea(new Dimension(12, 390)));
		
		JPanel			brushTypePanel	= new JPanel();
			JLabel			brushTypeLabel	= new JLabel("Brush Type: ");
			JRadioButton	paintbrushButton = new JRadioButton("Paintbrush");
		    paintbrushButton.setActionCommand("Paintbrush");
		    paintbrushButton.setSelected(true);
		    JRadioButton	pencilButton = new JRadioButton("Pencil");
		    pencilButton.setActionCommand("Pencil");
		    pencilButton.setSelected(false);
		    brushTypePanel.setLayout(new BoxLayout(brushTypePanel, BoxLayout.LINE_AXIS));
		    brushTypePanel.add(brushTypeLabel);
		    brushTypePanel.add(paintbrushButton);
		    brushTypePanel.add(pencilButton);
		    ButtonGroup buttonGroup = new ButtonGroup();
		    buttonGroup.add(paintbrushButton);
		    buttonGroup.add(pencilButton);
		    paintbrushButton.addActionListener(this);
		    pencilButton.addActionListener(this);
		    
		    palette	= new JColorChooser();
		  
		    AbstractColorChooserPanel[] defaultChooserPanels = palette.getChooserPanels();
		    palette.removeChooserPanel(defaultChooserPanels[0]);
		    palette.removeChooserPanel(defaultChooserPanels[2]);
		    palette.getSelectionModel().addChangeListener(this);
		    
		    palettePreviewPanel	= new JPanel();
		    palettePreviewPanel.setBackground(Color.red);
		    palettePreviewPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		    
		    palette.setPreviewPanel(palettePreviewPanel);

		paintBrushSettingsPanel.setMinimumSize(new Dimension(350, 350));
		paintBrushSettingsPanel.setPreferredSize(new Dimension(350, 350));
		paintBrushSettingsPanel.setMaximumSize(new Dimension(350, 350));
		paintBrushSettingsPanel.add(brushTypePanel);
		paintBrushSettingsPanel.add(palette);
		
		paintTabs	= new JTabbedPane();
		paintTabs.addTab("Glass Canvas", paintPixelGridPanel);
		paintTabs.addTab("Brush Settings", paintBrushSettingsPanel);
		paintEditorPanel.add(paintTabs);
		
		treatmentCheckBoxPanel	= new JPanel();
		treatmentCheckBoxPanel.setLayout(new BoxLayout(treatmentCheckBoxPanel, BoxLayout.PAGE_AXIS));
		enterDesignNameLabel	= new JLabel("Enter a name for this design:");
		enterDesignNameLabel.setAlignmentX(LEFT_ALIGNMENT);
		enterDesignNameField	= new JTextField();
		enterDesignNameField.setMinimumSize(new Dimension(150, 25));
		enterDesignNameField.setPreferredSize(new Dimension(150, 25));
		enterDesignNameField.setMaximumSize(new Dimension(150, 25));
		enterDesignNameField.setAlignmentX(LEFT_ALIGNMENT);
		
		selectTreatmentsLabel	= new JLabel("Select treatments:");
		cutCheckBox				= new JCheckBox("Cut");
		breakoutCheckBox		= new JCheckBox("Breakout");
		drillCheckBox			= new JCheckBox("Drill");
		crossseamCheckBox		= new JCheckBox("Cross-Seam");
		grindCheckBox			= new JCheckBox("Grind");
		paintCheckBox			= new JCheckBox("Paint");
		washCheckBox			= new JCheckBox("Wash");
		uvCheckBox				= new JCheckBox("UV Treat");
		ovenCheckBox			= new JCheckBox("Bake");
		cutCheckBox.setEnabled(false);
		breakoutCheckBox.setEnabled(false);
		drillCheckBox.setEnabled(false);
		paintCheckBox.setEnabled(false);
		treatmentCheckBoxPanel.add(enterDesignNameLabel);
		treatmentCheckBoxPanel.add(enterDesignNameField);
		treatmentCheckBoxPanel.add(Box.createRigidArea(new Dimension(150, 25)));
		treatmentCheckBoxPanel.add(selectTreatmentsLabel);
		treatmentCheckBoxPanel.add(cutCheckBox);
		treatmentCheckBoxPanel.add(breakoutCheckBox);
		treatmentCheckBoxPanel.add(drillCheckBox);
		treatmentCheckBoxPanel.add(crossseamCheckBox);
		treatmentCheckBoxPanel.add(grindCheckBox);
		treatmentCheckBoxPanel.add(uvCheckBox);
		treatmentCheckBoxPanel.add(paintCheckBox);
		treatmentCheckBoxPanel.add(washCheckBox);
		treatmentCheckBoxPanel.add(ovenCheckBox);
		cutCheckBox.addItemListener(this);
		breakoutCheckBox.addItemListener(this);
		drillCheckBox.addItemListener(this);
		crossseamCheckBox.addItemListener(this);
		grindCheckBox.addItemListener(this);
		paintCheckBox.addItemListener(this);
		washCheckBox.addItemListener(this);
		uvCheckBox.addItemListener(this);
		ovenCheckBox.addItemListener(this);
		treatmentEditorPanel.add(Box.createRigidArea(new Dimension(50, 300)));
		treatmentEditorPanel.add(treatmentCheckBoxPanel);
		treatmentEditorPanel.add(Box.createRigidArea(new Dimension(125, 300)));
		
		//pixelGridPanel.setMinimumSize(new Dimension(GRIDW, GRIDH));
		//pixelGridPanel.setPreferredSize(new Dimension(GRIDW, GRIDH));
		//pixelGridPanel.setMaximumSize(new Dimension(GRIDW, GRIDH));
		//pixelGridPanel.setAlignmentX(CENTER_ALIGNMENT);
		optionsPanel.setMinimumSize(new Dimension(400, 425));
		optionsPanel.setPreferredSize(new Dimension(400, 425));
		optionsPanel.setMaximumSize(new Dimension(400, 425));		
		optionsPanel.add(cutEditorPanel, "CUT EDITOR PANEL");
		optionsPanel.add(drillEditorPanel, "DRILL EDITOR PANEL");
		optionsPanel.add(paintEditorPanel, "PAINT EDITOR PANEL");
		optionsPanel.add(treatmentEditorPanel, "TREATMENT EDITOR PANEL");
		optionsPanel.setAlignmentX(CENTER_ALIGNMENT);
		optionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Glass Editor"), BorderFactory.createEmptyBorder(5,5,5,5)));
		
		CardLayout c = (CardLayout)(optionsPanel.getLayout());
		c.show(optionsPanel, "CUT EDITOR PANEL");
		
				
		//buttonMessage		= new JLabel();
		//buttonMessage.setText("Click 'Next' when you are finished.");
		//controlPanel.add(buttonMessage, BorderLayout.NORTH);
		
		
		quitButton			= new JButton("Quit");
		clearButton			= new JButton("Clear");
		previewButton		= new JButton("Preview");
		continueButton		= new JButton("Continue");
		finishButton		= new JButton("Finish");
		quitButton.addActionListener(this);
		clearButton.addActionListener(this);
		previewButton.addActionListener(this);
		continueButton.addActionListener(this);
		finishButton.addActionListener(this);
		controlPanel.add(Box.createHorizontalGlue());
		controlPanel.add(quitButton);
		controlPanel.add(Box.createRigidArea(new Dimension(10, 50)));
		controlPanel.add(clearButton);
		controlPanel.add(Box.createRigidArea(new Dimension(10, 50)));
		controlPanel.add(previewButton);
		controlPanel.add(Box.createRigidArea(new Dimension(10, 50)));
		controlPanel.add(continueButton);
		controlPanel.add(Box.createHorizontalGlue());
		//controlPanel.add(buttonPanel);
		controlPanel.setMinimumSize(new Dimension(400, 50));
		controlPanel.setPreferredSize(new Dimension(400, 50));
		controlPanel.setMaximumSize(new Dimension(400, 50));
		//controlPanel.setBorder(BorderFactory.createEtchedBorder());
		
		this.add(Box.createRigidArea(new Dimension(400, 5)));
		this.add(infoPanel);
		this.add(optionsPanel);
		this.add(controlPanel);
		
		t.start();
		
		invalidate();
		validate();
		repaint();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(edittingCut)
		{
			if(e.getSource() == clearButton)
			{
				cutPixelGridPanel.clearPixels();
				this.glassPreviewPanel.resetGlassImage();
			}
			if(e.getSource() == previewButton)
			{
				try
				{
					newGlassDesign	= new GlassDesign(cutPixelGridPanel.getInputPixelSchematic());
					//newGlassDesign.printPixelSchematic();
					
					newGlassDesign.findBorderPoints();
					for(Point2D.Double pt : newGlassDesign.getBorderPoints())
					{
						this.glassPreviewPanel.getPreviewGlass().cutGlassAtPoint(pt);
					}
					
					infoText.setText("<html>Draw the shape to cut out of this piece of glass.<BR><FONT COLOR=#000000>Your shape should be exactly one closed loop.</FONT></html>");
				}
				catch (NoClosedShapeException exc)
				{
					infoText.setText("<html>Draw the shape to cut out of this piece of glass.<BR>Your shape should be exactly one <FONT COLOR=#FF0000>closed loop.</FONT></html>");
				}
				catch(MultipleClosedShapeException exc)
				{
					infoText.setText("<html>Draw the shape to cut out of this piece of glass.<BR>Your shape should be <FONT COLOR=#FF0000>exactly one</FONT> closed loop.</html>");
				}
			}
			if(e.getSource() == continueButton)
			{
				try
				{
					newGlassDesign	= new GlassDesign(cutPixelGridPanel.getInputPixelSchematic());
					//newGlassDesign.printPixelSchematic();
					
					drillPixelGridPanel.setSetupPixelSchematic(newGlassDesign.getPhysicalSchematic());
					
					newGlassDesign.findBorderPoints();
					for(Point2D.Double pt : newGlassDesign.getBorderPoints())
					{
						this.glassPreviewPanel.getPreviewGlass().cutGlassAtPoint(pt);
					}
					for(int r = 0; r < newGlassDesign.getPhysicalSchematic().size(); r++)
					{
						for(int c = 0; c < newGlassDesign.getPhysicalSchematic().get(0).size(); c++)
						{
							if(newGlassDesign.getPhysicalSchematic().get(r).get(c) == 0)
							{
								this.glassPreviewPanel.getPreviewGlass().breakoutGlassCompletely(newGlassDesign.getPhysicalSchematic());
							}
						}
					}
					
					CardLayout c = (CardLayout)(optionsPanel.getLayout());
					c.show(optionsPanel, "DRILL EDITOR PANEL");
					
					infoText.setText("<html>Select where to drill holes in this piece of glass.<BR>Drill holes can not be placed on the edges.</html>");
					infoText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Custom-Drilled Glass"), BorderFactory.createEmptyBorder(5,5,5,5)));
					
					edittingCut		= false;
					edittingDrill	= true;
				}
				catch (NoClosedShapeException exc)
				{
					infoText.setText("<html>Draw the shape to cut out of this piece of glass.<BR>Your shape should be exactly one <FONT COLOR=#FF0000>closed loop.</FONT></html>");
				}
				catch(MultipleClosedShapeException exc)
				{
					infoText.setText("<html>Draw the shape to cut out of this piece of glass.<BR>Your shape should be <FONT COLOR=#FF0000>exactly one</FONT> closed loop.</html>");
				}
				
				
				//else
				//{
					
				//}
			}
			if(e.getSource() == quitButton)
			{	
				parentPanel.closeWizard();
			}
		}
		else if(edittingDrill)
		{
			if(e.getSource() == clearButton)
			{
				drillPixelGridPanel.clearPixels();
				this.glassPreviewPanel.resetGlassImage();
				
				for(Point2D.Double pt : newGlassDesign.getBorderPoints())
				{
					this.glassPreviewPanel.getPreviewGlass().cutGlassAtPoint(pt);
				}
				for(int r = 0; r < newGlassDesign.getPhysicalSchematic().size(); r++)
				{
					for(int c = 0; c < newGlassDesign.getPhysicalSchematic().get(0).size(); c++)
					{
						if(newGlassDesign.getPhysicalSchematic().get(r).get(c) == 0)
						{
							this.glassPreviewPanel.getPreviewGlass().breakoutGlassCompletely(newGlassDesign.getPhysicalSchematic());
						}
					}
				}
			}
			if(e.getSource() == previewButton)
			{
				newGlassDesign.setPhysicalSchematic(drillPixelGridPanel.getInputPixelSchematic());
				newGlassDesign.printPixelSchematic();
				
				newGlassDesign.findDrillPoints();
				for(Point2D.Double pt : newGlassDesign.getDrillPoints())
				{
					this.glassPreviewPanel.getPreviewGlass().drillGlassAtPoint(pt);
				}
			}
			if(e.getSource() == continueButton)
			{
				newGlassDesign.setPhysicalSchematic(drillPixelGridPanel.getInputPixelSchematic());
				newGlassDesign.printPixelSchematic();
				
				paintPixelGridPanel.setSetupPixelSchematic(newGlassDesign.getPhysicalSchematic());
				
				newGlassDesign.findDrillPoints();
				for(Point2D.Double pt : newGlassDesign.getDrillPoints())
				{
					this.glassPreviewPanel.getPreviewGlass().drillGlassAtPoint(pt);
				}
				
				CardLayout c = (CardLayout)(optionsPanel.getLayout());
				c.show(optionsPanel, "PAINT EDITOR PANEL");
				
				infoText.setText("<html>Paint the colors you would like on this piece of glass.<BR>Use the Brush Settings tab to change brush size and color.</html>");
				infoText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Custom-Painted Glass"), BorderFactory.createEmptyBorder(5,5,5,5)));
				
				edittingDrill	= false;
				edittingPaint	= true;
				
			}
			if(e.getSource() == quitButton)
			{
				parentPanel.closeWizard();
			}
		}
		else if(edittingPaint)
		{
			if(e.getSource() == quitButton)
			{	
				parentPanel.closeWizard();
			}
			if(e.getSource() == clearButton)
			{
				paintPixelGridPanel.clearPixels();
				this.glassPreviewPanel.resetGlassImage();
				
				for(Point2D.Double pt : newGlassDesign.getBorderPoints())
				{
					this.glassPreviewPanel.getPreviewGlass().cutGlassAtPoint(pt);
				}
				for(int r = 0; r < newGlassDesign.getPhysicalSchematic().size(); r++)
				{
					for(int c = 0; c < newGlassDesign.getPhysicalSchematic().get(0).size(); c++)
					{
						if(newGlassDesign.getPhysicalSchematic().get(r).get(c) == 0)
						{
							this.glassPreviewPanel.getPreviewGlass().breakoutGlassCompletely(newGlassDesign.getPhysicalSchematic());
						}
					}
				}
				for(Point2D.Double pt : newGlassDesign.getDrillPoints())
				{
					this.glassPreviewPanel.getPreviewGlass().drillGlassAtPoint(pt);
				}
			}
			if(e.getSource() == previewButton)
			{
				for(int r = 0; r < paintPixelGridPanel.getPixels().size(); r++)
				{
					newGlassDesign.getPaintSchematic().add(new ArrayList<Color>());
					if(newGlassDesign.getPaintSchematic().get(r).size() != 0)
					{				
						for(int c = 0; c < paintPixelGridPanel.getPixels().get(0).size(); c++)
						{
							newGlassDesign.getPaintSchematic().get(r).set(c, paintPixelGridPanel.getPixels().get(r).get(c).getPixelColor());
						}
					}
					else
					{
						for(int c = 0; c < paintPixelGridPanel.getPixels().get(0).size(); c++)
						{
							newGlassDesign.getPaintSchematic().get(r).add(paintPixelGridPanel.getPixels().get(r).get(c).getPixelColor());
						}
					}
					
				}
				
				for(int r = 0; r < newGlassDesign.getPhysicalSchematic().size(); r++)
				{
					for(int c = 0; c < newGlassDesign.getPhysicalSchematic().get(0).size(); c++)
					{
						if(newGlassDesign.getPhysicalSchematic().get(r).get(c) == 1)
						{
							this.glassPreviewPanel.getPreviewGlass().paintGlassAtPoint(new Point2D.Double(c, r), newGlassDesign.getPaintSchematic().get(r).get(c));
						}
					}
				}
			}
			if(e.getSource() == continueButton)
			{
				for(int r = 0; r < paintPixelGridPanel.getPixels().size(); r++)
				{
					newGlassDesign.getPaintSchematic().add(new ArrayList<Color>());
					if(newGlassDesign.getPaintSchematic().get(r).size() != 0)
					{				
						for(int c = 0; c < paintPixelGridPanel.getPixels().get(0).size(); c++)
						{
							newGlassDesign.getPaintSchematic().get(r).set(c, paintPixelGridPanel.getPixels().get(r).get(c).getPixelColor());
						}
					}
					else
					{
						for(int c = 0; c < paintPixelGridPanel.getPixels().get(0).size(); c++)
						{
							newGlassDesign.getPaintSchematic().get(r).add(paintPixelGridPanel.getPixels().get(r).get(c).getPixelColor());
						}
					}
					
				}
				
				for(int r = 0; r < newGlassDesign.getPhysicalSchematic().size(); r++)
				{
					for(int c = 0; c < newGlassDesign.getPhysicalSchematic().get(0).size(); c++)
					{
						if(newGlassDesign.getPhysicalSchematic().get(r).get(c) == 1)
						{
							this.glassPreviewPanel.getPreviewGlass().paintGlassAtPoint(new Point2D.Double(c, r), newGlassDesign.getPaintSchematic().get(r).get(c));
						}
					}
				}
				
				if(cutPixelGridPanel.getWasEditted())
				{
					cutCheckBox.setSelected(true);
					breakoutCheckBox.setSelected(true);
				}
				if(drillPixelGridPanel.getWasEditted())
				{
					drillCheckBox.setSelected(true);
				}
				if(paintPixelGridPanel.getWasEditted())
				{
					paintCheckBox.setSelected(true);
				}
				
				continueButton.setText("Finish");
				CardLayout c = (CardLayout)(optionsPanel.getLayout());
				c.show(optionsPanel, "TREATMENT EDITOR PANEL");
				
				infoText.setText("<html>Select the treatments you would like applied to this piece of glass.</html>");
				infoText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Custom-Treated Glass"), BorderFactory.createEmptyBorder(5,5,5,5)));
				
				edittingPaint		= false;
				edittingTreatment	= true;
				
			}
		
			if(e.getSource() == paintbrushButton)
			{
				System.out.println("paintbrush radio hit");
				paintPixelGridPanel.setBrushWidth(GlassGridPanel.PAINTBRUSH_WIDTH);
				pencilButton.setSelected(false);
			}
			else if(e.getSource() == pencilButton)
			{
				System.out.println("pencil radio hit");
				paintPixelGridPanel.setBrushWidth(GlassGridPanel.PENCIL_WIDTH);
				paintbrushButton.setSelected(false);
			}
		}
		else if(edittingTreatment)
		{
			if(e.getSource() == quitButton)
			{	
				parentPanel.closeWizard();
			}
			if(e.getSource() == clearButton)
			{
				crossseamCheckBox.setSelected(false);
				grindCheckBox.setSelected(false);
				washCheckBox.setSelected(false);
				uvCheckBox.setSelected(false);
				ovenCheckBox.setSelected(false);
			}
			if(e.getSource() == previewButton)
			{
				if(grindCheckBox.isSelected() && !washCheckBox.isSelected())
				{
					this.glassPreviewPanel.getPreviewGlass().setEffectsState("Ground");
				}
				else if(washCheckBox.isSelected())
				{
					this.glassPreviewPanel.getPreviewGlass().setEffectsState("Washed");
				}
			}
			if(e.getSource() == continueButton)
			{
				if(grindCheckBox.isSelected() && !washCheckBox.isSelected())
				{
					this.glassPreviewPanel.getPreviewGlass().setEffectsState("Ground");
				}
				else if(washCheckBox.isSelected())
				{
					this.glassPreviewPanel.getPreviewGlass().setEffectsState("Washed");
				}
				
				newGlassDesign.setDesignName(enterDesignNameField.getText());
				newGlassDesign.setPreviewImage(this.glassPreviewPanel.getPreviewGlass().getGlassImage());
				parentPanel.addNewGlassDesign(newGlassDesign);
				
				parentPanel.closeWizard();
				
				for(String s : newGlassDesign.getTreatmentSchematic())
				{
					System.out.println(s);
				}

				System.out.println("Finished.");
			}
		}
		
		
		repaint();
	}	
	
	public void itemStateChanged(ItemEvent e)
	{
		if(e.getItemSelectable() == cutCheckBox)
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				newGlassDesign.getTreatmentSchematic().add("Cut");
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				newGlassDesign.getTreatmentSchematic().remove("Cut");
			}
		}
		else if(e.getItemSelectable() == breakoutCheckBox)
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				newGlassDesign.getTreatmentSchematic().add("Breakout");
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				newGlassDesign.getTreatmentSchematic().remove("Breakout");
			}
		}
		else if(e.getItemSelectable() == drillCheckBox)
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				newGlassDesign.getTreatmentSchematic().add("Drill");
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				newGlassDesign.getTreatmentSchematic().remove("Drill");
			}
		}
		else if(e.getItemSelectable() == crossseamCheckBox)
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				newGlassDesign.getTreatmentSchematic().add("Cross-Seam");
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				newGlassDesign.getTreatmentSchematic().remove("Cross-Seam");
			}
		}
		else if(e.getItemSelectable() == grindCheckBox)
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				newGlassDesign.getTreatmentSchematic().add("Grind");
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				newGlassDesign.getTreatmentSchematic().remove("Grind");
			}
		}
		else if(e.getItemSelectable() == paintCheckBox)
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				newGlassDesign.getTreatmentSchematic().add("Paint");
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				newGlassDesign.getTreatmentSchematic().remove("Paint");
			}
		}
		else if(e.getItemSelectable() == washCheckBox)
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				newGlassDesign.getTreatmentSchematic().add("Wash");
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				newGlassDesign.getTreatmentSchematic().remove("Wash");
			}
		}
		else if(e.getItemSelectable() == uvCheckBox)
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				newGlassDesign.getTreatmentSchematic().add("UV Treat");
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				newGlassDesign.getTreatmentSchematic().remove("UV Treat");
			}
		}
		else if(e.getItemSelectable() == ovenCheckBox)
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				newGlassDesign.getTreatmentSchematic().add("Bake");
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				newGlassDesign.getTreatmentSchematic().remove("Bake");
			}
		}
	}
	
	public void stateChanged(ChangeEvent e) 
	{ 
	    paintPixelGridPanel.setBrushColor(palette.getColor());
	    palettePreviewPanel.setBackground(palette.getColor());
	}
/*
	public static void main(String[] args)
	{
		JFrame	demoFrame	= new JFrame();
		demoFrame.add(new GlassDesignWizardPanel());
		
		demoFrame.setVisible(true);
		demoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demoFrame.setSize(425, 625);
		
		
	}
*/
}
