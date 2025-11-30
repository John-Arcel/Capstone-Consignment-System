import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.text.DecimalFormat;

// ================================================================================
// -------------------------- InventoryPanel class --------------------------------
// ================================================================================

public class InventoryPanel extends JFrame {
    private JPanel contentPane;
    private JTable table;
    private JTextField searchIDTextField;
    private JTextField searchConsignorTextField;
    private JLabel totalItemsLabel;
    private JLabel totalConsignorsLabel;
    private JButton addItemButton;
    private JButton deleteItemButton;
    private JLabel EMPTY_SPACE;
    private JLabel EMPTY_SPACE2;

    private String[] headers = {"Item Name", "Item ID", "Consignor", "Quantity", "Price", "Date Received", "Return Date"};
    private Object[][] data = {
            {"Apple", "A001", "John", 1, 100.50, "01/01/2025", "01/01/2026"},
            {"Banana", "B002", "Mary", 2, 50.01, "02/01/2025", "02/01/2026"},
            {"Carrot", "C003", "Alice", 3, 30.02, "03/01/2025", "03/01/2026"},
            {"Dates", "D004", "Bob", 4, 200.34, "04/01/2025", "04/01/2026"},
            {"Eggplant", "E005", "Eve", 5, 80.76, "05/01/2025", "05/01/2026"},
            {"Fig", "F006", "John", 6, 150.00, "06/01/2025", "06/01/2026"},
            {"Grapes", "G007", "Mary", 7, 120.00, "07/01/2025", "07/01/2026"},
            {"Honeydew", "H008", "Alice", 8, 180.11, "08/01/2025", "08/01/2026"},
            {"Iceberg Lettuce", "I009", "Bob", 9, 60.01, "09/01/2025", "09/01/2026"},
            {"Jackfruit", "J010", "Eve", 10, 300.69, "10/01/2025", "10/01/2026"}
    };

    private int totalItems;
    private int totalConsignors;

    public InventoryPanel() {
        setContentPane(contentPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        table.setRowHeight(30);
        table.setBounds(0,0,100,200);
        drawTable(data, headers);
        prettifyTable();

        totalItems = getTotalItems();
        totalConsignors = getTotalConsignors();
        totalItemsLabel.setText(Integer.toString(totalItems));
        totalConsignorsLabel.setText(Integer.toString(totalConsignors));

        addTextFieldPlaceholderText();

        //purpose: adds functionality to search by item id and consignor
        searchIDTextField.addActionListener(e -> runCombinedSearch());
        searchConsignorTextField.addActionListener(e -> runCombinedSearch());

        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog();
            }
        });

        pack();
        setVisible(true);
        //purpose: focus on window first (default is focus on text field)
        SwingUtilities.invokeLater(() -> contentPane.requestFocusInWindow());
    }



    // ================================================================================
    // --------------------- Logic for search in both text fields ---------------------
    // ================================================================================

    private void runCombinedSearch() {
        String idText = searchIDTextField.getText().trim();
        String consignorText = searchConsignorTextField.getText().trim();

        boolean noID = idText.isEmpty() || idText.equals("Search Item ID");
        boolean noConsignor = consignorText.isEmpty() || consignorText.equals("Search Consignor");

        // If both empty → reset table
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

            // filters table for item id
            boolean idMatches = idFilter.isEmpty() || idFilter.equals("Search Item ID")
                    || itemID.contains(idFilter.toLowerCase());

            // filters table for consignor
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

        // Update table
        drawTable(filteredData, headers);
        prettifyTable();
    }



    // ================================================================================
    // ------------------------------ Helper methods ----------------------------------
    // ================================================================================

    //purpose: draws the table using original data and headers
    private void drawTable(Object[][] data, String[] headers) {
        table.setModel(new javax.swing.table.DefaultTableModel(data, headers) {
            //purpose: to be able to sort double and int values properly in the table
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 3 -> Double.class; // quantity
                    case 4 -> Double.class; // price
                    default -> String.class; // rest of the columns
                };
            }
        });
    }

    //purpose: adds table cell padding (5), center-aligns quantity, 2 decimal + $ to price
    private void prettifyTable() {
        int cellPadding = 5;
        // It does:
        // 1. adds cell padding to the table cell
        // 2. center-aligns integer values (quantity column)
        // 3. adds $ sign in front and displays 2 decimal places for double values (price column)

        // Quantity → integer style
        table.getColumnModel().getColumn(3).setCellRenderer(new TableFormatter.IntegerRenderer(cellPadding, cellPadding, cellPadding, cellPadding));

        // Price → $ with 2 decimals
        table.getColumnModel().getColumn(4).setCellRenderer(new TableFormatter.DollarDecimalRenderer(cellPadding,cellPadding,cellPadding,cellPadding));

        // Other columns → padded text
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 3 && i != 4) {
                table.getColumnModel().getColumn(i).setCellRenderer(new TableFormatter.PaddedCellRenderer(cellPadding,cellPadding,cellPadding,cellPadding));
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
        int roundRadius = 60;
        searchIDTextField = new Style.RoundedTextField(roundRadius);
        searchConsignorTextField = new Style.RoundedTextField(roundRadius);
        addItemButton = new Style.RoundedButton(roundRadius);
        deleteItemButton = new Style.RoundedButton(roundRadius);
    }

    public static void main(String[] args) {
        new InventoryPanel();
    }
}
