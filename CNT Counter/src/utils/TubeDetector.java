package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
		
		BufferedImage cpy = ImageUtils.deepCopy(original);
		Graphics2D g2d = cpy.createGraphics();
		g2d.setColor(Color.BLUE);
		g2d.fillRect(0, drawHeight - 2, width, 5);
		
		g2d.setColor(Color.GREEN);
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
}
