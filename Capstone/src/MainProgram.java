import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainProgram extends JFrame{
    private CardLayout cardLayout;

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
    private JTable table1;
    private JTextField textField1;
    private JTable table2;
    private JButton findItemButton;
    private JPanel DashboardCard;
    private JPanel InventoryCard;
    private JTable table;
    private JLabel inventoryAmountLabel;

    public MainProgram(){
        setTitle("Consignment System - Main");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(panelMain);

        cardLayout = (CardLayout)MainContentPanel.getLayout();

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (e.getActionCommand()) {
                    case "Dashboard":
                        cardLayout.show(MainContentPanel, "DashboardCard");
                        break;
                    case "Inventory":
                        cardLayout.show(MainContentPanel, "InventoryCard");
                        break;
                    case "Transactions":
                        cardLayout.show(MainContentPanel, "TransactionsCard");
                        break;
                    case "Payouts":
                        cardLayout.show(MainContentPanel, "PayoutsCard");
                        break;
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

    public static void main(String[] args) {
        new MainProgram();
    }
}