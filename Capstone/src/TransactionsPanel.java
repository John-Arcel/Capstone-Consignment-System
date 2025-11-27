import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TransactionsPanel extends JPanel{
    private JPanel contentPane;
    private JTextField transactionTextField;
    private JTable table1;
    private JTextField transactionHistoryTextField;

    public TransactionsPanel(){

        String[][] data = {
                {"T-0000001", "Apple", "January 6", "$1.57", "$0.65"},
                {"T-0000002", "Banana", "January 7", "$1.37", "$0.78"},
                {"T-0000003", "Mango", "January 8", "$2.57", "$1.67"},
                {"T-0000004", "Pineapple", "January 9", "$11.57", "$5.46"},
                {"T-0000005", "Pineapple", "January 9", "$11.57", "$5.46"}
        };
        String[] columnNames = {"Transaction ID", "Item", "Date", "Revenue", "Share"};


        table1.setModel(new DefaultTableModel(data, columnNames));

    }

    public static void main(String[] args) {
        new TransactionsPanel();
    }




}
