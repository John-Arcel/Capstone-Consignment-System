package classes;

import java.time.LocalDate;


public class Payout {
    private final String payoutId;
    private Consignor consignor;
    private double amountPaid;
    private LocalDate payoutDate;

    public Payout(Consignor consignor, double amountPaid, LocalDate payoutDate, String payoutId) {
        this.payoutId = payoutId;
        this.consignor = consignor;
        this.amountPaid = amountPaid;
        this.payoutDate = payoutDate;
    }

    public String getPayoutId() {
        return payoutId;
    }

    public Consignor getConsignor() {
        return consignor;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public LocalDate getPayoutDate() {
        return payoutDate;
    }

    public String toCSV(){
        return payoutId + "," + consignor.getName() + "," + consignor.getID() + "," + String.format("%.2f", amountPaid) + "," + payoutDate;
    }
}

