package org.saflo.ghostNetFishing.util;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.saflo.ghostNetFishing.model.Entity.Person;

import java.io.Serializable;
import java.util.logging.Logger;


/**
 * Utility class for managing the user session.
 */
@Named
@SessionScoped
public class SessionUtil implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(SessionUtil.class.getName());

    private static Person loggedInPerson;

    /**
     * @return the logged-in person.
     */
    public static Person getLoggedInPerson() {
        return SessionUtil.loggedInPerson;
    }


    /**
     * Sets the logged-in person.
     * @param loggedInPerson the logged-in person.
     */
    public static void setLoggedInPerson(Person loggedInPerson) {
        SessionUtil.loggedInPerson = loggedInPerson;
    }

    /**
     * @return true if a user is logged in, otherwise false.
     */
    public static boolean isLoggedIn() {
        return SessionUtil.loggedInPerson != null;
    }

    /**
     * Logs the user out and invalidates the session.
     */
    public static void logout() {
        LOGGER.info("The User: " + SessionUtil.getLoggedInPerson().getName() + " ist Logout");

        SessionUtil.setLoggedInPerson(null);

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }


}
