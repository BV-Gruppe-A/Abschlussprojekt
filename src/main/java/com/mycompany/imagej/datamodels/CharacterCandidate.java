package com.mycompany.imagej.datamodels;

import ij.process.ImageProcessor;

/**
 * models one segment, which could be a character to classify
 * implements Comparable to enable sorting of arrays of this type
 */
public class CharacterCandidate implements Comparable<CharacterCandidate> {
	// local objects
	private ImageProcessor image;
	
	// local variables
	private int leftBorder;
	private int rightBorder;
	private int upperBorder;
	private int bottomBorder;
	
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
	 * @return imageProcessor which represents this character
	 */
	public ImageProcessor getImage() {
		return image;
	}

	/**
	 * @return x-coordinate of the most left black pixel
	 */
	public int getLeftBorder() {
		return leftBorder;
	}

	/**
	 * sets the x-coordinate of the most left black pixel to the given value
	 * @param leftBorder new coordinate
	 */
	public void setLeftBorder(int leftBorder) {
		this.leftBorder = leftBorder;
	}

	/**
	 * @return x-coordinate of the most left black pixel
	 */
	public int getRightBorder() {
		return rightBorder;
	}

	/**
	 * sets the x-coordinate of the most right black pixel to the given value
	 * @param rightBorder new coordinate
	 */
	public void setRightBorder(int rightBorder) {
		this.rightBorder = rightBorder;
	}

	/**
	 * @return y-coordinate of the highest black pixel
	 */
	public int getUpperBorder() {
		return upperBorder;
	}

	/**
	 * sets the y-coordinate of the highest black pixel to the given value
	 * @param upperBorder new coordinate
	 */
	public void setUpperBorder(int upperBorder) {
		this.upperBorder = upperBorder;
	}

	/**
	 * @return y-coordinate of the lowest black pixel
	 */
	public int getBottomBorder() {
		return bottomBorder;
	}

	/**
	 * sets the y-coordinate of the lowest black pixel to the given value
	 * @param bottomBorder new coordinate
	 */
	public void setBottomBorder(int bottomBorder) {
		this.bottomBorder = bottomBorder;
	}
	
	/**
	 * @return width of the candidate
	 */
	public int getWidth() {
		return getRightBorder() - getLeftBorder();
	}
	
	/**
	 * @return height of the candidate
	 */
	public int getHeight() {
		return getBottomBorder() - getUpperBorder();
	}
	
	/**
	 * checks if a given candidate has a size that could either be a character, a dot or a dash
	 * @param toCheck the candidate to check
	 * @param typeToCheckFor the type of character to check for
	 * @return true if this segment is a valid possibility for a character, dot or dash
	 */
	public static boolean checkIfValidSize(CharacterCandidate toCheck, CharacterType typeToCheckFor) {
		int[] sizes = CharacterType.getSizesForCharacterType(typeToCheckFor);
		
		if(toCheck.getWidth() > sizes[CharacterType.INDEX_MAX_WIDTH] || toCheck.getHeight() > sizes[CharacterType.INDEX_MAX_HEIGHT]) {
			return false;
		}
		
		if(toCheck.getWidth() < sizes[CharacterType.INDEX_MIN_WIDTH] || toCheck.getHeight() < sizes[CharacterType.INDEX_MIN_HEIGHT]) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * checks if the candidate is an umlaut and changes the border accordingly
	 * @param allSegments array containing all segments as character candidates
	 */
	public void checkForUmlautAndChangeBorder(CharacterCandidate[] allSegments) {
		CharacterCandidate[] dots = checkForUmlaut(allSegments);
		if(!checkForNull(dots)) {
			changeBordersToUmlaut(dots);
		}
	}

	/**
	 * sets the image Processor to a scaled variant of the given Image
	 * @param wholeImage whole number plate as an image processor
	 */
	public void setAndScaleImage(ImageProcessor wholeImage) {
		image = wholeImage.createProcessor(getWidth(), getHeight());
		for(int countInY = 0; countInY < getHeight(); countInY++) {
			for(int countInX = 0; countInX < getWidth(); countInX++) {
				int pixelToPut = wholeImage.getPixel(getLeftBorder() + countInX, getUpperBorder() + countInY);
				image.putPixel(countInX, countInY, pixelToPut);
			}
		}
		image = image.resize(CharacterType.OPT_CHARDASH_WIDTH, CharacterType.OPT_CHAR_HEIGHT);
	}
	
	/**
	 * checks if the current segment is an umlaut
	 * @param allSegments array containing all segments as character candidates
	 * @return array containing the two possible dots of the umlaut
	 */
	private CharacterCandidate[] checkForUmlaut(CharacterCandidate[] allSegments) {
		CharacterCandidate[] dots = new CharacterCandidate[2];
		int currentDot = 0;
		
		for(int countSegments = 0; countSegments < allSegments.length; countSegments++) {
			if(allSegments[countSegments].getBottomBorder() <= getUpperBorder()) {
				if(checkIfValidSize(allSegments[countSegments], CharacterType.DOT)) {
					dots[currentDot++] = allSegments[countSegments];
				}
			}
		}
		
		return dots;
	}
	
	/**
	 * changes the borders of the current candidate to the borders of the given dots (if necessary)
	 * @param dots array containing the two dots 
	 */
	private void changeBordersToUmlaut(CharacterCandidate[] dots) {
		int leftDot = dots[0].compareTo(dots[1]) <= 0 ? 0 : 1;
		int rightDot = leftDot == 1 ? 0 : 1;
		int higherDot = dots[0].getUpperBorder() <= dots[1].getUpperBorder() ? 0 : 1;
		
		if(getLeftBorder() > dots[leftDot].getLeftBorder()) {
			setLeftBorder(dots[leftDot].getLeftBorder());
		}
		
		if(getRightBorder() < dots[rightDot].getRightBorder()) {
			setRightBorder(dots[rightDot].getRightBorder());
		}
		
		if(getUpperBorder() > dots[higherDot].getUpperBorder()) {
			setUpperBorder(dots[higherDot].getUpperBorder());
		}
	}
	
	/**
	 * checks if an array of candidates contains a null-value
	 * @param possibleDots array to check
	 * @return true if at least one value is null
	 */
	private boolean checkForNull(CharacterCandidate[] possibleDots) {
		for(int count = 0; count < possibleDots.length; count++) {
			if(possibleDots[count] == null) {
				return true;
			}
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
