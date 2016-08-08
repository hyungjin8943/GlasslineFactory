package factory.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;

import factory.agent.GlassOrder;
//import factory.agent.GlassOrder.GlassTreatment;
import factory.agent.GlassOrder.GlassTreatmentStatus;
import factory.gui.GuiGlass;

public class GlassPanel extends JPanel implements ActionListener
{
	Application					parentApplication;
	
	ArrayList<GlassDesign>		glassDesigns;
	ArrayList<String>			glassDesignNames;
	GlassDesignPresets			designPresets;
	
	GlassOrder					currentOrder;
	GlassDesign					orderedDesign;
	int							orderedQuantity;
	
	JFrame						wizardFrame;
	
	GlassPreviewPanel			previewDesignPanel;
	JPanel						existingDesignPanel;
	JLabel							orderLabel;
	JLabel							selectDesignLabel;
	JComboBox						selectDesignMenu;
	JLabel							enterQuantityLabel;
	JTextField						enterQuantityField;
	JButton							orderButton;
	JButton							saveButton;
	
	JPanel						createDesignPanel;
	JLabel							customLabel;
	JButton							customButton;

    public GlassPanel(Application parentApp) 
    {
    	parentApplication	= parentApp;
    	
    	glassDesigns		= new ArrayList<GlassDesign>();
    	glassDesignNames	= new ArrayList<String>();
    	designPresets		= new GlassDesignPresets(); 
    	
    	//addStandardGlassDesign();
    	
    	currentOrder	= null;
    	
    	this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    	
    	previewDesignPanel	= new GlassPreviewPanel(150, 150);
    	existingDesignPanel	= new JPanel();
    	createDesignPanel	= new JPanel();
    	
    	existingDesignPanel.setLayout(new BoxLayout(existingDesignPanel, BoxLayout.PAGE_AXIS));
    	createDesignPanel.setLayout(new BoxLayout(createDesignPanel, BoxLayout.PAGE_AXIS));
    	
    	previewDesignPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Design Preview"), BorderFactory.createEmptyBorder(5,5,5,5)));
		previewDesignPanel.setMinimumSize(new Dimension(150, 150));
		previewDesignPanel.setPreferredSize(new Dimension(150, 150));
		previewDesignPanel.setMaximumSize(new Dimension(150, 150));
		
		existingDesignPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Order Existing Design"), BorderFactory.createEmptyBorder(5,5,5,5)));
		existingDesignPanel.setMinimumSize(new Dimension(300, 150));
		existingDesignPanel.setPreferredSize(new Dimension(300, 150));
		existingDesignPanel.setMaximumSize(new Dimension(300, 150));
			orderLabel					= new JLabel("Specify new glass order:");
			
			JPanel	selectDesignPanel	= new JPanel();
			selectDesignPanel.setAlignmentX(LEFT_ALIGNMENT);
			selectDesignPanel.setLayout(new BoxLayout(selectDesignPanel, BoxLayout.LINE_AXIS));
				selectDesignLabel		= new JLabel("Select Design: ");
				selectDesignMenu		= new JComboBox(glassDesignNames.toArray());
				selectDesignMenu.addActionListener(this);
				selectDesignMenu.setMinimumSize(new Dimension(150, 25));
				selectDesignMenu.setPreferredSize(new Dimension(150, 25));
				selectDesignMenu.setMaximumSize(new Dimension(150, 25));
				selectDesignPanel.add(selectDesignLabel);
				selectDesignPanel.add(Box.createRigidArea(new Dimension(15, 25)));
				selectDesignPanel.add(selectDesignMenu);
			JPanel	enterQuantityPanel	= new JPanel();
			enterQuantityPanel.setAlignmentX(LEFT_ALIGNMENT);
			enterQuantityPanel.setLayout(new BoxLayout(enterQuantityPanel, BoxLayout.LINE_AXIS));
				enterQuantityLabel 			= new JLabel("Quantity: ");
				enterQuantityField	= new JTextField();
				enterQuantityField.setMinimumSize(new Dimension(150, 25));
				enterQuantityField.setPreferredSize(new Dimension(150, 25));
				enterQuantityField.setMaximumSize(new Dimension(150, 25));
				enterQuantityPanel.add(enterQuantityLabel);
				enterQuantityPanel.add(Box.createRigidArea(new Dimension(46, 25)));
				enterQuantityPanel.add(enterQuantityField);
			JPanel	buttonPanel	= new JPanel();
			buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
				orderButton			= new JButton("Place Glass Order");
				orderButton.addActionListener(this);
				buttonPanel.add(Box.createRigidArea(new Dimension(65, 25)));
				buttonPanel.add(orderButton);
				/////
				saveButton			= new JButton("Save");
				saveButton.addActionListener(this);
				//buttonPanel.add(saveButton);
				/////
			existingDesignPanel.add(orderLabel);
			existingDesignPanel.add(Box.createRigidArea(new Dimension(10, 5)));
			existingDesignPanel.add(selectDesignPanel);
			existingDesignPanel.add(Box.createRigidArea(new Dimension(10, 5)));
			existingDesignPanel.add(enterQuantityPanel);
			existingDesignPanel.add(Box.createRigidArea(new Dimension(10, 10)));
			existingDesignPanel.add(buttonPanel);
		
		createDesignPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Create New Design"), BorderFactory.createEmptyBorder(5,5,5,5)));
		createDesignPanel.setMinimumSize(new Dimension(300, 150));
		createDesignPanel.setPreferredSize(new Dimension(300, 150));
		createDesignPanel.setMaximumSize(new Dimension(300, 150));
			customLabel		= new JLabel("<html>Customize the shape, drill and paint patterns, and treatments for a new glass design.</html>");
			customButton	= new JButton("Launch Custom Glass Wizard");
			customButton.addActionListener(this);
			createDesignPanel.add(customLabel);
			createDesignPanel.add(Box.createRigidArea(new Dimension(10, 30)));
			JPanel	buttonPanel2	= new JPanel();
				buttonPanel2.setAlignmentX(LEFT_ALIGNMENT);
				buttonPanel2.setLayout(new BoxLayout(buttonPanel2, BoxLayout.LINE_AXIS));
				buttonPanel2.add(Box.createRigidArea(new Dimension(35, 25)));
				buttonPanel2.add(customButton);
			createDesignPanel.add(buttonPanel2);
    	
		this.add(previewDesignPanel);
		this.add(existingDesignPanel);
		this.add(createDesignPanel);
		
		loadPresetGlassDesigns();
    }
    
    public void addNewGlassDesign(GlassDesign newDesign)
    {
    	glassDesigns.add(newDesign);
    	selectDesignMenu.addItem(newDesign.getDesignName());
    	
    	closeWizard();
    }
    
    public void addPresetGlassDesigns()
    {
    	for(GlassDesign preset : designPresets.getPresetDesigns())
    	{
    		this.glassDesigns.add(preset);
    		selectDesignMenu.addItem(preset.getDesignName());
    	}
    }
    
    public void loadPresetGlassDesigns()
    {
    	ObjectInputStream inputStream = null;
            
        try 
    	{
    		//Construct the ObjectInputStream object
    	   	inputStream = new ObjectInputStream(new FileInputStream("savedglassdesignpresets.txt"));
                
           	Object savedObject = null;
           	
           	GlassDesignPresets savedPresets	= new GlassDesignPresets();
                
           	if ((savedObject = inputStream.readObject()) != null) 
    		{
               	savedPresets	= new GlassDesignPresets((GlassDesignPresets)savedObject);
    		}
    		else
    		{
    			System.out.println("There are no saved GlassDesign presets.");
    		}
           	
           	this.designPresets	= savedPresets;
           	addPresetGlassDesigns();
              	
    	}
    	catch(Exception e)
    	{
    		System.out.println("There was an error attempting to load saved GlassDesign presets.");
    	}
    }
    
    public void addStandardGlassDesign()
    {
    	GlassDesign	newStdDesign	= new GlassDesign(); 
    	glassDesigns.add(newStdDesign);
    	glassDesignNames.add(newStdDesign.getDesignName());  
    	orderedDesign	= newStdDesign;
    }
    
    public void launchWizard()
    {
    	wizardFrame	= new JFrame("Custom Glass Design Wizard");
		wizardFrame.add(new GlassDesignWizardPanel(this));
		
		wizardFrame.setVisible(true);
		wizardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		wizardFrame.setSize(425, 625);
    }
    
    public void closeWizard()
    {
    	wizardFrame.setVisible(false);
    	wizardFrame.dispose();
    }
    
    public GlassDesignPresets getDesignPresets()
    {
    	return this.designPresets;
    }
    
    public JPanel getPreviewDesignPanel()
    {
    	return this.previewDesignPanel;
    }

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == orderButton)
		{
			String	quantityInputText	= enterQuantityField.getText();
			try
			{
				int 	quantityInputNum	= Integer.parseInt(quantityInputText);
				
				if(quantityInputNum <= 0)
				{
					this.orderLabel.setText("<html><FONT COLOR=#FF0000>Minimum order quantity is 1.</FONT></html>");
				}
				else if(quantityInputNum > 1337)
				{
					this.orderLabel.setText("<html><FONT COLOR=#FF0000>Maximum order quantity is 1000.</FONT></html>");
				}
				else
				{
					for(int i = 0; i < quantityInputNum; i++)
			    	{
						//Create new glass order
						GlassOrder	newGlassOrder	= new GlassOrder(orderedDesign);
						
						parentApplication.factoryPanel.gpanel.createGlass(newGlassOrder);//, quantityInputNum);
						
						this.orderLabel.setText("Specify new glass order:");
			    	}
				}
			}
			catch(Exception exc)
			{
				this.orderLabel.setText("<html><FONT COLOR=#FF0000>Invalid quantity entry.</FONT></html>");
			}
		}
		if(e.getSource() == customButton)
		{
			launchWizard();
		}
		if(e.getSource() == selectDesignMenu)
		{
			JComboBox cb = (JComboBox)e.getSource();
			
			for(GlassDesign design : glassDesigns)
			if(design.getDesignName().equals((String)cb.getSelectedItem()))
			{
				this.orderedDesign	= design;
				this.previewDesignPanel.getPreviewGlass().setGlassImage(design.getPreviewImage());
			}
		}
		
		if(e.getSource() == saveButton)
		{
			if(!this.designPresets.getPresetDesigns().contains(orderedDesign))
			{
				this.designPresets.addGlassDesign(orderedDesign);
			}
					
			//this.designPresets.writeToFile();
		}
		
	}
}
