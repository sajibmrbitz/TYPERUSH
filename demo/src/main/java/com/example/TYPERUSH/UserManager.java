package com.example.TYPERUSH;

import java.io.*;
import java.util.HashMap;

public class UserManager {
    private static final String FILE_NAME = "users.dat";
    private static HashMap<String, User> users = new HashMap<>();
    public static User currentUser;

    public static void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            users = (HashMap<String, User>) ois.readObject();
        } catch (Exception e) { users = new HashMap<>(); }
    }

    public static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static boolean signup(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, password));
        saveUsers();
        return true;
    }

    public static boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }
}