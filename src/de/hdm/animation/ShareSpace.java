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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private File directory;
    private File animationDir = new File(System.getProperty("java.io.tmpdir") + "/animation");
    private boolean isSpread = false;
    private JPanel deviceDownloadButtonPanel = new JPanel();
    private JPanel deviceUploadButtonPanel = new JPanel();
    private Map<RemoteDevice, JButton> shownDevices = new HashMap<RemoteDevice, JButton>();
    private ArrayList<RemoteDevice> collectedDevices = new ArrayList<RemoteDevice>();

    public ShareSpace() {
        super("Flying Docs");
        dap.setFrame(this);
        dap.setDirectory(animationDir);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(dap, BorderLayout.CENTER);
        if (!animationDir.exists()) {
            animationDir.mkdirs();
        }

        initializeLabelPanel();
        initializeSmartphoneList();

        pack();
        setLocationRelativeTo(null);
        setBackground(Color.white);
        setVisible(true);

        startRemoteDeviceDiscovery();
    }

    public static void main(String[] args) {
        new ShareSpace().setDirectory("C:/users/christian/desktop/franz");

    }

    public void remove() {
        setVisible(false);
        dispose();
    }

    private void initializeLabelPanel() {
        JPanel comPanel = new JPanel(new BorderLayout());
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

        comPanel.add(new JButton("Upload"), BorderLayout.EAST);
        comPanel.add(button, BorderLayout.CENTER);
        comPanel.add(new JButton("Download"), BorderLayout.WEST);
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
        JButton donwloadButton = new JButton(name);
        JButton uploadButton = new JButton(name);

        final String friendlyName = name;
        final String bluetoothAddress = device.getBluetoothAddress();

        donwloadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                User user = User.getUser(device.getBluetoothAddress());
                if (user.hasToken()) {
                    try {
                        new Dropbox(user.getToken(), dap).downloadFiles(animationDir);
                    } catch (DbxException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    System.out.println("User of " + friendlyName + " is not registered.");
                    new RegisterSmartphoneQR(friendlyName, bluetoothAddress);
                }
            }
        });

        uploadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                User user = User.getUser(device.getBluetoothAddress());
                if (user.hasToken()) {
                    try {
                        new Dropbox(user.getToken(), dap).uploadFiles(animationDir);
                        isSpread = false;
                    } catch (DbxException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    System.out.println("User of " + friendlyName + " is not registered.");
                    new RegisterSmartphoneQR(friendlyName, bluetoothAddress);
                }
            }
        });

        deviceDownloadButtonPanel.add(donwloadButton);
        deviceUploadButtonPanel.add(uploadButton);
        shownDevices.put(device, donwloadButton);
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

    public void setDirectory(String dir) {
        setDirectory(new File(dir));
    }

    public void setDirectory(File dir) {
        directory = dir;
    }

    public void downloadDir() {
        try {
            File target = null;
            for (String f : directory.list()) {
                target = new File(animationDir, f);
                copy(new File(directory, f), target);
                dap.addFile(target);
            }
            // copy(directory, animationDir);
            // dap.spreadDir();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isSpread = true;
    }

    public void uploadDir() {
        try {
            File source = null;
            for (String f : animationDir.list()) {
                source = new File(animationDir, f);
                copy(source, new File(directory, f));
                delete(source);
                dap.removeFile(source);
            }
            // copy(animationDir, directory);
            // dap.shrinkDir();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            target.mkdirs();
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

    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
        if (!shownDevices.containsKey(btDevice)) {
            this.addDeviceButtons(btDevice);
        }
        collectedDevices.add(btDevice);

    }

    @Override
    public void inquiryCompleted(int arg0) {
        // System.out.println("Inquiry completed.");
        for (RemoteDevice shown : shownDevices.keySet()) {
            if (!collectedDevices.contains(shown)) {
                removeDeviceButton(shown);
            }
        }
        try {
            // wait for 1 minute to resume device discovery
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startRemoteDeviceDiscovery();
    }

    @Override
    public void serviceSearchCompleted(int arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {
        // TODO Auto-generated method stub

    }
}
