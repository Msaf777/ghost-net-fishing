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

    //Testet, dass der Benutzer bei gültigen Anmeldedaten zur Startseite weitergeleitet wird.
    @Test
    void whenLoginWithValidUser_thenRedirectToHome() {
        Person person = new Person();
        when(personDAO.findPersonByNameAndPhone("testuser", "123456789")).thenReturn(person);

        loginController.setName("testuser");
        loginController.setPhoneNumber("123456789");
        loginController.setStayAnonymous(false);

            String outcome = loginController.login();

            assertEquals("home.xhtml?faces-redirect=true", outcome);
            verify(facesContext, never()).addMessage(any(), any());
            verify(personDAO).findPersonByNameAndPhone("testuser", "123456789");
            sessionUtilMockedStatic.verify(() -> SessionUtil.setLoggedInPerson(person));

    }

    @Test
    void whenLoginWithInvalidUser_thenShowErrorMessage() {
        when(personDAO.findPersonByNameAndPhone("invaliduser", "wrongnumber")).thenReturn(null);

        loginController.setName("invaliduser");
        loginController.setPhoneNumber("wrongnumber");
        loginController.setStayAnonymous(false);

            String outcome = loginController.login();

            assertEquals("", outcome);
            verify(facesContext).addMessage(any(), messageCaptor.capture());
            FacesMessage capturedMessage = messageCaptor.getValue();
            assertEquals(FacesMessage.SEVERITY_ERROR, capturedMessage.getSeverity());
            assertEquals("Falsche Angabe", capturedMessage.getSummary());
            verify(personDAO).findPersonByNameAndPhone("invaliduser", "wrongnumber");
            sessionUtilMockedStatic.verify(() -> SessionUtil.setLoggedInPerson(any()), never());
    }

    @Test
    void whenLoginAnonymously_thenRedirectToAddGhostNet() {
        loginController.setStayAnonymous(true);

        String outcome = loginController.login();

        assertEquals("addGhostNet.xhtml?faces-redirect=true", outcome);
        verify(personDAO, never()).findPersonByNameAndPhone(anyString(), anyString());
        verify(facesContext, never()).addMessage(any(), any());
    }

    @Test
    void whenLogout_thenRedirectToLogin() {

            String outcome = loginController.logout();

            assertEquals("login.xhtml?faces-redirect=true", outcome);
            sessionUtilMockedStatic.verify(SessionUtil::logout);
    }

}