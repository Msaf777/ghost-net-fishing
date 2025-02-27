package org.saflo.ghostNetFishing.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Filter for managing login requirements and protecting pages.
 */
public class LoginFilter implements Filter {


    /**
     * Filters requests and redirects users based on their login status.
     * @param request the ServletRequest object.
     * @param response the ServletResponse object.
     * @param chain the FilterChain object.
     * @throws IOException if an I/O error occurs.
     * @throws ServletException if a servlet error occurs.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String loginURI = req.getContextPath() + "/login.xhtml";
        String addPersonURI = req.getContextPath() + "/addPerson.xhtml";
        String addGhostNetURI = req.getContextPath() + "/addGhostNet.xhtml";

        boolean loggedIn = SessionUtil.isLoggedIn();
        boolean loginRequest = req.getRequestURI().equals(loginURI);
        boolean addPersonRequest = req.getRequestURI().equals(addPersonURI);
        boolean addGhostNetReq = req.getRequestURI().equals(addGhostNetURI);

        // Überprüfe, ob der Benutzer eingeloggt ist
        if(loggedIn && loginRequest) {
            // Benutzer ist eingeloggt, und will zur Login-Seite, leite zu Startseite
            res.sendRedirect(req.getContextPath() + "/home.xhtml");
        } else if (!loggedIn && (!loginRequest && !addPersonRequest && !addGhostNetReq)) {
            // Benutzer ist nicht eingeloggt und will sich nicht registrieren oder keine neue GhostNetz melden, leite zur Login-Seite weiter
            res.sendRedirect(loginURI);
        } else {
            // Benutzer ist nicht eingeloggt, setze die Filterkette fort
            chain.doFilter(request, response);
        }
    }

}
