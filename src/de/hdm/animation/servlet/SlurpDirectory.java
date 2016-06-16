package de.hdm.animation.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import de.hdm.animation.DirectoryAnimation;
import de.hdm.animation.DirectoryAnimationPanel;

/**
 * Servlet implementation class Register
 */
@WebServlet("/SlurpDirectory")
public class SlurpDirectory extends AnimationServlet {
    private static final long serialVersionUID = 1L;

    String getDirectoryCookieName() {
        return "destinationDirectory";
    }
    
    void workWith(File tmpDir, File dir, HttpSession session) throws IOException {
        if (!tmpDir.exists()) {
            return;
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }

        copy(tmpDir, dir);
        
        DirectoryAnimation animation = (DirectoryAnimation)session.getAttribute("animation");
        if (animation == null){
            animation = new DirectoryAnimation(tmpDir.getAbsolutePath());
        }
        animation.shrinkDir();       

    }

}
