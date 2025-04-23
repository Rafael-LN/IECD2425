package gui;

import client.GoBangClient;
import common.GameClientListener;
import gui.enums.PanelType;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static javax.swing.JOptionPane.*;

public class MainWindow extends JFrame implements GameClientListener {

    private final GoBangClient client;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

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
        SwingUtilities.invokeLater(() -> {
            Lobby lobby = new Lobby(this, username);
            cardPanel.add(lobby, PanelType.LOBBY.name());
            changePanel(PanelType.LOBBY);
        });
    }


    @Override
    public void onLoginError(String msg) {
        showError("Login falhou", msg);
    }

    @Override
    public void onRegisterSuccess(String username) {
        showInfo("Registo completo", "Utilizador registado com sucesso.");
        Lobby lobby = new Lobby(this, username);
        cardPanel.add(lobby, PanelType.LOBBY.name());
        changePanel(PanelType.LOBBY);
    }


    @Override
    public void onRegisterError(String msg) {
        showError("Registo falhou", msg);
    }

    @Override
    public void onGameStart(String you, String opponent, boolean youStart) {
        // futura lógica
    }

    @Override
    public void onConnectionClosed() {
        showError("Ligação encerrada", "O servidor fechou a ligação.");
        dispose();
    }
}
