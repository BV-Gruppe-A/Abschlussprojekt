package com.mycompany.imagej;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import com.mycompany.imagej.datamodels.*;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * @author Alissa Müller
 *
 */
public class Classificator {
	//String with all possible licence plate characters
	final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ-0123456789";	
	
	private Template[] templates_fe;
	private Template[] templates_din;
	private boolean[] charIsFe;
	private String csvName = "list.csv";
	private HashMap<String,String> results;
	
	/**
	 * 
	 */
	public Classificator() {
		results = new HashMap<>();
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
		//initialize array with alphabets length
		templates_fe = new Template[alphabet.length()];
		templates_din = new Template[alphabet.length()];
		ImageProcessor img;
		Image template;
		//for each char in the alphabet create a Template object
		for (int i = 0; i < templates_fe.length; i++) {
			template = Toolkit.getDefaultToolkit().getImage((getClass().getClassLoader().getResource("Templates_FE/" + alphabet.substring(i, i+1) + ".png")));
	        img = new ImagePlus(template.toString(),template).getProcessor().convertToByte(false);
	        templates_fe[i] = new Template(alphabet.substring(i, i+1), calcMean(img), img);
	        
	    }
		for (int i = 0; i < templates_din.length; i++) {
			template = Toolkit.getDefaultToolkit().getImage((getClass().getClassLoader().getResource("Templates_DIN/" + alphabet.substring(i, i+1) + ".png")));
	        img = new ImagePlus(template.toString(),template).getProcessor().convertToByte(false);
	        templates_din[i] = new Template(alphabet.substring(i, i+1), calcMean(img), img);
	        
	    }
	}

	

	/**
	 * @param characters all characters of one licence plate segregated (each with own image processor)
	 * classifies each character and saves to string that is than writen to an excel file
	 */
	public void classify(CharacterCandidate[] characters, String filename) {
		String licencePlate = "";
		charIsFe = new boolean[characters.length];
		int[] spaces = new int[2];
		int countSpaces = 0;
		
		for (int i = 0; i < characters.length; i++) {
			licencePlate += classifyChar(characters[i], i, FontToClassify.BOTH);
			if(i < characters.length - 1) {
				if(characters[i+1].getLeftBorder() - characters[i].getRightBorder() > 10) {
					spaces[countSpaces++] = i + 1;
				}
			}
		}
		
		licencePlate = balanceTheFont(licencePlate, characters);
		
		for(int i = 0; i < spaces.length; i++) {
			if(spaces[i] != 0) {
				licencePlate = licencePlate.substring(0, spaces[i]) + "_" + licencePlate.substring(spaces[i], licencePlate.length());
				if(i + 1 < spaces.length && spaces[i + 1] != 0) {
					spaces[i + 1]++;
				}				
			}
		}
		
		results.put(filename,licencePlate);
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
	private String classifyChar(CharacterCandidate character, int currentChar, FontToClassify font) {
		Template bestChoiceDin = null;
		Template bestChoiceFE = null;
		String s;
		double maxVal_fe = -100;
		double val_fe;
		double maxVal_din = -100;
		double val_din;
		character.setMean(calcMean(character.getImage()));
		
		if(character.getMean() == 0) {
			maxVal_din = 100.0;
			bestChoiceFE = templates_fe[alphabet.indexOf("-")];
			bestChoiceDin = templates_din[alphabet.indexOf("-")];
		} else {
			for (int i = 0; i < alphabet.length(); i++) {
				if(font != FontToClassify.DIN) {
					val_fe = templateMatch(templates_fe[i], character);
					if(val_fe > maxVal_fe) {
						maxVal_fe = val_fe;
						bestChoiceFE = templates_fe[i];
					}
				} 
				if(font != FontToClassify.FE) {
					val_din = templateMatch(templates_din[i], character);
					if(val_din > maxVal_din) {
						maxVal_din = val_din;
						bestChoiceDin = templates_din[i];
					}
				}				
			}
		}
		
		switch(font) {
		case BOTH:
			if(maxVal_fe > maxVal_din) {
				charIsFe[currentChar] = true;
				s = hardships_fe(bestChoiceFE,character);
				// IJ.log("This is 1. fe = " + s);
			} else {
				charIsFe[currentChar] = false;
				s = hardship_din(bestChoiceDin,character);
				// IJ.log("This is 1. din = " + s);
			}
			break;
			
		case DIN:
			s = hardship_din(bestChoiceDin,character);
			// IJ.log("This is 2. din = " + s);
			break;
			
		case FE:
			s = hardships_fe(bestChoiceFE,character);
			// IJ.log("This is 2. fe = " + s);
			break;
			
		default:
			IJ.log("No usable case!");
			s = "";
			break;
		}
		
		return s;
	}
	
	/**
	 * checks which font was used more and re-classifies the chars which used the othe one
	 * @param currentPlate current recognised characters
	 * @param characters all possible characters
	 * @return changed string containing all changed characters
	 */
	private String balanceTheFont(String currentPlate, CharacterCandidate[] characters) {
		int countDin = 0, countFe = 0;
		for(int count = 0; count < charIsFe.length; count++) {
			if(charIsFe[count] == true) {
				countFe++;
			} else {
				countDin++;
			}
		}
		
		for(int count = 0; count < charIsFe.length; count++) {
			if(countFe >= countDin) {
				if(charIsFe[count] == false) {
					currentPlate.toCharArray()[count] = classifyChar(characters[count], count, FontToClassify.FE).charAt(0);
				}
			} else {
				if(charIsFe[count] == true) {
					currentPlate.toCharArray()[count] = classifyChar(characters[count], count, FontToClassify.DIN).charAt(0);
				}
			}
		}
		
		return currentPlate;
	}
	
	private String hardship_din(Template bestChoice, CharacterCandidate character) {
		String s = bestChoice.getCharacter();
		double mean = 0.0;
		switch(bestChoice.getCharacter()) {
		case "0":
			break;
		case "O":
			break;
		case "I":
		case "1":
			if(character.getHeight() <= character.getWidth()) {
				s = "-";
			}else {
				mean = calcMean(character.getImage(),1,6,8,12);
				IJ.log(String.format("%.3f", mean));
				if(mean > 80) {
					s = "I";
				} else {
					s = "1";
				}
			}
			break;
		case "D":
			break;
		case "H":
		case "N":
			mean = (calcMean(character.getImage(),14,19,24,28) + calcMean(character.getImage(),6,9,7,11)) /2;
			if(mean > 80) {
				s = "H";
			} else {
				s = "N";
			}
			break;
			
		}
		return s;
	}



	private String hardships_fe(Template bestChoice, CharacterCandidate character) {
		String s = bestChoice.getCharacter();
		switch(bestChoice.getCharacter()) {
		case "-":
		case "I":
			if(character.getHeight() > character.getWidth()) {
				s = "I";
			}else {
				s = "-";
			}
			break;
		}
		return s;
	}



	/**
	 * uses template matching to determine similarity
	 * @param template the template to compare the sample to
	 * @param sample the sample image to compare to template
	 */
	private double templateMatch(Template template, CharacterCandidate candidate) {
		ImageProcessor sample = candidate.getImage();
		int J = sample.getWidth();
		int K = sample.getHeight();
		
		double numerator = 0;
		double denominator1 = 0;
		double  denominator2 = 0;
		
		double sampleMean = candidate.getMean();
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
		double mean = calcMean(img,0, img.getWidth(),0, img.getHeight());
		return mean;
	}

	/**
	 * calculates the mean of the values of a specified rectangle of pixels of an image
	 * @param img
	 * @return mean of the images pixel values
	 */
	private double calcMean(ImageProcessor img, int leftBorder, int rightBorder, int upperBorder, int lowerBorder) {
		double mean = 0.0;
		double N = (double) (rightBorder - leftBorder) * (lowerBorder - upperBorder) ;
		for (int j = leftBorder ; j < rightBorder; j++) {
			for(int k = upperBorder; k < lowerBorder; k++) {
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
	
	/**
	 * 
	 */
	public void evaluation() {
		double rate = 0.0;

		rate = errorRate(results);
		try {
			writeToExcel(String.format("%.3f", rate),"Classification Rate:");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * @param results
	 * @return
	 */
	private double errorRate(HashMap<String,String> results) {
		double rate = 0.0;
		int lengthAllChars = 0;
		int mistakes = 0;
		for(Entry<String, String> result : results.entrySet()) {
			String key = result.getKey();
			String value = result.getValue();
			lengthAllChars += key.length();
			if(value.length() > key.length()) {
				mistakes += value.length() - key.length();
			}
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

}

