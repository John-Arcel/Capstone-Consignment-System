package handlers;

import classes.Item;
import classes.Transaction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionsHandler {
    private String path;
    private List<Transaction> transaction_list;
    private InventoryHandler inventoryHandler;

    public TransactionsHandler(String entityID, InventoryHandler inventoryHandler){
        this.inventoryHandler = inventoryHandler;
        path = "data/" + entityID + "/transactions.csv";
        transaction_list = new ArrayList<>();

        loadTransactions();
    }

    // reads from csv file to array of transaction objects
    private void loadTransactions() {
        transaction_list.clear();
        File file = new File(path);

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String transactionID = data[0];
                String itemID = data[1];


                Item item = inventoryHandler.getItemFromID(itemID);
                Transaction transaction = new Transaction(transactionID, item);

                transaction_list.add(transaction);
            }
        }
        catch(IOException e) {
            System.out.println("Error: during reading transaction from CSV file");
        }
    }

    // writes from transaction array to csv file
    private void saveTransactions() {
        File file = new File(path);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for(Transaction t : transaction_list) {
                bw.write(t.toCSV());
                bw.newLine();
            }
        }
        catch(IOException e) {
            System.out.println("Error: during saving of transaction");
        }
    }

    // converts array of transaction objects to matrix of raw data and returns matrix
    public Object[][] getAllTransactions() {
        int headerAmount = 6;
        Object[][] matrix = new Object[transaction_list.size()][headerAmount];
        for(int i = 0; i < transaction_list.size(); i++) {
            Transaction t = transaction_list.get(i);
            matrix[i][0] = t.getTransactionId();
            matrix[i][1] = t.getSoldItem().getName();
            matrix[i][2] = t.getSaleDate().toString();
            matrix[i][3] = t.getTotalAmount();
            matrix[i][4] = t.getStoreRevenue();
            matrix[i][5] = t.getConsignorShare();
        }

        return matrix;
    }
}
