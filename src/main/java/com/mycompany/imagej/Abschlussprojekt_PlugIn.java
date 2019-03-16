package com.mycompany.imagej;

import com.mycompany.imagej.gui.MainWindow;
import ij.plugin.frame.PlugInFrame;
           
/**
 * Main Class for our PlugIn
 */
@SuppressWarnings("serial")
public class Abschlussprojekt_PlugIn extends PlugInFrame {
	// constant values
	public static int BLACK = 0;
	public static int WHITE = 255;	

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
    	window.showDialog();
    }
}