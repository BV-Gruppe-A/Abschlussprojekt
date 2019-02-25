package com.mycompany.imagej.gui;

import java.io.File;
import javax.swing.JFileChooser;
import com.mycompany.imagej.Abschlussprojekt_PlugIn;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Controller, which controls everything that happens in the main Window
 */
public class MainWindowController {
	// local objects
	private MainWindow windowToControl;
	private File imgToOpen;
	private File placeToSave;
	
	// local variables
	private int contrastNumber;
	private int grayscaleNumber;
	private int medianNumber;
	private int methodNumber;	
	private boolean isPreprocessing = true;
	
	/**
	 * Constructor, which sets the window-object to control
	 * @param window window to control
	 */
	public MainWindowController(MainWindow window) {
		windowToControl = window;
	}
	
	/**
	 * decides, if a single or more than one file should be chosen
	 */
	public void decideWhichFileChooser() {
		if(windowToControl.rbOneImage.isSelected()) {
			openFileChooserLoadingSingleImage();
		} else {
			openFileChooserLoadingImages();
		}
	}
	
	/**
	 * opens a file chooser for a single image
	 */
	private void openFileChooserLoadingSingleImage() {
		JFileChooser fcOpen = new JFileChooser();
		fcOpen.setFileFilter(new ImageFileFilter());
		
        int returnVal = fcOpen.showOpenDialog(windowToControl.btnOpenFile);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	imgToOpen = fcOpen.getSelectedFile();
        	windowToControl.txtOpenLocation.setText(imgToOpen.getName());
        }
	}
	
	/**
	 * opens a file chooser for a whole folder
	 */
	private void openFileChooserLoadingImages() {
		IJ.error("More than one file is currently not implemented!");
		
		// TODO: Add this file chooser
		/*
		JFileChooser fcOpen = new JFileChooser();
		fcOpen.setFileFilter(new ImageFileFilter());
		
        int returnVal = fcOpen.showOpenDialog(windowToControl.btnOpenFile);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	imgToOpen = fcOpen.getSelectedFile();
        }
        */
	}
	
	/**
	 * converts the chosen file to a Image Processor
	 * @return the corresponding image processor
	 */
	private ImageProcessor makeImageFromFile() {
		ImagePlus imgAsPlus = new ImagePlus(imgToOpen.getAbsolutePath());
		return imgAsPlus.getProcessor();
	}
	
	/**
	 * Opens the file chooser to choose a location for the excel file
	 */
	public void openFileChooserForSaving() {
		// TODO: Add this file chooser
	}
	
	/**
	 * sets the Numbers from the textfields for the preprocessing order
	 * @return true if all textfields contained a number
	 */
	private boolean setPreprocessingNumbers() {
		if(checkIfStringIsNumber(windowToControl.txtContrastNumber.getText())) {
			contrastNumber = Integer.parseInt(windowToControl.txtContrastNumber.getText());
		} else {
			IJ.error("The Number for Contrast is not a Number!");
			return false;
		}
		
		if(checkIfStringIsNumber(windowToControl.txtGrayscaleNumber.getText())) {
			grayscaleNumber = Integer.parseInt(windowToControl.txtGrayscaleNumber.getText());
		} else {
			IJ.error("The Number for Grayscale is not a Number!");
			return false;
		}
		
		if(checkIfStringIsNumber(windowToControl.txtMedianNumber.getText())) {
			medianNumber = Integer.parseInt(windowToControl.txtMedianNumber.getText());
		} else {
			IJ.error("The Number for Median is not a Number!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * gets called when the start button is pressed
	 */
	public void reactToStartButton() {
		isPreprocessing = windowToControl.rbPreprocessing.isSelected();
		
		if(imgToOpen == null || !imgToOpen.exists()) {
			IJ.error("No existing image to open was chosen");
			return;
		}
		
		/*
		if(placeToSave == null || !placeToSave.exists()) {
			IJ.error("No existing place to save was chosen");
			return;
		}
		*/
		
		if(isPreprocessing) {
			if(!setPreprocessingNumbers() || !checkIfPreproccessingValid() || !checkPreprocessingOrder()) {
				return;
			}
			
			for(int orderCounter = 1; orderCounter < 4; orderCounter++) {
				if(orderCounter == contrastNumber) {
					Abschlussprojekt_PlugIn.chooseMethod(Abschlussprojekt_PlugIn.CONTRAST, makeImageFromFile());
				}
				
				if(orderCounter == grayscaleNumber) {
					Abschlussprojekt_PlugIn.chooseMethod(Abschlussprojekt_PlugIn.GRAYSCALE, makeImageFromFile());
				}
				
				if(orderCounter == medianNumber) {
					Abschlussprojekt_PlugIn.chooseMethod(Abschlussprojekt_PlugIn.MEDIAN, makeImageFromFile());
				}
			}
		} else {
			if(!setMethodNumber()) {
				return;
			}
			
			Abschlussprojekt_PlugIn.chooseMethod(methodNumber, makeImageFromFile());
		}
	}
	
	/**
	 * sets the Method Number to the value from its textfield
	 * @return true if everything is okay
	 */
	private boolean setMethodNumber() {
		if(checkIfStringIsNumber(windowToControl.txtMethodNumber.getText())) {
			methodNumber = Integer.parseInt(windowToControl.txtMethodNumber.getText());
			if(methodNumber < 1 || methodNumber > Abschlussprojekt_PlugIn.METHOD_AMOUNT) {
				IJ.error("The Method Number should be a value between 1 and " + Abschlussprojekt_PlugIn.METHOD_AMOUNT + "!");
				return false;
			}
		} else {
			IJ.error("The Method Number should be an int value!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * checks if all the preprocessing numbers have different values
	 * @return true if the numbers are different
	 */
	private boolean checkPreprocessingOrder() {		
		if(contrastNumber == grayscaleNumber || contrastNumber == medianNumber || medianNumber == grayscaleNumber) {
			IJ.error("The Preprocessing methods need to have different numbers!");
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * checks if all preprocessing numbers are valid
	 * @return true if all preprocessing numbers are in the given area
	 */
	private boolean checkIfPreproccessingValid() {
		if(!checkForPreprocessingTextFields(contrastNumber, "Contrast", 0, 3)) {
			return false;
		}
		
		if(!checkForPreprocessingTextFields(grayscaleNumber, "Grayscale", 0, 3)) {
			return false;
		}
		
		if(!checkForPreprocessingTextFields(medianNumber, "Median", 0, 3)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * checks for a single field if it contains a valid number
	 * @param methodNumber value of the textField
	 * @param currentMethod name of the method (for the error)
	 * @param low lower bound of the area
	 * @param high higher bound of the area
	 * @return true if the number is valid
	 */
	private boolean checkForPreprocessingTextFields(int methodNumber, String currentMethod, int low, int high) {
		if(methodNumber < low || methodNumber > high) {
			IJ.error("The Number for " + currentMethod + " should be an int value between " + low + " and " + high + "!");
			return false;
		} 
		
		return true;	
	}
	
	/**
	 * checks if a given string can be parsed to a number
	 * @param toCheck string to check
	 * @return true if it can be parsed
	 */
	private boolean checkIfStringIsNumber(String toCheck) {
		try {
			Integer.parseInt(toCheck);
		} catch (NumberFormatException nfEx) {
			return false;
		}
		return true;
	}
}