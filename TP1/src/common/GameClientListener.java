package common;

public interface GameClientListener {
    void onLoginSuccess();
    void onLoginError(String msg);

    void onRegisterSuccess();
    void onRegisterError(String msg);

    // Futuro: outros eventos
    void onGameStart(String you, String opponent, boolean youStart);
    void onConnectionClosed();
}
