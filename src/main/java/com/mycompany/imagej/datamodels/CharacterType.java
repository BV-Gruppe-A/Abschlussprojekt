package com.mycompany.imagej.datamodels;

import ij.IJ;

/**
 * Enum describing the different types of characters which would need to be isolated: 
 * Characters, Dots and Dashes
 * 
 * the different percentages are based on the example pictures provided
 */
public enum CharacterType {
	/**
	 * candidate is a character
	 */
	CHARACTER,
	/**
	 * candidate is a dot for an umlaut
	 */
	DOT,
	/**
	 * candidate is a dash
	 */
	DASH;
	
	/**
	 * Index for the returned sizes array
	 * resembling the max width
	 */
	public final static int INDEX_MAX_WIDTH = 0;
	
	/**
	 * Index for the returned sizes array
	 * resembling the min width
	 */
	public final static int INDEX_MIN_WIDTH = 1;
	
	/**
	 * Index for the returned sizes array
	 * resembling the max height
	 */
	public final static int INDEX_MAX_HEIGHT = 2;
	
	/**
	 * Index for the returned sizes array
	 * resembling the min height
	 */
	public final static int INDEX_MIN_HEIGHT = 3;
	
	/**
	 * max width of a char as a percentage of the whole image
	 */
	private final static double CHAR_WIDTH_MAX = 0.13;
	
	/**
	 * min width of a char as a percentage of the whole image
	 */
	private final static double CHAR_WIDTH_MIN = 0.05;
	
	/**
	 * max height of a char as a percentage of the whole image
	 */
	private final static double CHAR_HEIGHT_MAX = 0.8;
	
	/**
	 * min height of a char as a percentage of the whole image
	 */
	private final static double CHAR_HEIGHT_MIN = 0.5;
	
	/**
	 * max width of a dash as a percentage of the whole image
	 */
	private final static double DASH_WIDTH_MAX = 0.09;
	
	/**
	 * min width of a dash as a percentage of the whole image
	 */
	private final static double DASH_WIDTH_MIN = 0.05;
	
	/**
	 * max height of a dash as a percentage of the whole image
	 */
	private final static double DASH_HEIGHT_MAX = 0.08;
	
	/**
	 * min height of a dash as a percentage of the whole image
	 */
	private final static double DASH_HEIGHT_MIN = 0.06;
	
	/**
	 * max width of a dot as a percentage of the whole image
	 */
	private final static double DOT_WIDTH_MAX = 0.05;
	
	/**
	 * min width of a dot as a percentage of the whole image
	 */
	private final static double DOT_WIDTH_MIN = 0.01;
	
	/**
	 * max height of a dot as a percentage of the whole image
	 */
	private final static double DOT_HEIGHT_MAX = 0.1;
	
	/**
	 * min height of a dot as a percentage of the whole image
	 */
	private final static double DOT_HEIGHT_MIN = 0.06;
	
	/**
	 * max width of a space as a percentage of the whole image
	 */
	public final static double SPACE_WIDTH_MAX = 0.14;
	
	/**
	 * min width of a space as a percentage of the whole image
	 */
	public final static double SPACE_WIDTH_MIN = 0.035;
	
	/**
	 * gets the min and max sizes for a certain character Type
	 * @param type type to get the sizes for
	 * @return double array containing the min and max percentage
	 */
	public static double[] getSizesForCharacterType(CharacterType type) {
		double[] percentage = new double[4];
		
		switch(type) {
		case CHARACTER:
			percentage[INDEX_MAX_WIDTH] = CHAR_WIDTH_MAX;
			percentage[INDEX_MIN_WIDTH] = CHAR_WIDTH_MIN;
			percentage[INDEX_MAX_HEIGHT] = CHAR_HEIGHT_MAX;
			percentage[INDEX_MIN_HEIGHT] = CHAR_HEIGHT_MIN;
			break;
			
		case DOT:
			percentage[INDEX_MAX_WIDTH] = DOT_WIDTH_MAX;
			percentage[INDEX_MIN_WIDTH] = DOT_WIDTH_MIN;
			percentage[INDEX_MAX_HEIGHT] = DOT_HEIGHT_MAX;
			percentage[INDEX_MIN_HEIGHT] = DOT_HEIGHT_MIN;
			break;
			
		case DASH:
			percentage[INDEX_MAX_WIDTH] = DASH_WIDTH_MAX;
			percentage[INDEX_MIN_WIDTH] = DASH_WIDTH_MIN;
			percentage[INDEX_MAX_HEIGHT] = DASH_HEIGHT_MAX;
			percentage[INDEX_MIN_HEIGHT] = DASH_HEIGHT_MIN;
			break;
		
		default:
			IJ.error("Wrong Character Type");
			break;		
		}
		
		return percentage;
	}
}
