package com.mycompany.imagej.gui;

import java.io.File;
import javax.swing.JFileChooser;
import com.mycompany.imagej.Abschlussprojekt_PlugIn;
import com.mycompany.imagej.gui.filefilters.ImgFilterDirectoryLoop;
import com.mycompany.imagej.gui.filefilters.ImgFilterFileChooser;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Controller, which controls everything that happens in the main Window
 */
public class MainWindowController {
	// local objects
	private MainWindow windowToControl;
	private File imgOrFolderToOpen;
	private File placeToSave;
	
	// local variables
	private int contrastNumber;
	private int grayscaleNumber;
	private int shadingNumber;
	private int methodNumber;	
	private boolean isPreprocessing = true;
	private boolean isOneFile = true;

	// local objects
	private ImageProcessor currentImage;
	
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
		isOneFile = windowToControl.rbOneImage.isSelected();
		
		if(isOneFile) {
			openFileChooserLoadingSingleImage();
		} else {
			openFileChooserLoadingImages();
		}
	}
	
	/**
	 * @return the current Image as an Image Processor
	 */
	public ImageProcessor getCurrentImageProcessor() {
		return currentImage.resize(295);
	}
	
	/**
	 * sets the current Image Processor to the given one
	 * @param imageToSet new Image Processor to set
	 */
	public void setCurrentImageProcessor(ImageProcessor imageToSet) {
		currentImage = imageToSet;
	}
	
	/**
	 * opens a file chooser for a single image
	 */
	private void openFileChooserLoadingSingleImage() {
		JFileChooser fcOpen = new JFileChooser();
		fcOpen.setFileFilter(new ImgFilterFileChooser());
		
        int returnVal = fcOpen.showOpenDialog(windowToControl.btnOpenFile);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	imgOrFolderToOpen = fcOpen.getSelectedFile();
        	windowToControl.txtOpenLocation.setText(imgOrFolderToOpen.getPath());
        	ImagePlus imgAsPlus = new ImagePlus(imgOrFolderToOpen.getAbsolutePath());
        	setCurrentImageProcessor(imgAsPlus.getProcessor());
        }
	}
	
	/**
	 * opens a file chooser for a whole folder
	 */
	private void openFileChooserLoadingImages() {
		JFileChooser fcOpen = new JFileChooser();
		fcOpen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
        int returnVal = fcOpen.showOpenDialog(windowToControl.btnOpenFile);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	imgOrFolderToOpen = fcOpen.getSelectedFile();   
        	windowToControl.txtOpenLocation.setText(imgOrFolderToOpen.getPath());
        }
	}
		
	/**
	 * Opens the file chooser to choose a location for the excel file
	 */
	public void openFileChooserForSaving() {
		JFileChooser fcSave = new JFileChooser();
		
		int returnVal = fcSave.showSaveDialog(windowToControl.btnSaveFile);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	placeToSave = fcSave.getSelectedFile();
        	windowToControl.txtSaveLocation.setText(placeToSave.getPath());
        	// TODO: give the file location to the classifier
        }
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
		
		if(checkIfStringIsNumber(windowToControl.txtShadingNumber.getText())) {
			shadingNumber = Integer.parseInt(windowToControl.txtShadingNumber.getText());
		} else {
			IJ.error("The Number for Shading is not a Number!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * gets called when the start button is pressed
	 */
	public void reactToStartButton() {
		isPreprocessing = windowToControl.rbPreprocessing.isSelected();
		
		if(imgOrFolderToOpen == null || !imgOrFolderToOpen.exists() && !imgOrFolderToOpen.isDirectory()) {
			IJ.error("No existing image to open was chosen");
			return;
		}
		
		/*
		if(placeToSave == null) {
			IJ.error("No existing place to save was chosen");
			return;
		}
		*/
		
		if(isOneFile) {
			startProcessForOneImage();
		} else {
			for(File currentImage : imgOrFolderToOpen.listFiles(new ImgFilterDirectoryLoop())) {
				ImagePlus imgAsPlus = new ImagePlus(currentImage.getAbsolutePath());
	        	setCurrentImageProcessor(imgAsPlus.getProcessor());
	        	startProcessForOneImage();
			}
		}		
	}
	
	/**
	 * starts a chosen process for one specific image
	 */
	private void startProcessForOneImage() {
		if(isPreprocessing) {
			if(!setPreprocessingNumbers() || !checkIfPreproccessingValid() || !checkPreprocessingOrder()) {
				return;
			}
			
			for(int orderCounter = 1; orderCounter < 4; orderCounter++) {
				if(orderCounter == contrastNumber) {
					Abschlussprojekt_PlugIn.chooseMethod(Abschlussprojekt_PlugIn.CONTRAST, getCurrentImageProcessor(), true);
				}
				
				if(orderCounter == grayscaleNumber) {
					Abschlussprojekt_PlugIn.chooseMethod(Abschlussprojekt_PlugIn.GRAYSCALE, getCurrentImageProcessor(), true);
				}
				
				if(orderCounter == shadingNumber) {
					Abschlussprojekt_PlugIn.chooseMethod(Abschlussprojekt_PlugIn.SHADING, getCurrentImageProcessor(), true);
				}
			}
		} else {
			if(!setMethodNumber()) {
				return;
			}
			
			Abschlussprojekt_PlugIn.chooseMethod(methodNumber, getCurrentImageProcessor(), false);
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
		boolean isValidOrder = true;
		
		if(contrastNumber == grayscaleNumber && contrastNumber != 0) {
			isValidOrder = false;
		}
		
		if(contrastNumber == shadingNumber && contrastNumber != 0) {
			isValidOrder = false;
		}
		
		if(shadingNumber == grayscaleNumber && shadingNumber != 0) {
			isValidOrder = false;
		}
		
		if(!isValidOrder) {
			IJ.error("The Preprocessing methods need to have different numbers!");
		}
		
		return isValidOrder;
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
		
		if(!checkForPreprocessingTextFields(shadingNumber, "Shading", 0, 3)) {
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