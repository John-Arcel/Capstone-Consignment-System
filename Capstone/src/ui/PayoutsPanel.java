package ui;

import handlers.*;
import classes.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



public class PayoutsPanel extends JPanel {

    private JPanel content;
    private JTextField Search;
    private JTable HistoryTable;
    private JTable PendingTable;
    private JScrollPane ScrollPending;
    private JScrollPane ScrollHistory;
    private JLabel PayoutNum;
    private JLabel PendingNum;
    private JButton transferButton;

    private TransactionsHandler transactionsHandler;
    private PayoutsHandler payoutsHandler;
    private SupplierHandler supplierHandler;

    private List<Object[]> historyDataList = new ArrayList<>(); // list of objects na makita sa history
    private List<Object[]> pendingDataList = new ArrayList<>(); // list of objects na makita sa pending

    private Object[][] historyData;
    private final String[] historyHeaders = {"Payout ID", "Consignor", "Amount Paid", "Date"};
    private Object[][] pendingData;
    private final String[] pendingHeaders = {"Select", "Transaction ID", "Total Amount", "Share", "Consignor"};

    public static int payoutIdCtr;
    int countPending = 0; // for the total pending display
    int countPayout = 0; // for the total payout displayed

    public PayoutsPanel(TransactionsHandler t, PayoutsHandler p, SupplierHandler s) {
        this.transactionsHandler = t;
        this.payoutsHandler = p;
        this.supplierHandler = s;

        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);

        // Initial Load
        refresh();

        // Setup Tables
        HistoryTable.setRowHeight(30);
        PendingTable.setRowHeight(30);

        // Listeners
        transferButton.addActionListener(e -> handleTransferAction());

        Search.addActionListener(e -> runCombinedSearch());

        // Placeholder text logic
        Search.setText("Search Payout ID");
        Search.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (Search.getText().equals("Search Payout ID")) Search.setText("");
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (Search.getText().isEmpty()) Search.setText("Search Payout ID");
            }
        });
    }

    // ========================================================
    // CORE REFRESH LOGIC
    // ========================================================
    public void refresh() {
        // 1. Fetch History Data directly from Handler
        this.historyData = payoutsHandler.getAllPayouts();

        // 2. Fetch Pending Data (Filter unpaid transactions)
        // We convert the list to Object[][] to match the 'data' field style
        List<Object[]> pendingList = new ArrayList<>();
        for (Transaction tr : transactionsHandler.getTransactionList()) {
            if (!tr.isPaid()) { // Only show unpaid items
                pendingList.add(new Object[]{
                        false, // Checkbox starts unchecked
                        tr.getTransactionId(),
                        String.format("%.2f", tr.getTotalAmount()),
                        String.format("%.2f", tr.getConsignorShare()),
                        tr.getSoldItem().getOwner().getName()
                });
            }
        }
        this.pendingData = pendingList.toArray(new Object[0][]);

        // 3. Draw Tables
        drawHistoryTable(historyData);
        drawPendingTable(pendingData);
        prettifyTables();

        // 4. Update Labels
        updateCounters();
    }

    // ========================================================
    // TABLE DRAWING METHODS
    // ========================================================

    private void drawHistoryTable(Object[][] rows) {
        // Standard Read-Only Table (Same as TransactionsPanel)
        DefaultTableModel model = new DefaultTableModel(rows, historyHeaders) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        HistoryTable.setModel(model);
        HistoryTable.getTableHeader().setReorderingAllowed(false);
    }

    private void drawPendingTable(Object[][] rows) {
        // Custom Table: Column 0 (Checkbox) MUST be editable
        DefaultTableModel model = new DefaultTableModel(rows, pendingHeaders) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only the checkbox column is editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Renders the first column as a Boolean Checkbox
                return columnIndex == 0 ? Boolean.class : super.getColumnClass(columnIndex);
            }
        };
        PendingTable.setModel(model);

        // Lock checkbox column width
        PendingTable.getColumnModel().getColumn(0).setMaxWidth(50);
        PendingTable.getTableHeader().setReorderingAllowed(false);
    }

    private void prettifyTables() {
        // Apply your custom formatters (Center alignment, currency, etc.)

        // History Table
        for (int i = 0; i < HistoryTable.getColumnCount(); i++) {
            HistoryTable.getColumnModel().getColumn(i).setCellRenderer(new TableFormatter.PaddedCellRenderer(5, 5, 5, 5));
        }
        // Example: Align Amount column (Index 2) to right
        HistoryTable.getColumnModel().getColumn(2).setCellRenderer(new TableFormatter.DollarDecimalRenderer(5, 5, 5, 5));

        // Pending Table
        for (int i = 1; i < PendingTable.getColumnCount(); i++) {
            PendingTable.getColumnModel().getColumn(i).setCellRenderer(new TableFormatter.PaddedCellRenderer(5, 5, 5, 5));
        }
    }

    private void updateCounters() {
        double pendingTotal = 0;
        for (Transaction t : transactionsHandler.getTransactionList()) {
            if (!t.isPaid()) pendingTotal += t.getConsignorShare();
        }

        PayoutNum.setText(String.valueOf(historyData.length));
        PendingNum.setText(String.format("$%.2f", pendingTotal));
    }

    // ========================================================
    // LOGIC ACTIONS
    // ========================================================

    private void handleTransferAction() {
        List<String> paidIDs = new ArrayList<>();
        double totalPaid = 0;

        // Loop through the table model to see what is checked
        for (int i = 0; i < PendingTable.getRowCount(); i++) {
            boolean isChecked = (Boolean) PendingTable.getValueAt(i, 0);
            if (isChecked) {
                String transID = (String) PendingTable.getValueAt(i, 1);
                paidIDs.add(transID);
            }
        }

        if (paidIDs.isEmpty()) {
            Style.showCustomMessage(this, "No items selected.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean confirmed = Style.showCustomConfirm(this,
                "Are you sure you want to process payments for " + paidIDs.size() + " items?",
                "Confirm Payout");

        if (!confirmed) {
            return;
        }

        // Process actual payments
        for (String id : paidIDs) {
            totalPaid += payoutsHandler.processPayout(id);
        }

        refresh(); // Reload tables
        String successMsg = "Paid $" + String.format("%.2f", totalPaid) + " for " + paidIDs.size() + " transactions.";
        Style.showCustomMessage(this, successMsg, "Payout Successful", JOptionPane.INFORMATION_MESSAGE);    }

    private Transaction findTransaction(String id) {
        for (Transaction t : transactionsHandler.getTransactionList()) {
            if (t.getTransactionId().equals(id)) return t;
        }
        return null;
    }

    private void runCombinedSearch() {
        String text = Search.getText().trim().toLowerCase();
        if (text.isEmpty() || text.equals("search payout id")) {
            drawHistoryTable(historyData); // Reset
            return;
        }

        // Filter Logic
        List<Object[]> filtered = new ArrayList<>();
        for (Object[] row : historyData) {
            String id = row[0].toString().toLowerCase(); // Column 0 is ID
            if (id.contains(text)) {
                filtered.add(row);
            }
        }
        drawHistoryTable(filtered.toArray(new Object[0][]));
        prettifyTables();
    }

    // Helper for IntelliJ Designer
    private void createUIComponents() {
        Search = new Style.RoundedTextField(40);
        transferButton = new Style.RoundedButton(40);
    }
}