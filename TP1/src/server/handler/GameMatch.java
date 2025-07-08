package server.handler;

import common.XmlMessageBuilder;
import gui.board.CellState;
import server.ClientConnection;

public class GameMatch {

    private final ClientConnection player1;
    private final ClientConnection player2;
    private final CellState[][] board;
    private ClientConnection currentTurn;
    private final long startTimeMillis;

    public GameMatch(ClientConnection p1, ClientConnection p2) {
        this.player1 = p1;
        this.player2 = p2;
        this.currentTurn = p1;
        this.board = new CellState[15][15];
        this.startTimeMillis = System.currentTimeMillis();
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

    // Verifica se houve vitória a partir da última jogada
    public boolean isVictory(int row, int col) {
        CellState playerState = board[row][col];
        if (playerState == CellState.EMPTY) return false;
        // Verifica em todas as direções (horizontal, vertical, diagonais)
        return checkDirection(row, col, 0, 1, playerState)   // Horizontal
            || checkDirection(row, col, 1, 0, playerState)   // Vertical
            || checkDirection(row, col, 1, 1, playerState)   // Diagonal descendente
            || checkDirection(row, col, 1, -1, playerState); // Diagonal ascendente
    }

    // Verifica se há 5 em linha numa direção
    private boolean checkDirection(int row, int col, int dRow, int dCol, CellState playerState) {
        int count = 1;
        // Para trás
        int r = row - dRow, c = col - dCol;
        while (isValid(r, c) && board[r][c] == playerState) {
            count++;
            r -= dRow;
            c -= dCol;
        }
        // Para a frente
        r = row + dRow;
        c = col + dCol;
        while (isValid(r, c) && board[r][c] == playerState) {
            count++;
            r += dRow;
            c += dCol;
        }
        return count >= 5;
    }

    // Verifica se a posição está dentro do tabuleiro
    private boolean isValid(int row, int col) {
        return row >= 0 && row < 15 && col >= 0 && col < 15;
    }

    // Verifica se o tabuleiro está cheio (empate)
    public boolean isDraw() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] == CellState.EMPTY) {
                    return false;
                }
            }
        }
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

    public long getDurationMillis() {
        return System.currentTimeMillis() - startTimeMillis;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }
}
