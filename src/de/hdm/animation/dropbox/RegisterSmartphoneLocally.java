package de.hdm.animation.dropbox;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dropbox.core.DbxWebAuth;

/**
 * Servlet implementation class RegisterSmartphoneLocally
 */
@WebServlet("/RegisterSmartphoneLocally")
public class RegisterSmartphoneLocally extends DropboxAccessServlet {
    private static final long serialVersionUID = 1L;
    

    /**
     * Default constructor.
     */
    public RegisterSmartphoneLocally() {
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

        String smartphone = request.getParameter("smartphone");
        if (smartphone == null) {
            return;
        }

        // Start the authorization process with Dropbox.
        DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
                // After we redirect the user to the Dropbox website for
                // authorization,
                // Dropbox will redirect them back here.
                .withRedirectUri(getRedirectUri(request), getSessionStore(request)).build();
        String authorizeUrl = getWebAuth(request).authorize(authRequest);

        // Redirect the user to the Dropbox website so they can approve our
        // application.
        // The Dropbox website will send them back to /DirectoryAnimation when
        // they're done.
        response.sendRedirect(authorizeUrl);

    }

    // -------------------------------------------------------------------------------------------

    
}
