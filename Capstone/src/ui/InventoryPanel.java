package ui;

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
    private Object[][] data = {
            {"Apple",       "I-0000001", "John", 1, 100.50, "01/01/2025", "01/01/2026", "Perishable"},
            {"Banana",      "I-0000002", "Mary", 2, 50.01, "02/01/2025", "02/01/2026", "Perishable"},
            {"Carrot",      "I-0000003", "Alice", 3, 30.02, "03/01/2025", "03/01/2026", "Perishable"},
            {"Dates",       "I-0000004", "Bob", 4, 200.34, "04/01/2025", "04/01/2026", "Perishable"},
            {"Eggplant",    "I-0000005", "Eve", 5, 80.76, "05/01/2025", "05/01/2026", "Perishable"},
            {"Metal",         "I-0000006", "John", 6, 150.00, "06/01/2025", "06/01/2026", "Non-Perishable"},
            {"Screw",      "I-0000007", "Mary", 7, 120.00, "07/01/2025", "07/01/2026", "Non-Perishable"},
            {"Iron",    "I-0000008", "Alice", 8, 180.11, "08/01/2025", "08/01/2026", "Non-Perishable"},
            {"Gold",     "I-0000009", "Bob", 9, 60.01, "09/01/2025", "09/01/2026", "Non-Perishable"},
            {"Diamond",   "I-0000010", "Eve", 10, 300.69, "10/01/2025", "10/01/2026", "Non-Perishable"}
    };

    public InventoryPanel() {
        setLayout(new BorderLayout());

        add(contentPane, BorderLayout.CENTER);

        dataTable.setRowHeight(30);
        dataTable.setBounds(0,0,100,200);
        drawTable(data, headers);
        prettifyTable();

        int totalItems = getTotalItems();
        int totalConsignors = getTotalConsignors();
        totalItemsLabel.setText(Integer.toString(totalItems));
        totalConsignorsLabel.setText(Integer.toString(totalConsignors));
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
                String newItemID = generateID('I', getMaxID(data)+1);
                newItemData.add(1, newItemID);
                addRow(newItemData);
            }
            drawTable(data, headers);
            prettifyTable();
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
                deleteRow(selectedRow);

                drawTable(data, headers);
                prettifyTable();
                totalItemsLabel.setText(Integer.toString(getTotalItems()));
                totalConsignorsLabel.setText(Integer.toString(getTotalConsignors()));
            }
        });
//
//        pack();
//        setVisible(true);
//        //purpose: focus on window first (default is focus on text field)
//        SwingUtilities.invokeLater(() -> contentPane.requestFocusInWindow());
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

    private void addRow(ArrayList<Object> newRow) {
        List<Object[]> dataList = new ArrayList<>(java.util.Arrays.asList(data));
        dataList.add(newRow.toArray());
        data = dataList.toArray(new Object[0][]);
    }

    private void deleteRow(int selectedRow) {
        // Convert view index to model index in case dataTable is sorted
        int modelRow = dataTable.convertRowIndexToModel(selectedRow);

        // Remove the row
        List<Object[]> dataList = new ArrayList<>(java.util.Arrays.asList(data));
        dataList.remove(modelRow);
        data = dataList.toArray(new Object[0][]);
    }

    //purpose: public generator for item id
    public static String generateID(char prefix, int number) {
        // %07d → pads number with zeros to 7 digits
        return String.format("%c-%07d", prefix, number);
    }

    //purpose: returns the max id from a data matrix, used alongside generateID()
    private static int getMaxID(Object[][] data) {
        int max = 0;
        for (Object[] row : data) {
            String id = row[1].toString();          // e.g., "I-0000010"
            String numberPart = id.split("-")[1];   // "0000010"
            int num = Integer.parseInt(numberPart);
            if (num > max) max = num;
        }
        return max;
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
        });
        dataTable.getTableHeader().setReorderingAllowed(false);
    }

    //purpose: adds dataTable cell padding (5), center-aligns quantity, 2 decimal + $ to price
    private void prettifyTable() {
        int cellPadding = 5;
        // It does:
        // 1. adds cell padding to the dataTable cell
        // 2. center-aligns integer values (quantity column)
        // 3. adds $ sign in front and displays 2 decimal places for double values (price column)

        // Quantity → integer style
        dataTable.getColumnModel().getColumn(3).setCellRenderer(new TableFormatter.IntegerRenderer(cellPadding, cellPadding, cellPadding, cellPadding));

        // Price → $ with 2 decimals
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
