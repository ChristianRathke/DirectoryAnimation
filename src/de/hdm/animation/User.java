/*
 * Created on 26.06.2016
 *
 */
package de.hdm.animation;

import java.io.File;
import java.util.Properties;

public class User {
    /*
     * The userDB contains key-value pairs associating smartphone ids to
     * dropbox access tokens.
     */
    private static final String userDBName = "users.xml";
    private static File userDB;
    private static Properties users = new Properties();
    static {
        userDB = new File(System.getenv("appdata") +  "/HdMSharingApp/" + userDBName);
        Common.loadXMLFile(userDB, users);
    }
    
    
    private String id;
    private String token;

    public static User getUser(String id) {
        Common.loadXMLFile(userDB, users);
        return new User(id, users.getProperty(id));
    }
    
    public User(String id, String token) {
        this(id);
        setToken(token);
    }

    public User(String id) {
        this.id = id;
    }

    public void saveUsers() {
        Common.saveXMLFile(userDB, users);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        if (token!=null) {
            users.put(id, token);
        }
    }

    public boolean hasToken() {
        return token != null;
    }
}
