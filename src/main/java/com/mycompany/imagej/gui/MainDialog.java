package com.mycompany.imagej.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.process.ImageProcessor;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import javax.swing.*;

import com.mycompany.imagej.Abschlussprojekt_PlugIn;

public class MainDialog {
	private GenericDialog gd;
	private final boolean DEBUG = true;
	private File imgToOpen;
	private int currentY = 0;
	
	// Constructor, which fills the dialog with elements
	public MainDialog() {			
		gd = new GenericDialog("Choose Plate to Identify");
		gd.setModalityType(Dialog.ModalityType.MODELESS);	
		
		JPanel pane = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
	
		// Radio Buttons for file amount
		JLabel openText = new JLabel("Open & identify ...");
		constraints.gridx = 0;
		constraints.gridy = currentY++;
		constraints.weightx = 0.0;
		constraints.gridwidth = 2;
		pane.add(openText, constraints);
		
		JRadioButton rbOneImage = new JRadioButton();
		rbOneImage.setSelected(true);
		rbOneImage.setText("... 1 Image");
		JRadioButton rbMoreThanOneImage = new JRadioButton();
		rbMoreThanOneImage.setSelected(false);
		rbMoreThanOneImage.setText("... more than 1 image");
		
		ButtonGroup radioButtons = new ButtonGroup();
		radioButtons.add(rbOneImage);
		radioButtons.add(rbMoreThanOneImage);
		
		constraints.gridx = 0;
		constraints.gridy = currentY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.5;
		pane.add(rbOneImage, constraints);
		constraints.gridx = 1;
		constraints.gridy = currentY++;
		constraints.gridwidth = 1;
		constraints.weightx = 0.5;
		pane.add(rbMoreThanOneImage, constraints);
		
		// File Chooser
		JTextField openLocation = new JTextField("File Location");
		openLocation.setEditable(false);
		constraints.gridx = 0;
		constraints.gridy = currentY;
		constraints.gridwidth = 1;
		constraints.weightx = 0.8;
		pane.add(openLocation, constraints);
		
		JButton btnForOpenFile = new JButton("Choose Image");
		openFileLocation(btnForOpenFile);
		constraints.gridx = 1;
		constraints.gridy = currentY++;
		constraints.gridwidth = 1;
		constraints.weightx = 0.2;
		pane.add(btnForOpenFile, constraints);
		
		if(DEBUG) {
			// which method 
			JLabel method = new JLabel("Method:");
			constraints.gridx = 0;
			constraints.gridy = currentY;
			constraints.gridwidth = 1;
			constraints.weightx = 0.5;
			pane.add(method, constraints);
			
			JTextField methodNumber = new JTextField("Number");
			constraints.gridx = 1;
			constraints.gridy = currentY++;
			constraints.gridwidth = 1;
			constraints.weightx = 0.5;
			pane.add(methodNumber, constraints);				
		} else {
			// output directory (File Chooser)
			JLabel excelFile = new JLabel("Save Excel-File to:");
			constraints.gridx = 0;
			constraints.gridy = currentY++;
			constraints.gridwidth = 2;
			constraints.weightx = 0.0;
			pane.add(excelFile, constraints);
			
			JTextField saveLocation = new JTextField("File Location");
			saveLocation.setEditable(false);
			constraints.gridx = 0;
			constraints.gridy = currentY;
			constraints.gridwidth = 1;
			constraints.weightx = 0.8;
			pane.add(saveLocation, constraints);
			
			JButton btnForSaveFile = new JButton("Choose Image");
			btnForSaveFile.addActionListener((e) -> {
				JFileChooser jfc = new JFileChooser();
		        int returnVal = jfc.showOpenDialog(btnForSaveFile);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = jfc.getSelectedFile();
		        }
			});
			constraints.gridx = 1;
			constraints.gridy = currentY++;
			constraints.gridwidth = 1;
			constraints.weightx = 0.2;
			pane.add(btnForSaveFile, constraints);
		}	
		
		JButton btnStartProcess = new JButton("Start");
		btnStartProcess.addActionListener((e) -> {
			// TODO get Number from TextField
			everythingIsOkay("2");
		}); 
		constraints.gridx = 1;
		constraints.gridy = currentY++;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		pane.add(btnStartProcess, constraints);		
		
		gd.add(pane);
		gd.showDialog();
	}
	
	private void everythingIsOkay(String numberAsString) {
		try {
			int methodNumber = Integer.parseInt(numberAsString);
			// TODO Check if the image processor is null
			Abschlussprojekt_PlugIn.chooseMethod(methodNumber, convertFileToImageProcessor());
		} catch (NumberFormatException nfEx) {
			// TODO Inform the user
		}
	}
	
	private void openFileLocation(JButton btnToOpenFile) {		
		btnToOpenFile.addActionListener((e) -> {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new ImageFileFilter());
	        int returnVal = jfc.showOpenDialog(btnToOpenFile);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        	imgToOpen = jfc.getSelectedFile();
	        }
		});
	}
	
	private ImageProcessor convertFileToImageProcessor() {
		if(imgToOpen != null) {
			ImagePlus img = IJ.openImage(imgToOpen.getAbsolutePath()); 
		    return img.getProcessor(); 
		} else {
			return null;
		}
		
	}
}