package classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;

public abstract class Item {
    private final String itemID;
    private final String name;
    private final Consignor owner;
    private int quantity;
    private final double sellingPrice;
    private double commissionRate;
    private final LocalDate dateReceived;
    private final LocalDate returnDate;
    private State status;

    public enum State {AVAILABLE, SOLD, EXPIRED}

    public Item(String itemID, String name, Consignor owner, int quantity, double sellingPrice, double commissionRate, String dateReceived, int daysToSell, String status){
        //super if extended sa entity
        this.itemID = itemID;
        this.name = name;
        this.owner = owner;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
        this.commissionRate = commissionRate;
        this.dateReceived = LocalDate.parse(dateReceived);
        returnDate = this.dateReceived.plusDays(daysToSell);
        this.status = State.valueOf(status);
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
    public int getQuantity(){
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }
    public double getCommissionRate() {
        return commissionRate;
    }
    public String getStatus() {
        return String.valueOf(status);
    }
    public LocalDate getDateReceived() {
        return dateReceived;
    }
    public LocalDate getReturnDate(){ return returnDate; }

    public double calculateConsignorShare(){
        return sellingPrice * (1 - commissionRate); // supplier/owner gets the other percent of the sale
    }
    public void setStatus(State s){
        status = s;
    }

    public String toCSV(){
        return name + "," + itemID + "," + owner.getName() + "," + quantity + "," + String.format("%.2f", sellingPrice) + "," + dateReceived + "," + returnDate + "," + status;
    }
}