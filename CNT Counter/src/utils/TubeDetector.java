package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TubeDetector {
	
	public static int detectTubes(BufferedImage img) {
		
		
		int width = img.getWidth();
		int height = img.getHeight() - 64;
		
		
		
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
					}
					inTube = 0;
				} else {
					inTube++;
				}
			}
			tubesPerRow.add(tubes);
		}
		
		return tubesPerRow.get(tubesPerRow.size() / 2);
	}
	
	public static BufferedImage drawTubes(BufferedImage img) {
		BufferedImage cpy = new BufferedImage(img.getWidth(), img.getHeight(), img.TYPE_INT_RGB);
		Graphics2D g2d = cpy.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		
		int width = img.getWidth();
		int height = img.getHeight() - 64;
		
		g2d.setColor(Color.blue);
		
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
						g2d.fillRect(j - (inTube / 2) - 3, i, 6, 1);
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
	
}
