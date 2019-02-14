package com.mycompany.imagej;

import ij.IJ;
import ij.process.ImageProcessor;

/**
 * contains everything related to the segmentation
 */
public class Segmentation {
	// constant values
	private final int BLACK = 0;
	private final int WHITE = 255;
	private final int WHITE_SEGMENT = 0;
	
	/**
	 * fills the Segments with the corresponding pixels
	 * @param binarisedImg image to segment, completely in black and white
	 */
	public void addPixelsToSegment(ImageProcessor binarisedImg) {
		int width = binarisedImg.getWidth();
		int height = binarisedImg.getHeight();
		int[][] segments = new int[width][height];
		int segmentCounter = WHITE_SEGMENT + 1;
		
		for(int countInX = 0; countInX < width; countInX++) {
			for(int countInY = 0; countInY < height; countInY++) {
				int segmentToSet;
				int currentPixel = binarisedImg.getPixel(countInX, countInY);
				
				if (currentPixel == BLACK) {
					// Look above
					// ... then below
					// ... then its a new segment
					if(countInX - 1 >= 0 && binarisedImg.getPixel(countInX - 1, countInY) == BLACK) {
						segmentToSet = segments[countInX - 1][countInY];						
					} else if(countInY - 1 >= 0 && binarisedImg.getPixel(countInX, countInY - 1) == BLACK) {
						segmentToSet = segments[countInX][countInY - 1];	
					} else {
						// no need to look to the right and below since we sort them into a segment afterwards
						segmentToSet = segmentCounter++;
					}
				} else {
					segmentToSet = WHITE_SEGMENT;
				}
				
				segments[countInX][countInY] = segmentToSet;
			}
		}
		
		// TODO: Current problem: Too many segments
		
		// DEBUG to see if the segmentation worked
		colorTheSegments(binarisedImg, segments, segmentCounter - 1);
	}
	
	/**
	 * DEBUG METHOD
	 * Colors the segments so that you can see if the segmentation functions properly
	 * @param image image to repaint
	 * @param segments number of the segment for each pixel
	 * @param numberOfSegments number of (originally black) segments
	 */
	private void colorTheSegments(ImageProcessor image, int[][] segments, int numberOfSegments) {
		int width = image.getWidth();
		int height = image.getHeight();
		int stepWidth = (int) ((double) WHITE / (double) numberOfSegments);
		IJ.log("Segments: " + numberOfSegments + " stepWidth: " + stepWidth);
		
		for(int countInX = 0; countInX < width; countInX++) {
			for(int countInY = 0; countInY < height; countInY++) {
				if(segments[countInX][countInY] != WHITE_SEGMENT) {
					int currentSegment = segments[countInX][countInY];
					int newPixel = WHITE - (currentSegment * stepWidth);
					image.putPixel(countInX, countInY, newPixel);
				}				
			}
		}
		
	}
}
