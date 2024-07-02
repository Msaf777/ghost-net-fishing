package org.saflo.ghostNetFishing.util;

import org.springframework.security.crypto.bcrypt.BCrypt;


/**
 * Utility class for password hashing and checking using BCrypt.
 */
public class PasswordUtil {

    /**
     * Hashes a plain text password using BCrypt.
     * @param plainTextPassword the plain text password to hash.
     * @return the hashed password.
     */
    public static String hashPassword (String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Checks if the plain text password matches the hashed password.
     * @param plainTextPassword the plain text password to check.
     * @param hashedPassword the hashed password to check against.
     * @return true if the passwords match, otherwise false.
     */
    public static boolean checkPassword (String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
