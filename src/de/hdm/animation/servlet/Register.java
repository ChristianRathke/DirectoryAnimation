package de.hdm.animation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.WriterException;

import de.hdm.animation.GenerateQRCode;

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
	    
	    Cookie sourceCookie = new Cookie("sourceDirectory", request.getParameter("sourceDirectory"));
        Cookie destCookie = new Cookie("destinationDirectory", request.getParameter("destinationDirectory"));
	    response.addCookie(sourceCookie);
	    response.addCookie(destCookie);
	    
	    response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
        out.println(docType);
        out.println("<html><body>");

/*
 *      String host = InetAddress.getLocalHost().getHostAddress();
        String sourceDirQrCode="";
        String destDirQrCode="";
        try {
            sourceDirQrCode = GenerateQRCode.encodeToString(
                    GenerateQRCode.createQRImage("http://" + host + ":8080/DirectoryAnimationPanel/SpreadDirectory", 200),
                    "jpg");
            destDirQrCode = GenerateQRCode.encodeToString(
                    GenerateQRCode.createQRImage("http://" + host + ":8080/DirectoryAnimationPanel/SlurpDirectory", 200),
                    "jpg");
        } catch (WriterException e) {
            e.printStackTrace();
        }
        out.println();
        out.println("Source directory " + request.getParameter("sourceDirectory") + " remembered.<br>");
        out.println("<img src=\"data:image/jpg;base64," + sourceDirQrCode + "\">");
        out.println();
        out.println("Destination directory " + request.getParameter("destinationDirectory") + " remembered.<br>");
        out.println("<img src=\"data:image/jpg;base64," + destDirQrCode + "\">");
*/        
        out.println("</body>");
        out.println("</html>");

        out.close();
        
        

	}

}
