package gui.board;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private final int size;
    private final CellButton[][] cells;
    private boolean readOnly = false;
    private ClickHandler handler;

    public BoardPanel(int size, ClickHandler handler) {
        this.size = size;
        this.handler = handler;
        this.cells = new CellButton[size][size];

        setLayout(new GridLayout(size, size));

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                CellButton btn = new CellButton(row, col);
                btn.addActionListener(e -> {
                    if (!readOnly && handler != null) {
                        handler.clicked(btn.getRow(), btn.getCol());
                    }
                });
                cells[row][col] = btn;
                add(btn);
            }
        }
    }

    public CellButton getCell(int row, int col) {
        return cells[row][col];
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
