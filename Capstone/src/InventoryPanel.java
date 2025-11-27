import javax.swing.*;

public class InventoryPanel extends JPanel {
    private JPanel contentPane;
    private JTable table;
    private JLabel inventoryAmountLabel;
    private JTextField searchTextField;

    private int itemAmount = 0;

    public InventoryPanel() {
        String[] headers = {"Name", "ID", "Owner", "Selling Price", "Receive Date", "Expiry Date"};
        Object[][] data = {
                {"Apple", "A001", "John", 100.00, "01/01/2025", "01/01/2026"},
                {"Banana", "B002", "Mary", 50.00, "02/01/2025", "02/01/2026"},
                {"Carrot", "C003", "Alice", 30.00, "03/01/2025", "03/01/2026"},
                {"Dates", "D004", "Bob", 200.00, "04/01/2025", "04/01/2026"},
                {"Eggplant", "E005", "Eve", 80.00, "05/01/2025", "05/01/2026"},
                {"Fig", "F006", "John", 150.00, "06/01/2025", "06/01/2026"},
                {"Grapes", "G007", "Mary", 120.00, "07/01/2025", "07/01/2026"},
                {"Honeydew", "H008", "Alice", 180.00, "08/01/2025", "08/01/2026"},
                {"Iceberg Lettuce", "I009", "Bob", 60.00, "09/01/2025", "09/01/2026"},
                {"Jackfruit", "J010", "Eve", 300.00, "10/01/2025", "10/01/2026"}
        };

        table.setModel(new javax.swing.table.DefaultTableModel(data, headers));
        table.setRowHeight(30);
        table.setBounds(0,0,100,200);

        String inventoryAmountText = "Items in inventory: ";
        int itemAmount = getInventoryAmount(data);
        inventoryAmountLabel.setText(inventoryAmountText + itemAmount);

        // put a "Search" text in the search text field when it's not in focus
        searchTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchTextField.getText().equals("Search")) {
                    searchTextField.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchTextField.getText().isEmpty()) {
                    searchTextField.setText("Search");
                }
            }
        });

        afterInit();
    }

    // focus the window first upon setup, helper for searchTextField "Search" text
    public void afterInit() {
        SwingUtilities.invokeLater(() -> contentPane.requestFocusInWindow());
    }

    private int getInventoryAmount(Object[][] data) {
        return data.length;
    }

    public static void main(String[] args) {
        new InventoryPanel();
    }
}