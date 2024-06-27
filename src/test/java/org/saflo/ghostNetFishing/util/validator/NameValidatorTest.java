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
import org.mockito.junit.jupiter.MockitoExtension;
import org.saflo.ghostNetFishing.service.PersonService;
import org.saflo.ghostNetFishing.util.CDIServiceLocator;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;


/**
 * Unit tests for the NameValidator class.
 */
@ExtendWith(MockitoExtension.class)
class NameValidatorTest {
    @InjectMocks
    private NameValidator nameValidator;

    @Mock
    private FacesContext facesContext;

    @Mock
    private UIComponent component;

    @Mock
    private PersonService personService;

    private MockedStatic<CDIServiceLocator> cdiServiceLocatorMockedStatic;

    @BeforeEach
    void setUp() {
        cdiServiceLocatorMockedStatic = mockStatic(CDIServiceLocator.class);
        cdiServiceLocatorMockedStatic.when(() -> CDIServiceLocator.getBean(PersonService.class)).thenReturn(personService);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("label", "Name");
        when(component.getAttributes()).thenReturn(attributes);
    }

    @AfterEach
    void tearDown() {
        cdiServiceLocatorMockedStatic.close();
    }

    /**
     * Tests that validate method throws ValidatorException when the name is already taken.
     */
    @Test
    void whenNameIsNotAvailable_thenThrowValidatorException() {
        when(personService.isNameAvailable("existingUser")).thenReturn(false);
        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> nameValidator.validate(facesContext, component, "existingUser"));
        assertEquals("Name wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Der Benutzername ist bereits vergeben. Bitte wählen Sie einen anderen Namen.", exception.getFacesMessage().getDetail());
    }
    /**
     * Tests that the validate method throws a ValidatorException when the name is too short.
     */
    @Test
    void whenNameIsTooShort_thenThrowValidatorException() {
        when(personService.isNameAvailable("a")).thenReturn(true);

        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> nameValidator.validate(facesContext, component, "a"));

        assertEquals("Name wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Der Name muss zwischen 2 und 50 Zeichen lang sein.", exception.getFacesMessage().getDetail());
    }

    /**
     * Tests that the validate method throws a ValidatorException when the name is too long.
     */
    @Test
    void whenNameIsTooLong_thenThrowValidatorException() {
        String longName = "a".repeat(51);
        when(personService.isNameAvailable(longName)).thenReturn(true);

        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> nameValidator.validate(facesContext, component, longName));

        assertEquals("Name wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Der Name muss zwischen 2 und 50 Zeichen lang sein.", exception.getFacesMessage().getDetail());
    }

    /**
     * Tests that the validate method throws a ValidatorException when the name does not start with a letter.
     */
    @Test
    void whenNameDoesNotStartWithLetter_thenThrowValidatorException() {
        when(personService.isNameAvailable("1name")).thenReturn(true);

        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> nameValidator.validate(facesContext, component, "1name"));

        assertEquals("Name wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Der Name muss mit einem Buchstaben beginnen.", exception.getFacesMessage().getDetail());
    }

    /**
     * Tests that the validate method throws a ValidatorException when the name contains whitespace or special characters.
     */
    @Test
    void whenNameContainsWhitespaceOrSpecialCharacters_thenThrowValidatorException() {
        when(personService.isNameAvailable("na me")).thenReturn(true);

        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> nameValidator.validate(facesContext, component, "na me"));

        assertEquals("Name wert ist ungültig!", exception.getFacesMessage().getSummary());
        assertEquals("Der Name darf keine Leer- oder Sonderzeichen enthalten.", exception.getFacesMessage().getDetail());
    }

    /**
     * Tests that the validate method does not throw an exception when the name is valid.
     */
    @Test
    void whenNameIsValid_thenValidationPasses() {
        when(personService.isNameAvailable("validName")).thenReturn(true);

        assertDoesNotThrow(() -> nameValidator.validate(facesContext, component, "validName"));
    }
}