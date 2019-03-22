package com.mycompany.imagej.gui;

import ij.gui.GUI;
import ij.gui.GenericDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;

// TODO: GUI.Fancify().betterPadding()

/**
 * the main window in which the user can interact
 */
@SuppressWarnings("serial")
public class MainWindow extends GenericDialog {
	// graphical elements
	protected JPanel contentPanel = new JPanel(new GridBagLayout());
	protected JLabel lblOpenText = new JLabel("Open & identify ...");
	protected JLabel lblExcelFile = new JLabel("Save Excel-File to: ");
	protected JRadioButton rbOneImage = new JRadioButton("... 1 Image");
	protected JRadioButton rbMoreImages = new JRadioButton("... more than 1 image");
	protected JTextField txtOpenLocation = new JTextField("Imagefile Location");
	protected JTextField txtSaveLocation = new JTextField("CSV File Location");
	protected JButton btnOpenFile = new JButton("Choose Image");
	protected JButton btnSaveFile = new JButton("Choose Location");
	protected JButton btnStartProcess = new JButton("Start");
	protected JCheckBox ckbEvaluation = new JCheckBox("Display Classification Rate"); 
	
	// local variables & objects
	private int currentY = 0;
	private GridBagConstraints constraints = new GridBagConstraints();
	private MainWindowController controller;
	
	/**
	 * Constructor which sets the title and fills the window with life
	 * @param windowName window Name to set
	 */
	public MainWindow(String windowName) {
		super(windowName);
		controller = new MainWindowController(this);
		fillWindowWithLife();
		GUI.center(this);
	}
	
	/**
	 * @return connected MainWindowController of this object
	 */
	public MainWindowController getController() {
		return controller;
	}
	
	/**
	 * fills the window with life by adding all graphical elements
	 */
	private void fillWindowWithLife() {			
		// Radio Buttons
		setConstraints(0, currentY++, 2, 0.0);
		contentPanel.add(lblOpenText, constraints);
		rbOneImage.setSelected(true);
		rbMoreImages.setSelected(false);
		
		ButtonGroup bgAmountToOpen = new ButtonGroup();
		bgAmountToOpen.add(rbOneImage);
		bgAmountToOpen.add(rbMoreImages);

		setConstraints(0, currentY, 1, 0.5);
		contentPanel.add(rbOneImage, constraints);
		setConstraints(1, currentY++, 1, 0.5);
		contentPanel.add(rbMoreImages, constraints);
		
		// File Chooser for Loading
		txtOpenLocation.setEditable(false);
		setConstraints(0, currentY, 1, 0.8);
		contentPanel.add(txtOpenLocation, constraints);
		setConstraints(1, currentY++, 1, 0.2);
		contentPanel.add(btnOpenFile, constraints);
		
		btnOpenFile.addActionListener((e) -> {
			controller.decideWhichFileChooser();
		});
				
		// output directory (File Chooser)
		setConstraints(0, currentY++, 2, 0.0);
		contentPanel.add(lblExcelFile, constraints);
		
		txtSaveLocation.setEditable(false);
		setConstraints(0, currentY, 1, 0.8);
		contentPanel.add(txtSaveLocation, constraints);
		
		setConstraints(1, currentY++, 1, 0.2);
		contentPanel.add(btnSaveFile, constraints);
		btnSaveFile.addActionListener((e) -> {
			controller.openFileChooserForSaving();
		});		
		
		// Check Box for Classification Rate
		setConstraints(0, currentY++, 2, 0.0);
		contentPanel.add(ckbEvaluation, constraints);
		
		// Start Button
		setConstraints(1, currentY++, 1, 0.0);
		contentPanel.add(btnStartProcess, constraints);		
		btnStartProcess.addActionListener((e) -> {
			controller.reactToStartButton();
		});
		
		this.add(contentPanel);
	}
	
	/**
	 * sets the gridBagConstraints to the given values 
	 * @param xPos current column
	 * @param yPos current row
	 * @param width how many columns the element should span
	 * @param weightInX weight in X direction
	 */
	private void setConstraints(int xPos, int yPos, int width, double weightInX) {
		constraints.gridx = xPos;
		constraints.gridy = yPos;
		constraints.gridwidth = width;
		constraints.weightx = weightInX;
		constraints.fill = GridBagConstraints.HORIZONTAL;
	}
}