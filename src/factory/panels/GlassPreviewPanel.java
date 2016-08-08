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
import javax.swing.Timer;
import javax.swing.text.JTextComponent;

import factory.gui.GuiGlass;

public class GlassPreviewPanel extends JPanel
{
	GuiGlass			previewGlass;
	Rectangle2D.Double	backgroundRectangle;
	Color				bgColor					= new Color(64, 64, 64);
	int					panelWidth, panelHeight;
	
	public GlassPreviewPanel(int width, int height)
	{
		super();
		panelWidth	= width;
		panelHeight	= height;
		backgroundRectangle	= new Rectangle2D.Double(5, 15, panelWidth-10, panelHeight-20);
		previewGlass				= new GuiGlass(0, 0, 0);
		previewGlass.setXCoord( (panelWidth - previewGlass.getWidth())/2 );
		previewGlass.setYCoord( (panelHeight - previewGlass.getHeight())/2 + 5);
		//this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Preview"), BorderFactory.createEmptyBorder(5,5,5,5)));
		//this.setMinimumSize(new Dimension(100, 100));
		//this.setPreferredSize(new Dimension(100, 100));
		//this.setMaximumSize(new Dimension(100, 100));
		this.setBackground(bgColor);

	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(bgColor);
		g2.fill(backgroundRectangle);		
		previewGlass.draw(g2, this);
	}
	
	public void resetGlassImage()
	{
		previewGlass				= new GuiGlass(0, 0, 0);
		previewGlass.setXCoord( (panelWidth - previewGlass.getWidth())/2 );
		previewGlass.setYCoord( (panelHeight - previewGlass.getHeight())/2 + 5);
	}
	
	public GuiGlass getPreviewGlass()
	{
		return previewGlass;
	}
}
