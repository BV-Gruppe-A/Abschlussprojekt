package com.mycompany.imagej.gui.filefilters;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filter for the loop which opens all images in one folder
 */
public class ImgFilterDirectoryLoop implements FilenameFilter {
	/**
	 * checks if a given file is accepted by the filter
	 * @param directory directory of the given file
	 * @param fileName name of the given file
	 * @return true if the file is accepted
	 */
	@Override
	public boolean accept(File directory, String fileName) {
	    String extension = ImgFileFilter.getFileExtension(fileName);
	    if (extension == null) {
	        return false;
	    }

	    return ImgFileFilter.checkForImageExtension(extension);
	}
}
