package org.saflo.ghostNetFishing.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.model.enums.PersonType;
import org.saflo.ghostNetFishing.util.SessionUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

/**
 * Unit tests for the UserService class.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;

    private MockedStatic<SessionUtil> sessionUtilMockedStatic;

    private Person mockPerson;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        sessionUtilMockedStatic = mockStatic(SessionUtil.class);

        mockPerson = new Person("john", "1234567890", PersonType.RECOVERER, "password");
    }

    @AfterEach
    void tearDown() {
        sessionUtilMockedStatic.close();
    }

    /**
     * Tests that the isLoggedIn method returns true when the user is logged in.
     */
    @Test
    void whenIsLoggedIn_thenReturnTrueIfLoggedIn() {
        // Arrange
        sessionUtilMockedStatic.when(SessionUtil::isLoggedIn).thenReturn(true);

        // Act
        boolean result = userService.isLoggedIn();

        // Assert
        assertTrue(result);
        sessionUtilMockedStatic.verify(SessionUtil::isLoggedIn, times(1));
    }

    /**
     * Tests that the isLoggedIn method returns false when the user is not logged in.
     */
    @Test
    void whenIsLoggedIn_thenReturnFalseIfNotLoggedIn() {
        // Arrange
        sessionUtilMockedStatic.when(SessionUtil::isLoggedIn).thenReturn(false);

        // Act
        boolean result = userService.isLoggedIn();

        // Assert
        assertFalse(result);
        sessionUtilMockedStatic.verify(SessionUtil::isLoggedIn, times(1));
    }

    /**
     * Tests that the getLoggedInPersonName method returns the capitalized name of the logged-in person.
     */
    @Test
    void whenGetLoggedInPersonName_thenReturnCapitalizedName() {
        // Arrange
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(mockPerson);

        // Act
        String result = userService.getLoggedInPersonName();

        // Assert
        assertEquals("John", result);
        sessionUtilMockedStatic.verify(SessionUtil::getLoggedInPerson, times(1));
    }

    /**
     * Tests that the getLoggedInPersonName method returns null when there is no logged-in person.
     */
    @Test
    void whenGetLoggedInPersonName_thenReturnNullIfNoLoggedInPerson() {
        // Arrange
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(null);

        // Act
        String result = userService.getLoggedInPersonName();

        // Assert
        assertNull(result);
        sessionUtilMockedStatic.verify(SessionUtil::getLoggedInPerson, times(1));
    }

    /**
     * Tests that the isLoggedInPersonRecoverer method returns true if the logged-in person is a Recoverer.
     */
    @Test
    void whenIsLoggedInPersonRecoverer_thenReturnTrueIfPersonIsRecoverer() {
        // Arrange
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(mockPerson);

        // Act
        boolean result = userService.isLoggedInPersonRecoverer();

        // Assert
        assertTrue(result);
        sessionUtilMockedStatic.verify(SessionUtil::getLoggedInPerson, times(1));
    }

    /**
     * Tests that the isLoggedInPersonRecoverer method returns false if the logged-in person is not a Recoverer.
     */
    @Test
    void whenIsLoggedInPersonRecoverer_thenReturnFalseIfPersonIsNotRecoverer() {
        // Arrange
        mockPerson.setType(PersonType.REPORTER);
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(mockPerson);

        // Act
        boolean result = userService.isLoggedInPersonRecoverer();

        // Assert
        assertFalse(result);
        sessionUtilMockedStatic.verify(SessionUtil::getLoggedInPerson, times(1));
    }

    /**
     * Tests that the isLoggedInPersonRecoverer method returns false when there is no logged-in person.
     */
    @Test
    void whenIsLoggedInPersonRecoverer_thenReturnFalseIfNoLoggedInPerson() {
        // Arrange
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(null);

        // Act
        boolean result = userService.isLoggedInPersonRecoverer();

        // Assert
        assertFalse(result);
        sessionUtilMockedStatic.verify(SessionUtil::getLoggedInPerson, times(1));
    }

    /**
     * Tests that the isLoggedInPersonReporter method returns true if the logged-in person is a Reporter.
     */
    @Test
    void whenIsLoggedInPersonReporter_thenReturnTrueIfPersonIsReporter() {
        // Arrange
        mockPerson.setType(PersonType.REPORTER);
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(mockPerson);

        // Act
        boolean result = userService.isLoggedInPersonReporter();

        // Assert
        assertTrue(result);
        sessionUtilMockedStatic.verify(SessionUtil::getLoggedInPerson, times(1));
    }

    /**
     * Tests that the isLoggedInPersonReporter method returns false if the logged-in person is not a Reporter.
     */
    @Test
    void whenIsLoggedInPersonReporter_thenReturnFalseIfPersonIsNotReporter() {
        // Arrange
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(mockPerson);

        // Act
        boolean result = userService.isLoggedInPersonReporter();

        // Assert
        assertFalse(result);
        sessionUtilMockedStatic.verify(SessionUtil::getLoggedInPerson, times(1));
    }

    /**
     * Tests that the isLoggedInPersonReporter method returns false when there is no logged-in person.
     */
    @Test
    void whenIsLoggedInPersonReporter_thenReturnFalseIfNoLoggedInPerson() {
        // Arrange
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(null);

        // Act
        boolean result = userService.isLoggedInPersonReporter();

        // Assert
        assertFalse(result);
        sessionUtilMockedStatic.verify(SessionUtil::getLoggedInPerson, times(1));
    }
}