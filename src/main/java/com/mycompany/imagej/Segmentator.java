package com.mycompany.imagej;

import java.util.Arrays;
import java.util.Stack;
import com.mycompany.imagej.datamodels.CharacterCandidate;
import com.mycompany.imagej.datamodels.CharacterType;
import com.mycompany.imagej.datamodels.Pixel;

import ij.process.BinaryProcessor;

/**
 * contains everything related to the segmentation
 * TODO change comment
 */
public class Segmentator {
	/**
	 * Number of the first segment
	 */
	private final int SEGMENTS_START_AMOUNT = 0;	
	
	/**
	 * Value for a pixel, which has not been initialised yet
	 */
	private final int NOT_SEGMENTED = -1;
	
	/**
	 * the binary Image, which should be segmented
	 */
	private BinaryProcessor binarisedImg;
	
	/**
	 * height of the image
	 */
	private int imgHeight;
	
	/**
	 * width of the image
	 */
	private int imgWidth;
	
	/**
	 * two dimensional array which saves the number of the segment for each pixel
	 */
	private int[][] segments;
	
	/**
	 * resets the member values for this object
	 * saves the image, height and width of the current Image from the PlugIn-class
	 * declares a new segments array
	 */
	private void resetValuesForNewImage() {
		binarisedImg = Abschlussprojekt_PlugIn.getCurrentImageProcessor();
		imgHeight = Abschlussprojekt_PlugIn.getCurrentHeight();
		imgWidth = Abschlussprojekt_PlugIn.getCurrentWidth();
		segments = new int[imgHeight][imgWidth];
		fillSegmentsArrayWithDefault(imgHeight);
	}
	
	/**
	 * segments the given picture
	 * @return each character as a single image processor
	 */
	public CharacterCandidate[] segmentThePicture() {
		resetValuesForNewImage();
		return checkSizesAndRescale(makeCharCandidatesOutOfSegments(fillTheSegments()));
	}
	
	/**
	 * fills the Segments with the corresponding pixels
	 * @return the number of segments in the image
	 */
	private int fillTheSegments() {
		int segmentCounter = SEGMENTS_START_AMOUNT;
		
		for(int countInY = 0; countInY < imgHeight; countInY++) {
			for(int countInX = 0; countInX < imgWidth; countInX++) {
				int currentValueToCheck = binarisedImg.getPixel(countInX, countInY);
				if(segments[countInY][countInX] == NOT_SEGMENTED 
						&& currentValueToCheck == Abschlussprojekt_PlugIn.BLACK) {
					identifyAWholeSegment(new Pixel(countInX, countInY, currentValueToCheck),
							segmentCounter++);			
				}
			}
		}
		
		return segmentCounter - 1;
	}
	
	/**
	 * uses Flood Filling to identify and set a whole segment
	 * based on this source:
	 * W. Burger und M. J. Burge, Digitale Bildverarbeitung: Eine algorithmische Einführung mit 
	 * Java, 3. Auﬂ., Serie X.media.press.Berlin: Springer Vieweg, 2015, S. 227
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
						pixelToLookAt.push(new Pixel(currentX - 1, currentY,
								binarisedImg.getPixel(currentX - 1, currentY)));
					}
				}
				if (currentX + 1 < imgWidth) {
					if (segments[currentY][currentX + 1] != currentSegment) {
						pixelToLookAt.push(new Pixel(currentX + 1, currentY,
								binarisedImg.getPixel(currentX + 1, currentY)));
					}					
				}
				
				if (currentY - 1 >= 0) {
					if (segments[currentY - 1][currentX] != currentSegment) {
						pixelToLookAt.push(new Pixel(currentX, currentY - 1,
								binarisedImg.getPixel(currentX, currentY - 1)));
					}					
				}
				if (currentY + 1 < imgHeight) {
					if(segments[currentY + 1][currentX] != currentSegment) {
						pixelToLookAt.push(new Pixel(currentX, currentY + 1,
								binarisedImg.getPixel(currentX, currentY + 1)));
					}
				}
			}
		}
	}

	/**
	 * fills the segment array with default values since there is only one object of the segmentation per gui
	 * and the array should be set to a value that symbolizes that each pixel has not yet been segmented
	 * @param rows number of rows in the two dimensional array
	 */
	private void fillSegmentsArrayWithDefault(int rows) {
		for(int countRows = 0; countRows < rows; countRows++) {
			Arrays.fill(segments[countRows], NOT_SEGMENTED);		
		}
	}
	
	/**
	 * takes a single picture and slices it up into different segments,
	 *  each representing a single character
	 * @param segmentAmount number of Segments in the image
	 * @return array containing all possible characters
	 */
	private CharacterCandidate[] makeCharCandidatesOutOfSegments(int segmentAmount) {
		CharacterCandidate[] allSegments = new CharacterCandidate[segmentAmount];
		
		for(int countSegment = SEGMENTS_START_AMOUNT; 
				countSegment < segmentAmount; countSegment++) {
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
			
			allSegments[countSegment] = new CharacterCandidate(leftBorder, rightBorder,
					upperBorder, bottomBorder);	
		}
		
		return allSegments;	
	}
	
	/**
	 * takes the given segments and checks for each one if 
	 * it should be included for the classification
	 * all that should be included will be rescaled
	 * @param allSegments all Segments to consider
	 * @return array containing all possible characters
	 */
	private CharacterCandidate[] checkSizesAndRescale(CharacterCandidate[] allSegments) {
		CharacterCandidate[] arrToReturn = new CharacterCandidate[allSegments.length];
		int currentReturnArrayPos = 0;
		
		for(int countSegment = 0; countSegment < allSegments.length; countSegment++) {
			boolean couldBeChar = CharacterCandidate.checkIfValidSize(allSegments[countSegment],
					CharacterType.CHARACTER);
			boolean couldBeDash = CharacterCandidate.checkIfValidSize(allSegments[countSegment],
					CharacterType.DASH);

			if(couldBeChar || couldBeDash) {
				if(couldBeChar) {
					allSegments[countSegment].checkForUmlautAndChangeBorder(allSegments);
				}	
				
				allSegments[countSegment].cutCharacterFromWholeImage(binarisedImg);
				arrToReturn[currentReturnArrayPos++] = allSegments[countSegment];
			}
		}
		
		return cleanUpAndSortCharacterArray(arrToReturn, currentReturnArrayPos);
	}
	
	/**
	 * shortens the array so that only segments identified as possible characters are used
	 * @param arrayToClean array which needs to be cleaned
	 * @param filledImages number of filled entries in the array 
	 * @return a new, cleaned-up array
	 */
	private CharacterCandidate[] cleanUpAndSortCharacterArray(CharacterCandidate[] arrToProcess,
			int filledImages) {	
		CharacterCandidate[] cleanedArray = Arrays.copyOf(arrToProcess, filledImages);
		Arrays.sort(cleanedArray);
		return cleanedArray;
 	}	
	

}