package org.saflo.ghostNetFishing.util.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Named;
import org.saflo.ghostNetFishing.service.PersonService;
import org.saflo.ghostNetFishing.util.CDIServiceLocator;

@Named
@FacesValidator("nameValidator")
public class NameValidator implements Validator {
    private static final String START_WITH_LETTER_PATTERN = "^[a-zA-Z].*";
    private static final String NO_WHITESPACE_OR_SPECIAL_CHAR_PATTERN = "^[a-zA-Z0-9]*$";


    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        // Beschaffe den PersonService programmgesteuert
        PersonService personService = CDIServiceLocator.getBean(PersonService.class);

        String name = value.toString();
        String messageSummary = component.getAttributes().get("label") + " wert ist ungültig!";

        if (!personService.isNameAvailable(name)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary, "Der Benutzername ist bereits vergeben. Bitte wählen Sie einen anderen Namen."));
        }

        if (name.length() < 2 || name.length() > 50) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary, "Der Name muss zwischen 2 und 50 Zeichen lang sein."));
        }

        if (!name.matches(START_WITH_LETTER_PATTERN)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary, "Der Name muss mit einem Buchstaben beginnen."));
        }

        if (!name.matches(NO_WHITESPACE_OR_SPECIAL_CHAR_PATTERN)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary, "Der Name darf keine Leer- oder Sonderzeichen enthalten."));
        }
    }

}


