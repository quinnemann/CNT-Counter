package drivers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import utils.*;

public class Tester {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter file: ");
		String file = in.next();
		BufferedImage img = ImageUtils.readImage("images/" + file + ".jpg");
		
		BufferedImage cpy = new BufferedImage(img.getWidth(), img.getHeight(), img.TYPE_INT_RGB);
		Graphics2D g2d = cpy.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		
		g2d.setColor(Color.green);
		g2d.fillRect(20, 20, 20, 20);

		try {
			ImageIO.write(cpy, "jpg", new File("images/" + file + "_test.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
