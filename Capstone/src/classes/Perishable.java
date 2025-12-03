package classes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Perishable extends Item{
    private final String expiryDate;
    public Perishable(String name, double sellingPrice, String dateReceived, Consignee owner, String expiryDate){
        super(name, sellingPrice, dateReceived, owner);
        this.expiryDate = expiryDate;
    }

    public boolean isExpired(String date){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yy");

        LocalDate expiry = LocalDate.parse(expiryDate, format);
        LocalDate input = LocalDate.parse(date, format);

        return !input.isBefore(expiry);
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}