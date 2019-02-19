package com.mycompany.imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Abschlussprojekt_PlugIn implements PlugInFilter {

    @Override    
    public int setup(String args, ImagePlus im) {  
    	//this plugin accepts RGB images
        return DOES_RGB; 
        
    	// DEBUG FOR JANFI
    	//return DOES_8G;
    }

    @Override
    public void run(ImageProcessor ip) { 
    	
    	int whichMethod = (int)IJ.getNumber("Welche Methode soll aufgerufen werden?", 1);
    	
    	switch(whichMethod) {
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
        //FBaustelle Julian 2	
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
  }
}
