package org.saflo.ghostNetFishing.controller;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.model.FilterMeta;
import org.saflo.ghostNetFishing.model.Entity.GhostNet;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.model.enums.GhostNetStatus;
import org.saflo.ghostNetFishing.model.enums.PersonType;
import org.saflo.ghostNetFishing.service.FlashMessageService;
import org.saflo.ghostNetFishing.service.GhostNetService;
import org.saflo.ghostNetFishing.util.SessionUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the {@link GhostNetController} class.
 */
@ExtendWith(MockitoExtension.class)
public class GhostNetControllerTest {

    @InjectMocks
    private GhostNetController ghostNetController;

    @Mock
    private GhostNetService ghostNetService;

    @Mock
    private FlashMessageService flashMessageService;

    @Mock
    private FacesContext facesContext;

    @Mock
    private Person loggedInPerson;

    @Mock
    private FilterMeta filterBy;

    @Captor
    private ArgumentCaptor<FacesMessage> messageCaptor;

    private static GhostNet ghostNet;

    private MockedStatic<FacesContext> facesContextMockedStatic;
    private MockedStatic<SessionUtil> sessionUtilMockedStatic;

    /**
     * Sets up the test environment before all tests.
     */
    @BeforeAll
    static void setUPAll() {
        ghostNet = new GhostNet(10.0, 20.0, 100);
    }

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        facesContextMockedStatic = Mockito.mockStatic(FacesContext.class);
        facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
        sessionUtilMockedStatic = Mockito.mockStatic(SessionUtil.class);


    }

    /**
     * Cleans up the test environment after each test.
     */
    @AfterEach
    void tearDown() {
        facesContextMockedStatic.close();
        sessionUtilMockedStatic.close();
    }


    /**
     * Tests that when {@code addGhostNet} is called in the {@link GhostNetController}, the {@link GhostNetService}
     * and {@link FlashMessageService} are called with the correct parameters.
     */
    @Test
    void whenAddGhostNet_thenGhostNetIsAdded() {
        // Arrange
        ghostNetController.setLatitude(10.0);
        ghostNetController.setLongitude(20.0);
        ghostNetController.setEstimatedSize(100);

        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(loggedInPerson);

        // Act
        ghostNetController.addGhostNet();

        // Assert
        // Verifizieren, dass der GhostNetService mit den richtigen Parametern aufgerufen wurde
        verify(ghostNetService).addGhostNet(ghostNetController.getLatitude(), ghostNetController.getLongitude(), ghostNetController.getEstimatedSize(), loggedInPerson);

        // Verifizieren, dass der FlashMessageService mit den richtigen Parametern aufgerufen wurde
        verify(flashMessageService).addFlashMessage(eq(FacesMessage.SEVERITY_INFO), eq("Netz ist erfolgreich gemeldet"), eq(""));
    }


    @Test
    void wenn_StatusIstReportedUndPersonIstRecovererDann_ReportRecoveryPendingAllowedIstTrue() {
        ghostNet.setStatus(GhostNetStatus.REPORTED);
        when(loggedInPerson.getType()).thenReturn(PersonType.RECOVERER);
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(loggedInPerson);

        assertTrue(ghostNetController.isReportRecoveryPendingAllowed(ghostNet));
    }

    @Test
    void wenn_StatusIstNichtReportedDann_ReportRecoveryPendingAllowedIstFalse() {
        ghostNet.setStatus(GhostNetStatus.RECOVERY_PENDING);

        assertFalse(ghostNetController.isReportRecoveryPendingAllowed(ghostNet));
    }

    @Test
    void wenn_StatusIstReportedAberPersonIstNichtRecovererDann_ReportRecoveryPendingAllowedIstFalse() {
        ghostNet.setStatus(GhostNetStatus.REPORTED);
        when(loggedInPerson.getType()).thenReturn(PersonType.REPORTER);
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(loggedInPerson);

        assertFalse(ghostNetController.isReportRecoveryPendingAllowed(ghostNet));
    }

    @Test
    void wenn_StatusIstRecoveryPendingUndPersonIstRecovererDann_ReportRecoveredAllowedIstTrue() {
        ghostNet.setStatus(GhostNetStatus.RECOVERY_PENDING);
        when(loggedInPerson.getType()).thenReturn(PersonType.RECOVERER);
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(loggedInPerson);

        assertTrue(ghostNetController.isReportRecoveredAllowed(ghostNet));
    }

    @Test
    void wenn_StatusIstNichtRecoveryPendingDann_ReportRecoveredAllowedIstFalse() {
        ghostNet.setStatus(GhostNetStatus.REPORTED);

        assertFalse(ghostNetController.isReportRecoveredAllowed(ghostNet));
    }

    @Test
    void wenn_StatusIstRecoveryPendingAberPersonIstNichtRecovererDann_ReportRecoveredAllowedIstFalse() {
        ghostNet.setStatus(GhostNetStatus.RECOVERY_PENDING);
        when(loggedInPerson.getType()).thenReturn(PersonType.REPORTER);
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(loggedInPerson);

        assertFalse(ghostNetController.isReportRecoveredAllowed(ghostNet));
    }

    @Test
    void wenn_StatusIstNichtLostOderRecoveredDann_ReportLostAllowedIstTrue() {
        ghostNet.setStatus(GhostNetStatus.REPORTED);

        assertTrue(ghostNetController.isReportLostAllowed(ghostNet));
    }

    @Test
    void wenn_StatusIstLostDann_ReportLostAllowedIstFalse() {
        ghostNet.setStatus(GhostNetStatus.LOST);

        assertFalse(ghostNetController.isReportLostAllowed(ghostNet));
    }

    @Test
    void wenn_StatusIstRecoveredDann_ReportLostAllowedIstFalse() {
        ghostNet.setStatus(GhostNetStatus.RECOVERED);

        assertFalse(ghostNetController.isReportLostAllowed(ghostNet));
    }

    @Test
    void wenn_StatusIstReportedDann_getStatusInGermanIstGemeldet() {
        assertEquals("Gemeldet", ghostNetController.getStatusInGerman(GhostNetStatus.REPORTED));
    }

    @Test
    void wenn_StatusIstRecoveryPendingDann_getStatusInGermanIstBergungAusstehend() {
        assertEquals("Bergung ausstehend", ghostNetController.getStatusInGerman(GhostNetStatus.RECOVERY_PENDING));
    }

    @Test
    void wenn_StatusIstRecoveredDann_getStatusInGermanIstGeborgen() {
        assertEquals("Geborgen", ghostNetController.getStatusInGerman(GhostNetStatus.RECOVERED));
    }

    @Test
    void wenn_StatusIstLostDann_getStatusInGermanIstVerschollen() {
        assertEquals("Verschollen", ghostNetController.getStatusInGerman(GhostNetStatus.LOST));
    }

    @Test
    void wenn_StatusIstUnbekanntDann_getStatusInGermanIstUnbekannt() {
        assertEquals("Unbekannt", ghostNetController.getStatusInGerman(null));
    }

    @Test
    void wenn_FilterValueIstLostDann_toggleSplitButtonDeaktiviertSplitButton() {
        ghostNetController.setFilterBy(filterBy);
        when(filterBy.getFilterValue()).thenReturn("LOST");

        ghostNetController.toggleSplitButton();

        assertTrue(ghostNetController.isDisableMenuButton());
    }

    @Test
    void wenn_FilterValueIstRecoveredDann_toggleSplitButtonDeaktiviertSplitButton() {
        ghostNetController.setFilterBy(filterBy);
        when(filterBy.getFilterValue()).thenReturn("RECOVERED");

        ghostNetController.toggleSplitButton();

        assertTrue(ghostNetController.isDisableMenuButton());
    }

    @Test
    void wenn_FilterValueIstNichtLostOderRecoveredDann_toggleSplitButtonAktiviertSplitButton() {
        ghostNetController.setFilterBy(filterBy);
        when(filterBy.getFilterValue()).thenReturn("REPORTED");

        ghostNetController.toggleSplitButton();

        assertFalse(ghostNetController.isDisableMenuButton());
    }

    @Test
    void wenn_FilterValueIstNullDann_toggleSplitButtonAktiviertSplitButton() {
        ghostNetController.setFilterBy(filterBy);
        when(filterBy.getFilterValue()).thenReturn(null);

        ghostNetController.toggleSplitButton();

        assertFalse(ghostNetController.isDisableMenuButton());
    }
}
