/*
 * Created on 16.06.2016
 *
 */
package de.hdm.animation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sun.misc.BASE64Encoder;

public class ShareSpace extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private DirectoryAnimationPanel dap = new DirectoryAnimationPanel();
    private String directory = "";
    private File animationDir = new File(System.getProperty("java.io.tmpdir") + "/animation");
    private JButton download = new JButton("Download");
    private JButton upload = new JButton("Upload");
    private boolean isSpread = false;

    public ShareSpace() {
        super("ShareSpace");
        dap.setFrame(this);
        dap.setDirectory(animationDir);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        

        add(dap, BorderLayout.CENTER);
        if (!animationDir.exists()) {
            animationDir.mkdirs();
        }
        
        initializeButtonPanel();

        pack();
        setLocationRelativeTo(null);
        setBackground(Color.white);
        setVisible(true);
    }

    public static void main(String[] args) {
        new ShareSpace().setDirectory("C:/users/christian/desktop/fritz");
        
    }

    public void remove() {
        setVisible(false);
        dispose();
    }
    
    private void initializeButtonPanel() {
        JPanel comPanel = new JPanel(new BorderLayout());
        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spreadDir();
            }
        });

        upload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shrinkDir();
            }
        });
        
        comPanel.add(upload, BorderLayout.EAST);
        JButton button = new JButton(" ");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (isSpread) {
                    dap.shrinkDir();
                } else {
                    dap.spreadDir();
                }
                isSpread = !isSpread;
            }
        });
        comPanel.add(button, BorderLayout.CENTER);
        
        comPanel.add(download, BorderLayout.WEST);
        add(comPanel, BorderLayout.NORTH);
    }

    public void setDirectory(String dir) {
        if (dir != null) {
            directory = dir;
            download.setText("Download from " + directory);
            download.invalidate();
            upload.setText("Upload to " + directory);
            upload.invalidate();
        }
        pack();
    }

    public void spreadDir() {
        try {
            copy(new File(directory), animationDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dap.spreadDir();
        isSpread = true;
    }

    public void shrinkDir() {
        try {
            copy(animationDir, new File(directory));
        } catch (IOException e) {
            e.printStackTrace();
        }
        dap.shrinkDir();
        isSpread = false;
    }

    void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    void copyFile(File source, File target) throws IOException {
        try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(target)) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    void delete(File file) {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            file.delete();
        }
    }

    void deleteDirectory(File dir) {
        for (File file : dir.listFiles()) {
            delete(file);
        }
        dir.delete();
    }

    public String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }
}
