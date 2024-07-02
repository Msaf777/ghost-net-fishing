package org.saflo.ghostNetFishing.exception;

/**
 * Custom exception for database-related errors in the GhostNetFishing application.
 */
public class CustomDatabaseException extends RuntimeException {
    /**
     * Constructs a new CustomDatabaseException with the specified detail message and cause.
     * @param message the detail message.
     * @param cause the cause.
     */
    public CustomDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
