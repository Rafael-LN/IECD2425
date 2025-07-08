package gui;

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

            Map<String, String> dados = Map.of("username", username);
            gui.sendRequest("findMatch", dados);
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

        // Botão "Ver/Editar Perfil"
        gbc.gridy++;
        JButton profileButton = GuiUtils.createButton("View/Edit Profile", new Color(144, 238, 144), _ -> {
            Map<String, String> dados = Map.of("username", username);
            gui.sendRequest("requestProfile", dados);
        });
        add(profileButton, gbc);

        // Botão "Logout"
        gbc.gridy++;
        JButton logoutButton = GuiUtils.createButton("Logout", new Color(240, 128, 128), _ -> {
            gui.sendRequest("logout", Map.of("username", username));
        });
        add(logoutButton, gbc);
    }

    public void resetMatchmakingUI() {
        if (searchButton != null) searchButton.setVisible(true);
        if (cancelButton != null) cancelButton.setVisible(false);
        // Procura a label de pesquisa e limpa o texto
        for (Component comp : getComponents()) {
            if (comp instanceof JLabel label && label.getText().contains("Searching for a match")) {
                label.setText("");
            }
        }
    }
}
