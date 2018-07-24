package manualDetection;

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
import java.io.File;

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

public class FileSelect {
	private File[] files = null;
	private JLabel fileLabel = new JLabel("No Files Selected");
	public static Font defaultFont = new Font("Arial", Font.PLAIN, 0);
	private JLabel errorLabel = new JLabel("");

    public FileSelect(){
    	JFrame frame = new JFrame();
    	
    	//create file select button
        JButton fileButton = new JButton("Select Files");
        fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//open file select
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Open Images");
				fc.setPreferredSize(new Dimension((int)(frame.getWidth() * 1.5), frame.getHeight()));
				setFileChooserFont(fc.getComponents());
				
				//select multiple jpg and tiff files
				fc.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG and TIFF images", "jpg", "JPG", "jpeg", "JPEG", "tif", "TIF"
						, "tiff", "TIFF");
				fc.addChoosableFileFilter(filter);
				fc.setMultiSelectionEnabled(true);
				
				int returnValue = fc.showOpenDialog(frame);

				//get files and update label
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					files = fc.getSelectedFiles();
					if (files.length == 1) {
						fileLabel.setText("<html><center><p>" + files.length + " File Selected</p></center></html>");
					} else {
						fileLabel.setText("<html><center><p>" + files.length + " Files Selected</p></center></html>");
					}
					errorLabel.setText("");
				}
			}
		});
        fileButton.setFocusable(false);
        
        fileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        //create continue button
        JButton continueButton = new JButton("Continue");
        continueButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent event) {
        		if (files == null) { //if files are not selectd
        			errorLabel.setForeground(Color.red);
        			errorLabel.setText("No Files Selected");
        		} else {
        			String[] args = new String[4];
        			
        			//pass files as text separated by "?"
        			args[0] = "";
        			for (int i = 0 ; i < files.length; i++) {
        				if (i == files.length - 1) {
        					args[0] += files[i].getAbsolutePath();
        				} else {
        					args[0] += files[i].getAbsolutePath() + "?";
        				}
        			}
        			
        			//blank data for first iteration of the image viewer
        			args[1] = "";
        			args[2] = "";
        			args[3] = "0";
        			
        			//open image viewer ui
        			frame.setVisible(false);
        			frame.dispose();
        			ImageViewer.main(args);
        		}
        	}
        });
        continueButton.setFocusable(false);
        
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        frame.addComponentListener(new ComponentListener() {
        	@Override
            public void componentHidden(ComponentEvent arg0) {}
        	
        	@Override
            public void componentMoved(ComponentEvent arg0) {}

            @Override
            public void componentResized(ComponentEvent arg0) { //change font if window is resized
                int width = frame.getWidth();
                int height = frame.getHeight();
                defaultFont = new Font(defaultFont.getFontName(), defaultFont.getStyle(), (width + height) / 50);
                
                fileButton.setFont(defaultFont);
                fileLabel.setFont(defaultFont);
                continueButton.setFont(defaultFont);
                errorLabel.setFont(defaultFont);
                
                frame.getContentPane().revalidate();
            }

            @Override
            public void componentShown(ComponentEvent e) {}
        });

        //add components to window
        frame.getContentPane().setLayout(new GridLayout(2, 2, 0, 50));
        frame.getContentPane().add(fileButton);
        frame.getContentPane().add(fileLabel);
        frame.getContentPane().add(continueButton);
        frame.getContentPane().add(errorLabel);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();
        frame.setSize(width / 3, height / 2);
        
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/icons/icon.png"));
        frame.setIconImage(icon.getImage());
        
        frame.setLocationRelativeTo(null);
        frame.setTitle("Manual Counter");
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
            	new FileSelect();
            }
        });
    }
    
    //makes font readable on larger screens
    public static void setFileChooserFont(Component[] comp)
    {  
      for(int x = 0; x < comp.length; x++)  
      {  
        if(comp[x] instanceof Container) setFileChooserFont(((Container)comp[x]).getComponents());  
        try{comp[x].setFont(defaultFont);}  
        catch(Exception e){}//do nothing  
      }  
    }
}
