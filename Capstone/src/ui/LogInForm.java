package ui;

import classes.Consignee;
import classes.Entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


public class LogInForm extends JFrame {


    //Root Panel
    private JPanel LogInPanel;

    //Base Panel
    private JPanel Main_Panel;

    //Card Panels
    private JPanel Log_In_Panel;
    private JPanel Register_Panel;
    private JPanel Welcome_Panel;

    private JButton Register_Button_Directory;
    private JButton Log_In_Button_Directory;
    private JLabel Company_Name;
    private JTextField Register_Username_Text_Field;
    private JTextField Register_Password_TextField;
    private JTextField Commision_Rate_TextField;
    private JTextField Login_Username_TextField;
    private JPasswordField Login_Password_TextField;
    private JButton Login_Button;
    private JButton Register_Button;
    private JLabel Register_Flag;
    private JTextField Contact_Number_Text_Field;
    private JLabel Login_Flag;
    private JButton backButton;

    private CardLayout cardLayout;

    public LogInForm() {

        // Todo you can remove this basic initialization rani for testing
        // Todo just remove the 5 lines of code below
        setContentPane(LogInPanel);
        setTitle("Login Form");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = env.getMaximumWindowBounds();
        setBounds(bounds);

        setResizable(false);
        setVisible(true);

        //purpose call static function to make spacer transparent
        // for some reason di siya transparent
        makeTransparent(LogInPanel);

        // Create card layout on the MAIN PANEL inside the form
        cardLayout = new CardLayout();
        Main_Panel.setLayout(cardLayout);

        // Add sub-panels to the card layout
        Main_Panel.add(Welcome_Panel, "welcome");
        Main_Panel.add(Log_In_Panel, "login");
        Main_Panel.add(Register_Panel, "register");


        //purpose: remove outlines of button
        Login_Button.setBorderPainted(false);
        Register_Button.setBorderPainted(false);
        Register_Button_Directory.setBorderPainted(false);
        Log_In_Button_Directory.setBorderPainted(false);



        //-----------This is portion of code block is hardcoded due to limitation of GridBagLayout---------------

            //purpose: load the image resource for the back button
            ImageIcon originalIcon = new ImageIcon("Capstone/image/back_button.png");

            //purpose: scale the image to a smaller size (40x40) for the UI
            Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            ImageIcon backIcon = new ImageIcon(scaledImage);

            //purpose: initialize the button using the resized icon
            backButton = new JButton(backIcon);

            //purpose: configure button position (x,y) and size
            backButton.setBounds(25, 25, 40, 40);

            //purpose: remove default button styling (borders, background) to show only the PNG
            backButton.setBorderPainted(false);
            backButton.setContentAreaFilled(false);
            backButton.setFocusPainted(false);
            backButton.setOpaque(false);

            //purpose: hide button initially (only show on sub-panels)
            backButton.setVisible(false);

            //purpose: access the glass pane overlay to allow fixed positioning
            JPanel glassPane = (JPanel) this.getRootPane().getGlassPane();
            glassPane.setVisible(true);
            glassPane.setLayout(null);
            glassPane.setOpaque(false);

            //purpose: add the custom back button to the overlay
            glassPane.add(backButton);

            //purpose: define action to return to welcome screen on click
            backButton.addActionListener(e -> {
                cardLayout.show(Main_Panel, "welcome");
                LogInPanel.setBackground(new Color(0x1F4E79));
                backButton.setVisible(false);
            });
        //--------------------End of hardcoded portion--------------------------


        //============================================================================================
        //-------------------------------Welcome Panel Action Listeners-------------------------------
        //============================================================================================

        Register_Button_Directory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //purpose: change color of root panel to be the same as sa cards
                LogInPanel.setBackground(new Color(0xF2F4F7));

                //purpose: switch to register panel
                cardLayout.show(Main_Panel, "register");

                //purpose: show back button to allows users to go back to welcome page
                backButton.setVisible(true);
            }
        });


        Log_In_Button_Directory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //purpose: change color of root panel to be the same as sa cards
                LogInPanel.setBackground(new Color(0xF2F4F7));

                //purpose: switch to login panel
                cardLayout.show(Main_Panel, "login");

                //purpose: show back button to allows users to go back to welcome page
                backButton.setVisible(true);
            }
        });

        //==============================================================================================
        // -------------------------------Register Panel Action Listeners-------------------------------
        //==============================================================================================

        Register_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //purpose: get all information of the text fields
                String userName = Register_Username_Text_Field.getText();
                String password = Register_Password_TextField.getText();
                String commissionText = Commision_Rate_TextField.getText();


                //--------purpose: Validate all registration input fields--------

                // purpose: check if any required field is empty
                if (userName.isEmpty() || password.isEmpty() || commissionText.isEmpty()) {
                    Register_Flag.setText("Please enter missing fields");
                    return;
                }


                //--------purpose: validate username requirements--------

                // purpose: check if username already exists in admin_registry
                try (BufferedReader br = new BufferedReader(new FileReader("Capstone/data/admin_registry.csv"))) {

                    String line;
                    br.readLine();
                    // checks line by line in admin_registry
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");


                        if (parts.length > 1 && parts[1].trim().equals(userName.trim())) {
                            Register_Flag.setText("Username already exists");
                            return;
                        }
                    }

                } catch (IOException e22) {
                    e22.printStackTrace();
                }


                //--------purpose: validate password requirements--------

                // purpose: check if password has minimum length
                if (password.length() < 8) {
                    Register_Flag.setText("Password must be at least 8 characters long");
                    return;
                }

                // purpose: check if password contains at least one uppercase letter
                else if (!password.matches(".*[A-Z].*")) {
                    Register_Flag.setText("Password must contain at least 1 uppercase letter");
                    return;
                }

                // purpose: check if password contains at least one digit
                else if (!password.matches(".*[0-9].*")) {
                    Register_Flag.setText("Password must contain at least 1 number");
                    return;
                }

                // purpose: check if password contains at least one special character
                else if (!password.matches(".*[!@#$%^&*().?/:{}|<>+_-].*")) {
                    Register_Flag.setText("Password must contain at least 1 special character");
                    return;
                }

                //--------purpose: validate commission rate--------

                // purpose: check if commission input is numeric (integer-only check as written)
                if (!commissionText.matches("\\d+")) {
                    Register_Flag.setText("Commission rate must be a valid number");
                    return;
                }

                // purpose: convert commission string to double
                double commission = Double.parseDouble(commissionText);

                // purpose: ensure commission is between 0 and 100
                if (commission > 100 || commission < 0) {
                    Register_Flag.setText("Commission rate must be between 0 - 100");
                    return;
                }

                // purpose: convert percentage to decimal (example: 10 → 0.10)
                commission = commission / 100;

                // purpose: find the number of existing consignee
                int ConsigneeCounter = 0;
                try(BufferedReader br = new BufferedReader(new FileReader("Capstone/data/admin_registry.csv"))){

                    String line;
                    br.readLine();
                    while((line = br.readLine()) != null){
                        ConsigneeCounter++;
                    }
                }
                catch (IOException e1){
                    System.out.println("This is an error");
                }

                ConsigneeCounter++;

                //initialized
                Entity new_user = new Consignee(userName,password,String.format("V-%07d", ConsigneeCounter));


                // -------------------------------File Handling-------------------------------

                //purpose: create folder inside /data
                File userFolder = new File("Capstone/data/" + new_user.getID());
                userFolder.mkdirs();

                //purpose: creating files
                File config = new File(userFolder, "config.txt");
                File consignors = new File(userFolder," consignors.csv");
                File inventory = new File(userFolder, "inventory.csv");
                File transactions = new File(userFolder, "transactions.csv");
                File payouts = new File(userFolder, "payouts.csv");

                try{
                   config.createNewFile();
                   consignors.createNewFile();
                   inventory.createNewFile();
                   transactions.createNewFile();
                   payouts.createNewFile();

                   //purpose: write the username and commission rate on config.txt
                    try (BufferedWriter br = new BufferedWriter(new FileWriter(config))) {
                        br.write(String.format("%s,%.2f", userName, commission));
                    }

                    //purpose: append the user and password information of new user to admin_registry.csv
                    //         format of this is (id of user,username,password)  Ex. V-0000001,John,Password123!
                    try(BufferedWriter br = new BufferedWriter((new FileWriter("Capstone/data/admin_registry.csv",true)))){
                        br.write(new_user.getID() + "," + userName + "," + password);
                        br.newLine();
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                // Todo once the details are registered it will go to dashboard
                // Todo remove nala ni 2 lines of code kay temporary rana na mo balik sa welcome page
                    LogInPanel.setBackground(new Color(0x1F4E79));
                    cardLayout.show(Main_Panel, "welcome");
            }
        });

        //============================================================================================
        // -------------------------------Log In Panel Action Listeners-------------------------------
        //============================================================================================

        Login_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //purpose: get all information of the text fields
                // I used getPassword in password field text kay mas safe daw ingon ni AI hahaha
                String username = Login_Username_TextField.getText();
                char[] passArray = Login_Password_TextField.getPassword();
                String password = new String(passArray);

                // purpose: check if any of the fields are empty
                if(username.isEmpty() || password.isEmpty()){
                    Login_Flag.setText("Please enter missing fields");
                    return;
                }

                // purpose: read the admin_registry.csv and verify login
                try(BufferedReader br = new BufferedReader(new FileReader("Capstone/data/admin_registry.csv"))){
                    String line;
                    boolean foundUser = false;

                    // purpose: check each line (each user record) in admin_registry.csv
                    while((line = br.readLine()) != null){

                        // tokenized userID, username, password, etc.
                        String[] inputArr = line.split(",");
                        String id = inputArr[0];
                        String fileUser = inputArr[1];
                        String filePass = inputArr[2];

                        // purpose: if username matches, check password
                        if (fileUser.equals(username)) {        // user found
                            foundUser = true;

                            // purpose: if correct password, show dashboard panel
                            if (filePass.equals(password)) {

                                // Todo once the details are registered it will go to dashboard
                                // Todo remove nala ni 2 lines of code kay temporary rana na mo balik sa welcome page
                                new MainProgram(id);
                                LogInForm.this.dispose();
                            }

                            // purpose: password is incorrect for this user
                            else {
                                Login_Flag.setText("Incorrect password");
                                return;
                            }
                        }
                    }

                    // purpose: if username not found in admin_registry.csv
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


    //=============================================================================
    // -----------------------------Static Functions-------------------------------
    //=============================================================================

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

    //purpose: custom style for swing components
    //all the components here makes corner rounded
    private void createUIComponents() {

        Register_Username_Text_Field = new Style.RoundedTextField(60);
        Register_Password_TextField = new Style.RoundedTextField(60);
        Commision_Rate_TextField = new Style.RoundedTextField(60);
        Login_Username_TextField = new Style.RoundedTextField(60);
        Contact_Number_Text_Field = new Style.RoundedTextField(60);
        Login_Password_TextField = new Style.RoundedPasswordField(60);

        Log_In_Button_Directory = new Style.RoundedButton(60);

    }
}
