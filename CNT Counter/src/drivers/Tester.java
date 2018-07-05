package drivers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import utils.GenUtils;
import utils.Grapher;
import utils.ImageUtils;

public class Tester {

	public static void main(String[] args) {
		BufferedImage img = ImageUtils.readImage("images/bad.jpg");
		double actualSize = ImageUtils.actualSize(img);
		img = ImageUtils.cutBottom(img);
		//img = ImageUtils.averageExposure(img);
		//img = ImageUtils.contrast(img);
		
		//img = ImageUtils.contrastByRow(img);
		
		//img = ImageUtils.medianFilter(img);
		
		/*final int SIZE = 50;
		
		double[] total = new double[img.getWidth()];
		for (int i = 0; i < SIZE; i++) {
			double[] arr = Grapher.getGraph(img, 300, SIZE);
			for (int j = 0; j < arr.length; j++) {
				total[j] += arr[j] / SIZE;
			}
		}
		BufferedImage graph = Grapher.drawGraph(total);
		
		BufferedImage combo = new BufferedImage(img.getWidth(), img.getHeight() + 256, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = combo.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.drawImage(graph, 0, img.getHeight(), null);
		
		try {
			ImageIO.write(combo, "jpg", new File("images/graph.jpg"));
		} catch (IOException e1) {}*/
		
		double[] vals = new double[img.getWidth()];
		for (int i = 0; i < vals.length; i++) {
			double angle = Grapher.maxAngle(img, i, 500, 20, 45, 135, 5);
			vals[i] = GenUtils.average(Grapher.getAngledPixels(img, i, 500, 20, angle));
		}
		
		vals = Grapher.contrastVals(vals);
		
		for (int i = 0; i < 4; i++) {
			vals = avgVals(vals);
		}
		
		System.out.println(GenUtils.numPeaks(vals));
		System.out.println(actualSize);
		System.out.println(GenUtils.numPeaks(vals) / actualSize);
		
		BufferedImage graph = Grapher.drawGraph(vals);
		BufferedImage combo = new BufferedImage(img.getWidth(), img.getHeight() + 256, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = combo.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.drawImage(graph, 0, img.getHeight(), null);
		
		g2d.setColor(Color.GREEN);
		g2d.fillRect(0, 499, img.getWidth(), 3);
		
		try {ImageIO.write(combo, "jpg", new File("images/graph.jpg"));} catch (IOException e) {}
	}
	
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
