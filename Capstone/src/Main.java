package ui;

import javax.swing.*;



public class Main {
    public static void main(String[] args) {
        // 1. Delegate styling to your Style class
        Style.applyGlobalFonts();

        SwingUtilities.invokeLater(LogInForm::new);
    }
}