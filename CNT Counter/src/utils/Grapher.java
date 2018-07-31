package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import dataStructures.Extremum;

public class Grapher {
	
	//gets the value of a pixel at coordinates (x, y)
	public static int getPixel(BufferedImage img, int x, int y) {
		Color c = new Color(img.getRGB(x, y));
		return c.getRed();
	}
	
	//contrasts data to be from 0 to 255
	public static double[] contrastVals(double[] vals) {
		double min = GenUtils.min(vals);
		double max = GenUtils.max(vals);
		double mult = 255.0 / (max - min);
		
		for (int i = 0; i < vals.length; i++) {
			vals[i] = (vals[i] - min) * mult;
		}
		
		return vals;
	}
	
	//returns an image representation of values
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
	
	//gets a line of pixels at a specific angle
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
	
	//finds the angle where there is most likely to be a line (DOES NOT WORK)
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
	
	//finds the number of peaks in a set of values, ignoring all peaks smaller than diff
	public static int numPeaks(double[] vals, int diff) {
		int count = 0;
		
		ArrayList<Extremum> extrema = new ArrayList<Extremum>();
		for (int i = 0; i < vals.length; i++) {
			if (i == 0) {
				if (vals[i] > vals[i + 1]) {
					extrema.add(new Extremum(vals[i], true, i));
				} else if (vals[i] < vals[i + 1]) {
					extrema.add(new Extremum(vals[i], false, i));
				}
			} else if (i == vals.length - 1) {
				if (vals[i] > vals[i - 1]) {
					extrema.add(new Extremum(vals[i], true, i));
				} else if (vals[i] < vals[i - 1]) {
					extrema.add(new Extremum(vals[i], false, i));
				}
			} else {
				if (vals[i] > vals[i - 1] && vals[i] > vals[i + 1]) {
					extrema.add(new Extremum(vals[i], true, i));
				} else if (vals[i] < vals[i - 1] && vals[i] < vals[i + 1]) {
					extrema.add(new Extremum(vals[i], false, i));
				}
			}
		}
		
		for (int i = 1; i < extrema.size() - 1; i++) {
			if (extrema.get(i).isMax()) {
				Extremum prev = extrema.get(i - 1);
				Extremum curr = extrema.get(i);
				Extremum next = extrema.get(i + 1);
				if (!prev.isMax() && curr.getVal() - prev.getVal() > diff && !next.isMax() && curr.getVal() - next.getVal() > diff) {
					count++;
				}
			}
		}
		
		return count;
	}

	//draws the peaks on an image
	public static BufferedImage drawPeaks(double[] vals, int diff, BufferedImage img, int height, boolean isGraphHeight) {
		BufferedImage combo = ImageUtils.deepCopy(img);
		Graphics2D g2d = combo.createGraphics();
		g2d.setColor(Color.RED);
		
		ArrayList<Extremum> extrema = new ArrayList<Extremum>();
		for (int i = 0; i < vals.length; i++) {
			if (i == 0) {
				if (vals[i] > vals[i + 1]) {
					extrema.add(new Extremum(vals[i], true, i));
				} else if (vals[i] < vals[i + 1]) {
					extrema.add(new Extremum(vals[i], false, i));
				}
			} else if (i == vals.length - 1) {
				if (vals[i] > vals[i - 1]) {
					extrema.add(new Extremum(vals[i], true, i));
				} else if (vals[i] < vals[i - 1]) {
					extrema.add(new Extremum(vals[i], false, i));
				}
			} else {
				if (vals[i] > vals[i - 1] && vals[i] > vals[i + 1]) {
					extrema.add(new Extremum(vals[i], true, i));
				} else if (vals[i] < vals[i - 1] && vals[i] < vals[i + 1]) {
					extrema.add(new Extremum(vals[i], false, i));
				}
			}
		}
		
		for (int i = 1; i < extrema.size() - 1; i++) {
			if (extrema.get(i).isMax()) {
				Extremum prev = extrema.get(i - 1);
				Extremum curr = extrema.get(i);
				Extremum next = extrema.get(i + 1);
				if (!prev.isMax() && curr.getVal() - prev.getVal() > diff && !next.isMax() && curr.getVal() - next.getVal() > diff) {
					g2d.fillRect(curr.getX() - 2, height - 10, 5, 20);
					if (isGraphHeight) {
						g2d.fillRect(curr.getX() - 2, (int)(combo.getHeight() - curr.getVal() - 5), 5, 10);
					}
				}
			}
		}
		
		return combo;
	}
	
	//averages values to smooth noise
	public static double[] avgVals(double[] vals) {
		double[] avg = new double[vals.length];
		
		avg[0] = vals[0];
		avg[avg.length - 1] = vals[vals.length - 1];
		
		for (int i = 1; i < vals.length - 1; i++) {
			avg[i] = (vals[i - 1] + vals[i] + vals [i + 1]) / 3;
		}
		
		return avg;
	}
}
