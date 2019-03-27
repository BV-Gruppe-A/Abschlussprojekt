package com.mycompany.imagej;

import java.util.Arrays;
import java.util.Stack;
import com.mycompany.imagej.datamodels.CharacterCandidate;
import com.mycompany.imagej.datamodels.CharacterType;
import com.mycompany.imagej.datamodels.Pixel;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * contains everything related to the segmentation
 */
public class Segmentation {
	// constant values
	private final int SEGMENTS_START_AMOUNT = 0;	
	private final int NOT_SEGMENTED = -1;
	
	// local variables
	private ImageProcessor binarisedImg;
	private int imgHeight;
	private int imgWidth;
	private int segmentCounter;
	
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
	public CharacterCandidate[] segmentThePicture() {
		return checkSizesAndRescale(makeCharCandidatesOutOfSegments(fillTheSegments()));
	}
	
	/**
	 * DEBUG
	 * allows for debugging of the segmentation without needing to split it up
	 */
	public void debugSegmentation() {
		CharacterCandidate[] debug = checkSizesAndRescale(makeCharCandidatesOutOfSegments(fillTheSegments()));
		for(int count = 0; count < debug.length; count++) {
			ImagePlus imgToShow = new ImagePlus("char " + (count + 1), debug[count].getImage());
	    	imgToShow.show();
		}		
	}
	
	/**
	 * fills the Segments with the corresponding pixels
	 */
	private int fillTheSegments() {
		if(!binarisedImg.isBinary()) {     	
        	IJ.error("ImageProcessor is not binary!");
			// TODO: Maybe throw an exception? Or Delete but not just logging
        	return 0;
		}
				
		for(int countInY = 0; countInY < imgHeight; countInY++) {
			for(int countInX = 0; countInX < imgWidth; countInX++) {
				int currentValueToCheck = binarisedImg.getPixel(countInX, countInY);
				if(segments[countInY][countInX] == NOT_SEGMENTED && currentValueToCheck == Abschlussprojekt_PlugIn.BLACK) {
					identifyAWholeSegment(new Pixel(countInX, countInY, currentValueToCheck), segmentCounter++);			
				}
			}
		}
		
		return segmentCounter - 1;
	}
	
	/**
	 * identifies a whole segment by looking in each direction and setting each black pixel to the current segment
	 * @param start first Pixel of a segment
	 * @param currentSegment segment to sort the pixel into
	 */
	private void identifyAWholeSegment(Pixel start, int currentSegment) {
		Stack<Pixel> pixelToLookAt = new Stack<Pixel>();
		pixelToLookAt.push(start);
		
		while(!pixelToLookAt.isEmpty()) {
			Pixel currentPixel = pixelToLookAt.pop();
			if(currentPixel.getValue() == Abschlussprojekt_PlugIn.BLACK) {
				int currentX = currentPixel.getPosInX();
				int currentY = currentPixel.getPosInY();
				segments[currentY][currentX] = currentSegment;
				
				if (currentX - 1 >= 0) {
					if (segments[currentY][currentX - 1] != currentSegment) {
						pixelToLookAt.push(new Pixel(currentX - 1, currentY, binarisedImg.getPixel(currentX - 1, currentY)));
					}
				}
				if (currentX + 1 < imgWidth) {
					if (segments[currentY][currentX + 1] != currentSegment) {
						pixelToLookAt.push(new Pixel(currentX + 1, currentY, binarisedImg.getPixel(currentX + 1, currentY)));
					}					
				}
				
				if (currentY - 1 >= 0) {
					if (segments[currentY - 1][currentX] != currentSegment) {
						pixelToLookAt.push(new Pixel(currentX, currentY - 1, binarisedImg.getPixel(currentX, currentY - 1)));
					}					
				}
				if (currentY + 1 < imgHeight) {
					if(segments[currentY + 1][currentX] != currentSegment) {
						pixelToLookAt.push(new Pixel(currentX, currentY + 1, binarisedImg.getPixel(currentX, currentY + 1)));
					}
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
	 * @return array containing all possible characters
	 */
	private CharacterCandidate[] makeCharCandidatesOutOfSegments(int segmentAmount) {
		CharacterCandidate[] allSegments = new CharacterCandidate[segmentAmount];
		
		for(int countSegment = SEGMENTS_START_AMOUNT; countSegment < segmentAmount; countSegment++) {
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
			
			allSegments[countSegment] = new CharacterCandidate(leftBorder, rightBorder, upperBorder, bottomBorder);	
		}
		
		return allSegments;	
	}
	
	/**
	 * takes the given segments and checks for each one if it should be included for the classification
	 * all that should be included will be rescaled
	 * @param allSegments all Segments to consider
	 * @return array containing all possible characters
	 */
	private CharacterCandidate[] checkSizesAndRescale(CharacterCandidate[] allSegments) {
		CharacterCandidate[] arrToReturn = new CharacterCandidate[allSegments.length];
		int currentReturnArrayPos = 0;
		
		for(int countSegment = 0; countSegment < allSegments.length; countSegment++) {
			boolean couldBeChar = CharacterCandidate.checkIfValidSize(allSegments[countSegment], CharacterType.CHARACTER);
			boolean couldBeDash = CharacterCandidate.checkIfValidSize(allSegments[countSegment], CharacterType.DASH);

			if(couldBeChar || couldBeDash) {
				if(couldBeChar) {
					allSegments[countSegment].checkForUmlautAndChangeBorder(allSegments);
				}	
				
				allSegments[countSegment].setAndScaleImage(binarisedImg);
				arrToReturn[currentReturnArrayPos++] = allSegments[countSegment];
			}
		}
		
		return cleanUpAndSortCharacterArray(arrToReturn, currentReturnArrayPos);
	}
	
	/**
	 * shortens the array so that only full image processors are used
	 * @param arrayToClean array which needs to be cleaned
	 * @return a new, cleaned-up array
	 */
	private CharacterCandidate[] cleanUpAndSortCharacterArray(CharacterCandidate[] arrToProcess, int filledImages) {	
		CharacterCandidate[] cleanedArray = Arrays.copyOf(arrToProcess, filledImages);
		Arrays.sort(cleanedArray);
		return cleanedArray;
 	}
}