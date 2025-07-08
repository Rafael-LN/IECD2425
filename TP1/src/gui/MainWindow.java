package gui;

import client.GoBangClient;
import common.GameClientListener;
import common.UserProfileData;
import gui.board.GameBoardPanel;
import gui.enums.PanelType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import static javax.swing.JOptionPane.*;

public class MainWindow extends JFrame implements GameClientListener {

    private final GoBangClient client;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private GameBoardPanel gamePanel;

    public MainWindow(GoBangClient client) {
        this.client = client;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(255, 250, 240));

        // Inicializar painéis
        cardPanel.add(new PlayerAuthentication(this), PanelType.AUTHENTICATION.name());
        cardPanel.add(new PlayerLogin(this), PanelType.LOGIN.name());
        cardPanel.add(new PlayerRegistration(this), PanelType.REGISTRATION.name());


        setContentPane(cardPanel);
        changePanel(PanelType.AUTHENTICATION);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client.getProfile() != null && client.getProfile().username() != null) {
                    client.logout(client.getProfile().username());
                }
            }
        });
    }

    public void changePanel(PanelType pt) {
        setTitle(pt.getTitle());
        setSize(pt.getWidth(), pt.getHeight());
        setLocationRelativeTo(null);
        cardLayout.show(cardPanel, pt.name());
    }

    public void sendRequest(String type, Map<String, String> campos) {
        switch (type) {
            case "login" -> client.login(
                    campos.get("username"),
                    campos.get("password")
            );
            case "register" -> client.register(
                    campos.get("username"),
                    campos.get("password"),
                    Integer.parseInt(campos.get("age")),
                    campos.get("nationality"),
                    campos.get("photo")
            );

            case "findMatch" -> client.findMatch(
                    campos.get("username")
            );

            case "cancelMatch" -> client.cancelMatch(
                    campos.get("username")
            );

            case "quitMatch" -> client.quitMatch(
                    campos.get("username")
            );

            case "updateProfile" -> client.updateProfile(
                    campos.get("username"),
                    campos.get("photo")
            );

            case "requestProfile" -> client.requestProfile(
                    campos.get("username")
            );

            case "move" -> client.sendMove(campos);

            case "logout" -> client.logout(
                    campos.get("username")
            );

        }
    }

    public void showError(String title, String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, title, ERROR_MESSAGE)
        );
    }

    public void showInfo(String title, String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, title, INFORMATION_MESSAGE)
        );
    }

    @Override
    public void onLoginSuccess(String username) {
        showInfo("Login successful", "User authenticated successfully.");
        SwingUtilities.invokeLater(() -> {
            Lobby lobby = new Lobby(this, username);
            cardPanel.add(lobby, PanelType.LOBBY.name());
            changePanel(PanelType.LOBBY);
        });
    }


    @Override
    public void onLoginError(String msg) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(this, msg, "Login Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    @Override
    public void onRegisterSuccess(String username) {
        showInfo("Registration complete", "User registered successfully.");
        Lobby lobby = new Lobby(this, username);
        cardPanel.add(lobby, PanelType.LOBBY.name());
        changePanel(PanelType.LOBBY);
    }

    @Override
    public void onRegisterError(String msg) {
        showError("Registration failed", msg);
    }

    @Override
    public void onLogoutSuccess(){
        showInfo("Logout complete", "User logged out successfully.");
        changePanel(PanelType.AUTHENTICATION);
    }

    @Override
    public void onLogoutError(String msg) {
        showError("Logout failed", msg);
    }

    @Override
    public void onGameStart(String you, String opponent, boolean youStart) {
        SwingUtilities.invokeLater(() -> {
            gamePanel = new GameBoardPanel(this, you, opponent, youStart);
            cardPanel.add(gamePanel, PanelType.GAME.name());
            changePanel(PanelType.GAME);
        });
    }

    @Override
    public void onMove(int row, int col, String who) {
        if (gamePanel != null) {
            SwingUtilities.invokeLater(() -> gamePanel.applyMove(row, col, who));
        }
    }

    @Override
    public void onConnectionClosed() {
        showError("Connection closed", "The server closed the connection.");
        dispose();
    }

    @Override
    public void onGameEnd(String winner, String reason, String message) {
        String title = "Game Over";
        StringBuilder msg = new StringBuilder();
        if (message != null && !message.isBlank()) {
            msg.append(message);
        } else {
            // Mensagem fallback se não vier nada do servidor
            if (reason != null) {
                msg.append("Result: ").append(reason).append("\n");
            }
            if (winner != null && !winner.isEmpty()) {
                msg.append("Winner: ").append(winner).append("\n");
            }
        }
        JOptionPane.showMessageDialog(this, msg.toString(), title, JOptionPane.INFORMATION_MESSAGE);

        SwingUtilities.invokeLater(() -> {
            for (Component comp : cardPanel.getComponents()) {
                if (comp instanceof Lobby lobby) {
                    lobby.resetMatchmakingUI();
                }
            }
            changePanel(gui.enums.PanelType.LOBBY);
        });
    }

    @Override
    public void onProfileView(UserProfileData profile) {
        SwingUtilities.invokeLater(() -> {
            ViewProfilePanel profilePanel = new ViewProfilePanel(this, profile);
            cardPanel.add(profilePanel, PanelType.VIEW_PROFILE.name());
            changePanel(PanelType.VIEW_PROFILE);
        });
    }
}
