import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String transactionId;
    private Item soldItem;
    private LocalDateTime saleDate;
    private double totalAmount;
    private double storeRevenue;
    private double consignorShare;
    private static int counter;
    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(Item soldItem){
        this.transactionId = "T-" + String.format("%07d", ++counter);
        this.soldItem = soldItem;
        this.saleDate = LocalDateTime.now();
        this.totalAmount = soldItem.getSellingPrice(); //add getSellingPrice() sa Item
        this.storeRevenue = soldItem.getSellingPrice() - soldItem.calculateOwnerShare();
        this.consignorShare = soldItem.calculateOwnerShare();

    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getConsignorShare() {
        return consignorShare;
    }

    public double getStoreRevenue() {
        return storeRevenue;
    }

    public void generateReceipt(){
        System.out.println("Transaction No.: " + transactionId);
        System.out.println("Item: " + soldItem.getName());
        System.out.println("Date: " + saleDate.format(format));
        System.out.println("Total Amount: " + String.format("%.2f", totalAmount));
        System.out.println("Store Revenue: " + String.format("%.2f", storeRevenue));
        System.out.println("Consignor Share: " + String.format("%.2f", consignorShare));
    }



}
