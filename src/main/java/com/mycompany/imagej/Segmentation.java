package com.mycompany.imagej;

import java.util.Arrays;
import ij.IJ;
import ij.ImagePlus;
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
	
	private final int CHARACTER_HEIGHT = 38;
	private final int CHARACTER_WIDTH = 25;
	
	// local variables
	private ImageProcessor binarisedImg;
	private int imgHeight;
	private int imgWidth;
	private int segmentCounter;
	private int finalSegmentAmount;
	
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
		segmentCounter = WHITE_SEGMENT + 1;
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
		ImageProcessor[] debug = makeImagesOutOfSegments();
		for(int count = 0; count < debug.length; count++) {
			ImagePlus imgToShow = new ImagePlus("char " + (count + 1), debug[count]);
	    	imgToShow.show();
		}		
	}
	
	/**
	 * fills the Segments with the corresponding pixels
	 */
	private void fillTheSegments() {	
		for(int countInY = 0; countInY < imgHeight; countInY++) {
			for(int countInX = 0; countInX < imgWidth; countInX++) {
				if(segments[countInY][countInX] == NOT_SEGMENTED && binarisedImg.getPixel(countInX, countInY) == BLACK) {
					identifyAWholeSegment(countInX, countInY);
					sortPixelsIntoSegments(segmentCounter++);
				}
			}
		}
		
		finalSegmentAmount = segmentCounter - 1;
		// DEBUG to see if the segmentation worked
		colorTheSegments();
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
		ImageProcessor[] arrToReturn = new ImageProcessor[finalSegmentAmount];
		
		for(int countSegment = 1; countSegment <= finalSegmentAmount; countSegment++) {
			int upperLeftX = imgWidth, upperLeftY = imgHeight;
			int bottomRightX = 0, bottomRightY = 0;
			
			for(int countInY = 0; countInY < imgHeight; countInY++) {
				for(int countInX = 0; countInX < imgWidth; countInX++) {
					if(segments[countInY][countInX] == countSegment) {
						if(countInX < upperLeftX) {
							upperLeftX = countInX;
						}
						if(countInY < upperLeftY) {
							upperLeftY = countInY;
						}
						if(countInX > bottomRightX) {
							bottomRightX = countInX;
						}
						if(countInY > bottomRightY) {
							bottomRightY = countInY;
						}
					}
				}
			}
			
			int charWidth = bottomRightX - upperLeftX;
			int charHeight = bottomRightY - upperLeftY;
			IJ.log("Segment: " + countSegment + " Width: " + charWidth + " Height: " + charHeight);
			
			// TODO: Scale the characters instead of doing a hard cut
			
			ImageProcessor tempProcessor = binarisedImg.createProcessor(CHARACTER_WIDTH, CHARACTER_HEIGHT);
			for(int countInY = 0; countInY < CHARACTER_HEIGHT; countInY++) {
				for(int countInX = 0; countInX < CHARACTER_WIDTH; countInX++) {
					int pixelToPut = binarisedImg.getPixel(upperLeftX + countInX, upperLeftY + countInY);
					tempProcessor.putPixel(countInX, countInY, pixelToPut);
				}
			}
			arrToReturn[countSegment - 1] = tempProcessor;
		}		
		
		return arrToReturn;
	}
	
	/**
	 * DEBUG METHOD
	 * Colors the segments so that you can see if the segmentation functions properly
	 */
	private void colorTheSegments() {
		int stepWidth = (int) ((double) (BLACK - 2) / (double) finalSegmentAmount);
		IJ.log("Segments: " + finalSegmentAmount + " stepWidth: " + stepWidth);
		
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
