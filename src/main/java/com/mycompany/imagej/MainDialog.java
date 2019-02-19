package com.mycompany.imagej;

import ij.ImagePlus;
import ij.gui.GenericDialog;

import java.awt.Dialog;
import java.awt.Scrollbar;

public class MainDialog {
	private GenericDialog gd;
	private ImagePlus img = new ImagePlus("C:\\Users\\Janfi\\Downloads\\18032008066.tif");
	
	// Constructor, which fills the dialog with elements
	public MainDialog() {			
		gd = new GenericDialog("Choose Plate to Identify");
		gd.setModalityType(Dialog.ModalityType.MODELESS);		
		gd.setOKLabel("Start");
		
		// RadioButtons for file amount
		String[] rbOptions = {"... 1 Image", "... more than 1 image"};
		gd.addRadioButtonGroup("Open & Identify...", rbOptions, 1, 2, rbOptions[0]);
		
		// File Chooser
		
		// which method (debug)
		gd.addSlider("Which Method should be used?", 1, 6, 1);
		
		// output directory (File Chooser)
		
		if(gd.wasOKed()) {
			Scrollbar bla = (Scrollbar) gd.getSliders().get(0);
			Abschlussprojekt_PlugIn.chooseMethod(bla.getValue(), img.getProcessor());
		}
		
		gd.showDialog();
	}
}