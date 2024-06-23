package org.saflo.ghostNetFishing.util;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.saflo.ghostNetFishing.model.Entity.Person;

import java.io.Serializable;
import java.util.logging.Logger;


/**
 * Utility-Klasse für die Verwaltung der Benutzersitzung.
 */
@Named
@SessionScoped
public class SessionUtil implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(SessionUtil.class.getName());

    private static Person loggedInPerson;

    /**
     * @return die eingeloggte Person.
     */
    public static Person getLoggedInPerson() {
        return SessionUtil.loggedInPerson;
    }


    /**
     * Setzt die eingeloggte Person.
     * @param loggedInPerson die eingeloggte Person.
     */
    public static void setLoggedInPerson(Person loggedInPerson) {
        SessionUtil.loggedInPerson = loggedInPerson;
    }


    /**
     * @return true, wenn ein Benutzer eingeloggt ist, ansonsten false.
     */
    public static boolean isLoggedIn() {
        return SessionUtil.loggedInPerson != null;
    }

    /**
     * Loggt den Benutzer aus und invalidiert die Sitzung.
     */
    public static void logout() {
        LOGGER.info("The User: " + SessionUtil.getLoggedInPerson().getName() + " ist Logout");

        SessionUtil.setLoggedInPerson(null);

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }


}
