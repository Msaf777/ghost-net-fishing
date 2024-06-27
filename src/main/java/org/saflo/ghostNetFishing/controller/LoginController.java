package org.saflo.ghostNetFishing.controller;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.saflo.ghostNetFishing.model.DAO.PersonDAO;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.util.SessionUtil;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Controller for login management in the GhostNetFishing application.
 * This class is responsible for user authentication.
 */
@Named
@ViewScoped
public class LoginController implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(GhostNetController.class.getName());

    private PersonDAO personDAO;

    private String name;
    private String password;

    private boolean stayAnonymous;

    @Inject
    public LoginController(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    public LoginController() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * Authenticates the user and redirects to the appropriate page.
     *
     * @return the target page after authentication.
     */
    public String login() {
        LOGGER.info("The User: " + name + " attempt to Login");
        Person user = personDAO.findPersonByNameAndPassword(name.toLowerCase(), password);
        if (user == null) {
            FacesMessage message = new FacesMessage(
                    "Falsche Angabe",
                    "Der eingegebene Name oder die Password ist falsch. Bitte überprüfen Sie Ihre Eingaben und versuchen Sie es erneut.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        SessionUtil.setLoggedInPerson(user);
        return "home.xhtml?faces-redirect=true";

    }

    /**
     * Logs the user out and redirects to the login page.
     *
     * @return the target page after logout.
     */
    public String logout() {
        SessionUtil.logout();
        return "login.xhtml?faces-redirect=true";
    }
}


