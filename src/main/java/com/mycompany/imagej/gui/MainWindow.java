package com.mycompany.imagej.gui;

import ij.gui.GUI;
import ij.gui.GenericDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;

// TODO: GUI.fancify()

/**
 * the main window in which the user can interact
 */
@SuppressWarnings("serial")
public class MainWindow extends GenericDialog {
	// graphical elements
	protected JPanel contentPanel = new JPanel(new GridBagLayout());
	protected JLabel lblOpenText = new JLabel("Open & identify ...");
	protected JLabel lblMethod = new JLabel("Method: ");
	protected JLabel lblContrast = new JLabel("Contrast Adjustment: ");
	protected JLabel lblGrayscale = new JLabel("Grayscale: ");
	protected JLabel lblMedian = new JLabel("Median Filter: ");
	protected JLabel lblExcelFile = new JLabel("Save Excel-File to: ");
	protected JRadioButton rbOneImage = new JRadioButton("... 1 Image");
	protected JRadioButton rbMoreImages = new JRadioButton("... more than 1 image");
	protected JRadioButton rbPreprocessing = new JRadioButton("Test order for Preprocessing");
	protected JRadioButton rbCertainMethod = new JRadioButton("Test one specific method");
	protected JTextField txtOpenLocation = new JTextField("File Location");
	protected JTextField txtMethodNumber = new JTextField("Number");
	protected JTextField txtSaveLocation = new JTextField("Not Implemented yet...");
	protected JTextField txtContrastNumber = new JTextField("0 to ignore");
	protected JTextField txtGrayscaleNumber = new JTextField("0 to ignore");
	protected JTextField txtMedianNumber = new JTextField("0 to ignore");
	protected JButton btnOpenFile = new JButton("Choose Image");
	protected JButton btnSaveFile = new JButton("Choose Location");
	protected JButton btnStartProcess = new JButton("Start");
	
	// local variables
	private int currentY = 0;
	private GridBagConstraints constraints = new GridBagConstraints();
	private MainWindowController controller;
	
	/**
	 * Constructor which sets the titel and fills the window with life
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
		
		// Order of the Methods	
		rbPreprocessing.setSelected(true);
		rbCertainMethod.setSelected(false);
		
		ButtonGroup bgMethod = new ButtonGroup();
		bgMethod.add(rbPreprocessing);
		bgMethod.add(rbCertainMethod);
		
		setConstraints(0, currentY, 1, 0.5);
		contentPanel.add(rbPreprocessing, constraints);
		setConstraints(1, currentY++, 1, 0.5);
		contentPanel.add(rbCertainMethod, constraints);
		
		setConstraints(0, currentY, 1, 0.8);
		contentPanel.add(lblContrast, constraints);
		setConstraints(1, currentY++, 1, 0.2);
		contentPanel.add(txtContrastNumber, constraints);
		
		setConstraints(0, currentY, 1, 0.8);
		contentPanel.add(lblGrayscale, constraints);
		setConstraints(1, currentY++, 1, 0.2);
		contentPanel.add(txtGrayscaleNumber, constraints);

		setConstraints(0, currentY, 1, 0.8);
		contentPanel.add(lblMedian, constraints);
		setConstraints(1, currentY++, 1, 0.2);
		contentPanel.add(txtMedianNumber, constraints);
		
		setConstraints(0, currentY, 1, 0.8);
		contentPanel.add(lblMethod, constraints);
		setConstraints(1, currentY++, 1, 0.2);
		contentPanel.add(txtMethodNumber, constraints);		
		
		// output directory (File Chooser)
		setConstraints(0, currentY++, 2, 0.0);
		contentPanel.add(lblExcelFile, constraints);
		
		txtSaveLocation.setEditable(false);
		setConstraints(0, currentY, 1, 0.8);
		contentPanel.add(txtSaveLocation, constraints);
		
		setConstraints(1, currentY++, 1, 0.2);
		contentPanel.add(btnSaveFile, constraints);
		
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
	 * @param weightInX wight in X direction
	 */
	private void setConstraints(int xPos, int yPos, int width, double weightInX) {
		constraints.gridx = xPos;
		constraints.gridy = yPos;
		constraints.gridwidth = width;
		constraints.weightx = weightInX;
	}
}