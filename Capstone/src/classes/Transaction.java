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

    public Transaction(String transactionID, Item soldItem, LocalDateTime saleDate){
        this.transactionId = transactionID;
        this.soldItem = soldItem;
        this.saleDate = saleDate;
        this.totalAmount = soldItem.getSellingPrice(); //add getSellingPrice() sa Item
        this.storeRevenue = soldItem.getSellingPrice() - soldItem.calculateConsignorShare();
        this.consignorShare = soldItem.calculateConsignorShare();
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


    public String toCSV() {
        return transactionId + "," + soldItem.getItemID() + "," + saleDate.format(format);
    }
}
