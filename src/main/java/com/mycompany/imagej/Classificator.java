/**
 * 
 */
package com.mycompany.imagej;

import java.util.TreeMap;

import ij.process.ImageProcessor;

/**
 * @author Alissa Müller
 *
 */
public class Classificator {

	private TreeMap<String, ImageProcessor> templates;
	
	public Classificator() {
		initializeTemplates();
	}
	
	/**
	 * reads in all template pictures of characters in FE font and maps it to the character it represents
	 */
	private void initializeTemplates() {
		// TODO read all pictures in certain folder into tree map with character
		
	}

	/**
	 * @param characters all characters of one licence plate segregated (each with own image processor)
	 * classifies each character and saves to string that is than writen to an excel file
	 */
	private void classify(ImageProcessor [] characters) {
		String licencePlate = "";
		for (ImageProcessor character : characters) {
			licencePlate += classifyChar(character);
		}
		writeToExcel(licencePlate);
	}
	
	/**
	 * @param character to be characterized
	 * Does template matching of character with each template and chooses best option
	 * @return Character that the image represents most likely
	 */
	private String classifyChar(ImageProcessor character) {
		String s = null;
		for (ImageProcessor template : templates.values()) {
			templateMatch(template, character);
		}
		return s;
	}
	
	/**
	 * @param template the template to compare the sample to
	 * @param sample the sample image to compare to template
	 * uses template matching to determine similarity
	 */
	private void templateMatch(ImageProcessor template, ImageProcessor sample) {
		// TODO correlate pictures und calculate similarity
		
	}

	/**
	 * @param s licence plate as string (result of classification)
	 * writes one licence plate to an Excel file
	 */
	private void writeToExcel(String s){
		//write licence plate to excel
	}
}
