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
		System.out.print("Enter file: ");
		String file = in.next();
		BufferedImage img = ImageUtils.readImage("images/" + file + ".jpg");
		
		System.out.print("Enter noise reduction strength: ");
		int strength = in.nextInt();
		
		double size = ImageUtils.actualSize(img);
		System.out.println(size + " micrometers");
		
		//enhance image
		img = ImageUtils.averageExposure(img);
		img = ImageUtils.contrast(img);
		img = ImageUtils.contrastByRow(img);
		
		//reduce noise
		for (int i = 0; i < strength; i++) {
			img = ImageUtils.medianFilter(img);
		}
		try {
			ImageIO.write(img, "jpg", new File("images/" + file + "noise.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		img = TubeDetector.drawTubes(img);
		img = ImageUtils.medianFilter(img);
		try {
			ImageIO.write(img, "jpg", new File("images/" + file + "redraw.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//edge detection
		CannyEdgeDetector detector = new CannyEdgeDetector();
		detector.setSourceImage(img);
		detector.setLowThreshold(2f);
		detector.setHighThreshold(5f);
		detector.setGaussianKernelRadius(3f);
		detector.process();
		BufferedImage edges = detector.getEdgesImage();
		try {
			ImageIO.write(edges, "jpg", new File("images/" + file + "z.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int tubes = TubeDetector.detectTubes(edges) / 2;
		
		System.out.println("Estimated tubes: " + tubes);
		System.out.println("Estimated density: " + tubes / size);
		System.out.println("Done!");
		in.close();
	}
}
