/*
 * Created on 17.06.2016
 *
 */
package de.hdm.animation;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

import sun.awt.shell.ShellFolder;

public class FileLabel extends JLabel implements MouseMotionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    File file = null;

    public FileLabel(File file) {
        super(file.getName());
        if (file.getName().length() > 7) {
            setText(file.getName().substring(0,8));
        }
        this.file = file;
        
        ShellFolder sf = null;
        try {
            sf = ShellFolder.getShellFolder(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (sf.getIcon(true) != null) {
            ImageIcon imageIcon = new ImageIcon(sf.getIcon(true), sf.getFolderType());
            setIcon(imageIcon);
            setFont (getFont ().deriveFont (10.0f));
            setHorizontalAlignment(JLabel.CENTER);
            setVerticalTextPosition(JLabel.BOTTOM);
            setHorizontalTextPosition(JLabel.CENTER);
            setSize(getPreferredSize());
            setVisible(false);

            addMouseMotionListener(this);
        }
    }
    
    public File getFile() {
        return file;
    }

    // MouseMotionListener
    public void mouseMoved(MouseEvent me) {
    }

    public void mouseDragged(MouseEvent me) {
        getTransferHandler().exportAsDrag(this, me, TransferHandler.MOVE);
    }
}
