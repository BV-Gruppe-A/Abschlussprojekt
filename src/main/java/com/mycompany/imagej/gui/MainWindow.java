package com.mycompany.imagej.gui;

import ij.gui.GUI;
import ij.gui.GenericDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;

/**
 * the main window in which the user can interact
 */
@SuppressWarnings("serial")
public class MainWindow extends GenericDialog {
	/**
	 * panel which contains all content
	 */
	protected JPanel contentPanel = new JPanel(new GridBagLayout());
	
	/**
	 * Label at the top which informs the user about the possibilities of the first two radio buttons
	 */
	protected JLabel lblOpenText = new JLabel("Open & identify ...");
	
	/**
	 * Label before the button for choosing a save location
	 */
	protected JLabel lblExcelFile = new JLabel("Save Excel-File to: ");
	
	/**
	 * Radio Button for only one image
	 */
	protected JRadioButton rbOneImage = new JRadioButton("... 1 Image");
	
	/**
	 * Radio Button for more than one image
	 */
	protected JRadioButton rbMoreImages = new JRadioButton("... more than 1 image");
	
	/**
	 * TextField containing the location to open
	 */
	protected JTextField txtOpenLocation = new JTextField("Imagefile Location");
	
	/**
	 * TextField containing the location to save
	 */
	protected JTextField txtSaveLocation = new JTextField("CSV File Location");
	
	/**
	 * Button to open a file chooser for the open location
	 */
	protected JButton btnOpenFile = new JButton("Choose Image");
	
	/**
	 * Button to open a file chooser for the save location
	 */
	protected JButton btnSaveFile = new JButton("Choose Location");
	
	/**
	 * Button which starts the process
	 */
	protected JButton btnStartProcess = new JButton("Start");
	
	/**
	 * CheckBox which enables the evaluation of the classification
	 */
	protected JCheckBox ckbEvaluation = new JCheckBox("Display Classification Rate");
	
	/**
	 * CheckBox which clears the file before writing
	 */
	protected JCheckBox ckbClearFile = new JCheckBox("Clear File");
	
	/**
	 * describes the current y-position of added elements
	 */
	private int currentY = 0;
	
	/**
	 * contain all location informations for the graphical elements
	 */
	private GridBagConstraints constraints = new GridBagConstraints();
	
	/**
	 * controller which contains the logic of the window
	 */
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
		
		// Check Boxes for Classification Rate & Clearing
		setConstraints(0, currentY++, 2, 0.0);
		contentPanel.add(ckbEvaluation, constraints);
		setConstraints(0, currentY++, 2, 0.0);
		contentPanel.add(ckbClearFile, constraints);
		
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