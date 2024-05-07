package org.saflo.ghostNetFishing.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseUtil {
    private static final EntityManagerFactory emFactory;

    static {
        try {
            //"ghost-net-fishing" ist der Name der Persistence Unit in der persistence.xml
            emFactory = Persistence.createEntityManagerFactory("ghost-net-fishing");
        } catch (Exception e) {
            System.err.println("Initial EntityManagerFactory creation failed."+ e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    public static void close() {
        if(emFactory != null) {
            emFactory.close();
        }
    }
}
