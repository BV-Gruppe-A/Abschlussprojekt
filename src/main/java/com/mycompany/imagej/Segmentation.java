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
	private ImageProcessor binarisedImg;
	private int imgHeight;
	private int imgWidth;
	private int segmentCounter;
	
	/**
	 * Constructor which sets the first picture to work with
	 * @param image first picture to work on
	 */
	public Segmentation(ImageProcessor image) {
		changeImage(image);
	}
	
	/**
	 * changes the images which is currently worked on
	 * @param newImage new Image to work on
	 */
	public void changeImage(ImageProcessor newImage) {
		binarisedImg = newImage;
		imgHeight = newImage.getHeight();
		imgWidth = newImage.getWidth();
	}
	
	/**
	 * segments the given picture
	 * @return each character as a single image
	 */
	public ImageProcessor[] segmentThePicture() {		
		return makeImagesOutOfSegments(addPixelsToSegment());
	}
	
	/**
	 * allows for debugging of the segmentation without needing to split it up
	 */
	public void debugSegmentation() {
		addPixelsToSegment();
	}
	
	/**
	 * fills the Segments with the corresponding pixels
	 * @param binarisedImg image to segment, completely in black and white
	 */
	private int[][] addPixelsToSegment() {
		segmentCounter = WHITE_SEGMENT + 1;
		int[][] segments = new int[imgHeight][imgWidth];
		fillSegmentsArrayWithDefault(segments, imgHeight);
		
		for(int countInY = 0; countInY < imgHeight; countInY++) {
			for(int countInX = 0; countInX < imgWidth; countInX++) {
				if(segments[countInY][countInX] == NOT_SEGMENTED) {
					int segmentToSet;
					
					int currentPixel = binarisedImg.getPixel(countInX, countInY);				
					if (currentPixel == BLACK) {
						segmentToSet = findSegmentForPixel(segments, countInX, countInY);
					} else {
						segmentToSet = WHITE_SEGMENT;
					}
					
					segments[countInY][countInX] = segmentToSet;
				}
			}
		}
		
		// DEBUG to see if the segmentation worked
		colorTheSegments(segments, segmentCounter - 1);
		return segments;
	}
	
	/**
	 * fills the segment array with default values (-1)
	 * @param segments two dimensional array with the segments
	 * @param rows number of rows in the two dimensional array
	 * @return filled two dimensional array 
	 */
	private void fillSegmentsArrayWithDefault(int[][] segments, int rows) {
		for(int countRows = 0; countRows < rows; countRows++) {
			Arrays.fill(segments[countRows], NOT_SEGMENTED);		
		}
	}
	
	/**
	 * finds (& sets) the segment for each pixel recursively
	 * @param segments which pixel belongs to which segment
	 * @param currentX x-Coordinate of current pixel
	 * @param currentY y-Coordinate of current pixel
	 * @return the segment of the pixel to set
	 */
	private int findSegmentForPixel(int[][] segments, int currentX, int currentY) {
		int segmentToSet = STARTED_NOT_FINISHED;
		int currentSegment = segments[currentY][currentX];
		
		if(currentSegment > WHITE_SEGMENT) {
			segmentToSet = currentSegment;
		} else if(currentSegment == NOT_SEGMENTED) {			
			segments[currentY][currentX] = STARTED_NOT_FINISHED;
		}
		
		if(binarisedImg.getPixel(currentX, currentY) == WHITE) {
			IJ.log("WHITE PIXEL IN RECURSION");
		}
		
		if (currentX - 1 >= 0 && segmentToSet == STARTED_NOT_FINISHED) {
			if(binarisedImg.getPixel(currentX - 1, currentY) == BLACK && segments[currentY][currentX - 1] != STARTED_NOT_FINISHED) {
				segmentToSet = findSegmentForPixel(segments, currentX - 1, currentY);
				segments[currentY][currentX - 1] = segmentToSet;
			}
		}
		
		if (currentY - 1 >= 0 && segmentToSet == STARTED_NOT_FINISHED) {
			if(binarisedImg.getPixel(currentX, currentY - 1) == BLACK && segments[currentY - 1][currentX] != STARTED_NOT_FINISHED) {
				segmentToSet = findSegmentForPixel(segments, currentX, currentY - 1);
				segments[currentY - 1][currentX] = segmentToSet;
			}
		}
		
		if (currentX + 1 < imgWidth && segmentToSet == STARTED_NOT_FINISHED) {
			if(binarisedImg.getPixel(currentX + 1, currentY) == BLACK && segments[currentY][currentX + 1] != STARTED_NOT_FINISHED) {
				segmentToSet = findSegmentForPixel(segments, currentX + 1, currentY);
				segments[currentY][currentX + 1] = segmentToSet;
			}
		}
		
		if (currentY + 1 < imgHeight && segmentToSet == STARTED_NOT_FINISHED) {
			if(binarisedImg.getPixel(currentX, currentY + 1) == BLACK && segments[currentY + 1][currentX] != STARTED_NOT_FINISHED) {
				segmentToSet = findSegmentForPixel(segments, currentX, currentY + 1);
				segments[currentY + 1][currentX] = segmentToSet;
			}
		}
		
		if(segmentToSet == STARTED_NOT_FINISHED) {
			segmentToSet = segmentCounter++;
			IJ.log("Segment: " + segmentToSet + " X: " + currentX + " Y: " + currentY);
		}		
		
		return segmentToSet;
	}
	
	/**
	 * takes a single picture and slices it up into different segments, each representing a single character
	 * @param segments which pixel belongs to which segment
	 * @return all characters as a single picture
	 */
	private ImageProcessor[] makeImagesOutOfSegments(int[][] segments) {
		// TODO: Fill with life as soon as the segmentation works properly
		return new ImageProcessor[5];
	}
	
	/**
	 * DEBUG METHOD
	 * Colors the segments so that you can see if the segmentation functions properly
	 * @param image image to repaint
	 * @param segments number of the segment for each pixel
	 * @param numberOfSegments number of (originally black) segments
	 */
	private void colorTheSegments(int[][] segments, int numberOfSegments) {
		int stepWidth = (int) ((double) BLACK / (double) numberOfSegments);
		IJ.log("Segments: " + numberOfSegments + " stepWidth: " + stepWidth);
		
		for(int countInY = 0; countInY < imgHeight; countInY++) {
			for(int countInX = 0; countInX < imgWidth; countInX++) {
				if(segments[countInY][countInX] != WHITE_SEGMENT) {
					int currentSegment = segments[countInY][countInX];
					int newPixel = BLACK - (currentSegment * stepWidth);
					binarisedImg.putPixel(countInX, countInY, newPixel);
				}				
			}
		}
		
	}
}
