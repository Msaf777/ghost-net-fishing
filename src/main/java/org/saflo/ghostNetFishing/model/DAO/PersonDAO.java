package org.saflo.ghostNetFishing.model.DAO;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.util.DatabaseUtil;

import java.util.logging.Logger;

@Named
@RequestScoped
public class PersonDAO {
    private static final Logger logger = Logger.getLogger(PersonDAO.class.getName());

    public void addPerson(Person person) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
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
            throw e;
        } finally {
            em.close();
            logger.info("EntityManager closed after attempting to add a new person");
        }

    }



    public Person findPersonByNameAndPhone(String name, String phone){
        TypedQuery<Person> query = DatabaseUtil.getEntityManager()
                .createQuery("SELECT p FROM Person p WHERE p.name = :name AND p.phoneNumber = :phone", Person.class);
        query.setParameter("name", name);
        query.setParameter("phone", phone);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}