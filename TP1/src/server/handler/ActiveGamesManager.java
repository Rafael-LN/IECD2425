package server.handler;

import server.ClientConnection;
import server.handler.GameEndListener;

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

    private static GameEndListener gameEndListener;

    public static void setGameEndListener(GameEndListener listener) {
        gameEndListener = listener;
    }

    private static void handleVictory(GameMatch match, ClientConnection winner, ClientConnection loser, int row, int col) {
        String winMsg = common.XmlMessageBuilder.buildGameEnd(winner.getUsername(), "vitória", "Parabéns, ganhaste!");
        String loseMsg = common.XmlMessageBuilder.buildGameEnd(winner.getUsername(), "derrota", "Perdeste o jogo.");
        winner.send(winMsg);
        if (loser != null) loser.send(loseMsg);
        long duration = System.currentTimeMillis() - match.getStartTimeMillis();
        if (gameEndListener != null) {
            gameEndListener.onGameEnd(winner, loser, "vitória", duration);
        }
        removeMatch(winner);
    }

    private static void handleDraw(GameMatch match, ClientConnection p1, ClientConnection p2) {
        String drawMsg = common.XmlMessageBuilder.buildGameEnd("", "empate", "O jogo terminou empatado.");
        p1.send(drawMsg);
        if (p2 != null) p2.send(drawMsg);
        long duration = System.currentTimeMillis() - match.getStartTimeMillis();
        if (gameEndListener != null) {
            gameEndListener.onGameDraw(p1, p2, duration);
        }
        removeMatch(p1);
    }

    public static synchronized void processMove(ClientConnection client, int row, int col) {
        GameMatch match = activeGames.get(client);
        if (match != null) {
            boolean valid = match.applyMove(client, row, col);
            if (valid) {
                String moveXml = common.XmlMessageBuilder.buildMoveRequest(client.getUsername(), row, col);
                client.send(moveXml);
                ClientConnection opponent = match.getOpponent(client);
                if (opponent != null) {
                    opponent.send(moveXml);
                }
                // Verificar vitória
                if (match.isVictory(row, col)) {
                    handleVictory(match, client, opponent, row, col);
                } else if (match.isDraw()) {
                    handleDraw(match, client, opponent);
                }
            } else {
                client.send(common.XmlMessageBuilder.buildResponse(
                    "error",
                    "Jogada inválida (posição ocupada ou não é o seu turno).",
                    "move"
                ));
            }
        } else {
            System.err.println("⚠️ Jogador não está em nenhum jogo ativo.");
        }
    }
}
