package org.saflo.ghostNetFishing.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.logging.Level;
import java.util.logging.Logger;


public class DatabaseUtil {
    private static final Logger logger = Logger.getLogger(DatabaseUtil.class.getName());
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


    public static EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

}
