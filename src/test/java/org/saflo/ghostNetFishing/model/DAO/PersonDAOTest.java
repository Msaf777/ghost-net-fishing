package org.saflo.ghostNetFishing.model.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.util.DatabaseUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonDAOTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction entityTransaction;

    @InjectMocks
    private PersonDAO personDAO;

    @Mock
    private TypedQuery<Person> typedQuery;

    private Person person;

    @BeforeEach
    public void setUp() {
        person = new Person();
        person.setName("John Doe");
        person.setPhoneNumber("1234567890");
    }

    @Test
    public void whenAddPersonCalled_thenPersonIsPersisted() {
        try (MockedStatic<DatabaseUtil> databaseUtilMockedStatic = mockStatic(DatabaseUtil.class)) {
            // Arrange
            databaseUtilMockedStatic.when(DatabaseUtil::getEntityManager).thenReturn(entityManager);
            when(entityManager.getTransaction()).thenReturn(entityTransaction);

            // Act
            personDAO.addPerson(person);

            // Assert
            verify(entityTransaction, times(1)).begin();
            verify(entityManager, times(1)).persist(person);
            verify(entityTransaction, times(1)).commit();
            verify(entityManager, times(1)).close();
        }
    }

    @Test
    public void whenPersonWithNameAndPhoneExists_thenPersonIsReturned() {
        try (MockedStatic<DatabaseUtil> databaseUtilMockedStatic = mockStatic(DatabaseUtil.class)) {
            // Arrange
            databaseUtilMockedStatic.when(DatabaseUtil::getEntityManager).thenReturn(entityManager);
            when(entityManager.createQuery(anyString(), eq(Person.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("name", "John Doe")).thenReturn(typedQuery);
            when(typedQuery.setParameter("phone", "1234567890")).thenReturn(typedQuery);
            when(typedQuery.getSingleResult()).thenReturn(person);

            // Act
            Person result = personDAO.findPersonByNameAndPhone("John Doe", "1234567890");

            // Assert
            assertNotNull(result);
            assertEquals(person, result);
        }
    }

    @Test
    public void whenPersonWithNameAndPhoneDoesNotExist_thenThrowNoResultException() {
        try (MockedStatic<DatabaseUtil> databaseUtilMockedStatic = mockStatic(DatabaseUtil.class)) {
            // Arrange
            databaseUtilMockedStatic.when(DatabaseUtil::getEntityManager).thenReturn(entityManager);
            when(entityManager.createQuery(anyString(), eq(Person.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("name", "John Doe")).thenReturn(typedQuery);
            when(typedQuery.setParameter("phone", "1234567890")).thenReturn(typedQuery);
            when(typedQuery.getSingleResult()).thenThrow(new jakarta.persistence.NoResultException());

            // Act
            Person result = personDAO.findPersonByNameAndPhone("John Doe", "1234567890");

            // Assert
            assertNull(result);
        }
    }
}

