package com.mycompany.imagej.preprocessing;

import com.mycompany.imagej.Abschlussprojekt_PlugIn;

import ij.process.ImageProcessor;


public class ContrastAdjustment {    
	
	public void Contrast(ImageProcessor ip, int PercentageWhite, int PercentageBlack) {  //name contrast ?
		
	    int width = Abschlussprojekt_PlugIn.getCurrentWidth();
	    int height =  Abschlussprojekt_PlugIn.getCurrentHeight();
	    
        // the contrast should be expanded to the whole range of greyscales
	    int colorMax = Abschlussprojekt_PlugIn.WHITE;
	    int colorMin = Abschlussprojekt_PlugIn.BLACK;

	    int[] histogram;
	    

	   
	    histogram = ip.getHistogram();
	    
	    int minThreshold = Abschlussprojekt_PlugIn.BLACK;
	    int maxThreshold = Abschlussprojekt_PlugIn.WHITE;  
	    
	
	        // calculate amount of pixels needed to reach x%
	        int pixelAmountLow = (int) Math.ceil(ip.getPixelCount() * PercentageBlack / 100.0);
	        int pixelAmountHigh = (int) Math.ceil(ip.getPixelCount() * PercentageWhite / 100.0);
	        
	        // the values that need to be colored in min/max values in the next step
	        minThreshold = Abschlussprojekt_PlugIn.BLACK;
	        maxThreshold = Abschlussprojekt_PlugIn.WHITE;	
	        
	        
	        // the amount of pixels in the lowest/highest values
	        int minSum = histogram[minThreshold];
	        int maxSum = histogram[maxThreshold];
	        
	        // add up lowest gray shades until pixel amount needed for x% is reached
	        while(minSum < pixelAmountLow) {
	        	minThreshold++;
	        	minSum += histogram[minThreshold];
	        }
	        
	        // add up highest gray shades until pixel amount needed for x% is reached
	        while(maxSum < pixelAmountHigh) {
	        	maxThreshold--;
	        	maxSum += histogram[maxThreshold];
	        }
	   
	
	    int new_p;
	    
	    // iterate over all image coordinates (u,v)
	    for (int u = 0; u < width; u++) {
	        for (int v = 0; v < height; v++) {
	            int p = ip.getPixel(u, v);
	            
	            if(p > minThreshold && p < maxThreshold) {
	            	//scale values linear over new range
	            	new_p = colorMin + (p - minThreshold) * (colorMax - colorMin) / (maxThreshold - minThreshold);	
	            } else {
	            	new_p = (p >= maxThreshold) ? colorMax : colorMin;
	            } 
	        
	            ip.putPixel(u, v, new_p);
	        }
	    }
	}
	
	
	
	
	public void Binarization(ImageProcessor ip, int PercentageWhite) {
		
		int width = Abschlussprojekt_PlugIn.getCurrentWidth();
	    int height =  Abschlussprojekt_PlugIn.getCurrentHeight();

	    int[] histogram;

	   
	    histogram = ip.getHistogram();
	    

	    int maxThreshold = Abschlussprojekt_PlugIn.WHITE;  
	    
	
	        // calculate amount of pixels needed to reach x%
	        int pixelAmountHigh = (int) Math.ceil(ip.getPixelCount() * PercentageWhite / 100);
	        
	        // the amount of pixels in the highest values
	        int maxSum = histogram[maxThreshold];
	        
	        // add up highest gray shades until pixel amount needed for x% is reached
	        while(maxSum < pixelAmountHigh) {
	        	maxThreshold--;
	        	maxSum += histogram[maxThreshold];
	        }
	   
	
	    int new_p;
	    
	    // iterate over all image coordinates (u,v)
	    for (int u = 0; u < width; u++) {
	        for (int v = 0; v < height ; v++) {
	            int p = ip.getPixel(u, v);
	            
	            if(p < maxThreshold) {
	            	new_p  = Abschlussprojekt_PlugIn.BLACK;
	            	
	            } else {
	            	new_p =  Abschlussprojekt_PlugIn.WHITE;
	            } 
	        
	            ip.putPixel(u, v, new_p);
	        }
	    }
	}
}
