package de.hdm.animation.dropbox;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
@WebServlet("/StoreAccessTokenLocally")
public class StoreAccessTokenLocally extends DropboxAccessServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {// -------------------------------------------------------------------------------------------
        // GET /StoreAccessTokenLocally
        // -------------------------------------------------------------------------------------------
        // The Dropbox API authorization page will redirect the user's browser
        // to this page.
        //
        // This is a GET (even though it modifies state) because we get here via
        // a browser
        // redirect (Dropbox redirects the user here). You can't do a browser
        // redirect to
        // an HTTP POST.

        DbxAuthFinish authFinish;
        try {
            authFinish = getWebAuth(request).finishFromRedirect(getRedirectUri(request), getSessionStore(request),
                    request.getParameterMap());
        } catch (DbxWebAuth.BadRequestException e) {
            System.out.println("On /dropbox-auth-finish: Bad request: " + e.getMessage());
            response.sendError(400);
            return;
        } catch (DbxWebAuth.BadStateException e) {
            // Send them back to the start of the auth flow.
            response.sendRedirect(getUrl(request, "/dropbox-auth-start"));
            return;
        } catch (DbxWebAuth.CsrfException e) {
            System.out.println("On /dropbox-auth-finish: CSRF mismatch: " + e.getMessage());
            response.sendError(403);
            return;
        } catch (DbxWebAuth.NotApprovedException e) {
            //common.page(response, 200, "Not approved?", "Why not, bro?");
            return;
        } catch (DbxWebAuth.ProviderException e) {
            System.out.println("On /dropbox-auth-finish: Auth failed: " + e.getMessage());
            response.sendError(503, "Error communicating with Dropbox.");
            return;
        } catch (DbxException e) {
            System.out.println("On /dropbox-auth-finish: Error getting token: " + e);
            response.sendError(503, "Error communicating with Dropbox.");
            return;
        }

        // We have an Dropbox API access token now. This is what will let us
        // make Dropbox API
        // calls. Save it in the database entry for the current user.
        User user = Common.assignUserInHttpSession(request);
        user.setToken(authFinish.getAccessToken());

        try {
            new Dropbox(user.getToken(), null).showFiles();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        
        response.sendRedirect("/EnterShareSpace");
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
