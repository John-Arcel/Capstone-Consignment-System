package ui;

import classes.Item;
import classes.Transaction;
import handlers.TransactionsHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionsPanel extends JPanel{
    private JPanel contentPane;
    private JTable table;
    private JScrollPane scroll;
    private JTextField searchTextField;
    private JLabel totalTransactionsLabel;
    private JLabel totalStoreRevenueLabel;
    private JLabel totalConsignorShareLabel;
    private Object[][] data;
    private final String[] columnNames = {"Transaction ID", "Items", "Date", "Total", "Store Revenue", "Consignor Share"};

    private TransactionsHandler transactionsHandler;

    public TransactionsPanel(TransactionsHandler transactionsHandler){
        this.transactionsHandler = transactionsHandler;

        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);

        refresh();

        // add placeholder to text-field
        searchTextField.setText("Search Transaction ID");
        searchTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchTextField.getText().equals("Search Transaction ID")) {
                    searchTextField.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchTextField.getText().isEmpty()) {
                    searchTextField.setText("Search Transaction ID");
                }
            }
        });

        table.setRowHeight(30);

        //Search Transaction ID
        searchTextField.addActionListener(e->runCombinedSearch());

        //purpose: focus on window first (default is focus on text field)
        SwingUtilities.invokeLater(() -> contentPane.requestFocusInWindow());
    }

    public void refresh() {
        // Get the latest matrix from the Handler
        this.data = transactionsHandler.getAllTransactions();

        // Redraw table
        drawTable(data, columnNames);
        prettifyTable();

        // Recalculate and Update Labels
        totalTransactionsLabel.setText(Integer.toString(getTotalTransactions()));
        totalStoreRevenueLabel.setText(String.format("$%.2f", getTotalStoreRevenue()));
        totalConsignorShareLabel.setText(String.format("$%.2f", getTotalConsignorShare()));
    }

    public void drawTable(Object[][] rows, Object[] headers) {
        //purpose: disables cell editing
        DefaultTableModel model = new DefaultTableModel(rows, headers) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
        table.getTableHeader().setReorderingAllowed(false);
    }

    private void prettifyTable() {
        // It does:
        // 1. adds cell padding to the table cell
        // 2. adds $ sign in front and displays 2 decimal places for double values (total, storeRevenue, consignorShare)

        // total, storeRevenue, consignorShare → $ with 2 decimals
        table.getColumnModel().getColumn(3).setCellRenderer(new TableFormatter.DollarDecimalRenderer(5, 5, 5, 5));

        table.getColumnModel().getColumn(4).setCellRenderer(new TableFormatter.DollarDecimalRenderer(5, 5, 5, 5));

        table.getColumnModel().getColumn(5).setCellRenderer(new TableFormatter.DollarDecimalRenderer(5, 5, 5, 5));

        // Other columns → padded text
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 3 && i != 4 && i != 5) {
                table.getColumnModel().getColumn(i).setCellRenderer(new TableFormatter.DollarDecimalRenderer(5, 5, 5, 5));
            }
        }

    }
    // private for now if walay lain class maggamit
    private int getTotalTransactions(){
        return data.length;
    }

    // private for now if walay lain class maggamit
    private double getTotalStoreRevenue(){
        double total = 0;

        for(int i=0; i<data.length;i++){
            total = total + (double)data[i][4];
        }

        return total;
    }

    // private for now if walay lain class maggamit
    private double getTotalConsignorShare(){
        double total = 0;

        for(int i=0; i<data.length;i++){
            total = total + (double)data[i][5];
        }

        return total;
    }

    private void runCombinedSearch() {
        String idText = searchTextField.getText().trim();

        boolean noID = idText.isEmpty() || idText.equals("Search Transaction ID");

        // If both empty → reset dataTable
        if (noID) {
            drawTable(data, columnNames);
            prettifyTable();
            return;
        }

        filterTable(idText);
    }

    private void filterTable(String idFilter) {
        List<Object[]> filtered = new ArrayList<>();

        for (Object[] row : data) {
            String itemID = row[0].toString().toLowerCase();

            // filters dataTable for transaction id
            boolean idMatches = idFilter.isEmpty() || idFilter.equals("Search Transaction ID")
                    || itemID.contains(idFilter.toLowerCase());

            if (idMatches) {
                filtered.add(row);
            }
        }

        for(Object[] a: filtered){
            System.out.println(a[0]);
        }

        // Convert list → Object[][]
        Object[][] filteredData = new Object[filtered.size()][];
        for (int i = 0; i < filtered.size(); i++) {
            filteredData[i] = filtered.get(i);
        }

        // Update dataTable
        drawTable(filteredData, columnNames);
        prettifyTable();
    }

    private void createUIComponents() {
        int roundRadius = 30;
        searchTextField = new Style.RoundedTextField(roundRadius);

    }

}
