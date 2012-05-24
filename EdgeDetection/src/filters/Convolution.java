package filters;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

public class Convolution {

	private BufferedImage image = null;

	public Convolution(BufferedImage image) {
		this.image = image;
	}

	private boolean inRange(int x, int y, BufferedImage image) {
		if (x > 0 && x < image.getWidth() && y > 0 && y < image.getHeight()) {
			return true;
		}
		return false;
	}

	public BufferedImage applyConvolution(int[][] kernel, int radius) {
		int weightSum = 0;
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;

		for (int i = 0; i < radius; i++) {
			for (int j = 0; j < radius; j++) {
				weightSum += kernel[i][j];
			}
		}

		WritableRaster origImage = image.getRaster();
		WritableRaster newImage = origImage.createCompatibleWritableRaster();

		int height = image.getHeight();
		int width = image.getWidth();

		int[][] grayScales = new int[height][width];

		for (int x = 0; x < height; x++) {
			for (int y = 0; y < width; y++) {
				List<Integer> values = new ArrayList<Integer>();
				int[] pixel = null;
				pixel = origImage.getPixel(x, y, pixel);

				int halfRadius = (int) radius / (int) 2;
				for (int i = 0; i < radius; i++) {
					for (int j = 0; j < radius; j++) {
						int dx = i - halfRadius + x;
						int dy = j - halfRadius + y;
						if (inRange(dx, dy, image)) {
							// System.out.println(x+" "+y+" "+i+" "+j+
							// " "+kernel[i][j]+" "+dx+" "+dy);
							int[] newPixel = null;
							newPixel = origImage.getPixel(dx, dy, newPixel);
							// System.out.println(newPixel[0]+"*"+kernel[i][j]+"="+newPixel[0]*kernel[i][j]);
							values.add(newPixel[0] * kernel[i][j]);
						}
					}
				}

				long sum = 0;
				for (Integer value : values) {
					sum += value;
				}

				int val = (int) sum;
//				if (weightSum != 0) {
//					val = (int) sum / weightSum;
//					System.out.println("Weight sum not zero!"+weightSum);
//				}
				int[] array = new int[1];
				array[0] = val;
				if (val > max) {
					max = val;
				}
				if (val < min) {
					min = val;
				}

				grayScales[x][y] = val;

				// System.out.println(val);
				newImage.setPixel(x, y, array);

				// int px[] = null;
				// px = newImage.getPixel(x,y,px);
				// System.out.println("Pix:"+px[0]);
				//
			}
		}

//		int threshold = 60;
//		for (int x = 0; x < height; x++) {
//			for (int y = 0; y < width; y++) {
//				int[] array = new int[1];
//				array[0] = 0;
//
//				if (inRange(x, y + 1, image)) {
//
//					if (((grayScales[x][y] > 0) && (grayScales[x][y + 1] < 0)) || (grayScales[x][y] < 0)
//							&& (grayScales[x][y + 1] > 0) || ((grayScales[x][y] == 0) && (grayScales[x][y + 1] != 0))
//							|| ((grayScales[x][y] != 0) && (grayScales[x][y + 1] == 0))) {
//
//						if (Math.abs(grayScales[x][y] - grayScales[x][y + 1]) > threshold) {
//							array[0] = 255;
//						}
//					}
//				}
//
//				newImage.setPixel(x, y, array);
//
//			}
//		}

		for (int x = 0; x < height; x++) {
			for (int y = 0; y < width; y++) {
				int val = grayScales[x][y];
				int scaled = (255 - 0) * (val - min) / (max - min) + 0;
				int[] array = new int[1];
				array[0] = scaled;
				newImage.setPixel(x, y, array);
				//System.out.println(scaled);
			}
		}

		BufferedImage image2 = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		image2.setData(newImage);
		return image2;
	}	
	
	public int[][] applyConvolutionNoScale(int[][] kernel, int radius) {
		int weightSum = 0;
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;

		for (int i = 0; i < radius; i++) {
			for (int j = 0; j < radius; j++) {
				weightSum += kernel[i][j];
			}
		}

		WritableRaster origImage = image.getRaster();
		WritableRaster newImage = origImage.createCompatibleWritableRaster();

		int height = image.getHeight();
		int width = image.getWidth();

		int[][] grayScales = new int[height][width];

		for (int x = 0; x < height; x++) {
			for (int y = 0; y < width; y++) {
				List<Integer> values = new ArrayList<Integer>();
				int[] pixel = null;
				pixel = origImage.getPixel(x, y, pixel);

				int halfRadius = (int) radius / (int) 2;
				for (int i = 0; i < radius; i++) {
					for (int j = 0; j < radius; j++) {
						int dx = i - halfRadius + x;
						int dy = j - halfRadius + y;
						if (inRange(dx, dy, image)) {
							int[] newPixel = null;
							newPixel = origImage.getPixel(dx, dy, newPixel);
							values.add(newPixel[0] * kernel[i][j]);
						}
					}
				}

				long sum = 0;
				for (Integer value : values) {
					sum += value;
				}

				int val = (int) sum;
//				if (weightSum != 0) {
//					val = (int) sum / weightSum;
//					System.out.println("Weight sum not zero!"+weightSum);
//				}
				int[] array = new int[1];
				array[0] = val;
				if (val > max) {
					max = val;
				}
				if (val < min) {
					min = val;
				}

				grayScales[x][y] = val;
			}
		}
		return grayScales;
	}

}
