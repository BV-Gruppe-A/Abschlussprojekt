package com.mycompany.imagej;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import com.mycompany.imagej.datamodels.*;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * This class can be used to identify 25x38 big images of characters in FE oder DIN1451 font. 
 * It uses Template Matching to identify a character.
 */
public class Classificator {

	/**
	 * String with all possible license plate characters
	 */
	private final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ-0123456789";	
	
	/**
	 * All Templates of the FE font
	 */
	private Template[] templatesFe;
	
	/**
	 * All templates of the DIN1451 font
	 */
	private Template[] templatesDin;
	
	/**
	 * Array to count FE or DIN character for one license plate, to reevaluate certain characters, 
	 * that are not in the majority of the found font
	 */
	private boolean[] charIsFe;
	
	/**
	 * Name/path of the csv file, where the results are going to be stored
	 */
	private String csvName = "list.csv";
	
	/**
	 * HashMap of the classified license plate with the filename as key and the identified characters as values
	 */
	private HashMap<String,String> results;
	
	/**
	 * Creates a new Classificator. Initializes the results HashMap and loads the Templates from resources
	 */
	public Classificator() {
		results = new HashMap<>();
		initializeTemplates();
	}
	
		
	/**
	 * @return the name of the current file-/pathname where the results are going to be stored
	 */
	public String getCsvName() {
		return csvName;
	}


	/**
	 * Sets the name of the csv-file, where the results should be stored and resets the file if the boolean is set
	 * @param csvName the file-/pathname where results should be stored
	 * @param clear a boolean that indicates whether the csv, if it already exists should be cleared before writing 
	 * the new results 
	 */
	public void setCsvName(String csvName, boolean clear) {
		if(!csvName.endsWith(".csv")){
			csvName += ".csv";
		}
		this.csvName = csvName;
		File file = new File(csvName); 
		if(clear && file.exists()) {
			file.delete();
		}
	}



	/**
	 * reads in all template pictures of characters in FE and DIN 1451 font from the plugins resources, 
	 * maps it to the character it represents and calculates the mean of the image
	 */
	private void initializeTemplates() {
		templatesFe = new Template[ALPHABET.length()];
		templatesDin = new Template[ALPHABET.length()];
		ImageProcessor img;
		Image template;
		
		for (int i = 0; i < templatesFe.length; i++) {
			template = Toolkit.getDefaultToolkit().getImage((getClass().getClassLoader()
					.getResource("Templates_FE/" + ALPHABET.substring(i, i+1) + ".png")));
	        img = new ImagePlus(template.toString(),template).getProcessor().convertToByte(false);
	        templatesFe[i] = new Template(ALPHABET.substring(i, i+1), calcMean(img), img);
	        
	    }
		for (int i = 0; i < templatesDin.length; i++) {
			template = Toolkit.getDefaultToolkit().getImage((getClass().getClassLoader()
					.getResource("Templates_DIN/" + ALPHABET.substring(i, i+1) + ".png")));
	        img = new ImagePlus(template.toString(),template).getProcessor().convertToByte(false);
	        templatesDin[i] = new Template(ALPHABET.substring(i, i+1), calcMean(img), img);
	        
	    }
	}


	/**
	 * Classifies each character (of one license plate) and saves them to a String that is then written to an excel 
	 * file with the filename as label. Because there are two different possible fonts for a license plate, each 
	 * character is checked separately and if the identified font was mixed for the characters, it classifies character 
	 * of the minority font again. For gaps between the characters in the original picture, underscores are added to 
	 * the String.
	 * @param characters all characters of one license plate separately and all scaled to a size of 25x38
	 * @param filename name of the license plates image file
	 */
	public void classify(CharacterCandidate[] characters, String filename) {
		String licensePlate = "";
		charIsFe = new boolean[characters.length];
		int amountOfSpaces = characters.length - 1 >= 0 ? characters.length - 1 : characters.length;
		int[] spaces = new int[amountOfSpaces];
		int countSpaces = 0;
		
		for (int i = 0; i < characters.length; i++) {
			licensePlate += classifyChar(characters[i], i, FontToClassify.BOTH);
			if(i < characters.length - 1) {
				double distBetween = (double) (characters[i+1].getLeftBorder() - 
						characters[i].getRightBorder()) 
						/ (double) Abschlussprojekt_PlugIn.getCurrentWidth();
				if(distBetween < CharacterType.SPACE_WIDTH_MAX 
						&& distBetween > CharacterType.SPACE_WIDTH_MIN) {
					spaces[countSpaces++] = i + 1;
				}
			}
		}
		
		licensePlate = balanceTheFont(licensePlate, characters);
		
		licensePlate = insertSpaces(licensePlate, spaces);
		
		results.put(filename, licensePlate);

		writeToCsv(licensePlate, filename);		
	}


	/**
	 * inserts the spaces at the given positions
	 * @param licensePlate the String of a license plate without spaces
	 * @param spaces the positions of found spaces in the plate
	 * @return new license Plate with the strings
	 */
	private String insertSpaces(String licensePlate, int[] spaces) {
		for(int i = 0; i < spaces.length; i++) {
			if(spaces[i] != 0) {
				licensePlate = licensePlate.substring(0, spaces[i]) + "_" 
						+ licensePlate.substring(spaces[i], licensePlate.length());
				
				int changeFollowing = i + 1;
				while(changeFollowing < spaces.length && spaces[changeFollowing] != 0) {
					spaces[changeFollowing++]++;
				}				
			}
		}
		return licensePlate;
	}
	

	/**
	 * Does template matching of character with each template and chooses best option. If the character to be 
	 * classified is completely black, it will be pre-classfied as a "-", to avoid dividing by zero. Characters that 
	 * have proven to be hard to distinguish will be tested further.
	 * @param character character to be characterized
	 * @param currentChar position of the character in the license plate
	 * @param font specifies which font to use for identification, either both, FE or DIN
	 * @return character that the image represents most likely
	 */
	private String classifyChar(CharacterCandidate character, int currentChar, FontToClassify font) {
		Template bestChoiceDin = null, bestChoiceFE = null;
		double maxValFe = -100, maxValDin = -100;
		double valFe, valDin;
		
		character.setMean(calcMean(character.getImage()));
		
		if(character.getMean() == 0) {
			maxValDin = 100.0;
			bestChoiceDin = templatesDin[ALPHABET.indexOf("-")];
		} else {
			for (int i = 0; i < ALPHABET.length(); i++) {
				if(font != FontToClassify.DIN) {
					valFe = templateMatch(templatesFe[i], character);
					if(valFe > maxValFe) {
						maxValFe = valFe;
						bestChoiceFE = templatesFe[i];
					}
				} 
				if(font != FontToClassify.FE) {
					valDin = templateMatch(templatesDin[i], character);
					if(valDin > maxValDin) {
						maxValDin = valDin;
						bestChoiceDin = templatesDin[i];
					}
				}				
			}
		}
		
		if(maxValFe >= maxValDin) {
			charIsFe[currentChar] = true;
			return checkHardships(bestChoiceFE.getCharacter(), character, FontToClassify.FE);
		} else {
			charIsFe[currentChar] = false;
			return checkHardships(bestChoiceDin.getCharacter(), character, FontToClassify.DIN);
		}	 
	}
	
	/**
	 * checks which font was used more and re-classifies the chars which used the other one
	 * if both fonts have been found euqally, FE gets used because it is more common 
	 * @param currentPlate current recognized characters
	 * @param characters all possible characters
	 * @return changed string containing all changed characters
	 */
	private String balanceTheFont(String currentPlate, CharacterCandidate[] characters) {
		int countDin = 0, countFe = 0;
		for(int count = 0; count < charIsFe.length; count++) {
			if(charIsFe[count]) {
				countFe++;
			} else {
				countDin++;
			}
		}
		
		for(int count = 0; count < charIsFe.length; count++) {
			if(countFe >= countDin) {
				if(!charIsFe[count]) {
					currentPlate.toCharArray()[count] = classifyChar(characters[count], 
							count, FontToClassify.FE).charAt(0);
				}
			} else {
				if(charIsFe[count]) {
					currentPlate.toCharArray()[count] = classifyChar(characters[count], 
							count, FontToClassify.DIN).charAt(0);
				}
			}
		}
		
		return currentPlate;
	}
	
	/**
	 * Checks characters that have been proven to be hard to distinguish in the given font. Certain areas have been 
	 * identified manually in which these characters differ. The mean of these areas is then calculated and the 
	 * character is identified via a threshold determined by tests. The hardships of the FE font include O's that have 
	 * falsely been classified as 0's, I's and 1's, 0's falsely classfied as D's and H's and N's. 
	 * The hardships of the DIN font include -'s and I'sand 8's and 9's
	 * @param currentBestChoice the current best fit for the character to identify as a string
	 * @param character the character to identify
	 * @param font the type of font to classify
	 * @return the character that has been identified as the best fit
	 */
	private String checkHardships(String currentBestChoice, CharacterCandidate character, FontToClassify font) {	
		if(font == FontToClassify.FE) {
			switch(currentBestChoice) {
				case "0":
					if(calcMean(character.getImage(),20,23,7,11) < 10) {
						currentBestChoice = "O";
					}
					break;
				case "I":
				case "1":
					if(character.getHeight() <= character.getWidth()) {
						currentBestChoice = "-";
					} else {
						if(calcMean(character.getImage(),1,6,8,12) > 80) {
							currentBestChoice = "I";
						} else {
							currentBestChoice = "1";
						}
					}
				break;
				case "D":
					if(calcMean(character.getImage(),20,23,7,11) > 50) {
						currentBestChoice = "0";
					}
					break;
				case "H":
				case "N":
					if((calcMean(character.getImage(),14,19,24,28) + calcMean(character.getImage(),6,9,7,11)) /2 > 80) {
						currentBestChoice = "H";
					} else {
						currentBestChoice = "N";
					}
					break;
			}
		} else {
			switch(currentBestChoice) {
			case "-":
			case "I":
				if(character.getHeight() > character.getWidth()) {
					currentBestChoice = "I";
				}else {
					currentBestChoice = "-";
				}
				break;
			case "8":
			case "9":
				if(calcMean(character.getImage(),1,7,22,27) > 127) {
					currentBestChoice = "9";
				} else {
					currentBestChoice = "8";
				}
			}
			
		}
		
		return currentBestChoice;		
	}

	/**
	 * Correlates two pictures and calculates the similarity between the two. 
	 * @param template the template to compare the sample to
	 * @param candidate the sample image to compare to template
	 * @return value between -100 and 100 with 100 indicating a perfect match and -100 indicating an inverted match
	 */
	private double templateMatch(Template template, CharacterCandidate candidate) {
		ImageProcessor sample = candidate.getImage();
		
		double numerator = 0;
		double templateDenom = 0, sampleDenom = 0;
		
		double sampleMean = candidate.getMean();
		double templateMean = template.getMean();
		
		ImageProcessor templateImg = template.getImage();
		
		for (int j = 0; j < Template.WIDTH; j++) {
			for(int k = 0; k < Template.HEIGHT; k++) {

				numerator += ((double)templateImg.getPixel(j, k) - templateMean) * 
						((double)sample.getPixel(j, k) - sampleMean);
				
				templateDenom += Math.pow((double)templateImg.getPixel(j, k) - templateMean, 2);
				
				sampleDenom += Math.pow((double)sample.getPixel(j , k) - sampleMean, 2);
			}
		}
		
		templateDenom = Math.sqrt(templateDenom);
		sampleDenom = Math.sqrt(sampleDenom);
		
		return numerator / (templateDenom * sampleDenom) * 100;
	}


	/**
	 * Calculates the mean of the values of all pixels of an image
	 * @param img The image to calculate the mean of
	 * @return The mean of the images pixel values
	 */
	private double calcMean(ImageProcessor img) {
		double mean = calcMean(img, 0, img.getWidth(), 0, img.getHeight());
		return mean;
	}

	/**
	 * calculates the mean of the values of a specified rectangle of pixels of an image
	 * @param img The image to calculate the mean of
	 * @param leftBorder left Border of the rectangle
	 * @param rightBorder right Border of the rectangle
	 * @param upperBorder upper Border of the rectangle
	 * @param lowerBorder lower Border of the rectangle
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
	 * writes one line to a csv file, with the format "label", "value"
	 * @param value value to write to the csv file
	 * @param label label to write to the csv file
	 */
	private void writeToCsv(String value, String label){
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(csvName, true))) {
			writer.write(label + ", "+ value + "\n");
		} catch(IOException ex) {
			ex.printStackTrace();
		} 
	}
	
	/**
	 * Evaluates the classification rate of one run of classifications. The files used for testing have to be named like
	 * the values of the license plates they represent with underscores instead of spaces.
	 * Writes the rate at the end of the csv file with the results.
	 * The classification rate is determined characterwise by counting the characters of the desired plate, counting the
	 * characters that are missing in our found representation. The position of the characters is ignored, so that a 
	 * missing character at the beginning only counts as one mistake. Segments that have aditionally been identified as 
	 * characters are also counted as mistakes. The rate is then calculated by 1-errors/deriredCharacters 
	 */
	public void evaluation() {
		double rate = 0.0;
		int lengthAllChars = 0, mistakes = 0;
		
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

		writeToCsv(String.format(" %.3f", rate), "Classification Rate");		
	}

	/**
	 * Resets the results the Classificator uses to calculate the Classification Rate
	 */
	public void resetClassificator() {
		results.clear();
	}
}

