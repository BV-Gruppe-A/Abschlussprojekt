package com.mycompany.imagej;

import java.util.Arrays;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;


public class MedianFilter {
	final int radius = 1;	

	
	public ByteProcessor median(ImageProcessor ip) {
		int N = ip.getHeight();
		int M = ip.getWidth();
		ImageProcessor copy = ip.duplicate();
		ByteProcessor med = new ByteProcessor(M,N);
		// vector to hold pixels from (2r+1)x(2r+1) neighborhood:
		int[] A = new int[(2 * radius + 1) * (2 * radius + 1)];
		
		// index of center vector element n = 2(r2 + r):
		int n = 2 * (radius * radius + radius);
		
		for (int u = radius; u <= M - radius - 2; u++) {
			for (int v = radius; v <= N - radius - 2; v++) {
				// fill the pixel vector A for filter position (u,v):
				int k = 0;
				for (int i = -radius; i <= radius; i++) {
					for (int j = -radius; j <= radius; j++) {
						A[k] = copy.getPixel(u + i, v + j);
						k++;
					}
				}
				// sort vector A and take the center element A[n]:
				Arrays.sort(A);
				med.putPixel(u, v, A[n]);
			
			}
		}
		return med;
	}
}