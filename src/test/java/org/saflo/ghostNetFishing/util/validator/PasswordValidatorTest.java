package org.saflo.ghostNetFishing.util.validator;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the PasswordValidator class.
 */
@ExtendWith(MockitoExtension.class)
public class PasswordValidatorTest {
    @InjectMocks
    private PasswordValidator passwordValidator;

    @Mock
    private FacesContext facesContext;

    @Mock
    private UIComponent component;

    @BeforeEach
    void setUp() {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("label", "Test Label");
        when(component.getAttributes()).thenReturn(attributes);
    }

    /**
     * Tests that the validate method throws a ValidatorException when the password is too short.
     */
    @Test
    void whenPasswordIsTooShort_thenThrowValidatorException() {
        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> passwordValidator.validate(facesContext, component, "Pass1!"));

        assertEquals("Test Label wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Das Passwort muss mindestens 8 Zeichen lang sein.", exception.getFacesMessage().getDetail());
    }

    /**
     * Tests that the validate method throws a ValidatorException when the password does not contain an uppercase letter.
     */
    @Test
    void whenPasswordDoesNotContainUppercase_thenThrowValidatorException() {
        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> passwordValidator.validate(facesContext, component, "password1!"));

        assertEquals("Test Label wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Das Passwort muss mindestens einen Großbuchstaben enthalten.", exception.getFacesMessage().getDetail());
    }

    /**
     * Tests that the validate method throws a ValidatorException when the password does not contain a lowercase letter.
     */
    @Test
    void whenPasswordDoesNotContainLowercase_thenThrowValidatorException() {
        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> passwordValidator.validate(facesContext, component, "PASSWORD1!"));

        assertEquals("Test Label wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Das Passwort muss mindestens einen Kleinbuchstaben enthalten.", exception.getFacesMessage().getDetail());
    }

    /**
     * Tests that the validate method throws a ValidatorException when the password does not contain a digit.
     */
    @Test
    void whenPasswordDoesNotContainDigit_thenThrowValidatorException() {
        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> passwordValidator.validate(facesContext, component, "Password!"));

        assertEquals("Test Label wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Das Passwort muss mindestens eine Zahl enthalten.", exception.getFacesMessage().getDetail());
    }

    /**
     * Tests that the validate method throws a ValidatorException when the password does not contain a special character.
     */
    @Test
    void whenPasswordDoesNotContainSpecialCharacter_thenThrowValidatorException() {
        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> passwordValidator.validate(facesContext, component, "Password1"));

        assertEquals("Test Label wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Das Passwort muss mindestens ein Sonderzeichen enthalten.", exception.getFacesMessage().getDetail());
    }

    /**
     * Tests that the validate method does not throw an exception when the password is valid.
     */
    @Test
    void whenPasswordIsValid_thenValidationPasses() {
        assertDoesNotThrow(() -> passwordValidator.validate(facesContext, component, "Valid10!"));
    }
}