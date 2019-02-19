package com.mycompany.imagej;

import ij.process.ImageProcessor;

public class Grayscale {

	final int BLACK = 0;
	final int WHITE = 255;
	 
	// maximal vector length in the RGB color space if red = green = blue = 255
	final double MAX_VECTOR_LENGTH = Math.sqrt(3 * Math.pow(WHITE, 2));
	
	// masks to get the 8-Bit color value from the whole 32-Bit int
	final int RED_MASK = 0x00FF0000;
	final int GREEN_MASK = 0x0000FF00;
	final int BLUE_MASK = 0x000000FF;
	
	public Grayscale(ImageProcessor ip) {
		int M = ip.getWidth();
        int N = ip.getHeight();
        
        for (int u = 0; u < M; u++) {
            for (int v = 0; v < N; v++) {
            	int color = ip.getPixel(u, v);            	
            	int new_p = calculateIntensity(getRGBValues(color));               	
        		ip.putPixel(u, v, new_p);            	
            }
        }
	}
	// splits the color-value into the 3 resulting colors (RGB)
    private int[] getRGBValues(int color) {
    	int red = (color & RED_MASK) >> 16;
    	int green = (color & GREEN_MASK) >> 8;
    	int blue = color & BLUE_MASK;
    	
    	int[] rgb = {red, green, blue};
    	return rgb;
    }
	
	// calculates the grey value as the length of the vector in the RGB color space
    private int calculateIntensity(int[] rgbValues) {
    	double redSquared =  Math.pow(rgbValues[0], 2);
    	double greenSquared =  Math.pow(rgbValues[1], 2);
    	double blueSquared =  Math.pow(rgbValues[2], 2);
    	
    	double intensity =  Math.sqrt(redSquared + greenSquared + blueSquared);
    	int grey = (int) Math.round((intensity * (double) WHITE) / MAX_VECTOR_LENGTH);
    	
    	return makeGreyToRGBValue(grey);
    }
    
    // saves the grey value as a valid rgb value by setting r = g = b = grey
    private int makeGreyToRGBValue(int grey) {
    	return ((grey & 0xff) << 16) | ((grey & 0xff) << 8) | grey & 0xff;
    }
    
	
}
