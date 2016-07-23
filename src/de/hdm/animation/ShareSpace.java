/*
 * Created on 16.06.2016
 *
 */
package de.hdm.animation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dropbox.core.DbxException;

import de.hdm.animation.dropbox.RegisterSmartphoneQR;

public class ShareSpace extends JFrame implements DiscoveryListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static ShareSpace instance = null;

    private DirectoryAnimationPanel dap = new DirectoryAnimationPanel();
    private String contextPath = "http://localhost:8080/DirectoryAnimation/";
    private File animationDir = new File(System.getProperty("java.io.tmpdir") + "/animation");
    private JButton animateButton = new JButton("Animate");
    private JPanel devicesPanel = new JPanel();
    private Color backgroundColor = Color.white;

    private Map<RemoteDevice, JLabel> deviceLabels = new HashMap<RemoteDevice, JLabel>();
    private ArrayList<RemoteDevice> tmpCollectedDevices = new ArrayList<RemoteDevice>();

    private class DbxLabel extends JLabel {
        private static final long serialVersionUID = 1L;
        private User user;

        private DbxLabel(User u) {
            user = u;
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            updateLabelText();
        }

        private void updateLabelText() {

            if (user.hasToken()) {
                setText("<html><body><center>" + user.getName() + "<br>(" + user.getDeviceFriendlyName()
                        + ")</center></body></html>");
            } else {
                setText(user.getDeviceFriendlyName());
            }
        }
    }

    public static ShareSpace instance() {
        if (instance == null) {
            instance = new ShareSpace();
        }
        return instance;
    }

    private ShareSpace() {
        super("Flying Docs");
        dap.setFrame(this);
        dap.setDirectory(animationDir);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                instance = null;
                dispose();
            }

        });
        add(dap, BorderLayout.CENTER);

        try {
            contextPath = "http://" + InetAddress.getLocalHost().getHostAddress() + ":8080/DirectoryAnimation/";
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        initializeLabelPanel();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        startRemoteDeviceDiscovery();
    }

    public static void main(String[] args) {
        ShareSpace.instance();
    }

    public void remove() {
        setVisible(false);
        dispose();
    }

    private void initializeLabelPanel() {

        animateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                dap.animate();
            }
        });

        JButton removeFilesButton = new JButton("Remove all files");
        removeFilesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        dap.deleteAllFiles();
                        dap.reset();
                    }
                }.start();
            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(backgroundColor);
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(backgroundColor);

        buttonsPanel.add(emptyPanel);
        buttonsPanel.add(animateButton);
        buttonsPanel.add(removeFilesButton);
        emptyPanel = new JPanel();
        emptyPanel.setBackground(backgroundColor);
        buttonsPanel.add(emptyPanel);

        devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.PAGE_AXIS));
        devicesPanel.setBackground(backgroundColor);

        JPanel qrCodePanel = new JPanel(new BorderLayout());
        qrCodePanel.setBackground(backgroundColor);
        qrCodePanel.add(buttonsPanel, BorderLayout.CENTER);
        qrCodePanel.add(new QRCode(contextPath + "Download", 100).getJPanel("Download"),
                BorderLayout.WEST);
        // comPanel.add(devicesPanel);
        add(qrCodePanel, BorderLayout.NORTH);

        qrCodePanel = new JPanel(new BorderLayout());
        qrCodePanel.setBackground(backgroundColor);
        qrCodePanel.add(new QRCode(contextPath + "Upload", 100).getJPanel("Upload"),
                BorderLayout.EAST);
        add(qrCodePanel, BorderLayout.SOUTH);

    }

    private void addDeviceButtons(RemoteDevice device) {
        User user = new User(device);

        JPanel deviceButtonsPanel = new JPanel(new BorderLayout());
        deviceButtonsPanel.setBackground(backgroundColor);

        JButton downloadButton = new JButton();
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downloadUserDropbox(user);
            }
        });
        downloadButton.setText("Down");
        deviceButtonsPanel.add(downloadButton, BorderLayout.WEST);

        DbxLabel deviceLabel = new DbxLabel(user);
        deviceButtonsPanel.add(deviceLabel, BorderLayout.CENTER);

        JButton uploadButton = new JButton();
        uploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uploadUserDropbox(user);
            }
        });
        uploadButton.setText("Up");
        deviceButtonsPanel.add(uploadButton, BorderLayout.EAST);

        devicesPanel.add(deviceButtonsPanel);
        deviceLabels.put(device, deviceLabel);

        pack();
    }

    public void downloadUserDropbox(User user) {
        if (user.hasToken()) {
            new Thread() {
                public void run() {
                    try {
                        animateButton.setText("Downloading ....");
                        // this will recreate all file labels in the
                        // animation panel
                        dap.reset();
                        user.getDropbox().withAnimationPanel(dap).downloadFiles(animationDir);
                        animateButton.setText("Animate");
                    } catch (DbxException e1) {
                        e1.printStackTrace();
                    }
                }
            }.start();
        } else {
            System.out.println("User of " + user.getDeviceFriendlyName() + " is not registered.");
            RegisterSmartphoneQR.newInstance(user.getDeviceFriendlyName(), user.getDeviceId());
        }
    }

    public void uploadUserDropbox(User user) {
        if (user.hasToken()) {
            new Thread() {
                public void run() {
                    try {
                        animateButton.setText("Uploading ....");
                        user.getDropbox().withAnimationPanel(dap).uploadFiles(animationDir);
                        // this will recreate all file labels in the
                        // animation panel
                        dap.reset();
                        animateButton.setText("Animate");
                    } catch (DbxException e1) {
                        e1.printStackTrace();
                    }
                }
            }.start();
        } else {
            System.out.println("User of " + user.getDeviceFriendlyName() + " is not registered.");
            RegisterSmartphoneQR.newInstance(user.getDeviceFriendlyName(), user.getDeviceId());
        }
    }

    private void indicateRegistrationStatus(RemoteDevice device) {

    }

    private void removeDeviceButton(RemoteDevice device) {
        JLabel label = deviceLabels.get(device);
        if (label != null) {
            devicesPanel.remove(label.getParent());
        }
    }

    private void startRemoteDeviceDiscovery() {
        try {
            LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, this);
        } catch (BluetoothStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
        if (!deviceLabels.containsKey(btDevice)) {
            this.addDeviceButtons(btDevice);
        }
        tmpCollectedDevices.add(btDevice);
        indicateRegistrationStatus(btDevice);

    }

    @Override
    public void inquiryCompleted(int arg0) {
        // System.out.println("Inquiry completed.");
        for (RemoteDevice shown : deviceLabels.keySet()) {
            if (!tmpCollectedDevices.contains(shown)) {
                removeDeviceButton(shown);
            }
        }
        try {
            // wait for 1 minute to resume device discovery
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startRemoteDeviceDiscovery();
    }

    @Override
    public void serviceSearchCompleted(int arg0, int arg1) {

    }

    @Override
    public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {

    }
}
