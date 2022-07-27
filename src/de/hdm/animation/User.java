/*
 * Created on 26.06.2016
 *
 */
package de.hdm.animation;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.bluetooth.RemoteDevice;

import com.dropbox.core.DbxException;

import de.hdm.animation.dropbox.Dropbox;

public class User {
    /*
     * The userDB contains key-value pairs associating smartphone ids to
     * dropbox access tokens.
     */
    
    private final File appFileDir = new File(System.getenv("appdata") +  "/HdMSharingApp");
    private final String deviceId;
    private Dropbox dropbox;
    private final Properties props = new Properties();

    public User(String deviceId) {
        this.deviceId = deviceId;
        loadProps();
    }
    
    public User(RemoteDevice device) {
        this(device.getBluetoothAddress());
        try {
            setDeviceFriendlyName(device.getFriendlyName(false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadProps() {
        Common.loadXMLFile(new File(appFileDir, deviceId), props);
    }
    
    public void save() {
        Common.saveXMLFile(new File(appFileDir, deviceId), props);
    }
    
    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceFriendlyName() {
        return props.getProperty("deviceFriendlyName");
    }
    
    public void setDeviceFriendlyName(String name) {
        props.setProperty("deviceFriendlyName", name);
    }

    public String getToken() {
        return props.getProperty("token");
    }

    public void setToken(String token) {
        props.put("token", token);
        dropbox = new Dropbox(token);
        save();
    }
    
    public Dropbox getDropbox() {
        if (dropbox == null) {
            dropbox = new Dropbox(getToken());
        }
        return dropbox;
    }
    
    public String getName() {
        try {
            return getDropbox().getAccount().getName().getFamiliarName();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasToken() {
        return props.getProperty("token")!=null;
    }
}
