package server.handler;

import server.ClientConnection;

/**
 * Interface para notificação do fim do jogo (vitória ou empate).
 * Permite separar a lógica de gestão de jogos da lógica de atualização de histórico e persistência.
 */
public interface GameEndListener {
    void onGameEnd(ClientConnection winner, ClientConnection loser, String result, long duration);
    void onGameDraw(ClientConnection p1, ClientConnection p2, long duration);
}

