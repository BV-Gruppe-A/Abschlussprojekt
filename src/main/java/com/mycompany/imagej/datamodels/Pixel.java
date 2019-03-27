package com.mycompany.imagej.datamodels;

/**
 * represents a single pixel with its value
 * used for segmentation only
 */
public class Pixel {
	private int xPos;
	private int yPos;
	private int value;
	
	/**
	 * Constructor which sets the three attributes to the given values
	 * @param x coordinate on the x-axis
	 * @param y coordinate on the y-axis
	 * @param val value of the pixel
	 */
	public Pixel(int x, int y, int val) {
		setXPos(x);
		setYPos(y);
		setValue(val);
	}
	
	/**
	 * @return coordinate on the x-axis
	 */
	public int getPosInX() {
		return xPos;
	}
	
	/**
	 * sets the coordinate on the x-axis to a new value
	 * @param newX new coordinate
	 */
	public void setXPos(int newX) {
		xPos = newX;
	}
	
	/**
	 * @return coordinate on the y-axis
	 */
	public int getPosInY() {
		return yPos;
	}
	
	/**
	 * sets the coordinate on the y-axis to a new value
	 * @param newX new coordinate
	 */
	public void setYPos(int newY) {
		yPos = newY;
	}
	
	/**
	 * @return value of this pixel
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * sets the value of this pixel
	 * @param newValue value to set
	 */
	public void setValue(int newValue) {
		value = newValue;
	}
}
