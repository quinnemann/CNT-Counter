package drivers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import utils.AFMUtils;
import utils.GenUtils;
import utils.ImageUtils;
import utils.TubeDetector;

public class AFMUI {
	private File file = null;
	private JLabel fileLabel = new JLabel("No File Selected");
	private Font defaultFont = new Font("Arial", Font.PLAIN, 0);
	private JLabel errorLabel = new JLabel("");

    public AFMUI(){
    	JFrame frame = new JFrame();
    	
        JButton fileButton = new JButton("Select File");
        fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fc = new JFileChooser("C:\\Users\\quinn\\Documents\\git\\CNT Counter\\images");
				fc.setDialogTitle("Open Image");
				fc.setPreferredSize(new Dimension((int)(frame.getWidth() * 1.5), frame.getHeight()));
				setFileChooserFont(fc.getComponents());
				
				fc.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("*.jpg, *.tif", "jpg", "tif");
				fc.addChoosableFileFilter(filter);
				
				int returnValue = fc.showOpenDialog(frame);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					fileLabel.setText("<html><p><center>" + file.getName() + "</center></p></html>");
				}
			}
		});
        
        fileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
      /*  JLabel scaleLabel = new JLabel("Image Scale (nm):");
        scaleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextField scaleInput = new JTextField("100.0");
        scaleInput.setHorizontalAlignment(SwingConstants.CENTER);*/
        
        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (file == null) {
					errorLabel.setForeground(Color.RED);
					errorLabel.setText("No File Selected");
				} else {
					BufferedImage img = ImageUtils.readImage(file.getAbsolutePath());
					img = AFMUtils.blackAndWhite(img);
					double size = AFMUtils.actualSize(img);
					img = AFMUtils.crop(img);
					String imageName = file.getName();
					imageName = "images/" + imageName.substring(0, imageName.length() - 4);
					try {
						ImageIO.write(img, "jpg", new File(imageName + "out1.jpg"));
					} catch (IOException e) {}
					img = ImageUtils.medianFilter(img);
					img = AFMUtils.sharpen(img);
					img = ImageUtils.contrastByRow(img);
					try {
						ImageIO.write(img, "jpg", new File(imageName + "out2.jpg"));
					} catch (IOException e) {}
					double density = TubeDetector.detectTubes(img) / size;
					errorLabel.setForeground(new Color(0, 153, 0));
					errorLabel.setText("<html>Density: " + GenUtils.roundThousandths(density) + " &micro;m<sup>-1</sup></html>");
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
                /*scaleLabel.setFont(defaultFont);
                scaleInput.setFont(defaultFont);*/
                submit.setFont(defaultFont);
                errorLabel.setFont(defaultFont);
                
                frame.getContentPane().revalidate();
            }

            @Override
            public void componentShown(ComponentEvent e) {

            }
        });

        frame.getContentPane().setLayout(new GridLayout(2, 2, 0, 50));
        frame.getContentPane().add(fileButton);
        frame.getContentPane().add(fileLabel);
        /*frame.getContentPane().add(scaleLabel);
        frame.getContentPane().add(scaleInput);*/
        frame.getContentPane().add(submit);
        frame.getContentPane().add(errorLabel);

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
            	new AFMUI();
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
