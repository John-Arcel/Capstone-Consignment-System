import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


public class LogInForm extends JFrame {
    private static int LoginFormCount = 1;


    // REMOVE STATIC — MUST NOT BE STATIC
    private JPanel LogInPanel;      // This is the real root panel created by IntelliJ GUI Designer
    private JPanel Main_Panel;
    private JPanel Log_In_Panel;
    private JPanel Register_Panel;
    private JPanel Welcome_Panel;
    private JButton Register_Button_Directory;
    private JButton Log_In_Button_Directory;
    private JLabel Company_Name;
    private JTextField Shop_Name_Text_Field;
    private JTextField Admin_Name_Text_Field;
    private JTextField Register_Password_TextField;
    private JTextField Commision_Rate_TextField;
    private JTextField Enter_ID_TextField;
    private JPasswordField Login_Password_TextField;
    private JButton Login_Button;
    private JButton Register_Button;
    private JLabel Register_Flag;
    private JTextField Contact_Number_Text_Field;
    private JLabel Login_Flag;

    private CardLayout cardLayout;

    public LogInForm() {

        // DO THIS FIRST — your root panel must be the content pane
        setContentPane(LogInPanel);

        setTitle("Login Form");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        makeTransparent(LogInPanel);
        // Create card layout on the MAIN PANEL inside the form
        cardLayout = new CardLayout();
        Main_Panel.setLayout(cardLayout);

        // Add sub-panels to the card layout
        Main_Panel.add(Welcome_Panel, "welcome");
        Main_Panel.add(Log_In_Panel, "login");
        Main_Panel.add(Register_Panel, "register");


        Login_Button.setBorderPainted(false);
        Register_Button.setBorderPainted(false);
        Register_Button_Directory.setBorderPainted(false);
        Log_In_Button_Directory.setBorderPainted(false);




        //-------------------------------Weclome Action Listeners-------------------------------
        Register_Button_Directory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogInPanel.setBackground(new Color(0xF2F4F7));
                LogInPanel.setOpaque(true);

                // Force Swing to redraw immediately
                LogInPanel.revalidate();
                LogInPanel.repaint();

                cardLayout.show(Main_Panel, "register");
            }
        });
        Log_In_Button_Directory.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LogInPanel.setBackground(new Color(0xF2F4F7));
                LogInPanel.setOpaque(true);

                // Force Swing to redraw immediately
                LogInPanel.revalidate();
                LogInPanel.repaint();

                cardLayout.show(Main_Panel, "login");
            }
        });


        // -------------------------------Register Action Listeners-------------------------------

        Register_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // variables
                String shopName = Shop_Name_Text_Field.getText();
                String adminName = Admin_Name_Text_Field.getText();
                String password = Register_Password_TextField.getText();
                String contactNumberText = Contact_Number_Text_Field.getText();
                String commissionText = Commision_Rate_TextField.getText();

                     // check if all the fields are not empty
                    if (shopName.isEmpty() || adminName.isEmpty() || password.isEmpty() || commissionText.isEmpty() || contactNumberText.isEmpty()) {
                        Register_Flag.setText("Please enter missing fields");
                        return;
                    }

                    // check if password is valid
                    if (password.length() < 8) { // checks if it is 8 character long
                        Register_Flag.setText("Password must be at least 8 characters long");
                        return;
                    }
                    else if (!password.matches(".*[A-Z].*")) { // checks for uppercase letter
                        Register_Flag.setText("Password must contain at least 1 uppercase letter");
                        return;
                    }
                    else if (!password.matches(".*[0-9].*")) { // checks for number
                        Register_Flag.setText("Password must contain at least 1 number");
                        return;
                    }
                    else if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) { // checks for special character
                        Register_Flag.setText("Password must contain at least 1 special character");
                        return;
                    }

                    // check if contactnumber is valid
                    if (!contactNumberText.matches("\\d+")) { // this checks if the input is all digits
                        Register_Flag.setText("Contact number must be digits only");
                        return;
                    }
                    else if(contactNumberText.length() != 11){ // this checks if it is 11 digits long
                        Register_Flag.setText("Contact number must be 11 digits long");
                        return;
                    }

                    if (!commissionText.matches("\\d+")) {  // allows decimals
                        Register_Flag.setText("Commission rate must be a valid number");
                        return;
                    }

                    double commission = Double.parseDouble(commissionText);
                    if(commission > 100 || commission < 0){
                        Register_Flag.setText("Commission rate must be between 0 - 100");
                        return;
                    }

                    commission = commission / 100;
                Entity new_user = new Consignee(adminName,contactNumberText,password);


                // -------------------------------File Handling-------------------------------

                File userFolder = new File("Capstone/data/" + new_user.getID());
                userFolder.mkdirs();

                File config = new File(userFolder, "config.txt");
                File suppliers = new File(userFolder, "suppliers.csv");
                File inventory = new File(userFolder, "inventory.csv");
                File salesLog = new File(userFolder, "sales_log.csv");

                try{
                   config.createNewFile();
                   suppliers.createNewFile();
                   inventory.createNewFile();
                   salesLog.createNewFile();

                    try (BufferedWriter br = new BufferedWriter(new FileWriter(config))) {
                        br.write(String.format("%s,%.2f", shopName, commission));
                    }

                    try(BufferedWriter br = new BufferedWriter((new FileWriter("Capstone/data/admin_registry.csv",true)))){
                        br.write(new_user.getID() + "," + password);
                        br.newLine();
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                // Todo once the details are registered it will go to dashboard
                    LogInPanel.setBackground(new Color(0x1F4E79));
                    LogInPanel.setOpaque(true);
                    cardLayout.show(Main_Panel, "welcome");
            }
        });


        // -------------------------------Log In Action Listeners-------------------------------

        Login_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = Enter_ID_TextField.getText();
                char[] passArray = Login_Password_TextField.getPassword();
                String password = new String(passArray);

                    if(userId.isEmpty() || password.isEmpty()){
                        Login_Flag.setText("Please enter missing fields");
                        return;
                    }

                    try(BufferedReader br = new BufferedReader(new FileReader("Capstone/data/admin_registry.csv"))){
                        String s;
                        boolean foundUser = false;

                        while((s = br.readLine()) != null){
                            // tokenized usedId and password
                            String[] inputArr = s.split(",");

                            String fileUser = inputArr[0];
                            String filePass = inputArr[1];

                            if (fileUser.equals(userId)) {        // user found
                                foundUser = true;

                                if (filePass.equals(password)) {  // correct password
                                    // Todo once the details are registered it will go to dashboard

                                    LogInPanel.setBackground(new Color(0x1F4E79));
                                    LogInPanel.setOpaque(true);
                                    cardLayout.show(Main_Panel, "welcome");
                                    return;
                                } else {
                                    Login_Flag.setText("Incorrect password");
                                    return;
                                }
                            }
                        }

                        if (!foundUser) {
                            Login_Flag.setText("No account exists");
                        }

                    }
                    catch (IOException e12) {
                        e12.printStackTrace();
                    }

            }
        });
    }








    // Force spacers to be opaque
    public static void makeTransparent(JComponent container) {
      for (Component c : container.getComponents()) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;

            // Check if it's a container (like a panel holding other things)
            if (jc.getComponentCount() > 0) {
                // It has children, so it's a structural panel. Recurse inside.
                makeTransparent(jc);
            } else {
                // It has NO children. It might be a Spacer, Label, or Button.
                // Filter out actual controls so we don't break them.
                if (!(c instanceof JButton) &&
                    !(c instanceof JTextField) &&
                    !(c instanceof JLabel) &&
                    !(c instanceof JCheckBox)) {

                    // It's likely a spacer (custom or JPanel-based)
                    jc.setOpaque(false);
                }
            }
        }
    }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LogInForm().setVisible(true));
    }


    private void createUIComponents() {
        Shop_Name_Text_Field = new Style.RoundedTextField(60);
        Admin_Name_Text_Field = new Style.RoundedTextField(60);
        Shop_Name_Text_Field = new Style.RoundedTextField(60);
        Register_Password_TextField = new Style.RoundedTextField(60);
        Commision_Rate_TextField = new Style.RoundedTextField(60);
        Enter_ID_TextField = new Style.RoundedTextField(60);
        Contact_Number_Text_Field = new Style.RoundedTextField(60);
        Login_Password_TextField = new Style.RoundedPasswordField(60);

        Log_In_Button_Directory = new Style.RoundedButton(60);

    }
}
