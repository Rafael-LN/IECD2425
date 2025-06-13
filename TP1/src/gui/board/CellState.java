package gui.board;

public enum CellState {
    EMPTY(" "),
    PLAYER1("X"),
    PLAYER2("O");

    private final String symbol;

    CellState(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
