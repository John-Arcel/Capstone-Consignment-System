package ui;

import classes.*;

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

    public List<Transaction> getTransactionList() {
        return transactionList;
    }
    private final String[] headers = {"Item Name", "Item ID", "Consignor", "Quantity", "Price", "Date Received", "Expiry/Return Date", "Item Type"};
    private Object[][] data = {
            {"Apple",       "I-0000001", "John", 1, 100.50, "2025-01-01", "2026-01-01", "Perishable"},
            {"Banana",      "I-0000002", "Mary", 2, 50.01, "2025-02-01", "2026-02-01", "Perishable"},
            {"Carrot",      "I-0000003", "Alice", 3, 30.02, "2025-03-01", "2026-03-01", "Perishable"},
            {"Dates",       "I-0000004", "Bob", 4, 200.34, "2025-04-01", "2026-04-01", "Perishable"},
            {"Eggplant",    "I-0000005", "Eve", 5, 80.76, "2025-05-01", "2026-05-01", "Perishable"},
            {"Metal",       "I-0000006", "John", 6, 150.00, "2025-06-01", "2026-06-01", "Non-Perishable"},
            {"Screw",       "I-0000007", "Mary", 7, 120.00, "2025-07-01", "2026-07-01", "Non-Perishable"},
            {"Iron",        "I-0000008", "Alice", 8, 180.11, "2025-08-01", "2026-08-01", "Non-Perishable"},
            {"Gold",        "I-0000009", "Bob", 9, 60.01, "2025-09-01", "2026-09-01", "Non-Perishable"},
            {"Diamond",     "I-0000010", "Eve", 10, 300.69, "2025-10-01", "2026-10-01", "Non-Perishable"}
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
                    .filter(it -> it.getItemID().equalsIgnoreCase(id))
                    .findFirst()
                    .orElse(null);

            if (found == null) {
                JOptionPane.showMessageDialog(this, "Item not found");
                return;
            }

            // Calculate sold quantity dynamically
            long soldQty = transactionList.stream()
                    .filter(t -> t.getSoldItem().getItemID().equalsIgnoreCase(found.getItemID()))
                    .count();
            int stockLeft = found.getQuantity() - (int) soldQty;

            String[] cols = {"Field", "Value"};
            String[][] rows = {
                    {"Item ID", found.getItemID()},
                    {"Name", found.getName()},
                    {"Price", String.format("₱%.2f", found.getSellingPrice())},
                    {"Quantity", String.valueOf(found.getQuantity())},
                    {"Sold Quantity", String.valueOf(soldQty)},
                    {"Stock Left", String.valueOf(stockLeft)},
                    {"Expiry", String.valueOf(found.getReturnDate())}
            };

            drawTable(itemDetail, rows, cols);
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
                    .filter(it -> it.getItemID().equalsIgnoreCase(id))
                    .findFirst()
                    .orElse(null);

            if (foundItem == null) {
                JOptionPane.showMessageDialog(this, "Item ID not found.");
                return;
            }

            if (isSoldOut(foundItem)) {
                JOptionPane.showMessageDialog(this, "No more stock available!");
                return;
            }

            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            Transaction t = new Transaction(id, foundItem);
            transactionList.add(t);


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

//            //todo remove later
            inventory.add(new Perishable(id, name, new Consignor(consignor, "12345"), qty, price, dateReceived, 1));
//            inventory.add(new NonPerishable(id, name, new Consignor("Josh", "123"), qty, price, dateReceived));
        }

        // Example transactions
//        transactionList.add(new Transaction("I-0000001", "11/29/2025", 100.50));
//        transactionList.add(new Transaction("I-0000003", "11/29/2025", 30.02));
    }



    // -----------------------------------------------------
    // ITEMS DUE - SORT BY NEAREST EXPIRY
    // -----------------------------------------------------
    private void loadItemsDueTable() {
        ArrayList<Item> due = new ArrayList<>();

        // Only items with stock > 0 AND not expired
        for (Item it : inventory) {
            if (stockLeft(it) > 0 ) {
                due.add(it);
            }
        }

        DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        due.sort((a, b) -> a.getReturnDate()
                .compareTo(b.getReturnDate()));

        String[] cols = {"Item ID", "Name", "Consignor", "Expiry", "Stock Left", "Price", "Item Type"};
        String[][] rows = new String[due.size()][cols.length];

        for (int i = 0; i < due.size(); i++) {
            Item it = due.get(i);
            rows[i][0] = it.getItemID();
            rows[i][1] = it.getName();
            rows[i][2] = it.getOwner().getName();
            rows[i][3] = String.valueOf(it.getReturnDate());
            rows[i][4] = String.valueOf(stockLeft(it));
            rows[i][5] = String.format("₱%.2f", it.getSellingPrice());
            if(it instanceof Perishable) {
                rows[i][6] = "Perishable";
            }else rows[i][6] = "Non-Perishable";
        }
        drawTable(itemsDue, rows, cols);
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
                    .filter(i -> i.getItemID().equalsIgnoreCase(t.getSoldItem().getItemID()))
                    .findFirst()
                    .orElse(null);

            if (item != null) {
                earningsSum += t.getTotalAmount() * AppConfig.storeCutPercent;
                pendingSum  += t.getTotalAmount() * (1 - AppConfig.storeCutPercent);
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
            rows[i][0] = t.getSoldItem().getItemID();
            rows[i][1] = String.valueOf(t.getSaleDate());
            rows[i][2] = String.format("₱%.2f", t.getTotalAmount());
        }

        drawTable(transactions, rows, cols);
        transactions.setRowHeight(30);
    }


    // -----------------------------------------------------
    // MAKE PANELS TRANSPARENT
    // -----------------------------------------------------
    public static void makeTransparent(JComponent container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JComponent jc) {

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


    // Stock left is dynamically calculated from transactions
    int stockLeft(Item i) {
        long soldCount;
        soldCount = transactionList.stream()
                .filter(t -> t.getSoldItem().getItemID().equalsIgnoreCase(i.getItemID())).count();
        return i.getQuantity() - (int) soldCount;
    }

    boolean isSoldOut(Item i) {
        return stockLeft(i) <= 0;
    }

    boolean isExpired(Item i) {
//        DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate expiryDate = i.getReturnDate();
        return expiryDate.isBefore(LocalDate.now());
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


    // -----------------------------------------------------
    // Helper functions
    // -----------------------------------------------------
    private void drawTable(JTable table, Object[][] rowData, Object[] headers) {

        //purpose: disables cell editing
        DefaultTableModel model = new DefaultTableModel(rowData, headers) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
    }
}
