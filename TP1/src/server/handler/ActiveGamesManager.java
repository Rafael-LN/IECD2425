package server.handler;

import server.ClientConnection;

import java.util.HashMap;
import java.util.Map;

public class ActiveGamesManager {

    private static final Map<String, GameMatch> activeGames = new HashMap<>();

    public static synchronized void registerGame(ClientConnection p1, ClientConnection p2, GameMatch match) {
        activeGames.put(p1.getUsername(), match);
        activeGames.put(p2.getUsername(), match);
    }

    public static synchronized GameMatch getMatch(String username) {
        return activeGames.get(username);
    }

    public static synchronized void removeMatch(String username) {
        GameMatch match = activeGames.get(username);
        if (match != null) {
            activeGames.remove(match.getCurrentTurn().getUsername());
            activeGames.remove(match.getOpponent(match.getCurrentTurn()).getUsername());
        }
    }

    public static synchronized boolean isInGame(String username) {
        return activeGames.containsKey(username);
    }
}
