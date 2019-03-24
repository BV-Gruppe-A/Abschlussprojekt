package com.mycompany.imagej.gui.filefilters;

/**
 * Image File Filter which handels the most important aspects for the two slightly different filters
 * based on this class from the Oracle Java Tutorials
 * https://docs.oracle.com/javase/tutorial/uiswing/examples/components/FileChooserDemo2Project/src/components/ImageFilter.java
 */
public class GeneralFileFilter {
	/**
	 * gets the extension for the given file
	 * @param file file for that the extension is searched
	 * @return all characters after the last dot
	 */
	protected static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex > 0 &&  dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return null;
	}
	
	/**
	 * check if the extension is one of the following image-extensions:
	 * 			- jpg
	 * 			- jpeg
	 * 			- png
	 * 			- tif
	 * 			- tiff
	 * 			- bmp
	 * @param extension string to check for
	 * @return true if the file is an image
	 */
	protected static boolean checkForImageExtension(String extension) {
		if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("tif") || extension.equals("tiff") || extension.equals("bmp")) {
			return true;
		} else {
			return false;
		}
	}
}
