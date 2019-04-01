package com.mycompany.imagej;

import com.mycompany.imagej.gui.DebugWindow;
import com.mycompany.imagej.gui.MainWindow;
import ij.plugin.frame.PlugInFrame;
import ij.process.ImageProcessor;
           
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
	 * the current Image which is processed
	 */
	private static ImageProcessor currentImage;
	
	/**
	 * Constructor which calls the super constructor with the given applet name
	 */
	public Abschlussprojekt_PlugIn() {
		super("Abschlussprojekt Gruppe A");
	}
	
	/**
	 * @return the current Image as an Image Processor
	 */
	public static ImageProcessor getCurrentImageProcessor() {
		return currentImage;
	}
	
	/**
	 * sets the current Image Processor to the given one
	 * @param imageToSet new Image Processor to set
	 * @param shouldResize true if the image should be resized
	 */
	public static void setCurrentImageProcessor(ImageProcessor imageToSet, boolean shouldResize) {
		currentImage = shouldResize ? imageToSet.resize(AVRG_IMAGE_WIDTH) : imageToSet;
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
    	//DebugWindow window = new DebugWindow("Abschlussprojekt Gruppe A");
    	window.showDialog();
    }
}