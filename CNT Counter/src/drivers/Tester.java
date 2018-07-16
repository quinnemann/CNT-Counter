package drivers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import utils.AFMUtils;
import utils.GenUtils;
import utils.Grapher;
import utils.ImageUtils;

public class Tester {

	public static void main(String[] args) {
		BufferedImage img = ImageUtils.readImage("images/JEOL4.jpg");
		
		img = AFMUtils.blackAndWhite(img);
		
		double actualSize = ImageUtils.actualSize(img);
		img = ImageUtils.cutBottom(img);
		//img = ImageUtils.averageExposure(img);
		//img = ImageUtils.contrast(img);
		
		//img = ImageUtils.contrastByRow(img);
		
		/*BufferedImage sobelTest = ImageUtils.deepCopy(img);
		for (int i = 0; i < 0; i++) {
			sobelTest = ImageUtils.medianFilter(sobelTest);
		}
		sobelTest = ImageUtils.customSobel(sobelTest);
		try {ImageIO.write(sobelTest, "jpg", new File("images/test.jpg"));} catch (IOException e) {}*/
		
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
		
		double[][] vals = new double[img.getHeight()][img.getWidth()];
		for (int j = 0; j < vals.length; j++) {
			for (int i = 0; i < vals[j].length; i++) {
				double angle = Grapher.maxAngle(img, i, j, 5, 45, 135, 5);
				vals[j][i] += GenUtils.average(Grapher.getAngledPixels(img, i, j, 5, angle));
			}
		}
		
		for (int j = 0; j < vals.length; j++) {
			vals[j] = Grapher.contrastVals(vals[j]);
		}
		
		//20
		for (int j = 0; j < vals.length; j++) {
			for (int i = 0; i < 20; i++) {
				vals[j] = avgVals(vals[j]);
			}
		}
		
		BufferedImage graph = Grapher.drawGraph(vals[vals.length / 2]);
		
		BufferedImage combo = new BufferedImage(img.getWidth(), img.getHeight() + 256, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = combo.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.drawImage(graph, 0, img.getHeight(), null);
		
		g2d.setColor(Color.GREEN);
		g2d.fillRect(0, (img.getHeight() / 2) - 1, img.getWidth(), 3);
		
		combo = Grapher.numPeaks(vals[vals.length / 2], 15, combo);
		
		int total = 0;
		for (int j = 0; j < vals.length; j++) {
			total+= Grapher.numPeaks(vals[j], 15); //15
			System.out.println(Grapher.numPeaks(vals[j], 15) / actualSize);
		}
		double average = (double)total / vals.length;
		
		System.out.println(average / actualSize);
		
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
