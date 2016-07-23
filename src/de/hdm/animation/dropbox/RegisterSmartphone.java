package de.hdm.animation.dropbox;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";

        out.println(docType);
        out.println("<html><body style=\"font-size:6vw\">");

        out.println("<h2>Authorize Access to HdM's Flying Docs</h2>");
        out.println("<form action=\"StoreAccessToken\" method=\"post\">");
        out.println("<a href=\"" + authorizeUrl + "\" TARGET=\"_blank\">First copy Dropbox authorization code</a>");
        
        if (direction != null) {
            out.println("<input type=\"hidden\" name=\"direction\" value=\"" + direction + "\" />");
        }
        out.println("<input type=\"hidden\" name=\"smartphone\" value=\"" + smartphone + "\" />");
        
        out.println("<p>Then paste Code here:</p> <input type=\"text\" name=\"code\" style=\"font-size: 6vw\" />");

        out.print("<p>Remember me: ");
        out.print("<input type=\"checkbox\" name=\"rememberme\" value=\"true\" style=\"width:6vw;height:6vw\" />");
        out.println(" </p>");
        
        out.println("<input type=\"submit\" name=\"submit\" value=\"Submit\" style=\"font-size: 6vw\" /> </p>");
        out.println("</form>");

        out.println("</body>");
        out.println("</html>");

        out.close();

    }

    // -------------------------------------------------------------------------------------------

}
