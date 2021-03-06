/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking;

import eit.headtracking.wiimote.DualWiimoteHeadTracker;
import eit.headtracking.wiimote.WiimoteHeadTracker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author vegar
 */
public class Test extends JPanel implements WindowListener, ActionListener, KeyListener, ChangeListener, Runnable {

    public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException, IOException {
                File file = new File("headtracking.properties");
        FileInputStream fis = new FileInputStream("headtracking.properties");
        Properties prop = new Properties(System.getProperties());
        prop.load(fis);
        System.setProperties(prop);
        fis.close();
        HeadTracker head = (HeadTracker) Class.forName(System.getProperty("eit.headtracking.headtracker")).newInstance();
        Test test = new Test(head);
        while(true) {
            test.repaint();
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    HeadTracker head;
    BufferedImage image;
    boolean isRendering = false;
    private JFrame f;
    private JPanel buttonPanel = new JPanel();
    private JSlider xSlider = new JSlider(JSlider.VERTICAL, -1000, 1000, 0);
    private JSlider ySlider = new JSlider(JSlider.VERTICAL, -1000, 1000, 0);
    private JSlider zSlider = new JSlider(JSlider.VERTICAL, -1000, 1000, 0);
    private JButton calibrateButton = new JButton("Calibrate");

    public Test(HeadTracker h) {
        this.head = h;
        f = new JFrame();
        f.setContentPane(this);
        f.addWindowListener(this);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1024, 480);
        f.setVisible(true);

        this.setLayout(new BorderLayout());
        this.buttonPanel.setLayout(new GridLayout());
        xSlider.addChangeListener(this);
        ySlider.addChangeListener(this);
        zSlider.addChangeListener(this);
        calibrateButton.addActionListener(this);
        this.buttonPanel.add(calibrateButton);
        this.buttonPanel.add(xSlider);
        this.buttonPanel.add(ySlider);
        this.buttonPanel.add(zSlider);

        this.add(buttonPanel, BorderLayout.EAST);
        
        System.out.println("Starting head tracker");
        head.start();
        
    }

    public void paintComponent(Graphics g) {
        if (isRendering) {
            return;
        }
        isRendering = true;
        g.setColor(Color.BLACK);
        g.clearRect(0, 0, 1024, 768);
        //g.drawImage(image, 0, 0, this);
        g.setColor(Color.BLUE);
        g.setFont(new Font("TRUETYPE", Font.PLAIN, 32));
        g.drawString("Pos(xyz): (" + head.getHeadX() + ", " + head.getHeadY() + ", " + head.getHeadZ() + ")", 0, 100);
        g.drawString("Rot(xyz): (" + head.getRotX() + ", " + head.getRotY() + ", " + head.getRotZ() + ")", 0, 200);
//        g.drawString("Left angle: " + head.leftAngle() * 180.0 / Math.PI, 0, 300);
//        g.drawString("Offset left: " + head.leftOffsetRadians * 180.0 / Math.PI, 0, 400);
//         g.drawString("Right angle: " + head.rightAngle() * 180.0 / Math.PI, 0, 500);
//        g.drawString("Offset right: " + head.rightOffsetRadians * 180.0 / Math.PI, 0, 600);

        g.fillOval((int) (head.getHeadX() * (double) getWidth()), (int) (head.getHeadY() * (double) getHeight()), 5, 5);
        isRendering = false;
    }

    public void keyTyped(KeyEvent arg0) {
        System.out.println("Faen");
    //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void keyPressed(KeyEvent arg0) {
        System.out.println("KEEEEY");
        //throw new UnsupportedOperationException("Not supported yet.");
        if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
            head.calibrate();
        }
    }

    public void keyReleased(KeyEvent arg0) {
        System.out.println("Helvete");
    }

    public void stateChanged(ChangeEvent arg0) {
        if (head instanceof DualWiimoteHeadTracker) {
            DualWiimoteHeadTracker h = (DualWiimoteHeadTracker) head;
            if (arg0.getSource() == zSlider) {
                h.headZ = zSlider.getValue() / 50.0;
            } else if (arg0.getSource() == xSlider) {
                h.headX = xSlider.getValue() / 100.0;
            } else if (arg0.getSource() == ySlider) {
                h.headY = ySlider.getValue() / 100.0;
            }
        }


    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
            repaint();
        }
    }

    public void actionPerformed(ActionEvent arg0) {
        head.calibrate();
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        System.out.println("Stopping head tracker");
        head.stop();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
}
