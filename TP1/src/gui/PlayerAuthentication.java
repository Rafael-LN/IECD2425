package gui;

import gui.enums.PanelType;
import gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;

public class PlayerAuthentication extends JPanel {

    public PlayerAuthentication(MainWindow gui) {
        setBackground(new Color(255, 250, 240)); // Fundo pastel
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Fonte global para o painel
        Font titleFont = new Font("Roboto", Font.BOLD, 20);
        Font subtitleFont = new Font("Roboto", Font.PLAIN, 13);

        // Título
        JLabel titleLabel = GuiUtils.createLabel("Welcome to GoBang", SwingConstants.CENTER, titleFont, null);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(32)); // Espaço superior
        add(titleLabel);

        // Subtítulo
        JLabel subtitleLabel = GuiUtils.createLabel("Please login or register to continue", SwingConstants.CENTER, subtitleFont, null);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(8));
        add(subtitleLabel);

        add(Box.createVerticalStrut(16));
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(240, 4));
        add(separator);
        separator.setVisible(false);


        add(Box.createVerticalGlue());

        // Painel de botões centralizado
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 0));

        JButton loginButton = GuiUtils.createButton("Login", new Color(173, 216, 230),
                _ -> gui.changePanel(PanelType.LOGIN));
        loginButton.setFont(new Font("Roboto", Font.PLAIN, 15));

        JButton registerButton = GuiUtils.createButton("Register", new Color(240, 128, 128),
                _ -> gui.changePanel(PanelType.REGISTRATION));
        registerButton.setFont(new Font("Roboto", Font.PLAIN, 15));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(buttonPanel);

        add(Box.createVerticalGlue());
        add(Box.createVerticalStrut(32));
    }
}