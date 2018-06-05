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
		
		double size = ImageUtils.actualSize(img);
		System.out.println(size + " micrometers");
		
		
		
		ImageUtils.averageExposure(img);
		ImageUtils.contrast(img);
		ImageUtils.contrastByRow(img);
		
		try {
			ImageIO.write(img, "jpg", new File("images/" + file + "output.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ImageUtils.medianFilter(img);
		ImageUtils.medianFilter(img);
		ImageUtils.medianFilter(img);
		
		try {
			ImageIO.write(img, "jpg", new File("images/" + file + "output2.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CannyEdgeDetector detector = new CannyEdgeDetector();
		detector.setSourceImage(img);
		detector.setLowThreshold(75f);
		detector.setGaussianKernelRadius(3f);
		detector.process();
		BufferedImage edges = detector.getEdgesImage();
		
		edges = new BufferedImage(edges.getWidth(), edges.getHeight(), edges.TYPE_BYTE_GRAY);
		
		try {
			ImageIO.write(edges, "jpg", new File("images/" + file + "output3.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int width = img.getWidth();
		int height = img.getHeight() - 64;
		ArrayList<Integer> rowPeaks = new ArrayList<Integer>();
		for (int i = 1; i < height - 1; i++) {
			int[] row = new int[width];
			for (int j = 1; j < width-1; j++) {
				Color c = new Color(edges.getRGB(j,i));
				row[j] = c.getRed();
			}
			rowPeaks.add(numPeaks(row));
		}
		Collections.sort(rowPeaks);
		int median = rowPeaks.get(rowPeaks.size() / 2);
		int average = (int)GenUtils.average(rowPeaks);
		
		System.out.println("Estimated tubes by average: " + average);
		System.out.println("Estimated tubes by median: " + median);
		System.out.println("Estimated density: " + 2 * median / size);
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
