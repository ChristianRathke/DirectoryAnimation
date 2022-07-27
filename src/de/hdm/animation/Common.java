/*
 * Created on 26.06.2016
 *
 */
package de.hdm.animation;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Properties;

public class Common {

    public static void loadXMLFile(File file, Properties props) {
        /*
          Get properties from xml file and convert key-value-pairs to props
         */
        try {
            InputStream inputStream = new FileInputStream(file);
            props.loadFromXML(inputStream);
            inputStream.close();

        } catch (FileNotFoundException ignored) {
        } catch (IOException ipfe) {
            ipfe.printStackTrace();
        }
    }

    public static void saveXMLFile(File file, Properties props) {
        OutputStream outputStream;
        try {
            if (file.getParentFile().mkdirs()) {
                outputStream = Files.newOutputStream(file.toPath());
                props.storeToXML(outputStream, null);
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static User assignUserInHttpSession(HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");
        // return session user if already present
        if (user != null) {
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
