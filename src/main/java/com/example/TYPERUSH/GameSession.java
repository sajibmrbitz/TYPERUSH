package com.example.TYPERUSH;

public class GameSession {
    public static final int PORT = 5555;

    public static String localPlayerName = "Host/Joiner";
    public static String opponentName = "Opponent";

    public static boolean isHost = false;
    public static String joinIp = "localhost";

    public static GameServer server;
    public static GameClient client;

    public static void sendStats(double progress, int wpm, int accuracy) {
        if (isHost && server != null) {
            server.sendStats(progress, wpm, accuracy);
        } else if (!isHost && client != null) {
            client.sendStats(progress, wpm, accuracy);
        }
    }

    public static void sendFinish() {
        if (isHost && server != null) {
            server.sendFinish(localPlayerName);
        } else if (!isHost && client != null) {
            client.sendFinish(localPlayerName);
        }
    }

    // NEW: Triggers the network thread to send the LEAVE message
    public static void sendLeave() {
        if (isHost && server != null) {
            server.sendLeave();
        } else if (!isHost && client != null) {
            client.sendLeave();
        }
    }

    public static void disconnect() {
        if (server != null) {
            server.stopServer();
            server = null;
        }
        if (client != null) {
            client.stopClient();
            client = null;
        }
    }
}