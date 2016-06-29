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
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

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

}
