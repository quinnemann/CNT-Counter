package drivers;

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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import manualDetection.FileSelect;

public class Main {
	private Font defaultFont = new Font("Arial", Font.PLAIN, 0);

    public Main(){
    	JFrame frame = new JFrame();
    	
    	//Create button that opens the automatic counter
        JButton autoButton = new JButton("Automatic Counter");
        autoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				frame.setVisible(false);
				frame.dispose();
				
				AutoSEM.main(new String[0]);
			}
		});
        autoButton.setFocusable(false);
        
        //Create button that opens the manual counter
        JButton manButton = new JButton("Manual Counter");
        manButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				frame.setVisible(false);
				frame.dispose();
				
				FileSelect.main(new String[0]);
			}
		});
        manButton.setFocusable(false);
        
        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(ComponentEvent arg0) {}

            @Override
            public void componentMoved(ComponentEvent arg0) {}

            //change font whenever the window is resized
            @Override
            public void componentResized(ComponentEvent arg0) {
                int width = frame.getWidth();
                int height = frame.getHeight();
                defaultFont = new Font(defaultFont.getFontName(), defaultFont.getStyle(), (width + height) / 40);
                
                autoButton.setFont(defaultFont);
                manButton.setFont(defaultFont);
                
                frame.getContentPane().revalidate();
            }

            @Override
            public void componentShown(ComponentEvent e) {}
        });

        //add components to window
        frame.getContentPane().setLayout(new GridLayout(1, 2, 0, 50));
        frame.getContentPane().add(autoButton);
        frame.getContentPane().add(manButton);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        //set window size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();
        frame.setSize(width / 3, height / 4);
        
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
            	new Main();
            }
        });
    }
}
