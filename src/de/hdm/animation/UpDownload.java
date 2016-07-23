package de.hdm.animation;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpDownload extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void assignSessionUser(HttpServletRequest request)
            throws ServletException, IOException {
        
        if (request.getSession().getAttribute("user") != null){
            return;
        }

        String smartphone = request.getParameter("smartphone");
        if (smartphone == null & request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("smartphone")) {
                    smartphone = cookie.getValue();
                    break;
                }
            }
        }
        
        if (smartphone == null) {
            smartphone = new BigInteger(130, new SecureRandom()).toString();
        }
        
        request.getSession().setAttribute("user", new User(smartphone));
    }
}
