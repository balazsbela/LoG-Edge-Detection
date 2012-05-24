package filters;

import java.awt.image.BufferedImage;

import filters.Filter.FilterType;


public class Main {
	private final static String IMG_FORMAT = "png";
	private static BufferedImage image = null;	
	
	//THE HIGHER, THE LESS BLUR
	private static double CLEARNESS_FACTOR = 0.5;
	
	
	public static void main(String[] args) {
		ImageUtils m = new ImageUtils();
		
		BufferedImage image = m.loadImage("jimmy.jpg",true);		
		
//		BufferedImage image = m.loadImage("lena_clean.jpg",true);		
		
		BufferedImage gaussian = m.fourierGausian(image, CLEARNESS_FACTOR * 10000);
		m.saveImage(gaussian, "gaussian.png", "png");
	
		Laplacians l = new Laplacians();
		
//		BufferedImage lap1 = l.laplacian3x3_1(gaussian);		
//
//		m.saveImage(l.laplacian3x3_1(image), "gaussless_laplacian3x3_1.png", "png");

		
//		Filter filter = new Filter(lap1);
//		BufferedImage median = filter.applyFilter(FilterType.MEDIAN);
//		m.saveImage(median, "median_lap3x3_1.png", "png");
		
		BufferedImage zc3 = l.laplacian5x5_1(gaussian);	
		BufferedImage zc2 = l.laplacian3x3_2(gaussian);	
		BufferedImage zc4 = l.laplacian3x3_1(gaussian);	

		BufferedImage zc1 = l.laplacian7x7_1(gaussian);
		
		
		BufferedImage combined = m.applyMask(image, zc1);
		m.saveImage(combined, "combined7x7_1.png", "png");
		 
		combined = m.applyMask(image, zc4);
		m.saveImage(combined, "combined3x3_1.png", "png");
		
		combined = m.applyMask(image, zc2);
		m.saveImage(combined, "combined3x3_2.png", "png");
		
		combined = m.applyMask(image, zc3);
		m.saveImage(combined, "combined5x5_1.png", "png");
		
		//BufferedImage lap4 = l.laplacian7x7_1(gaussian);		


//		m.saveImage(l.laplacian5x5_1(image), "gaussless_laplacian5x5_1.png", "png");
		
//		filter = new Filter(lap1);
//		median = filter.applyFilter(FilterType.MEDIAN);
//		m.saveImage(median, "median_lap5x5_1.png", "png");
	
		
	}
}
