package gui;

import gui.enums.PanelType;
import gui.utils.GuiUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class EditProfilePanel extends JPanel {

    private final MainWindow gui;
    private final String username;
    private JLabel photoPreviewLabel;
    private JButton choosePhotoButton, removePhotoButton, saveButton, backButton;
    private byte[] newPhotoData;

    public EditProfilePanel(MainWindow gui, String username) {
        this.gui = gui;
        this.username = username;

        setLayout(new GridBagLayout());
        setBackground(new Color(255, 250, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = GuiUtils.createLabel("Edit Your Profile", SwingConstants.CENTER, new Font("Roboto", Font.BOLD, 16), null);
        add(titleLabel, gbc);

        // Preview da foto atual
        gbc.gridy++;
        photoPreviewLabel = new JLabel();
        photoPreviewLabel.setPreferredSize(new Dimension(150, 150));
        photoPreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(photoPreviewLabel, gbc);

        // Botão para escolher nova foto
        gbc.gridy++;
        choosePhotoButton = GuiUtils.createButton("Choose New Photo", new Color(100, 149, 237), this::chooseNewPhoto);
        add(choosePhotoButton, gbc);

        // Botão para remover foto
        gbc.gridy++;
        removePhotoButton = GuiUtils.createButton("Remove Photo", new Color(240, 128, 128), e -> {
            newPhotoData = null;
            photoPreviewLabel.setIcon(null);
        });
        add(removePhotoButton, gbc);

        // Botão para guardar alterações
        gbc.gridy++;
        saveButton = GuiUtils.createButton("Save Changes", new Color(144, 238, 144), e -> saveChanges());
        add(saveButton, gbc);

        // Botão para voltar ao lobby
        gbc.gridy++;
        backButton = GuiUtils.createButton("Back to Lobby", new Color(192, 192, 192), e -> {
            gui.changePanel(PanelType.LOBBY);
        });
        add(backButton, gbc);
    }

    private void chooseNewPhoto(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                Path photoPath = fileChooser.getSelectedFile().toPath();
                newPhotoData = Files.readAllBytes(photoPath);

                ImageIcon icon = new ImageIcon(newPhotoData);
                Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                photoPreviewLabel.setIcon(new ImageIcon(image));

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveChanges() {
        if (newPhotoData == null) {
            JOptionPane.showMessageDialog(this, "Please choose a new photo.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, String> campos = new HashMap<>();
        campos.put("username", username);
        campos.put("photo", Base64.getEncoder().encodeToString(newPhotoData));

        gui.sendRequest("updateProfile", campos);

        JOptionPane.showMessageDialog(this, "Profile updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        gui.changePanel(PanelType.LOBBY);
    }
}
