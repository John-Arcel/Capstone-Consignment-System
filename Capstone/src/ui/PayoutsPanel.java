package ui;

import handlers.*;
import classes.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



public class PayoutsPanel extends JPanel {

    private JPanel content;
    private JTextField Search;
    private JTable HistoryTable;
    private JTable PendingTable;
    private JScrollPane ScrollPending;
    private JScrollPane ScrollHistory;
    private JLabel PayoutNum;
    private JLabel PendingNum;
    private JButton transferButton;

    private List<Object[]> historyDataList = new ArrayList<>(); // list of objects na makita sa history
    private List<Object[]> pendingDataList = new ArrayList<>(); // list of objects na makita sa pending
    private TransactionsHandler transactionsHandler;
    private PayoutsHandler payoutsHandler;
    private final String[] HistoryHeaders = {"Payout ID", "Consignor", "Amount Paid", "Date"};

    public static int payoutIdCtr;
    int countPending = 0; // for the total pending display
    int countPayout = 0; // for the total payout displayed

    public PayoutsPanel(TransactionsHandler t, PayoutsHandler p) {
        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);

        this.transactionsHandler = t;
        this.payoutsHandler = p;
        processConsigneeTransactions(transactionsHandler.getAllTransactions(), payoutsHandler.getAllPayouts());

        Object[][] HistoryData = historyDataList.toArray(new Object[0][]);
        HistoryTable.setModel(new javax.swing.table.DefaultTableModel(HistoryData, HistoryHeaders));
        HistoryTable.setRowHeight(30);

        String[] pendingHeaders = {"Select", "Transaction ID", "Total Amount", "Consignor Share"};
        PendingTableModel pendingModel = new PendingTableModel(this.pendingDataList, pendingHeaders);
        PendingTable.setModel(pendingModel);
        HistoryTable.setRowHeight(30);

        // Initial count of total payout and pending
        if (countPayout <= 0 || countPending <= 0) {
            PayoutNum.setText("" + 0);
            PendingNum.setText("" + 0);
        }
        PayoutNum.setText("" + countPayout);
        PendingNum.setText("" + countPending);


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

    private void SelectFilter(String text) {
        historyDataList.clear();

        String input = text.trim().toLowerCase();

        if (input.isEmpty()) {
            return;
        } else {

            Object[][] arr = payoutsHandler.getAllPayouts();

            for(int i = 0; i < arr.length; i++){
                String comp = (String)arr[i][0];
                if(comp.contains(input)){
                    historyDataList.add(new Object[]{
                            arr[i][0],
                            arr[i][1],
                            arr[i][2],
                            arr[i][3]
                    });
                }
            }

            updateHistoryTable();
        }

    }

    // -------------------------------Process Transaction Function -------------------------------
    // This function goes through the consignee's list of transactions and goes through everything, if it's a transaction(pending) or is already a payout
    // stores the payouts in history while transactions are put in the pending

    private void processConsigneeTransactions(Object[][] allTransactions, Object[][] allPayouts) {

        for(Object[] payoutRow : allPayouts){
            historyDataList.add(payoutRow);
            countPayout++;
        }

        for(Object[] transactionRow : allTransactions){
            pendingDataList.add(new Object[]{
                    Boolean.FALSE,
                    transactionRow[0],
                    transactionRow[3],
                    transactionRow[5]
            });
            countPending++;
        }
    }

    // -------------------------------Handle Transfer-------------------------------
    // this function updates the history table according to the transfers made from the pending table

    private void handleTransferAction(PendingTableModel model) {
        List<Object[]> selectedItems = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Boolean) model.getValueAt(i, 0)) {
                selectedItems.add(model.getRowData(i));
            }
        }

        if(selectedItems.isEmpty()){
            JOptionPane.showMessageDialog(this, "No transactions selected for payout");
            return;
        }

        for (Object[] item : selectedItems) {
            String transactionID = (String) item[1];
            double consignorShare = (double) item[1];
            String consignorName = "N/A";

            Payout newPayout = toPayout(transactionID, consignorShare, consignorName);

            countPayout++;

            historyDataList.add(new Object[]{
                    newPayout.getPayoutId(),
                    newPayout.getConsignor(),
                    newPayout.getAmountPaid(),
                    newPayout.getPayoutDate()
            });

            payoutsHandler.addPayoutAndSave(newPayout);


            for(int i = 0; i < pendingDataList.size(); i++){
                if(pendingDataList.get(i)[1].equals(transactionID)){
                    pendingDataList.remove(i);
                    countPending--;
                    break;
                }
            }
        }


        model.fireTableDataChanged();
        updateHistoryTable();
        JOptionPane.showMessageDialog(this, "Successfully initiated payout for " + selectedItems.size() + " transactions");
    }

//    // -------------------------------Add To History-------------------------------
//    // this function add to the history data List that show up at the table
//    public void addHistory(Object transactionId) {
//        for (PayoutsPanel.Transaction a : Check) {
//            if (a.getTransactionID().equals(transactionId)) {
//                PayoutsPanel.Payout newPayout = toPayout(a);
//                countPayout++;
//                AllPayout.add(newPayout);
//                historyDataList.add(new Object[]{
//                        newPayout.getPayoutId(),
//                        newPayout.getConsignor(),
//                        newPayout.getAmountPaid(),
//                        newPayout.getPayoutDate()});
//                break;
//            }
//        }
//
//    }

    // -------------------------------Update History Table-------------------------------
    // if a new payout is added to the history or a specific payout is searched, this function updates what is shown in the table
    public void updateHistoryTable() {

        //updated total payout and total pending
        if (countPayout <= 0 || countPending <= 0) {
            PayoutNum.setText("" + 0);
            PendingNum.setText("" + 0);
        }
        PayoutNum.setText("" + countPayout);
        PendingNum.setText("" + countPending);

        Object[][] HistoryData = historyDataList.toArray(new Object[0][0]);
        HistoryTable.setModel(new javax.swing.table.DefaultTableModel(HistoryData, HistoryHeaders));
        HistoryTable.setRowHeight(30);
    }


    // -------------------------------Pending Table Creator-------------------------------
    // Custom create the Pending table

    public static class PendingTableModel extends AbstractTableModel {
        private List<Object[]> data;
        private String[] columnNames;

        public PendingTableModel(List<Object[]> pendingData, String[] headers) {
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
            if (columnIndex == 0) {
                data.get(rowIndex)[columnIndex] = aValue;
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }

        public Object[] getRowData(int i) {
            return data.get(i);
        }
    }


    // -------------------------------Transaction to Payout-------------------------------
    // Accepts a transaction and makes it into a Payout
    public Payout toPayout(String transactionId, double amount, String consignorName) {
        String consignorID = "T-" + String.format("%07d", ++payoutIdCtr);

        Consignor tempConsignor = new Consignor(consignorName, consignorID);

        return new Payout(tempConsignor, amount, LocalDate.now(), transactionId);
    }


    // -------------------------------Elements Radius editor-------------------------------
    //
    private void createUIComponents() {
        Search = new Style.RoundedTextField(40);
        transferButton = new Style.RoundedButton(40);
    }




//    public static void main(String[] args) {
//
//    }
}