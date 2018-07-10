package manualDetection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import utils.GenUtils;

public class Results {
	
	JLabel densityLabel = new JLabel("<html><p><center>Please select a save file</center></p></html>");
	private File file = null;
	
	 public Results(String[] args){
		  JFrame frame = new JFrame();
		  
	        JButton saveButton = new JButton("Select Save File");
	        saveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					JFileChooser fc = new JFileChooser("C:\\Users\\quinn\\Documents\\git\\CNT Counter\\images");
					fc.setDialogTitle("Select Save File");
					fc.setPreferredSize(new Dimension((int)(frame.getWidth() * 1.5), frame.getHeight()));
					FileSelect.setFileChooserFont(fc.getComponents());
					
					fc.setAcceptAllFileFilterUsed(false);
					FileNameExtensionFilter filter = new FileNameExtensionFilter("*.csv", "csv");
					fc.addChoosableFileFilter(filter);
					fc.setSelectedFile(new File("output.csv"));
					
					int returnValue = fc.showSaveDialog(frame);

					if (returnValue == JFileChooser.APPROVE_OPTION) {
						file = fc.getSelectedFile();
					}
					
					PrintWriter pw = null;
					try {
						pw = new PrintWriter(file);
					} catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(frame, "ERROR: File is being used by another program!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					
					if (pw != null) {
						String files[] = args[2].split("\\?");
						String tubesByFile[] = args[0].split(";");
						String distancesByFile[] = args[1].split(";");
						
						pw.println("Filename,Row,Tubes,Distance,Density");
						
						for (int i = 0; i < files.length; i++) {
							File file = new File(files[i]);
							String tubes[] = tubesByFile[i].split(",");
							String distances[] = distancesByFile[i].split(",");
							for (int j = 0; j < tubes.length; j++) {
								pw.println(file.getName() + "," + (j + 1) + "," + tubes[j] + "," + distances[j] + "," +
										GenUtils.roundThousandths(Integer.parseInt(tubes[j]) / Double.parseDouble(distances[j])));
							}
						}
						
						densityLabel.setForeground(new Color(18, 188, 0));
						densityLabel.setText("Data Saved!");
						pw.close();
					} else {
						file = null;
					}
				}
			});
	        saveButton.setFocusable(false);
	        
	        densityLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        
	        JButton restart = new JButton("Restart");
	        restart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					if (file == null) {
						int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to restart without saving your data?",
								"Confirm Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (option == JOptionPane.YES_OPTION) {
							frame.setVisible(false);
							frame.dispose();
							FileSelect.main(new String[0]);
						}
					} else {
						frame.setVisible(false);
						frame.dispose();
						FileSelect.main(new String[0]);
					}
				}
			});
	        restart.setFocusable(false);
	        
	        JButton quit = new JButton("Quit");
	        quit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					if (file == null) {
						int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit without saving your data?",
								"Confirm Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (option == JOptionPane.YES_OPTION) {
							System.exit(0);
						}
					} else {
						System.exit(0);
					}
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
	                FileSelect.defaultFont = new Font(FileSelect.defaultFont.getFontName(),
	                		FileSelect.defaultFont.getStyle(), (width + height) / 50);
	                
	                saveButton.setFont(FileSelect.defaultFont);
	                densityLabel.setFont(FileSelect.defaultFont);
	                restart.setFont(FileSelect.defaultFont);
	                quit.setFont(FileSelect.defaultFont);
	                
	                frame.getContentPane().revalidate();
	            }

	            @Override
	            public void componentShown(ComponentEvent e) {}
	        });

	        frame.getContentPane().setLayout(new GridLayout(2, 2, 50, 50));
	        frame.getContentPane().add(saveButton);
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
	            	new Results(args);
	            }
	        });
	    }
}
