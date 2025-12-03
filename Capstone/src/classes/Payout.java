package classes;

import java.time.LocalDate;


public class Payout {
    private final String payoutId;
    private Consignor consignor;
    private double amountPaid;
    private LocalDate payoutDate;
    private static int ctr;

    public Payout(Consignor consignor, double amountPaid, LocalDate payoutDate) {
        this.payoutId = "T-" + String.format("%07d", ++ctr);
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

    public void generateReceipt() {
        System.out.println("PAYOUT RECEIPT");
        System.out.println("classes.Payout ID: " + payoutId);
        System.out.println("classes.Consignor: " + consignor.getName());
        System.out.println("AmountPaid: ₱" + String.format("%.2f", amountPaid));
        System.out.println("classes.Payout Date: " + payoutDate);
    }
}

