package org.post.utils;

import org.post.config.Settings;
import org.post.models.User;

import java.io.*;

public class Caching {


    public static void saveUser(User user) {
        try (FileOutputStream fileOut = new FileOutputStream(Settings.cacheFileName);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            objectOut.writeObject(user);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static User loadUser() {
        try (FileInputStream fileIn = new FileInputStream(Settings.cacheFileName);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            Object obj = objectIn.readObject();
            if (obj instanceof User) {
                return (User) obj;
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}






