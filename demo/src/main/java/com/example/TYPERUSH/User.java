package com.example.TYPERUSH;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String username;
    private String password;
    private List<RaceResult> history = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public List<RaceResult> getHistory() { return history; }
    public void addResult(RaceResult result) { history.add(result); }
}