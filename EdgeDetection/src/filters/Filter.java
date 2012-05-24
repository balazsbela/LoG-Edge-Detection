package filters;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Filter {

	public static enum FilterType {
		DILATION, EROSION, MEDIAN
	}

	private BufferedImage image = null;
	private int radius = 2;
	private int nrDirections = 3;
	private int dxArr[] = { -1, 0, 1 };
	private int weightSum = 13;

	public Filter(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage applyFilter(FilterType type) {

		WritableRaster origImage = image.getRaster();
		WritableRaster newImage = origImage.createCompatibleWritableRaster();

		int height = image.getHeight();
		int width = image.getWidth();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				List<Integer> values = new ArrayList<Integer>();
				int[] pixel = null;
				pixel = origImage.getPixel(x, y, pixel);
				
				//System.out.println("for "+x+" "+y);
				for(int dx=0;dx<2*radius;dx++) {
					for(int dy=0;dy<2*radius;dy++) {
						
						int offX = dx - radius;
						int offY = dy - radius;
						
						//System.out.println((x+offX)+" "+(y+offY));
						if (inRange(x + offX, y + offY, image)) {
							int[] newPixel = null;
							newPixel = origImage.getPixel(x + offX, y + offY, pixel);
							values.add(newPixel[0]);
						}
					}
				}
				
//				for (int offX = 0; offX < nrDirections; offX++) {
//					for (int offY = 0; offY < nrDirections; offY++) {
//
//						int dx = dxArr[offX];
//						int dy = dxArr[offY];
//
//						if (inRange(x + dx, y + dy, image)) {
//							int[] newPixel = null;
//							newPixel = origImage.getPixel(x + dx, y + dy, pixel);
//							values.add(newPixel[0]);
//						}
//					}
//
//				}

				Collections.sort(values);			
				int val = 0;
				switch(type) {
					case DILATION: {
						val = values.get(values.size()-1);
						break;
					}
					case EROSION: {
						val = values.get(0);
						break;
					}
					case MEDIAN: {
						val = values.get(values.size()/2);
						break;
					}
				}
				int[] array = new int[1];
				array[0] = val;
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
