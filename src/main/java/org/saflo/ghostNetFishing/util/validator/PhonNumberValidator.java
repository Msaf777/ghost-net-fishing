package org.saflo.ghostNetFishing.util.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import org.saflo.ghostNetFishing.service.PersonService;
import org.saflo.ghostNetFishing.util.CDIServiceLocator;

@FacesValidator("phoneNumberValidator")
public class PhonNumberValidator implements Validator {
    private static final String PHONE_PATTERN = "^[0-9]{10,15}$";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        PersonService personService = CDIServiceLocator.getBean(PersonService.class);

        String phoneNumber = value.toString();
        String messageSummary = component.getAttributes().get("label") + " wert ist ungültig!";
        if(!personService.isPhoneNumberAvailable(phoneNumber)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary,"Die Telefonnummer ist bereits vergeben. Bitte wählen Sie eine andere Nummer."));
        }
        if (!phoneNumber.matches(PHONE_PATTERN)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary,"Die Telefonnummer muss nur aus Zahlen bestehen und zwischen 10 und 15 Ziffern lang sein."));
        }
    }



}
