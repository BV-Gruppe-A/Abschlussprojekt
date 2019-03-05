package com.mycompany.imagej;

import com.mycompany.imagej.gui.MainWindow;
import com.mycompany.imagej.preprocessing.ContrastAdjustment;
import com.mycompany.imagej.preprocessing.Grayscale;
import com.mycompany.imagej.preprocessing.ShadingFilter;

import ij.ImagePlus;
import ij.plugin.frame.PlugInFrame;
import ij.process.ImageProcessor;
           
/**
 * Main Class for our PlugIn
 */
@SuppressWarnings("serial")
public class Abschlussprojekt_PlugIn extends PlugInFrame {
	// constant values
	private static final String PLUGIN_NAME = "Abschlussprojekt Gruppe A"; 
	
	// constant static values
	public static final int METHOD_AMOUNT = 6;
	public static final int CONTRAST = 1;
	public static final int SHADING = 2;
	public static final int GRAYSCALE = 3;
	public static final int NO4 = 4;
	public static final int SEGMENTATION = 5;
	public static final int CLASSIFICATION = 6;
	
	// local objects
	private static MainWindow window = new MainWindow(PLUGIN_NAME);;
	

	/**
	 * Constructor which calls the super constructor with the given applet name
	 */
	public Abschlussprojekt_PlugIn() {
		super("Abschlussprojekt Gruppe A");
	}
	
	/**
	 * run-Method which shows the main Window
	 */
    public void run(String arg) {   	
       	window.showDialog();
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
     * @param ip Image Processor to use the method on
     * @param shouldChangeProcess true if the method should change the controllers image processor
     */
    public static void chooseMethod(int chosenMethod, ImageProcessor ip, boolean shouldChangeProcess) {
    	switch(chosenMethod) {
        // Baustelle Torsten 1
        case CONTRAST:      	
        	// Hard border for contrast adjustment
        	int PercentageBlack = 5;
        	int PercentageWhite = 5;
        	
        	ContrastAdjustment cont = new ContrastAdjustment();
        	cont.Contrast(ip, PercentageWhite, PercentageBlack);
        	break;
        	
        case SHADING:
            ShadingFilter filt1 = new ShadingFilter();
            if(shouldChangeProcess) {
            	window.getController().setCurrentImageProcessor(filt1.shading(ip));
            } else {
            	ip = filt1.shading(ip);
            }
            
        	int BinarizationWhite = 65;
        	
        	ContrastAdjustment bin = new ContrastAdjustment();
        	bin.Binarization(ip, BinarizationWhite);
            
            break;
            
        //Baustelle Julian 1   
        case GRAYSCALE:
        	//RGB Bild in Grauwert Bild umwandeln
            Grayscale gray = new Grayscale();
            if(shouldChangeProcess) {
            	window.getController().setCurrentImageProcessor(gray.Grayscale_function(ip));
            } else {
            	ip = gray.Grayscale_function(ip);
            }
            
        	break;
        	
        //Baustelle Julian 2	
        case NO4:
        	
        	break;
        	
        case SEGMENTATION:
        	// DEBUG - only usable with a binarised input image!
        	Segmentation segm = new Segmentation(ip);
        	// segm.segmentThePicture();
        	segm.debugSegmentation();
        	
        	break;
        	
        case CLASSIFICATION:
        	Segmentation segm1 = new Segmentation(ip);
        	//ImageProcessor[] image = new ImageProcessor[1];
        	//image[0] = ip;
        	Classificator classificator = new Classificator();
        	classificator.classify(segm1.segmentThePicture(), "Kennzeichen1");
        	
        	break;
            
        default: 
        	
    	}  
    	
    	ImagePlus imgToShow = new ImagePlus("img", ip);
    	imgToShow.show();
    }
}