import java.util.ArrayList;
import java.util.List;

public class Consignor extends Entity {

    private double payableBalance;
    private boolean isActive;
    private String location;

    private final List<Transaction> transactionHistory;

    public Consignor(String name, String contactNumber, String location) {
        super(name, contactNumber);

        if (location == null || location.isBlank())
            throw new NullPointerException("Location cannot be null or empty.");

        this.location = location;
        this.payableBalance = 0.0;
        this.isActive = true;
        this.transactionHistory = new ArrayList<>();
    }

    // --- Getters ---

    public double getPayableBalance() {
        return payableBalance;
    }

    public boolean IsActive() {
        return isActive;
    }

    public String getLocation() {
        return location;
    }

    public List<Transaction> getSalesHistory() {
        return transactionHistory;
    }


    // Add or subtract from balance
    public void updateBalance(double amount) {
        this.payableBalance += amount;
    }

    // Toggle active/inactive
    public void changeActive() {
        this.isActive = !isActive;
    }

    // Change supplier location
    public void changeLocation(String newLocation) {
        if (newLocation == null || newLocation.isBlank())
            throw new NullPointerException("New location cannot be null or empty.");
        this.location = newLocation;
    }

    // Record a transaction
    public void addTransaction(Transaction t) {
        if (t == null)
            throw new NullPointerException("Transaction cannot be null.");
        transactionHistory.add(t);
    }
}
