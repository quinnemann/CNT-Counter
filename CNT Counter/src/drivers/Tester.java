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
		
		for (int i = 0; i < 5; i++) {
			img = ImageUtils.medianFilter(img);
		}
		BufferedImage sobelTest = ImageUtils.customSobel(img);
		
		//sobelTest = ImageUtils.contrastByRow(sobelTest);
		
		try {ImageIO.write(sobelTest, "jpg", new File("images/test.jpg"));} catch (IOException e) {}

		img = ImageUtils.sobel7(img);
		
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
		
		int scanHeight = img.getHeight() / 4;
		double[] vals1 = new double[img.getWidth()];
		for (int i = 0; i < vals1.length; i++) {
			double angle = Grapher.maxAngle(img, i, scanHeight, 50, 0, 180, 5);
			vals1[i] += GenUtils.average(Grapher.getAngledPixels(img, i, scanHeight, 50, angle)) / 3;
		}
		
		scanHeight *= 2;
		double[] vals2 = new double[img.getWidth()];
		for (int i = 0; i < vals2.length; i++) {
			double angle = Grapher.maxAngle(img, i, scanHeight, 5, 0, 180, 5);
			vals2[i] += GenUtils.average(Grapher.getAngledPixels(img, i, scanHeight, 5, angle)) / 3;
		}
		
		scanHeight += img.getHeight() / 4;
		double[] vals3 = new double[img.getWidth()];
		for (int i = 0; i < vals3.length; i++) {
			double angle = Grapher.maxAngle(img, i, scanHeight, 50, 0, 180, 5);
			vals3[i] += GenUtils.average(Grapher.getAngledPixels(img, i, scanHeight, 50, angle)) / 3;
		}
		
		vals1 = Grapher.contrastVals(vals1);
		vals2 = Grapher.contrastVals(vals2);
		vals3 = Grapher.contrastVals(vals3);
		
		for (int i = 0; i < 25; i++) {
			vals1 = avgVals(vals1);
			vals2 = avgVals(vals2);
			vals3 = avgVals(vals3);
		}
		
		System.out.println(GenUtils.numPeaks(vals1) / actualSize);
		System.out.println(GenUtils.numPeaks(vals2) / actualSize);
		System.out.println(GenUtils.numPeaks(vals3) / actualSize);
		
		BufferedImage graph = Grapher.drawGraph(vals2);
		BufferedImage combo = new BufferedImage(img.getWidth(), img.getHeight() + 256, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = combo.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.drawImage(graph, 0, img.getHeight(), null);
		
		g2d.setColor(Color.GREEN);
		g2d.fillRect(0, (img.getHeight() / 2) - 1, img.getWidth(), 3);
		
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
