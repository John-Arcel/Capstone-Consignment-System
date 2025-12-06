package classes;

import java.util.ArrayList;
import java.util.List;

public class Consignor extends Entity {

    private double payableBalance;
    private boolean isActive;
    private final List<Transaction> transactionHistory;

    public Consignor(String name, String entityID, boolean isActive) {
        super(name,entityID);
        this.payableBalance = 0.0;
        this.isActive = isActive;
        this.transactionHistory = new ArrayList<>();
    }

    // --- Getters ---

    public double getPayableBalance() {
        return payableBalance;
    }

    public boolean IsActive() {
        return isActive;
    }

    public List<Transaction> getSalesHistory() {
        return transactionHistory;
    }

    public void setPayableBalance(double amount) {
        payableBalance = amount;
    }

    // Add or subtract from balance
    public void updateBalance(double amount) {
        this.payableBalance += amount;
    }

    // Toggle active/inactive
    public void changeActive() {
        isActive = false;
    }

    public String toCSV() {
        return getID() + "," + getName() + "," + payableBalance + "," + isActive;
    }
}
