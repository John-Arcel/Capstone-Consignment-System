package ui;

import handlers.TransactionsHandler;
import handlers.PayoutsHandler;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;



public class PayoutsPanel extends JPanel{

    private JPanel content;
    private JTextField Search;
    private JTable HistoryTable;
    private JTable PendingTable;
    private JScrollPane ScrollPending;
    private JScrollPane ScrollHistory;
    private JLabel PayoutNum;
    private JLabel PendingNum;
    private JButton transferButton;

    private List<Transaction> Check = new ArrayList<>(); // list that stores all transactions that are not initially payouts
    private List<Payout> AllPayout = new ArrayList<>(); // list of payouts
    private List<Object[]> historyDataList = new ArrayList<>(); // list of objects na makita sa history
    private List<Object[]> pendingDataList = new ArrayList<>(); // list of objects na makita sa pending
    private Consignee consignee; // the admin
    private final String[] HistoryHeaders= {"Payout ID" , "Consignor" , "Amount Paid" , "Date"};
    private final String[] PendingHeaders = {"Select" , "Transaction ID" , "Total Amount" , "Consignor Share"};

    int countPending = 0; // for the total pending display
    int countPayout = 0; // for the total payout displayed

    public PayoutsPanel(TransactionsHandler t, PayoutsHandler p){
        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);

        // Todo this is a temporary consignee
        // Todo implement file handling :D
        //this.consignee = new Consignee();

//        setSize(800,700);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setContentPane(content);

        processConsigneeTransactions();

        Object[][] HistoryData = historyDataList.toArray(new Object[0][]);
        HistoryTable.setModel(new javax.swing.table.DefaultTableModel(HistoryData, HistoryHeaders));
        HistoryTable.setRowHeight(30);
        //HistoryTable.setBounds(0,0,100,150);

        PendingTableModel pendingModel = new PendingTableModel(this.pendingDataList, PendingHeaders);
        PendingTable.setModel(pendingModel);
        HistoryTable.setRowHeight(30);
        //HistoryTable.setBounds(0,0,100,150);

        // Initial count of total payout and pending
        if(countPayout <= 0 || countPending <= 0){
            PayoutNum.setText("" + 0);
            PendingNum.setText(""+ 0);
        }
        PayoutNum.setText("" + countPayout);
        PendingNum.setText(""+ countPending);

//        setLocationRelativeTo(null);
//        setVisible(true);
        transferButton.requestFocusInWindow();

        // if transfer button is selected then it will add the amount of pending payout to payout list
        transferButton.addActionListener(new ActionListener() {
            @Override

            // once transfer is clicked, tanan selected na pending will be transferred to history
            // then calls to update history table as pending table will update on its own
            public void actionPerformed(ActionEvent e) {
                handleTransferAction(pendingModel);
                updateHistoryTable();
            }
        });

        Search.setText("Search Payout ID");
        Search.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (Search.getText().equals("Search Payout ID")) {
                    Search.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (Search.getText().trim().isEmpty()) {
                    Search.setText("Search Payout ID");
                }
            }
        });

        Search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCombinedSearch(); // This will call SelectFilter(Search.getText())
            }
        });

        //Search.addKeyListener(new java.awt.event.KeyAdapter() {
//            static boolean first = false;
//
//            // Giset nako ang field to display search payout, if this is invoked it will clear that para makatype
//            // if na invoke nakaisa then it will not clear again para dili sya mo print "" after every other key
//            public void keyPressed(java.awt.event.KeyEvent e) {
//                if(first == false){
//                    Search.setText("");
//                    first = true;
//                }else{
//                    return;
//                }
//            }
//
//            // gets the text once nakaenter naka after typing
//            @Override
//            public void keyTyped(java.awt.event.KeyEvent e) {
//                SelectFilter(Search.getText());
//            }
//
//        });
    }


    //FUNCTIONS

    // -------------------------------Search Functions-------------------------------
    // this code accepts the string from text field and filters that specific item and updates table accordingly
    private void runCombinedSearch() {
        String idText = Search.getText().trim();

        boolean noID = idText.isEmpty() || idText.equals("Search Payout ID");

//         If empty → reset dataTable
        if (noID) {
            return;
        }

        SelectFilter(idText);
    }
    private void SelectFilter(String text){
        historyDataList.clear();

        String input = text.trim().toLowerCase();

        if(input.isEmpty()){
            AllPayout.forEach(p -> {
                    historyDataList.add(new Object[]{
                            p.getPayoutID(),
                            p.getConsignor(),
                            p.getAmountPaid(),
                            p.getPayoutDate()});

            });
        }else{
            AllPayout.forEach(p -> {

                String ID = null;
                if (p.getPayoutID() != null) {
                    ID = p.getPayoutID().toLowerCase();
                }
                if (ID.contains(input)) {
                    historyDataList.add(new Object[]{
                            p.getPayoutID(),
                            p.getConsignor(),
                            p.getAmountPaid(),
                            p.getPayoutDate()});
                }
            });
        }

        updateHistoryTable();
    }

    // -------------------------------Process Transaction Function -------------------------------
    // This function goes through the consignee's list of transactions and goes through everything, if it's a transaction(pending) or is already a payout
    // stores the payouts in history while transactions are put in the pending

    private void processConsigneeTransactions() {
//        BufferedReader t = new BufferedReader(new FileReader("transactions.csv"));
//        BufferedReader p = new BufferedReader(new FileReader("payouts.csv"));

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

    // -------------------------------Handle Transfer-------------------------------
    // this function updates the history table according to the transfers made from the pending table

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

    // -------------------------------Add To History-------------------------------
    // this function add to the history data List that show up at the table
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

    // -------------------------------Update History Table-------------------------------
    // if a new payout is added to the history or a specific payout is searched, this function updates what is shown in the table
    public void updateHistoryTable(){

        //updated total payout and total pending
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


    // -------------------------------Pending Table Creator-------------------------------
    // Custom create the Pending table

    private static class PendingTableModel extends AbstractTableModel {
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


    // -------------------------------Transaction to Payout Transformer-------------------------------
    // Accepts a transaction and makes it into a Payout
    public Payout toPayout(Transaction t){
        Payout p = new Payout();
        return p;
    }



    // -------------------------------Elements Radius editor-------------------------------
    //
    private void createUIComponents() {
        Search = new Style.RoundedTextField(40);
        transferButton = new Style.RoundedButton(40);
    }








    // DUMMY PSVM FOR MOCK RUNNING

//    public static void main(String[] args) {
//        // Dummy data for testing the structure
//        Consignee dummyConsignee = createDummyConsignee();
//
//        SwingUtilities.invokeLater(() -> new PayoutsPanel(dummyConsignee));
//    }
//
//    // Dummy classes and data for compilation/testing
//    // These must exist in your project for the code to compile
//    public static class Transaction {
//        String getTransactionID() { return "TXN-1"; }
//        double getTotalAmount() { return 100.00; }
//        double getConsignorShare() { return 70.00; }
//    }
//    public static class Payout extends Transaction {
//        //public Payout(Consignor c, )
//        String payID;
//        //String consignor = "Jane Doe";
//        //double amountPaid = 500.00;
//        //String payDate = "2025-11-28";
//        static int ctr = 0;
//        public Payout(){
//            this.payID = "P0-00" + ctr;
//            ctr++;
//        }
//        String getPayoutID() {
//
//            return payID; }
//        String getConsignor() { return "Jane Doe"; }
//        double getAmountPaid() { return 500.00; }
//        String getPayoutDate() { return "2025-11-28"; }
//    }
//    public static class Consignee {
//        List<Transaction> transactions;
//    }
//    public static Consignee createDummyConsignee() {
//        Consignee c = new Consignee();
//        c.transactions = new ArrayList<>();
//        c.transactions.add(new Payout());
//        c.transactions.add(new Payout());
//        c.transactions.add(new Transaction());
//        c.transactions.add(new Transaction());
//        return c;
//    }
//
//
//}
