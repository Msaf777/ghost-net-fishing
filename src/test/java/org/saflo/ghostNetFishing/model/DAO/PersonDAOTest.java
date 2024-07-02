package org.saflo.ghostNetFishing.model.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.saflo.ghostNetFishing.exception.CustomDatabaseException;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.util.DatabaseUtil;
import org.saflo.ghostNetFishing.util.PasswordUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Unit test class for PersonDAO using Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class PersonDAOTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PersonDAO personDAO;

    private MockedStatic<DatabaseUtil> databaseUtilMockedStatic;

    @BeforeEach
    void setUp() {
        databaseUtilMockedStatic = mockStatic(DatabaseUtil.class);
        databaseUtilMockedStatic.when(DatabaseUtil::getEntityManager).thenReturn(entityManager);
    }

    @AfterEach
    void tearDown() {
        databaseUtilMockedStatic.close();
    }

    /**
     * Test for the addPerson method to verify if a person is added successfully.
     */
    @Test
    void whenAddPerson_thenSuccess() {
        Person person = new Person();
        person.setName("John Doe");
        person.setPassword("password123");

        doNothing().when(entityManager).persist(any(Person.class));
        EntityTransaction transaction = mock(EntityTransaction.class);
        when(entityManager.getTransaction()).thenReturn(transaction);

        assertDoesNotThrow(() -> personDAO.addPerson(person));
        verify(entityManager).persist(any(Person.class));
        verify(transaction).begin();
        verify(transaction).commit();
        verify(entityManager).close();
    }

    /**
     * Test for the addPerson method to verify if a CustomDatabaseException is thrown when an error occurs.
     */
    @Test
    void whenAddPerson_thenFailure() {
        Person person = new Person();
        person.setName("John Doe");
        person.setPassword("password123");

        EntityTransaction transaction = mock(EntityTransaction.class);
        when(entityManager.getTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException("Database error")).when(entityManager).persist(any(Person.class));

        assertThrows(CustomDatabaseException.class, () -> personDAO.addPerson(person));
        verify(transaction, times(1)).begin();
        verify(transaction,never()).commit();
        verify(entityManager).close();
    }

    /**
     * Test for the findPersonByNameAndPassword method to verify if a person is found by name and password.
     */
    @Test
    void whenFindPersonByNameAndPassword_thenSuccess() {
        Person person = new Person();
        person.setName("John Doe");
        person.setPassword(PasswordUtil.hashPassword("password123"));

        TypedQuery<Person> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Person.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(person);

        Person result = personDAO.findPersonByNameAndPassword("John Doe", "password123");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    /**
     * Test for the findPersonByNameAndPassword method to verify if null is returned when the person is not found.
     */
    @Test
    void whenFindPersonByNameAndPassword_thenFailure() {
        TypedQuery<Person> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Person.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(null);

        Person result = personDAO.findPersonByNameAndPassword("John Doe", "wrongpassword");

        assertNull(result);
    }

    /**
     * Test for the findPersonByField method to verify if a person is found by a specific field.
     */
    @Test
    void whenFindPersonByField_thenSuccess() {
        Person person = new Person();
        person.setName("John Doe");
        person.setPhoneNumber("123456789");

        TypedQuery<Person> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Person.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(person);

        Person result = personDAO.findPersonByName("John Doe");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    /**
     * Test for the findPersonByField method to verify if null is returned when the person is not found.
     */
    @Test
    void whenFindPersonByField_thenFailure() {
        TypedQuery<Person> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Person.class))).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(null);

        Person result = personDAO.findPersonByName("NonExistingName");

        assertNull(result);
    }
}