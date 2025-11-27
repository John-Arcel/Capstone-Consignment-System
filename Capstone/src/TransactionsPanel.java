import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TransactionsPanel extends JPanel{
    private JPanel contentPane;
    private JTable table1;
    private JScrollPane scroll;
    private JTextField searchTextField;

    public TransactionsPanel(){

        String[][] data = {
                {"T-0000001", "Apple", "2025-01-06", "$1.57", "$0.65"},
                {"T-0000002", "Banana", "2025-01-07", "$1.37", "$0.78"},
                {"T-0000003", "Mango", "2025-01-08", "$20.57", "$1.67"},
                {"T-0000004", "Pineapple", "2025-01-09", "$11.57", "$5.46"},
                {"T-0000005", "Kiwi", "2025-01-10", "$11.57", "$5.46"},
                {"T-0000006", "Star apple", "2025-01-06", "$11.89", "$5.65"},
                {"T-0000007", "Tomato", "2025-01-07", "$12.34", "$0.78"},
                {"T-0000008", "Strawberry", "2025-01-08", "$6.56", "$1.67"},
                {"T-0000009", "Blueberry", "2025-01-09", "$112.00", "$51.46"},
                {"T-0000010", "Corn", "2025-01-10", "$11.57", "$5.46"},
                {"T-0000011", "Eggplant", "2025-01-06", "$111.57", "$67.60"},
                {"T-0000012", "Squash", "2025-01-07", "$221.37", "$0.78"},
                {"T-0000013", "Pumpkin", "2025-01-08", "$2.57", "$1.67"},
                {"T-0000014", "Coconut", "2025-01-09", "$141.57", "$75.00"},
                {"T-0000015", "Zebra", "2025-01-10", "$11.57", "$5.46"}
        };
        String[] columnNames = {"Transaction ID", "Item", "Date", "Revenue", "Share"};


        table1.setModel(new DefaultTableModel(data, columnNames));

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
                    searchTextField.setText("Enter");
                }
            }
        });

        afterInit();
    }

    public static void main(String[] args) {
        new TransactionsPanel();
    }

    public void afterInit() {
        SwingUtilities.invokeLater(() -> contentPane.requestFocusInWindow());
    }



}
