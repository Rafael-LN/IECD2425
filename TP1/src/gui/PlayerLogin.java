package gui;

import gui.enums.PanelType;
import gui.utils.GuiUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Map;

public class PlayerLogin extends JPanel {

    private JButton loginButton;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public PlayerLogin(MainWindow gui) {
        setLayout(new GridBagLayout());
        setBackground(new Color(255, 250, 240)); // Pastel background

        JButton backButton = GuiUtils.createButton("<- Back", new Color(192, 192, 192), e -> {
            gui.changePanel(PanelType.AUTHENTICATION);
        });
        add(backButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;

        // Spacer para empurrar para o centro
        JPanel spacerTop = new JPanel();
        spacerTop.setOpaque(false);
        add(spacerTop, gbc);

        // Username
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(GuiUtils.createLabel("Username:", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = GuiUtils.createTextField(15);
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(GuiUtils.createLabel("Password:", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = GuiUtils.createPasswordField(15);
        add(passwordField, gbc);

        // Spacer inferior
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        JPanel spacerBottom = new JPanel();
        spacerBottom.setOpaque(false);
        add(spacerBottom, gbc);

        // Botão de ‘login’
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.insets = new Insets(20, 5, 5, 5);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(new Color(255, 250, 240));

        loginButton = GuiUtils.createButton("Login", new Color(173, 216, 230), e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            Map<String, String> campos = Map.of(
                    "username", user,
                    "password", pass
            );
            gui.sendRequest("login", campos);
        });

        loginButton.setEnabled(false);
        buttonPanel.add(loginButton);
        add(buttonPanel, gbc);

        // Ativar/desativar botão dinamicamente
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateButton(); }
            public void removeUpdate(DocumentEvent e) { updateButton(); }
            public void changedUpdate(DocumentEvent e) { updateButton(); }

            private void updateButton() {
                String user = usernameField.getText().trim();
                String pass = new String(passwordField.getPassword()).trim();
                loginButton.setEnabled(!user.isEmpty() && !pass.isEmpty());
            }
        };

        usernameField.getDocument().addDocumentListener(docListener);
        passwordField.getDocument().addDocumentListener(docListener);
    }
}
