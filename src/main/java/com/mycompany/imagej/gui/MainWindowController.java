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
public class MainWindowController {
	/**
	 * value to which the width of a picture is scaled
	 * found in most example images
	 */
	public final int AVRG_IMAGE_WIDTH = 295;
	
	/**
	 * the Main Window which should be controlled
	 */
	private MainWindow windowToControl;
	
	/**
	 * the file or folder which should be opened
	 */
	private File imgOrFolderToOpen;
	
	/**
	 * the file in which the results should be saved
	 */
	private File fileToSave;
	
	/**
	 * current Image to process
	 */
	private ImageProcessor currentImage;
	
	/**
	 * class for contrast adjustement
	 */
	private ContrastAdjustment cont = new ContrastAdjustment();
	
	/**
	 * class for everything grayscale related
	 */
	private Grayscale gray = new Grayscale();
	
	/**
	 * class for shading
	 */
	private ShadingFilter shading = new ShadingFilter();
	
	/**
	 * class for segmentation
	 */
	private Segmentation segm = new Segmentation();
	
	/**
	 * class for the classification
	 */
	private Classificator classificator = new Classificator();
	
	/**
	 * true if only one image should be opened
	 */
	private boolean isOneFile = true;
	
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
		fcSave.setFileFilter(new FilterForFileChooser(false));
		
		int returnVal = fcSave.showSaveDialog(windowToControl.btnSaveFile);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	fileToSave = fcSave.getSelectedFile();
        	windowToControl.txtSaveLocation.setText(fileToSave.getPath());
        }
	}
	
	/**
	 * gets called when the start button is pressed
	 */
	public void reactToStartButton() {			
		if(imgOrFolderToOpen == null || !imgOrFolderToOpen.exists() 
				&& !imgOrFolderToOpen.isDirectory()) {
			IJ.error("No existing image (or directory) to open was chosen");
			return;
		}
		
		if(fileToSave == null) {
			IJ.error("No existing place to save was chosen");
			return;
		}	
		
		classificator.setCsvName(fileToSave.getAbsolutePath(),
				windowToControl.ckbClearFile.isSelected());
		
		if(isOneFile) {
			startProcessForOneImage(removeFileExtension(imgOrFolderToOpen.getName()));
		} else {
			File[] allFilesToOpen = imgOrFolderToOpen.listFiles(new FilterForImageDirectory());
			
			if(allFilesToOpen.length == 0) {
				IJ.error("The chosen directory does not contain any images!");
				return;
			}
			
			for(File currentImage : allFilesToOpen) {
				ImagePlus imgAsPlus = new ImagePlus(currentImage.getAbsolutePath());
	        	setCurrentImageProcessor(imgAsPlus.getProcessor());
	        	startProcessForOneImage(removeFileExtension(currentImage.getName()));       	
			}			
		}
		
		if(windowToControl.ckbEvaluation.isSelected()) {
			classificator.evaluation();
		}		

		// TODO: find better way to inform user
		IJ.error("Processing finished!");
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
		int PercentageBlack = 5;
    	int PercentageWhite = 5;  
    	int BinarizationWhite = 65;
		
    	setCurrentImageProcessor(gray.Grayscale_function(getCurrentImageProcessor()));   
    	cont.Contrast(getCurrentImageProcessor(), PercentageWhite, PercentageBlack);
    	setCurrentImageProcessor(shading.shading(getCurrentImageProcessor()));           	
    	cont.Binarization(getCurrentImageProcessor(), BinarizationWhite);    
    	segm.changeImage(getCurrentImageProcessor());
    	classificator.classify(segm.segmentThePicture(), imageName);   	
    	
    	/*
    	ImagePlus imgToShow = new ImagePlus(imageName, getCurrentImageProcessor());
    	imgToShow.show();    
    	*/
	}
}