package org.saflo.ghostNetFishing.controller;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.saflo.ghostNetFishing.model.DAO.PersonDAO;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.model.enums.PersonType;
import org.saflo.ghostNetFishing.util.SessionUtil;

import java.io.Serializable;

/**
 * Controller for managing persons in the GhostNetFishing application.
 * This class is responsible for user registration and management.
 */
@Named
@ViewScoped
public class PersonController implements Serializable {

    @Inject
    private PersonDAO personDAO;

    private String name;
    private String phoneNumber;
    private PersonType type;

    private String message;

    // Getter und Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public PersonType getType() { return type; }
    public void setType(PersonType type) { this.type = type; }

    public String getMessage() { return message; }

    /**
     * Sets the message for the user.
     * @param message the message for the user.
     */
    public void setMessage(String message){this.message = message; }

    /**
     * Registers a new person and saves them in the database.
     * Uses Flash-Scoped to store the PrimeFaces message globally,
     * so that the message is displayed to the user after successful registration
     * despite automatic redirection to the home page.
     * @return the target page after registration.
     */
    public String register() {
        if (userExists()) {
            message = "Benutzer existiert bereits. Möchten Sie zur Login-Seite weitergeleitet werden?";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warnung", message));
            PrimeFaces.current().executeScript("PF('confirmDialog').show();");
            return null;
        } else {
            Person person = new Person(name.toLowerCase(), phoneNumber, type);
            personDAO.addPerson(person);
            SessionUtil.setLoggedInPerson(person);
            // Flash-Scope um Nachricht in der aktuellen Sitzung zu speichern
            Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
            flash.setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Erfolgreich registriert", "Willkommen: " + getLoggedInPersonName()));
            return "home?faces-redirect=true";
        }
    }


    /**
     * Redirects to the login page.
     * @return the target page for login.
     */
    public String redirectToLogin() {
        return "login?faces-redirect=true";
    }

    /**
     * Checks if a user with the given name and phone number already exists.
     * @return true if the user exists, otherwise false.
     */
    private boolean userExists() {
        Person existingUser = personDAO.findPersonByNameAndPhone(name.toLowerCase(), phoneNumber);
        return existingUser != null;
    }

    /**
     * Mediates the call to the static method isLoggedIn() for use in JSF-EL.
     * @return true if a user is logged in, otherwise false.
     */
    public boolean isLoggedIn() {
        return SessionUtil.isLoggedIn();
    }


    /**
     * @return true if the logged-in user is a Recoverer, otherwise false.
     */
    public boolean isLoggedInPersonRecoverer() {
        return SessionUtil.getLoggedInPerson().getType().equals(PersonType.RECOVERER);
    }

    /**
     * @return true if the logged-in user is a Reporter, otherwise false.
     */
    public boolean isLoggedInPersonReporter() {
        return SessionUtil.getLoggedInPerson().getType().equals(PersonType.REPORTER);
    }

    /**
     * @return true if the user is a Reporter and is logged in, otherwise false.
     */
    public boolean isPersonReporterAndLoggedIn() {
        return (SessionUtil.getLoggedInPerson() != null && isLoggedInPersonReporter());
    }

    /**
     * @return the name of the logged-in user with the first letter capitalized.
     */
    public String getLoggedInPersonName() {
        String firstLetter = SessionUtil.getLoggedInPerson().getName().substring(0,1).toUpperCase();
        return firstLetter.concat(SessionUtil.getLoggedInPerson().getName().substring(1));
    }

}
