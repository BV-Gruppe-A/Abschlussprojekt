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
        	// Hard border for contrast adjustment
        	int PercentageBlack = 35;
        	int PercentageWhite = 65;
        	
        	ContrastAdjustment cont = new ContrastAdjustment();
        	cont.Contrast(ip,PercentageWhite,PercentageBlack);
        	break;

        	
        //Case 3 und danach Case 1 ausführen
        case 2:
            Grayscale gray1 = new Grayscale();
            ip = gray1.Grayscale_function(ip);
        	ip.convertToByteProcessor(false);
        	new ImagePlus("GrayscaledImage",ip).show();
        	
        	int PercentageBlack1 = 35;
        	int PercentageWhite1 = 65;
        	
        	ContrastAdjustment cont1 = new ContrastAdjustment();
        	cont1.Contrast(ip,PercentageWhite1,PercentageBlack1);
        	
            MedianFilter filt1 = new MedianFilter();
            ip = filt1.median(ip);        
            
            break;
            
        //Baustelle Julian 1   
        case 3:
        	//RGB Bild in Grauwert Bild umwandeln
            Grayscale gray = new Grayscale();
            ip = gray.Grayscale_function(ip);
        	ip.convertToByteProcessor(false);
        	new ImagePlus("GrayscaledImage",ip).show();
        	
        	break;
        	
        //Baustelle Julian 2	
        case 4:
        	
        	break;
        	
        //Alissa
        case 5:
        	Classificator classificator = new Classificator();
        	classificator.classify(null, "Kennzeichen1");
        	break;
        	
        //Janfi
        case 6:
        	// DEBUG - only usable with a binarised input image!
        	Segmentation segm = new Segmentation(ip);
        	// segm.segmentThePicture();
        	segm.debugSegmentation();
        	break;
        	
        case 7:
        	//Median Filter
            MedianFilter filt = new MedianFilter();
            ip = filt.median(ip);        	
        	
        	break;
            
        default: 
        	
    	}  
    	
    	ImagePlus imgToShow = new ImagePlus("Test", ip);
    	imgToShow.show();
    }
}