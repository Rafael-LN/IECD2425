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
            boolean valid = match.applyMove(client, row, col);
            if (valid) {
                // Mensagem de jogada válida para ambos os jogadores
                String moveXml = common.XmlMessageBuilder.buildMoveRequest(client.getUsername(), row, col);
                client.send(moveXml); // Confirmação ao jogador que jogou
                ClientConnection opponent = match.getOpponent(client);
                if (opponent != null) {
                    opponent.send(moveXml); // Atualização ao adversário
                }
                // Aqui podes adicionar lógica para verificar vitória/empate e enviar mensagem de fim de jogo
            } else {
                // Jogada inválida
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
