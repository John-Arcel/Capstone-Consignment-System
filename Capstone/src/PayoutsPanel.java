import javax.swing.*;

public class PayoutsPanel extends JPanel{

    PayoutsPanel(){
        this.setSize(800,600);
        this.setLayout(null);

        String[] columnNames = {"Payout ID" , "Consignor" , "Paid" , "Date"};
        Object[][] data = {{"P0000001", "P0000002", "P0000003"},{"Sally's shop", "GAP" , "Bench"},{10, 20 ,30} ,{"11/02/25", "11/03/25", "11/04/25"}};

        JTable table = new JTable(data, columnNames);
        table.setBounds(50,50,700,200);
        this.add(table);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new PayoutsPanel();
    }

}
