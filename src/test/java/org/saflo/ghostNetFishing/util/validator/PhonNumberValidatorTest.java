package org.saflo.ghostNetFishing.util.validator;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.saflo.ghostNetFishing.service.PersonService;
import org.saflo.ghostNetFishing.util.CDIServiceLocator;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the PhonNumberValidator class.
 */
@ExtendWith(MockitoExtension.class)
public class PhonNumberValidatorTest {
    @InjectMocks
    private PhonNumberValidator phonNumberValidator;

    @Mock
    private FacesContext facesContext;

    @Mock
    private UIComponent component;

    @Mock
    private PersonService personService;

    private MockedStatic<CDIServiceLocator> mockedCDIServiceLocator;


    @BeforeEach
    void setUp() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("label", "Test Label");
        when(component.getAttributes()).thenReturn(attributes);

        mockedCDIServiceLocator = Mockito.mockStatic(CDIServiceLocator.class);

        mockedCDIServiceLocator.when(() -> CDIServiceLocator.getBean(PersonService.class)).thenReturn(personService);
    }

    @AfterEach
    void tearDown() {
        mockedCDIServiceLocator.close();
    }

    /**
     * Tests that the validate method throws a ValidatorException when the phone number is already taken.
     */
    @Test
    void whenPhoneNumberIsTaken_thenThrowValidatorException() {
        when(personService.isPhoneNumberAvailable("1234567890")).thenReturn(false);

        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> phonNumberValidator.validate(facesContext, component, "1234567890"));

        assertEquals("Test Label wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Die Telefonnummer ist bereits vergeben. Bitte wählen Sie eine andere Nummer.", exception.getFacesMessage().getDetail());
    }

    /**
     * Tests that the validate method throws a ValidatorException when the phone number does not match the pattern.
     */
    @Test
    void whenPhoneNumberDoesNotMatchPattern_thenThrowValidatorException() {
        when(personService.isPhoneNumberAvailable("123")).thenReturn(true);

        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> phonNumberValidator.validate(facesContext, component, "123"));

        assertEquals("Test Label wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Die Telefonnummer muss nur aus Zahlen bestehen und zwischen 10 und 15 Ziffern lang sein.", exception.getFacesMessage().getDetail());
    }

    /**
     * Tests that the validate method does not throw an exception when the phone number is valid and available.
     */
    @Test
    void whenPhoneNumberIsValidAndAvailable_thenValidationPasses() {
        when(personService.isPhoneNumberAvailable("1234567890")).thenReturn(true);

        assertDoesNotThrow(() -> phonNumberValidator.validate(facesContext, component, "1234567890"));
    }
}