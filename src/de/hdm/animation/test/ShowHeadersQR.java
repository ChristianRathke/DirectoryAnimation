/*
 * Created on 16.06.2016
 *
 */
package de.hdm.animation.test;

import java.awt.BorderLayout;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.hdm.animation.QRCode;

public class ShowHeadersQR extends JFrame {

    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ShowHeadersQR() {
        super("Show Headers");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        try {
            JPanel codePanel = new JPanel();
            String host = "";
            ImageIcon icon = null;
            host = InetAddress.getLocalHost().getHostAddress();
            icon = new ImageIcon(new QRCode("http://" + host + ":8080/DirectoryAnimation/ShowHeaders", 400).getImage());
            JLabel QRCode = new JLabel(icon);
            codePanel.add(QRCode);
            add(codePanel, BorderLayout.CENTER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new ShowHeadersQR();
    }
}
