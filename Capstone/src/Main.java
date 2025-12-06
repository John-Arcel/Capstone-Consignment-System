import ui.LogInForm;
import javax.swing.*;

public class Main{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LogInForm::new);
    }
}