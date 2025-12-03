package classes;

import java.time.LocalDate;

public abstract class Item {
    private final String itemID;
    private String name;
    private final Consignor owner;
    private int quantity;
    private double sellingPrice;
    private double commissionRate;
    private final LocalDate dateReceived;
    private final LocalDate returnDate;

    public Item(String itemID, String name, Consignor owner, int quantity, double sellingPrice, double commissionRate, String dateReceived, int daysToSell){
        //super if extended sa entity
        this.itemID = itemID;
        this.name = name;
        this.owner = owner;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
        this.commissionRate = commissionRate;
        this.dateReceived = LocalDate.parse(dateReceived);
        returnDate = this.dateReceived.plusDays(daysToSell);
    }

    public Consignor getOwner(){
        return owner;
    }
    public String getItemID(){
        return itemID;
    }
    public String getName() {
        return name;
    }
    public double getSellingPrice() {
        return sellingPrice;
    }
    public LocalDate getDateReceived() {
        return dateReceived;
    }
    public LocalDate getReturnDate(){ return returnDate; }

    public double calculateOwnerShare(){
        return sellingPrice * commissionRate; // supplier/owner gets 75% of the sale
    }

    public String toCSV(){
        return name + "," + itemID + "," + owner.getName() + "," + quantity + "," + String.format("%.2f", sellingPrice) + "," + dateReceived + "," + returnDate;
    }
}