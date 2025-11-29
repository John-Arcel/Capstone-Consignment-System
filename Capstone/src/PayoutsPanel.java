import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


public class PayoutsPanel extends JFrame{

    private JPanel content;
    private JTextField Search;
    private JTable HistoryTable;
    private JTable PendingTable;
    private JScrollPane ScrollPending;
    private JScrollPane ScrollHistory;
    private JLabel PayoutNum;
    private JLabel PendingNum;
    private JButton transferButton;

    private List<Transaction> Check = new ArrayList<>();
    private List<Payout> AllPayout = new ArrayList<>();
    private List<Object[]> historyDataList = new ArrayList<>();
    private List<Object[]> pendingDataList = new ArrayList<>();
    private Consignee consignee;
    private final String[] HistoryHeaders= {"Payout ID" , "Consignor" , "Amount Paid" , "Date"};
    private final String[] PendingHeaders = {"Select" , "Transaction ID" , "Total Amount" , "Consignor Share"};

    int countPending = 0;
    int countPayout = 0;

    public PayoutsPanel(Consignee consignee){
        this.consignee = consignee;

        setSize(800,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(content);

        processConsigneeTransactions();

        Object[][] HistoryData = historyDataList.toArray(new Object[0][]);
        HistoryTable.setModel(new javax.swing.table.DefaultTableModel(HistoryData, HistoryHeaders));
        HistoryTable.setRowHeight(30);
        //HistoryTable.setBounds(0,0,100,150);

        PendingTableModel pendingModel = new PendingTableModel(this.pendingDataList, PendingHeaders);
        PendingTable.setModel(pendingModel);
        HistoryTable.setRowHeight(30);
        //HistoryTable.setBounds(0,0,100,150);

        if(countPayout <= 0 || countPending <= 0){
            PayoutNum.setText("" + 0);
            PendingNum.setText(""+ 0);
        }
        PayoutNum.setText("" + countPayout);
        PendingNum.setText(""+ countPending);

        setLocationRelativeTo(null);
        setVisible(true);

        // if transfer button is selected then it will add the amount of pending payout to payout list
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTransferAction(pendingModel);
                updateHistoryTable();
            }
        });
        Search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectFilter(Search.getText());
            }
        });
    }

    private void SelectFilter(String text){

        historyDataList.clear();

        String input = text.trim().toLowerCase();

        AllPayout.forEach(p -> {
            String ID = p.getTransactionID().toLowerCase();
            if (ID.equals(input)) {
                historyDataList.add(new Object[]{
                        p.getPayoutID(),
                        p.getConsignor(),
                        p.getAmountPaid(),
                        p.getPayoutDate()});
            }
        });


        updateHistoryTable();
    }

    private void processConsigneeTransactions(){
        if(consignee != null && consignee.transactions!= null){// getter medthod for consignee's list of transactions
            for(Transaction a : consignee.transactions){
                if (a instanceof Payout){ // if the transactions is already a payout
                    countPayout++;
                    AllPayout.add((Payout)a);
                    historyDataList.add(new Object[]{
                            ((Payout)a).getPayoutID() ,
                            ((Payout)a).getConsignor(),
                            ((Payout)a).getAmountPaid(),
                            ((Payout)a).getPayoutDate() });
                }else{ // if transaction is not a payout
                    countPending++;
                    Check.add(a);
                    pendingDataList.add(new Object[]
                            { Boolean.FALSE,//the first part of the table row contains checkboxes,
                             a.getTransactionID(),
                             a.getTotalAmount(),
                             a.getConsignorShare() });
                    }
                }
        }
    }

    private void handleTransferAction(PendingTableModel model){
        List<Object[]> selectedItems = new ArrayList<>();
        for(int i = 0; i < model.getRowCount(); i++){
            if((Boolean) model.getValueAt(i, 0)){
                selectedItems.add(model.getRowData(i));
            }
        }

        for(Object[] item : selectedItems){
            Object transactionID = item[1];
            for(int j = 0; j < pendingDataList.size(); j++){
                if(pendingDataList.get(j)[1].equals(transactionID)){
                    pendingDataList.remove(item);
                    countPending--;
                    break;
                }
            }

            addHistory(transactionID);
        }

        model.fireTableDataChanged();
        JOptionPane.showMessageDialog(this, "Initiating payout for " + selectedItems.size() + " transactions");
    }

    public void addHistory(Object transactionId){
        for(PayoutsPanel.Transaction a : Check){
            if(a.getTransactionID().equals(transactionId)){
                PayoutsPanel.Payout newPayout = toPayout(a);
                countPayout++;
                AllPayout.add(newPayout);
                historyDataList.add(new Object[]{
                        newPayout.getPayoutID() ,
                        newPayout.getConsignor(),
                        newPayout.getAmountPaid(),
                        newPayout.getPayoutDate() });
                break;
            }
        }

    }

    public void updateHistoryTable(){
        if(countPayout <= 0 || countPending <= 0){
            PayoutNum.setText("" + 0);
            PendingNum.setText(""+ 0);
        }
        PayoutNum.setText("" + countPayout);
        PendingNum.setText(""+ countPending);

        Object[][] HistoryData = historyDataList.toArray(new Object[0][0]);
        HistoryTable.setModel(new javax.swing.table.DefaultTableModel(HistoryData, HistoryHeaders));
        HistoryTable.setRowHeight(30);
    }

    private class PendingTableModel extends AbstractTableModel {
        private List<Object[]> data;
        private String[] columnNames;

        public PendingTableModel(List<Object[]> pendingData, String[] headers){
            this.data = pendingData;
            this.columnNames = headers;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == 0) ? Boolean.class : super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data.get(rowIndex)[columnIndex];
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if(columnIndex == 0){
                data.get(rowIndex)[columnIndex] = aValue;
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }

        public Object[] getRowData(int i) {
            return data.get(i);
        }
    }

    public Payout toPayout(Transaction t){
        Payout p = new Payout();
        return p;}

    public static void main(String[] args) {
        // Dummy data for testing the structure
        Consignee dummyConsignee = createDummyConsignee();

        SwingUtilities.invokeLater(() -> new PayoutsPanel(dummyConsignee));
    }

    // Dummy classes and data for compilation/testing
    // These must exist in your project for the code to compile
    public static class Transaction {
        String getTransactionID() { return "TXN-1"; }
        double getTotalAmount() { return 100.00; }
        double getConsignorShare() { return 70.00; }
    }
    public static class Payout extends Transaction {
        //public Payout(Consignor c, )
        //String payID = "PO-001";
        //String consignor = "Jane Doe";
        //double amountPaid = 500.00;
        //String payDate = "2025-11-28";
        static int ctr = 0;
        public Payout(){

        }
        String getPayoutID() {
            String id = "P0-00" + ctr;
            ctr++;
            return id; }
        String getConsignor() { return "Jane Doe"; }
        double getAmountPaid() { return 500.00; }
        String getPayoutDate() { return "2025-11-28"; }
    }
    public static class Consignee {
        List<Transaction> transactions;
    }
    public static Consignee createDummyConsignee() {
        Consignee c = new Consignee();
        c.transactions = new ArrayList<>();
        c.transactions.add(new Payout());
        c.transactions.add(new Transaction());
        c.transactions.add(new Transaction());
        return c;
    }


}
