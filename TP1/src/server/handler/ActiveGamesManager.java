package server.handler;

import server.ClientConnection;

import java.util.HashMap;
import java.util.Map;

public class ActiveGamesManager {

    private static final Map<ClientConnection, GameMatch> activeGames = new HashMap<>();

    public static synchronized void registerGame(ClientConnection p1, ClientConnection p2, GameMatch match) {
        activeGames.put(p1, match);
        activeGames.put(p2, match);
    }

    public static synchronized GameMatch getMatch(ClientConnection client) {
        return activeGames.get(client);
    }

    public static synchronized void removeMatch(ClientConnection client) {
        GameMatch match = activeGames.get(client);
        if (match != null) {
            activeGames.remove(match.getPlayer1());
            activeGames.remove(match.getPlayer2());
        }
    }

    public static synchronized boolean isInGame(ClientConnection client) {
        return activeGames.containsKey(client);
    }

    public static synchronized void processMove(ClientConnection client, int row, int col) {
        GameMatch match = activeGames.get(client);
        if (match != null) {
            match.applyMove(client, row, col);
        } else {
            System.err.println("⚠️ Jogador não está em nenhum jogo ativo.");
        }
    }
}
