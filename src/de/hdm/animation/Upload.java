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
@WebServlet("/Upload")
public class Upload extends UpDownload {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        assignSessionUser(request);
        User user = (User) request.getSession().getAttribute("user");

        if (user.hasToken()) {
            ShareSpace.instance().uploadUserDropbox(user);
            response.sendRedirect("/DirectoryAnimation/FlyingDocs");
        } else {
            response.sendRedirect(
                    "/DirectoryAnimation/RegisterSmartphone?smartphone=" + user.getDeviceId() + "&direction=upload");
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
