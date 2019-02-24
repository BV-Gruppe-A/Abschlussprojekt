package com.mycompany.imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class Abschlussprojekt_PlugIn implements PlugInFilter {

    @Override    
    public int setup(String args, ImagePlus im) {  
    	//this plugin accepts RGB images
    	return DOES_RGB; 
        
    	// DEBUG FOR JANFI
    	//return DOES_8G;
    }

    @Override
    public void run(ImageProcessor ip) { 
    	
    	int whichMethod = (int)IJ.getNumber("Welche Methode soll aufgerufen werden?", 1);
    	
    	switch(whichMethod) {
        // Kontrastanpassung angepasst und läuft
        case 1:  
            // Hard border for contrast adjustment
        	int PercentageBlack = 35;
        	int PercentageWhite = 65;
        	
        	ContrastAdjustment cont = new ContrastAdjustment();
        	cont.Contrast(ip,PercentageWhite,PercentageBlack);
        	break;

        	
        //Case 3 und danach Case 1 ausführen
        case 2:
            Grayscale gray1 = new Grayscale();
            ip = gray1.Grayscale_function(ip);
        	ip.convertToByteProcessor(false);
        	new ImagePlus("GrayscaledImage",ip).show();
        	
        	int PercentageBlack1 = 35;
        	int PercentageWhite1 = 65;
        	
        	ContrastAdjustment cont1 = new ContrastAdjustment();
        	cont1.Contrast(ip,PercentageWhite1,PercentageBlack1);
        	
            MedianFilter filt1 = new MedianFilter();
            ip = filt1.median(ip);        
            
            break;
    
        case 3:
        	//RGB Bild in Grauwert Bild umwandeln
            Grayscale gray = new Grayscale();
            ip = gray.Grayscale_function(ip);
        	ip.convertToByteProcessor(false);
        	new ImagePlus("GrayscaledImage",ip).show();
        	
        	break;
        //FBaustelle Julian 2	
        case 4:
        	
        	break;
        //Alissa
        case 5:
        	Classificator classificator = new Classificator();
        	classificator.classify(null, "Kennzeichen1");
        	break;
        //Janfi
        case 6:
        	// DEBUG - only usable with a binarised input image!
        	Segmentation segm = new Segmentation(ip);
        	// segm.segmentThePicture();
        	segm.debugSegmentation();
        	break;
        	
        case 7:
        	//Median Filter
            MedianFilter filt = new MedianFilter();
            ip = filt.median(ip);        	
        	
        	break;
            
        default:   
    
    }
  }
}
