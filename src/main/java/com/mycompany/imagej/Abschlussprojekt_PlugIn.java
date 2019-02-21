package com.mycompany.imagej;

import com.mycompany.imagej.gui.MainDialog;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Abschlussprojekt_PlugIn implements PlugInFilter {

    @Override    
    public int setup(String args, ImagePlus im) {  
    	// needs no image, instead opens dialog
    	return NO_IMAGE_REQUIRED;
    }

    @Override
    public void run(ImageProcessor ip) {     	
    	MainDialog md = new MainDialog();
    }
    
    public static void chooseMethod(int chosenMethod, ImageProcessor ip) {
    	switch(chosenMethod) {
        // Baustelle Torsten 1
        case 1:      	
        	ContrastAdjustment cont = new ContrastAdjustment();
        	cont.Contrast(ip);
        	break;
        	
        //Baustelle Torsten 2
        case 2:
        	
            break;
            
        //Baustelle Julian 1    
        case 3:
        	//RGB Bild in Grauwerten 
        	Grayscale gray = new Grayscale(ip);
        	//Konvertierung in Grauwertbild funktioniert noch nicht
        	ip.convertToByteProcessor(false);
        	break;
        	
        //Baustelle Julian 2	
        case 4:
        	
        	break;
        	
        //Alissa
        case 5:
        	
        	break;
        	
        //Janfi
        case 6:
        	// DEBUG - only usable with a binarised input image!
        	Segmentation segm = new Segmentation(ip);
        	// segm.segmentThePicture();
        	segm.debugSegmentation();
        	break;
            
        default: 
        	
    	}  
    	
    	GenericDialog gd = new GenericDialog("");
    	ImagePlus img = new ImagePlus();
    	img.setImage(ip.createImage());
    	gd.addImage(img);
    }
}