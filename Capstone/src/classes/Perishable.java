package classes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Perishable extends Item{
    public Perishable(String itemID, String name, Consignor owner, int quantity, double sellingPrice, double commissionRate, String dateReceived, int daysToSell) {
        super(itemID, name, owner, quantity, sellingPrice, commissionRate, dateReceived, daysToSell);
    }

    public boolean isExpired(){
        LocalDate expiry = super.getReturnDate();
        LocalDate input = super.getDateReceived();

        return !input.isBefore(expiry);
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + "Perishable";
    }
}