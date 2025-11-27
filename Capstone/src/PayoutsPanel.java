import javax.swing.*;
import java.awt.*;

public class PayoutsPanel extends JFrame{

    private JPanel content;
    private JTextField textField1;
    private JTable HistoryTable;
    private JTextPane TotalPayout;
    private JTextPane PendingPayout;
    private JTable table1;

    public PayoutsPanel(){
        //setSize(800,600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(content);

        String[] HistoryHeaders= {"Payout ID" , "Consignor" , "Paid" , "Date"};
        Object[][] HistoryData = {
                {"P0000001","Sally's shop",10,"11/02/25"},
                {"P0000002","GAP",20,"11/03/25"},
                {"P0000003","Bench",30,"11/04/25"},
        };

        String[] PendingHeaders = {"Payout ID" , "Consignor" , "Paid" , "Date"};
        Object[][] PendingData = {
                {"P0000001","Sally's shop",10,"11/02/25"},
                {"P0000002","GAP",20,"11/03/25"},
                {"P0000003","Bench",30,"11/04/25"},
        };


        HistoryTable.setModel(new javax.swing.table.DefaultTableModel(HistoryData, HistoryHeaders));
        HistoryTable.setRowHeight(30);
        HistoryTable.setBounds(0,0,100,200);

        setLocationRelativeTo(null);
        setVisible(true);

    }

    public static void main(String[] args) {
        new PayoutsPanel();
    }

}
