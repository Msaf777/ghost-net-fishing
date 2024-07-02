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
 * Unit tests for the {@link RangeValidator} class.
 */
@ExtendWith(MockitoExtension.class)
class RangeValidatorTest {

    @Mock
    private FacesContext facesContext;

    @Mock
    private UIComponent component;

    @InjectMocks
    private RangeValidator rangeValidator;

    @BeforeEach
    void setUp() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("minValue", 1.0);
        attributes.put("maxValue", 10.0);
        attributes.put("label", "Test Label");

        when(component.getAttributes()).thenReturn(attributes);
    }

    /**
     * Tests that validation passes when the value is within the specified range.
     */
    @Test
    void whenValueIsWithinRange_thenValidationPasses() {
        assertDoesNotThrow(() -> rangeValidator.validate(facesContext, component, 5.0));
    }

    /**
     * Tests that validation fails when the value is below the specified range.
     */
    @Test
    void whenValueIsBelowRange_thenValidationFails() {
        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> rangeValidator.validate(facesContext, component, 0.5));

        assertEquals("Test Label wert ist ungültig!", exception.getFacesMessage().getSummary());
    }

    /**
     * Tests that validation fails when the value is above the specified range.
     */
    @Test
    void whenValueIsAboveRange_thenValidationFails() {
        ValidatorException exception = assertThrows(ValidatorException.class,
                () -> rangeValidator.validate(facesContext, component, 10.5));

        assertEquals("Test Label wert ist ungültig!", exception.getFacesMessage().getSummary());
    }

    /**
     * Tests that validation passes when the value is at the minimum of the specified range.
     */
    @Test
    void whenValueIsAtMinRange_thenValidationPasses() {
        assertDoesNotThrow(() -> rangeValidator.validate(facesContext, component, 1.0));
    }

    /**
     * Tests that validation passes when the value is at the maximum of the specified range.
     */
    @Test
    void whenValueIsAtMaxRange_thenValidationPasses() {
        assertDoesNotThrow(() -> rangeValidator.validate(facesContext, component, 10.0));
    }


}