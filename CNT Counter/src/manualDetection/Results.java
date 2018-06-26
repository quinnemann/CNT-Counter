package manualDetection;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import utils.GenUtils;

public class Results {
	 public Results(int tubes, double size){
		 	double density = GenUtils.roundThousandths(tubes / size);
		 
	    	JFrame frame = new JFrame();
	    	
	        JLabel infoLabel = new JLabel("<html><center>" + tubes + " Tubes</center>"
	        		+ "<center>Image Size: " + GenUtils.roundThousandths(size) + " &micro;m</center></html>");
	        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        
	        JLabel densityLabel = new JLabel("<html>Density: " + density + " &micro;m<sup>-1</sup></html>");
	        densityLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        
	        JButton restart = new JButton("New Image");
	        restart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					frame.setVisible(false);
					frame.dispose();
					FileSelect.main(new String[0]);
				}
			});
	        restart.setFocusable(false);
	        
	        JButton quit = new JButton("Quit");
	        quit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					System.exit(0);
				}
			});
	        quit.setFocusable(false);

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
	                
	                infoLabel.setFont(FileSelect.defaultFont);
	                densityLabel.setFont(FileSelect.defaultFont);
	                restart.setFont(FileSelect.defaultFont);
	                quit.setFont(FileSelect.defaultFont);
	                
	                frame.getContentPane().revalidate();
	            }

	            @Override
	            public void componentShown(ComponentEvent e) {}
	        });

	        frame.getContentPane().setLayout(new GridLayout(2, 2, 50, 50));
	        frame.getContentPane().add(infoLabel);
	        frame.getContentPane().add(densityLabel);
	        frame.getContentPane().add(restart);
	        frame.getContentPane().add(quit);

	        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	        
	        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	        int width = (int)screenSize.getWidth();
	        int height = (int)screenSize.getHeight();
	        frame.setSize(width / 4, height / 3);
	        
	        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/icons/icon.png"));
	        frame.setIconImage(icon.getImage());
	        
	        frame.setLocationRelativeTo(null);
	        frame.setTitle("CNT Counter");
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
	            	new Results(Integer.parseInt(args[0]), Double.parseDouble(args[1]));
	            }
	        });
	    }
}
