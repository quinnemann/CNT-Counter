package dataStructures;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import utils.ImageUtils;

//This structure contains a stack of images used for the undo feature
public class ImageStack {
	
	ArrayList<BufferedImage> list;
	
	public ImageStack() {
		list = new ArrayList<BufferedImage>();
	}
	
	public void edit(BufferedImage img) {
		list.set(0, ImageUtils.deepCopy(img));
	}
	
	public BufferedImage original() {
		return list.get(list.size() - 1);
	}
	
	public BufferedImage peek() {
		return list.get(0);
	}
	
	public BufferedImage pop() {
		BufferedImage temp = list.get(0);
		list.remove(0);
		return temp;
	}
	
	public void push(BufferedImage img) {
		list.add(0, ImageUtils.deepCopy(img));
	}
	
	public int size() {
		return list.size();
	}
}
