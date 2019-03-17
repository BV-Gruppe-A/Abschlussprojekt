package com.mycompany.imagej.datamodels;

import ij.process.ImageProcessor;

/**
 * models one segment, which could be a character to classify
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
	
	/**
	 * Constructor which sets the boundaries of this character
	 * @param left x-coordinate of the left Border of the char
	 * @param right x-coordinate of the right Border of the char
	 * @param up y-coordinate of the upper Border of the char
	 * @param down y-coordinate of the bottom Border of the char
	 */
	public CharacterCandidate(int left, int right, int up, int down) {
		setLeftBorder(left);
		setRightBorder(right);
		setUpperBorder(up);
		setBottomBorder(down);
	}
	
	/**
	 * calculates the height of the char after bottom or upper border were changed
	 */
	private void calculateHeight() {
		height = bottomBorder > upperBorder ? bottomBorder - upperBorder : upperBorder - bottomBorder;
	}
	
	/**
	 * calculates the width of the char after left or right border were changed
	 */
	private void calculateWidth() {
		width = rightBorder > leftBorder ? rightBorder - leftBorder : leftBorder - rightBorder;
	}

	/**
	 * @return imageProcessor which represents this character
	 */
	public ImageProcessor getImage() {
		return image;
	}

	/**
	 * sets the image Processor to a scaled variant of the given Image
	 * @param wholeImage the whole Image to get the character from
	 */
	public void setAndScaleImage(ImageProcessor wholeImage) {
		image = wholeImage.createProcessor(width, height);
		for(int countInY = 0; countInY < height; countInY++) {
			for(int countInX = 0; countInX < width; countInX++) {
				int pixelToPut = wholeImage.getPixel(getLeftBorder() + countInX, getUpperBorder() + countInY);
				image.putPixel(countInX, countInY, pixelToPut);
			}
		}
		image = image.resize(OPT_CHAR_WIDTH, OPT_CHAR_HEIGHT);
	}

	/**
	 * @return x-coordinate of the most left black pixel
	 */
	public int getLeftBorder() {
		return leftBorder;
	}

	/**
	 * sets the x-coordinate of the most left black pixel to the given value
	 * & refreshes the width
	 * @param leftBorder new coordinate
	 */
	public void setLeftBorder(int leftBorder) {
		this.leftBorder = leftBorder;
		calculateWidth();
	}

	/**
	 * @return x-coordinate of the most left black pixel
	 */
	public int getRightBorder() {
		return rightBorder;
	}

	/**
	 * sets the x-coordinate of the most right black pixel to the given value
	 * & refreshes the width
	 * @param rightBorder new coordinate
	 */
	public void setRightBorder(int rightBorder) {
		this.rightBorder = rightBorder;
		calculateWidth();
	}

	/**
	 * @return y-coordinate of the highest black pixel
	 */
	public int getUpperBorder() {
		return upperBorder;
	}

	/**
	 * sets the y-coordinate of the highest black pixel to the given value
	 * & refreshes the height
	 * @param upperBorder new coordinate
	 */
	public void setUpperBorder(int upperBorder) {
		this.upperBorder = upperBorder;
		calculateHeight();
	}

	/**
	 * @return y-coordinate of the lowest black pixel
	 */
	public int getBottomBorder() {
		return bottomBorder;
	}

	/**
	 * sets the y-coordinate of the lowest black pixel to the given value
	 * & refreshes the height
	 * @param bottomBorder new coordinate
	 */
	public void setBottomBorder(int bottomBorder) {
		this.bottomBorder = bottomBorder;
		calculateHeight();
	}
	
	/**
	 * checks if this segment has a size which could be a character
	 * @return true if this segment is not a valid possibility for a character
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

	
	/**
	 * compares this character to another one for sorting
	 * Criteria:
	 * - the lower value for the left Border should be smaller
	 * - if the bottom Border is above the higher Border of the other, than the higher one should be smaller
	 *  @param otherCandidate other possible Character to compare with
	 *  @return positive value if this should be sorted higher than the other
	 *  		negative value if this should be sorted lower than the other
	 *  		zero if this is euqal to the other
	 */
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
