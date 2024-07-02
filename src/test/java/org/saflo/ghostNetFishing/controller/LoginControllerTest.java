package org.saflo.ghostNetFishing.controller;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.saflo.ghostNetFishing.model.DAO.PersonDAO;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.util.SessionUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the {@link LoginController} class.
 */
@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private PersonDAO personDAO;

    @Mock
    private FacesContext facesContext;

    @Captor
    private ArgumentCaptor<FacesMessage> messageCaptor;


    private MockedStatic<FacesContext> facesContextMockedStatic;
    private MockedStatic<SessionUtil> sessionUtilMockedStatic;

    @BeforeEach
    void setUp() {
        facesContextMockedStatic = Mockito.mockStatic(FacesContext.class);

        facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
        sessionUtilMockedStatic = Mockito.mockStatic(SessionUtil.class);
    }

    @AfterEach
    void tearDown() {
        facesContextMockedStatic.close();
        sessionUtilMockedStatic.close();
    }


    /**
     * Tests that the user is redirected to the home page when valid credentials are provided.
     */
    @Test
    void whenLoginWithValidUser_thenRedirectToHome() {
        Person person = new Person();
        when(personDAO.findPersonByNameAndPassword("testuser", "123456789")).thenReturn(person);

        loginController.setName("testuser");
        loginController.setPassword("123456789");

            String outcome = loginController.login();

            assertEquals("home.xhtml?faces-redirect=true", outcome);
            verify(facesContext, never()).addMessage(any(), any());
            verify(personDAO).findPersonByNameAndPassword("testuser", "123456789");
            sessionUtilMockedStatic.verify(() -> SessionUtil.setLoggedInPerson(person));
    }

    /**
     * Tests that an error message is shown when invalid credentials are provided.
     */
    @Test
    void whenLoginWithInvalidUser_thenShowErrorMessage() {
        when(personDAO.findPersonByNameAndPassword("invaliduser", "wrongnumber")).thenReturn(null);

        loginController.setName("invaliduser");
        loginController.setPassword("wrongnumber");

            String outcome = loginController.login();

            assertEquals("", outcome);
            verify(facesContext).addMessage(any(), messageCaptor.capture());
            FacesMessage capturedMessage = messageCaptor.getValue();
            assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity());
            assertEquals("Falsche Angabe", capturedMessage.getSummary());
            verify(personDAO).findPersonByNameAndPassword("invaliduser", "wrongnumber");
            sessionUtilMockedStatic.verify(() -> SessionUtil.setLoggedInPerson(any()), never());
    }


    /**
     * Tests that the user is redirected to the login page when logging out.
     */
    @Test
    void whenLogout_thenRedirectToLogin() {

            String outcome = loginController.logout();

            assertEquals("login.xhtml?faces-redirect=true", outcome);
            sessionUtilMockedStatic.verify(SessionUtil::logout);
    }

}