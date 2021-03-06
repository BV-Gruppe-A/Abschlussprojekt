package com.mycompany.imagej;

import com.mycompany.imagej.gui.MainWindow;
import ij.plugin.frame.PlugInFrame;
import ij.process.BinaryProcessor;
           
/**
 * Main Class for our PlugIn
 */
@SuppressWarnings("serial")
public class Abschlussprojekt_PlugIn extends PlugInFrame {
	/**
	 * Value for a black pixel
	 */
	public static int BLACK = 0;
	
	/**
	 * Value for a white pixel
	 */
	public static int WHITE = 255;	
	
	/**
	 * Width to which the picture is initally resized
	 */
	public static int AVRG_IMAGE_WIDTH = 295;
	
	/**
	 * the current Image which is processed as a binary image
	 */
	private static BinaryProcessor currentImage;
	
	/**
	 * Constructor which calls the super constructor with the given applet name
	 */
	public Abschlussprojekt_PlugIn() {
		super("Abschlussprojekt Gruppe A");
	}
	
	/**
	 * @return the current Image as an Image Processor
	 */
	public static BinaryProcessor getCurrentImageProcessor() {
		return currentImage;
	}
	
	/**
	 * sets the current Image Processor to the given one
	 * @param imageToSet new Image Processor to set
	 */
	public static void setCurrentImageProcessor(BinaryProcessor imageToSet) {
		currentImage = imageToSet;
	}
	
	/**
	 * @return height of the current Image Processor
	 */
	public static int getCurrentHeight() {
		return getCurrentImageProcessor().getHeight();
	}
	
	/**
	 * @return width of the current Image Processor
	 */
	public static int getCurrentWidth() {
		return getCurrentImageProcessor().getWidth();
	}

	/**
	 * run-Method which shows the main Window
	 */
    public void run(String arg) {   	
    	MainWindow window = new MainWindow("Abschlussprojekt Gruppe A");
    	window.showDialog();
    }
}