package gui;

import gui.enums.PanelType;
import gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;

public class PlayerAuthentication extends JPanel {

    public PlayerAuthentication(MainWindow gui) {
        setLayout(new GridBagLayout());
        setBackground(new Color(255, 250, 240)); // Pastel background

        Font titleFont = new Font("Roboto", Font.BOLD, 16);
        Font subtitleFont = new Font("Roboto", Font.PLAIN, 12);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = GuiUtils.createLabel("Welcome to GoBang", SwingConstants.CENTER, titleFont, null);
        add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subtitleLabel = GuiUtils.createLabel("Please login or register to continue", SwingConstants.CENTER, subtitleFont, null);
        add(subtitleLabel, gbc);

        // Botões
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;

        JButton loginButton = GuiUtils.createButton("Login", new Color(173, 216, 230), e -> gui.changePanel(PanelType.LOGIN));
        add(loginButton, gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;

        JButton registerButton = GuiUtils.createButton("Register", new Color(240, 128, 128), e -> gui.changePanel(PanelType.REGISTRATION));
        add(registerButton, gbc);
    }
}
