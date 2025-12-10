package handlers;

import classes.Consignor;
import classes.Item;
import classes.Transaction;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

public class TransactionsHandler {
    private String path;
    private List<Transaction> transaction_list;
    private InventoryHandler inventoryHandler;
    private SupplierHandler supplierHandler;

    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TransactionsHandler(String entityID, InventoryHandler inventoryHandler, SupplierHandler supplierHandler){
        this.inventoryHandler = inventoryHandler;
        this.supplierHandler = supplierHandler;
        path = "Capstone/data/" + entityID + "/transactions.csv";
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

                String transID = data[0];
                String itemID = data[1];
                String itemName = data[2]; // Loaded from file
                String consignorName = data[3];
                String dateStr = data[4];
                boolean isPaid = Boolean.parseBoolean(data[5]);
                double total = Double.parseDouble(data[6]);
                double revenue = Double.parseDouble(data[7]);
                double share = Double.parseDouble(data[8]);

                // Try to find the item object, but don't crash if null
                Item item = inventoryHandler.getItemFromID(itemID);

                Transaction t = new Transaction(
                        transID, itemName, consignorName,
                        LocalDateTime.parse(dateStr, format),
                        total, revenue, share, isPaid, item
                );

                transaction_list.add(t);
            }
        }
        catch(IOException e) {
            System.out.println("Error: during reading transaction from CSV file");
        }
    }

    // writes from transaction array to csv file
    public void saveTransactions() {
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

    public List<Transaction> getTransactionList(){
        return transaction_list;
    }

    // converts array of transaction objects to matrix of raw data and returns matrix
    public Object[][] getAllTransactions() {
        int headerAmount = 6;
        Object[][] matrix = new Object[transaction_list.size()][headerAmount];
        for(int i = 0; i < transaction_list.size(); i++) {
            Transaction t = transaction_list.get(i);
            matrix[i][0] = t.getTransactionId();
            matrix[i][1] = t.getItemName();
            matrix[i][2] = t.getSaleDate().format(format);
            matrix[i][3] = t.getTotalAmount();
            matrix[i][4] = t.getStoreRevenue();
            matrix[i][5] = t.getConsignorShare();
        }

        return matrix;
    }

    protected Transaction getTransactionFromID(String transID){
        for(Transaction t : transaction_list){
            if(t.getTransactionId().equals(transID)) return t;
        }
        return null;
    }

    public void processSale(String itemID){
        Item item = inventoryHandler.getItemFromID(itemID);

        int newID = 0;
        for(Transaction t : transaction_list){
            int currentID = Integer.parseInt(t.getTransactionId().split("-")[1]);
            if(currentID > newID){
                newID = currentID;
            }
        }
        newID++;

        Transaction transaction = new Transaction("T-" + String.format("%07d", newID), item, LocalDateTime.now(), false);
        transaction_list.add(transaction);

        item.setQuantity(item.getQuantity()-1);
        if(stockLeft(item) == 0) item.setStatus(Item.State.SOLD);

        double share = item.calculateConsignorShare();
        Consignor owner = supplierHandler.getConsignorByID(item.getOwner().getID());
        owner.updateBalance(share);
    }

    public void markAsPaid(Transaction pending){
        for(Transaction t : transaction_list){
            if(t.equals(pending)){
                t.balanceTransferred();
                return;
            }
        }
    }

    private int stockLeft(Item i) {
        return i.getQuantity();
    }
}
