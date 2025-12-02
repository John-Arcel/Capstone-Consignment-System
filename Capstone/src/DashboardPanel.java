import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JFrame {

    // ---------------------------
    // TEMPORARY APP CONFIG
    // ---------------------------
    public static class AppConfig {
        public static double storeCutPercent = 0.20; // default 20% store cut
    }


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

    private JScrollPane itemsDueScrollPane;


    private List<Item> inventory = new ArrayList<>();
    private List<Transaction> transactionList = new ArrayList<>();

    // -----------------------------------------------------
    // Constructor
    // -----------------------------------------------------
    public DashboardPanel() {

        setContentPane(dashboardPanel);
        setTitle("Dashboard");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        itemsDueScrollPane.setViewportView(itemsDue);

        // Make the text visible (white)
        itemIDField.setForeground(Color.WHITE);
        itemIDField.setCaretColor(Color.WHITE);
        itemIDField.setBackground(new Color(40,40,40));

        itemsDue.setOpaque(false);
        itemsDue.setBackground(new Color(0, 0, 0, 0));
        itemsDue.setShowGrid(false);

        itemsDueScrollPane.setOpaque(false);
        itemsDueScrollPane.getViewport().setOpaque(false);

        itemsDueScrollPane.setBorder(null);

        itemsDue.getTableHeader().setOpaque(false);
        itemsDue.getTableHeader().setBackground(new Color(0,0,0,0));
// -------------------------------------------------------

        itemIDField.setForeground(Color.WHITE);
        itemIDField.setCaretColor(Color.WHITE);
        itemIDField.setBackground(new Color(40,40,40));

        pack();
        setLocationRelativeTo(null);

        makeTransparent(dashboardPanel);

        loadSampleData();
        loadTransactionTable();
        loadItemsDueTable();
        updateTotals(); // initial calculation

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
                    {"Quantity", String.valueOf(found.quantity)},
                    {"Sold Quantity", String.valueOf(found.soldQuantity)},
                    {"Stock Left", String.valueOf(found.quantity - found.soldQuantity)},
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

                    if (it.isSoldOut()) {
                        JOptionPane.showMessageDialog(this, "No more stock available!");
                        return;
                    }

                    // increase sold stock
                    it.soldQuantity++;

                    // Add transaction
                    transactionList.add(new Transaction(id, "2025-11-30", it.price));

                    // Update dashboard totals and tables
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

        inventory.add(new Item("A101", "Blue Shirt", 350.00, 10, 1, "2025-12-05"));
        inventory.add(new Item("A102", "White Pants", 550.00, 5, 0, "2025-12-01"));
        inventory.add(new Item("A103", "Black Shoes", 999.00, 3, 1, "2025-11-29"));
        inventory.add(new Item("A104", "Red Jacket", 1200.00, 4, 0, "2025-11-30"));
        inventory.add(new Item("A105", "Green Hat", 220.00, 8, 0, "2025-11-28")); // nearest expiry

        transactionList.add(new Transaction("A101", "2025-11-29", 350.00));
        transactionList.add(new Transaction("A103", "2025-11-29", 999.00));
    }


    // -----------------------------------------------------
    // ITEMS DUE - SORT BY NEAREST EXPIRY
    // -----------------------------------------------------
    private void loadItemsDueTable() {

        ArrayList<Item> due = new ArrayList<>(inventory);

        due.sort((a, b) -> a.expiry.compareTo(b.expiry));

        String[] cols = {"Item ID", "Name", "Expiry", "Stock Left"};
        String[][] rows = new String[due.size()][4];

        for (int i = 0; i < due.size(); i++) {
            Item it = due.get(i);
            rows[i][0] = it.id;
            rows[i][1] = it.name;
            rows[i][2] = it.expiry;
            rows[i][3] = String.valueOf(it.quantity - it.soldQuantity);
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

            soldCount += it.soldQuantity;

            earningsSum += it.soldQuantity * it.price * AppConfig.storeCutPercent;
            pendingSum  += it.soldQuantity * it.price * (1 - AppConfig.storeCutPercent);
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

        // Determine how many rows we will display (max 5)
        int size = Math.min(5, transactionList.size());

        // Create a 2D array for ONLY the last 5 transactions
        String[][] rows = new String[size][3];

        // Start index (so we get the LAST 5)
        int start = transactionList.size() - size;

        for (int i = 0; i < size; i++) {
            Transaction t = transactionList.get(start + i);
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
        int quantity;        // total stock
        int soldQuantity;    // how many sold
        String expiry;

        Item(String id, String name, double price, int quantity, int soldQuantity, String expiry) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.soldQuantity = soldQuantity;
            this.expiry = expiry;
        }

        boolean isSoldOut() {
            return soldQuantity >= quantity;
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
        itemIDField = new Style.RoundedTextField(30);
        sellItemButton = new Style.RoundedButton(30);
        findItemButton = new Style.RoundedButton(30);

        itemsDue = new JTable();
        transactions = new JTable();
        itemDetail = new JTable();

    }
}
