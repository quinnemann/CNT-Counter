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
		
		img = ImageUtils.averageExposure(img);
		img = ImageUtils.contrast(img);
		img = ImageUtils.contrastByRow(img);

		try {
			ImageIO.write(img, "jpg", new File("images/" + file + "_test.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
