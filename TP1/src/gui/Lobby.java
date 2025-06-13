package gui;

import common.UserProfileData;
import gui.enums.PanelType;
import gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class Lobby extends JPanel {

    private JButton searchButton, cancelButton;

    public Lobby(MainWindow gui, String username) {

        setLayout(new GridBagLayout());
        setBackground(new Color(255, 250, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel welcomeLabel = GuiUtils.createLabel("Welcome to the Lobby!", SwingConstants.CENTER,
                new Font("Roboto", Font.BOLD, 16), null);
        add(welcomeLabel, gbc);

        gbc.gridy++;
        JLabel userLabel = GuiUtils.createLabel("Player: " + username, SwingConstants.CENTER);
        add(userLabel, gbc);

        gbc.gridy++;
        JLabel searchingLabel  = GuiUtils.createLabel("", SwingConstants.CENTER);
        add(searchingLabel , gbc);

        // Botão "Find Match"
        gbc.gridy++;
        searchButton = GuiUtils.createButton("Find Match", new Color(100, 149, 237), _ -> {
            // UI update imediato
            searchingLabel.setText("Searching for a match...");
            searchButton.setVisible(false);
            cancelButton.setVisible(true);

            // Enviar pedido numa thread separada
            new Thread(() -> {
                Map<String, String> dados = Map.of("username", username);
                gui.sendRequest("findMatch", dados);
            }).start();
        });
        add(searchButton, gbc);

        cancelButton = GuiUtils.createButton("Cancel", new Color(100, 149, 237), _ -> {
            Map<String, String> dados = Map.of("username", username);
            gui.sendRequest("cancelMatch", dados);

            // Reset UI
            searchingLabel.setText("");
            cancelButton.setVisible(false);
            searchButton.setVisible(true);
        });
        cancelButton.setVisible(false);
        add(cancelButton, gbc);


        add(searchButton, gbc);

        // Botão "Edit Profile"
        gbc.gridy++;
        JButton editProfileButton = GuiUtils.createButton("Edit Profile", new Color(173, 216, 230), _ -> {
            UserProfileData profile = gui.getLoggedUserProfile();
            ViewProfilePanel viewProfilePanel = new ViewProfilePanel(gui, profile);
            gui.addPanel(viewProfilePanel, PanelType.VIEW_PROFILE);
            gui.changePanel(PanelType.VIEW_PROFILE);

        });
        add(editProfileButton, gbc);

        // Botão "Logout"
        gbc.gridy++;
        JButton logoutButton = GuiUtils.createButton("Logout", new Color(240, 128, 128), _ -> {
            gui.sendRequest("logout", Map.of("username", username));
            gui.changePanel(PanelType.AUTHENTICATION);
        });
        add(logoutButton, gbc);
    }
}
