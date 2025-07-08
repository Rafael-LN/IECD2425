package gui.board;

import gui.enums.PanelType;
import gui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GameBoardPanel extends JPanel implements ClickHandler {

    private final MainWindow gui;
    private final BoardPanel board;
    private final JLabel turnLabel;
    private boolean isMyTurn;
    private final String player;
    private final String opponent;

    public GameBoardPanel(MainWindow gui, String player, String opponent, boolean isMyTurn) {
        this.gui = gui;
        this.player = player;
        this.opponent = opponent;
        this.isMyTurn = isMyTurn;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(255, 250, 240));

        JLabel title = new JLabel("You: " + player + " vs " + opponent, SwingConstants.CENTER);
        title.setFont(new Font("Roboto", Font.BOLD, 16));

        turnLabel = new JLabel("", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        updateTurnLabel();

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(title);
        top.add(turnLabel);
        add(top, BorderLayout.NORTH);

        board = new BoardPanel(15, this);
        board.setReadOnly(!isMyTurn);
        add(board, BorderLayout.CENTER);

        JButton quit = new JButton("Quit Game");
        quit.setBackground(new Color(240, 128, 128));
        quit.addActionListener(_ -> {
            gui.sendRequest("quitMatch", Map.of("username", player));
            gui.changePanel(PanelType.LOBBY);
        });
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(quit);
        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void clicked(int row, int col) {
        if (!isMyTurn) return;

        CellButton cell = board.getCell(row, col);
        if (cell.getEstado() != CellState.EMPTY) return;

        // Enviar jogada para o servidor (sem aplicar localmente)
        Map<String, String> move = new HashMap<>();
        move.put("row", String.valueOf(row));
        move.put("col", String.valueOf(col));
        gui.sendRequest("move", move);

        board.setReadOnly(true);
        isMyTurn = false;
        updateTurnLabel();
    }

    public void applyMove(int row, int col, String who) {
        CellButton cell = board.getCell(row, col);
        CellState state = who.equals(player) ? CellState.PLAYER1 : CellState.PLAYER2;
        cell.setEstado(state);

        isMyTurn = who.equals(opponent);
        board.setReadOnly(!isMyTurn);
        updateTurnLabel();
    }

    private void updateTurnLabel() {
        turnLabel.setText(isMyTurn ? "Your turn!" : "Waiting for opponent...");
    }
}
