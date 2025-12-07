package ui;

import classes.*;
import handlers.InventoryHandler;
import handlers.SupplierHandler;
import handlers.TransactionsHandler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JPanel {

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

    private InventoryHandler inventoryHandler;
    private TransactionsHandler transactionsHandler;
    private SupplierHandler supplierHandler;

    private final String[] headers = {"Item Name", "Item ID", "Consignor", "Quantity", "Price", "Date Received", "Expiry/Return Date", "Item Type"};
    private Object[][] data;

    // -----------------------------------------------------
    // Constructor
    // -----------------------------------------------------
    public DashboardPanel(InventoryHandler inv, TransactionsHandler trans, SupplierHandler supp) {

        this.inventoryHandler = inv;
        this.transactionsHandler = trans;
        this.supplierHandler = supp;

        setLayout(new BorderLayout());
        add(dashboardPanel, BorderLayout.CENTER);
        itemsDueScrollPane.setViewportView(itemsDue);

        itemsDue.setOpaque(false);
        itemsDue.setBackground(new Color(0, 0, 0, 0));
        itemsDue.setShowGrid(true);

        itemsDueScrollPane.setOpaque(false);
        itemsDueScrollPane.getViewport().setOpaque(false);

        itemsDueScrollPane.setBorder(null);

        itemsDue.getTableHeader().setOpaque(false);
        itemsDue.getTableHeader().setBackground(new Color(0,0,0,0));
        itemsDue.setRowHeight(30);

        makeTransparent(dashboardPanel);

        setupAutoComplete();
        refresh();

        // -----------------------------------------------------
        // FIND ITEM
        // -----------------------------------------------------
        findItemButton.addActionListener(e -> {
            String id = itemIDField.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a valid ID.");
                return;
            }

            Item found = inventoryHandler.getInventoryList().stream()
                    .filter(it -> it.getItemID().equalsIgnoreCase(id))
                    .findFirst()
                    .orElse(null);

            if (found == null) {
                JOptionPane.showMessageDialog(this, "Item not found.");
                return;
            }

            // Calculate sold quantity dynamically
            long soldQty = transactionsHandler.getTransactionList().stream()
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
                JOptionPane.showMessageDialog(this, "Enter a valid ID.");
                return;
            }

            Item item = inventoryHandler.getInventoryList().stream()
                    .filter(it -> it.getItemID().equalsIgnoreCase(id))
                    .findFirst()
                    .orElse(null);

            if (item == null) {
                JOptionPane.showMessageDialog(this, "Item ID not found.");
                return;
            }

            if (isSoldOut(item)) {
                JOptionPane.showMessageDialog(this, "No more stock available!");
                return;
            }

            transactionsHandler.processSale(item.getItemID());

            JOptionPane.showMessageDialog(this, "Item Sold!");

            itemIDField.setText("");
            itemDetail.setModel(new DefaultTableModel());
            refresh();
        });

    }

    public void refresh() {
        updateTotals();
        loadItemsDueTable();
        loadRecentTransactions();
    }

    // -----------------------------------------------------
    // UPDATE TOTALS
    // -----------------------------------------------------
    private void updateTotals() {
        String today = LocalDate.now().toString();
        List<Transaction> allTrans = transactionsHandler.getTransactionList();

        List<Transaction> todayTransactions = new ArrayList<>();
        for (Transaction t : allTrans) {
            if (t.getSaleDate().toLocalDate().toString().equals(today)) {
                todayTransactions.add(t);
            }
        }

        long soldCount = todayTransactions.size();
        double salesSum = 0;
        double earningsSum = 0;

        for (Transaction t : todayTransactions) {
            salesSum += t.getTotalAmount();
            earningsSum += t.getStoreRevenue();
        }

        double pendingSum = 0;
        for (Consignor c : supplierHandler.getSupplierList()) {
            pendingSum += c.getPayableBalance();
        }

        totalSales.setText(String.format("$%.2f", salesSum));
        earnings.setText(String.format("$%.2f", earningsSum));
        itemSold.setText(String.valueOf(soldCount));
        pendingPayout.setText(String.format("$%.2f", pendingSum));
    }

    // -----------------------------------------------------
    // ITEMS DUE - SORT BY NEAREST EXPIRY
    // -----------------------------------------------------
    private void loadItemsDueTable() {
        List<Item> allItems = inventoryHandler.getInventoryList();
        LocalDate today = LocalDate.now();

        // Filter: Item must be AVAILABLE AND ReturnDate is Today or before
        List<Item> dueItems = allItems.stream()
                .filter(i -> i.getReturnDate().isBefore(today) || i.getReturnDate().isEqual(today))
                .filter(i -> i.getStatus().equals("AVAILABLE"))
                .toList();

        String[] cols = {"Item ID", "Name", "Owner", "Expiry Date", "Price"};
        String[][] rows = new String[dueItems.size()][cols.length];

        for (int i = 0; i < dueItems.size(); i++) {
            Item item = dueItems.get(i);
            rows[i][0] = item.getItemID();
            rows[i][1] = item.getName();
            rows[i][2] = item.getOwner().getName();
            rows[i][3] = item.getReturnDate().toString();
            rows[i][4] = String.format("%.2f", item.getSellingPrice());
        }

        drawTable(itemsDue, rows, cols);
    }

    // -----------------------------------------------------
    // TRANSACTION TABLE
    // -----------------------------------------------------
    private void loadRecentTransactions() {
        List<Transaction> allTransactions = transactionsHandler.getTransactionList();

        int maxRows = 5;
        int rowCount = Math.min(allTransactions.size(), maxRows);

        String[] cols = {"Trans ID", "Item Name", "Amount", "Date"};
        String[][] data = new String[rowCount][cols.length];

        int listIndex = allTransactions.size() - 1;

        for (int i = 0; i < rowCount; i++) {
            Transaction t = allTransactions.get(listIndex);

            data[i][0] = t.getTransactionId();
            data[i][1] = t.getSoldItem().getName();
            data[i][2] = String.format("$%.2f", t.getTotalAmount());
            data[i][3] = t.getSaleDate().toLocalDate().toString();

            listIndex--;
        }

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactions.setModel(model);
        transactions.setRowHeight(25);
    }

    private void setupAutoComplete() {
        JPopupMenu suggestionPopup = new JPopupMenu();

        // 1. Add Listener to the Text Field
        itemIDField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateSuggestions(); }

            private void updateSuggestions() {
                String text = itemIDField.getText().trim().toLowerCase();
                suggestionPopup.setVisible(false);
                suggestionPopup.removeAll();

                if (text.isEmpty()) return;

                // 2. Filter Inventory for Matches
                List<Item> matches = inventoryHandler.getInventoryList().stream()
                        .filter(item -> item.getName().toLowerCase().contains(text) ||
                                item.getItemID().toLowerCase().contains(text))
                        .limit(3) // Limit to max 3 suggestions
                        .toList();

                if (matches.isEmpty()) return;

                // 3. Create Menu Items
                for (Item match : matches) {
                    // Display: "Name (ID)"
                    String label = match.getName() + " (" + match.getItemID() + ")";
                    JMenuItem item = new JMenuItem(label);

                    // 4. Handle Selection
                    item.addActionListener(e -> {
                        // When clicked, fill the box with the ID only (needed for logic)
                        itemIDField.setText(match.getItemID());
                        suggestionPopup.setVisible(false);
                    });

                    suggestionPopup.add(item);
                }

                // 5. Show Popup below the field
                suggestionPopup.show(itemIDField, 0, itemIDField.getHeight());
                itemIDField.requestFocus(); // Keep focus on typing
            }
        });

        // Optional: Hide popup when Enter is pressed
        itemIDField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    suggestionPopup.setVisible(false);
                }
            }
        });
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

    // Stock left is dynamically calculated from transactions
    int stockLeft(Item i) {
        long soldCount;
        soldCount = transactionsHandler.getTransactionList().stream()
                .filter(t -> t.getSoldItem().getItemID().equalsIgnoreCase(i.getItemID())).count();
        return i.getQuantity() - (int) soldCount;
    }

    boolean isSoldOut(Item i) {
        return stockLeft(i) <= 0;
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
