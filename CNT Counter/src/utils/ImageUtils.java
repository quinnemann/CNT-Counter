package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

//This class provides a variety of utility functions for CNT image manipulation
public class ImageUtils {
	
	//Reads in an image from a file
	public static BufferedImage readImage(String file) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		} while (c.getRed() < 100);
		
		int whtCount = 0;
		do {
			c = new Color(img.getRGB(blkCount + whtCount, scaleHeight));
			whtCount++;
		} while (c.getRed() > 100);
		
		return img.getWidth() / (double)whtCount / 10;
	}
	
	//averages out vertical differences in image exposure
	public static BufferedImage averageExposure(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight() - 64;
		int totalAverage = averageLight(img);
		
		for (int j = 0; j < height; j++) {
			int rowAverage = averageRowLight(img, j);
			int diff = (totalAverage - rowAverage) / 2;
			for (int i = 0; i < width; i++) {
				Color prev = new Color(img.getRGB(i, j));
				int newColor = prev.getRed() + diff;
				if (newColor > 255) {
					newColor = 255;
				} else if (newColor < 0) {
					newColor = 0;
				}
				Color newC = new Color(newColor, newColor, newColor);
				img.setRGB(i, j, newC.getRGB());
			}
		}
		
		return img;
	}
	
	//increases the contrast in the image to maximum with no data loss
	public static BufferedImage contrast(BufferedImage img) {
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
		
		double multiplier = 255.0 / (max - min);
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color c = new Color(img.getRGB(i, j));
				int light = c.getRed();
				int newLight = (int)Math.round((light - min) * multiplier);
				Color newC = new Color(newLight, newLight, newLight);
				img.setRGB(i, j, newC.getRGB());
			}
		}
		
		return img;
	}
	
	//maximum contrast using an threshold of the average pixel per row
	public static BufferedImage contrastByRow(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		Color blk = new Color(0, 0, 0);
		Color wht = new Color(255, 255, 255);
		
		
		for (int i = 0; i < height - 64; i++) {
			int average = averageRowLight(img, i);
			
			int threshold = average;
			
			for (int j = 0; j < width; j++) {
				Color c = new Color(img.getRGB(j, i));
				if (c.getRed() < threshold) {
					img.setRGB(j, i, blk.getRGB());
				} else {
					img.setRGB(j, i, wht.getRGB());
				}
			}
		}
		
		return img;
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
	public static BufferedImage noiseReduction(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		Color blk = new Color(0, 0, 0);
		Color wht = new Color(255, 255, 255);
		
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 65; j++) {
				if (pixelIsWhtIsland(i, j, img))
					img.setRGB(i, j, blk.getRGB());
				if (pixelIsBlkIsland(i, j, img))
					img.setRGB(i, j, wht.getRGB());
			}
		}
		
		return img;
	}
	
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
	public static BufferedImage medianFilter(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight() - 64;
		
		int[][] pixels = new int[height - 2][width - 2];
		
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				pixels[j - 1][i - 1] = medianOfPixels(img, i, j);
			}
		}
		
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				int newColor = pixels[j-1][i-1];
				Color newC = new Color(newColor,newColor,newColor);
				img.setRGB(i, j, newC.getRGB());
			}
		}
		
		return img;
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
		
		pixels.add(n.getRed());
		pixels.add(ne.getRed());
		pixels.add(e.getRed());
		pixels.add(se.getRed());
		pixels.add(s.getRed());
		pixels.add(sw.getRed());
		pixels.add(w.getRed());
		pixels.add(nw.getRed());
		
		Collections.sort(pixels);
		
		return (pixels.get(3) + pixels.get(2)) / 2;
	}
	
	//gets the average of a pixel's neighbors
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
	
	//enhances vertical lines in an image
	public static BufferedImage vertLineEnhancer(BufferedImage img) {
		
		
		
		return img;
	}
	
	public static BufferedImage green(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		Color green = new Color(0, 255, 0);
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				img.setRGB(i, j, green.getRGB());
			}
		}
		
		return img;
	}
}