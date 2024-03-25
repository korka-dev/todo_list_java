package org.post.utils;

import at.favre.lib.crypto.bcrypt.BCrypt.Hasher;
import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Verifyer;

public class Hashing {

    private static final Hasher hasher = BCrypt.withDefaults();
    private static final Verifyer verifyer = BCrypt.verifyer();

    public static String hashPassword(String password) {
        return hasher.hashToString(12, password.toCharArray());
    }

    public static Boolean verifyPassword(String plainPassword, String hashedPassword) {
        return verifyer.verify(plainPassword.getBytes(),
                hashedPassword.getBytes()).verified;
    }
}


