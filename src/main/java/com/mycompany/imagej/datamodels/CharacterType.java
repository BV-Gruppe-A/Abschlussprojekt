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
	
	// constant values for the sizes
	private final static double THRESHOLD = 0.3;
	// constant height of a character
	public final static int OPT_CHAR_HEIGHT = 38;
	private final static int MAX_CHAR_HEIGHT = OPT_CHAR_HEIGHT + (int) Math.round(OPT_CHAR_HEIGHT * THRESHOLD);
	private final static int MIN_CHAR_HEIGHT = OPT_CHAR_HEIGHT - (int) Math.round(OPT_CHAR_HEIGHT * THRESHOLD);
	// constant width of a character or a dash	
	public final static int OPT_CHARDASH_WIDTH = 25;
	private final static int MAX_CHARDASH_WIDTH = OPT_CHARDASH_WIDTH + (int) Math.round(OPT_CHARDASH_WIDTH * THRESHOLD);
	private final static int MIN_CHARDASH_WIDTH = OPT_CHARDASH_WIDTH - (int) Math.round(OPT_CHARDASH_WIDTH * THRESHOLD);
	// constant height of a dot or a dash
	private final static int OPT_DOTDASH_HEIGHT = 6;
	private final static int MAX_DOTDASH_HEIGHT = OPT_DOTDASH_HEIGHT + (int) Math.round(OPT_DOTDASH_HEIGHT * THRESHOLD);
	private final static int MIN_DOTDASH_HEIGHT = OPT_DOTDASH_HEIGHT - (int) Math.round(OPT_DOTDASH_HEIGHT * THRESHOLD);
	//constant width of a dot
	private final static int OPT_DOT_WIDTH = 10;	
	private final static int MAX_DOT_WIDTH = OPT_DOT_WIDTH + (int) Math.round(OPT_DOT_WIDTH * THRESHOLD);
	private final static int MIN_DOT_WIDTH = OPT_DOT_WIDTH - (int) Math.round(OPT_DOT_WIDTH * THRESHOLD);
	
	/**
	 * gets the min and max sizes for a certain character Type
	 * @param type type to get the sizes for
	 * @return int array containing the min and max sizes 
	 */
	public static int[] getSizesForCharacterType(CharacterType type) {
		int[] sizes = new int[4];
		
		switch(type) {
		case CHARACTER:
			sizes[INDEX_MAX_WIDTH] = MAX_CHARDASH_WIDTH;
			sizes[INDEX_MIN_WIDTH] = MIN_CHARDASH_WIDTH;
			sizes[INDEX_MAX_HEIGHT] = MAX_CHAR_HEIGHT;
			sizes[INDEX_MIN_HEIGHT] = MIN_CHAR_HEIGHT;
			break;
			
		case DOT:
			sizes[INDEX_MAX_WIDTH] = MAX_DOT_WIDTH;
			sizes[INDEX_MIN_WIDTH] = MIN_DOT_WIDTH;
			sizes[INDEX_MAX_HEIGHT] = MAX_DOTDASH_HEIGHT;
			sizes[INDEX_MIN_HEIGHT] = MIN_DOTDASH_HEIGHT;
			break;
			
		case DASH:
			sizes[INDEX_MAX_WIDTH] = MAX_CHARDASH_WIDTH;
			sizes[INDEX_MIN_WIDTH] = MIN_CHARDASH_WIDTH;
			sizes[INDEX_MAX_HEIGHT] = MAX_DOTDASH_HEIGHT;
			sizes[INDEX_MIN_HEIGHT] = MIN_DOTDASH_HEIGHT;
			break;
		
		default:
			IJ.error("Wrong Character Type");
			break;		
		}
		
		return sizes;
	}
}
