package classes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String transactionId;
    private Item soldItem;
    private LocalDateTime saleDate;
    private double totalAmount;
    private double storeRevenue;
    private double consignorShare;
    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//    public Transaction(Item soldItem){
//        this.transactionId = "T-" + String.format("%07d", ++counter);
//        this.soldItem = soldItem;
//        this.saleDate = LocalDateTime.now();
//        this.totalAmount = soldItem.getSellingPrice(); //add getSellingPrice() sa classes.Item
//        this.storeRevenue = soldItem.getSellingPrice() - soldItem.calculateOwnerShare();
//        this.consignorShare = soldItem.calculateOwnerShare();
//    }
    public Transaction(String transactionID, Item soldItem){
        this.transactionId = transactionID;
        this.soldItem = soldItem;
        this.saleDate = LocalDateTime.now();
        this.totalAmount = soldItem.getSellingPrice(); //add getSellingPrice() sa classes.Item
        this.storeRevenue = soldItem.getSellingPrice() - soldItem.calculateOwnerShare();
        this.consignorShare = soldItem.calculateOwnerShare();

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

    public Item getSoldItem() {
        return soldItem;
    }

    public double getConsignorShare() {
        return consignorShare;
    }

    public double getStoreRevenue() {
        return storeRevenue;
    }

    public void generateReceipt(){
        System.out.println("classes.Transaction No.: " + transactionId);
        System.out.println("classes.Item: " + soldItem.getName());
        System.out.println("Date: " + saleDate.format(format));
        System.out.println("Total Amount: " + String.format("%.2f", totalAmount));
        System.out.println("Store Revenue: " + String.format("%.2f", storeRevenue));
        System.out.println("classes.Consignor Share: " + String.format("%.2f", consignorShare));
    }

    public String toCSV() {
        return transactionId + "," + soldItem.getItemID() + "," + saleDate + "," + totalAmount + "," + storeRevenue + "," + consignorShare;
    }
}
