package classes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String transactionId;
    private String itemName;
    private Item soldItem;
    private String consignorName;
    private LocalDateTime saleDate;
    private double totalAmount;
    private double storeRevenue;
    private double consignorShare;
    private boolean isPaid;

    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // for adding transactions when selling
    public Transaction(String transactionID, Item soldItem, LocalDateTime saleDate, boolean isPaid){
        this.transactionId = transactionID;
        this.soldItem = soldItem;
        itemName = soldItem.getName();
        consignorName = soldItem.getOwner().getName();
        this.saleDate = saleDate;
        this.isPaid = isPaid;

        this.totalAmount = soldItem.getSellingPrice(); //add getSellingPrice() sa Item
        this.storeRevenue = soldItem.getSellingPrice() - soldItem.calculateConsignorShare();
        this.consignorShare = soldItem.calculateConsignorShare();
    }

    // for adding transactions from csv file
    public Transaction(String id, String itemName, String consignorName, LocalDateTime date, double total, double revenue, double share, boolean paid, Item itemRef) {
        this.transactionId = id;
        this.itemName = itemName;
        this.consignorName = consignorName;
        this.saleDate = date;
        this.totalAmount = total;
        this.storeRevenue = revenue;
        this.consignorShare = share;
        this.isPaid = paid;
        this.soldItem = itemRef; // Can be null if item was deleted
    }

    public String getTransactionId() {
        return transactionId;
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    public LocalDateTime getSaleDate() {
        return saleDate;
    }
    public String getItemName() {
        return itemName;
    }
    public String getConsignorName(){
        return consignorName;
    }
    public Item getSoldItem() {
        return soldItem;
    }
    public double getConsignorShare() {
        return consignorShare;
    }
    public double getStoreRevenue() {
        return storeRevenue;
    }
    public boolean isPaid(){
        return isPaid;
    }

    public void balanceTransferred(){
        isPaid = true;
    }

    public String toCSV() {
        // Save ItemID if item exists, else "null"
        String itemID = (soldItem != null) ? soldItem.getItemID() : "DELETED";

        return String.join(",",
                transactionId,
                itemID,
                itemName, // Save Name
                consignorName,
                saleDate.format(format),
                String.valueOf(isPaid),
                String.format("%.2f", totalAmount),    // Save Money
                String.format("%.2f", storeRevenue),   // Save Money
                String.format("%.2f", consignorShare)  // Save Money
        );
    }
}
