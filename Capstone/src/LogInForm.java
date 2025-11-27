import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogInForm extends JFrame {

    // REMOVE STATIC — MUST NOT BE STATIC
    private JPanel LogInPanel;      // This is the real root panel created by IntelliJ GUI Designer
    private JPanel Main_Panel;
    private JPanel Log_In_Panel;
    private JPanel Register_Panel;
    private JPanel Welcome_Panel;
    private JButton Register_Button_Directory;
    private JButton Log_In_Button_Directory;
    private JLabel Company_Name;

    private CardLayout cardLayout;

    public LogInForm() {

        // DO THIS FIRST — your root panel must be the content pane
        setContentPane(LogInPanel);

        setTitle("Login Form");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        
        // Create card layout on the MAIN PANEL inside the form
        cardLayout = new CardLayout();
        Main_Panel.setLayout(cardLayout);

        // Add sub-panels to the card layout
        Main_Panel.add(Welcome_Panel, "welcome");
        Main_Panel.add(Log_In_Panel, "login");
        Main_Panel.add(Register_Panel, "register");

        // Switching panels
        Register_Button_Directory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(Main_Panel, "register");
            }
        });
        Log_In_Button_Directory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(Main_Panel, "login");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LogInForm().setVisible(true));
    }
}
