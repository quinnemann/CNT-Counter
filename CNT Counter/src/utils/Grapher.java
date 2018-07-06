package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Grapher {
	
	public static double[] getGraph(BufferedImage img, int row, int size) {
		double[] pixels = new double[img.getWidth()];
		for (int i = 0; i < img.getWidth(); i++) {
			pixels[i] = 0;
		}
		
		int j = row - size / 2;
		for (int count = 0; count < size; count++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color c = new Color(img.getRGB(i, j));
				pixels[i] += (double)c.getRed() / size;
			}
			j++;
		}
		
		return pixels;
	}
	
	public static BufferedImage rowGraph(BufferedImage img, int row) {
		BufferedImage graph = new BufferedImage(img.getWidth(), 256, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = graph.createGraphics();
		g2d.setColor(Color.WHITE);
		
		for (int i = 0; i < img.getWidth(); i++) {
			Color light = new Color(img.getRGB(i, row - 1));
			int val = light.getRed() / 3;
			g2d.fillRect(i, 86 - val, 1, val);
		}
		
		for (int i = 0; i < img.getWidth(); i++) {
			Color light = new Color(img.getRGB(i, row));
			int val = light.getRed() / 3;
			g2d.fillRect(i, 171 - val, 1, val);
		}
		
		for (int i = 0; i < img.getWidth(); i++) {
			Color light = new Color(img.getRGB(i, row + 1));
			int val = light.getRed() / 3;
			g2d.fillRect(i, 256 - val, 1, val);
		}
		
		return graph;
	}
	
	public static BufferedImage rowGraphNoContrast(BufferedImage img, int row) {
		BufferedImage graph = new BufferedImage(img.getWidth(), 256, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = graph.createGraphics();
		g2d.setColor(Color.WHITE);
		
		int[] vals = new int[img.getWidth()];
		for (int i = 0; i < img.getWidth(); i++) {
			int val = 0;
			Color light = new Color(img.getRGB(i, row - 2));
			val += light.getRed() / 5;
			light = new Color(img.getRGB(i, row - 1));
			val += light.getRed() / 5;
			light = new Color(img.getRGB(i, row));
			val += light.getRed() / 5;
			light = new Color(img.getRGB(i, row + 1));
			val += light.getRed() / 5;
			light = new Color(img.getRGB(i, row + 1));
			val += light.getRed() / 5;
			
			vals[i] = val;
		}
		
		int min = GenUtils.min(vals);
		int max = GenUtils.max(vals);
		double mult = 255.0 / (max - min);
		
		for (int i = 0; i < vals.length; i++) {
			vals[i] = (int)Math.round((vals[i] - min) * mult);
		}
		
		for (int i = 0; i < vals.length; i++) {
			g2d.fillRect(i, 256 - vals[i], 1, vals[i]);
		}
		
		return graph;
	}
	
	public static BufferedImage colGraph(BufferedImage img) {
		BufferedImage graph = new BufferedImage(img.getWidth(), 256, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = graph.createGraphics();
		g2d.setColor(Color.WHITE);
		
		int vals[] = new int[img.getWidth()];
		for (int i = 0; i < img.getWidth(); i++) {
			int[] col = new int[img.getHeight()];
			for (int j = 0; j < img.getHeight(); j++) {
				Color light = new Color(img.getRGB(i, j));
				col[j] = light.getRed();
			}
			vals[i] = (int)Math.round(GenUtils.average(col));
		}
		
		int min = GenUtils.min(vals);
		int max = GenUtils.max(vals);
		double mult = 255.0 / (max - min);
		
		for (int i = 0; i < vals.length; i++) {
			vals[i] = (int)Math.round((vals[i] - min) * mult);
		}
		
		for (int i = 0; i < vals.length; i++) {
			g2d.fillRect(i, 256 - vals[i], 1, vals[i]);
		}
		
		return graph;
	}
	
	public static int getPixel(BufferedImage img, int x, int y) {
		Color c = new Color(img.getRGB(x, y));
		return c.getRed();
	}
	
	public static double[] contrastVals(double[] vals) {
		double min = GenUtils.min(vals);
		double max = GenUtils.max(vals);
		double mult = 255.0 / (max - min);
		
		for (int i = 0; i < vals.length; i++) {
			vals[i] = (vals[i] - min) * mult;
		}
		
		return vals;
	}
	
	public static BufferedImage drawGraph(double[] vals) {
		BufferedImage graph = new BufferedImage(vals.length, 256, BufferedImage.TYPE_INT_RGB);
		
		vals = contrastVals(vals);
		
		Graphics2D g2d = graph.createGraphics();
		g2d.setColor(Color.WHITE);
		
		for (int i = 0; i < vals.length; i++) {
			int val = (int)Math.round(vals[i]);
			g2d.fillRect(i, 256 - val, 1, val);
		}
		
		return graph;
	}
	
	public static ArrayList<Integer> getAngledPixels(BufferedImage img, int x, int y, int size, double angle) {
		ArrayList<Integer> vals = new ArrayList<Integer>();
		angle = GenUtils.degreeToRadian(angle);
		
		int count = 0;
		int i = 0 - (size / 2);
		while (count < size) {
			int newX = (int)Math.round(1.0 / Math.tan(angle) * i) + x;
			int newY = i + y;
			if (newX >= 0 && newX < img.getWidth() && newY >= 0 && newY < img.getHeight()) {
				vals.add(getPixel(img, newX, newY));
			}
			count++;
			i++;
		}
		
		return vals;
	}
	
	public static double maxAngle(BufferedImage img, int x, int y, int size, double minAngle, double maxAngle, double iteration) {
		double max = minAngle;
		for (double i = minAngle + iteration; i <= maxAngle; i += iteration) {
			ArrayList<Integer> maxVals = getAngledPixels(img, x, y, size, max);
			ArrayList<Integer> nextVals = getAngledPixels(img, x, y, size, i);
			if (GenUtils.average(maxVals) < GenUtils.average(nextVals)) {
				max = i;
			}
		}
		
		return max;
	}
}
