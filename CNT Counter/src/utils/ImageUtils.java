package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

//This class provides a variety of utility functions for CNT image manipulation
public class ImageUtils {
	
	//Creates a deep copy of an image
	public static BufferedImage deepCopy(BufferedImage bi) {
		 BufferedImage cpy = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
		 Graphics2D g2d = cpy.createGraphics();
		 g2d.drawImage(bi, 0, 0, null);
		 return cpy;
		}
	
	//Reads in an image from a file
	public static BufferedImage readImage(String file) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	//returns an image's width in micrometers, assuming scale is 100nm
	public static double actualSize(BufferedImage img) {
		//height of the image scale
		int scaleHeight = img.getHeight() - 54;
		
		int blkCount = 0;
		Color c = null;
		do {
			c = new Color(img.getRGB(blkCount, scaleHeight));
			blkCount++;
		} while (c.getRed() < 100 && blkCount < img.getWidth() - 1);
		
		int whtCount = 0;
		do {
			c = new Color(img.getRGB(blkCount + whtCount, scaleHeight));
			whtCount++;
		} while (c.getRed() > 100 && blkCount + whtCount < img.getWidth() - 1);
		
		return img.getWidth() / (double)whtCount / 10;
	}
	
	//averages out vertical differences in image exposure
	public static BufferedImage averageExposure(BufferedImage img) {
		BufferedImage cpy = deepCopy(img);
		
		int width = cpy.getWidth();
		int height = cpy.getHeight();
		int totalAverage = averageLight(cpy);
		
		for (int j = 0; j < height; j++) {
			int rowAverage = averageRowLight(cpy, j);
			int diff = totalAverage - rowAverage / 2;
			for (int i = 0; i < width; i++) {
				Color prev = new Color(cpy.getRGB(i, j));
				int newColor = prev.getRed() + diff;
				if (newColor > 255) {
					newColor = 255;
				} else if (newColor < 0) {
					newColor = 0;
				}
				Color newC = new Color(newColor, newColor, newColor);
				cpy.setRGB(i, j, newC.getRGB());
			}
		}
		
		return cpy;
	}
	
	//increases the contrast in the image to maximum with no data loss
	public static BufferedImage contrast(BufferedImage img) {
		BufferedImage cpy = deepCopy(img);
		
		int width = cpy.getWidth();
		int height = cpy.getHeight();
		
		int pixels[] = new int[width * height];
		int pixelCount = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color c = new Color(cpy.getRGB(i, j));
				pixels[pixelCount] = c.getRed();
				pixelCount++;
			}
		}
		
		int min = GenUtils.min(pixels);
		int max = GenUtils.max(pixels);
		
		double multiplier = 255.0 / (max - min);
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color c = new Color(cpy.getRGB(i, j));
				int light = c.getRed();
				int newLight = (int)Math.round((light - min) * multiplier);
				Color newC = new Color(newLight, newLight, newLight);
				cpy.setRGB(i, j, newC.getRGB());
			}
		}
		
		return cpy;
	}
	
	//maximum contrast using an threshold of the average pixel per row
	public static BufferedImage contrastByRow(BufferedImage img) {
		BufferedImage cpy = deepCopy(img);
		
		int width = cpy.getWidth();
		int height = cpy.getHeight();
		Color blk = new Color(0, 0, 0);
		Color wht = new Color(255, 255, 255);
		
		
		for (int i = 0; i < height; i++) {
			int average = averageRowLight(cpy, i);
			
			int threshold = average;
			
			for (int j = 0; j < width; j++) {
				Color c = new Color(cpy.getRGB(j, i));
				if (c.getRed() < threshold) {
					cpy.setRGB(j, i, blk.getRGB());
				} else {
					cpy.setRGB(j, i, wht.getRGB());
				}
			}
		}
		
		return cpy;
	}
	
	//returns the average pixel value of the entire image
	public static int averageLight(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight() - 64;
		
		int[] pixels = new int[width * height];
		int pixelCount = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color c = new Color(img.getRGB(i, j));
				pixels[pixelCount] = c.getRed();
				pixelCount++;
			}
		}
		
		return (int)GenUtils.average(pixels);
	}
	
	//returns the average pixel value of a single row
	public static int averageRowLight(BufferedImage img, int row) {
		int width = img.getWidth();
		
		int[] pixels = new int[width];
		for (int i = 0; i < width; i++) {
			Color c = new Color(img.getRGB(i, row));
			pixels[i] = c.getRed();
		}
		
		return (int)GenUtils.average(pixels);
	}
	
	//returns the maximum pixel value of a single row
	public static int maxRowLight(BufferedImage img, int row) {
		int width = img.getWidth();
		
		int[] pixels = new int[width];
		for (int i = 0; i < width; i++) {
			Color c = new Color(img.getRGB(i, row));
			pixels[i] = c.getRed();
		}
		
		return (int)GenUtils.max(pixels);
	}
	
	//returns the minimum pixel value of a single row
	public static int minRowLight(BufferedImage img, int row) {
		int width = img.getWidth();
		
		int[] pixels = new int[width];
		for (int i = 0; i < width; i++) {
			Color c = new Color(img.getRGB(i, row));
			pixels[i] = c.getRed();
		}
		
		return (int)GenUtils.min(pixels);
	}
	
	//unused function that reduces noise by removing individual pixels
	/*public static BufferedImage noiseReduction(BufferedImage img) {
		BufferedImage cpy = deepCopy(img);
		
		int width = cpy.getWidth();
		int height = cpy.getHeight();
		Color blk = new Color(0, 0, 0);
		Color wht = new Color(255, 255, 255);
		
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 65; j++) {
				if (pixelIsWhtIsland(i, j, cpy))
					cpy.setRGB(i, j, blk.getRGB());
				if (pixelIsBlkIsland(i, j, cpy))
					cpy.setRGB(i, j, wht.getRGB());
			}
		}
		
		return cpy;
	}*/
	
	//tests if a white pixel is individual
	public static boolean pixelIsWhtIsland(int x, int y, BufferedImage img) {
		Color c = new Color(img.getRGB(x, y));
		Color nColor = new Color(img.getRGB(x, y - 1));
		Color eColor = new Color(img.getRGB(x + 1, y));
		Color wColor = new Color(img.getRGB(x - 1, y));
		Color sColor = new Color(img.getRGB(x, y + 1));
		
		int color = c.getRed();
		int n = nColor.getRed();
		int e = eColor.getRed();
		int w = wColor.getRed();
		int s = sColor.getRed();
		
		if (color > 100 && n < 100 && e < 100 && w < 100 && s < 100)
			return true;
		return false;
	}
	
	//tests if a white pixel is almost individual
	public static boolean pixelIsWhtIslandStrong(int x, int y, BufferedImage img) {
		Color c = new Color(img.getRGB(x, y));
		Color nColor = new Color(img.getRGB(x, y - 1));
		Color eColor = new Color(img.getRGB(x + 1, y));
		Color wColor = new Color(img.getRGB(x - 1, y));
		Color sColor = new Color(img.getRGB(x, y + 1));
		
		int color = c.getRed();
		int n = nColor.getRed();
		int e = eColor.getRed();
		int w = wColor.getRed();
		int s = sColor.getRed();
		
		if (color > 100 && ((n < 100 && e < 100 && w < 100 && s < 100) || (e < 100 && w < 100 && s < 100) || (n < 100 && w < 100 && s < 100) || (n < 100 && e < 100 && s < 100) || (n < 100 && e < 100 && w < 100)))
			return true;
		return false;
	}
	
	//tests if black pixel is individual
	public static boolean pixelIsBlkIsland(int x, int y, BufferedImage img) {
		Color c = new Color(img.getRGB(x, y));
		Color nColor = new Color(img.getRGB(x, y - 1));
		Color eColor = new Color(img.getRGB(x + 1, y));
		Color wColor = new Color(img.getRGB(x - 1, y));
		Color sColor = new Color(img.getRGB(x, y + 1));
		
		int color = c.getRed();
		int n = nColor.getRed();
		int e = eColor.getRed();
		int w = wColor.getRed();
		int s = sColor.getRed();
		
		if (color < 100 && n > 100 && e > 100 && w > 100 && s > 100)
			return true;
		return false;
	}
	
	//tests if black pixel is almost individual
	public static boolean pixelIsBlkIslandStrong(int x, int y, BufferedImage img) {
		Color c = new Color(img.getRGB(x, y));
		Color nColor = new Color(img.getRGB(x, y - 1));
		Color eColor = new Color(img.getRGB(x + 1, y));
		Color wColor = new Color(img.getRGB(x - 1, y));
		Color sColor = new Color(img.getRGB(x, y + 1));
		
		int color = c.getRed();
		int n = nColor.getRed();
		int e = eColor.getRed();
		int w = wColor.getRed();
		int s = sColor.getRed();
		
		if (color < 100 && ((n > 100 && e > 100 && w > 100 && s > 100) || (e > 100 && w > 100 && s > 100) || (n > 100 && w > 100 && s > 100) || (n > 100 && e > 100 && s > 100) || (n > 100 && e > 100 && w > 100)))
			return true;
		return false;
	}
	
	//prints the minimum, maximum, and average pixel value
	public static void pixelInfo(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight() - 64;
		
		int pixels[] = new int[width * height];
		int pixelCount = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color c = new Color(img.getRGB(i, j));
				pixels[pixelCount] = c.getRed();
				pixelCount++;
			}
		}
		
		int min = GenUtils.min(pixels);
		int max = GenUtils.max(pixels);
		int average = (int)GenUtils.average(pixels);
		
		System.out.println("Min: " + min + ", Max: " + max + ", Average: " + average);
	}
	
	//more advanced noise reduction algorithm
	//TODO: revert stronger filter changes
	public static BufferedImage medianFilter(BufferedImage img) {
		BufferedImage cpy = deepCopy(img);
		
		int width = cpy.getWidth();
		int height = cpy.getHeight();
		
		int[][] pixels = new int[height - 2][width - 2];
		
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				pixels[j - 1][i - 1] = medianOfPixels(cpy, i, j);
			}
		}
		
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				int newColor = pixels[j-1][i-1];
				Color newC = new Color(newColor,newColor,newColor);
				cpy.setRGB(i, j, newC.getRGB());
			}
		}
		
		return cpy;
	}
	
	//gets the median of a pixel's neighbors
	public static int medianOfPixels(BufferedImage img, int x, int y) {
		ArrayList<Integer> pixels = new ArrayList<Integer>();
		
		Color n = new Color(img.getRGB(x, y-1));
		Color ne = new Color(img.getRGB(x+1, y-1));
		Color e = new Color(img.getRGB(x+1, y));
		Color se = new Color(img.getRGB(x+1, y+1));
		Color s = new Color(img.getRGB(x, y+1));
		Color sw = new Color(img.getRGB(x-1, y+1));
		Color w = new Color(img.getRGB(x-1, y));
		Color nw = new Color(img.getRGB(x-1, y-1));
		/*Color nnw = new Color(img.getRGB(x-1, y-2));
		Color nn = new Color(img.getRGB(x, y-2));
		Color nne = new Color(img.getRGB(x+1, y-2));
		Color ssw = new Color(img.getRGB(x-1, y+2));
		Color ss = new Color(img.getRGB(x, y+2));
		Color sse = new Color(img.getRGB(x+1, y+2));*/
		
		pixels.add(n.getRed());
		pixels.add(ne.getRed());
		pixels.add(e.getRed());
		pixels.add(se.getRed());
		pixels.add(s.getRed());
		pixels.add(sw.getRed());
		pixels.add(w.getRed());
		pixels.add(nw.getRed());
		/*pixels.add(nnw.getRed());
		pixels.add(nn.getRed());
		pixels.add(nne.getRed());
		pixels.add(ssw.getRed());
		pixels.add(ss.getRed());
		pixels.add(sse.getRed());*/
		
		Collections.sort(pixels);
		
		return (pixels.get(3) + pixels.get(2)) / 2;
	}
	
	//UNUSED, gets the average of a pixel's neighbors
	public static int averageOfPixels(BufferedImage img, int x, int y) {
		ArrayList<Integer> pixels = new ArrayList<Integer>();
		
		Color n = new Color(img.getRGB(x, y-1));
		Color ne = new Color(img.getRGB(x+1, y-1));
		Color e = new Color(img.getRGB(x+1, y));
		Color se = new Color(img.getRGB(x+1, y+1));
		Color s = new Color(img.getRGB(x, y+1));
		Color sw = new Color(img.getRGB(x-1, y+1));
		Color w = new Color(img.getRGB(x-1, y));
		Color nw = new Color(img.getRGB(x-1, y-1));
		
		pixels.add(n.getRed());
		pixels.add(ne.getRed());
		pixels.add(e.getRed());
		pixels.add(se.getRed());
		pixels.add(s.getRed());
		pixels.add(sw.getRed());
		pixels.add(w.getRed());
		pixels.add(nw.getRed());
		
		return (int)GenUtils.average(pixels);
	}
	
	public static BufferedImage cutBottom(BufferedImage img) {
		BufferedImage cpy = new BufferedImage(img.getWidth(), img.getHeight() - 64, img.TYPE_INT_RGB);
		Graphics2D g2d = cpy.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		return cpy;
	}
	
	public static BufferedImage toBufferedImage(Image img){
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	public static BufferedImage gaussianBlur(BufferedImage img) {
		Kernel kernel = new Kernel(9, 9, new float[] {
				0f, 0.000001f, 0.000014f, 0.000055f, 0.000088f, 0.000055f, 0.000014f, 0.000001f, 0f, 
				0.000001f, 0.000036f, 0.000362f, 0.001445f, 0.002289f, 0.001445f, 0.000362f, 0.000036f, 0.000001f,
				0.000014f, 0.000362f, 0.003672f, 0.014648f, 0.023205f, 0.014648f, 0.003672f, 0.000362f, 0.000014f,
				0.000055f, 0.001445f, 0.014648f, 0.058434f, 0.092566f, 0.058434f, 0.014648f, 0.001445f, 0.000055f,
				0.000088f, 0.002289f, 0.023205f, 0.092566f, 0.146634f, 0.092566f, 0.023205f, 0.002289f, 0.000088f,
				0.000055f, 0.001445f, 0.014648f, 0.058434f, 0.092566f, 0.058434f, 0.014648f, 0.001445f, 0.000055f,
				0.000014f, 0.000362f, 0.003672f, 0.014648f, 0.023205f, 0.014648f, 0.003672f, 0.000362f, 0.000014f,
				0.000001f, 0.000036f, 0.000362f, 0.001445f, 0.002289f, 0.001445f, 0.000362f, 0.000036f, 0.000001f,
				0f, 0.000001f, 0.000014f, 0.000055f, 0.000088f, 0.000055f, 0.000014f, 0.000001f, 0f});
		    BufferedImageOp op = new ConvolveOp(kernel);
		    return op.filter(img, null);
	}
	
	public static BufferedImage sobel3(BufferedImage img) {
		Kernel kernel = new Kernel(3, 3, new float[]{
				-1f, 0f, 1f,
				-2f, 0f, 2f,
				-1f, 0f, 1f
		});
		BufferedImageOp op = new ConvolveOp(kernel);
		return op.filter(img, null);
	}
	
	public static BufferedImage sobel7(BufferedImage img) {
		Kernel kernel = new Kernel(7, 7, new float[]{
				0f, -1f, -2f, 0f, 2f, 1f, 0f,
				-1f, -2f, -4f, 0f, 4f, 2f, 1f,
				-2f, -4f, -8f, 0f, 8f, 4f, 2f,
				-4f, -8f, -16f, 0f, 16f, 8f, 4f,
				-2f, -4f, -8f, 0f, 8f, 4f, 2f,
				-1f, -2f, -4f, 0f, 4f, 2f, 1f,
				0f, -1f, -2f, 0f, 2f, 1f, 0f
		});
		BufferedImageOp op = new ConvolveOp(kernel);
		return op.filter(img, null);
	}
}
