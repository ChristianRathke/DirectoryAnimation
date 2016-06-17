package de.hdm.animation.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;

import de.hdm.animation.DirectoryAnimation;

/**
 * Servlet implementation class Register
 */
@WebServlet("/SpreadDirectory")
public class SpreadDirectory extends AnimationServlet {
    private static final long serialVersionUID = 1L;

    String getDirectoryCookieName() {
        return "sourceDirectory";
    }
    
    void workWith(File tmpDir, File dir) throws IOException {
        if (tmpDir.exists()) {
            for (File file : tmpDir.listFiles()) {
                delete(file);
            }
        } else {
            tmpDir.mkdir();
        }
        
        copy(dir, tmpDir);          
        shareSpace().spreadDir(tmpDir);

    }

}
