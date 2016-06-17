package de.hdm.animation.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;

import de.hdm.animation.DirectoryAnimation;

/**
 * Servlet implementation class Register
 */
@WebServlet("/SlurpDirectory")
public class SlurpDirectory extends AnimationServlet {
    private static final long serialVersionUID = 1L;

    String getDirectoryCookieName() {
        return "destinationDirectory";
    }
    
    void workWith(File tmpDir, File dir) throws IOException {
        if (!tmpDir.exists()) {
            return;
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }

        copy(tmpDir, dir);          
        shareSpace().shrinkDir(tmpDir);

    }

}
