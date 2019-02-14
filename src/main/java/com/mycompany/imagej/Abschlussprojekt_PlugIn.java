package com.mycompany.imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Abschlussprojekt_PlugIn implements PlugInFilter {
    
	final int BLACK = 0;
	final int WHITE = 255;
	 
	// maximal vector length in the RGB color space if red = green = blue = 255
	final double MAX_VECTOR_LENGTH = Math.sqrt(3 * Math.pow(WHITE, 2));
	
	// masks to get the 8-Bit color value from the whole 32-Bit int
	final int RED_MASK = 0x00FF0000;
	final int GREEN_MASK = 0x0000FF00;
	final int BLUE_MASK = 0x000000FF;
	
	// the weights are based on the values descriped in the book on page 324
	final double WEIGHT_RED = 0.299;
	final double WEIGHT_GREEN = 0.587;
	final double WEIGHT_BLUE = 0.114;

    @Override    
    public int setup(String args, ImagePlus im) {  
    	// this plugin accepts RGB images
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
        	
            break;
        //Baustelle Torsten 2
        case 2:
        	
            break;
        //Baustelle Julian 1    
        case 3:
        	//public void convertToGrayscale(ImageProcessor ip);
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
        	Segmentation segm = new Segmentation();
        	segm.addPixelsToSegment(ip);
        	break;
            
        default:   
    
    }
  }
}