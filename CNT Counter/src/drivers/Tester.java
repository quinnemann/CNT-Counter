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
		
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(new Color(0,255,0));
		g2d.fillRect ( 0, 0, img.getWidth(),img.getHeight() );

		try {
			ImageIO.write(img, "jpg", new File("images/" + file + "_test.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
