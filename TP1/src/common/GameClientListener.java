package common;

public interface GameClientListener {
    void onLoginSuccess(String username);
    void onLoginError(String msg);

    void onRegisterSuccess(String username);
    void onRegisterError(String msg);

    void onLogoutSuccess();
    void onLogoutError(String msg);

    // Futuro: outros eventos
    void onGameStart(String you, String opponent, boolean youStart);
    void onMove(int row, int col, String who);
    void onConnectionClosed();
    void onGameEnd(String winner, String reason, String message);
    void onProfileView(UserProfileData profile);
}
