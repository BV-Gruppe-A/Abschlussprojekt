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
	
	// the weights are based on the values described in the book on page 324
	final double WEIGHT_RED = 0.299;
	final double WEIGHT_GREEN = 0.587;
	final double WEIGHT_BLUE = 0.114;

    @Override    
    public int setup(String args, ImagePlus im) {  
    	// this plugin accepts RGB images
        return DOES_RGB; 
        
    	// DEBUG FOR JANFI
    	// return DOES_8G;
    }

    @Override
    public void run(ImageProcessor ip) { 
    	
    	int whichMethod = (int)IJ.getNumber("Welche Methode soll aufgerufen werden?", 1);
    	
    	switch(whichMethod) {
        // Baustelle Torsten 1
        case 1:      	
        	Contrast(ip);
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
        	Segmentation segm = new Segmentation(ip);
        	// segm.segmentThePicture();
        	segm.debugSegmentation();
        	break;
            
        default:   
    
    }
  }












	public void Contrast(ImageProcessor ip) {
	    int M = ip.getWidth();
	    int N = ip.getHeight();
	    
	    int color_min, color_max;
	    int[] histogram;
	    
	    // Hard border for contrast adjustment
	    int percentage = 5;
	    
	    histogram = ip.getHistogram();
	    
	    int minThreshold = BLACK;
	    int maxThreshold = WHITE;  
	    
	
	        // calculate amount of pixels needed to reach x%
	        int pixelAmount = (int) Math.ceil(ip.getPixelCount() * percentage / 100.0);
	        
	        // the values that need to be colored in min/max values in the next step
	        minThreshold = BLACK;
	        maxThreshold = WHITE;	
	        
	        // the contrast should be expanded to the whole range of greyscales
	        color_max = WHITE;
	        color_min = BLACK;
	        
	        // the amount of pixels in the lowest/highest values
	        int minSum = histogram[minThreshold];
	        int maxSum = histogram[maxThreshold];
	        
	        // add up lowest gray shades until pixel amount needed for x% is reached
	        while(minSum < pixelAmount) {
	        	minThreshold++;
	        	minSum += histogram[minThreshold];
	        }
	        
	        // add up highest gray shades until pixel amount needed for x% is reached
	        while(maxSum < pixelAmount) {
	        	maxThreshold--;
	        	maxSum += histogram[maxThreshold];
	        }
	   
	
	    int new_p;
	    
	    // iterate over all image coordinates (u,v)
	    for (int u = 0; u < M; u++) {
	        for (int v = 0; v < N; v++) {
	            int p = ip.getPixel(u, v);
	            
	            if(p > minThreshold && p < maxThreshold) {
	            	//scale values linear over new range
	            	new_p = color_min + (p - minThreshold) * (color_max - color_min) / (maxThreshold - minThreshold);	
	            } else {
	            	new_p = (p >= maxThreshold) ? color_max : color_min;
	            } 
	        
	            ip.putPixel(u, v, new_p);
	        }
	    }
	}
}