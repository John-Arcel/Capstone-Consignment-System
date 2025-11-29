import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.concurrent.CountDownLatch;

public class TransactionsPanel extends JPanel{
    private JPanel contentPane;
    private JTable table;
    private JScrollPane scroll;
    private JTextField searchTextField;
    private JLabel totalTransactionsLabel;
    private JLabel totalStoreRevenueLabel;
    private JLabel totalConsignorShareLabel;
    private Object[][] data;

    public TransactionsPanel(){
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setContentPane(contentPane);

        String[] columnNames = {"Transaction ID", "Items", "Date", "Total", "Store Revenue", "Consignor Share"};
        data = new Object[][] {
                {"T-0000001", "Apple", "2025-01-06", 1.57, 0.39, 1.18},
                {"T-0000002", "Banana", "2025-01-06", 0.89, 0.22, 0.67},
                {"T-0000003", "Grapes (lb)", "2025-01-07", 4.99, 1.25, 3.74},
                {"T-0000004", "Milk (gallon)", "2025-01-07", 3.25, 0.81, 2.44},
                {"T-0000005", "Eggs (dozen)", "2025-01-08", 2.78, 0.70, 2.08},
                {"T-0000006", "Bread", "2025-01-08", 2.19, 0.55, 1.64},
                {"T-0000007", "Cheese Block", "2025-01-09", 5.50, 1.38, 4.12},
                {"T-0000008", "Chicken Breast (lb)", "2025-01-09", 8.10, 2.02, 6.08},
                {"T-0000009", "Tomato", "2025-01-10", 0.75, 0.19, 0.56},
                {"T-0000010", "Potato Bag", "2025-01-10", 3.50, 0.88, 2.62},
                {"T-0000011", "Orange Juice", "2025-01-11", 4.15, 1.04, 3.11},
                {"T-0000012", "Cereal Box", "2025-01-11", 3.88, 0.97, 2.91},
                {"T-0000013", "Salmon Fillet", "2025-01-12", 12.99, 3.25, 9.74},
                {"T-0000014", "Spinach Bag", "2025-01-12", 1.99, 0.50, 1.49},
                {"T-0000015", "Ground Coffee", "2025-01-13", 7.45, 1.86, 5.59}
        };

        table.setModel(new DefaultTableModel(data, columnNames));

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
        prettifyTable();

        totalTransactionsLabel.setText(Integer.toString(getTotalTransactions()));
        totalStoreRevenueLabel.setText("$" + Double.toString(getTotalStoreRevenue()));
        totalConsignorShareLabel.setText("$" + Double.toString(getTotalConsignorShare()));
//        pack();
//        setVisible(true);

        afterInit();

    }



    public void afterInit() {
        SwingUtilities.invokeLater(() -> contentPane.requestFocusInWindow());
    }

    public double getTotalAmount(){
        double total = 0;

        for(int i=0; i<data.length;i++){
            total = total + (double)data[i][3];
        }
        return total;
    }

    private void prettifyTable() {
        // It does:
        // 1. adds cell padding to the table cell
        // 2. adds $ sign in front and displays 2 decimal places for double values (total, storeRevenue, consignorShare)

        // Quantity → integer style
        table.getColumnModel().getColumn(3).setCellRenderer(new DollarDecimalRenderer(5, 5, 5, 5));

        // Price → $ with 2 decimals
        table.getColumnModel().getColumn(4).setCellRenderer(new DollarDecimalRenderer(5, 5, 5, 5));

        table.getColumnModel().getColumn(5).setCellRenderer(new DollarDecimalRenderer(5, 5, 5, 5));

        // Other columns → padded text
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 3 && i != 4 && i != 5) {
                table.getColumnModel().getColumn(i).setCellRenderer(new PaddedCellRenderer(5, 5, 5, 5));
            }
        }

    }

    private int getTotalTransactions(){
        return data.length;
    }

    private double getTotalStoreRevenue(){
        double total = 0;

        for(int i=0; i<data.length;i++){
            total = total + (double)data[i][4];
        }

        return total;
    }

    private double getTotalConsignorShare(){
        double total = 0;

        for(int i=0; i<data.length;i++){
            total = total + (double)data[i][5];
        }

        return total;
    }

//    public static void main(String[] args) {
//        new TransactionsPanel();
//    }

}
