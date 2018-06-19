package drivers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.imageio.ImageIO;

import utils.*;

public class Main {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		BufferedImage img = ImageUtils.readImage("images/AFM2.tif");
		
		img = AFMUtils.blackAndWhite(img);
		
		double size = AFMUtils.actualSize(img, 100);
		
		img = AFMUtils.crop(img);
		try {
			ImageIO.write(img, "jpg", new File("images/AFM1out.jpg"));
		} catch (IOException e) {}
		
		img = ImageUtils.medianFilter(img);
		
		img = AFMUtils.sharpen(img);
		
		img = ImageUtils.contrastByRow(img);
		
		System.out.println(TubeDetector.detectTubes(img) / size);
		try {
			ImageIO.write(img, "jpg", new File("images/AFM1out2.jpg"));
		} catch (IOException e) {}
		
		System.out.println("Done!");
		in.close();
	}
}
