package server.handler;

import server.ClientConnection;

import java.util.LinkedList;
import java.util.Queue;

public class MatchmakingQueue {

    private static final Queue<ClientConnection> queue = new LinkedList<>();

    /**
     * Adiciona um cliente à fila de matchmaking e tenta formar uma partida.
     */
    public static synchronized void addToQueue(ClientConnection client) {
        if (client == null || client.getUsername() == null) {
            System.err.println("⚠️ Cliente inválido para matchmaking.");
            return;
        }

        queue.add(client);
        System.out.println("🎯 Jogador adicionado à fila: " + client.getUsername() + " (Total na fila: " + queue.size() + ")");

        if (queue.size() >= 2) {
            ClientConnection player1 = queue.poll();
            ClientConnection player2 = queue.poll();

            GameMatch.startMatch(player1, player2);
        }
    }

    /**
     * Remove um cliente da fila de matchmaking.
     */
    public static synchronized void removeFromQueue(ClientConnection client) {
        if (client == null || client.getUsername() == null) {
            System.err.println("⚠️ Cliente inválido para remoção da fila.");
            return;
        }

        if (queue.removeIf(conn -> client.getUsername().equals(conn.getUsername()))) {
            System.out.println("🎯 Jogador removido da fila: " + client.getUsername() + " (Total na fila: " + queue.size() + ")");
        } else {
            System.out.println("⚠️ Jogador não encontrado na fila: " + client.getUsername());
        }
    }

}
