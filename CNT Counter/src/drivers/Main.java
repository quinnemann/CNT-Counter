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
		
		
		
		img = ImageUtils.averageExposure(img);
		img = ImageUtils.contrast(img);
		img = ImageUtils.contrastByRow(img);
		
		try {
			ImageIO.write(img, "jpg", new File("images/" + file + "enhance.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < strength; i++) {
			img = ImageUtils.medianFilter(img);
		}
		
		try {
			ImageIO.write(img, "jpg", new File("images/" + file + "noise.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*CannyEdgeDetector detector = new CannyEdgeDetector();
		detector.setSourceImage(img);
		detector.setLowThreshold(75f);
		detector.setGaussianKernelRadius(3f);
		detector.process();
		BufferedImage edges = detector.getEdgesImage();
		
		try {
			ImageIO.write(edges, "jpg", new File("images/" + file + "output2.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		int tubes = TubeDetector.detectTubes(img);
		img = TubeDetector.drawTubes(img);
		
		try {
			ImageIO.write(img, "jpg", new File("images/" + file + "lines.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Estimated tubes: " + tubes);
		System.out.println("Estimated density: " + tubes / size);
		System.out.println("Done!");
		in.close();
	}
	
	static int numPeaks(int[] arr) {
		int count = 0;
		
		if (arr[0] > arr[1])
			count++;
		
		for (int i = 1; i < arr.length - 1; i++) {
			if (arr[i] > arr[i - 1] && arr[i] > arr[i + 1])
				count++;
		}
		
		if (arr[arr.length - 1] > arr[arr.length - 2])
			count++;
		
		return count;
	}
}
