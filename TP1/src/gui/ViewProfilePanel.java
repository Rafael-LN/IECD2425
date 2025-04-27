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
    private JLabel photoPreviewLabel;
    private byte[] newPhotoData;

    public ViewProfilePanel(MainWindow gui, UserProfileData profile) {
        this.gui = gui;
        this.profile = profile;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(255, 250, 240));

        // Título
        JLabel titleLabel = GuiUtils.createLabel("Your Profile", SwingConstants.CENTER, new Font("Roboto", Font.BOLD, 20), null);
        add(titleLabel, BorderLayout.NORTH);

        // Corpo: informação + foto
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        centerPanel.setOpaque(false);

        centerPanel.add(createUserInfoPanel());
        centerPanel.add(createPhotoPanel());

        add(centerPanel, BorderLayout.CENTER);

        // Botões
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20); // padding (top, left, bottom, right)
        gbc.anchor = GridBagConstraints.WEST; // alinhar à esquerda
        gbc.gridx = 0;
        gbc.gridy = 0;

        addInfoRow(panel, gbc, "Nickname:", profile.username());
        addInfoRow(panel, gbc, "Age:", String.valueOf(profile.age()));
        addInfoRow(panel, gbc, "Nationality:", profile.nationality());
        addInfoRow(panel, gbc, "Wins:", String.valueOf(profile.wins()));
        addInfoRow(panel, gbc, "Losses:", String.valueOf(profile.losses()));
        addInfoRow(panel, gbc, "Time Played:", formatTimePlayed(profile.timePlayed()));

        return panel;
    }

    private void addInfoRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Roboto", Font.BOLD, 14));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Roboto", Font.PLAIN, 14));

        gbc.gridx = 0;
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        panel.add(valueComponent, gbc);

        gbc.gridy++;
    }


    private JPanel createPhotoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        photoPreviewLabel = new JLabel();
        photoPreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoPreviewLabel.setPreferredSize(new Dimension(200, 250));
        photoPreviewLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (profile.photoBase64() != null && !profile.photoBase64().isEmpty()) {
            byte[] photoBytes = Base64.getDecoder().decode(profile.photoBase64());
            ImageIcon icon = new ImageIcon(photoBytes);
            Image image = icon.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
            photoPreviewLabel.setIcon(new ImageIcon(image));
        }

        panel.add(photoPreviewLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
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
                Image image = icon.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
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
