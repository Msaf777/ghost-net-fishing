package org.saflo.ghostNetFishing.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Hilfsklasse für den Zugriff auf die Datenbank.
 * Verwaltet den EntityManagerFactory und bietet eine statische Methode zum Abrufen eines EntityManagers.
 */
public class DatabaseUtil {
    private static final Logger logger = Logger.getLogger(DatabaseUtil.class.getName());

    // Inject the EntityManager to interact with the persistence context
    private static final EntityManagerFactory emFactory;

    static {
        try {
            //"ghost-net-fishing" ist der Name der Persistence Unit in der persistence.xml
            emFactory = Persistence.createEntityManagerFactory("ghost-net-fishing");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Initial EntityManagerFactory creation failed.", e);
            throw new ExceptionInInitializerError(e);
        }
    }


    /**
     * Gibt einen EntityManager zurück.
     * @return der EntityManager.
     */
    public static EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

}
