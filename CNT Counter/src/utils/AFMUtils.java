package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class AFMUtils {
	
	//returns a black and white copy of an image
	public static BufferedImage blackAndWhite(BufferedImage img) {
		BufferedImage cpy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = cpy.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		return cpy;
	}
	
	//gets the actual size of a 100nm AFM image
	public static double actualSize(BufferedImage img) {
		//height of the image scale
		int scaleHeight = img.getHeight() - 38;
		
		int x = img.getWidth() - 1;
		Color c = null;
		do {
			c = new Color(img.getRGB(x, scaleHeight));
			x--;
		} while (c.getRed() > 100);
		
		int count = 1;
		while (c.getRed() < 100) {
			x--;
			c = new Color(img.getRGB(x, scaleHeight));
			count++;
		}
		
		return 521.0 / count * (100.0 / 1000);
	}
	
	//crops an AFM image
	public static BufferedImage crop(BufferedImage img) {
		return img.getSubimage(12, 8, 521, 521);
	}
	
	//increases contrast in an AFM image
	public static BufferedImage contrast(BufferedImage img) {
		BufferedImage cpy = ImageUtils.deepCopy(img);
		
		int width = cpy.getWidth();
		int height = cpy.getHeight();
		
		for (int j = 0; j < height; j++) {
			int pixels[] = new int[width];
			for (int i = 0; i < width; i++) {
				Color c = new Color(cpy.getRGB(i, j));
				pixels[i] = c.getRed();
			}
			int min = GenUtils.min(pixels);
			int max = GenUtils.max(pixels);	
			double multiplier = 255.0 / (max - min);
			
			for (int i = 0; i < width; i++) {
				Color c = new Color(cpy.getRGB(i, j));
				int light = c.getRed();
				int newLight = (int)Math.round((light - min) * multiplier);
				Color newC = new Color(newLight, newLight, newLight);
				cpy.setRGB(i, j, newC.getRGB());
			}
		}
		
		return cpy;
	}
	
	//sharpens an image
	public static BufferedImage sharpen(BufferedImage img) {
		Kernel kernel = new Kernel(3, 3, new float[] { -1, -1, -1, -1, 9, -1, -1,
		        -1, -1 });
		    BufferedImageOp op = new ConvolveOp(kernel);
		    return op.filter(img, null);
	}
}
