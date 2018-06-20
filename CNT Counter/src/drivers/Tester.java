package drivers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import utils.AFMUtils;
import utils.ImageUtils;

public class Tester {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter file: ");
		String input = in.nextLine();
		
		BufferedImage img = ImageUtils.readImage("images/" + input);
		
		img = ImageUtils.cutBottom(img);
		img = ImageUtils.averageExposure(img);
		img = ImageUtils.contrast(img);
		img = ImageUtils.contrastByRow(img);
		try {
			ImageIO.write(img, "jpg", new File("images/badout.jpg"));
		} catch (IOException e) {}
		img = AFMUtils.sharpen(img);
	}
}
