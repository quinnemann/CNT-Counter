package drivers;

import java.awt.image.BufferedImage;

import utils.AFMUtils;
import utils.ImageUtils;

public class Tester {

	public static void main(String[] args) {
		BufferedImage img = ImageUtils.readImage("images/AFM1.tif");
		
		img = AFMUtils.blackAndWhite(img);
		
		//double actualSize = AFMUtils.actualSize(img);
		img = AFMUtils.crop(img);
		
		
		//try {ImageIO.write(combo, "jpg", new File("images/graph.jpg"));} catch (IOException e) {}
	}
}
