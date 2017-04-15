package de.hdm.animation.dropbox;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWebAuth;

import de.hdm.animation.Common;
import de.hdm.animation.User;

/**
 * Servlet implementation class StoreAccessTokenLocally
 */
@WebServlet("/StoreAccessTokenForFileTransfer")
public class StoreAccessTokenForFileTransfer extends DropboxAccessServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {// -------------------------------------------------------------------------------------------
        // POST /StoreAccessTokenLocally
        // -------------------------------------------------------------------------------------------
        // this includes the access token pasted from the dropbox authorization
        // page

        String code = request.getParameter("code").trim();
        String smartphone = request.getParameter("smartphone");
        String direction = request.getParameter("direction");
        if (request.getParameter("rememberme") != null) {
            response.addCookie(new Cookie("smartphone", smartphone));
        }

        DbxWebAuth webAuth = this.getWebAuth(request);
        DbxAuthFinish authFinish = null;
        try {
            authFinish = webAuth.finishFromCode(code);
        } catch (DbxException ex) {
            System.err.println("Error in DbxWebAuth.authorize: " + ex.getMessage());
            // System.exit(1);
            // return;
        }

        if (authFinish != null) {
            System.out.println("Authorization complete.");
            System.out.println("- User ID: " + authFinish.getUserId());
            System.out.println("- Access Token: " + authFinish.getAccessToken());

            RegisterSmartphoneQR.bluetoothAddressRegistered(smartphone);

            // We have an Dropbox API access token now. This is what will let us
            // make Dropbox API calls. Save it in the database entry for the
            // current user.

            User user = Common.assignUserInHttpSession(request);
            user.setToken(authFinish.getAccessToken());
        }

        if (direction != null && direction.equals("upload")) {
            response.sendRedirect("/DirectoryAnimation/Upload");
        } else {
            response.sendRedirect("/DirectoryAnimation/Download");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
