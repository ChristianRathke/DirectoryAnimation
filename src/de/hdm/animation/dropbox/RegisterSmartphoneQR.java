/*
 * Created on 16.06.2016
 *
 */
package de.hdm.animation.dropbox;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.bluetooth.RemoteDevice;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.hdm.animation.QRCode;

public class RegisterSmartphoneQR extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public RegisterSmartphoneQR(String friendlyName, String bluetoothAddress) {
        super("Register Smartphone " + friendlyName);
        try {
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JPanel registerPanel = new JPanel();
            String host = "";
            ImageIcon icon = null;
            host = InetAddress.getLocalHost().getHostAddress();
            icon = new ImageIcon(new QRCode(
                    "http://" + host + ":8080/DirectoryAnimation/RegisterSmartphone?"
                    + "smartphone=" + bluetoothAddress,                    
                    400).getImage());
            JLabel QRCode = new JLabel(icon);
            registerPanel.add(QRCode);
            add(registerPanel, BorderLayout.CENTER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new RegisterSmartphoneQR("foobar", "sdfjljj");
    }
}
