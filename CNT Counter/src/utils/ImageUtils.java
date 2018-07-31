package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
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
	
	//more advanced noise reduction algorithm
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
	
	//cuts the bottom of an SEM image
	public static BufferedImage cutBottom(BufferedImage img) {
		BufferedImage cpy = new BufferedImage(img.getWidth(), img.getHeight() - 64, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = cpy.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		return cpy;
	}
	
	//transforms Image to a BufferedImage
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
	
	//3x3 sobel filter
	public static BufferedImage sobel3(BufferedImage img) {
		Kernel kernel = new Kernel(3, 3, new float[]{
				-1f, 0f, 1f,
				-2f, 0f, 2f,
				-1f, 0f, 1f
		});
		BufferedImageOp op = new ConvolveOp(kernel);
		return op.filter(img, null);
	}
	
	//7x7 sobel filter
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
	
	//completely custom sobel filter
	public static BufferedImage customSobel(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		double[][] resMatrix = new double[width][height];
		
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				Color neColor = new Color(img.getRGB(i + 1, j - 1));
				Color eColor = new Color(img.getRGB(i + 1, j));
				Color seColor = new Color(img.getRGB(i + 1, j + 1));
				Color swColor = new Color(img.getRGB(i - 1, j + 1));
				Color wColor = new Color(img.getRGB(i - 1, j));
				Color nwColor = new Color(img.getRGB(i - 1, j - 1));
				
				int ne = neColor.getRed();
				int e = eColor.getRed() * 2;
				int se = seColor.getRed();
				int sw = swColor.getRed() * -1;
				int w = wColor.getRed() * -2;
				int nw = nwColor.getRed() * -1;
				
				resMatrix[i][j] = ne + e + se + sw + w + nw;
			}
		}
		
		double min = GenUtils.min(resMatrix[0]);
		for (int i = 0; i < resMatrix.length; i++) {
			if (GenUtils.min(resMatrix[i]) < min) {
				min = GenUtils.min(resMatrix[i]);
			}
		}
		
		double max = GenUtils.max(resMatrix[0]);
		for (int i = 0; i < resMatrix.length; i++) {
			if (GenUtils.max(resMatrix[i]) > max) {
				max = GenUtils.max(resMatrix[i]);
			}
		}
		
		for (int i = 0; i < resMatrix.length; i++) {
			for (int j = 0; j < resMatrix[i].length; j++) {
				resMatrix[i][j] = (resMatrix[i][j] - min) * (255.0 / (max - min));
			}
		}
		
		Graphics2D g2d = result.createGraphics();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				g2d.setColor(new Color((int)resMatrix[i][j], (int)resMatrix[i][j], (int)resMatrix[i][j]));
				g2d.drawRect(i, j, 1, 1);
			}
		}
		
		return result;
	}
	
	//threshold an image manually
	public static BufferedImage manualThreshold(BufferedImage img, int threshold) {
		int width = img.getWidth();
		int height = img.getHeight();
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g2d = result.createGraphics();
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color c = new Color(img.getRGB(i, j));
				if (c.getRed() > threshold) {
					g2d.setColor(Color.WHITE);
					g2d.fillRect(i, j, 1, 1);
				} else {
					g2d.setColor(Color.BLACK);
					g2d.fillRect(i, j, 1, 1);
				}
			}
		}
		
		return result;
	}
}
