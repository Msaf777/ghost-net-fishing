package org.saflo.ghostNetFishing.util;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;

@ApplicationScoped
public class CDIServiceLocator {

    public static <T> T getBean (Class<T> clazz) {
        return CDI.current().select(clazz).get();
    }
}
