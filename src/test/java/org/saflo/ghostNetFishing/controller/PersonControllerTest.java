package org.saflo.ghostNetFishing.controller;

import jakarta.faces.application.FacesMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.saflo.ghostNetFishing.model.enums.PersonType;
import org.saflo.ghostNetFishing.service.FlashMessageService;
import org.saflo.ghostNetFishing.service.PersonService;
import org.saflo.ghostNetFishing.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link PersonController} class.
 */
@ExtendWith(MockitoExtension.class)
class PersonControllerTest {

    @Mock
    private PersonService personService;

    @Mock
    private FlashMessageService flashMessageService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PersonController personController;

    @BeforeEach
    void setUp() {
        personController = new PersonController(personService, flashMessageService, userService);
        personController.setName("Test User");
        personController.setPhoneNumber("1234567890");
        personController.setPassword("Password123!");
        personController.setType(PersonType.REPORTER);
    }

    /**
     * Tests that a person is successfully registered and a flash message is added.
     */
    @Test
    void whenRegister_thenPersonIsRegisteredAndFlashMessageIsAdded() {
        // Arrange
        String expectedMessage = "Willkommen: Test User";
        when(userService.getLoggedInPersonName()).thenReturn("Test User");

        // Act
        String result = personController.register();

        // Assert
        verify(personService).registerPerson("Test User", "1234567890", "Password123!", PersonType.REPORTER);
        ArgumentCaptor<FacesMessage.Severity> severityCaptor = ArgumentCaptor.forClass(FacesMessage.Severity.class);
        ArgumentCaptor<String> summaryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detailCaptor = ArgumentCaptor.forClass(String.class);
        verify(flashMessageService).addFlashMessage(severityCaptor.capture(), summaryCaptor.capture(), detailCaptor.capture());

        assertEquals(FacesMessage.SEVERITY_INFO, severityCaptor.getValue());
        assertEquals("Erfolgreich registriert", summaryCaptor.getValue());
        assertEquals(expectedMessage, detailCaptor.getValue());
        assertEquals("home?faces-redirect=true", result);
    }

    /**
     * Tests if the user is logged in.
     */
    @Test
    void whenIsLoggedIn_thenReturnTrue() {
        when(userService.isLoggedIn()).thenReturn(true);

        assertTrue(personController.isLoggedIn());
        verify(userService).isLoggedIn();
    }

    /**
     * Tests if the logged-in person is a recoverer.
     */
    @Test
    void whenIsLoggedInPersonRecoverer_thenReturnTrue() {
        when(userService.isLoggedInPersonRecoverer()).thenReturn(true);

        assertTrue(personController.isLoggedInPersonRecoverer());
        verify(userService).isLoggedInPersonRecoverer();
    }

    /**
     * Tests if the logged-in person is a reporter.
     */
    @Test
    void whenIsLoggedInPersonReporter_thenReturnTrue() {
        when(userService.isLoggedInPersonReporter()).thenReturn(true);

        assertTrue(personController.isLoggedInPersonReporter());
        verify(userService).isLoggedInPersonReporter();
    }

    /**
     * Tests if the user is a reporter and logged in.
     */
    @Test
    void whenIsPersonReporterAndLoggedIn_thenReturnTrue() {
        when(userService.isPersonReporterAndLoggedIn()).thenReturn(true);

        assertTrue(personController.isPersonReporterAndLoggedIn());
        verify(userService).isPersonReporterAndLoggedIn();
    }

    /**
     * Tests the retrieval of the logged-in person's name.
     */
    @Test
    void whenGetLoggedInPersonName_thenReturnName() {
        String expectedName = "Test User";
        when(userService.getLoggedInPersonName()).thenReturn(expectedName);

        String result = personController.getLoggedInPersonName();

        assertEquals(expectedName, result);
        verify(userService).getLoggedInPersonName();
    }
}