package com.mycompany.imagej.gui;

import java.io.File;
import javax.swing.JFileChooser;
import com.mycompany.imagej.Classificator;
import com.mycompany.imagej.Segmentation;
import com.mycompany.imagej.gui.filefilters.ImgFilterDirectory;
import com.mycompany.imagej.gui.filefilters.ImgFilterFileChooser;
import com.mycompany.imagej.gui.filefilters.ResultFileFilter;
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
	// constant values	
	public final int AVRG_IMAGE_WIDTH = 295;
	
	// local objects
	private MainWindow windowToControl;
	private File imgOrFolderToOpen;
	private File placeToSave;
	private ImageProcessor currentImage;
	
	private ContrastAdjustment cont = new ContrastAdjustment();
	private Grayscale gray = new Grayscale();
	private ShadingFilter shading = new ShadingFilter();
	private Segmentation segm = new Segmentation();
	private Classificator classificator = new Classificator();
	
	// local variables
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
		fcSave.setFileFilter(new ResultFileFilter());
		
		int returnVal = fcSave.showSaveDialog(windowToControl.btnSaveFile);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	placeToSave = fcSave.getSelectedFile();
        	windowToControl.txtSaveLocation.setText(placeToSave.getPath());
        	classificator.setCsvName(placeToSave.getAbsolutePath());
        }
	}
	
	/**
	 * gets called when the start button is pressed
	 */
	public void reactToStartButton() {			
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
			for(File currentImage : imgOrFolderToOpen.listFiles(new ImgFilterDirectory())) {
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