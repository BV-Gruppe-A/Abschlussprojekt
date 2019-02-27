package com.mycompany.imagej.preprocessing;

import java.util.Arrays;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;


public class ShadingFilter {
	//determine the size of the filter
	final int radius = 6; 
	//determine the array size for the filter
	private int[] region = new int[(2 * radius + 1) * (2 * radius + 1)];
	
	public ByteProcessor shading(ImageProcessor ip) {
		int N = ip.getHeight();
		int M = ip.getWidth();
		ByteProcessor back = new ByteProcessor(M,N);
		
		//iterate over the image
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				int k = 0;
				//collect the values from the filterregion into an array
				for (int i = -radius; i <= radius; i++) {
					for (int j = -radius; j <= radius; j++) {
						region[k] = ip.getPixel(u + i, v + j);  
						k++;
					}
				}
				Arrays.sort(region);
				//select the highest value in the array to create the background of the license plate
				back.putPixel(u, v, region[region.length-1]);
			}
		}
		ImagePlus imgToShow = new ImagePlus("Background", back);
    	imgToShow.show();
    	//Weight pixels with background / lighting values
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				//Industrielle Bildverarbeitung: Wie optische Qualitätskontrolle wirklich funktioniert
				//Christian Demant, Bernd Streicher-Abel, Axel Springhoff
				//Springer-Verlag, 03.01.2011
				back.putPixel(u, v, (int)(255*((float)ip.getPixel(u,v))/((float)back.getPixel(u,v))));
			}
		}
		ImagePlus imgToShow2 = new ImagePlus("ShadingResult", back);
    	imgToShow2.show();
		return back;
	}
}