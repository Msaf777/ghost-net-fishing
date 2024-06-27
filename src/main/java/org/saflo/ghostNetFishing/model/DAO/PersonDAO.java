package org.saflo.ghostNetFishing.model.DAO;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.saflo.ghostNetFishing.exception.CustomDatabaseException;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.util.DatabaseUtil;
import org.saflo.ghostNetFishing.util.PasswordUtil;

import java.util.logging.Logger;

/**
 * Data Access Object (DAO) for managing persons in the database.
 */
@Named
@RequestScoped
public class PersonDAO {
    private static final Logger logger = Logger.getLogger(PersonDAO.class.getName());

    /**
     * Adds a new person to the database.
     * @param person the person to be added.
     */
    public void addPerson(Person person) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            // Hash the password before saving
            person.setPassword(PasswordUtil.hashPassword(person.getPassword()));
            logger.info("Starting transaction to add a new person.");
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
            logger.info("Transaction committed successfully and person added.");
        } catch (Exception e) {
            logger.severe("Transaction failed: " + e.getMessage());
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                logger.info("Transaction rolled back.");
            }
            throw new CustomDatabaseException("Error adding Person", e);
        } finally {
            em.close();
        }

    }

    private Person findPersonByField(String field, String value) {
        TypedQuery<Person> query = DatabaseUtil.getEntityManager()
                .createQuery("SELECT p FROM Person p WHERE p." + field + " = :value", Person.class);
        query.setParameter("value", value);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Finds a person by name and password.
     * @param name the name of the person.
     * @param plainTextPassword the password of the person in plain Text.
     * @return the found person or null if no person was found.
     */
    public Person findPersonByNameAndPassword(String name, String plainTextPassword){
        Person person = findPersonByField("name", name);
            if(person != null && PasswordUtil.checkPassword(plainTextPassword, person.getPassword())) {
                return person;
            } else {
                return null;
            }
    }

    /**
     * Finds a person by name.
     * @param name the name of the person.
     * @return the found person or null if no person was found.
     */
    public Person findPersonByName(String name){
        return findPersonByField("name", name);
    }

    /**
     * Finds a person by phone number.
     * @param phone the phone number of the person.
     * @return the found person or null if no person was found.
     */
    public Person findPersonByPhone(String phone) {
        return findPersonByField("phone", phone);
    }
}