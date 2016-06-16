/*
 * Created on 28.05.2016
 *
 */
package de.hdm.animation;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sun.awt.shell.ShellFolder;

public class DirectoryAnimationPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    int delay = 100;
    int distanceX = 100;
    int distanceY = 100;

    JLabel[] fileLabels = null;
    Point[] positions = null;
    Point source = new Point(-100, -100);

    public void setDirectory(String dirName) {

        File dir = new File(dirName);
        File[] files = null;
        
        if (dir.isDirectory()) {
            files = dir.listFiles();            
            generateFileLabels(files);
            generatePositions(files);
        }
        
        int length = (int)Math.round(Math.sqrt(files.length)) + 1;
                //10;
        setSize(100*length, 100*length);
        setPreferredSize(getSize());
    }

    public void runAnimation() {
        try {
            spreadDir();
            Thread.sleep(2000);
            shrinkDir();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public DirectoryAnimationPanel() {

        int length = 5;
        setSize(100*length, 100*length);
        setPreferredSize(getSize());
        setLocation(0, 0);
        setLayout(null);
        setBackground(new Color(100, 100, 100, 0));
        setVisible(true);
    }

    public void spreadDir() {
        try {
            for (int i = 0; i < fileLabels.length; i++) {
                new FileAnimation(fileLabels[i], positions[i]).start();
                Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void shrinkDir() {
        try {
            for (JLabel fileLabel : fileLabels) {
                new FileAnimation(fileLabel, source).start();
                Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void generateFileLabels(File[] files) {
        fileLabels = new JLabel[files.length];
        for (int i = 0; i < files.length; i++) {
            fileLabels[i] = generateFileLabel(files[i]);
        }
    }

    private JLabel generateFileLabel(File file) {
        ShellFolder sf = null;
        try {
            sf = ShellFolder.getShellFolder(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (sf.getIcon(true) == null)
            return null;
        ImageIcon imageIcon = new ImageIcon(sf.getIcon(true), sf.getFolderType());

        JLabel label = new JLabel(file.getName());
        label.setIcon(imageIcon);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setLocation(source);
        label.setSize(imageIcon.getIconWidth() + 20, imageIcon.getIconHeight() + 20);

        add(label);

        return label;
    }

    private void generatePositions(File[] files) {
        positions = new Point[files.length];
        int posX = 0;
        int posY = 0;
        for (int i = 0; i < files.length; i++) {
            positions[i] = new Point(posX, posY);
            posX += distanceX;
            if (posX >= getWidth()) {
                posX = 0;
                posY += distanceY;
            }
        }
    }

    private class FileAnimation extends Thread {

        JLabel label = null;
        Point start, goal = null;

        public FileAnimation(JLabel label, Point goal) {
            this.label = label;
            this.goal = goal;

            start = label.getLocation();

        }

        public void run() {
            double incrX = (goal.x - start.x) / 100.0;
            double incrY = (goal.y - start.y) / 100.0;
            for (int i = 0; i <= 100; i++) {
                label.setLocation(start.x + (int) (i * incrX), start.y + (int) (i * incrY));
                getParent().getParent().repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getParent().getParent().repaint();
        }

    }
}
