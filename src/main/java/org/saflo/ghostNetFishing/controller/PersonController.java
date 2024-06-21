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
    public void setMessage(String message){this.message = message; }

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

    public String redirectToLogin() {
        return "login?faces-redirect=true";
    }

    private boolean userExists() {
        Person existingUser = personDAO.findPersonByNameAndPhone(name.toLowerCase(), phoneNumber);
        return existingUser != null;
    }

    /**
     * Vermittelt den Aufruf der statischen Methode isLoggedIn() zur Nutzung in JSF-EL.
     */
    public boolean isLoggedIn() {
        return SessionUtil.isLoggedIn();
    }
    

    public boolean isLoggedInPersonRecoverer() {
        return SessionUtil.getLoggedInPerson().getType().equals(PersonType.RECOVERER);
    }

    public boolean isLoggedInPersonReporter() {
        return SessionUtil.getLoggedInPerson().getType().equals(PersonType.REPORTER);
    }

    public boolean isPersonReporterAndLoggedIn() {
        return (SessionUtil.getLoggedInPerson() != null && isLoggedInPersonReporter());
    }


    public String getLoggedInPersonName() {
        String firstLetter = SessionUtil.getLoggedInPerson().getName().substring(0,1).toUpperCase();
        return firstLetter.concat(SessionUtil.getLoggedInPerson().getName().substring(1));
    }

}
