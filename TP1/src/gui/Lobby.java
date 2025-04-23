package gui;

import gui.enums.PanelType;
import gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class Lobby extends JPanel {

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
        JButton searchButton = GuiUtils.createButton("Find Match", new Color(100, 149, 237), e -> {
            Map<String, String> dados = Map.of("username", username);
            gui.sendRequest("findMatch", dados);
        });
        add(searchButton, gbc);

        gbc.gridy++;
        JButton logoutButton = GuiUtils.createButton("Logout", new Color(240, 128, 128), e -> {
            gui.sendRequest("logout", Map.of("username", username));
            gui.changePanel(PanelType.AUTHENTICATION);
        });
        add(logoutButton, gbc);
    }
}
