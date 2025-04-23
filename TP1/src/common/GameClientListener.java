package common;

public interface GameClientListener {
    void onLoginSuccess(String username);
    void onLoginError(String msg);

    void onRegisterSuccess(String username);
    void onRegisterError(String msg);

    // Futuro: outros eventos
    void onGameStart(String you, String opponent, boolean youStart);
    void onConnectionClosed();
}
