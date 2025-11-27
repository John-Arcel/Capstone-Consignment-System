import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Style {


    //-------------This static class will make text Field Rounded-------------
    public static class RoundedTextField extends JTextField {
        private int cornerRadius;

        public RoundedTextField(int radius) {
            this.cornerRadius = radius;
            setOpaque(false); // Make the square background transparent

            // Add some padding so text doesn't touch the rounded corners
            setBorder(new EmptyBorder(10, 15, 10, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            // Enable Antialiasing for smooth corners
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the rounded background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

            // Draw the text (super must be called AFTER we paint the background)
            super.paintComponent(g);

            g2.dispose();
        }


    }

    //-------------This static class will make passwordField Rounded-------------
    public static class RoundedPasswordField extends JPasswordField{
        private int cornerRadius;

        public RoundedPasswordField(int radius) {
            this.cornerRadius = radius;
            setOpaque(false); // Make the square background transparent

            // Add some padding so text doesn't touch the rounded corners
            setBorder(new EmptyBorder(10, 15, 10, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            // Enable Antialiasing for smooth corners
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the rounded background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

            // Draw the text (super must be called AFTER we paint the background)
            super.paintComponent(g);

            g2.dispose();
        }


    }

    //-------------This static class will make button Rounded-------------
    public static class RoundedButton extends JButton {
        private int cornerRadius;

        public RoundedButton(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 15, 10, 15));

            // THIS IS IMPORTANT:
            // We must tell the button not to paint its own standard background
            // so we can paint our rounded one instead.
            setContentAreaFilled(false);
            setFocusPainted(false); // Optional: Removes the dotted line
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Check the state of the button (Hover? Clicked?)
            ButtonModel model = getModel();

            if (model.isPressed()) {
                // If clicked, make the color darker
                g2.setColor(getBackground().darker());
            } else if (model.isRollover()) {
                // If hovered, make the color slightly brighter (or a custom color)
                g2.setColor(getBackground().brighter());
            } else {
                // Normal state
                g2.setColor(getBackground());
            }

            // --- NEW LOGIC ENDS HERE ---

            // 2. Draw the rounded background with the chosen color
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

            g2.dispose();

            // 3. Draw the text/icon on top (Call super LAST)
            super.paintComponent(g);
        }


    }
}
