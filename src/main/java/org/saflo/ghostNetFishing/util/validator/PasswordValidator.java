package org.saflo.ghostNetFishing.util.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String password = value.toString();
        String messageSummary = component.getAttributes().get("label") + " wert ist ungültig!";

        if (password.length() < 8) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary,"Das Passwort muss mindestens 8 Zeichen lang sein."));
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary,"Das Passwort muss mindestens einen Großbuchstaben enthalten."));
        }

        if (!password.matches(".*[a-z].*")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary,"Das Passwort muss mindestens einen Kleinbuchstaben enthalten."));
        }

        if (!password.matches(".*\\d.*")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary,"Das Passwort muss mindestens eine Zahl enthalten."));
        }

        if (!password.matches(".*\\p{Punct}.*")) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary,"Das Passwort muss mindestens ein Sonderzeichen enthalten."));
        }
    }

}
