package com.mycompany.imagej.gui.filefilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filters the shown images in the file chooser, so only images or csv's are shown
 */
public class FilterForFileChooser extends FileFilter {
	/**
	 * is this file filter used for images (= true) or csv-files (= false)
	 */
	private boolean usedForImages;
	
	/**
	 * constructor, which configures if it is used for images
	 * @param images true, if it should be used for images
	 */
	public FilterForFileChooser(boolean images) {
		usedForImages = images;
	}
	
	/**
	 * checks, if a given file is accepted by the filter
	 * @param file the given file
	 * @return true if the file is accepted
	 */
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
	        return true;
	    }

	    String extension = FilterToolkit.getFileExtension(file.getName());
	    if (extension == null || (!usedForImages && !extension.equals("csv"))) {
	        return false;
	    }

	    if(usedForImages) {
	    	return FilterToolkit.checkForImageExtension(extension);
	    } else {
	    	return true;
	    }
	}

	/**
	 * gets the description for the file type in the file chooser
	 */
	@Override
	public String getDescription() {
		if(usedForImages) {
			return "Images: jpg, jpeg, bmp, png, tif or tiff";
		} else {
			return "CSV File";
		}
		
	}
}
