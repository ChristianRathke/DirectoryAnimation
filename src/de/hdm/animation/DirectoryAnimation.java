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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sun.awt.shell.ShellFolder;

public class DirectoryAnimation {

    JFrame display = new JFrame("File Animation");
    JPanel panel = new JPanel();

    int delay = 100;
    int distanceX = 100;
    int distanceY = 100;

    File[] files = null;
    JLabel[] fileLabels = null;
    Point[] positions = null;
    Point source = new Point(-100, -100);

    public DirectoryAnimation(String dirName) {

        File dir = new File(dirName);

        if (dir.isDirectory()) {
            initializeDisplay(dirName);
            files = dir.listFiles();
            generateFileLabels();
            generatePositions();
            runAnimation();
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            new DirectoryAnimation(args[0]);
        } else {
            new DirectoryAnimation(".\\");
        }
    }

    private void runAnimation() {
        try {
            spreadDir();
            Thread.sleep(2000);
            shrinkDir();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeDisplay(String dirName) {

        display.setTitle(dirName);
        display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        display.setSize(900, 600);
        display.setLocation(100, 100);
        display.setLayout(null);
        display.setVisible(true);

        panel.setSize(display.getSize());
        panel.setLocation(0, 0);
        panel.setLayout(null);
        panel.setBackground(new Color(100, 100, 100, 0));

        display.add(panel);
    }

    private void spreadDir() throws InterruptedException {
        for (int i = 0; i < fileLabels.length; i++) {
            new FileAnimation(fileLabels[i], positions[i]).start();
            Thread.sleep(delay);
        }
    }

    private void shrinkDir() throws InterruptedException {
        for (JLabel fileLabel : fileLabels) {
            new FileAnimation(fileLabel, source).start();
            Thread.sleep(delay);
        }
    }

    private void generateFileLabels() {
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

        panel.add(label);

        return label;
    }

    private void generatePositions() {
        positions = new Point[files.length];
        int posX = 0;
        int posY = 0;
        for (int i = 0; i < files.length; i++) {
            positions[i] = new Point(posX, posY);
            posX += distanceX;
            if (posX >= panel.getWidth()) {
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
                panel.repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            display.repaint();
        }

    }
}
