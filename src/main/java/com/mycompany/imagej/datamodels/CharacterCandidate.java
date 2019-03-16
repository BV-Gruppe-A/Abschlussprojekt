package com.mycompany.imagej.datamodels;

import ij.process.ImageProcessor;

/**
 *
 */
public class CharacterCandidate implements Comparable<CharacterCandidate> {
	// constant values
	private final double THRESHOLD = 0.25;	
	private final int OPT_CHAR_HEIGHT = 38;
	private final int MAX_CHAR_HEIGHT = OPT_CHAR_HEIGHT + (int) Math.round(OPT_CHAR_HEIGHT * THRESHOLD);
	private final int MIN_CHAR_HEIGHT = OPT_CHAR_HEIGHT - (int) Math.round(OPT_CHAR_HEIGHT * THRESHOLD);
	
	private final int OPT_CHAR_WIDTH = 25;
	private final int MAX_CHAR_WIDTH = OPT_CHAR_WIDTH + (int) Math.round(OPT_CHAR_WIDTH * THRESHOLD);
	private final int MIN_CHAR_WIDTH = OPT_CHAR_WIDTH - (int) Math.round(OPT_CHAR_WIDTH * THRESHOLD);
	
	// local objects
	private ImageProcessor image;
	
	// local variables
	private int leftBorder;
	private int rightBorder;
	private int upperBorder;
	private int bottomBorder;
	
	private int width;
	private int height;
	
	public CharacterCandidate(int left, int right, int up, int down) {
		setLeftBorder(left);
		setRightBorder(right);
		setUpperBorder(up);
		setBottomBorder(down);
	}
	
	private void calculateHeight() {
		height = bottomBorder - upperBorder;
	}
	
	private void calculateWidth() {
		width = rightBorder - leftBorder;
	}

	public ImageProcessor getImage() {
		return image;
	}

	public void setAndScaleImage(ImageProcessor wholeImage) {
		image = wholeImage.createProcessor(width, height);
		for(int countInY = 0; countInY < height; countInY++) {
			for(int countInX = 0; countInX < width; countInX++) {
				int pixelToPut = wholeImage.getPixel(leftBorder + countInX, upperBorder + countInY);
				image.putPixel(countInX, countInY, pixelToPut);
			}
		}
		image = image.resize(OPT_CHAR_WIDTH, OPT_CHAR_HEIGHT);
	}

	public int getLeftBorder() {
		return leftBorder;
	}

	public void setLeftBorder(int leftBorder) {
		this.leftBorder = leftBorder;
		calculateWidth();
	}

	public int getRightBorder() {
		return rightBorder;
	}

	public void setRightBorder(int rightBorder) {
		this.rightBorder = rightBorder;
		calculateWidth();
	}

	public int getUpperBorder() {
		return upperBorder;
	}

	public void setUpperBorder(int upperBorder) {
		this.upperBorder = upperBorder;
		calculateHeight();
	}

	public int getBottomBorder() {
		return bottomBorder;
	}

	public void setBottomBorder(int bottomBorder) {
		this.bottomBorder = bottomBorder;
		calculateHeight();
	}
	
	/**
	 * checks if a segment has a size which could be a character
	 * @param width width of the segment
	 * @param height height of the segment
	 * @return true if the segment is not a valid possibility for a character
	 */
	public boolean checkIfInvalidCharacter() {
		if(width > MAX_CHAR_WIDTH || height > MAX_CHAR_HEIGHT) {
			return true;
		}
		
		if(width < MIN_CHAR_WIDTH || height < MIN_CHAR_HEIGHT) {
			return true;
		}
		
		return false;
	}

	@Override
	public int compareTo(CharacterCandidate otherCandidate) {	
		if(getBottomBorder() < otherCandidate.getUpperBorder() || getLeftBorder() < otherCandidate.getLeftBorder()) {
			return -1;
		} else if (getLeftBorder() == otherCandidate.getLeftBorder()) {
			return 0;
		} 
		return 1;
	}	
}
