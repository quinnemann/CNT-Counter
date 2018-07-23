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
		BufferedImage img = ImageUtils.readImage("images/L223.tif");
		
		img = AFMUtils.blackAndWhite(img);
		
		double actualSize = AFMUtils.actualSize(img);
		img = AFMUtils.crop(img);
		
		final int SCAN_SIZE = 15;
		final int MIN_ANGLE = 85;
		final int MAX_ANGLE = 95;
		final int ANGLE_UNIT = 1;
		final int MIN_PEAK = 0;
		
		int scanHeight = img.getHeight() / 4;
		double[] vals1 = new double[img.getWidth()];
		for (int i = 0; i < vals1.length; i++) {
			//double angle = Grapher.maxAngle(img, i, scanHeight, SCAN_SIZE, MIN_ANGLE, MAX_ANGLE, ANGLE_UNIT);
			vals1[i] += GenUtils.average(Grapher.getAngledPixels(img, i, scanHeight, SCAN_SIZE, 90));
		}
		
		scanHeight *= 2;
		double[] vals2 = new double[img.getWidth()];
		for (int i = 0; i < vals2.length; i++) {
			double angle = Grapher.maxAngle(img, i, scanHeight, SCAN_SIZE, MIN_ANGLE, MAX_ANGLE, ANGLE_UNIT);
			vals2[i] += GenUtils.average(Grapher.getAngledPixels(img, i, scanHeight, SCAN_SIZE, angle));
		}
		
		scanHeight *= 1.5;
		double[] vals3 = new double[img.getWidth()];
		for (int i = 0; i < vals2.length; i++) {
			double angle = Grapher.maxAngle(img, i, scanHeight, SCAN_SIZE, MIN_ANGLE, MAX_ANGLE, ANGLE_UNIT);
			vals3[i] += GenUtils.average(Grapher.getAngledPixels(img, i, scanHeight, SCAN_SIZE, angle));
		}
		
		vals1 = Grapher.contrastVals(vals1);
		vals2 = Grapher.contrastVals(vals2);
		vals3 = Grapher.contrastVals(vals3);
		
		//20
		for (int i = 0; i < 2; i++) {
			vals1 = Grapher.avgVals(vals1);
			vals2 = Grapher.avgVals(vals2);
			vals3 = Grapher.avgVals(vals3);
		}
		
		BufferedImage graph = Grapher.drawGraph(vals2);
		
		BufferedImage combo = new BufferedImage(img.getWidth(), img.getHeight() + 256, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = combo.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.drawImage(graph, 0, img.getHeight(), null);
		
		g2d.setColor(Color.GREEN);
		g2d.fillRect(0, (img.getHeight() / 4) - 1, img.getWidth(), 3);
		g2d.fillRect(0, (img.getHeight() / 2) - 1, img.getWidth(), 3);
		g2d.fillRect(0, ((img.getHeight() / 4) * 3) - 1, img.getWidth(), 3);
		
		combo = Grapher.drawPeaks(vals1, MIN_PEAK, combo, img.getHeight() / 4, false);
		combo = Grapher.drawPeaks(vals2, MIN_PEAK, combo, img.getHeight() / 2, true);
		combo = Grapher.drawPeaks(vals3, MIN_PEAK, combo, (img.getHeight() / 4) * 3, false);
		
		double density1 = GenUtils.roundThousandths(Grapher.numPeaks(vals1, MIN_PEAK) / actualSize);
		double density2 = GenUtils.roundThousandths(Grapher.numPeaks(vals2, MIN_PEAK) / actualSize);
		double density3 = GenUtils.roundThousandths(Grapher.numPeaks(vals3, MIN_PEAK) / actualSize);
		
		System.out.println(density1);
		System.out.println(density2);
		System.out.println(density3);
		System.out.println((density1 + density2 + density3) / 3);
		
		try {ImageIO.write(combo, "jpg", new File("images/graph.jpg"));} catch (IOException e) {}
	}
}
