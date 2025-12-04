package ui;

import handlers.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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


        transactionsHandler = new TransactionsHandler(entityID);
        supplierHandler = new SupplierHandler(entityID);
        payoutsHandler = new PayoutsHandler(entityID);
        inventoryHandler = new InventoryHandler(entityID, supplierHandler);
         cardLayout = (CardLayout) MainContentPanel.getLayout();

       // MainContentPanel.add(new DashboardPanel(), "KEY_DASHBOARD");
        MainContentPanel.add(new InventoryPanel(), "KEY_INVENTORY");
        MainContentPanel.add(new TransactionsPanel(), "KEY_TRANSACTIONS");
        //MainContentPanel.add(new PayoutsPanel(), "KEY_PAYOUTS");

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Compare the source of the click to your button variables
                if (e.getSource() == dashboardButton) {
                    cardLayout.show(MainContentPanel, "KEY_DASHBOARD");
                }
                else if (e.getSource() == inventoryButton) {
                    cardLayout.show(MainContentPanel, "KEY_INVENTORY");
                }
                else if (e.getSource() == transactionsButton) {
                    cardLayout.show(MainContentPanel, "KEY_TRANSACTIONS");
                }
                else if (e.getSource() == payoutsButton) {
                    cardLayout.show(MainContentPanel, "KEY_PAYOUTS");
                }
            }
        };

        dashboardButton.addActionListener(listener);
        inventoryButton.addActionListener(listener);
        transactionsButton.addActionListener(listener);
        payoutsButton.addActionListener(listener);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}