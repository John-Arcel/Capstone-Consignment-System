package classes;

public class NonPerishable extends Item{
    public NonPerishable(String itemID, String name, Consignor owner, int quantity, double sellingPrice, double commissionRate, String dateReceived, String status) {
        super(itemID, name, owner, quantity, sellingPrice, commissionRate, dateReceived, 60, status);
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + "Non-Perishable";
    }
}