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
	private final int BLACK = 0;
	private final int WHITE = 255;

	private final int SEGMENTS_START_AMOUNT = 0;	
	private final int NOT_SEGMENTED = -2;
	private final int IDENTIFIED = -1;
	
	private final double THRESHOLD = 0.2;	
	private final int OPT_CHAR_HEIGHT = 38;
	private final int MAX_CHAR_HEIGHT = OPT_CHAR_HEIGHT + (int) Math.round(OPT_CHAR_HEIGHT * THRESHOLD);
	private final int MIN_CHAR_HEIGHT = OPT_CHAR_HEIGHT - (int) Math.round(OPT_CHAR_HEIGHT * THRESHOLD);
	
	private final int OPT_CHAR_WIDTH = 25;
	private final int MAX_CHAR_WIDTH = OPT_CHAR_WIDTH + (int) Math.round(OPT_CHAR_WIDTH * THRESHOLD);
	private final int MIN_CHAR_WIDTH = OPT_CHAR_WIDTH - (int) Math.round(OPT_CHAR_WIDTH * THRESHOLD);
	
	// local variables
	private ImageProcessor binarisedImg;
	private int imgHeight;
	private int imgWidth;
	private int segmentCounter;
	private int finalSegmentAmount;
	
	private int[][] segments;
	
	/**
	 * changes the image which is currently worked on
	 * @param newImage new Image to work on
	 */
	public void changeImage(ImageProcessor newImage) {
		binarisedImg = newImage;
		imgHeight = newImage.getHeight();
		imgWidth = newImage.getWidth();
		segments = new int[imgHeight][imgWidth];
		fillSegmentsArrayWithDefault(imgHeight);
		segmentCounter = SEGMENTS_START_AMOUNT;
	}
	
	/**
	 * segments the given picture
	 * @return each character as a single image processor
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
		if(!binarisedImg.isBinary()) {     	
        	IJ.log("ImageProcessor is not binary!");
			// TODO: Maybe throw an exception? Or Delete but not just logging
        	return;
		}
				
		for(int countInY = 0; countInY < imgHeight; countInY++) {
			for(int countInX = 0; countInX < imgWidth; countInX++) {
				if(segments[countInY][countInX] == NOT_SEGMENTED && binarisedImg.getPixel(countInX, countInY) == BLACK) {
					identifyWholeSegment(countInX, countInY);
					sortPixelsIntoSegments(segmentCounter++);
				}
			}
		}
		
		finalSegmentAmount = segmentCounter - 1;
	}

	/**
	 * starts at a given pixel and identifies all pixel which belong to the same segment
	 * @param startX x-Coordinate of the starting pixel
	 * @param startY y-Coordinate of the starting pixel
	 */
	private void identifyWholeSegment(int startX, int startY) {
		segments[startY][startX] = IDENTIFIED;
		
		if (startY - 1 >= 0) {
			if(binarisedImg.getPixel(startX, startY - 1) == BLACK && segments[startY - 1][startX] != IDENTIFIED) {
				identifyWholeSegment(startX, startY - 1);
			}
		}
		
		if (startX - 1 >= 0) {
			if(binarisedImg.getPixel(startX - 1, startY) == BLACK && segments[startY][startX - 1] != IDENTIFIED) {
				identifyWholeSegment(startX - 1, startY);
			}
		}
		
		if (startY + 1 < imgHeight) {
			if(binarisedImg.getPixel(startX, startY + 1) == BLACK && segments[startY + 1][startX] != IDENTIFIED) {
				identifyWholeSegment(startX, startY + 1);
			}
		}
		
		if (startX + 1 < imgWidth) {
			if(binarisedImg.getPixel(startX + 1, startY) == BLACK && segments[startY][startX + 1] != IDENTIFIED) {
				identifyWholeSegment(startX + 1, startY);
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
	 * @return all characters as a single image processor
	 */
	private ImageProcessor[] makeImagesOutOfSegments() {
		ImageProcessor[] arrToReturn = new ImageProcessor[finalSegmentAmount];
		int currentArrayPos = 0;
		
		for(int countSegment = SEGMENTS_START_AMOUNT; countSegment < finalSegmentAmount; countSegment++) {
			int leftBorder = imgWidth, upperBorder = imgHeight;
			int rightBorder = 0, bottomBorder = 0;
			
			for(int countInY = 0; countInY < imgHeight; countInY++) {
				for(int countInX = 0; countInX < imgWidth; countInX++) {
					if(segments[countInY][countInX] == countSegment) {
						if(countInX < leftBorder) {
							leftBorder = countInX;
						}
						if(countInY < upperBorder) {
							upperBorder = countInY;
						}
						if(countInX > rightBorder) {
							rightBorder = countInX;
						}
						if(countInY > bottomBorder) {
							bottomBorder = countInY;
						}
					}
				}
			}
			
			int charWidth = rightBorder - leftBorder;
			int charHeight = bottomBorder - upperBorder;
			
			if(checkIfInvalidCharacter(charWidth, charHeight)) {
				continue;
			}
			
			/*
			// check if there is a black pixel above the current segment (probably from ö, ä or ü)
			for(int countInY = 0; countInY < upperBorder; countInY++) {
				for(int countInX = 0; countInX < charWidth; countInX++) {
					if(binarisedImg.getPixel(leftBorder + countInX, countInY) == BLACK) {
						upperBorder = countInY;
						charHeight = bottomBorder - upperBorder;
					}
				}
			}
			*/
			
			ImageProcessor tempProcessor = binarisedImg.createProcessor(charWidth, charHeight);
			for(int countInY = 0; countInY < charHeight; countInY++) {
				for(int countInX = 0; countInX < charWidth; countInX++) {
					int pixelToPut = binarisedImg.getPixel(leftBorder + countInX, upperBorder + countInY);
					tempProcessor.putPixel(countInX, countInY, pixelToPut);
				}
			}
			
			arrToReturn[currentArrayPos] = tempProcessor.resize(OPT_CHAR_WIDTH, OPT_CHAR_HEIGHT);
			currentArrayPos++;		
		}		
		
		return cleanUpImageArray(arrToReturn);
	}
	
	/**
	 * checks if a segment has a size which could be a character
	 * @param width width of the segment
	 * @param height height of the segment
	 * @return true if the segment is not a valid possibility for a character
	 */
	private boolean checkIfInvalidCharacter(int width, int height) {
		if(width > MAX_CHAR_WIDTH || height > MAX_CHAR_HEIGHT) {
			return true;
		}
		
		if(width < MIN_CHAR_WIDTH || height < MIN_CHAR_HEIGHT) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * shortens the array so that only full image processors are used
	 * @param arrayToClean array which needs to be cleaned
	 * @return a new, cleaned-up array
	 */
	private ImageProcessor[] cleanUpImageArray(ImageProcessor[] arrToClean) {
		int countFilledImages = 0;
		for(int count = 0; count < arrToClean.length; count++) {
			if(arrToClean[count] != null) {
				countFilledImages++;
			}
		}
	
		return Arrays.copyOf(arrToClean, countFilledImages);
 	}
}