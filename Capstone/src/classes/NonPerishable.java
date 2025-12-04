package classes;

public class NonPerishable extends Item{
    public NonPerishable(String itemID, String name, Consignor owner, int quantity, double sellingPrice, String dateReceived) {
        super(itemID, name, owner, quantity, sellingPrice, dateReceived, 60);
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + "Non-Perishable";
    }
}