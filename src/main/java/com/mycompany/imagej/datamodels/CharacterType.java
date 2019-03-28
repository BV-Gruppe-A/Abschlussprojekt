package com.mycompany.imagej.datamodels;

import ij.IJ;

/**
 * Enum describing the different types of characters which would need to be isolated: 
 * Characters, Dots and Dashes
 * 
 * For whatever reason there are two different types of dashes. 
 * We focus on the long one because the short one is so short that it gets confused a lot
 */
public enum CharacterType {
	CHARACTER,
	DOT,
	DASH;
	
	// public constant indexes of the sizes array
	public final static int INDEX_MAX_WIDTH = 0;
	public final static int INDEX_MIN_WIDTH = 1;
	public final static int INDEX_MAX_HEIGHT = 2;
	public final static int INDEX_MIN_HEIGHT = 3;
	
	private final static double CHAR_WIDTH_MAX = 0.13;
	private final static double CHAR_WIDTH_MIN = 0.05;
	private final static double CHAR_HEIGHT_MAX = 0.8;
	private final static double CHAR_HEIGHT_MIN = 0.5;
	
	private final static double DASH_WIDTH_MAX = 0.09;
	private final static double DASH_WIDTH_MIN = 0.05;
	private final static double DASH_HEIGHT_MAX = 0.08;
	private final static double DASH_HEIGHT_MIN = 0.06;
	
	private final static double DOT_WIDTH_MAX = 0.05;
	private final static double DOT_WIDTH_MIN = 0.01;
	private final static double DOT_HEIGHT_MAX = 0.1;
	private final static double DOT_HEIGHT_MIN = 0.06;
	
	public final static double SPACE_WIDTH_MAX = 0.14;
	public final static double SPACE_WIDTH_MIN = 0.035;
	
	/**
	 * gets the min and max sizes for a certain character Type
	 * @param type type to get the sizes for
	 * @return int array containing the min and max sizes 
	 */
	public static double[] getSizesForCharacterType(CharacterType type) {
		double[] sizes = new double[4];
		
		switch(type) {
		case CHARACTER:
			sizes[INDEX_MAX_WIDTH] = CHAR_WIDTH_MAX;
			sizes[INDEX_MIN_WIDTH] = CHAR_WIDTH_MIN;
			sizes[INDEX_MAX_HEIGHT] = CHAR_HEIGHT_MAX;
			sizes[INDEX_MIN_HEIGHT] = CHAR_HEIGHT_MIN;
			break;
			
		case DOT:
			sizes[INDEX_MAX_WIDTH] = DOT_WIDTH_MAX;
			sizes[INDEX_MIN_WIDTH] = DOT_WIDTH_MIN;
			sizes[INDEX_MAX_HEIGHT] = DOT_HEIGHT_MAX;
			sizes[INDEX_MIN_HEIGHT] = DOT_HEIGHT_MIN;
			break;
			
		case DASH:
			sizes[INDEX_MAX_WIDTH] = DASH_WIDTH_MAX;
			sizes[INDEX_MIN_WIDTH] = DASH_WIDTH_MIN;
			sizes[INDEX_MAX_HEIGHT] = DASH_HEIGHT_MAX;
			sizes[INDEX_MIN_HEIGHT] = DASH_HEIGHT_MIN;
			break;
		
		default:
			IJ.error("Wrong Character Type");
			break;		
		}
		
		return sizes;
	}
}
