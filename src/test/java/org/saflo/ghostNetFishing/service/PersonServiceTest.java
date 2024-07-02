package org.saflo.ghostNetFishing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.saflo.ghostNetFishing.model.DAO.PersonDAO;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.model.enums.PersonType;
import org.saflo.ghostNetFishing.util.SessionUtil;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the PersonService class.
 */
@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonDAO personDAO;

    @InjectMocks
    private PersonService personService;


    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person("John Doe", "1234567890", PersonType.REPORTER, "password");
    }

    /**
     * Tests the registerPerson method to ensure a new person is registered and logged in.
     */
    @Test
    void whenRegisterPerson_thenPersonIsRegisteredAndLoggedIn() {
        // Arrange
        MockedStatic<SessionUtil> sessionUtilMockedStatic = mockStatic(SessionUtil.class);
        doNothing().when(personDAO).addPerson(any(Person.class));
        sessionUtilMockedStatic.when(() -> SessionUtil.setLoggedInPerson(any(Person.class))).thenAnswer(invocation -> {
            person = invocation.getArgument(0);
            return null;
        });

        // Act
        personService.registerPerson("John Doe", "1234567890", "password", PersonType.REPORTER);

        // Assert
        verify(personDAO, times(1)).addPerson(any(Person.class));
        sessionUtilMockedStatic.verify(() -> SessionUtil.setLoggedInPerson(any(Person.class)), times(1));
        sessionUtilMockedStatic.close();
    }

    /**
     * Tests the isNameAvailable method to ensure it returns true if the name is available.
     */
    @Test
    void whenIsNameAvailable_thenReturnTrueIfNameIsAvailable() {
        // Arrange
        when(personDAO.findPersonByName("John Doe")).thenReturn(null);

        // Act
        boolean result = personService.isNameAvailable("John Doe");

        // Assert
        assertTrue(result);
    }

    /**
     * Tests the isNameAvailable method to ensure it returns false if the name is not available.
     */
    @Test
    void whenIsNameAvailable_thenReturnFalseIfNameIsNotAvailable() {
        // Arrange
        when(personDAO.findPersonByName("John Doe")).thenReturn(person);

        // Act
        boolean result = personService.isNameAvailable("John Doe");

        // Assert
        assertFalse(result);
    }

    /**
     * Tests the isPhoneNumberAvailable method to ensure it returns true if the phone number is available.
     */
    @Test
    void whenIsPhoneNumberAvailable_thenReturnTrueIfPhoneNumberIsAvailable() {
        // Arrange
        when(personDAO.findPersonByPhone("1234567890")).thenReturn(null);

        // Act
        boolean result = personService.isPhoneNumberAvailable("1234567890");

        // Assert
        assertTrue(result);
    }

    /**
     * Tests the isPhoneNumberAvailable method to ensure it returns false if the phone number is not available.
     */
    @Test
    void whenIsPhoneNumberAvailable_thenReturnFalseIfPhoneNumberIsNotAvailable() {
        // Arrange
        when(personDAO.findPersonByPhone("1234567890")).thenReturn(person);

        // Act
        boolean result = personService.isPhoneNumberAvailable("1234567890");

        // Assert
        assertFalse(result);
    }
}