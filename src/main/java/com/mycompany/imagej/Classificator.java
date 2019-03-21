package com.mycompany.imagej;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.mycompany.imagej.datamodels.*;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * @author Alissa Müller
 *
 */
public class Classificator {

	private Template[] templates;
	private String csvName = "list.csv";
	
	/**
	 * 
	 */
	public Classificator() {
		
		initializeTemplates();
	}
	
	
	
	/**
	 * @return
	 */
	public String getCsvName() {
		return csvName;
	}



	/**
	 * @param csvName
	 */
	public void setCsvName(String csvName) {
		if(!csvName.endsWith(".csv")){
			csvName += ".csv";
		}
		this.csvName = csvName;
	}



	/**
	 * reads in all template pictures of characters in FE font and maps it to the character it represents
	 */
	private void initializeTemplates() {
		//String with all possible licence plate characters
		final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ-0123456789";
		//initialize array with alphabets length
		templates = new Template[alphabet.length()];
		
		ImageProcessor img;
		Image template;
		//for each char in the alphabet create a Template object
		for (int i = 0; i < templates.length; i++) {
			template = Toolkit.getDefaultToolkit().getImage((getClass().getClassLoader().getResource("Templates/" + alphabet.substring(i, i+1) + ".png")));
	        img = new ImagePlus(template.toString(),template).getProcessor().convertToByte(false);
	        templates[i] = new Template(alphabet.substring(i, i+1), calcMean(img), img);
	        
	    }
	}

	

	/**
	 * @param characters all characters of one licence plate segregated (each with own image processor)
	 * classifies each character and saves to string that is than writen to an excel file
	 */
	public void classify(CharacterCandidate[] characters, String filename) {
		String licencePlate = "";
		for (CharacterCandidate character : characters) {
			licencePlate += classifyChar(character.getImage());
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
		for ( Template template : templates) {
			val = templateMatch(template, character);
			if(val > maxVal) {
				maxVal = val;
				s = template.getCharacter();
			}
		}
		return s;
	}
	
	/**
	 * uses template matching to determine similarity
	 * @param template the template to compare the sample to
	 * @param sample the sample image to compare to template
	 */
	private double templateMatch(Template template, ImageProcessor sample) {
		int J = sample.getWidth();
		int K = sample.getHeight();
		
		double numerator = 0;
		double denominator1 = 0;
		double  denominator2 = 0;
		
		double sampleMean = calcMean(sample);
		double templateMean = template.getMean();
		
		ImageProcessor templateImg = template.getImage();
		
		for (int j = 0; j < J; j++) {
			for(int k = 0; k < K; k++) {
				//calculate numerator
				numerator += ((double)templateImg.getPixel(j, k) - templateMean) * ((double)sample.getPixel(j , k) - sampleMean);
				
				//calculate denominator1
				denominator1 += Math.pow((double)templateImg.getPixel(j, k) - templateMean, 2);
				
				//calculate denominator2
				denominator2 += Math.pow((double)sample.getPixel(j , k) - sampleMean,2);
			}
		}
		denominator1 = Math.sqrt(denominator1);
		denominator2 = Math.sqrt(denominator2);
		
		return numerator / (denominator1 * denominator2) * 100;
	}


	/**
	 * calculates the mean of the values of all pixels of an image
	 * @param img
	 * @return mean of the images pixel values
	 */
	private double calcMean(ImageProcessor img) {
		double mean = 0.0;
		int J = img.getWidth();
		int K = img.getHeight(); 
		double N = (double) J * K;
		for (int j = 0; j < J; j++) {
			for(int k = 0; k < K; k++) {
				mean += img.getPixel(j, k) / N;		
			}
		}		
		return mean;
	}

	/**
	 * writes one licence plate with filename into a row of a csv file
	 * @param plate plate licence plate as string (result of classification)
	 * @param filename
	 * @throws IOException 
	 */
	private void writeToExcel(String value, String label) throws IOException{
		BufferedWriter writer = null;
		try {
			 writer = new BufferedWriter(new FileWriter(csvName, true));
			 writer.write(label + "," + value + "\n");
			 writer.close();
		}catch(IOException e) {
			if (writer != null)
				writer.close();
		}
	}
	
	public void evaluation() {
		double rate = 0.0;

		HashMap<String,String> results = readResults();
		rate = errorRate(results);
		try {
			writeToExcel(String.format("%.3f", rate),"Classification Rate:");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private double errorRate(HashMap<String,String> results) {
		double rate = 0.0;
		int lengthAllChars = 0;
		int mistakes = 0;
		for(Entry<String, String> result : results.entrySet()) {
			String key = result.getKey();
			String value = result.getValue();
			for (char c : key.toCharArray()) {
				String character = String.valueOf(c);
				if(value.contains(character)){
					value.replaceFirst(character, ".");
				} else {
					mistakes++;
				}
			}
		}
		rate = 1.0 - (double) mistakes / (double) lengthAllChars;
 		return rate;
	}

	private HashMap<String,String> readResults() {
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        HashMap<String,String> results = new HashMap<>();
        
        try {

            br = new BufferedReader(new FileReader(csvName));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] plate = line.split(cvsSplitBy);
                results.put(plate[0],plate[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }
}

