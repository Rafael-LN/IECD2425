package gui.enums;

public enum PanelType {
    AUTHENTICATION("GoBang Game", 400, 200),
    LOGIN("Login", 400, 250),
    REGISTRATION("Player Registration", 600, 300),
    VIEW_PROFILE("View Profile", 600, 500),
    LOBBY("Lobby", 500, 300),
    GAME("Game", 800, 800);

    private final String title;
    private final int width, height;

    PanelType(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
