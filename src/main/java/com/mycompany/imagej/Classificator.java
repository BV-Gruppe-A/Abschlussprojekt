/**
 * 
 */
package com.mycompany.imagej;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import ij.IJ;
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
	        templates.put(fileEntry.getName().replaceFirst("[.][^.]+$", ""), i.getProcessor().convertToByte(false));
	    }
		/*for ( Map.Entry<String, ImageProcessor> template : templates.entrySet()) {
			template.getValue().invert();
		}*/
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
		double maxVal = -1;
		double val;
		for ( Map.Entry<String, ImageProcessor> template : templates.entrySet()) {
			val = templateMatch(template.getValue(), character);
			// IJ.log(template.getKey() + ": " + val);
			if(val > maxVal) {
				maxVal = val;
				s = template.getKey();
			}
		}
		return s;
	}
	
	/**
	 * @param template the template to compare the sample to
	 * @param sample the sample image to compare to template
	 * uses template matching to determine similarity
	 */
	private double templateMatch(ImageProcessor template, ImageProcessor sample) {
		// TODO correlate pictures and calculate similarity
		int J = template.getWidth();
		int K = template.getHeight();
		
		double numerator = 0;
		double denominator1 = 0;
		double  denominator2 = 0;
		
		double sampleMean = calcMean(sample);
		double templateMean = calcMean(template);
		
		for (int j = 0; j < J; j++) {
			for(int k = 0; k < K; k++) {
				//calculate numerator
				numerator += ((double)template.getPixel(j, k) - templateMean) * ((double)sample.getPixel(j , k) - sampleMean);
				
				//calculate denominator1
				denominator1 += Math.pow((double)template.getPixel(j, k) - templateMean, 2);
				
				//calculate denominator2
				denominator2 += Math.pow((double)sample.getPixel(j , k) - sampleMean,2);
			}
		}
		denominator1 = Math.sqrt(denominator1);
		denominator2 = Math.sqrt(denominator2);
		
		return numerator / (denominator1 * denominator2) * 100;
	}


	private double calcMean(ImageProcessor img) {
		double mean = 0.0;
		int J = img.getWidth();
		int K = img.getHeight(); 
		// IJ.log(K + "," + J);
		double N = (double) J * K;
		for (int j = 0; j < J; j++) {
			for(int k = 0; k < K; k++) {
				mean += img.getPixel(j, k) / N;
				
			}
		}
		//mean = mean / (double) img.getPixelCount();
		
		return mean;
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
