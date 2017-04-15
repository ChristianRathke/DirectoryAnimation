/*
 * Created on 26.06.2016
 *
 */
package de.hdm.animation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class Common {

    public static void loadXMLFile(File file, Properties props) {
        /**
         * Get properties from xml file and convert key-value-pairs to props
         */
        try {
            InputStream inputStream = new FileInputStream(file);
            props.loadFromXML(inputStream);
            inputStream.close();

        } catch (FileNotFoundException fnfe) {
        } catch (InvalidPropertiesFormatException ipfe) {
            ipfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void saveXMLFile(File file, Properties props) {
        OutputStream outputStream;
        try {
            file.getParentFile().mkdirs();
            outputStream = new FileOutputStream(file);
            props.storeToXML(outputStream, null);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public static User assignUserInHttpSession(HttpServletRequest request)
            throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute("user");
        // return session user if already present
        if (user != null){
            return user;
        }

        String smartphoneId = request.getParameter("smartphone");
        
        // look for "smartphone" cookie if smartphone id has not been provided
        if (smartphoneId == null & request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("smartphone")) {
                    smartphoneId = cookie.getValue();
                    break;
                }
            }
        }
        
        // invent an arbitrary smartphone id
        if (smartphoneId == null) {
            smartphoneId = new BigInteger(130, new SecureRandom()).toString();
        }
        
        // create user object and store it with current session
        user = new User(smartphoneId);
        request.getSession().setAttribute("user", user);
        return user;
    }

}
