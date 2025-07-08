package gui;

import gui.enums.PanelType;
import gui.utils.GuiUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Map;

public class PlayerLogin extends JPanel {

    private final JButton loginButton;
    private final JButton backButton;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public PlayerLogin(MainWindow gui) {
        setBackground(new Color(255, 250, 240));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = GuiUtils.createLabel("Username:", SwingConstants.RIGHT);
        add(userLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        usernameField = GuiUtils.createTextField(15);
        usernameField.setPreferredSize(new Dimension(140, 24));
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        JLabel passLabel = GuiUtils.createLabel("Password:", SwingConstants.RIGHT);
        add(passLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        passwordField = GuiUtils.createPasswordField(15);
        passwordField.setPreferredSize(new Dimension(140, 24));
        add(passwordField, gbc);

        // Painel para os botões Back e Login, centralizados e lado a lado
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(24, 8, 8, 8);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonsPanel.setOpaque(false);

        backButton = GuiUtils.createButton("<- Back", new Color(192, 192, 192), _ -> gui.changePanel(PanelType.AUTHENTICATION));
        loginButton = GuiUtils.createButton("Login", new Color(173, 216, 230), _ -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            Map<String, String> campos = Map.of(
                    "username", user,
                    "password", pass
            );
            gui.sendRequest("login", campos);
        });
        loginButton.setEnabled(false);

        buttonsPanel.add(backButton);
        buttonsPanel.add(loginButton);

        add(buttonsPanel, gbc);

        // DocumentListener para ativar/desativar o botão de login
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