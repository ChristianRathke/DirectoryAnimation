/*
 * Created on 16.06.2016
 *
 */
package de.hdm.animation.servlet;

import java.awt.BorderLayout;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.hdm.animation.QRCode;

public class RegisterDirectoryQR extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public RegisterDirectoryQR(String smartphone) {
        super("RegisterDirectory Dropbox Folder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String host = "";
        JPanel registerPanel = new JPanel();
        ImageIcon icon = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
            icon = new ImageIcon(
                    new QRCode("http://" + host + ":8080/DirectoryAnimation/RegisterDirectory?smartphone=" + smartphone,
                            400).getImage());
            JLabel QRCode = new JLabel(icon);
            registerPanel.add(QRCode);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        add(registerPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new RegisterDirectoryQR("foobar");
    }
}
