package org.saflo.ghostNetFishing.util;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.saflo.ghostNetFishing.model.Entity.Person;

import java.io.Serializable;
import java.util.logging.Logger;

@Named
@SessionScoped
public class SessionUtil implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(SessionUtil.class.getName());

    private static Person loggedInPerson;

    public static Person getLoggedInPerson() {
        return SessionUtil.loggedInPerson;
    }

    public static void setLoggedInPerson(Person loggedInPerson) {
        SessionUtil.loggedInPerson = loggedInPerson;
    }

    public static boolean isLoggedIn() {
        return SessionUtil.loggedInPerson != null;
    }

    public static void logout() {
        LOGGER.info("The User: " + SessionUtil.getLoggedInPerson().getName() + " ist Logout");

        SessionUtil.setLoggedInPerson(null);

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }


}
