package com.mycompany.imagej.datamodels;

/**
 * represents a single pixel with its value
 * used for segmentation only
 */
public class Pixel {
	/**
	 * current x-coordinate of the pixel
	 */
	private final int xPos;
	
	/**
	 * current y-coordinate of the pixel
	 */
	private final int yPos;
	
	/**
	 * current value of the pixel
	 */
	private final int value;
	
	/**
	 * Constructor which sets the three attributes to the given values
	 * @param x coordinate on the x-axis
	 * @param y coordinate on the y-axis
	 * @param val value of the pixel
	 */
	public Pixel(int x, int y, int val) {
		xPos = x;
		yPos = y;
		value = val;
	}
	
	/**
	 * @return coordinate on the x-axis
	 */
	public int getPosInX() {
		return xPos;
	}

	
	/**
	 * @return coordinate on the y-axis
	 */
	public int getPosInY() {
		return yPos;
	}

	
	/**
	 * @return value of this pixel
	 */
	public int getValue() {
		return value;
	}

}
