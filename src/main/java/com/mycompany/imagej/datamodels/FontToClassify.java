package com.mycompany.imagej.datamodels;

/**
 * describes with which font the current character should be compared to
 */
public enum FontToClassify {
	/**
	 * should check both fonts
	 */
	BOTH,
	/**
	 * should check only the new fe font
	 */
	FE,
	/**
	 * should check only the old din 1451 font
	 */
	DIN
}
