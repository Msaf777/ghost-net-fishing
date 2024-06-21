package org.saflo.ghostNetFishing.controller;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.PrimeFaces;
import org.saflo.ghostNetFishing.model.DAO.PersonDAO;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.model.enums.PersonType;
import org.saflo.ghostNetFishing.util.SessionUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonControllerTest {

    @InjectMocks
    private PersonController personController;

    @Mock
    private PersonDAO personDAO;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @Mock
    private Flash flash;

    @Mock
    private PrimeFaces primeFaces;

    @Captor
    private ArgumentCaptor<FacesMessage> messageCaptor;

    private MockedStatic<FacesContext> facesContextMockedStatic;
    private MockedStatic<SessionUtil> sessionUtilMockedStatic;
    private MockedStatic<PrimeFaces> primeFacesMockedStatic;

    @BeforeEach
    void setUp() {
        facesContextMockedStatic = Mockito.mockStatic(FacesContext.class);
        facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

        lenient().when(facesContext.getExternalContext()).thenReturn(externalContext);
        lenient().when(externalContext.getFlash()).thenReturn(flash);

        sessionUtilMockedStatic = Mockito.mockStatic(SessionUtil.class);
        primeFacesMockedStatic = Mockito.mockStatic(PrimeFaces.class);
        primeFacesMockedStatic.when(PrimeFaces::current).thenReturn(primeFaces);
    }

    @AfterEach
    void tearDown() {
        facesContextMockedStatic.close();
        sessionUtilMockedStatic.close();
        primeFacesMockedStatic.close();
    }

    @Test
    void whenRegisterWithExistingUser_thenShowWarningMessage() {
        when(personDAO.findPersonByNameAndPhone("testuser", "123456789")).thenReturn(new Person());

        personController.setName("testuser");
        personController.setPhoneNumber("123456789");
        personController.setType(PersonType.RECOVERER);

        String outcome = personController.register();

        assertNull(outcome);
        assertEquals("Benutzer existiert bereits. Möchten Sie zur Login-Seite weitergeleitet werden?", personController.getMessage());
        verify(facesContext).addMessage(any(), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_WARN, capturedMessage.getSeverity());
        verify(primeFaces).executeScript("PF('confirmDialog').show();");
    }

    @Test
    void whenRegisterWithNewUser_thenRegisterSuccessfully() {
        when(personDAO.findPersonByNameAndPhone("newuser", "987654321")).thenReturn(null);

        personController.setName("newuser");
        personController.setPhoneNumber("987654321");
        personController.setType(PersonType.REPORTER);

        Person mockPerson = new Person();
        mockPerson.setName("newuser");
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(mockPerson);

        String outcome = personController.register();

        assertEquals("home?faces-redirect=true", outcome);
        verify(personDAO).addPerson(any(Person.class));
        sessionUtilMockedStatic.verify(() -> SessionUtil.setLoggedInPerson(any(Person.class)));
        verify(flash).setKeepMessages(true);
        verify(facesContext).addMessage(any(), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();
        assertEquals("Erfolgreich registriert", capturedMessage.getSummary());
    }

    @Test
    void whenRedirectToLogin_thenReturnLoginPage() {
        String outcome = personController.redirectToLogin();
        assertEquals("login?faces-redirect=true", outcome);
    }

    @Test
    void whenIsLoggedIn_thenReturnSessionUtilIsLoggedIn() {
        sessionUtilMockedStatic.when(SessionUtil::isLoggedIn).thenReturn(true);
        assertTrue(personController.isLoggedIn());
        sessionUtilMockedStatic.verify(SessionUtil::isLoggedIn);
    }

    @Test
    void whenIsLoggedInPersonRecoverer_thenReturnTrueIfPersonTypeIsRecoverer() {
        Person person = new Person();
        person.setType(PersonType.RECOVERER);
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(person);

        assertTrue(personController.isLoggedInPersonRecoverer());
    }

    @Test
    void whenIsLoggedInPersonReporter_thenReturnTrueIfPersonTypeIsReporter() {
        Person person = new Person();
        person.setType(PersonType.REPORTER);
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(person);

        assertTrue(personController.isLoggedInPersonReporter());
    }

    @Test
    void whenIsPersonReporterAndLoggedIn_thenReturnTrueIfLoggedInAndPersonTypeIsReporter() {
        Person person = new Person();
        person.setType(PersonType.REPORTER);
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(person);

        assertTrue(personController.isPersonReporterAndLoggedIn());
    }

    @Test
    void whenGetLoggedInPersonName_thenReturnFormattedName() {
        Person person = new Person();
        person.setName("john");
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(person);

        assertEquals("John", personController.getLoggedInPersonName());
    }

}
