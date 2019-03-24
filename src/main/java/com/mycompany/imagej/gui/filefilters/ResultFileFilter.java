package com.mycompany.imagej.gui.filefilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filters the shown images in the file chooser
 */
public class ResultFileFilter extends FileFilter {
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

	    String extension = GeneralFileFilter.getFileExtension(file.getName());
	    if (extension == null || !extension.equals("csv")) {
	        return false;
	    } else {
	    	return true;
	    }
	}

	/**
	 * gets the description for the file type in the file chooser
	 */
	@Override
	public String getDescription() {
		return "CSV File";
	}
}
