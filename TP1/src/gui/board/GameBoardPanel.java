package gui.board;

import client.GoBangClient;
import common.XmlMessageBuilder;
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

        // Top panel
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

        // Board
        board = new BoardPanel(15, this);
        board.setReadOnly(!isMyTurn);
        add(board, BorderLayout.CENTER);

        // Bottom
        JButton quit = new JButton("Quit Game");
        quit.setBackground(new Color(240, 128, 128));
        quit.addActionListener(e -> gui.changePanel(PanelType.LOBBY));
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

        // Update UI
        cell.setEstado(CellState.PLAYER1);
        board.setReadOnly(true);
        isMyTurn = false;
        updateTurnLabel();

        // Send move to server
        Map<String, String> move = Map.of(
                "row", String.valueOf(row),
                "col", String.valueOf(col)
        );
        gui.sendRequest("move", move);

    }

    public void applyOpponentMove(int row, int col) {
        CellButton cell = board.getCell(row, col);
        cell.setEstado(CellState.PLAYER2);
        isMyTurn = true;
        board.setReadOnly(false);
        updateTurnLabel();
    }

    private void updateTurnLabel() {
        turnLabel.setText(isMyTurn ? "Your turn!" : "Waiting for opponent...");
    }
}
