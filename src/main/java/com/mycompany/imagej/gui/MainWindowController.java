package com.mycompany.imagej.gui;

import java.io.File;
import javax.swing.JFileChooser;
import com.mycompany.imagej.Classificator;
import com.mycompany.imagej.Preprocessor;
import com.mycompany.imagej.Segmentator;
import com.mycompany.imagej.gui.filefilters.FilterForImageDirectory;
import com.mycompany.imagej.gui.filefilters.FilterForFileChooser;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * Controller, which controls everything that happens in the main Window
 */
public class MainWindowController {
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
	 * class for handling the preprocessing
	 */
	private Preprocessor preprocessor = new Preprocessor();
	
	/**
	 * class for segmentation
	 */
	private Segmentator segm = new Segmentator();
	
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
	 * opens a file chooser for a single image
	 */
	private void openFileChooserLoadingSingleImage() {
		JFileChooser fcOpen = new JFileChooser();
		fcOpen.setFileFilter(new FilterForFileChooser(true));
		
        int returnVal = fcOpen.showOpenDialog(windowToControl.btnOpenFile);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	imgOrFolderToOpen = fcOpen.getSelectedFile();
        	windowToControl.txtOpenLocation.setText(imgOrFolderToOpen.getPath());
        }
	}
	
	/**
	 * opens a file chooser to choose a whole folder
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
	 * gets called when the start button is pressed, checks selected values
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
        	ImagePlus imgAsPlus = new ImagePlus(imgOrFolderToOpen.getAbsolutePath());
			startProcessForOneImage(imgAsPlus.getProcessor(),
        			removeFileExtension(imgOrFolderToOpen.getName()));
		} else {
			File[] allFilesToOpen = imgOrFolderToOpen.listFiles(new FilterForImageDirectory());
			
			if(allFilesToOpen.length == 0) {
				IJ.error("The chosen directory does not contain any images!");
				return;
			}
			
			for(File currentImage : allFilesToOpen) {
				ImagePlus imgAsPlus = new ImagePlus(currentImage.getAbsolutePath());
	        	startProcessForOneImage(imgAsPlus.getProcessor(),
	        			removeFileExtension(currentImage.getName()));       	
			}			
		}
		
		if(windowToControl.ckbEvaluation.isSelected()) {
			classificator.evaluation();
		}		

		classificator.resetClassificator();
		IJ.showMessage("Info", "Processing finished!");
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
	 * @param imageName name of the file which is processed
	 */
	private void startProcessForOneImage(ImageProcessor imgToProcess, String imageName) {	
		preprocessor.preprocessing(imgToProcess);
		classificator.classify(segm.segmentThePicture(), imageName);  	 	
	}
}