package com.example.TYPERUSH;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RaceResult implements Serializable {
    private int wpm;
    private int accuracy;
    private double timeSeconds;
    private int wordCount;
    private String dateTime;

    public RaceResult(int wpm, int accuracy, double timeSeconds, int wordCount) {
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.timeSeconds = timeSeconds;
        this.wordCount = wordCount;
        this.dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public int getWpm() { return wpm; }
    public int getAccuracy() { return accuracy; }
    public double getTimeSeconds() { return timeSeconds; }
    public int getWordCount() { return wordCount; }
    public String getDateTime() { return dateTime; }
}