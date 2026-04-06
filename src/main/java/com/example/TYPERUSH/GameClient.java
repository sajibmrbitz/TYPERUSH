package com.example.TYPERUSH;

import java.io.*;
import java.net.Socket;

public class GameClient extends Thread {
    private String serverIP;
    private ProgressListener listener;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running = true;

    public GameClient(String serverIP, ProgressListener listener) {
        this.serverIP = serverIP;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIP, GameSession.PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("NAME:" + GameSession.localPlayerName);

            String input;
            while (running && (input = in.readLine()) != null) {
                if (input.startsWith("NAME:")) {
                    GameSession.opponentName = input.substring(5);
                } else if (input.startsWith("PARA:")) {
                    listener.onParagraphReceived(input.substring(5));

                }
                else if(input.startsWith("COUNTDOWN:")){
                    listener.onCountdownStart();
                }
                else if (input.startsWith("STATS:")) {
                    String[] parts = input.substring(6).split(":");
                    double progress = Double.parseDouble(parts[0]);
                    int wpm = Integer.parseInt(parts[1]);
                    int acc = Integer.parseInt(parts[2]);
                    listener.onOpponentStats(progress, wpm, acc);
                } else if (input.startsWith("FINISH:")) {
                    listener.onRaceFinished(input.substring(7));
                } else if (input.equals("LEAVE:")) {
                    listener.onOpponentLeft();
                    break;
                }
            }
        } catch (IOException e) {
            if (running) listener.onError("Could not connect to host.");
        } finally {
            close();
        }
    }

    public void sendStats(double progress, int wpm, int accuracy) {
        if (out != null) out.println("STATS:" + progress + ":" + wpm + ":" + accuracy);
    }

    public void sendFinish(String winnerName) {
        if (out != null) out.println("FINISH:" + winnerName);
    }

    public void sendLeave() {
        if (out != null) out.println("LEAVE:");
    }

    public void stopClient() {
        running = false;
        close();
    }

    private void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}