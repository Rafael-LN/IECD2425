package gui;

import gui.utils.GuiUtils;
import gui.utils.NumericDocumentFilter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PlayerRegistration extends JPanel {

    private JTextField nicknameField, nationalityField, ageField;
    private JPasswordField passwordField;
    private JButton photoButton, registerButton;
    private JLabel photoPreviewLabel;
    private byte[] photoData;

    public PlayerRegistration(MainWindow gui) {
        setLayout(new GridBagLayout());
        setBackground(new Color(255, 250, 240)); // pastel background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;

        // Preview da foto
        JPanel photoPanel = new JPanel();
        photoPanel.setLayout(new BoxLayout(photoPanel, BoxLayout.Y_AXIS));
        photoPanel.setBackground(getBackground());
        photoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        photoPreviewLabel = new JLabel();
        photoPreviewLabel.setPreferredSize(new Dimension(100, 100));
        photoPanel.add(GuiUtils.createLabel("Photo Preview:", SwingConstants.CENTER));
        photoPanel.add(photoPreviewLabel);

        gbc.gridheight = 6;
        gbc.anchor = GridBagConstraints.NORTH;
        add(photoPanel, gbc);

        // Campos de input
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 1;
        add(GuiUtils.createLabel("Nickname:", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        nicknameField = GuiUtils.createTextField(20);
        add(nicknameField, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(GuiUtils.createLabel("Password:", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = GuiUtils.createPasswordField(20);
        add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(GuiUtils.createLabel("Nationality:", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        nationalityField = GuiUtils.createTextField(20);
        add(nationalityField, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(GuiUtils.createLabel("Age:", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        ageField = GuiUtils.createTextField(20);
        ((AbstractDocument) ageField.getDocument()).setDocumentFilter(new NumericDocumentFilter()); // manter se tiveres

        add(ageField, gbc);

        // Botão da foto
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(GuiUtils.createLabel("Photo:", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        photoButton = GuiUtils.createButton("Choose Photo", new Color(100, 149, 237), this::choosePhoto);
        photoButton.setPreferredSize(new Dimension(150, 25));
        add(photoButton, gbc);

        // Botão de registo
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);

        registerButton = GuiUtils.createButton("Register", new Color(240, 128, 128), e -> registerPlayer(gui));
        add(registerButton, gbc);
    }

    private void choosePhoto(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                Path photoPath = fileChooser.getSelectedFile().toPath();
                photoData = Files.readAllBytes(photoPath);
                ImageIcon imageIcon = new ImageIcon(photoData);
                Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                photoPreviewLabel.setIcon(new ImageIcon(image));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao carregar imagem.");
            }
        }
    }

    private void registerPlayer(MainWindow gui) {
        String username = nicknameField.getText();
        String password = new String(passwordField.getPassword());
        String nationality = nationalityField.getText();
        String age = ageField.getText();

        if (username.isEmpty() || password.isEmpty() || nationality.isEmpty() || age.isEmpty() || photoData == null) {
            gui.showError("Campos incompletos", "Por favor preenche todos os campos e escolhe uma foto.");
            return;
        }

        Map<String, String> campos = new HashMap<>();
        campos.put("username", username);
        campos.put("password", password);
        campos.put("nationality", nationality);
        campos.put("age", age);
        campos.put("photo", Base64.getEncoder().encodeToString(photoData));

        gui.sendRequest("register", campos);
    }
}
