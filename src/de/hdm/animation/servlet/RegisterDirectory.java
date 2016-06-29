package de.hdm.animation.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet receives a directory name as a parameter and puts it into a
 * cookie for later reference.
 */
@WebServlet("/RegisterDirectory")
public class RegisterDirectory extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public RegisterDirectory() {
        // TODO Auto-generated constructor stub
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

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
        out.println(docType);
        out.println("<html><body>");

        String directory = request.getParameter("directory");
        if (directory == null || directory.equals("")) {
            out.println("<h2>Specify Remote Directory</h2>");
            out.println("<form action=\"RegisterDirectory\" method=\"post\">");
            out.println("<p>Remote Directory: <input type=\"text\" name=\"directory\" size=\"60\"/></p>");
            out.println("<input type=\"submit\" name=\"submit\" value=\"Submit\">");
            out.println("</form>");
        } else {
            response.addCookie(new Cookie("directory", directory));

            out.println("Directory " + directory + " remembered on your device.<br><br>");
            out.println("<form action=\"EnterShareSpace\" method=\"Post\">");
            out.println("Sharing folder: <input type=\"submit\" name=\"directory\" value=\"" + directory
                    + "\">");
            out.println("</form>");
        }

        out.println("</body>");
        out.println("</html>");

        out.close();

    }

}
