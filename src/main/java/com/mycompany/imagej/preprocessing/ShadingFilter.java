package com.mycompany.imagej.preprocessing;

import java.util.Arrays;

import com.mycompany.imagej.Abschlussprojekt_PlugIn;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;


public class ShadingFilter {
	/**
	 * determines the size of the filter and the array size for the filter
	 */ 
	final int RADIUS = 6; 
	private int[] region = new int[(2 * RADIUS + 1) * (2 * RADIUS + 1)];
	
	public ByteProcessor shading(ImageProcessor ip) {
		
		int width = Abschlussprojekt_PlugIn.getCurrentWidth();
	    int height =  Abschlussprojekt_PlugIn.getCurrentHeight();
		/**
		 * creates an object to save the background in an extra image
		 */ 
		ByteProcessor back = new ByteProcessor(width,height);
		/**
		 * iterates over the image
		 */ 
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				/**
				 * collects the values from the filterregion into an array
				 */ 
				int k = 0;
				for (int i = -RADIUS; i <= RADIUS; i++) {
					for (int j = -RADIUS; j <= RADIUS; j++) {
						region[k] = ip.getPixel(u + i, v + j);  
						k++;
					}
				}
				Arrays.sort(region);
				/**
				 * selects the highest value in the array to create the background of the license plate
				 */ 
				back.putPixel(u, v, region[region.length-1]);
			}
		}
		/**
		 * weights pixels with background / lighting values
		 */ 
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				//Industrielle Bildverarbeitung: Wie optische Qualitätskontrolle wirklich funktioniert
				//Christian Demant, Bernd Streicher-Abel, Axel Springhoff
				//Springer-Verlag, 03.01.2011
				back.putPixel(u, v, (int)(255*((float)ip.getPixel(u,v))/((float)back.getPixel(u,v))));	
			}
		}
		return back;
	}
}