import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JFrame {

    private JPanel dashboardPanel;

    private JLabel totalSales;
    private JLabel earnings;
    private JLabel itemSold;
    private JLabel pendingPayout;

    private JTextField itemIDField;

    private JTable itemsDue;
    private JTable transactions;
    private JTable itemDetail;

    private JButton sellItemButton;
    private JButton findItemButton;

    private List<Item> inventory = new ArrayList<>();
    private List<Transaction> transactionList = new ArrayList<>();

    // -----------------------------------------------------
    // Constructor
    // -----------------------------------------------------
    public DashboardPanel() {

        setContentPane(dashboardPanel);
        setTitle("Dashboard");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Make the text visible (white)
        itemIDField.setForeground(Color.WHITE);
        itemIDField.setCaretColor(Color.WHITE);
        itemIDField.setBackground(new Color(40,40,40));

        pack();
        setLocationRelativeTo(null);

        makeTransparent(dashboardPanel);

        loadSampleData();
        loadTransactionTable();
        loadItemsDueTable();
        updateTotals();

        // -----------------------------------------------------
        // FIND ITEM
        // -----------------------------------------------------
        findItemButton.addActionListener(e -> {
            String id = itemIDField.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Item ID");
                return;
            }

            Item found = null;

            for (Item it : inventory) {
                if (it.id.equalsIgnoreCase(id)) {
                    found = it;
                    break;
                }
            }

            if (found == null) {
                JOptionPane.showMessageDialog(this, "Item not found");
                return;
            }

            String[] cols = {"Field", "Value"};
            String[][] rows = {
                    {"Item ID", found.id},
                    {"Name", found.name},
                    {"Price", String.format("₱%.2f", found.price)},
                    {"Sold", found.sold ? "YES" : "NO"},
                    {"Expiry", found.expiry}
            };

            itemDetail.setModel(new DefaultTableModel(rows, cols));
        });

        // -----------------------------------------------------
        // SELL ITEM
        // -----------------------------------------------------
        sellItemButton.addActionListener(e -> {

            String id = itemIDField.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Item ID");
                return;
            }

            for (Item it : inventory) {

                if (it.id.equalsIgnoreCase(id)) {

                    if (it.sold) {
                        JOptionPane.showMessageDialog(this, "Item already sold!");
                        return;
                    }

                    it.sold = true;
                    transactionList.add(new Transaction(id, "2025-11-30", it.price));

                    updateTotals();
                    loadTransactionTable();
                    loadItemsDueTable();

                    JOptionPane.showMessageDialog(this, "Item Sold!");
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Item ID not found.");
        });
    }

    // -----------------------------------------------------
    // SAMPLE DATA
    // -----------------------------------------------------
    private void loadSampleData() {

        inventory.add(new Item("A101", "Blue Shirt", 350.00, true, "2025-12-05"));
        inventory.add(new Item("A102", "White Pants", 550.00, false, "2025-12-01"));
        inventory.add(new Item("A103", "Black Shoes", 999.00, true, "2025-11-29"));
        inventory.add(new Item("A104", "Red Jacket", 1200.00, false, "2025-11-30"));
        inventory.add(new Item("A105", "Green Hat", 220.00, false, "2025-11-28")); // nearest expiry

        transactionList.add(new Transaction("A101", "2025-11-29", 350.00));
        transactionList.add(new Transaction("A103", "2025-11-29", 999.00));
    }

    // -----------------------------------------------------
    // ITEMS DUE - SORT BY NEAREST EXPIRY
    // -----------------------------------------------------
    private void loadItemsDueTable() {

        ArrayList<Item> due = new ArrayList<>(inventory);

        due.sort((a, b) -> a.expiry.compareTo(b.expiry));

        String[] cols = {"Item ID", "Name", "Expiry"};
        String[][] rows = new String[due.size()][3];

        for (int i = 0; i < due.size(); i++) {
            Item it = due.get(i);
            rows[i][0] = it.id;
            rows[i][1] = it.name;
            rows[i][2] = it.expiry;
        }

        itemsDue.setModel(new DefaultTableModel(rows, cols));
    }

    // -----------------------------------------------------
    // UPDATE TOTALS
    // -----------------------------------------------------
    private void updateTotals() {

        int soldCount = 0;
        double earningsSum = 0;
        double pendingSum = 0;

        for (Item it : inventory) {
            if (it.sold) {
                soldCount++;
                earningsSum += it.price;
            } else {
                pendingSum += it.price;
            }
        }

        itemSold.setText(String.valueOf(soldCount));
        earnings.setText(String.format("₱%.2f", earningsSum));
        pendingPayout.setText(String.format("₱%.2f", pendingSum));
        totalSales.setText(String.valueOf(transactionList.size()));
    }

    // -----------------------------------------------------
    // TRANSACTION TABLE
    // -----------------------------------------------------
    private void loadTransactionTable() {

        String[] cols = {"Item ID", "Date", "Amount"};
        String[][] rows = new String[transactionList.size()][3];

        for (int i = 0; i < transactionList.size(); i++) {
            Transaction t = transactionList.get(i);
            rows[i][0] = t.itemID;
            rows[i][1] = t.date;
            rows[i][2] = String.format("₱%.2f", t.amount);
        }

        transactions.setModel(new DefaultTableModel(rows, cols));
    }

    // -----------------------------------------------------
    // MAKE PANELS TRANSPARENT
    // -----------------------------------------------------
    public static void makeTransparent(JComponent container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;

                if (jc.getComponentCount() > 0) {
                    makeTransparent(jc);
                } else {
                    if (!(c instanceof JButton) &&
                            !(c instanceof JTextField) &&
                            !(c instanceof JLabel) &&
                            !(c instanceof JCheckBox) &&
                            !(c instanceof JTable)) {

                        jc.setOpaque(false);
                    }
                }
            }
        }
    }

    // -----------------------------------------------------
    // MAIN
    // -----------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardPanel().setVisible(true));
    }

    // -----------------------------------------------------
    // DATA CLASSES
    // -----------------------------------------------------
    static class Item {
        String id;
        String name;
        double price;
        boolean sold;
        String expiry;

        Item(String id, String name, double price, boolean sold, String expiry) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.sold = sold;
            this.expiry = expiry;
        }
    }

    static class Transaction {
        String itemID;
        String date;
        double amount;

        Transaction(String itemID, String date, double amount) {
            this.itemID = itemID;
            this.date = date;
            this.amount = amount;
        }
    }

    // -----------------------------------------------------
    // Needed when using IntelliJ .form custom components
    // -----------------------------------------------------
    private void createUIComponents() {
        itemIDField = new JTextField(20);
        itemsDue = new JTable();
        transactions = new JTable();
        itemDetail = new JTable();
        itemIDField = new Style.RoundedTextField(60);
        sellItemButton = new Style.RoundedButton(60);
        findItemButton = new Style.RoundedButton(60);

    }
}
