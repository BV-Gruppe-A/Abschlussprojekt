package com.mycompany.imagej.datamodels;

import ij.process.ImageProcessor;

/**
 * This class represents one template of a character used to match in automatic licence identification
 *
 */
public class Template {
	/**
	 * The height of all templates
	 */
	public static final int HEIGHT = 38;
	
	/**
	 * The Width of all templates
	 */
	public static final int WIDTH = 25;

	/**
	 * The character the template represents
	 */
	final private String character;
	
	/**
	 * The mean of the pixel values of the templates image
	 */
	final private double mean;
	
	/**
	 * The template image
	 */
	final private ImageProcessor image;
	
	/**
	 * Creates a new template
	 * @param character The character the template represents
	 * @param mean The mean of the pixel values of the templates image
	 * @param image The template image
	 */
	public Template(String character, double mean, ImageProcessor image) {
		this.character = character;
		this.mean = mean;
		this.image = image;
	}

	/**
	 * @return the character the template represents
	 */
	public String getCharacter() {
		return character;
	}

	/**
	 * @return the mean of the template image
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * @return the image of the template
	 */
	public ImageProcessor getImage() {
		return image;
	}
	
}
