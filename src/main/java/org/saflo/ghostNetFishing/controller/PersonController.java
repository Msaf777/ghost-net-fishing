package org.saflo.ghostNetFishing.controller;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.saflo.ghostNetFishing.model.enums.PersonType;
import org.saflo.ghostNetFishing.service.FlashMessageService;
import org.saflo.ghostNetFishing.service.PersonService;
import org.saflo.ghostNetFishing.service.UserService;

import java.io.Serializable;

/**
 * Controller for managing persons in the GhostNetFishing application.
 * This class is responsible for user registration and management.
 */
@Named
@ViewScoped
public class PersonController implements Serializable {

    private PersonService personService;

    private FlashMessageService flashMessageService;

    private UserService userService;

    private String name;
    private String phoneNumber;
    private String password;
    private PersonType type;

    @Inject
    public PersonController(PersonService personService, FlashMessageService flashMessageService, UserService userService) {
        this.personService = personService;
        this.flashMessageService = flashMessageService;
        this.userService = userService;
    }

    public PersonController(){}


    // Getter und Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public PersonType getType() { return type; }
    public void setType(PersonType type) { this.type = type; }

    /**
     * Registers a new person and saves them in the database.
     * Uses Flash-Scoped to store the PrimeFaces message globally,
     * so that the message is displayed to the user after successful registration
     * despite automatic redirection to the home page.
     * @return the target page after registration.
     */
    public String register() {
            personService.registerPerson(name, phoneNumber, password, type);
            // Flash-Scope um Nachricht in der aktuellen Sitzung zu speichern
            flashMessageService.addFlashMessage( FacesMessage.SEVERITY_INFO ,"Erfolgreich registriert", "Willkommen: " + getLoggedInPersonName());
            return "home?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return userService.isLoggedIn();
    }

    public boolean isLoggedInPersonRecoverer() {
        return userService.isLoggedInPersonRecoverer();
    }

    public boolean isLoggedInPersonReporter() {
        return userService.isLoggedInPersonReporter();
    }

    public String getLoggedInPersonName() {
        return userService.getLoggedInPersonName();
    }

}
