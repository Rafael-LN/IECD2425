package gui;

import common.UserProfileData;
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

public class ViewProfilePanel extends JPanel {

    private final MainWindow gui;
    private final UserProfileData profile;
    private final JLabel photoPreviewLabel;
    private byte[] newPhotoData;

    public ViewProfilePanel(MainWindow gui, UserProfileData profile) {
        this.gui = gui;
        this.profile = profile;

        setLayout(new GridBagLayout());
        setBackground(new Color(255, 250, 240)); // pastel background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = GuiUtils.createLabel("Your Profile", SwingConstants.CENTER,
                new Font("Roboto", Font.BOLD, 16), null);
        add(titleLabel, gbc);

        // Mostrar dados do utilizador
        gbc.gridy++;
        add(createUserInfoPanel(), gbc);

        // Preview da foto atual
        gbc.gridy++;
        photoPreviewLabel = new JLabel();
        photoPreviewLabel.setPreferredSize(new Dimension(150, 150));
        photoPreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(photoPreviewLabel, gbc);

        if (profile.photoBase64() != null && !profile.photoBase64().isEmpty()) {
            byte[] photoBytes = Base64.getDecoder().decode(profile.photoBase64());
            ImageIcon icon = new ImageIcon(photoBytes);
            Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            photoPreviewLabel.setIcon(new ImageIcon(image));
        }

        // Botões
        gbc.gridy++;
        add(createButtonsPanel(), gbc);
    }

    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));
        panel.setOpaque(false);

        panel.add(GuiUtils.createLabel("Nickname: " + profile.username(), SwingConstants.CENTER));
        panel.add(GuiUtils.createLabel("Age: " + profile.age(), SwingConstants.CENTER));
        panel.add(GuiUtils.createLabel("Nationality: " + profile.nationality(), SwingConstants.CENTER));
        panel.add(GuiUtils.createLabel("Wins: " + profile.wins(), SwingConstants.CENTER));
        panel.add(GuiUtils.createLabel("Losses: " + profile.losses(), SwingConstants.CENTER));
        panel.add(GuiUtils.createLabel("Time Played: " + formatTimePlayed(profile.timePlayed()), SwingConstants.CENTER));


        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setOpaque(false);

        JButton choosePhotoButton = GuiUtils.createButton("Choose New Photo", new Color(100, 149, 237), this::chooseNewPhoto);
        JButton removePhotoButton = GuiUtils.createButton("Remove Photo", new Color(240, 128, 128), _ -> {
            newPhotoData = null;
            photoPreviewLabel.setIcon(null);
        });
        JButton saveButton = GuiUtils.createButton("Save Changes", new Color(144, 238, 144), _ -> saveChanges());
        JButton backButton = GuiUtils.createButton("Back to Lobby", new Color(192, 192, 192), _ -> gui.changePanel(PanelType.LOBBY));

        panel.add(choosePhotoButton);
        panel.add(removePhotoButton);
        panel.add(saveButton);
        panel.add(backButton);

        return panel;
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
        campos.put("username", profile.username());
        campos.put("photo", Base64.getEncoder().encodeToString(newPhotoData));

        gui.sendRequest("updateProfile", campos);

        JOptionPane.showMessageDialog(this, "Profile updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        gui.changePanel(PanelType.LOBBY);
    }

    private String formatTimePlayed(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        return hours + "h " + minutes + "min";
    }
}
