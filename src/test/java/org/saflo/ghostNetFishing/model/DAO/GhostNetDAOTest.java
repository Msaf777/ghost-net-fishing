package org.saflo.ghostNetFishing.model.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.model.FilterMeta;
import org.saflo.ghostNetFishing.model.Entity.GhostNet;
import org.saflo.ghostNetFishing.model.enums.GhostNetStatus;
import org.saflo.ghostNetFishing.util.DatabaseUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link GhostNetDAO} class.
 */
@ExtendWith(MockitoExtension.class)
class GhostNetDAOTest {

    @Mock
    private static EntityManager entityManager;

    @Mock
    private static EntityTransaction entityTransaction;

    @Mock
    private TypedQuery<GhostNet> typedQuery;

    @InjectMocks
    private GhostNetDAO ghostNetDAO;

    private GhostNet ghostNet;

    private static MockedStatic<DatabaseUtil> databaseUtilMockedStatic;

    /**
     * Sets up the class-level test environment.
     */
    @BeforeAll
    public static void setUpClass() {
        databaseUtilMockedStatic = mockStatic(DatabaseUtil.class);
    }

    /**
     * Cleans up the class-level test environment.
     */
    @AfterAll
    public static void tearDownClass() {
        databaseUtilMockedStatic.close();
    }


    @BeforeEach
    public void setUp() {
        ghostNet = new GhostNet();
        ghostNet.setLatitude(90.0);
        ghostNet.setLongitude(180.0);
        ghostNet.setEstimatedSize(100);

        databaseUtilMockedStatic.when(DatabaseUtil::getEntityManager).thenReturn(entityManager);
        lenient().when(entityManager.getTransaction()).thenReturn(entityTransaction);

    }

    /**
     * Tests that a GhostNet is persisted when {@code addGhostNet} is called.
     */
    @Test
    public void whenAddGhostNetCalled_thenGhostNetIsPersisted() {

        // Act
        ghostNetDAO.addGhostNet(ghostNet);

        // Assert
        verify(entityTransaction, times(1)).begin();
        verify(entityManager, times(1)).persist(ghostNet);
        verify(entityTransaction, times(1)).commit();
        verify(entityManager, times(1)).close();
    }

    /**
     * Tests that the transaction is rolled back if an exception occurs during {@code addGhostNet}.
     */
    @Test
    public void whenAddGhostNetCalled_andExceptionOccurs_thenTransactionIsRolledBack(){
        doThrow(new RuntimeException("Error")).when(entityManager).persist(any(GhostNet.class));
        when(entityTransaction.isActive()).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> ghostNetDAO.addGhostNet(ghostNet));
        assertEquals("Error adding GhostNet", exception.getMessage());
        verify(entityTransaction, times(1)).begin();
        verify(entityTransaction, times(1)).rollback();
        verify(entityManager, times(1)).close();
    }

    /**
     * Tests that a GhostNet is returned when {@code findGhostNet} is called with a valid ID.
     */
    @Test
    public void whenFindGhostNetCalled_thenGhostNetIsReturned() {

        // Arrange
        when(entityManager.find(GhostNet.class, 1L)).thenReturn(ghostNet);

        // Act
        GhostNet result = ghostNetDAO.findGhostNet(1L);

        // Assert
        assertNotNull(result);
        assertEquals(ghostNet, result);

    }

    /**
     * Tests that null is returned when {@code findGhostNet} is called with an invalid ID.
     */
    @Test
    public void whenFindGhostNetCalled_thenNullIsReturned() {

        // Arrange
        when(entityManager.find(GhostNet.class, 2L)).thenReturn(null);

        // Act
        GhostNet result = ghostNetDAO.findGhostNet(2L);

        // Assert
        assertNull(result);

    }

    /**
     * Tests that a GhostNet is merged when {@code updateGhostNet} is called.
     */
    @Test
    public void whenUpdateGhostNetCalled_thenGhostNetIsMerged() {

        // Arrange

        // Act
        ghostNetDAO.updateGhostNet(ghostNet);

        // Assert
        verify(entityTransaction, times(1)).begin();
        verify(entityManager, times(1)).merge(ghostNet);
        verify(entityTransaction, times(1)).commit();
        verify(entityManager, times(1)).close();

    }


    /**
     * Tests that the transaction is rolled back if an exception occurs during {@code updateGhostNet}.
     */
    @Test
    public void whenUpdateGhostNetCalled_andExceptionOccurs_thenTransactionIsRolledBack() {
        // Arrange
        doThrow(new RuntimeException("Error")).when(entityManager).merge(any(GhostNet.class));
        when(entityTransaction.isActive()).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> ghostNetDAO.updateGhostNet(ghostNet));
        assertEquals("Error updating GhostNet", exception.getMessage());
        verify(entityTransaction, times(1)).begin();
        verify(entityTransaction, times(1)).rollback();
        verify(entityManager, times(1)).close();
    }

    /**
     * Tests that a list of GhostNets is returned when {@code getAllGhostNets} is called.
     */
    @Test
    public void whenGetAllGhostNetsCalled_thenListOfGhostNetsIsReturned() {

        // Arrange
        List<GhostNet> ghostNets = Arrays.asList(ghostNet);
        when(entityManager.createQuery(anyString(), eq(GhostNet.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(ghostNets);

        // Act
        List<GhostNet> result = ghostNetDAO.getAllGhostNets();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ghostNet, result.get(0));

    }

    /**
     * Tests that an empty list is returned when {@code getAllGhostNets} is called and no GhostNets exist.
     */
    @Test
    public void whenGetAllGhostNetsCalled_thenEmptyListIsReturned() {
        //Arrange
        when(entityManager.createQuery(anyString(), eq(GhostNet.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        //Act
        List<GhostNet> result = ghostNetDAO.getAllGhostNets();

        //Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests that a list of filtered GhostNets is returned when {@code getFilteredGhostNets} is called with valid filter criteria.
     */
    @Test
    public void whenGetFilteredGhostNetsCalled_thenListOfGhostNetsIsReturned() {

        // Arrange
        FilterMeta filterMeta = mock(FilterMeta.class);
        when(filterMeta.getFilterValue()).thenReturn("REPORTED");

        List<GhostNet> ghostNets = Arrays.asList(ghostNet);
        when(entityManager.createQuery(anyString(), eq(GhostNet.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("status"), eq(GhostNetStatus.REPORTED))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(ghostNets);

        // Act
        List<GhostNet> result = ghostNetDAO.getFilteredGhostNets(filterMeta);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ghostNet, result.get(0));


    }

    /**
     * Tests that an empty list is returned when {@code getFilteredGhostNets} is called with filter criteria that match no GhostNets.
     */
    @Test
    public void whenGetFilteredGhostNetsCalled_thenEmptyListIsReturned() {
        // Arrange
        FilterMeta filterMeta = mock(FilterMeta.class);
        when(filterMeta.getFilterValue()).thenReturn("REPORTED");

        when(entityManager.createQuery(anyString(), eq(GhostNet.class))).thenReturn(typedQuery);
        lenient().when(typedQuery.setParameter(eq("status"), eq(GhostNetStatus.REPORTED))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        List<GhostNet> result = ghostNetDAO.getFilteredGhostNets(filterMeta);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests that a list of available GhostNets is returned when {@code getAvailableGhostNets} is called.
     */
    @Test
    public void whenGetAvailableGhostNetsCalled_thenListOfGhostNetsIsReturned() {

        // Arrange
        List<GhostNet> ghostNets = Arrays.asList(ghostNet);
        when(entityManager.createQuery(anyString(), eq(GhostNet.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("statuses"), eq(Arrays.asList(GhostNetStatus.REPORTED, GhostNetStatus.RECOVERY_PENDING)))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(ghostNets);

        // Act
        List<GhostNet> result = ghostNetDAO.getAvailableGhostNets();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ghostNet, result.get(0));

    }

    /**
     * Tests that an empty list is returned when {@code getAvailableGhostNets} is called and no GhostNets are available.
     */
    @Test
    public void whenGetAvailableGhostNetsCalled_thenEmptyListIsReturned() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(GhostNet.class))).thenReturn(typedQuery);
        lenient().when(typedQuery.setParameter(eq("statuses"), eq(Arrays.asList(GhostNetStatus.REPORTED, GhostNetStatus.RECOVERY_PENDING)))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        List<GhostNet> result = ghostNetDAO.getAvailableGhostNets();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}