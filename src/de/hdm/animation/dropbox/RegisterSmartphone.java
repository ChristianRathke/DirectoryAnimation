package de.hdm.animation.dropbox;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dropbox.core.DbxWebAuth;

/**
 * Servlet implementation class RegisterSmartphoneLocally
 */
@WebServlet("/RegisterSmartphone")
public class RegisterSmartphone extends DropboxAccessServlet {
    private static final long serialVersionUID = 1L;

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

        String smartphone = request.getParameter("smartphone");
        String direction = request.getParameter("direction");
        
        // Start the authorization process with Dropbox.
        DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
                // After we redirect the user to the Dropbox website for
                // authorization,
                .withNoRedirect().build();
        String authorizeUrl = getWebAuth(request).authorize(authRequest);

        response.addCookie(new Cookie("smartphone", smartphone));
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";

        out.println(docType);
        out.println("<html><body>");

        out.println("<h2>Authorize Access to HdM's Flying Docs</h2>");
        out.println("<form action=\"StoreAccessToken\" method=\"post\">");
        out.println("<a href=\"" + authorizeUrl + "\" TARGET=\"_blank\">First get Dropbox authorization code</a>");
        if (direction != null) {
            out.println("<input type=\"hidden\" name=\"direction\" value=\"" + direction + "\" />");
        }
        out.println("<input type=\"hidden\" name=\"smartphone\" value=\"" + smartphone + "\" />");
        out.println("<p>Then paste Code here:<br> <input type=\"text\" name=\"code\" size=\"60\"/></p>");
        out.println("<input type=\"submit\" name=\"submit\" value=\"Submit\">");
        out.println("</form>");

        out.println("</body>");
        out.println("</html>");

        out.close();

    }

    // -------------------------------------------------------------------------------------------

}
