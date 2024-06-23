package org.saflo.ghostNetFishing.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

import java.util.logging.Logger;

@FacesValidator("rangeValidator")
public class RangeValidator implements Validator {

    private static final Logger LOGGER  = Logger.getLogger(RangeValidator.class.getName());

    @Override
    public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        double minValue = Double.parseDouble(component.getAttributes().get("minValue").toString());
        double maxValue = Double.parseDouble(component.getAttributes().get("maxValue").toString());

        LOGGER.info("MinValue= " + minValue + "\n maxValue: " + maxValue + "\n value: " + value);

        double numericValue  = Double.parseDouble(value.toString()) ;

        if(numericValue < minValue || numericValue > maxValue) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, component.getAttributes().get("label") + " wert ist ungültig!",
                    "Geben Sie ein Dezimalzahl zwischen " + minValue + " und " + maxValue));
        }

    }
}
