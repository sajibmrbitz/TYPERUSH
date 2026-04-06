package com.example.TYPERUSH;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class UserManager {
    public static List<RaceResult> localHistory = new ArrayList<>();

    public static void loadHistory() {
        try {
            FileInputStream fileIn = new FileInputStream("local_history.dat");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            localHistory = (List<RaceResult>) objectIn.readObject();

            objectIn.close();
        } catch (Exception e) {
            localHistory = new ArrayList<>();
        }
    }

    public static void saveHistory() {
        try {
            FileOutputStream fileOut = new FileOutputStream("local_history.dat");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(localHistory);

            objectOut.close();
        } catch (Exception e) {
            System.out.println("Could not save the file.");
        }
    }

    public static void addResult(RaceResult result) {
        localHistory.add(result);
        saveHistory();
    }

    public static List<RaceResult> getAllResults() {
        return new ArrayList<>(localHistory);
    }

    public static List<RaceResult> getTodaysResults() {
        String todayString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return localHistory.stream()
                .filter(result -> result.getDateTime().startsWith(todayString))
                .collect(Collectors.toList());
    }
}