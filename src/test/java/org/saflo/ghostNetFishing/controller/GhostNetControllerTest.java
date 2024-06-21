package org.saflo.ghostNetFishing.controller;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.saflo.ghostNetFishing.model.DAO.GhostNetDAO;
import org.saflo.ghostNetFishing.model.Entity.GhostNet;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.model.enums.GhostNetStatus;
import org.saflo.ghostNetFishing.model.enums.PersonType;
import org.saflo.ghostNetFishing.util.SessionUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GhostNetControllerTest {

    @InjectMocks
    private GhostNetController ghostNetController;

    @Mock
    private GhostNetDAO ghostNetDAO;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @Mock
    private Flash flash;

    @Mock
    private Person loggedInPerson;

    @Captor
    private ArgumentCaptor<FacesMessage> messageCaptor;

    private MockedStatic<FacesContext> facesContextMockedStatic;
    private MockedStatic<SessionUtil> sessionUtilMockedStatic;

    @BeforeEach
    void setUp() {
        facesContextMockedStatic = Mockito.mockStatic(FacesContext.class);
        facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
     lenient().when(facesContext.getExternalContext()).thenReturn(externalContext);
     lenient().when(externalContext.getFlash()).thenReturn(flash);
        sessionUtilMockedStatic = Mockito.mockStatic(SessionUtil.class);
    }

    @AfterEach
    void tearDown() {
        facesContextMockedStatic.close();
        sessionUtilMockedStatic.close();
    }

    @Test
    void whenPopulateMapWithGhostNets_thenMarkersAreAddedToMapModel() {
        List<GhostNet> ghostNets = new ArrayList<>();
        ghostNets.add(new GhostNet( 10.0, 10.0, 100));
        ghostNets.get(0).setStatus(GhostNetStatus.REPORTED);
        ghostNets.add(new GhostNet(20.0, 20.0, 200));
        ghostNets.get(1).setStatus(GhostNetStatus.LOST);

        when(ghostNetDAO.getAvailableGhostNets()).thenReturn(ghostNets);

        ghostNetController.populateMapWithGhostNets();

        MapModel<Long> model = ghostNetController.getSimpleModel();
        assertEquals(2, model.getMarkers().size());

        Marker marker1 = (Marker) model.getMarkers().get(0);
        assertEquals(new LatLng(10.0, 10.0), marker1.getLatlng());
        assertEquals("100m²", marker1.getTitle());

        Marker marker2 = (Marker) model.getMarkers().get(1);
        assertEquals(new LatLng(20.0, 20.0), marker2.getLatlng());
        assertEquals("200m²", marker2.getTitle());
    }

    @Test
    void whenAddGhostNet_thenGhostNetIsAdded() {
        ghostNetController.setLatitude(10.0);
        ghostNetController.setLongitude(20.0);
        ghostNetController.setEstimatedSize(100);

        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(loggedInPerson);
        lenient().when(loggedInPerson.getType()).thenReturn(PersonType.RECOVERER);

        ghostNetController.addGhostNet();

        ArgumentCaptor<GhostNet> captor = ArgumentCaptor.forClass(GhostNet.class);
        verify(ghostNetDAO).addGhostNet(captor.capture());
        GhostNet addedGhostNet = captor.getValue();

        assertEquals(10.0, addedGhostNet.getLatitude());
        assertEquals(20.0, addedGhostNet.getLongitude());
        assertEquals(100, addedGhostNet.getEstimatedSize());
        assertEquals(loggedInPerson, addedGhostNet.getReportedBy());

        verify(facesContext).addMessage(isNull(), messageCaptor.capture());
        FacesMessage message = messageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, message.getSeverity());
        assertEquals("Netz ist erfolgreich gemeldet", message.getSummary());
    }

    @Test
    void whenHandleAddGhostNetAnonymously_thenGhostNetIsAddedWithoutReporter() {
        ghostNetController.setLatitude(10.0);
        ghostNetController.setLongitude(20.0);
        ghostNetController.setEstimatedSize(100);
        ghostNetController.setStayAnonymous(true);

        sessionUtilMockedStatic.when(SessionUtil::isLoggedIn).thenReturn(true);
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(loggedInPerson);
        when(loggedInPerson.getType()).thenReturn(PersonType.REPORTER);

        ghostNetController.handleAddGhostNet();

        ArgumentCaptor<GhostNet> captor = ArgumentCaptor.forClass(GhostNet.class);
        verify(ghostNetDAO).addGhostNet(captor.capture());
        GhostNet addedGhostNet = captor.getValue();

        assertEquals(10.0, addedGhostNet.getLatitude());
        assertEquals(20.0, addedGhostNet.getLongitude());
        assertEquals(100, addedGhostNet.getEstimatedSize());
        assertNull(addedGhostNet.getReportedBy());

        verify(facesContext).addMessage(isNull(), messageCaptor.capture());
        FacesMessage message = messageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, message.getSeverity());
        assertEquals("Netz ist erfolgreich gemeldet", message.getSummary());
    }

    @Test
    void testUpdateStatus() {
        GhostNet ghostNet = new GhostNet(10.0, 20.0, 100);
        sessionUtilMockedStatic.when(SessionUtil::getLoggedInPerson).thenReturn(loggedInPerson);

        when(ghostNetDAO.findGhostNet(ghostNet.getId())).thenReturn(ghostNet);

        ghostNetController.updateStatus(ghostNet.getId(), "RECOVERED");

        assertEquals(GhostNetStatus.RECOVERED, ghostNet.getStatus());
        assertEquals(loggedInPerson, ghostNet.getRecoveredBy());
        verify(ghostNetDAO).updateGhostNet(ghostNet);
    }
}
