package org.saflo.ghostNetFishing.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.map.MapModel;
import org.saflo.ghostNetFishing.model.DAO.GhostNetDAO;
import org.saflo.ghostNetFishing.model.Entity.GhostNet;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.model.enums.GhostNetStatus;
import org.saflo.ghostNetFishing.util.SessionUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;



/**
 * Unit tests for the {@link GhostNetService} class.
 */
@ExtendWith(MockitoExtension.class)
class GhostNetServiceTest {
    @InjectMocks
    private GhostNetService ghostNetService;

    @Mock
    private GhostNetDAO ghostNetDAO;

    @Mock
    private Person person;


    private MockedStatic<SessionUtil> sessionUtilMockedStatic;

    @BeforeEach
    void setUp() {
        sessionUtilMockedStatic = Mockito.mockStatic(SessionUtil.class);
    }

    @AfterEach
    void tearDown() {
        sessionUtilMockedStatic.close();
    }


    /**
     * Tests that the map model is created with markers for available ghost nets.
     */
    @Test
    void whenCreateMapModel_thenMarkersAreAddedToMapModel() {
        GhostNet ghostNet1 = new GhostNet(10.0, 10.0, 100);
        ghostNet1.setStatus(GhostNetStatus.REPORTED);
        GhostNet ghostNet2 = new GhostNet(20.0, 20.0, 200);
        ghostNet2.setStatus(GhostNetStatus.LOST);

        List<GhostNet> ghostNets = Arrays.asList(ghostNet1, ghostNet2);
        when(ghostNetDAO.getAvailableGhostNets()).thenReturn(ghostNets);

        MapModel<Long> mapModel = ghostNetService.createMapModel();

        assertNotNull(mapModel);
        assertEquals(2, mapModel.getMarkers().size());
    }

    /**
     * Tests that all ghost nets are retrieved.
     */
    @Test
    void whenGetAllGhostNets_thenListOfGhostNetsIsReturned() {
        List<GhostNet> ghostNets = Arrays.asList(new GhostNet(), new GhostNet());
        when(ghostNetDAO.getAllGhostNets()).thenReturn(ghostNets);

        List<GhostNet> result = ghostNetService.getAllGhostNets();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * Tests that filtered ghost nets are retrieved based on filter criteria.
     */
    @Test
    void whenGetFilteredGhostNets_thenListOfFilteredGhostNetsIsReturned() {
        FilterMeta filterMeta = mock(FilterMeta.class);
        when(filterMeta.getFilterValue()).thenReturn("REPORTED");

        List<GhostNet> ghostNets = Arrays.asList(new GhostNet());
        when(ghostNetDAO.getFilteredGhostNets(filterMeta)).thenReturn(ghostNets);

        List<GhostNet> result = ghostNetService.getFilteredGhostNets(filterMeta);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Tests that a ghost net is added anonymously.
     */
    @Test
    void whenAddGhostNetAnonymously_thenGhostNetIsAdded() {
        ghostNetService.addGhostNetAnonymously(10.0, 20.0, 100);

        ArgumentCaptor<GhostNet> captor = ArgumentCaptor.forClass(GhostNet.class);
        verify(ghostNetDAO).addGhostNet(captor.capture());

        GhostNet addedGhostNet = captor.getValue();
        assertEquals(10.0, addedGhostNet.getLatitude());
        assertEquals(20.0, addedGhostNet.getLongitude());
        assertEquals(100, addedGhostNet.getEstimatedSize());
    }

    @Test
    void whenAddGhostNet_thenGhostNetIsAdded() {
        ghostNetService.addGhostNet(10.0, 20.0, 100, person);

        ArgumentCaptor<GhostNet> captor = ArgumentCaptor.forClass(GhostNet.class);
        verify(ghostNetDAO).addGhostNet(captor.capture());

        GhostNet addedGhostNet = captor.getValue();
        assertEquals(10.0, addedGhostNet.getLatitude());
        assertEquals(20.0, addedGhostNet.getLongitude());
        assertEquals(100, addedGhostNet.getEstimatedSize());
        assertEquals(person, addedGhostNet.getReportedBy());

    }

    /**
     * Tests that the status of a ghost net is updated correctly.
     */
    @Test
    void whenUpdateGhostNetStatus_thenGhostNetStatusIsUpdated() {
        GhostNet ghostNet = new GhostNet();
        when(ghostNetDAO.findGhostNet(1L)).thenReturn(ghostNet);

        ghostNetService.updateGhostNetStatus(1L, GhostNetStatus.RECOVERED, person);

        assertEquals(GhostNetStatus.RECOVERED, ghostNet.getStatus());
        assertEquals(person, ghostNet.getRecoveredBy());
        verify(ghostNetDAO).updateGhostNet(ghostNet);
    }

    /**
     * Tests that no update is performed if the ghost net is not found.
     */
    @Test
    void whenUpdateGhostNetStatusAndGhostNetNotFound_thenNoUpdateIsPerformed() {
        when(ghostNetDAO.findGhostNet(1L)).thenReturn(null);

        ghostNetService.updateGhostNetStatus(1L, GhostNetStatus.RECOVERED, person);

        verify(ghostNetDAO, never()).updateGhostNet(any(GhostNet.class));
    }

}