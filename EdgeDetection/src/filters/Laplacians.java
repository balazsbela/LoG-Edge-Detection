package filters;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import filters.Filter.FilterType;

public class Laplacians {
	public static int ZERO_CROSSINGS_THRESHOLD3x3_1 = 6;
	public static int ZERO_CROSSINGS_THRESHOLD3x3_2 = 15;
	public static int ZERO_CROSSINGS_THRESHOLD5x5_1 = 35;
	public static final int ZERO_CROSSINGS_THRESHOLD7x7_1 = 120;

//	public static int ZERO_CROSSINGS_THRESHOLD3x3_1 = 10;
//	public static int ZERO_CROSSINGS_THRESHOLD3x3_2 = 20;
//	public static int ZERO_CROSSINGS_THRESHOLD5x5_1 = 20;
	
	public BufferedImage laplacian3x3_1(BufferedImage image) {
		
		int[][] kernel = new int[][] {
				{0,1,0}, 
				{1,-4,1},
				{0,1,0}
		};
		
		Convolution conv = new Convolution(image);
		BufferedImage laplacian = conv.applyConvolution(kernel,3);
		ImageUtils m = new ImageUtils();

		m.saveImage(laplacian, "laplacian3x3_1.png", "png");
		
		BufferedImage zc = zeroCrossings(conv.applyConvolutionNoScale(kernel, 3), image,ZERO_CROSSINGS_THRESHOLD3x3_1);
		m.saveImage(zc, "zeroCrossings3x3_1.png", "png");

		Filter filter = new Filter(zc);
		m.saveImage( filter.applyFilter(FilterType.MEDIAN), "median_zc3x3_1.png", "png");
	
		return zc;
	}
	
	public BufferedImage laplacian3x3_2(BufferedImage image) {
			
			int[][] kernel = new int[][] {
					{1,1,1}, 
					{1,-8,1},
					{1,1,1}
			};
			
			Convolution conv = new Convolution(image);
			BufferedImage laplacian = conv.applyConvolution(kernel,3);
			ImageUtils m = new ImageUtils();
	
			m.saveImage(laplacian, "laplacian3x3_2.png", "png");
			
			BufferedImage zc = zeroCrossings(conv.applyConvolutionNoScale(kernel, 3), image,ZERO_CROSSINGS_THRESHOLD3x3_2);
			m.saveImage(zc, "zeroCrossings3x3_2.png", "png");
	
			Filter filter = new Filter(zc);
			m.saveImage( filter.applyFilter(FilterType.MEDIAN), "median_zc3x3_2.png", "png");
			
			return zc;
	}
	
	public BufferedImage laplacian5x5_1(BufferedImage image) {
		//http://fourier.eng.hmc.edu/e161/lectures/gradient/node10.html
		int[][] kernel = new int[][] {
				{0,0,1,0,0}, 
				{0,1,2,1,0},
				{1,2,-16,2,1},
				{0,1,2,1,0},
				{0,0,1,0,0}
		};
		
		Convolution conv = new Convolution(image);
		BufferedImage laplacian = conv.applyConvolution(kernel,5);
		ImageUtils m = new ImageUtils();

		m.saveImage(laplacian, "laplacian5x5_1.png", "png");
		
		BufferedImage zc = zeroCrossings(conv.applyConvolutionNoScale(kernel, 5), image,ZERO_CROSSINGS_THRESHOLD5x5_1);
		m.saveImage(zc, "zeroCrossings5x5_1.png", "png");
		Filter filter = new Filter(zc);
    	m.saveImage( filter.applyFilter(FilterType.MEDIAN), "median_zc5x5_1.png", "png");
		
		return zc;
	}
	
	
public BufferedImage laplacian7x7_1(BufferedImage image) {
		//http://www.fmwconcepts.com/imagemagick/laplacian/index.php
		int[][] kernel = new int[][] {				
				{-10, -5, -2, -1, -2, -5 ,-10},
				{-5, 0, 3 ,4 ,3 ,0 ,-5},
				{-2 ,3, 6, 7, 6, 3, -2},
				{-1, 4, 7 ,8, 7, 4, -1},
				{-2, 3, 6, 7, 6, 3, -2},
				{-5, 0 ,3, 4, 3, 0 ,-5},
				{-10, -5, -2, -1, -2, -5, -10}
		};
							
		
		
		Convolution conv = new Convolution(image);
		BufferedImage laplacian = conv.applyConvolution(kernel,5);
		ImageUtils m = new ImageUtils();

		m.saveImage(laplacian, "laplacian7x7_1.png", "png");
		
		BufferedImage zc = zeroCrossings(conv.applyConvolutionNoScale(kernel, 7), image,ZERO_CROSSINGS_THRESHOLD7x7_1);
		m.saveImage(zc, "zeroCrossings7x7_1.png", "png");
		Filter filter = new Filter(zc);
		m.saveImage( filter.applyFilter(FilterType.MEDIAN), "median_zc7x7_1.png", "png");	
		
		return zc;
	}
	
	
	public BufferedImage zeroCrossings(int[][] laplacian,BufferedImage image,int threshold) {
		WritableRaster origImage = image.getRaster();
		WritableRaster newImage = origImage.createCompatibleWritableRaster();		
		int height = image.getHeight();
		int width = image.getWidth();
		
		for (int x = 0; x < height; x++) {
			for (int y = 0; y < width; y++) {
				int[] array = new int[1];
				array[0] = 0;

				//before on y
				if (inRange(x, y - 1, image)) {
					if (((laplacian[x][y] > 0) && (laplacian[x][y - 1] < 0)) || (laplacian[x][y] < 0)
							&& (laplacian[x][y - 1] > 0) || ((laplacian[x][y] == 0) && (laplacian[x][y - 1] != 0))
							|| ((laplacian[x][y] != 0) && (laplacian[x][y - 1] == 0))) {

						if (Math.abs(laplacian[x][y] - laplacian[x][y - 1]) > threshold) {
							array[0] = 255;
						}
					}					
				}
				
				//After on y
				if (inRange(x, y + 1, image)) {

					if (((laplacian[x][y] > 0) && (laplacian[x][y + 1] < 0)) || (laplacian[x][y] < 0)
							&& (laplacian[x][y + 1] > 0) || ((laplacian[x][y] == 0) && (laplacian[x][y + 1] != 0))
							|| ((laplacian[x][y] != 0) && (laplacian[x][y + 1] == 0))) {

						if (Math.abs(laplacian[x][y] - laplacian[x][y + 1]) > threshold) {
							array[0] = 255;
						}
					}
					
				}
				
				//Over x
				if(inRange(x+1,y,image)) {
					if (((laplacian[x][y] > 0) && (laplacian[x+1][y] < 0)) || (laplacian[x][y] < 0)
							&& (laplacian[x+1][y] > 0) || ((laplacian[x][y] == 0) && (laplacian[x+1][y] != 0))
							|| ((laplacian[x][y] != 0) && (laplacian[x+1][y] == 0))) {

						if (Math.abs(laplacian[x+1][y] - laplacian[x][y]) > threshold) {
							array[0] = 255;
						}
					}
					
				}
				
				//Under x
				if(inRange(x-1,y,image)) {
 					if (((laplacian[x][y] > 0) && (laplacian[x-1][y] < 0)) || (laplacian[x-1][y] < 0)
							&& (laplacian[x-1][y] > 0) || ((laplacian[x][y] == 0) && (laplacian[x-1][y] != 0))
							|| ((laplacian[x][y] != 0) && (laplacian[x-1][y] == 0))) {

						if (Math.abs(laplacian[x-1][y] - laplacian[x][y]) > threshold) {
							array[0] = 255;
						}
					}
				}				

				newImage.setPixel(x, y, array);
			}
		}
		
		BufferedImage image2 = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		image2.setData(newImage);
		return image2;
	}
	
	private boolean inRange(int x, int y, BufferedImage image) {
		if (x > 0 && x < image.getWidth() && y > 0 && y < image.getHeight()) {
			return true;
		}
		return false;
	}
}
