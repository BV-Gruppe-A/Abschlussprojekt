package com.mycompany.imagej.preprocessing;

import java.util.Arrays;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;


public class ShadingFilter {
	//determine the size of the filter
	final int radius = 8; 
	//determine the array size for the filter
	private int[] region = new int[(2 * radius + 1) * (2 * radius + 1)];
	
	public ByteProcessor shading(ImageProcessor ip) {
		int N = ip.getHeight();
		int M = ip.getWidth();
		ByteProcessor back = new ByteProcessor(M,N);
		
		//iterate over the image
		for (int u = 0; u <= M - 0; u++) {
			for (int v = 0; v <= N - 0; v++) {
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
		back.invert();
		ip.invert();
		for (int u = 0; u <= M - 0; u++) {
			for (int v = 0; v <= N - 0; v++) {
				back.putPixel(u, v, ip.getPixel(u,v) - back.getPixel(u,v));	
			}
		}
		return back;
	}
//	final int radius = 1;	
//
//	
//	public ByteProcessor median(ImageProcessor ip) {
//		int N = ip.getHeight();
//		int M = ip.getWidth();
//		ImageProcessor copy = ip.duplicate();
//		ByteProcessor med = new ByteProcessor(M,N);
//		// vector to hold pixels from (2r+1)x(2r+1) neighborhood:
//		int[] A = new int[(2 * radius + 1) * (2 * radius + 1)];
//		
//		// index of center vector element n = 2(r2 + r):
//		int n = 2 * (radius * radius + radius);
//		
//		for (int u = radius; u <= M - radius - 2; u++) {
//			for (int v = radius; v <= N - radius - 2; v++) {
//				// fill the pixel vector A for filter position (u,v):
//				int k = 0;
//				for (int i = -radius; i <= radius; i++) {
//					for (int j = -radius; j <= radius; j++) {
//						A[k] = copy.getPixel(u + i, v + j);
//						k++;
//					}
//				}
//				// sort vector A and take the center element A[n]:
//				Arrays.sort(A);
//				med.putPixel(u, v, A[n]);
//			
//			}
//		}
//		return med;
//	}
}