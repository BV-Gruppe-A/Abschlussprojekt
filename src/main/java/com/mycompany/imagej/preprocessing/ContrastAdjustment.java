package com.mycompany.imagej.preprocessing;

import ij.process.ImageProcessor;


public class ContrastAdjustment {
	
	final int BLACK = 0;
	final int WHITE = 255;
	
    
	public void Contrast(ImageProcessor ip, int PercentageWhite, int PercentageBlack) {
	    int M = ip.getWidth();
	    int N = ip.getHeight();
	    
        // the contrast should be expanded to the whole range of greyscales
	    int color_max = WHITE;
	    int color_min = BLACK;

	    int[] histogram;
	    

	   
	    histogram = ip.getHistogram();
	    
	    int minThreshold = BLACK;
	    int maxThreshold = WHITE;  
	    
	
	        // calculate amount of pixels needed to reach x%
	        int pixelAmountLow = (int) Math.ceil(ip.getPixelCount() * PercentageBlack / 100.0);
	        int pixelAmountHigh = (int) Math.ceil(ip.getPixelCount() * PercentageWhite / 100.0);
	        
	        // the values that need to be colored in min/max values in the next step
	        minThreshold = BLACK;
	        maxThreshold = WHITE;	
	        
	        
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
	
	
	
	
	public void Binarization(ImageProcessor ip, int PercentageWhite) {
	    int M = ip.getWidth();
	    int N = ip.getHeight();

	    int[] histogram;

	   
	    histogram = ip.getHistogram();
	    

	    int maxThreshold = WHITE;  
	    
	
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
	    for (int u = 0; u < M; u++) {
	        for (int v = 0; v < N; v++) {
	            int p = ip.getPixel(u, v);
	            
	            if(p < maxThreshold) {
	            	new_p  = BLACK;
	            	
	            } else {
	            	new_p =  WHITE;
	            } 
	        
	            ip.putPixel(u, v, new_p);
	        }
	    }
	}
}
