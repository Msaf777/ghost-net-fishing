package org.saflo.ghostNetFishing.service;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.model.enums.PersonType;
import org.saflo.ghostNetFishing.util.SessionUtil;

/**
 * Service for managing user sessions and authentication.
 */
@Named
@ApplicationScoped
public class UserService {

    /**
     * Checks if a user is logged in.
     * @return true if a user is logged in, otherwise false.
     */
    public boolean isLoggedIn() {
        return SessionUtil.isLoggedIn();
    }

    /**
     * @return the name of the logged-in user with the first letter capitalized.
     */
    public String getLoggedInPersonName() {
        Person loggedInPerson = SessionUtil.getLoggedInPerson();
        if (loggedInPerson != null) {
            String firstLetter = loggedInPerson.getName().substring(0, 1).toUpperCase();
            return firstLetter.concat(loggedInPerson.getName().substring(1));
        }
        return null;
    }

    /**
     * @return true if the logged-in user is a Recoverer, otherwise false.
     */
    public boolean isLoggedInPersonRecoverer() {
        Person loggedInPerson = SessionUtil.getLoggedInPerson();
        return loggedInPerson != null && loggedInPerson.getType().equals(PersonType.RECOVERER);
    }

    /**
     * @return true if the logged-in user is a Reporter, otherwise false.
     */
    public boolean isLoggedInPersonReporter() {
        Person loggedInPerson = SessionUtil.getLoggedInPerson();
        return loggedInPerson != null && loggedInPerson.getType().equals(PersonType.REPORTER);
    }

    /**
     * @return true if the user is a Reporter and is logged in, otherwise false.
     */
    public boolean isPersonReporterAndLoggedIn() {
        return isLoggedIn() && isLoggedInPersonReporter();
    }
}
