package com.mycompany.imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Abschlussprojekt_PlugIn implements PlugInFilter {
    
	final int BLACK = 0;
	final int WHITE = 255;
	

    @Override    
    public int setup(String args, ImagePlus im) {  
    	// this plugin accepts 8-bit greyscales
        return DOES_8G; 
    }

    @Override
    public void run(ImageProcessor ip) { 
    }
}






















// Torsten versucht die kontrastanpassung mal umzumodeln:
// Kontrastanpassung
public void run(ImageProcessor ip) {
    int M = ip.getWidth();
    int N = ip.getHeight();
    
    histogram = ip.getHistogram();
    
    int minThreshold = BLACK;
    int maxThreshold = WHITE;  
    
    if(getAndCheckPercentage()) {
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
    } else {
    	// use normal linear contrast modification  
    	getAndCheckValues();
    	    	
    	maxThreshold = getMaximalValue();
    	minThreshold = getMinimalValue();
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