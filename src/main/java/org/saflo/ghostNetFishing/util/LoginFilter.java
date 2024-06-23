package org.saflo.ghostNetFishing.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Filter zur Verwaltung von Login-Anforderungen und zum Schutz von Seiten.
 */
public class LoginFilter implements Filter {


    /**
     * Filtert Anfragen und leitet Benutzer basierend auf ihrem Login-Status weiter.
     * @param request das ServletRequest-Objekt.
     * @param response das ServletResponse-Objekt.
     * @param chain das FilterChain-Objekt.
     * @throws IOException wenn ein IO-Fehler auftritt.
     * @throws ServletException wenn ein Servlet-Fehler auftritt.
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
