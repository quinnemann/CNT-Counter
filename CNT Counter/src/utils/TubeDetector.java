package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class TubeDetector {
	
	public static int detectTubes(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(Color.blue);
		
		ArrayList<Integer> tubesPerRow = new ArrayList<Integer>();
		for (int i = 2; i < height - 2; i++) {
			int tubes = 0;
			int inTube = 0;
			for (int j = 1; j < width - 1; j++) {
				Color current = new Color(img.getRGB(j, i));
				int curr = current.getRed();
				if (curr < 100) {
					if (inTube > 0) {
						tubes++;
						if (tubesPerRow.size() == 800) {
							g2d.fillRect(j - (inTube / 2), 0, 4, height);
						}
					}
					inTube = 0;
				} else {
					inTube++;
				}
			}
			tubesPerRow.add(tubes);
		}
		g2d.fillRect(0, 800, width, 5);
		
		return tubesPerRow.get(tubesPerRow.size() / 2);
	}
	
	public static BufferedImage drawTubes(BufferedImage img) {
		BufferedImage cpy = new BufferedImage(img.getWidth(), img.getHeight(), img.TYPE_INT_RGB);
		Graphics2D g2d = cpy.createGraphics();
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		g2d.setColor(Color.white);
		
		ArrayList<Integer> tubesPerRow = new ArrayList<Integer>();
		for (int i = 2; i < height - 2; i++) {
			int tubes = 0;
			int inTube = 0;
			for (int j = 1; j < width - 1; j++) {
				Color current = new Color(img.getRGB(j, i));
				int curr = current.getRed();
				if (curr < 100) {
					if (inTube > 4) {
						tubes++;
						g2d.fillRect(j - (inTube / 2) - 1, i - 7, 3, 15);
					}
					inTube = 0;
				} else {
					inTube++;
				}
			}
			tubesPerRow.add(tubes);
		}
		
		return cpy;
	}
	
	public static double density(File file, int strength) {
		BufferedImage img = ImageUtils.readImage(file.getAbsolutePath());
		
		double size = ImageUtils.actualSize(img);
		
		//enhance image
		img = ImageUtils.cutBottom(img);
		img = ImageUtils.averageExposure(img);
		img = ImageUtils.contrast(img);
		img = ImageUtils.contrastByRow(img);
		
		//reduce noise
		for (int i = 0; i < strength; i++) {
			img = ImageUtils.medianFilter(img);
		}
		/*try {
			ImageIO.write(img, "jpg", new File(file.getParent() + "//output1.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*img = TubeDetector.drawTubes(img);
		try {
			ImageIO.write(img, "jpg", new File(file.getParent() + "//output2.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//edge detection
		CannyEdgeDetector detector = new CannyEdgeDetector();
		detector.setSourceImage(img);
		detector.setLowThreshold(2f);
		detector.setHighThreshold(5f);
		detector.setGaussianKernelRadius(3f);
		detector.process();
		BufferedImage edges = detector.getEdgesImage();
		/*try {
			ImageIO.write(edges, "jpg", new File(file.getParent() + "//output2.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		int tubes = TubeDetector.detectTubes(edges);
		
		/*try {
			ImageIO.write(edges, "jpg", new File(file.getParent() + "//output3.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		return tubes / size;
    }
}
