package de.hdm.animation.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hdm.animation.ShareSpace;

/**
 * Servlet implementation class Register
 */
public abstract class AnimationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static ShareSpace shareSpace = null;
    
    public static ShareSpace shareSpace() {
        if (shareSpace == null) {
            shareSpace = new ShareSpace();
        }
        return shareSpace;
    }

    public void destroy() {
        if (shareSpace != null) {
            shareSpace.remove();
        }
        super.destroy();
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        File dir = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(getDirectoryCookieName())) {
                    dir = new File(cookie.getValue());
                    break;
                }
            }
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
        out.println(docType);
        out.println("<html><body>");

        out.println("Directory: " + dir.getAbsolutePath());

        out.println("</body>");
        out.println("</html>");
        
        File tmpDir = new File(System.getProperty("java.io.tmpdir") + "/animation");
        workWith(tmpDir, dir);  

        out.close();    

    }
    
    abstract String getDirectoryCookieName();
    abstract void workWith(File tmpDir, File dir) throws IOException;
        
    void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    void copyFile(File source, File target) throws IOException {        
        try (
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(target)
        ) {
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

}
