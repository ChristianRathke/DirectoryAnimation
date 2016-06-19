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
import javax.swing.JPanel;

import com.google.zxing.WriterException;

import sun.misc.BASE64Encoder;

public class RegisterDirectoryFrame extends JFrame {

   
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public RegisterDirectoryFrame() {
        super("Register Dropbox Folder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String host = "";
        JPanel registerPanel = new JPanel();
        ImageIcon icon = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
            icon = new ImageIcon(
                    GenerateQRCode.createQRImage("http://" + host + ":8080/DirectoryAnimation/register.html", 400));
            JLabel QRCode = new JLabel(icon);
            registerPanel.add(QRCode);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }
        add(registerPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new RegisterDirectoryFrame();
    }
}
