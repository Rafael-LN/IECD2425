package gui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class GuiUtils {

    public static JLabel createLabel(String text, int alignment) {
        return createLabel(text, alignment, new Font("Roboto", Font.PLAIN, 12), null);
    }

    public static JLabel createLabel(String text, int alignment, Font font, Color color) {
        JLabel label = new JLabel(text, alignment);
        if (font != null) label.setFont(font);
        if (color != null) label.setForeground(color);
        return label;
    }

    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Roboto", Font.PLAIN, 12));
        return field;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Roboto", Font.PLAIN, 12));
        return field;
    }

    public static JButton createButton(String text, Color bgColor, Consumer<ActionEvent> action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(e -> action.accept(e));
        return button;
    }
}
