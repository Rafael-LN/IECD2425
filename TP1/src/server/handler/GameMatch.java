package server.handler;

import common.XmlMessageBuilder;
import gui.board.CellState;
import server.ClientConnection;

public class GameMatch {

    private final ClientConnection player1;
    private final ClientConnection player2;
    private final CellState[][] board;
    private ClientConnection currentTurn;

    public GameMatch(ClientConnection p1, ClientConnection p2) {
        this.player1 = p1;
        this.player2 = p2;
        this.currentTurn = p1;
        this.board = new CellState[15][15];

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                board[i][j] = CellState.EMPTY;
            }
        }
    }

    public static void startMatch(ClientConnection p1, ClientConnection p2) {
        String xml1 = XmlMessageBuilder.buildGameStart(p1.getUsername(), p2.getUsername(), true);
        String xml2 = XmlMessageBuilder.buildGameStart(p2.getUsername(), p1.getUsername(), false);

        System.out.println("📤 A enviar gameStart para " + p1.getUsername());
        p1.send(xml1);
        System.out.println("✅ Enviado para " + p1.getUsername());

        System.out.println("📤 A enviar gameStart para " + p2.getUsername());
        p2.send(xml2);
        System.out.println("✅ Enviado para " + p2.getUsername());

        ActiveGamesManager.registerGame(p1, p2, new GameMatch(p1, p2));

        System.out.println("✅ Partida iniciada entre " + p1.getUsername() + " e " + p2.getUsername());
    }

    public boolean applyMove(ClientConnection player, int row, int col) {
        if (!player.equals(currentTurn)) return false;
        if (board[row][col] != CellState.EMPTY) return false;

        board[row][col] = player.equals(player1) ? CellState.PLAYER1 : CellState.PLAYER2;
        currentTurn = player.equals(player1) ? player2 : player1;
        return true;
    }

    public ClientConnection getOpponent(ClientConnection player) {
        return player.equals(player1) ? player2 : player1;
    }

    public ClientConnection getCurrentTurn() {
        return currentTurn;
    }

    public CellState getCellState(int row, int col) {
        return board[row][col];
    }

    public ClientConnection getPlayer1() {
        return player1;
    }

    public ClientConnection getPlayer2() {
        return player2;
    }
}
