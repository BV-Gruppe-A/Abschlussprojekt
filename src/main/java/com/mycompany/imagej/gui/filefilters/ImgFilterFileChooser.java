package com.mycompany.imagej.gui.filefilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filters the shown images in the file chooser
 * based on this class from the Oracle Java Tutorials
 * https://docs.oracle.com/javase/tutorial/uiswing/examples/components/FileChooserDemo2Project/src/components/ImageFilter.java
 */
public class ImgFilterFileChooser extends FileFilter {
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

	    String extension = ImgFileFilter.getFileExtension(file.getName());
	    if (extension == null) {
	        return false;
	    }

	    return ImgFileFilter.checkForImageExtension(extension);
	}

	/**
	 * gets the description for the file type in the file chooser
	 */
	@Override
	public String getDescription() {
		return "Just Images";
	}
}
