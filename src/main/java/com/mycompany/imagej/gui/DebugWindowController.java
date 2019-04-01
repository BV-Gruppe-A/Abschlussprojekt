package com.mycompany.imagej.gui;

import java.io.File;
import javax.swing.JFileChooser;
import com.mycompany.imagej.Classificator;
import com.mycompany.imagej.Segmentation;
import com.mycompany.imagej.gui.filefilters.FilterForImageDirectory;
import com.mycompany.imagej.gui.filefilters.FilterForFileChooser;
import com.mycompany.imagej.preprocessing.ContrastAdjustment;
import com.mycompany.imagej.preprocessing.Grayscale;
import com.mycompany.imagej.preprocessing.ShadingFilter;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Controller, which controls everything that happens in the main Window
 */
public class DebugWindowController {
	// constant values
	public final int METHOD_AMOUNT = 7;
	public final int CONTRAST = 1;
	public final int SHADING = 2;
	public final int GRAYSCALE = 3;
	public final int NO4 = 4;
	public final int SEGMENTATION = 5;
	public final int CLASSIFICATION = 6;
	public final int EVERYTHING = 7;
	
	public final int AVRG_IMAGE_WIDTH = 295;
	
	// local objects
	private DebugWindow windowToControl;
	private File imgOrFolderToOpen;
	private File placeToSave;
	private ImageProcessor currentImage;
	
	private ContrastAdjustment cont = new ContrastAdjustment();
	private Grayscale gray = new Grayscale();
	private ShadingFilter shading = new ShadingFilter();
	private Segmentation segm = new Segmentation();
	private Classificator classificator = new Classificator();
	
	// local variables
	private int contrastNumber;
	private int grayscaleNumber;
	private int shadingNumber;
	private int methodNumber;	
	private boolean isPreprocessing = true;
	private boolean isOneFile = true;
	
	/**
	 * Constructor, which sets the window-object to control
	 * @param window window to control
	 */
	public DebugWindowController(DebugWindow window) {
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
		return currentImage;
	}
	
	/**
	 * sets the current Image Processor to the given one
	 * @param imageToSet new Image Processor to set
	 */
	public void setCurrentImageProcessor(ImageProcessor imageToSet) {
		currentImage = imageToSet.resize(AVRG_IMAGE_WIDTH);
	}
	
	/**
	 * opens a file chooser for a single image
	 */
	private void openFileChooserLoadingSingleImage() {
		JFileChooser fcOpen = new JFileChooser();
		fcOpen.setFileFilter(new FilterForFileChooser(true));
		
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
        	classificator.setCsvName(placeToSave.getAbsolutePath(), false);
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
		
		if(placeToSave == null) {
			IJ.error("No existing place to save was chosen");
			return;
		}		
		
		if(isOneFile) {
			startProcessForOneImage(removeFileExtension(imgOrFolderToOpen.getName()));
		} else {
			for(File currentImage : imgOrFolderToOpen.listFiles(new FilterForImageDirectory())) {
				ImagePlus imgAsPlus = new ImagePlus(currentImage.getAbsolutePath());
	        	setCurrentImageProcessor(imgAsPlus.getProcessor());
	        	startProcessForOneImage(removeFileExtension(currentImage.getName()));
			}
		}		
	}
	
	/**
	 * removes the file extension from the given file name
	 * @param nameWithExtension name to remove the extension from
	 * @return filename without an extension
	 */
	private String removeFileExtension(String nameWithExtension) {
		int dotIndex = nameWithExtension.lastIndexOf('.');

        if (dotIndex > 0 &&  dotIndex < nameWithExtension.length() - 1) {
            return nameWithExtension.substring(0, dotIndex);
        }
        
        return nameWithExtension;
	}
	
	/**
	 * starts a chosen process for one specific image
	 * @param imageName name of the file which is processsed
	 */
	private void startProcessForOneImage(String imageName) {
		if(isPreprocessing) {
			if(!setPreprocessingNumbers() || !checkIfPreproccessingValid() || !checkPreprocessingOrder()) {
				return;
			}
			
			for(int orderCounter = 1; orderCounter < 4; orderCounter++) {
				if(orderCounter == contrastNumber) {
					chooseMethod(CONTRAST, imageName);
				}
				
				if(orderCounter == grayscaleNumber) {
					chooseMethod(GRAYSCALE, imageName);
				}
				
				if(orderCounter == shadingNumber) {
					chooseMethod(SHADING, imageName);
				}
			}
		} else {
			if(!setMethodNumber()) {
				return;
			}
			
			chooseMethod(methodNumber, imageName);
		}
	}
	
	 /**
     * Chooses a method to open
     * @param chosenMethod number of the method
     * 			1 - Contrast Adjustement
     * 			2 - SHADING Filter
     * 			3 - Grayscale
     * 			4 - ???
     * 			5 - Segmentation
     * 			6 - Classification
     * 			7 - Everything combinde
     * @param imageName Name of the current image
     */
    private void chooseMethod(int chosenMethod, String imageName) {
    	// Hard border for contrast adjustment
    	int PercentageBlack = 5;
    	int PercentageWhite = 5;  
    	int BinarizationWhite = 65;
    	
    	switch(chosenMethod) {
        case CONTRAST:              	
        	cont.Contrast(getCurrentImageProcessor(), PercentageWhite, PercentageBlack);
        	
        	break;
        	
        case SHADING:
           	setCurrentImageProcessor(shading.shading(getCurrentImageProcessor()));          
        	cont.Binarization(getCurrentImageProcessor(), BinarizationWhite);            
        	
            break;
              
        case GRAYSCALE:
        	//RGB Bild in Grauwert Bild umwandeln
            setCurrentImageProcessor(gray.Grayscale_function(getCurrentImageProcessor()));    
            
        	break;
        	
        case NO4:
        	
        	break;
        	
        case SEGMENTATION:
        	// DEBUG - only usable with a binarised input image!
        	segm.resetValuesForNewImage();
        	segm.debugSegmentation();
        	
        	break;
        	
        case CLASSIFICATION:
        	segm.resetValuesForNewImage();
        	classificator.classify(segm.segmentThePicture(), imageName);
        	
        	break;
            
        case EVERYTHING:
        	setCurrentImageProcessor(gray.Grayscale_function(getCurrentImageProcessor()));   
        	cont.Contrast(getCurrentImageProcessor(), PercentageWhite, PercentageBlack);
        	setCurrentImageProcessor(shading.shading(getCurrentImageProcessor()));           	
        	cont.Binarization(getCurrentImageProcessor(), BinarizationWhite);    
        	segm.resetValuesForNewImage();
        	classificator.classify(segm.segmentThePicture(), imageName);   	
        	break;        	
        default: 
        	
    	}  
    	
    	ImagePlus imgToShow = new ImagePlus(imageName, getCurrentImageProcessor());
    	imgToShow.show();
    }
	
	/**
	 * sets the Method Number to the value from its textfield
	 * @return true if everything is okay
	 */
	private boolean setMethodNumber() {
		if(checkIfStringIsNumber(windowToControl.txtMethodNumber.getText())) {
			methodNumber = Integer.parseInt(windowToControl.txtMethodNumber.getText());
			if(methodNumber < 1 || methodNumber > METHOD_AMOUNT) {
				IJ.error("The Method Number should be a value between 1 and " + METHOD_AMOUNT + "!");
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