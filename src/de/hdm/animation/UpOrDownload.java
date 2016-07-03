package de.hdm.animation;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UpOrDownload
 */
@WebServlet("/UpOrDownload")
public class UpOrDownload extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = null;
        String direction = request.getParameter("direction");
        if (direction == null) {
            direction = "download";
        }
        String smartphone = request.getParameter("smartphone");
        if (smartphone == null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("smartphone")) {
                    smartphone = cookie.getValue();
                    break;
                }
            }
        }

        String redirectString = "/DirectoryAnimation/RegisterSmartphone?direction=" + direction + "&smartphone=";
        if (smartphone != null) {
            user = new User(smartphone);
            if (user.hasToken()) {
                if (direction.equals("download")) {
                    ShareSpace.instance().downloadUserDropbox(user);
                }
                if (direction.equals("upload")) {
                    ShareSpace.instance().uploadUserDropbox(user);
                }
                response.sendRedirect("/DirectoryAnimation/FlyingDocs");
            } else {
                response.sendRedirect(redirectString + smartphone);
            }
        } else {
            response.sendRedirect(redirectString + new BigInteger(130, new SecureRandom()).toString());
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
