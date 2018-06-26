package drivers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import utils.ImageUtils;

public class Tester {

	public static void main(String[] args) {
		BufferedImage img = ImageUtils.readImage("images/20170405_test3_35.jpg");
		img = ImageUtils.cutBottom(img);
		img = ImageUtils.averageExposure(img);
		img = ImageUtils.contrast(img);
		
		//img = ImageUtils.contrastByRow(img);
		
		//img = ImageUtils.medianFilter(img);
		
		BufferedImage graph = ImageUtils.colGraph(img);
		
		Graphics2D g2d = img.createGraphics();
		/*g2d.setColor(Color.GREEN);
		g2d.fillRect(0, 399, img.getWidth(), 3);*/
		
		//img = img.getSubimage(0, 0, img.getWidth(), 425);
		
		BufferedImage fina = new BufferedImage(img.getWidth(), img.getHeight() + 256, BufferedImage.TYPE_INT_RGB);
		
		img = ImageUtils.contrastByRow(img);
		
		g2d = fina.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.drawImage(graph, 0, img.getHeight(), null);
		
		try {
			ImageIO.write(fina, "jpg", new File("images/test.jpg"));
		} catch (IOException e1) {}
		
		try {
			ImageIO.write(graph, "jpg", new File("images/graph.jpg"));
		} catch (IOException e1) {}
	}
}
