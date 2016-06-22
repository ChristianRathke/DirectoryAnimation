package de.hdm.animation.servlet;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Register() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	    String directory = request.getParameter("directory");
	    if (directory == null) {
	        directory = "";
	    }
	    Cookie dirCookie = new Cookie("directory", directory);
        
        if (dirCookie.getValue().equals("")) {
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            try {
                dirCookie.setValue((String)clpbrd.getData(DataFlavor.stringFlavor));
            } catch (UnsupportedFlavorException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
	    response.addCookie(dirCookie);
	    
	    response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
        out.println(docType);
        out.println("<html><body>");
        out.println("Directory " + dirCookie.getValue() + " remembered on your device.<br><br>");

        out.println("<form action=\"ShareFolder\" method=\"Post\">");
        out.println("Sharing folder: <input type=\"submit\" name=\"directory\" value=\"" + dirCookie.getValue() + "\">");
        out.println("</form>");
        
        out.println("</body>");
        out.println("</html>");

        out.close();
        
        

	}

}
