package com.example.TYPERUSH;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private int highWPM;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.highWPM = 0;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getHighWPM() { return highWPM; }
    public void setHighWPM(int highWPM) { this.highWPM = highWPM; }
}