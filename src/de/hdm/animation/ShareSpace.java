/*
 * Created on 16.06.2016
 *
 */
package de.hdm.animation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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
import javax.swing.JPanel;

import com.dropbox.core.DbxException;

import de.hdm.animation.dropbox.Dropbox;
import de.hdm.animation.dropbox.RegisterSmartphoneQR;

public class ShareSpace extends JFrame implements DiscoveryListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private DirectoryAnimationPanel dap = new DirectoryAnimationPanel();
    private File animationDir = new File(System.getProperty("java.io.tmpdir") + "/animation");
    private boolean isSpread = false;
    private JButton titleButton = new JButton("Animate");
    private JPanel deviceDownloadButtonPanel = new JPanel();
    private JPanel deviceUploadButtonPanel = new JPanel();
    
    private Map<String,User> phones = new HashMap<String,User>();
    
    private Map<RemoteDevice, JButton> shownDevices = new HashMap<RemoteDevice, JButton>();
    private ArrayList<RemoteDevice> tmpCollectedDevices = new ArrayList<RemoteDevice>();

    public ShareSpace() {
        super("Flying Docs");
        dap.setFrame(this);
        dap.setDirectory(animationDir);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(dap, BorderLayout.CENTER);

        initializeLabelPanel();
        initializeSmartphoneList();

        pack();
        setLocationRelativeTo(null);
        setBackground(Color.white);
        setVisible(true);

        startRemoteDeviceDiscovery();
    }

    public static void main(String[] args) {
        new ShareSpace();
    }

    public void remove() {
        setVisible(false);
        dispose();
    }

    private void initializeLabelPanel() {
        JPanel middlePanel = new JPanel(new BorderLayout());
        titleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (isSpread) {
                    dap.shrinkDir();
                } else {
                    dap.spreadDir();
                }
                isSpread = !isSpread;
            }
        });
        middlePanel.add(titleButton, BorderLayout.WEST);
        
        JButton clearAnimationDirectory = new JButton("Remove all files");
        clearAnimationDirectory.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        clearAnimationDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dap.deleteAllFiles();
            }
        });
        middlePanel.add(clearAnimationDirectory, BorderLayout.EAST);

        JPanel comPanel = new JPanel(new BorderLayout());
        comPanel.add(middlePanel, BorderLayout.CENTER);
        comPanel.add(new JButton("Download       "), BorderLayout.WEST);
        comPanel.add(new JButton("Upload         "), BorderLayout.EAST);
        add(comPanel, BorderLayout.NORTH);
    }

    private void initializeSmartphoneList() {
        deviceDownloadButtonPanel.setLayout(new BoxLayout(deviceDownloadButtonPanel, BoxLayout.PAGE_AXIS));
        deviceUploadButtonPanel.setLayout(new BoxLayout(deviceUploadButtonPanel, BoxLayout.PAGE_AXIS));
        add(deviceDownloadButtonPanel, BorderLayout.WEST);
        add(deviceUploadButtonPanel, BorderLayout.EAST);
    }

    private void addDeviceButtons(RemoteDevice device) {
        String name = "unknown Phone";
        try {
            name = device.getFriendlyName(false);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        JButton downloadButton = new JButton(name);
        //downloadButton.setFont(downloadButton.getFont().deriveFont(14.0f));
        downloadButton.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JButton uploadButton = new JButton(name);
        //uploadButton.setFont(uploadButton.getFont().deriveFont(14.0f));
        uploadButton.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        final String friendlyName = name;
        final String bluetoothAddress = device.getBluetoothAddress();

        downloadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                User user = User.getUser(bluetoothAddress);
                if (user.hasToken()) {
                    new Thread() {
                        public void run() {
                            try {
                                titleButton.setText("Downloading ....");
                                // this will recreate all file labels in the animation panel
                                dap.reset();
                                new Dropbox(user.getToken(), dap).downloadFiles(animationDir);
                                titleButton.setText("Animate");
                                isSpread = true;
                            } catch (DbxException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    System.out.println("User of " + friendlyName + " is not registered.");
                    RegisterSmartphoneQR.newInstance(friendlyName, bluetoothAddress);
                }
            }
        });

        uploadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                User user = User.getUser(device.getBluetoothAddress());
                if (user.hasToken()) {
                    new Thread() {
                        public void run() {
                            try {
                                titleButton.setText("Uploading ....");
                                new Dropbox(user.getToken(), dap).uploadFiles(animationDir);
                                // this will recreate all file labels in the animation panel
                                dap.reset();
                                titleButton.setText("Animate");
                                isSpread = false;
                            } catch (DbxException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    System.out.println("User of " + friendlyName + " is not registered.");
                    RegisterSmartphoneQR.newInstance(friendlyName, bluetoothAddress);
                }
            }
        });

        deviceDownloadButtonPanel.add(downloadButton);
        deviceUploadButtonPanel.add(uploadButton);
        shownDevices.put(device, downloadButton);
        pack();
    }

    private void removeDeviceButton(RemoteDevice device) {
        JButton downloadButton = shownDevices.get(device);
        deviceDownloadButtonPanel.remove(downloadButton);
        shownDevices.remove(device);
    }

    private void startRemoteDeviceDiscovery() {
        try {
            LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, this);
        } catch (BluetoothStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
        if (!shownDevices.containsKey(btDevice)) {
            this.addDeviceButtons(btDevice);
        }
        tmpCollectedDevices.add(btDevice);

    }

    @Override
    public void inquiryCompleted(int arg0) {
        // System.out.println("Inquiry completed.");
        for (RemoteDevice shown : shownDevices.keySet()) {
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
