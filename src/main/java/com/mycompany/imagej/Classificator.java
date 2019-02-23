/**
 * 
 */
package com.mycompany.imagej;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import ij.ImagePlus;
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
		//add file explorer
		templates = new TreeMap<>();
		
		final File folder = new File("Templates");
		ImagePlus i;
		for (final File fileEntry : folder.listFiles()) {
	        i = new ImagePlus(fileEntry.getPath());
	        templates.put(fileEntry.getName(), i.getProcessor());
	    }
	}

	

	/**
	 * @param characters all characters of one licence plate segregated (each with own image processor)
	 * classifies each character and saves to string that is than writen to an excel file
	 */
	public void classify(ImageProcessor [] characters, String filename) {
		String licencePlate = "";
		for (ImageProcessor character : characters) {
			licencePlate += classifyChar(character);
		}
		
		try {
			writeToExcel(licencePlate, filename);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
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
	 * @param plate plate licence plate as string (result of classification)
	 * @param filename
	 * writes one licence plate with filename into a row of a csv file
	 * @throws IOException 
	 */
	private void writeToExcel(String plate, String filename) throws IOException{
		BufferedWriter writer = null;
		try {
			 writer = new BufferedWriter(new FileWriter("list.csv", true));
			 writer.write(filename + "," + plate + "\n");
			 writer.close();
			
		}catch(IOException e) {
			if (writer != null)
				writer.close();
		}
		
		
		//write licence plate to excel
	}
}
