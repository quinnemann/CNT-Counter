package manualDetection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
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
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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
	private double ratio;
	private double scale;
	
	public ImageViewer(String file, boolean isAfm) {
		JFrame frame = new JFrame();
		
		imageStack = new ImageStack();
		
		BufferedImage img = ImageUtils.readImage(file);
		
		if (isAfm) {
			actualSize = AFMUtils.actualSize(img);
			img = AFMUtils.crop(img);
			img = img.getSubimage(0, 0, img.getWidth(), img.getHeight());
		} else {
			actualSize = ImageUtils.actualSize(img);
			img = ImageUtils.cutBottom(img);
			img = ImageUtils.averageExposure(img);
			img = ImageUtils.contrast(img);
			//img = ImageUtils.contrastByRow(img);
		}
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		scale = (screenSize.getHeight() / img.getHeight()) - .5;
		
		img = ImageUtils.toBufferedImage(img.getScaledInstance((int)(scale * img.getWidth()), (int)(scale * img.getHeight()),
				BufferedImage.SCALE_SMOOTH));
		
		imageStack.push(img);
		display = img;
		ratio = (double)img.getWidth() / img.getHeight();
		int width = img.getWidth();
		
		ArrayList<Integer> linePos = new ArrayList<Integer>();

		JLabel imgDisplay = new JLabel(new ImageIcon(display));
		
		JLabel status = new JLabel("Tubes: 0");
        
        JButton continueButton = new JButton("Calculate");
        continueButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent event) {
        		String[] args = new String[2];
        		args[0] = "" + (linePos.size());
        		double countedSize = (GenUtils.max(linePos) - GenUtils.min(linePos) + GenUtils.averageDiff(linePos))
        				* ((double)actualSize / width);
        		args[1] = "" + countedSize;
        		frame.setVisible(false);
        		frame.dispose();
        		Results.main(args);
        	}
        });
        continueButton.setFocusable(false);

        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(ComponentEvent arg0) {}

            @Override
            public void componentMoved(ComponentEvent arg0) {}

            @Override
            public void componentResized(ComponentEvent arg0) {
                int width = frame.getWidth();
                int height = frame.getHeight();
                FileSelect.defaultFont = new Font(FileSelect.defaultFont.getFontName(), FileSelect.defaultFont.getStyle(), (width + height) / 50);
                
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
			public void mousePressed(MouseEvent e) {
				Point position = imgDisplay.getMousePosition();
				BufferedImage cpy = ImageUtils.deepCopy(imageStack.peek());
				Graphics2D g2d = cpy.createGraphics();
				
				if (imageStack.size() == 1) {
					if (isAfm) {
						g2d.setColor(Color.BLUE);
					} else {
						g2d.setColor(Color.ORANGE);
					}
					vertPos = (int)position.getY();
					g2d.fillRect(0, vertPos - 3, cpy.getWidth(), 6);
				} else {
					if (isAfm) {
						g2d.setColor(Color.GREEN);
					} else {
						g2d.setColor(Color.RED);
					}
					g2d.fillRect((int)position.getX() - 3, vertPos - 50, 6, 100);
					linePos.add((int)position.getX());
					//printLinePos(linePos);
				}
				imageStack.push(cpy);
				
				g2d.dispose();
				
				status.setText("Tubes: " + (imageStack.size() - 2));
				
				frame.getContentPane().revalidate();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {}
        });
        
        frame.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent arg0) {}
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
				Point position = imgDisplay.getMousePosition();
				display = ImageUtils.deepCopy(imageStack.peek());
				Graphics2D g2d = display.createGraphics();
				if (imageStack.size() == 1) {
					try {
						if (isAfm) {
							g2d.setColor(Color.BLUE);
						} else {
							g2d.setColor(Color.ORANGE);
						}
						g2d.fillRect(0, (int)position.getY() - 3, display.getWidth(), 6);
					} catch (Exception e) {}
				} else {
					try {
						if (isAfm) {
							g2d.setColor(Color.GREEN);
						} else {
							g2d.setColor(Color.RED);
						}
						g2d.fillRect((int)position.getX() - 3, vertPos - 50, 6, 100);
					} catch (Exception e) {};
				}
				imgDisplay.setIcon(new ImageIcon(display));
				
				g2d.dispose();
				frame.getContentPane().revalidate();
			}
        });
        
        frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {}

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (imageStack.size() > 1 && arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					imageStack.pop();
					display = imageStack.peek();
					if (linePos.size() != 0) {
						linePos.remove(linePos.size() - 1);
					}
					//printLinePos(linePos);
					imgDisplay.setIcon(new ImageIcon(display));
				}
				
				/*if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
					System.out.println(imageStack.size());
				}*/
			}

			@Override
			public void keyTyped(KeyEvent arg0) {}
        });

        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(imgDisplay);
        GridLayout bottom = new GridLayout(2, 1, 0, 0);
        //frame.getContentPane().add(status);
        frame.getContentPane().add(continueButton);
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        frame.setSize(display.getWidth(), display.getHeight() + 200);
        
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/icons/icon.png"));
        frame.setIconImage(icon.getImage());
        
        frame.setLocationRelativeTo(null);
        frame.setTitle("CNT Counter");
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
            	new ImageViewer(args[0], args[1].equals("true"));
            }
        });
    }
	
	public void printLinePos(ArrayList<Integer> linePos) {
		for (int i = 0; i < linePos.size(); i++) {
			System.out.print(linePos.get(i) + ", ");
		}
		System.out.println();
	}
}