package gui;

import common.UserProfileData;
import common.GameHistory;
import gui.enums.PanelType;
import gui.utils.GuiUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewProfilePanel extends JPanel {

    private final MainWindow gui;
    private final UserProfileData profile;
    private JLabel photoPreviewLabel;
    private byte[] newPhotoData;
    private JButton saveButton;

    public ViewProfilePanel(MainWindow gui, UserProfileData profile) {
        this.gui = gui;
        this.profile = profile;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(255, 250, 240));
        setPreferredSize(new Dimension(950, 500));

        // Title
        JLabel titleLabel = GuiUtils.createLabel("Your Profile", SwingConstants.CENTER, new Font("Roboto", Font.BOLD, 22), null);
        add(titleLabel, BorderLayout.NORTH);

        // Main content – user info, photo, history
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setOpaque(false);

        JPanel userInfoPanel = createUserInfoPanel();
        JPanel photoPanel = createPhotoPanel();
        JPanel historyPanel = createHistoryPanel();

        // Add space between blocks for better visual separation
        centerPanel.add(Box.createHorizontalStrut(24));
        centerPanel.add(userInfoPanel);
        centerPanel.add(Box.createHorizontalStrut(24));
        centerPanel.add(photoPanel);
        centerPanel.add(Box.createHorizontalStrut(24));
        centerPanel.add(historyPanel);
        centerPanel.add(Box.createHorizontalStrut(24));

        add(centerPanel, BorderLayout.CENTER);

        // Buttons panel at the bottom
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }


    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 16, 6, 16);
        gbc.anchor = GridBagConstraints.WEST;
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
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        photoPreviewLabel = new JLabel();
        photoPreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoPreviewLabel.setPreferredSize(new Dimension(200, 250));
        photoPreviewLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        setProfilePhoto(profile.photoBase64());
        panel.add(photoPreviewLabel, gbc);

        return panel;
    }

    private void setProfilePhoto(String base64) {
        if (base64 != null && !base64.isEmpty()) {
            try {
                byte[] photoBytes = Base64.getDecoder().decode(base64);
                ImageIcon icon = new ImageIcon(photoBytes);
                Image image = icon.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
                photoPreviewLabel.setIcon(new ImageIcon(image));
            } catch (Exception ignored) {
            }
        }
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel histLabel = GuiUtils.createLabel("Game History", SwingConstants.CENTER,
                new Font("Roboto", Font.BOLD, 16), null);
        panel.add(histLabel, BorderLayout.NORTH);

        List<GameHistory> history = profile.gamesHistory();
        String[] columns = {"Date", "Duration", "Opponent", "Result"};
        String[][] data;
        if (history == null || history.isEmpty()) {
            data = new String[][]{{"No games recorded", "", "", ""}};
        } else {
            data = new String[history.size()][4];
            for (int i = 0; i < history.size(); i++) {
                GameHistory entry = history.get(i);
                data[i][0] = formatDateTime(entry.dateTime());
                data[i][1] = formatDuration(entry.duration());
                data[i][2] = entry.opponent();
                data[i][3] = entry.result();
            }
        }
        JTable table = new JTable(data, columns);
        table.setFont(new Font("Roboto", Font.PLAIN, 13));
        table.setRowHeight(22);
        table.setEnabled(false);
        table.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 13));
        table.setBackground(new Color(255, 250, 240));

        // Centralizar colunas e cabeçalhos
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(i).setHeaderRenderer(centerRenderer);
        }

        // Ajustar larguras
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(130); // Date
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // Duration
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Opponent
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Result

        // Tornar a tabela scrollável e bem dimensionada
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(420, 220));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBackground(new Color(255, 250, 240));
        panel.add(scrollPane, BorderLayout.CENTER);

        panel.setPreferredSize(new Dimension(430, 260));
        return panel;
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    private String formatDuration(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return minutes + "m " + seconds + "s";
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        panel.setOpaque(false);

        JButton choosePhotoButton = GuiUtils.createButton("Choose New Photo", new Color(100, 149, 237), this::chooseNewPhoto);
        choosePhotoButton.setToolTipText("Select a new profile photo (jpg/png/gif)");

        JButton removePhotoButton = GuiUtils.createButton("Remove Photo", new Color(240, 128, 128), _ -> {
            newPhotoData = null;
            saveButton.setEnabled(true);
        });
        removePhotoButton.setToolTipText("Remove the profile photo and set the default image");

        saveButton = GuiUtils.createButton("Save Changes", new Color(144, 238, 144), _ -> saveChanges());
        saveButton.setToolTipText("Save the new profile photo");
        saveButton.setEnabled(false); // Only enable if there are changes

        JButton backButton = GuiUtils.createButton("Back to Lobby", new Color(192, 192, 192), _ -> gui.changePanel(PanelType.LOBBY));
        backButton.setToolTipText("Go back to the main lobby");

        // Consistent font
        Font btnFont = new Font("Roboto", Font.PLAIN, 14);
        choosePhotoButton.setFont(btnFont);
        removePhotoButton.setFont(btnFont);
        saveButton.setFont(btnFont);
        backButton.setFont(btnFont);

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
                saveButton.setEnabled(true); // Enable save button
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
        Map<String, String> fields = new HashMap<>();
        fields.put("username", profile.username());
        fields.put("photo", Base64.getEncoder().encodeToString(newPhotoData));
        gui.sendRequest("updateProfile", fields);
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
