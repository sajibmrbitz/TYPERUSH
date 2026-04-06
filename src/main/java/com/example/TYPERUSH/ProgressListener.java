package com.example.TYPERUSH;

public interface ProgressListener {
    void onParagraphReceived(String para);
    void onOpponentStats(double progress, int wpm, int accuracy);
    void onRaceFinished(String winnerName);
    void onError(String message);
    void onOpponentLeft(); //  Tells the screen the other player left
    void onCountdownStart();
}