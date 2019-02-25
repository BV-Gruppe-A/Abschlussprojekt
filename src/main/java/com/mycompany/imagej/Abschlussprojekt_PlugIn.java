package com.mycompany.imagej;

import com.mycompany.imagej.gui.MainWindow;

import ij.ImagePlus;
import ij.plugin.frame.PlugInFrame;
import ij.process.ImageProcessor;

@SuppressWarnings("serial")
public class Abschlussprojekt_PlugIn extends PlugInFrame {
	private final String PLUGIN_NAME = "Abschlussprojekt Gruppe A"; 

	public Abschlussprojekt_PlugIn() {
		super("Abschlussprojekt Gruppe A");
	}
	
	
    public void run(String arg) {     	
    	MainWindow window = new MainWindow(PLUGIN_NAME);
    	window.showDialog();
    }
    
    public static void chooseMethod(int chosenMethod, ImageProcessor ip) {
    	switch(chosenMethod) {
        // Baustelle Torsten 1
        case 1:      	
        	ContrastAdjustment cont = new ContrastAdjustment();
        	cont.Contrast(ip);
        	break;
        	
        //Baustelle Torsten 2
        case 2:
        	
            break;
            
        //Baustelle Julian 1    
        case 3:
        	//RGB Bild in Grauwerten 
        	Grayscale gray = new Grayscale(ip);
        	//Konvertierung in Grauwertbild funktioniert noch nicht
        	ip.convertToByteProcessor(false);
        	break;
        	
        //Baustelle Julian 2	
        case 4:
        	
        	break;
        	
        //Alissa
        case 5:
        	
        	break;
        	
        //Janfi
        case 6:
        	// DEBUG - only usable with a binarised input image!
        	Segmentation segm = new Segmentation(ip);
        	// segm.segmentThePicture();
        	segm.debugSegmentation();
        	break;
            
        default: 
        	
    	}  
    	
    	ImagePlus imgToShow = new ImagePlus("Test", ip);
    	imgToShow.show();
    }
}