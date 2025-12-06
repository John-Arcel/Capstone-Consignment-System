package ui;

import handlers.InventoryHandler;
import handlers.SupplierHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

// ================================================================================
// -------------------------- InventoryPanel class --------------------------------
// ================================================================================

public class InventoryPanel extends JPanel {
    private JPanel contentPane;
    private JTable dataTable;
    private JTextField searchIDTextField;
    private JTextField searchConsignorTextField;
    private JLabel totalItemsLabel;
    private JLabel totalConsignorsLabel;
    private JButton addItemButton;
    private JButton deleteItemButton;

    private final String[] headers = {"Item Name", "Item ID", "Consignor", "Quantity", "Price", "Date Received", "Expiry/Return Date", "Item Type"};
    private Object[][] data;

    private InventoryHandler inventoryHandler;
    private SupplierHandler supplierHandler;

    public InventoryPanel(InventoryHandler inventoryHandler, SupplierHandler supplierHandler) {
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);

        this.inventoryHandler = inventoryHandler;
        this.supplierHandler = supplierHandler;

        // Todo: connect data from InventoryHandler
        refresh();

        dataTable.setRowHeight(30);
        dataTable.setBounds(0,0,100,200);
        addTextFieldPlaceholderText();

        //purpose: adds functionality to search by item id and consignor
        searchIDTextField.addActionListener(e -> runCombinedSearch());
        searchConsignorTextField.addActionListener(e -> runCombinedSearch());

        addItemButton.addActionListener(e ->  {
            AddItemDialog dialog = new AddItemDialog();
            dialog.pack();
            dialog.setVisible(true);

            if(dialog.isConfirmed()) {
                ArrayList<Object> newItemData = dialog.getAllFieldInput();
                inventoryHandler.addItem(
                        (String) newItemData.getFirst(),
                        (String) newItemData.get(1),
                        (String) newItemData.get(2),
                        (String) newItemData.get(3),
                        (String) newItemData.get(4),
                        (String) newItemData.get(5),
                        (boolean) newItemData.get(6)
                );
            }
            refresh();
        });

        deleteItemButton.addActionListener(e -> {
            int selectedRow = dataTable.getSelectedRow();

            //purpose: error dialog box if user did not select row
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a row first!",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            //purpose: show confirm dialog window to confirm deletion
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the selected item?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            //purpose: if deletion is confirmed, delete the row and update total items/consignors
            if (result == JOptionPane.YES_OPTION) {
                inventoryHandler.deleteItem(dataTable.getValueAt(selectedRow, 1).toString());

                refresh();
            }
        });
    }

    // ================================================================================
    // --------------------- Logic for search in both text fields ---------------------
    // ================================================================================

    private void runCombinedSearch() {
        String idText = searchIDTextField.getText().trim();
        String consignorText = searchConsignorTextField.getText().trim();

        boolean noID = idText.isEmpty() || idText.equals("Search Item ID");
        boolean noConsignor = consignorText.isEmpty() || consignorText.equals("Search Consignor");

        // If both empty → reset dataTable
        if (noID && noConsignor) {
            drawTable(data, headers);
            prettifyTable();
            return;
        }

        filterTable(idText, consignorText);
    }

    private void filterTable(String idFilter, String consignorFilter) {
        List<Object[]> filtered = new ArrayList<>();

        for (Object[] row : data) {
            String itemID = row[1].toString().toLowerCase();
            String consignor = row[2].toString().toLowerCase();

            // filters dataTable for item id
            boolean idMatches = idFilter.isEmpty() || idFilter.equals("Search Item ID")
                    || itemID.contains(idFilter.toLowerCase());

            // filters dataTable for consignor
            boolean consignorMatches = consignorFilter.isEmpty() || consignorFilter.equals("Search Consignor")
                    || consignor.contains(consignorFilter.toLowerCase());

            if (idMatches && consignorMatches) {
                filtered.add(row);
            }
        }

        // Convert list → Object[][]
        Object[][] filteredData = new Object[filtered.size()][];
        for (int i = 0; i < filtered.size(); i++) {
            filteredData[i] = filtered.get(i);
        }

        // Update dataTable
        drawTable(filteredData, headers);
        prettifyTable();
    }

    // ================================================================================
    // ------------------------------ Helper methods ----------------------------------
    // ================================================================================

    public void refresh(){
        data = inventoryHandler.getAllItems();
        drawTable(data, headers);
        prettifyTable();
        totalItemsLabel.setText(Integer.toString(inventoryHandler.getAvailableItems()));
        totalConsignorsLabel.setText(Integer.toString(supplierHandler.getActiveConsignors()));
    }

    //purpose: draws the dataTable using original data and headers
    private void drawTable(Object[][] data, String[] headers) {
        dataTable.setModel(new javax.swing.table.DefaultTableModel(data, headers) {
            //purpose: to be able to sort double and int values properly in the dataTable
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 3 -> Double.class; // quantity
                    case 4 -> Double.class; // price
                    default -> String.class; // rest of the columns
                };
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        dataTable.getTableHeader().setReorderingAllowed(false);
    }

    //purpose: adds dataTable cell padding (5), center-aligns quantity, 2 decimal + ₱ to price
    private void prettifyTable() {
        int cellPadding = 5;
        // It does:
        // 1. adds cell padding to the dataTable cell
        // 2. center-aligns integer values (quantity column)
        // 3. adds ₱ sign in front and displays 2 decimal places for double values (price column)

        // Quantity → integer style
        dataTable.getColumnModel().getColumn(3).setCellRenderer(new TableFormatter.IntegerRenderer(cellPadding, cellPadding, cellPadding, cellPadding));

        // Price → ₱ with 2 decimals
        dataTable.getColumnModel().getColumn(4).setCellRenderer(new TableFormatter.DollarDecimalRenderer(cellPadding,cellPadding,cellPadding,cellPadding));

        // Other columns → padded text
        for (int i = 0; i < dataTable.getColumnCount(); i++) {
            if (i != 3 && i != 4) {
                dataTable.getColumnModel().getColumn(i).setCellRenderer(new TableFormatter.PaddedCellRenderer(cellPadding,cellPadding,cellPadding,cellPadding));
            }
        }
    }

    //purpose: add placeholder text to item id and consignor text field
    private void addTextFieldPlaceholderText() {
        searchIDTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchIDTextField.getText().equals("Search Item ID")) {
                    searchIDTextField.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchIDTextField.getText().isEmpty()) {
                    searchIDTextField.setText("Search Item ID");
                }
            }
        });
        searchConsignorTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchConsignorTextField.getText().equals("Search Consignor")) {
                    searchConsignorTextField.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchConsignorTextField.getText().isEmpty()) {
                    searchConsignorTextField.setText("Search Consignor");
                }
            }
        });
    }

    private int getTotalConsignors() {
        Set<String> uniqueNames = new HashSet<>();
        for (Object[] row : data) {
            if (row.length > 2 && row[2] != null) {
                uniqueNames.add(row[2].toString());
            }
        }
        return uniqueNames.size();
    }

    private int getTotalItems() {
        return data.length;
    }

    private void createUIComponents() {
        int roundRadius = 30;
        searchIDTextField = new Style.RoundedTextField(roundRadius);
        searchConsignorTextField = new Style.RoundedTextField(roundRadius);
        addItemButton = new Style.RoundedButton(roundRadius);
        deleteItemButton = new Style.RoundedButton(roundRadius);
        dataTable = new JTable();
    }
}
