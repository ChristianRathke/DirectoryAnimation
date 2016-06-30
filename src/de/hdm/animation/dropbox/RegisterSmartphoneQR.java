/*
 * Created on 16.06.2016
 *
 */
package de.hdm.animation.dropbox;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
    private static RegisterSmartphoneQR QRCodeWindow = null;
    private String address;

    public static RegisterSmartphoneQR newInstance(String friendlyName, String bluetoothAddress) {
        if (QRCodeWindow != null) {
            QRCodeWindow.remove();
        }
        QRCodeWindow = new RegisterSmartphoneQR(friendlyName, bluetoothAddress);
        return QRCodeWindow;
    }

    public static void bluetoothAddressRegistered(String address) {
        if (QRCodeWindow != null) {
            QRCodeWindow.finalizeRegistration(address);
        }
    }

    private void finalizeRegistration(String address) {
        if (address.equals(this.address)) {
            remove();
        }
    }

    private void remove() {
        QRCodeWindow.setVisible(false);
        QRCodeWindow.dispose();
        QRCodeWindow = null;
    }

    private RegisterSmartphoneQR(String friendlyName, String bluetoothAddress) {
        super(friendlyName + " Registration");
        address = bluetoothAddress;
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                remove();
            }

        });

        // setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        try {
            JPanel registerPanel = new JPanel();
            String host = "";
            ImageIcon icon = null;
            host = InetAddress.getLocalHost().getHostAddress();
            icon = new ImageIcon(new QRCode("http://" + host + ":8080/DirectoryAnimation/RegisterSmartphone?"
                    + "smartphone=" + bluetoothAddress, 400).getImage());
            JLabel QRCode = new JLabel(icon);
            registerPanel.add(QRCode);
            add(registerPanel, BorderLayout.CENTER);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new RegisterSmartphoneQR("foobar", "sdfjljj");
    }
}
