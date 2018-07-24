package manualDetection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import dataStructures.ImageStack;
import utils.AFMUtils;
import utils.GenUtils;
import utils.ImageUtils;

public class ImageViewer {
	
	private ImageStack imageStack;
	private BufferedImage display;
	private int vertPos = 0;
	private double actualSize;
	private double scale;
	private String lineCounts = "";
	private String sizeCounts = "";
	private boolean isAfm = false;
	
	public ImageViewer(String[] args) {
		//get files
		String[] files = args[0].split("\\?");
		
		//get current iteration
		int count = Integer.parseInt(args[3]);
		
		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout(1, 2, 0, 0));
		
		//create a stack of images used for undo feature
		imageStack = new ImageStack();
		
		//get image for current iteration
		BufferedImage img = ImageUtils.readImage(files[count]);
		
		//detect difference between AFM and SEM images
		if (img.getWidth() < 1000) {
			isAfm = true;
		}
		
		if (isAfm) { //get size, crop AFM image
			actualSize = AFMUtils.actualSize(img);
			img = AFMUtils.crop(img);
			img = img.getSubimage(0, 0, img.getWidth(), img.getHeight());
		} else {//get size, crop, enhance SEM image
			actualSize = ImageUtils.actualSize(img);
			img = ImageUtils.cutBottom(img);
			img = ImageUtils.averageExposure(img);
			img = ImageUtils.contrast(img);
		}
		
		//scale image to 3/4 screen height
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		scale = (screenSize.getHeight() / img.getHeight()) * .75;
		img = ImageUtils.toBufferedImage(img.getScaledInstance((int)(scale * img.getWidth()), (int)(scale * img.getHeight()),
				BufferedImage.SCALE_SMOOTH));
		
		//initialize display using new image
		imageStack.push(img);
		display = img;
		int width = img.getWidth();
		
		//create array used to store x coordinates of lines
		ArrayList<Integer> linePos = new ArrayList<Integer>();

		//create the image display
		JLabel imgDisplay = new JLabel(new ImageIcon(display));
		imgDisplay.setHorizontalAlignment(JLabel.LEFT);
		imgDisplay.setVerticalAlignment(JLabel.TOP);
		
		//create the status label
		JLabel status = new JLabel("Tubes: 0");
		status.setHorizontalAlignment(JLabel.CENTER);
		
		//create button to make a new row
		JButton newRowButton = new JButton("New Row");
		newRowButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent event) {
        		if (linePos.size() > 1) { //every line must have a minimum of 2 tubes
	        		lineCounts += linePos.size() + ","; //save number of lines
	        		sizeCounts += (GenUtils.max(linePos) - GenUtils.min(linePos) + GenUtils.averageDiff(linePos))
	        				* ((double)actualSize / width) + ","; //save distance counted
	        		
	        		//reset data
	        		linePos.clear();
	        		BufferedImage cpy = ImageUtils.deepCopy(imageStack.peek());
	        		imageStack = new ImageStack();
	        		imageStack.push(cpy);
	        		status.setText("Tubes: 0");
        		}
        	}
        });
		newRowButton.setFocusable(false);
        
		//create continue button
        JButton continueButton = new JButton("");
        //change text if file is the last one to view
        if (count == files.length - 1) {
        	continueButton.setText("Continue");
        } else {
        	continueButton.setText("Next Image");
        }
        continueButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent event) {
        		if (linePos.size() >= 2) { //make sure line contains 2 or more tubes
	        		if (count == files.length - 1) { //if this is the last file
		        		String[] newArgs = new String[3];
		        		newArgs[0] = args[1] + lineCounts + linePos.size(); //add the line count data
		        		
		        		//add the size data
		        		double countedSize = (GenUtils.max(linePos) - GenUtils.min(linePos) + GenUtils.averageDiff(linePos))
		        				* ((double)actualSize / width);
		        		newArgs[1] = args[2] + sizeCounts + countedSize;
		        		
		        		//pass the list of files
		        		newArgs[2] = args[0];
		        		
		        		//go to results ui
		        		frame.setVisible(false);
		        		frame.dispose();
		        		Results.main(newArgs);
	        		} else { //if there are still more images to count
	        			String[] newArgs = new String[4];
	        			newArgs[0] = args[0]; //pass the list of files
	        			newArgs[1] = args[1] + lineCounts + linePos.size() + ";"; //add the line count data
	        			double countedSize = (GenUtils.max(linePos) - GenUtils.min(linePos) + GenUtils.averageDiff(linePos))
		        				* ((double)actualSize / width);
		        		newArgs[2] = args[2] + sizeCounts + countedSize + ";"; //add the size data
		        		newArgs[3] = "" + (count + 1); //move to next file
		        		
		        		//call next image
		        		frame.setVisible(false);
		        		frame.dispose();
		        		ImageViewer.main(newArgs);
	        		}
        		} else {
        			JOptionPane.showMessageDialog(frame, "Every row must contain 2 or more lines.", "Error", JOptionPane.ERROR_MESSAGE);
        		}
        	}
        });
        continueButton.setFocusable(false);

        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(ComponentEvent arg0) {}

            @Override
            public void componentMoved(ComponentEvent arg0) {}

            @Override
            public void componentResized(ComponentEvent arg0) {//set font size relative to window size
                int width = frame.getWidth();
                int height = frame.getHeight();
                FileSelect.defaultFont = new Font(FileSelect.defaultFont.getFontName(), FileSelect.defaultFont.getStyle(), (width + height) / 50);
                
                newRowButton.setFont(FileSelect.defaultFont);
                continueButton.setFont(FileSelect.defaultFont);
                status.setFont(FileSelect.defaultFont);
               
                frame.getContentPane().revalidate();
            }
			@Override
			public void componentShown(ComponentEvent arg0) {
			}
        });
        
        frame.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) { //when mouse is clicked on image
				Point position = imgDisplay.getMousePosition();
				
				if (position != null) { //if mouse is within image
					//draw on image
					BufferedImage cpy = ImageUtils.deepCopy(imageStack.peek());
					Graphics2D g2d = cpy.createGraphics();
					
					if (imageStack.size() == 1) { //if selecting row
						if (isAfm) {
							g2d.setColor(Color.BLUE);
						} else {
							g2d.setColor(Color.ORANGE);
						}
						vertPos = (int)position.getY();
						g2d.fillRect(0, vertPos - 3, cpy.getWidth(), 6); //draw the row
					} else { //if selecting lines
						if (isAfm) {
							g2d.setColor(Color.GREEN);
						} else {
							g2d.setColor(Color.RED);
						}
						g2d.fillRect((int)position.getX() - 3, vertPos - 50, 6, 100); //draw the tube
						linePos.add((int)position.getX()); //save the tube data
					}
					
					//update display
					imageStack.push(cpy);
					status.setText("Tubes: " + (imageStack.size() - 2));
					frame.getContentPane().revalidate();
					g2d.dispose();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {}
        });
        
        frame.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent arg0) {}
			
			@Override
			public void mouseMoved(MouseEvent arg0) {//whenever the mouse is moved within the frame
				Point position = imgDisplay.getMousePosition();
				
				//draw on display
				display = ImageUtils.deepCopy(imageStack.peek());
				Graphics2D g2d = display.createGraphics();
				if (imageStack.size() == 1) {//if selecting row
					try {
						if (isAfm) {
							g2d.setColor(Color.BLUE);
						} else {
							g2d.setColor(Color.ORANGE);
						}
						g2d.fillRect(0, (int)position.getY() - 3, display.getWidth(), 6);//draw row
					} catch (Exception e) {}
				} else {//if selecting tube
					try {
						if (isAfm) {
							g2d.setColor(Color.GREEN);
						} else {
							g2d.setColor(Color.RED);
						}
						g2d.fillRect((int)position.getX() - 3, vertPos - 50, 6, 100);//draw tube
					} catch (Exception e) {};
				}
				
				//update display
				imgDisplay.setIcon(new ImageIcon(display));
				frame.getContentPane().revalidate();
				g2d.dispose();
			}
        });
        
        frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {}

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (imageStack.size() > 1 && arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE) { //if backspace is pressed while selecting tube
					
					//remove last tube and update display
					imageStack.pop();
					display = imageStack.peek();
					if (linePos.size() != 0) {
						linePos.remove(linePos.size() - 1);
					}
					imgDisplay.setIcon(new ImageIcon(display));
					status.setText("Tubes: " + linePos.size());
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {}
        });

        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        frame.getContentPane().add(imgDisplay);
        
        //create grid for bottom of window
        JPanel bottom = new JPanel();
        bottom.setLayout(new GridLayout(1, 3, 0, 0));
        bottom.setPreferredSize(new Dimension(display.getWidth(), 200));
        bottom.add(newRowButton);
        bottom.add(status);
        bottom.add(continueButton);
        
        //add grid
        frame.getContentPane().add(bottom);
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        frame.setSize(display.getWidth(), display.getHeight() + 250);
        
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/icons/icon.png"));
        frame.setIconImage(icon.getImage());
        
        frame.setLocationRelativeTo(null);
        File actualFile = new File(files[count]);
        frame.setTitle(actualFile.getName() + " (" + (count + 1) + "/" + files.length + ")");
        frame.setResizable(false);
        frame.setVisible(true);
	}
	
	public static void main(String[] args) {
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new ImageViewer(args);
            }
        });
    }
}