package server.handler;

import server.ClientConnection;

import java.util.HashMap;
import java.util.Map;

public class ActiveGamesManager {

    // Mapa: jogador → adversário
    private static final Map<String, String> activeGames = new HashMap<>();

    /**
     * Regista um novo jogo entre dois jogadores.
     */
    public static synchronized void registerGame(ClientConnection player1, ClientConnection player2) {
        activeGames.put(player1.getUsername(), player2.getUsername());
        activeGames.put(player2.getUsername(), player1.getUsername());

        System.out.println("🕹️ Jogo registado: " + player1.getUsername() + " vs " + player2.getUsername());
    }

    /**
     * Retorna o adversário de um jogador.
     */
    public static synchronized String getOpponent(String username) {
        return activeGames.get(username);
    }

    /**
     * Remove um jogo ativo (ex: após terminar ou desconexão).
     */
    public static synchronized void removeGame(String username) {
        String opponent = activeGames.remove(username);
        if (opponent != null) {
            activeGames.remove(opponent);
            System.out.println("🛑 Jogo terminado: " + username + " vs " + opponent);
        }
    }

    /**
     * Verifica se um jogador está atualmente num jogo.
     */
    public static synchronized boolean isPlaying(String username) {
        return activeGames.containsKey(username);
    }
}
