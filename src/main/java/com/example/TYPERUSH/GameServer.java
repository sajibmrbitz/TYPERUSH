package com.example.TYPERUSH;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer extends Thread {
    private ProgressListener listener;
    private String paragraph;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running = true;

    public GameServer(String paragraph, ProgressListener listener) {
        this.paragraph = paragraph;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(GameSession.PORT);
            clientSocket = serverSocket.accept();

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("NAME:" + GameSession.localPlayerName);
            out.println("PARA:" + paragraph);


            String firstLine = in.readLine();
            if (firstLine != null && firstLine.startsWith("NAME:")) {
                GameSession.opponentName = firstLine.substring(5); // ← store joiner's name
            }

            listener.onParagraphReceived(paragraph);
            out.println("COUNTDOWN:");
            listener.onCountdownStart();

            String input;
            while (running && (input = in.readLine()) != null) {
                if (input.startsWith("NAME:")) {
                    GameSession.opponentName = input.substring(5);
                } else if (input.startsWith("STATS:")) {
                    String[] parts = input.substring(6).split(":");
                    double progress = Double.parseDouble(parts[0]);
                    int wpm = Integer.parseInt(parts[1]);
                    int acc = Integer.parseInt(parts[2]);
                    listener.onOpponentStats(progress, wpm, acc);
                } else if (input.startsWith("FINISH:")) {
                    listener.onRaceFinished(input.substring(7));
                } else if (input.equals("LEAVE:")) {
                    listener.onOpponentLeft(); // NEW: Handle opponent leaving
                    break;
                }
            }
        } catch (IOException e) {
            if (running) listener.onError("Connection Lost.");
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

    // NEW: Send the command out
    public void sendLeave() {
        if (out != null) out.println("LEAVE:");
    }

    public void stopServer() {
        running = false;
        close();
    }

    private void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}