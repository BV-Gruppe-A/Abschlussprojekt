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
	 * @return the current Image as an Image Processor
	 */
	public static ImageProcessor getCurrentImageProcessor() {
		return currentImage;
	}
	
	/**
	 * sets the current Image Processor to the given one
	 * @param imageToSet new Image Processor to set
	 */
	public static void setCurrentImageProcessor(ImageProcessor imageToSet) {
		currentImage = imageToSet.resize(AVRG_IMAGE_WIDTH);

	}
	
	
	
	// constant values
	public static int BLACK = 0;
	public static int WHITE = 255;	
	public static int WIDTH = currentImage.getWidth();
	public static int HEIGHT = currentImage.getHeight();

	/**
	 * Constructor which calls the super constructor with the given applet name
	 */
	public Abschlussprojekt_PlugIn() {
		super("Abschlussprojekt Gruppe A");
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