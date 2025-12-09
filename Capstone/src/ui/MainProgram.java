package ui;

import classes.Transaction;
import handlers.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainProgram extends JFrame{
    private final CardLayout cardLayout;
    private JPanel panelMain;
    private JPanel SideBarMenu;
    private JLabel greetTextField;
    private JButton dashboardButton;
    private JPanel MainContentPanel;
    private JLabel adminID;
    private JButton inventoryButton;
    private JButton transactionsButton;
    private JButton payoutsButton;
    private JButton logoutButton;

    private InventoryHandler inventoryHandler;
    private TransactionsHandler transactionsHandler;
    private SupplierHandler supplierHandler;
    private PayoutsHandler payoutsHandler;

    public MainProgram(String entityID){
        setTitle("Consignment System - Main");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panelMain);

        String name = "";
        double commissionRate = 0.0;
        try(BufferedReader br = new BufferedReader(new FileReader("Capstone/data/" + entityID + "/config.txt"))){
            String line;
            while((line = br.readLine()) != null){
                String[] data = line.split(",");

                name = data[0];
                commissionRate = Double.parseDouble(data[1]);
            }
        } catch (IOException e) {

        }

        supplierHandler = new SupplierHandler(entityID);
        inventoryHandler = new InventoryHandler(entityID, supplierHandler, commissionRate);
        transactionsHandler = new TransactionsHandler(entityID, inventoryHandler, supplierHandler);
        payoutsHandler = new PayoutsHandler(entityID, transactionsHandler, supplierHandler);

        cardLayout = (CardLayout) MainContentPanel.getLayout();

        DashboardPanel dashboardPanel = new DashboardPanel(inventoryHandler, transactionsHandler, supplierHandler);
        InventoryPanel inventoryPanel = new InventoryPanel(inventoryHandler, supplierHandler);
        TransactionsPanel transactionsPanel = new TransactionsPanel(transactionsHandler);
        PayoutsPanel payoutsPanel = new PayoutsPanel(transactionsHandler, payoutsHandler, supplierHandler);

        MainContentPanel.add(dashboardPanel, "KEY_DASHBOARD");
        MainContentPanel.add(inventoryPanel, "KEY_INVENTORY");
        MainContentPanel.add(transactionsPanel, "KEY_TRANSACTIONS");
        MainContentPanel.add(payoutsPanel, "KEY_PAYOUTS");

        greetTextField.setText("Welcome, " + name);
        adminID.setText("ID: " + entityID);

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Compare the source of the click to your button variables
                if (e.getSource() == dashboardButton) {
                    dashboardPanel.refresh();
                    cardLayout.show(MainContentPanel, "KEY_DASHBOARD");
                }
                else if (e.getSource() == inventoryButton) {
                    inventoryPanel.refresh();
                    cardLayout.show(MainContentPanel, "KEY_INVENTORY");
                }
                else if (e.getSource() == transactionsButton) {
                    transactionsPanel.refresh();
                    cardLayout.show(MainContentPanel, "KEY_TRANSACTIONS");
                }
                else if (e.getSource() == payoutsButton) {
                    payoutsPanel.refresh();
                    cardLayout.show(MainContentPanel, "KEY_PAYOUTS");
                }
            }
        };

        dashboardButton.addActionListener(listener);
        inventoryButton.addActionListener(listener);
        transactionsButton.addActionListener(listener);
        payoutsButton.addActionListener(listener);

        logoutButton.addActionListener(e -> {
            // Use your custom Style method for the confirmation dialog
            boolean confirmed = Style.showCustomConfirm(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout"
            );

            // If the user clicked "Confirm", proceed with logout
            if (confirmed) {
                inventoryHandler.saveInventory();
                supplierHandler.saveSuppliers();
                transactionsHandler.saveTransactions();
                payoutsHandler.savePayouts();

                dispose();
                new LogInForm().setVisible(true);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                inventoryHandler.saveInventory();
                supplierHandler.saveSuppliers();
                transactionsHandler.saveTransactions();
                payoutsHandler.savePayouts();

                dispose();
                System.exit(0);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}