package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JPanel {

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

    private final String[] headers = {"Item Name", "Item ID", "Consignor", "Quantity", "Price", "Date Received", "Expiry/Return Date", "Item Type"};
    private Object[][] data = {
            {"Apple",       "I-0000001", "John", 1, 100.50, "01/01/2025", "01/01/2026", "Perishable"},
            {"Banana",      "I-0000002", "Mary", 2, 50.01, "02/01/2025", "02/01/2026", "Perishable"},
            {"Carrot",      "I-0000003", "Alice", 3, 30.02, "03/01/2025", "03/01/2026", "Perishable"},
            {"Dates",       "I-0000004", "Bob", 4, 200.34, "04/01/2025", "04/01/2026", "Perishable"},
            {"Eggplant",    "I-0000005", "Eve", 5, 80.76, "05/01/2025", "05/01/2026", "Perishable"},
            {"Metal",       "I-0000006", "John", 6, 150.00, "06/01/2025", "06/01/2026", "Non-Perishable"},
            {"Screw",       "I-0000007", "Mary", 7, 120.00, "07/01/2025", "07/01/2026", "Non-Perishable"},
            {"Iron",        "I-0000008", "Alice", 8, 180.11, "08/01/2025", "08/01/2026", "Non-Perishable"},
            {"Gold",        "I-0000009", "Bob", 9, 60.01, "09/01/2025", "09/01/2026", "Non-Perishable"},
            {"Diamond",     "I-0000010", "Eve", 10, 300.69, "10/01/2025", "10/01/2026", "Non-Perishable"}
    };


    // -----------------------------------------------------
    // Constructor
    // -----------------------------------------------------
    public DashboardPanel() {

        setLayout(new BorderLayout());
        add(dashboardPanel, BorderLayout.CENTER);

        itemsDueScrollPane.setViewportView(itemsDue);

        // Make the text visible (white)
//        itemIDField.setForeground(Color.WHITE);
//        itemIDField.setCaretColor(Color.WHITE);
//        itemIDField.setBackground(new Color(40,40,40));

        itemsDue.setOpaque(false);
        itemsDue.setBackground(new Color(0, 0, 0, 0));
        itemsDue.setShowGrid(true);

        itemsDueScrollPane.setOpaque(false);
        itemsDueScrollPane.getViewport().setOpaque(false);

        itemsDueScrollPane.setBorder(null);

        itemsDue.getTableHeader().setOpaque(false);
        itemsDue.getTableHeader().setBackground(new Color(0,0,0,0));
        itemsDue.setRowHeight(30);

// -------------------------------------------------------

//        itemIDField.setForeground(Color.WHITE);
//        itemIDField.setCaretColor(Color.WHITE);
//        itemIDField.setBackground(new Color(40,40,40));


        makeTransparent(dashboardPanel);

        loadSampleData();
        loadTransactionTable();
        loadItemsDueTable();
        updateTotals(); // initial calculation

        // -----------------------------------------------------
        // AUTO-FILL ITEM ID WHEN TABLE ROW CLICKED
        // -----------------------------------------------------
        itemsDue.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = itemsDue.getSelectedRow();
                if (selectedRow >= 0) {
                    String itemId = itemsDue.getValueAt(selectedRow, 0).toString(); // Column 0 = Item ID
                    itemIDField.setText(itemId);
                }
            }
        });


        // -----------------------------------------------------
        // FIND ITEM
        // -----------------------------------------------------
        findItemButton.addActionListener(e -> {
            String id = itemIDField.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Item ID");
                return;
            }

            Item found = inventory.stream()
                    .filter(it -> it.id.equalsIgnoreCase(id))
                    .findFirst()
                    .orElse(null);

            if (found == null) {
                JOptionPane.showMessageDialog(this, "Item not found");
                return;
            }

            // Calculate sold quantity dynamically
            long soldQty = transactionList.stream()
                    .filter(t -> t.itemID.equalsIgnoreCase(found.id))
                    .count();
            int stockLeft = found.quantity - (int) soldQty;

            String[] cols = {"Field", "Value"};
            String[][] rows = {
                    {"Item ID", found.id},
                    {"Name", found.name},
                    {"Price", String.format("₱%.2f", found.price)},
                    {"Quantity", String.valueOf(found.quantity)},
                    {"Sold Quantity", String.valueOf(soldQty)},
                    {"Stock Left", String.valueOf(stockLeft)},
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

            Item foundItem = inventory.stream()
                    .filter(it -> it.id.equalsIgnoreCase(id))
                    .findFirst()
                    .orElse(null);

            if (foundItem == null) {
                JOptionPane.showMessageDialog(this, "Item ID not found.");
                return;
            }

            if (foundItem.isSoldOut(transactionList)) {
                JOptionPane.showMessageDialog(this, "No more stock available!");
                return;
            }

            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            transactionList.add(new Transaction(id, today, foundItem.price));

            updateTotals();
            loadTransactionTable();
            loadItemsDueTable();

            JOptionPane.showMessageDialog(this, "Item Sold!");
        });




    }

    // -----------------------------------------------------
    // SAMPLE DATA
    // -----------------------------------------------------
    private void loadSampleData() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        for (Object[] row : data) {
            String name = (String) row[0];
            String id = (String) row[1];
            String consignor = (String) row[2];
            int qty = (int) row[3];
            double price = (double) row[4];
            String dateReceived = (String) row[5];
            String expiry = (String) row[6];
            String itemType = (String) row[7];

            inventory.add(new Item(id, name, consignor, price, qty, dateReceived, expiry, itemType));
        }

        // Example transactions
        transactionList.add(new Transaction("I-0000001", "11/29/2025", 100.50));
        transactionList.add(new Transaction("I-0000003", "11/29/2025", 30.02));
    }



    // -----------------------------------------------------
    // ITEMS DUE - SORT BY NEAREST EXPIRY
    // -----------------------------------------------------
    private void loadItemsDueTable() {
        ArrayList<Item> due = new ArrayList<>();

        // Only items with stock > 0 AND not expired
        for (Item it : inventory) {
            if (it.stockLeft(transactionList) > 0 && !it.isExpired()) {
                due.add(it);
            }
        }

        DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        due.sort((a, b) -> LocalDate.parse(a.expiry, f)
                .compareTo(LocalDate.parse(b.expiry, f)));

        String[] cols = {"Item ID", "Name", "Consignor", "Expiry", "Stock Left", "Price", "Item Type"};
        String[][] rows = new String[due.size()][cols.length];

        for (int i = 0; i < due.size(); i++) {
            Item it = due.get(i);
            rows[i][0] = it.id;
            rows[i][1] = it.name;
            rows[i][2] = it.consignor;
            rows[i][3] = it.expiry;
            rows[i][4] = String.valueOf(it.stockLeft(transactionList));
            rows[i][5] = String.format("₱%.2f", it.price);
            rows[i][6] = it.itemType;
        }

        itemsDue.setModel(new DefaultTableModel(rows, cols));
    }





    // -----------------------------------------------------
    // UPDATE TOTALS
    // -----------------------------------------------------
    private void updateTotals() {
        int soldCount = transactionList.size();
        double earningsSum = 0;
        double pendingSum = 0;

        for (Transaction t : transactionList) {
            Item item = inventory.stream()
                    .filter(i -> i.id.equalsIgnoreCase(t.itemID))
                    .findFirst()
                    .orElse(null);

            if (item != null) {
                earningsSum += t.amount * AppConfig.storeCutPercent;
                pendingSum  += t.amount * (1 - AppConfig.storeCutPercent);
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
        transactions.setRowHeight(30);
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
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new DashboardPanel().setVisible(true));
//    }

    // -----------------------------------------------------
    // DATA CLASSES
    // -----------------------------------------------------
    static class Item {
        String id;
        String name;
        String consignor;
        double price;
        int quantity;       // total stock
        String dateReceived;
        String expiry;
        String itemType;

        Item(String id, String name, String consignor, double price, int quantity,
             String dateReceived, String expiry, String itemType) {
            this.id = id;
            this.name = name;
            this.consignor = consignor;
            this.price = price;
            this.quantity = quantity;
            this.dateReceived = dateReceived;
            this.expiry = expiry;
            this.itemType = itemType;
        }

        // Stock left is dynamically calculated from transactions
        int stockLeft(List<Transaction> transactions) {
            long soldCount = transactions.stream()
                    .filter(t -> t.itemID.equalsIgnoreCase(this.id))
                    .count();
            return quantity - (int) soldCount;
        }

        boolean isSoldOut(List<Transaction> transactions) {
            return stockLeft(transactions) <= 0;
        }

        boolean isExpired() {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate expiryDate = LocalDate.parse(expiry, f);
            return expiryDate.isBefore(LocalDate.now());
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
