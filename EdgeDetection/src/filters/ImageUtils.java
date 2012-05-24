package filters;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;
import external.Complex;


public class ImageUtils {
	
	private static BufferedImage image = null;

	public Complex[] doubleFFT(BufferedImage image) {
		List<Complex> complexPixels = new ArrayList<Complex>();
		WritableRaster origImage = image.getRaster();
		double[] arr = new double[2*image.getWidth() * image.getHeight()];
		int index = 0;

		int height = image.getHeight();
		int width = image.getWidth();
		System.out.println(height + "  " + width);

		for (int x = 0; x < height; x++) {
			for (int y = 0; y < width; y++) {
				List<Integer> values = new ArrayList<Integer>();
				int[] pixel = null;
				pixel = origImage.getPixel(x, y, pixel);

				arr[2*x*width+2*y] = pixel[0];
				arr[2*x*width+2*y+1] = 0;
			}
		}
		
		
		DoubleFFT_2D fft2d = new DoubleFFT_2D(height,width);
		fft2d.complexForward(arr);
		
		Complex[] complexArr = new Complex[image.getWidth() * image.getHeight()];

		for (int x = 0; x < height; x++) {
			for (int y = 0; y < width; y++) {		
				complexArr[x * width + y] = new Complex(arr[2*x*width+2*y],arr[2*x*width+2*y+1]);
			}
		}
		
		return complexArr;
	}
	
	
	public Complex[] InverseFFT(Complex[] arr,int width,int height) {
		
		double[] array = new double[2*width*height];
		for (int x = 0; x < height; x++) {
			for (int y = 0; y < width; y++) {		
				array[2*x*width+2*y] = arr[x * width + y].re();
				array[2*x*width+2*y+1] = arr[x * width + y].im();	
			}
		}
		
		DoubleFFT_2D fft2d = new DoubleFFT_2D(height,width);
		fft2d.complexInverse(array,true);
		
		Complex[] complexArr = new Complex[width * height];

		for (int x = 0; x < height; x++) {
			for (int y = 0; y < width; y++) {		
				complexArr[x * width + y] = new Complex(array[2*x*width+2*y],array[2*x*width+2*y+1]);
			}
		}
		
		return complexArr;
	}
	
	public BufferedImage writeComplexImage(Complex[] ifft, BufferedImage origImage) {
		BufferedImage img = new BufferedImage(origImage.getWidth(), origImage.getHeight(), origImage.getType());
		WritableRaster raster = img.getRaster().createCompatibleWritableRaster();
		for (int x = 0; x < origImage.getWidth(); x++) {
			for (int y = 0; y < origImage.getHeight(); y++) {
				int index = x * origImage.getWidth() + y;
				int[] arr = new int[1];
				arr[0] = (int) ifft[index].abs();
				raster.setPixel(x, y, arr);
			}
		}

		img.setData(raster);
		return img;
	}

	
	public BufferedImage convertToGray(BufferedImage image) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		image = op.filter(image, null);
		return image;
	}
	
	public BufferedImage convertToColor(BufferedImage image) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		image = op.filter(image, null);
		return image;
	}

	public BufferedImage loadImage(String img,boolean gray) {
		try {
			image = ImageIO.read(new File(img));
			if(gray) image = convertToGray(image);
			else {
				image = convertToColor(image);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public void saveImage(BufferedImage image, String img, String format) {
		try {
			ImageIO.write(image, format, new File(img));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BufferedImage fourierGausian(BufferedImage image, double rad) {

		Complex[] fft = doubleFFT(image);
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				// Distance from 0,0
				int dx = (j < image.getHeight() / 2) ? j : image.getHeight() - j;
				int dy = (i < image.getWidth() / 2) ? i : image.getWidth() - i;
				double distance = Math.sqrt(dx * dx + dy * dy);

				double coef = 0;
				// Lower frequencies are cancelled out smothly
				coef = Math.exp(-(distance * distance) / rad);

				double real = fft[i * image.getWidth() + j].re() * coef;
				double im = fft[i * image.getWidth() + j].im() * coef;

				Complex nc = new Complex(real, im);

				fft[i * image.getWidth() + j] = nc;
			}
		}
		Complex[] iFFT = InverseFFT(fft,image.getWidth(),image.getHeight());
		return writeComplexImage(iFFT, image);
	}


	public static BufferedImage getImage() {
		return image;
	}
	
	public BufferedImage applyMask(BufferedImage origImage,BufferedImage mask) {
		BufferedImage img = new BufferedImage(origImage.getWidth(), origImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = img.getRaster().createCompatibleWritableRaster();
	
		WritableRaster origRaster = origImage.getRaster();
		WritableRaster maskRaster = mask.getRaster();

		
		for (int x = 0; x < origImage.getWidth(); x++) {
			for (int y = 0; y < origImage.getHeight(); y++) {
				int[] px1 = new int[1];			
				maskRaster.getPixel(x,y,px1);
				if(px1[0] == 255) {
					int dark[] = new int[3];
					dark[0]=255;
					dark[1] = 0;
					dark[2] = 0;
					raster.setPixel(x, y, dark);
				}
				else {
					int[] px2= new int[1];	
					origRaster.getPixel(x, y, px2);
					
					int[] pxrgb = new int[3];
					pxrgb[0] = px2[0];
					pxrgb[1] = px2[0];
					pxrgb[2] = px2[0];
					
					raster.setPixel(x, y, pxrgb);
				}							
			}
		}

		img.setData(raster);
		return img;
	}


	public static void setImage(BufferedImage image) {
		ImageUtils.image = image;
	}

}
