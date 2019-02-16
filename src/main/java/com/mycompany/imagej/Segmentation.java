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
	
	private final int NOT_SEGMENTED = -2;
	private final int IDENTIFIED = -1;
	private final int WHITE_SEGMENT = 0;
	
	// local variables
	private ImageProcessor binarisedImg;
	private int imgHeight;
	private int imgWidth;
	private int segmentCounter;
	
	private int[][] segments;
	
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
		segments = new int[imgHeight][imgWidth];
		fillSegmentsArrayWithDefault(imgHeight);
	}
	
	/**
	 * segments the given picture
	 * @return each character as a single image
	 */
	public ImageProcessor[] segmentThePicture() {	
		fillTheSegments();
		return makeImagesOutOfSegments();
	}
	
	/**
	 * allows for debugging of the segmentation without needing to split it up
	 */
	public void debugSegmentation() {
		fillTheSegments();
	}
	
	/**
	 * fills the Segments with the corresponding pixels
	 */
	private int[][] fillTheSegments() {
		segmentCounter = WHITE_SEGMENT + 1;
		
		for(int countInY = 0; countInY < imgHeight; countInY++) {
			for(int countInX = 0; countInX < imgWidth; countInX++) {
				if(segments[countInY][countInX] == NOT_SEGMENTED && binarisedImg.getPixel(countInX, countInY) == BLACK) {
					identifyAWholeSegment(countInX, countInY);
					sortPixelsIntoSegments(segmentCounter++);
				}
			}
		}
		
		// DEBUG to see if the segmentation worked
		colorTheSegments(segmentCounter - 1);
		return segments;
	}

	/**
	 * starts at a given pixel and identifies all pixel which belong to the same segment
	 * @param startX x-Coordinate of the starting pixel
	 * @param startY y-Coordinate of the starting pixel
	 */
	private void identifyAWholeSegment(int startX, int startY) {
		segments[startY][startX] = IDENTIFIED;
		
		if (startY - 1 >= 0) {
			if(binarisedImg.getPixel(startX, startY - 1) == BLACK && segments[startY - 1][startX] != IDENTIFIED) {
				identifyAWholeSegment(startX, startY - 1);
			}
		}
		
		if (startX - 1 >= 0) {
			if(binarisedImg.getPixel(startX - 1, startY) == BLACK && segments[startY][startX - 1] != IDENTIFIED) {
				identifyAWholeSegment(startX - 1, startY);
			}
		}
		
		if (startY + 1 < imgHeight) {
			if(binarisedImg.getPixel(startX, startY + 1) == BLACK && segments[startY + 1][startX] != IDENTIFIED) {
				identifyAWholeSegment(startX, startY + 1);
			}
		}
		
		if (startX + 1 < imgWidth) {
			if(binarisedImg.getPixel(startX + 1, startY) == BLACK && segments[startY][startX + 1] != IDENTIFIED) {
				identifyAWholeSegment(startX + 1, startY);
			}
		}
	}	
	
	/**
	 * adds all identified pixel to the given Segment
	 * @param currentSegment segment which all identified pixel belong to
	 */
	private void sortPixelsIntoSegments(int currentSegment) {
		for(int countInY = 0; countInY < imgHeight; countInY++) {
			for(int countInX = 0; countInX < imgWidth; countInX++) {
				if(segments[countInY][countInX] == IDENTIFIED) {
					segments[countInY][countInX] = currentSegment;
				}
			}
		}
	}	
	
	/**
	 * fills the segment array with default values (-2)
	 * @param rows number of rows in the two dimensional array
	 * @return filled two dimensional array 
	 */
	private void fillSegmentsArrayWithDefault(int rows) {
		for(int countRows = 0; countRows < rows; countRows++) {
			Arrays.fill(segments[countRows], NOT_SEGMENTED);		
		}
	}
	
	/**
	 * takes a single picture and slices it up into different segments, each representing a single character
	 * @return all characters as a single picture
	 */
	private ImageProcessor[] makeImagesOutOfSegments() {
		// TODO: Fill with life as soon as the segmentation works properly
		return new ImageProcessor[5];
	}
	
	/**
	 * DEBUG METHOD
	 * Colors the segments so that you can see if the segmentation functions properly
	 * @param numberOfSegments number of (originally black) segments
	 */
	private void colorTheSegments(int numberOfSegments) {
		int stepWidth = (int) ((double) (BLACK - 2) / (double) numberOfSegments);
		// IJ.log("Segments: " + numberOfSegments + " stepWidth: " + stepWidth);
		
		for(int countInY = 0; countInY < imgHeight; countInY++) {
			for(int countInX = 0; countInX < imgWidth; countInX++) {
				if(segments[countInY][countInX] != WHITE_SEGMENT) {
					int currentSegment = segments[countInY][countInX];
					int newPixel = WHITE + (currentSegment * stepWidth);
					binarisedImg.putPixel(countInX, countInY, newPixel);
				}				
			}
		}
		
	}
}
