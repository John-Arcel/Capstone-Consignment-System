package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.util.Enumeration;

public class Style {

    //------------- Rounded Text Field -------------
    public static class RoundedTextField extends JTextField {
        private int cornerRadius;

        public RoundedTextField(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 15, 10, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    //------------- Rounded Password Field -------------
    public static class RoundedPasswordField extends JPasswordField {
        private int cornerRadius;

        public RoundedPasswordField(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 15, 10, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    //------------- Rounded Button -------------
    public static class RoundedButton extends JButton {
        private int cornerRadius;

        public RoundedButton(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setContentAreaFilled(false);
            setFocusPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            ButtonModel model = getModel();
            if (model.isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (model.isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }

            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==========================================
    //       FONT LOGIC (Now correctly Static)
    // ==========================================

    // FIXED: Added 'static' here so Main can call Style.applyGlobalFonts()
    public static void applyGlobalFonts() {
        // 1. Load the Regular font
        String familyName = loadCustomFont("canva-sans-regular.otf");

        // 2. Load the Bold font
        loadCustomFont("canva-sans-bold.otf");

        // 3. Apply it globally
        if (familyName != null) {
            updateUIManagerWithFont(familyName);
        }
    }

    // --- INTERNAL HELPERS (Private) ---

    private static String loadCustomFont(String filename) {
        try {
            String[] pathsToCheck = {
                    "Capstone/Image/" + filename,
                    "Image/" + filename,
                    "src/Image/" + filename
            };

            File fontFile = null;
            for (String path : pathsToCheck) {
                File f = new File(path);
                if (f.exists()) {
                    fontFile = f;
                    break;
                }
            }

            if (fontFile == null) {
                System.err.println("❌ Style Error: Could not find font file: " + filename);
                return null;
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);

            return font.getFamily();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void updateUIManagerWithFont(String newFontFamily) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);

            if (value instanceof FontUIResource) {
                FontUIResource original = (FontUIResource) value;

                FontUIResource newFont = new FontUIResource(
                        newFontFamily,
                        original.getStyle(),
                        original.getSize()
                );

                UIManager.put(key, newFont);
            }
        }
    }


    // --- COLOR PALETTE ---
    public static final Color BLUE_COLOR = new Color(46, 139, 239);
    public static final Color RED_COLOR = new Color(239, 83, 80);
    public static final Color GRAY_COLOR = new Color(108, 117, 125);

    // --- 1. HELPER: Show a simple message (1 Button: OK) ---
    public static void showCustomMessage(Component parent, String message, String title, int messageType) {
        Style.RoundedButton btnOk = new Style.RoundedButton(15);
        btnOk.setText("OK");
        btnOk.setForeground(Color.WHITE);

        // FIX: Automatically set color based on message type
        if (messageType == JOptionPane.ERROR_MESSAGE || messageType == JOptionPane.WARNING_MESSAGE) {
            btnOk.setBackground(Style.RED_COLOR); // Red for errors/warnings
        } else {
            btnOk.setBackground(Style.BLUE_COLOR); // Blue for success
        }

        // Close window logic
        btnOk.addActionListener(evt -> SwingUtilities.getWindowAncestor(btnOk).dispose());

        Object[] options = { btnOk };

        JOptionPane.showOptionDialog(parent, message, title,
                JOptionPane.DEFAULT_OPTION, messageType, null, options, btnOk);
    }
    // --- 2. HELPER: Show a Confirm dialog (2 Buttons: Confirm/Cancel) ---
    // Returns TRUE if "Confirm" is clicked, FALSE otherwise.
    public static boolean showCustomConfirm(Component parent, String message, String title) {
        // Yes Button
        RoundedButton btnYes = new RoundedButton(15);
        btnYes.setText("Confirm");
        btnYes.setBackground(BLUE_COLOR);
        btnYes.setForeground(Color.WHITE);

        // No Button
        RoundedButton btnNo = new RoundedButton(15);
        btnNo.setText("Cancel");
        btnNo.setBackground(RED_COLOR);
        btnNo.setForeground(Color.WHITE);

        final boolean[] confirmed = {false};

        // Close logic
        btnYes.addActionListener(evt -> {
            confirmed[0] = true;
            SwingUtilities.getWindowAncestor(btnYes).dispose();
        });

        btnNo.addActionListener(evt -> {
            confirmed[0] = false;
            SwingUtilities.getWindowAncestor(btnNo).dispose();
        });

        Object[] options = {btnYes, btnNo};

        JOptionPane.showOptionDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, btnYes);

        return confirmed[0];
    }
}