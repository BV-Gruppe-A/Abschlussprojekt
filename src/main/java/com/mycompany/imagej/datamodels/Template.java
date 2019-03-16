package com.mycompany.imagej.datamodels;

import ij.process.ImageProcessor;

public class Template {

	final private String character;
	final private double mean;
	final private ImageProcessor image;
	
	public Template(String character, double mean, ImageProcessor image) {
		this.character = character;
		this.mean = mean;
		this.image = image;
	}

	public String getCharacter() {
		return character;
	}

	public double getMean() {
		return mean;
	}

	public ImageProcessor getImage() {
		return image;
	}
	
}
