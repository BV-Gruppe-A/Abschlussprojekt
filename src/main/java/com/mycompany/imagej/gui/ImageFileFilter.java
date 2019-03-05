package com.mycompany.imagej.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filters the shown images in the file chooser
 */
public class ImageFileFilter extends FileFilter {
	/**
	 * checks if a given file is accepted by the filter
	 * @param file the given file
	 * @return true if the file is accepted
	 */
	@Override
	public boolean accept(File file) {
		// directories should be displayed
		if (file.isDirectory()) {
	        return true;
	    }

	    String extension = getFileExtension(file);
	    if (extension == null) {
	        return false;
	    }

	    return checkForImageExtension(extension);
	}

	/**
	 * gets the description for the file type in the file chooser
	 */
	@Override
	public String getDescription() {
		return "Just Images";
	}
	
	/**
	 * gets the extension for the given file
	 * @param file file for that the extension is searched
	 * @return all characters after the last dot
	 */
	private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex > 0 &&  dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return null;
	}
	
	/**
	 * check if the extension is one of the image-extensions
	 * @param extension string to check for
	 * @return true if the file is an image
	 */
	private boolean checkForImageExtension(String extension) {
		if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("tif") || extension.equals("tiff") || extension.equals("bmp")) {
			return true;
		} else {
			return false;
		}
	}

}
