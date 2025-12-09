package ui;

import javax.swing.*;



public class Main {
    public static void main(String[] args) {
        // Add custom font
        Style.applyGlobalFonts();

        SwingUtilities.invokeLater(LogInForm::new);
    }
}