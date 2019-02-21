package com.mycompany.imagej;

import ij.ImagePlus;
import ij.gui.GenericDialog;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.*;

public class MainDialog {
	private GenericDialog gd;
	private ImagePlus img = new ImagePlus("C:\\Users\\Janfi\\Downloads\\18032008066.tif");
	private final boolean DEBUG = true;
	
	// Constructor, which fills the dialog with elements
	public MainDialog() {			
		gd = new GenericDialog("Choose Plate to Identify");
		gd.setModalityType(Dialog.ModalityType.MODELESS);		
		gd.setOKLabel("Start");
		
		JPanel pane = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
	
		// Radio Buttons for file amount
		JLabel openText = new JLabel("Open & identify ...");
		constraints.gridx = 0;
		constraints.gridy = 0;
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
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weightx = 0.5;
		pane.add(rbOneImage, constraints);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weightx = 0.5;
		pane.add(rbMoreThanOneImage, constraints);
		
		// File Chooser
		JTextField openLocation = new JTextField("File Location");
		openLocation.setEditable(false);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.weightx = 0.8;
		pane.add(openLocation, constraints);
		
		JButton btnForFileChooser = new JButton("Choose Image");
		btnForFileChooser.addActionListener((e) -> {
			JFileChooser jfc = new JFileChooser();
	        int returnVal = jfc.showOpenDialog(btnForFileChooser);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = jfc.getSelectedFile();
	        }
		});
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.weightx = 0.2;
		pane.add(btnForFileChooser, constraints);
		
		if(DEBUG) {
			// which method 
			JLabel method = new JLabel("Method:");
			constraints.gridx = 0;
			constraints.gridy = 3;
			constraints.gridwidth = 1;
			constraints.weightx = 0.5;
			pane.add(method, constraints);
			
			JTextField methodNumber = new JTextField("Number");
			constraints.gridx = 1;
			constraints.gridy = 3;
			constraints.gridwidth = 1;
			constraints.weightx = 0.5;
			pane.add(methodNumber, constraints);			
		} else {
			// output directory (File Chooser)
			JLabel excelFile = new JLabel("Save Excel-File to:");
			constraints.gridx = 0;
			constraints.gridy = 3;
			constraints.gridwidth = 2;
			constraints.weightx = 0.0;
			pane.add(excelFile, constraints);
			
			JTextField saveLocation = new JTextField("File Location");
			saveLocation.setEditable(false);
			constraints.gridx = 0;
			constraints.gridy = 4;
			constraints.gridwidth = 1;
			constraints.weightx = 0.8;
			pane.add(saveLocation, constraints);
			
			JButton btnForFileChooser2 = new JButton("Choose Image");
			btnForFileChooser2.addActionListener((e) -> {
				JFileChooser jfc = new JFileChooser();
		        int returnVal = jfc.showOpenDialog(btnForFileChooser);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = jfc.getSelectedFile();
		        }
			});
			constraints.gridx = 1;
			constraints.gridy = 4;
			constraints.gridwidth = 1;
			constraints.weightx = 0.2;
			pane.add(btnForFileChooser2, constraints);
		}	
		
		if(gd.wasOKed()) {
			everythingIsOkay();
		}
		
		gd.add(pane);
		gd.showDialog();
	}
	
	private void everythingIsOkay() {
	}
}