package drivers;

import java.awt.image.BufferedImage;
import java.util.Scanner;

import utils.*;

public class Tester {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter file: ");
		String file = in.next();
		BufferedImage img = ImageUtils.readImage("images/" + file + ".jpg");
		
		img = ImageUtils.averageExposure(img);
		img = ImageUtils.contrast(img);
		
		in.close();
	}

}
