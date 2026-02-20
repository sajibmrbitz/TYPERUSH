package com.example.TYPERUSH;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    // The single list that holds all your local results
    public static List<RaceResult> localHistory = new ArrayList<>();

    // 1. Load the history when the game starts
    public static void loadHistory() {
        try {
            FileInputStream fileIn = new FileInputStream("local_history.dat");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            // Read the saved list into our variable
            localHistory = (List<RaceResult>) objectIn.readObject();

            objectIn.close();
        } catch (Exception e) {
            // Start with a blank list if the file doesn't exist yet
            localHistory = new ArrayList<>();
        }
    }

    // 2. Save the history
    public static void saveHistory() {
        try {
            FileOutputStream fileOut = new FileOutputStream("local_history.dat");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            // Write our entire list into the file
            objectOut.writeObject(localHistory);

            objectOut.close();
        } catch (Exception e) {
            System.out.println("Could not save the file.");
        }
    }

    // 3. Add a result and save immediately (Brought this back!)
    public static void addResult(RaceResult result) {
        localHistory.add(result);
        saveHistory();
    }
}