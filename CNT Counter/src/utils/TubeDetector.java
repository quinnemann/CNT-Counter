package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class TubeDetector {
	
	public static int detectTubes(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		
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
					}
					inTube = 0;
				} else {
					inTube++;
				}
			}
			tubesPerRow.add(tubes);
		}
		Collections.sort(tubesPerRow);
		
		return tubesPerRow.get((int) (tubesPerRow.size() * .5));
	}
	
	public static BufferedImage drawTubes(BufferedImage original, BufferedImage img, int drawHeight) {
		int width = img.getWidth();
		int height = img.getHeight();
		
		BufferedImage cpy = ImageUtils.deepCopy(original);
		Graphics2D g2d = cpy.createGraphics();
		g2d.setColor(new Color(152, 0, 255));
		g2d.fillRect(0, drawHeight - 2, width, 5);
		
		g2d.setColor(Color.BLUE);
		int inTube = 0;
		for (int j = 1; j < width - 1; j++) {
			Color current = new Color(img.getRGB(j, drawHeight));
			int curr = current.getRed();
			if (curr < 100) {
				if (inTube > 0) {
					g2d.fillRect(j - (inTube / 2), drawHeight - 10, 4, 20);
				}
				inTube = 0;
			} else {
				inTube++;
			}
		}
		
		return cpy;
	}

	
	public static double density(File file, int strength) {
		BufferedImage img = ImageUtils.readImage(file.getAbsolutePath());
		
		double size = ImageUtils.actualSize(img);
		
		//enhance image
		img = ImageUtils.cutBottom(img);
		
		
		//reduce noise
		for (int i = 0; i < strength; i++) {
			img = ImageUtils.gaussianBlur(img);
		}
		img = ImageUtils.sobel7(img);
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
