import javax.swing.*;
import java.util.Vector;

public class InventoryPanel extends JFrame {
    private JPanel contentPane;
    private JTable table;
    private JLabel inventoryAmountLabel;

    public InventoryPanel() {
        setTitle("Inventory");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(contentPane);

        String[] headers = {"Name", "ID", "Owner", "Selling Price", "Receive Date", "Expiry Date"};
        Object[][] data = {
                {"name1", "id1", "owner1", "P100.00", "10/10/10", "10/11/10"},
                {"name2", "id2", "owner2", "P100.00", "10/12/10", "10/13/10"},
                {"name3", "id3", "owner3", "P100.00", "10/13/10", "10/14/10"},
        };

        table.setModel(new javax.swing.table.DefaultTableModel(data, headers));
        table.setRowHeight(30);
        table.setBounds(0,0,100,200);

        String inventoryAmountText = "Items in inventory: ";
        int itemAmount = getInventoryAmount(data);
        inventoryAmountLabel.setText(inventoryAmountText + itemAmount);

        pack();
        setVisible(true);
    }

    private int getInventoryAmount(Object[][] data) {
        return data.length;
    }

    public static void main(String[] args) {
        new InventoryPanel();
    }
}
