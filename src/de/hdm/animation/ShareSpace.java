/*
 * Created on 16.06.2016
 *
 */
package de.hdm.animation;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.zxing.WriterException;

import sun.misc.BASE64Encoder;

public class ShareSpace {

    private JFrame shareSpace = new JFrame("ShareSpace");

    public ShareSpace() {
        shareSpace.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        String host="";
        ImageIcon icon=null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
            icon = new ImageIcon(
                    GenerateQRCode.createQRImage("http://" + host + ":8080/DirectoryAnimationPanel/SlurpDirectory", 200));
            icon = new ImageIcon(
                    GenerateQRCode.createQRImage("http://" + host + ":8080/DirectoryAnimationPanel/SpreadDirectory", 200));
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JLabel slurpQRLabel = new JLabel("Upload Files", icon, JLabel.CENTER);
        slurpQRLabel.setVerticalTextPosition(JLabel.BOTTOM);
        slurpQRLabel.setHorizontalTextPosition(JLabel.CENTER);

        shareSpace.add(slurpQRLabel, BorderLayout.EAST);
        JLabel spreadQRLabel = new JLabel("Download Files", icon, JLabel.CENTER);
        spreadQRLabel.setVerticalTextPosition(JLabel.BOTTOM);
        spreadQRLabel.setHorizontalTextPosition(JLabel.CENTER);
        
        shareSpace.add(spreadQRLabel, BorderLayout.WEST);
        DirectoryAnimationPanel dap = new DirectoryAnimationPanel();
        shareSpace.add(dap);
        
        shareSpace.setLocationRelativeTo(null);
        shareSpace.pack();
        shareSpace.setVisible(true);
    }
    
    public static void main(String[] args) {
        new ShareSpace();
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
