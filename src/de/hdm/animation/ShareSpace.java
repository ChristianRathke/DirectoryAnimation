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
    private JButton titleButton = new JButton("Animate");
    private JPanel deviceDownloadButtonPanel = new JPanel();
    private JPanel deviceUploadButtonPanel = new JPanel();
    private Color backgroundColor = Color.white;

    private Map<String, User> phones = new HashMap<String, User>();

    private Map<RemoteDevice, DbxButton> downloadButtons = new HashMap<RemoteDevice, DbxButton>();
    private Map<RemoteDevice, DbxButton> uploadButtons = new HashMap<RemoteDevice, DbxButton>();

    private ArrayList<RemoteDevice> tmpCollectedDevices = new ArrayList<RemoteDevice>();

    private class DbxButton extends JButton {
        private static final long serialVersionUID = 1L;
        private User user;

        private DbxButton(User u) {
            super(u.getFriendlyName());
            user = u;
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        }

        private boolean isConnected() {
            return user.hasToken();
        }

        private boolean equalsSmartphoneId(String id) {
            return id.equals(user.getDeviceId());
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
        initializeSmartphoneList();

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

        titleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                dap.animate();
            }
        });

        JButton clearAnimationDirectory = new JButton("Remove all files");
        clearAnimationDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run() {
                        dap.deleteAllFiles();
                        dap.reset();
                    }
                }.start();
            }
        });

        JPanel middlePanel = new JPanel();
        middlePanel.setBackground(backgroundColor);
        JPanel tmpPanel = new JPanel();
        tmpPanel.setBackground(backgroundColor);

        middlePanel.add(tmpPanel);
        middlePanel.add(titleButton);
        middlePanel.add(clearAnimationDirectory);
        tmpPanel = new JPanel();
        tmpPanel.setBackground(backgroundColor);
        middlePanel.add(tmpPanel);

        JPanel comPanel = new JPanel(new BorderLayout());
        comPanel.setBackground(backgroundColor);
        comPanel.add(middlePanel, BorderLayout.CENTER);
        comPanel.add(new QRCode(contextPath + "UpOrDownload?direction=download", 200)
                .getJPanel("Download"), BorderLayout.WEST);
        comPanel.add(new QRCode(contextPath + "UpOrDownload?direction=upload", 200)
                .getJPanel("Upload"), BorderLayout.EAST);
        add(comPanel, BorderLayout.NORTH);
    }

    private void initializeSmartphoneList() {
        deviceDownloadButtonPanel.setLayout(new BoxLayout(deviceDownloadButtonPanel, BoxLayout.PAGE_AXIS));
        deviceDownloadButtonPanel.setBackground(backgroundColor);

        deviceUploadButtonPanel.setLayout(new BoxLayout(deviceUploadButtonPanel, BoxLayout.PAGE_AXIS));
        deviceUploadButtonPanel.setBackground(backgroundColor);

        add(deviceDownloadButtonPanel, BorderLayout.WEST);
        add(deviceUploadButtonPanel, BorderLayout.EAST);
    }

    private void addDeviceButtons(RemoteDevice device) {
        User user = new User(device);

        DbxButton downloadButton = new DbxButton(user);
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downloadUserDropbox(user);
            }
        });
        deviceDownloadButtonPanel.add(downloadButton);

        DbxButton uploadButton = new DbxButton(user);
        uploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uploadUserDropbox(user);
            }
        });
        deviceUploadButtonPanel.add(uploadButton);

        downloadButtons.put(device, downloadButton);
        uploadButtons.put(device, uploadButton);

        pack();
    }

    public void downloadUserDropbox(User user) {
        if (user.hasToken()) {
            new Thread() {
                public void run() {
                    try {
                        titleButton.setText("Downloading ....");
                        // this will recreate all file labels in the
                        // animation panel
                        dap.reset();
                        user.getDropbox().withAnimationPanel(dap).downloadFiles(animationDir);
                        titleButton.setText("Animate");
                    } catch (DbxException e1) {
                        e1.printStackTrace();
                    }
                }
            }.start();
        } else {
            System.out.println("User of " + user.getFriendlyName() + " is not registered.");
            RegisterSmartphoneQR.newInstance(user.getFriendlyName(), user.getDeviceId());
        }
    }

    public void uploadUserDropbox(User user) {
        if (user.hasToken()) {
            new Thread() {
                public void run() {
                    try {
                        titleButton.setText("Uploading ....");
                        user.getDropbox().withAnimationPanel(dap).uploadFiles(animationDir);
                        // this will recreate all file labels in the
                        // animation panel
                        dap.reset();
                        titleButton.setText("Animate");
                    } catch (DbxException e1) {
                        e1.printStackTrace();
                    }
                }
            }.start();
        } else {
            System.out.println("User of " + user.getFriendlyName() + " is not registered.");
            RegisterSmartphoneQR.newInstance(user.getFriendlyName(), user.getDeviceId());
        }
    }

    private void indicateRegistrationStatus(RemoteDevice device) {
        DbxButton db = downloadButtons.get(device);
        DbxButton ub = uploadButtons.get(device);

        if (db.user.hasToken()) {
            try {
                ub.setText(db.user.getDropbox().getAccount().getName().getFamiliarName());
                db.setText(db.user.getDropbox().getAccount().getName().getFamiliarName());
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeDeviceButton(RemoteDevice device) {
        JButton button = downloadButtons.get(device);
        deviceDownloadButtonPanel.remove(button);
        downloadButtons.remove(device);

        button = uploadButtons.get(device);
        deviceDownloadButtonPanel.remove(button);
        uploadButtons.remove(device);
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
        if (!downloadButtons.containsKey(btDevice)) {
            this.addDeviceButtons(btDevice);
        }
        tmpCollectedDevices.add(btDevice);
        indicateRegistrationStatus(btDevice);

    }

    @Override
    public void inquiryCompleted(int arg0) {
        // System.out.println("Inquiry completed.");
        for (RemoteDevice shown : downloadButtons.keySet()) {
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
