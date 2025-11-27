import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.text.DecimalFormat;

// adds $ sign and displays 2 decimal places to doubles (price column) in table
class DollarDecimalRenderer extends DefaultTableCellRenderer {
    private DecimalFormat formatter;
    private int top, left, bottom, right;

    public DollarDecimalRenderer(int top, int left, int bottom, int right) {
        this.formatter = new DecimalFormat("$#.00"); // $ and 2 decimals
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        setHorizontalAlignment(RIGHT); // align numbers to the right
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (c instanceof JLabel) {
            ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
            if (value instanceof Number) {
                setText(formatter.format(value));
            }
        }
        return c;
    }
}

// center aligns integers (quantity column) in table
class IntegerRenderer extends DefaultTableCellRenderer {
    private int top, left, bottom, right;

    public IntegerRenderer(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        setHorizontalAlignment(CENTER);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (c instanceof JLabel) {
            ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
            if (value instanceof Number) {
                setText(String.valueOf(((Number) value).intValue()));
            }
        }
        return c;
    }
}

// adds cell padding to table cells
class PaddedCellRenderer extends DefaultTableCellRenderer {
    private int top, left, bottom, right;

    public PaddedCellRenderer(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        setOpaque(true); // important so background is visible
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (c instanceof JLabel) {
            ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        }
        return c;
    }
}

public class InventoryPanel extends JPanel {
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

    private String[] headers;
    private Object[][] data;

    private int totalItems;
    private int totalConsignors;

    public InventoryPanel() {
//        setContentPane(contentPane);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);

        headers = new String[]{"Item Name", "Item ID", "Consignor", "Quantity", "Price", "Date Received", "Return Date"};
        data = new Object[][]{
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

        table.setModel(new javax.swing.table.DefaultTableModel(data, headers) {
            // this override is to be able to sort double and int values properly in the table
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 3 -> Double.class; // quantity
                    case 4 -> Double.class; // price
                    default -> String.class; // rest of the columns
                };
            }
        });

        table.setRowHeight(30);
        table.setBounds(0,0,100,200);

        int tableCellPadding = 5;
        prettifyTable(tableCellPadding); // adds table cell padding, center-aligns quantity, 2 decimal + $ to price

        totalItems = getTotalItems();
        totalConsignors = getTotalConsignors();
        totalItemsLabel.setText(Integer.toString(totalItems));
        totalConsignorsLabel.setText(Integer.toString(totalConsignors));

        itemIDSearchFieldPlaceholderText();
        consignorSearchFieldPlaceholderText();

//        pack();
//        setVisible(true);
        afterInit();
    }

    // focus the window first upon setup, helper for searchIDTextField "Search" text
    public void afterInit() {
        SwingUtilities.invokeLater(() -> contentPane.requestFocusInWindow());
    }

    // add placeholder text to item id search field
    private void itemIDSearchFieldPlaceholderText() {
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
    }

    // add placeholder text to consignor search field
    private void consignorSearchFieldPlaceholderText() {
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

    private void prettifyTable(int tableCellPadding) {
        // It does:
        // 1. adds cell padding to the table cell
        // 2. center-aligns integer values (quantity)
        // 3. adds $ sign in front and displays 2 decimal places for double values (price)

        // Quantity → integer style
        table.getColumnModel().getColumn(3).setCellRenderer(new IntegerRenderer(tableCellPadding, tableCellPadding, tableCellPadding, tableCellPadding));

        // Price → $ with 2 decimals
        table.getColumnModel().getColumn(4).setCellRenderer(new DollarDecimalRenderer(tableCellPadding, tableCellPadding, tableCellPadding, tableCellPadding));

        // Other columns → padded text
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 3 && i != 4) {
                table.getColumnModel().getColumn(i).setCellRenderer(new PaddedCellRenderer(tableCellPadding, tableCellPadding, tableCellPadding, tableCellPadding));
            }
        }

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

//    public static void main(String[] args) {
//        new InventoryPanel();
//    }
}