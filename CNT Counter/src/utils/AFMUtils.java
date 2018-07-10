package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class AFMUtils {
	
	public static BufferedImage blackAndWhite(BufferedImage img) {
		BufferedImage cpy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = cpy.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		return cpy;
	}
	
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
		
		String result = null;
		ITesseract instance = new Tesseract();
	    try {
			result = instance.doOCR(AFMUtils.sharpen(img.getSubimage(390, 543, 140, 30)));
		} catch (TesseractException e) {
			return -1;
		}
	    String[] results = result.split(" ");
		
		return 521.0 / count * (Double.parseDouble(results[0]) / 1000);
	}
	
	public static BufferedImage crop(BufferedImage img) {
		return img.getSubimage(12, 8, 521, 521);
	}
	
	public static BufferedImage threshold(BufferedImage img) {
		BufferedImage cpy = ImageUtils.deepCopy(img);
		
		Color c = null;
		for (int i = 0; i < cpy.getWidth(); i++) {
			for (int j = 0; j < cpy.getWidth(); j++) {
				c = new Color(img.getRGB(i, j));
				if (c.getRed() < 100) {
					cpy.setRGB(i, j, Color.BLACK.getRGB());
				} else {
					cpy.setRGB(i, j, Color.white.getRGB());
				}
			}
		}
		
		return cpy;
	}
	
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
	
	public static BufferedImage sharpen(BufferedImage img) {
		Kernel kernel = new Kernel(3, 3, new float[] { -1, -1, -1, -1, 9, -1, -1,
		        -1, -1 });
		    BufferedImageOp op = new ConvolveOp(kernel);
		    return op.filter(img, null);
	}
}
