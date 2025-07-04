package gui.board;

import javax.swing.*;
import java.awt.*;

public class CellButton extends JButton {

    private final int row;
    private final int col;
    private CellState estado;

    public CellButton(int row, int col) {
        this.row = row;
        this.col = col;
        this.estado = CellState.EMPTY;

        setPreferredSize(new Dimension(35, 35));
        setFont(new Font("Monospaced", Font.BOLD, 16));
        updateDisplay();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public CellState getEstado() {
        return estado;
    }

    public void setEstado(CellState estado) {
        this.estado = estado;
        updateDisplay();
    }

    private void updateDisplay() {
        switch (estado) {
            case PLAYER1 -> setText("X");
            case PLAYER2 -> setText("O");
            default -> setText("");
        }
        setEnabled(estado == CellState.EMPTY);
    }
}
