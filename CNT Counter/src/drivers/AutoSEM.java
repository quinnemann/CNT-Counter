package drivers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import utils.AFMUtils;
import utils.GenUtils;
import utils.Grapher;
import utils.ImageUtils;

public class AutoSEM{
	
	private File[] files = null;
	private File saveFile = null;
	private JLabel fileLabel = new JLabel("No Files Selected");
	private static Font defaultFont = new Font("Arial", Font.PLAIN, 0);
	private JLabel errorLabel = new JLabel("");

    public AutoSEM(){
    	JFrame frame = new JFrame();
    	
        JButton fileButton = new JButton("Select Files");
        fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fc = new JFileChooser("C:\\Users\\quinn\\Documents\\git\\CNT Counter\\images");
				fc.setDialogTitle("Open Images");
				fc.setPreferredSize(new Dimension((int)(frame.getWidth() * 1.5), frame.getHeight()));
				setFileChooserFont(fc.getComponents());
				
				fc.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG and TIFF images", "jpg", "JPG", "jpeg", "JPEG", "tif", "TIF"
						, "tiff", "TIFF");
				fc.addChoosableFileFilter(filter);
				fc.setMultiSelectionEnabled(true);
				
				int returnValue = fc.showOpenDialog(frame);

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
        
        JCheckBox saveCountImages = new JCheckBox("Save Marked Images");
        saveCountImages.setFocusable(false);
        saveCountImages.setHorizontalAlignment(SwingConstants.CENTER);
        
        fileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton submit = new JButton("Save Data");
        submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {	
				if (files == null) {
					errorLabel.setForeground(Color.RED);
					errorLabel.setText("No Files Selected");
				} else {
					JFileChooser fc = new JFileChooser(files[0]);
					fc.setDialogTitle("Select Save File");
					fc.setPreferredSize(new Dimension((int)(frame.getWidth() * 1.5), frame.getHeight()));
					setFileChooserFont(fc.getComponents());
					
					fc.setAcceptAllFileFilterUsed(false);
					FileNameExtensionFilter filter = new FileNameExtensionFilter("*.csv", "csv");
					fc.addChoosableFileFilter(filter);
					fc.setSelectedFile(new File("output.csv"));
					
					int returnValue = fc.showSaveDialog(frame);

					if (returnValue == JFileChooser.APPROVE_OPTION) {
						saveFile = fc.getSelectedFile();
					}
					
					PrintWriter pw = null;
					try {
						pw = new PrintWriter(saveFile);
					} catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(frame, "ERROR: File is being used by another program!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					
					if (pw != null) {
						double[][] densityData = new double[files.length][3];
						
						for (int f = 0; f < files.length; f++) {
							BufferedImage img = ImageUtils.readImage(files[f].getAbsolutePath());
							
							img = AFMUtils.blackAndWhite(img);
							
							double actualSize = ImageUtils.actualSize(img);
							img = ImageUtils.cutBottom(img);
							
							final int SCAN_SIZE = 50;
							
							int scanHeight = img.getHeight() / 4;
							double[] vals1 = new double[img.getWidth()];
							for (int i = 0; i < vals1.length; i++) {
								vals1[i] += GenUtils.average(Grapher.getAngledPixels(img, i, scanHeight, SCAN_SIZE, 90));
							}
							
							scanHeight *= 2;
							double[] vals2 = new double[img.getWidth()];
							for (int i = 0; i < vals2.length; i++) {
								vals2[i] += GenUtils.average(Grapher.getAngledPixels(img, i, scanHeight, SCAN_SIZE, 90));
							}
							
							scanHeight *= 1.5;
							double[] vals3 = new double[img.getWidth()];
							for (int i = 0; i < vals2.length; i++) {
								vals3[i] += GenUtils.average(Grapher.getAngledPixels(img, i, scanHeight, SCAN_SIZE, 90));
							}
							
							vals1 = Grapher.contrastVals(vals1);
							vals2 = Grapher.contrastVals(vals2);
							vals3 = Grapher.contrastVals(vals3);
							
							for (int i = 0; i < 15; i++) {
								vals1 = Grapher.avgVals(vals1);
								vals2 = Grapher.avgVals(vals2);
								vals3 = Grapher.avgVals(vals3);
							}
							
							BufferedImage graph = Grapher.drawGraph(vals2);
							
							BufferedImage combo = new BufferedImage(img.getWidth(), img.getHeight() + 256, BufferedImage.TYPE_INT_RGB);
							Graphics2D g2d = combo.createGraphics();
							g2d.drawImage(img, 0, 0, null);
							g2d.drawImage(graph, 0, img.getHeight(), null);
							
							g2d.setColor(Color.GREEN);
							g2d.fillRect(0, (img.getHeight() / 4) - 1, img.getWidth(), 3);
							g2d.fillRect(0, (img.getHeight() / 2) - 1, img.getWidth(), 3);
							g2d.fillRect(0, ((img.getHeight() / 4) * 3) - 1, img.getWidth(), 3);
							
							final int MIN_PEAK = 3;
							
							combo = Grapher.drawPeaks(vals1, MIN_PEAK, combo, img.getHeight() / 4, false);
							combo = Grapher.drawPeaks(vals2, MIN_PEAK, combo, img.getHeight() / 2, true);
							combo = Grapher.drawPeaks(vals3, MIN_PEAK, combo, (img.getHeight() / 4) * 3, false);
							
							double density1 = GenUtils.roundThousandths(Grapher.numPeaks(vals1, MIN_PEAK) / actualSize);
							double density2 = GenUtils.roundThousandths(Grapher.numPeaks(vals2, MIN_PEAK) / actualSize);
							double density3 = GenUtils.roundThousandths(Grapher.numPeaks(vals3, MIN_PEAK) / actualSize);
							
							densityData[f][0] = density1;
							densityData[f][1] = density2;
							densityData[f][2] = density3;
							
							String fileName = files[f].getName();
							fileName = fileName.substring(0, fileName.length() - 4);
							if (saveCountImages.isSelected()) {
								File folder = new File(saveFile.getParent() + "/counts");
								if (!folder.exists()) {
									folder.mkdirs();
								}
								try {ImageIO.write(combo, "jpg", new File(folder.getAbsolutePath() + "/" + fileName + "_count.jpg"));} catch (IOException e) {}
							}
						}
						pw.println("Filename,Top Density,Middle Density,Bottom Density,Average");
						for (int i = 0; i < densityData.length; i++) {
							pw.println(files[i].getName() + "," + densityData[i][0] + "," + densityData[i][1] + "," + densityData[i][2] + ","
									+ GenUtils.roundThousandths((densityData[i][0] + densityData[i][1] + densityData[i][2]) / 3));
						}
						
						errorLabel.setForeground(new Color(0, 153, 0));
						errorLabel.setText("Saved!");
						
						pw.close();
					} else {
						saveFile = null;
					}
				}
			}
		});
        submit.setFocusable(false);
        
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
                saveCountImages.setFont(defaultFont);
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
        frame.getContentPane().add(saveCountImages);
        frame.getContentPane().add(new JLabel());
        frame.getContentPane().add(submit);
        frame.getContentPane().add(errorLabel);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();
        frame.setSize(width / 3, height / 2);
        
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
            	new AutoSEM();
            }
        });
    }
    
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
