package de.hdm.animation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UpDownload
 */
@WebServlet("/Download")
public class Download extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        
        User user = Common.assignUserInHttpSession(request);

        if (user.hasToken()) {
            ShareSpace.instance().downloadUserDropbox(user);
            response.sendRedirect("/DirectoryAnimation/FlyingDocs");
        } else {
            response.sendRedirect(
                    "/DirectoryAnimation/RegisterSmartphone?smartphone=" + user.getDeviceId() + "&direction=download");
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        doGet(request, response);
    }

}
