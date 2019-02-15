package com.mycompany.imagej;

import java.util.Arrays;

import ij.IJ;
import ij.process.ImageProcessor;

/**
 * contains everything related to the segmentation
 */
public class Segmentation {
	// constant values
	private final int BLACK = 255;
	private final int WHITE = 0;
	private final int WHITE_SEGMENT = 0;
	private final int NOT_SEGMENTED = -1;
	private final int STARTED_NOT_FINISHED = -2;
	
	// local variables
	private int segmentCounter = WHITE_SEGMENT + 1;
	
	/**
	 * fills the Segments with the corresponding pixels
	 * @param binarisedImg image to segment, completely in black and white
	 */
	public void addPixelsToSegment(ImageProcessor binarisedImg) {
		int width = binarisedImg.getWidth();
		int height = binarisedImg.getHeight();
		segmentCounter = WHITE_SEGMENT + 1;
		int[][] segments = new int[height][width];
		segments = fillSegmentsArrayWithDefault(segments, height);
		
		for(int countInY = 0; countInY < height; countInY++) {
			for(int countInX = 0; countInX < width; countInX++) {
				int segmentToSet;
				
				int currentPixel = binarisedImg.getPixel(countInX, countInY);				
				if (currentPixel == BLACK) {
					segmentToSet = findSegmentForPixel(segments, binarisedImg, countInX, countInY);
				} else {
					segmentToSet = WHITE_SEGMENT;
				}
				
				segments[countInY][countInX] = segmentToSet;
			}
		}
		
		// DEBUG to see if the segmentation worked
		colorTheSegments(binarisedImg, segments, segmentCounter);
	}
	
	/**
	 * fills the segment array with default values (-1)
	 * @param segments two dimensional array with the segments
	 * @param rows number of rows in the two dimensional array
	 * @return filled two dimensional array 
	 */
	private int[][] fillSegmentsArrayWithDefault(int[][] segments, int rows) {
		for(int countRows = 0; countRows < rows; countRows++) {
			Arrays.fill(segments[countRows], NOT_SEGMENTED);			
		}
		return segments;
	}
	
	private int findSegmentForPixel(int[][] segments, ImageProcessor image, int currentX, int currentY) {
		if(segments[currentY][currentX] >= WHITE_SEGMENT) {
			return segments[currentY][currentX];
		} 
		
		segments[currentY][currentX] = STARTED_NOT_FINISHED;
		
		if (currentX - 1 >= 0) {
			if(image.getPixel(currentX - 1, currentY) == BLACK && segments[currentY][currentX - 1] != STARTED_NOT_FINISHED) {
				return findSegmentForPixel(segments, image, currentX - 1, currentY);
			}
		}
		if (currentY - 1 >= 0) {
			if(image.getPixel(currentX, currentY - 1) == BLACK && segments[currentY - 1][currentX] != STARTED_NOT_FINISHED) {
				return findSegmentForPixel(segments, image, currentX, currentY - 1);
			}
		}
		if (currentX + 1 < image.getWidth()) {
			if(image.getPixel(currentX + 1, currentY) == BLACK && segments[currentY][currentX + 1] != STARTED_NOT_FINISHED) {
				return findSegmentForPixel(segments, image, currentX + 1, currentY);
			}
		}
		if (currentY + 1 < image.getHeight()) {
			if(image.getPixel(currentX, currentY + 1) == BLACK && segments[currentY + 1][currentX] != STARTED_NOT_FINISHED) {
				return findSegmentForPixel(segments, image, currentX, currentY + 1);
			}
		}
		
		return segmentCounter++;
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
		int stepWidth = (int) ((double) BLACK / (double) numberOfSegments);
		IJ.log("Segments: " + numberOfSegments + " stepWidth: " + stepWidth);
		
		for(int countInY = 0; countInY < height; countInY++) {
			for(int countInX = 0; countInX < width; countInX++) {
				if(segments[countInY][countInX] != WHITE_SEGMENT) {
					int currentSegment = segments[countInY][countInX];
					int newPixel = BLACK - (currentSegment * stepWidth);
					image.putPixel(countInX, countInY, newPixel);
				}				
			}
		}
		
	}
}
