/*
 * Created on 28.05.2016
 *
 */
package de.hdm.animation;

import java.awt.Color;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

public class DirectoryAnimationPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    int delay = 10;
    int distanceX = 80;
    int distanceY = 80;

    File directory = null;
    JFrame display = null;
    Vector<FileLabel> fileLabels = new Vector<FileLabel>();
    Point source = null;

    private FileLabelTransferHandler transferHandler = new FileLabelTransferHandler();

    public DirectoryAnimationPanel() {

        int length = 5;
        setSize(100 * length, 100 * length);
        setPreferredSize(getSize());
        setLocation(0, 0);
        setLayout(null);
        setBackground(new Color(100, 100, 100, 0));
        setVisible(true);

        source = // new Point(getWidth() / 2, getHeight() / 2);
                new Point(-100, -100);
    }

    /*
     * directory related operations
     * *************************************************************************
     * *****
     */

    public void setDirectory(String dirName) {
        setDirectory(new File(dirName));
    }

    public void setDirectory(File directory) {

        this.directory = directory;
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (directory.isDirectory()) {
            setTransferHandler(new FileTransferHandler(directory, this));
            reset();
        }
    }

    public void reset() {
        removeAll();
        fileLabels.clear();

        File[] files = directory.listFiles();
        generateFileLabels(files);

        // int length = (int) Math.round(Math.sqrt(files.length)) + 1;
        // // 10;
        // setSize(100 * length, 100 * length);
        // setPreferredSize(getSize());
        display.repaint();
    }

    public void setFrame(JFrame frame) {
        display = frame;
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

    public void spreadDir() {
        try {
            for (FileLabel fileLabel : fileLabels) {
                fileLabel.setLocation(source);
            }
            for (int i = 0; i < fileLabels.size(); i++) {
                new FileAnimation(fileLabels.get(i), computeLabelPosition(i), true).start();
                Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void shrinkDir() {
        try {
            for (int i = 0; i < fileLabels.size(); i++) {
                fileLabels.get(i).setLocation(computeLabelPosition(i));
            }
            for (FileLabel label : fileLabels) {
                new FileAnimation(label, source, false).start();
                Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * single file operations
     * *************************************************************************
     */

    public void addFile(File file) {
        FileLabel label = findFileLabel(file);
        if (label == null) {
            label = generateFileLabel(file);
            fileLabels.add(label);
        }
        spreadFile(label);
    }

    public void deleteAllFiles() {
        for (File file : directory.listFiles()) {
            deleteFile(file);
        }
    }

    public void deleteDirectory(File dir) {
        for (File file : dir.listFiles()) {
            deleteFile(file);
        }
        dir.delete();
    }

    public void deleteFile(File file) {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            FileLabel label = findFileLabel(file);
            if (label != null) {
                removeLabel(label);
            }
            file.delete();
        }
    }

    private FileLabel findFileLabel(File file) {
        for (FileLabel label : fileLabels) {
            if (file.equals(label.getFile())) {
                return label;
            }
        }
        return null;
    }

    public void removeLabel(FileLabel label) {
        int index = fileLabels.indexOf(label);
        new FileAnimation(label, source, false).start();
        fileLabels.remove(index);

        for (int j = index; j < fileLabels.size(); j++) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void spreadFile(FileLabel label) {
        int index = fileLabels.indexOf(label);
        label.setLocation(source);
        new FileAnimation(label, computeLabelPosition(index), true).start();
    }

    public void shrinkFile(File file) {
        FileLabel label = findFileLabel(file);
        if (label != null) {
            int index = fileLabels.indexOf(label);
            label.setLocation(computeLabelPosition(index));
            new FileAnimation(label, source, false).start();
        }
    }

    private void generateFileLabels(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                if (file.listFiles().length == 0) {
                    file.delete();
                } else {
                    generateFileLabels(file.listFiles());
                }
            } else {
                fileLabels.add(generateFileLabel(file));
            }
        }
    }

    private FileLabel generateFileLabel(File file) {
        FileLabel label = new FileLabel(file);
        label.setTransferHandler(transferHandler);
        add(label);
        return label;
    }

    private Point computeLabelPosition(int index) {
        int placesPerRow = getWidth() / distanceX;
        return new Point((index % placesPerRow) * distanceX, (index / placesPerRow) * distanceY);
    }

    private class FileAnimation extends Thread {

        JLabel label = null;
        Point start, goal = null;
        boolean isFinallyVisible = true;

        public FileAnimation(JLabel label, Point goal, boolean isFinallyVisible) {
            this.label = label;
            this.goal = goal;
            this.isFinallyVisible = isFinallyVisible;

            start = label.getLocation();

        }

        public void run() {
            double incrX = (goal.x - start.x) / 100.0;
            double incrY = (goal.y - start.y) / 100.0;
            for (int i = 0; i <= 100; i++) {
                label.setLocation(start.x + (int) (i * incrX), start.y + (int) (i * incrY));
                label.setVisible(true);
                display.repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            label.setVisible(isFinallyVisible);
            display.repaint();
        }

    }

    private class FileLabelTransferHandler extends TransferHandler {

        /**
         * serialVersionUID
         */
        public static final long serialVersionUID = -1L;

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }

        @Override
        public Transferable createTransferable(JComponent c) {

            Transferable tf = new Transferable() {

                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return flavor.equals(DataFlavor.javaFileListFlavor);
                }

                public DataFlavor[] getTransferDataFlavors() {
                    DataFlavor[] dfs = new DataFlavor[1];
                    dfs[0] = DataFlavor.javaFileListFlavor;
                    return dfs;
                }

                public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
                    if (df.equals(DataFlavor.javaFileListFlavor)) {
                        java.util.List<File> fileList = new java.util.ArrayList<File>();
                        fileList.add(((FileLabel) c).getFile());
                        return fileList;
                    }
                    throw new UnsupportedFlavorException(df);
                }
            };

            return tf;

        }

        @Override
        public void exportDone(JComponent c, Transferable t, int action) {
            if (action != TransferHandler.COPY) {
                FileLabel label = (FileLabel) c;
                File file = label.getFile();
                System.out.println("removing " + file);
                deleteFile(file);
            }
        }
    }

}
