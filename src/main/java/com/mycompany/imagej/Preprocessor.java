package com.mycompany.imagej;

import java.util.Arrays;

import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * This class can be used to preprocess a RGB picture of a license plate to a binarized image with as little 
 * noise as possible. Therefore it is converted to grayscale, gets a contrast adjustment and a correction of its shade
 * and is binarized via thresholding.
 */
public class Preprocessor {
	/**
	 * maximal vector length in the RGB color space if red = green = blue = 255
	 */
	private final double MAX_VECTOR_LENGTH = Math.sqrt(3 * Math.pow(Abschlussprojekt_PlugIn.WHITE, 2));
	
	/**
	 * masks to get the 8-Bit color value for red from the whole 32-Bit int
	 */ 
	private final int RED_MASK = 0x00FF0000;
	
	/**
	 * masks to get the 8-Bit color value for green from the whole 32-Bit int
	 */ 
	private final int GREEN_MASK = 0x0000FF00;
	
	/**
	 * masks to get the 8-Bit color value for blue from the whole 32-Bit int
	 */ 
	private final int BLUE_MASK = 0x000000FF;
	
	/**
	 * percentage of pixels which should be colored black or white during the contrast adjustment 
	 */
	private final int CONTRAST_PERCENTAGE = 5;
	
	/**
	 * Percentage of pixel that should be colored white during binarization
	 */
	private final int BINARY_PERCENTAGE = 65;
	
	/**
	 * radius for the shading filter, optimized for the used image size
	 */
	private final int RADIUS = 6; 
	
	/**
	 * height of the image which needs to be processed
	 */
	private int height;
	
	/**
	 * width of the image which needs to be processed
	 */
	private int width;
	
	/**
	 * region for the shading filter
	 */
	private int[] region = new int[(2 * RADIUS + 1) * (2 * RADIUS + 1)];
	
	/**
	 * Preprocesses an image in the following order:
	 * 1. Resize
	 * 2. Convert to grayscale
	 * 3. Contrast adjustment
	 * 4. Correct Shading
	 * 5. Binarize
	 * @param imgToProcess image processor to preprocess
	 */
	public void preprocessing(ImageProcessor imgToProcess) {
		imgToProcess = imgToProcess.resize(Abschlussprojekt_PlugIn.AVRG_IMAGE_WIDTH);
		height = imgToProcess.getHeight();
		width = imgToProcess.getWidth();
		ByteProcessor grayImg = convertToGrayscale(imgToProcess);
		adjustContrast(grayImg);
		grayImg = correctShade(grayImg);
		Abschlussprojekt_PlugIn.setCurrentImageProcessor(binarizeImage(grayImg));
	}
	
	/**
	 * converts the image to a grayscale image by using the geometric interpretation of the color space
	 * @param ip image processor to work on
	 * @return changed image as a byte processor
	 */
	private ByteProcessor convertToGrayscale(ImageProcessor ip) {
        ByteProcessor greyScaleIp = new ByteProcessor(width, height);
        
        for (int u = 0; u < width; u++) {
            for (int v = 0; v < height; v++) {
            	int color = ip.getPixel(u, v);            	
            	int newPixel = calculateIntensity(getRGBValues(color));               	
        		greyScaleIp.putPixel(u, v, newPixel);            	
            }
        }
        return greyScaleIp;
	}
	
	/**
	 * splits the color-value into the three resulting colors (RGB)
	 * @param color 32 bit representation of one value
	 * @return Array containing the red, green and blue values
	 */ 
    private int[] getRGBValues(int color) {
    	int red = (color & RED_MASK) >> 16;
    	int green = (color & GREEN_MASK) >> 8;
    	int blue = color & BLUE_MASK;
    	
    	int[] rgb = {red, green, blue};
    	return rgb;
    }
    
    /**
	 * calculates the grey value as the length of the vector in the RGB color space
	 * @param rgbValues values of the three colors red, green and blue
	 * @return grey value as an int
	 */ 
    private int calculateIntensity(int[] rgbValues) {
    	double redSquared =  Math.pow(rgbValues[0], 2);
    	double greenSquared =  Math.pow(rgbValues[1], 2);
    	double blueSquared =  Math.pow(rgbValues[2], 2);
    	
    	double intensity =  Math.sqrt(redSquared + greenSquared + blueSquared);
    	int grey = (int) Math.round((intensity * (double) Abschlussprojekt_PlugIn.WHITE) / MAX_VECTOR_LENGTH);
    
    	return grey;  	
    }
    
    /**
     * Linearly scales the grayscale values of the pixels to use the whole range from black to white with a minimum
     * percentage of pixels to use the min/max values
     * @param ip the grayscale image to adjust the contrast of
     */
    private void adjustContrast(ByteProcessor ip) {  			    
	    int colorMax = Abschlussprojekt_PlugIn.WHITE;
	    int colorMin = Abschlussprojekt_PlugIn.BLACK;
	    int maxThreshold = colorMax, minThreshold = colorMin;

	    int[] histogram = ip.getHistogram();
	    
        int pixelAmountLowHigh = (int) Math.ceil(ip.getPixelCount() * CONTRAST_PERCENTAGE / 100.0);      
        
        int minSum = histogram[minThreshold];
        int maxSum = histogram[maxThreshold];
        
	    int newPixel;
        
        while(minSum < pixelAmountLowHigh) {
        	minThreshold++;
        	minSum += histogram[minThreshold];
        }
        
        while(maxSum < pixelAmountLowHigh) {
        	maxThreshold--;
        	maxSum += histogram[maxThreshold];
        }
         
	    for (int u = 0; u < width; u++) {
	        for (int v = 0; v < height; v++) {
	            int p = ip.getPixel(u, v);
	            
	            if(p > minThreshold && p < maxThreshold) {
	            	newPixel = colorMin + (p - minThreshold) * (colorMax - colorMin) / (maxThreshold - minThreshold);	
	            } else {
	            	newPixel = (p >= maxThreshold) ? colorMax : colorMin;
	            } 
	        
	            ip.putPixel(u, v, newPixel);
	        }
	    }
	}
    
    /**
     * uses a shading filter to correct shading
     * Industrielle Bildverarbeitung: Wie optische Qualitätskontrolle wirklich funktioniert,
	 * Christian Demant, Bernd Streicher-Abel, Axel Springhoff,
	 * Springer-Verlag, 2011
     * @param ip image to use the shading filter on
     * @return the corrected image as image processor
     */
    private ByteProcessor correctShade(ByteProcessor ip) {
		ByteProcessor back = new ByteProcessor(width, height);

		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				int k = 0;
				for (int i = -RADIUS; i <= RADIUS; i++) {
					for (int j = -RADIUS; j <= RADIUS; j++) {
						region[k++] = ip.getPixel(u + i, v + j);  
					}
				}
				Arrays.sort(region);
				back.putPixel(u, v, region[region.length - 1]);
			}
		}
		
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				back.putPixel(u, v, (int) (Abschlussprojekt_PlugIn.WHITE * 
						((float) ip.getPixel(u,v)) / ((float) back.getPixel(u,v))));	
			}
		}
		return back;
	}
    

    /**
     * binarizes a given image with a minimum percentage of white pixels
     * @param ip the grayscale image to be binarized
     * @return a binary image processor
     */
    private BinaryProcessor binarizeImage(ByteProcessor ip) {
	    int[] histogram = ip.getHistogram();
	    int maxThreshold = Abschlussprojekt_PlugIn.WHITE;  
        int pixelAmountHigh = (int) Math.ceil(ip.getPixelCount() * BINARY_PERCENTAGE / 100);
        int maxSum = histogram[maxThreshold];
        
        int newPixel;
        
        while(maxSum < pixelAmountHigh) {
        	maxThreshold--;
        	maxSum += histogram[maxThreshold];
        } 
	    
	    for (int u = 0; u < width; u++) {
	        for (int v = 0; v < height ; v++) {
	            int p = ip.getPixel(u, v);	            
	            if(p < maxThreshold) {
	            	newPixel  = Abschlussprojekt_PlugIn.BLACK;	            	
	            } else {
	            	newPixel =  Abschlussprojekt_PlugIn.WHITE;
	            } 	        
	            ip.putPixel(u, v, newPixel);
	        }
	    }
	    
	    return new BinaryProcessor(ip);
	}
}
