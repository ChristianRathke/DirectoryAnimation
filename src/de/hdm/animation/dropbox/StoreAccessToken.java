package de.hdm.animation.dropbox;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWebAuth;

import de.hdm.animation.User;

/**
 * Servlet implementation class StoreAccessTokenLocally
 */
@WebServlet("/StoreAccessToken")
public class StoreAccessToken extends DropboxAccessServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {// -------------------------------------------------------------------------------------------
        // POST /StoreAccessTokenLocally
        // -------------------------------------------------------------------------------------------
        // this includes the access token pasted from the dropbox authorization page

        String code = request.getParameter("code").trim();
        String smartphone = request.getParameter("smartphone");
        
        DbxWebAuth webAuth = this.getWebAuth(request);
        DbxAuthFinish authFinish;
        try {
            authFinish = webAuth.finishFromCode(code);
        } catch (DbxException ex) {
            System.err.println("Error in DbxWebAuth.authorize: " + ex.getMessage());
            //System.exit(1);
            return;
        }

        System.out.println("Authorization complete.");
        System.out.println("- User ID: " + authFinish.getUserId());
        System.out.println("- Access Token: " + authFinish.getAccessToken());


        // We have an Dropbox API access token now. This is what will let us
        // make Dropbox API
        // calls. Save it in the database entry for the current user.
        User user = new User(smartphone, authFinish.getAccessToken());
        user.saveUsers();
        
        RegisterSmartphoneQR.bluetoothAddressRegistered(smartphone);

//        try {
//            new Dropbox(user.getToken(), null).showFiles();
//        } catch (DbxException e) {
//            e.printStackTrace();
//        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";

        out.println(docType);
        out.println("<html><body>");

        out.println("<h1>Authorization Complete</h1>");
        
        out.println("</body>");
        out.println("</html>");

        out.close();
    }

    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
