package drivers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import utils.CannyEdgeDetector;
import utils.ImageUtils;
import utils.TubeDetector;

public class UI{
	
	private File file = null;
	private JLabel fileLabel = new JLabel("No File Selected");
	private Font defaultFont = new Font("Arial", Font.PLAIN, 0);
	private JLabel errorLabel = new JLabel("");
	private final JTextField noiseInput = new JTextField("0", 20);

    public UI(){
    	JFrame frame = new JFrame();
    	
        JButton fileButton = new JButton("Select File");
        fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fc = new JFileChooser("C:\\Users\\quinn\\Documents\\git\\CNT Counter\\images");
				fc.setDialogTitle("Open Image");
				fc.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
				setFileChooserFont(fc.getComponents());
				
				fc.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("*.JPG, *.jpg, *.JPEG, *.jpeg", "JPG", "jpg", "JPEG", "jpeg");
				fc.addChoosableFileFilter(filter);
				
				int returnValue = fc.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					fileLabel.setText(file.getName());
				}
			}
		});
        
        fileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel noiseLabel = new JLabel("Noise reduction strength:");
        noiseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        noiseInput.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int strength = -1;
				try {
					strength = Integer.parseInt(noiseInput.getText());
				}catch (Exception e) {}
				
				if (file == null) {
					errorLabel.setForeground(Color.RED);
					errorLabel.setText("No File Selected");
				} else if (strength < 0) {
					errorLabel.setForeground(Color.RED);
					errorLabel.setText("<html><center>Strength must be a</center><center>nonnegative integer</center></html>");
				} else {
					errorLabel.setForeground(Color.BLACK);
					errorLabel.setText("Working...");
					int tubes = TubeDetector.density(file, strength);
					errorLabel.setText("Density: " + tubes);
				}
			}
		});
        
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        frame.addComponentListener(new ComponentListener() {

            @Override
            public void componentHidden(ComponentEvent arg0) {
            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
            }

            @Override
            public void componentResized(ComponentEvent arg0) {
                int width = frame.getWidth();
                int height = frame.getHeight();
                defaultFont = new Font(defaultFont.getFontName(), defaultFont.getStyle(), (width + height) / 50);
                
                fileButton.setFont(defaultFont);
                fileLabel.setFont(defaultFont);
                noiseLabel.setFont(defaultFont);
                noiseInput.setFont(defaultFont);
                submit.setFont(defaultFont);
                errorLabel.setFont(defaultFont);
                
                frame.getContentPane().revalidate();
            }

            @Override
            public void componentShown(ComponentEvent e) {

            }
        });

        frame.getContentPane().setLayout(new GridLayout(3, 2, 0, 50));
        frame.getContentPane().add(fileButton);
        frame.getContentPane().add(fileLabel);
        frame.getContentPane().add(noiseLabel);
        frame.getContentPane().add(noiseInput);
        frame.getContentPane().add(submit);
        frame.getContentPane().add(errorLabel);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();
        frame.setSize(width / 4, height / 3);
        
        ImageIcon icon = new ImageIcon("images/icon.png");
        frame.setIconImage(icon.getImage());
        
        frame.setLocationRelativeTo(null);
        frame.setTitle("CNT Counter");
        frame.setVisible(true);
    }

    public static void main(String[] args) {
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new UI();
            }
        });
    }
    
    private void setFileChooserFont(Component[] comp)
    {  
      for(int x = 0; x < comp.length; x++)  
      {  
        if(comp[x] instanceof Container) setFileChooserFont(((Container)comp[x]).getComponents());  
        try{comp[x].setFont(defaultFont);}  
        catch(Exception e){}//do nothing  
      }  
    }
}
