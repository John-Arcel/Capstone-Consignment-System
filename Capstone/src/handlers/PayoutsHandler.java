package handlers;

import classes.*;
import ui.PayoutsPanel;

import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PayoutsHandler {
    private String path;
    private List<Payout> payout_list;
    private TransactionsHandler transactionsHandler;
    private SupplierHandler supplierHandler;

    public PayoutsHandler(String entityID, TransactionsHandler transactionsHandler, SupplierHandler supplierHandler){
        path = "Capstone/data/" + entityID + "/payouts.csv";
        payout_list = new ArrayList<>();
        this.transactionsHandler = transactionsHandler;
        this.supplierHandler = supplierHandler;

        loadPayouts();
    }

    private void loadPayouts(){
        payout_list.clear();
        File file = new File(path);

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                String[] data = line.split(",");

                String payoutID = data[0];
                String consignorName = data[1];
                String consignorID = data[2];
                String amountPaid = data[3];
                String dateString = data[4];

                // Convert date string to LocalDate ---
                LocalDate date = LocalDate.parse(dateString);

                // Create a Consignor object (assuming the handler uses the name as a key) ---
                Consignor consignor = new Consignor(consignorName, consignorID, true);

                Payout payout;
                payout = new Payout(
                        consignor,
                        Double.parseDouble(amountPaid),
                        date,
                        payoutID
                );

                payout_list.add(payout);
            }
        } catch (IOException e) {
            System.out.println("This is an error");
        }
    }

    // writes from item array to csv file
    public void savePayouts(){
        File file = new File(path);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            for(Payout p : payout_list){
                bw.write(p.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("This is an error");
        }
    }

    // converts array of Item object to a primitive matrix with its raw data and returns
    public Object[][] getAllPayouts(){
        int headerAmount = 4;
        Object[][] matrix = new Object[payout_list.size()][headerAmount];
        for(int i = 0; i<payout_list.size(); i++){
            Payout payout = payout_list.get(i);
            matrix[i][0] = payout.getPayoutId();
            matrix[i][1] = payout.getConsignor().getName();
            matrix[i][2] = payout.getAmountPaid();
            matrix[i][3] = payout.getPayoutDate();
        }

        return matrix;
    }

    // Adds to payout list when transactions are turned to payouts
    public double processPayout(String transactionID){
        Transaction t = transactionsHandler.getTransactionFromID(transactionID);

        // mark transaction as paid
        transactionsHandler.markAsPaid(t);

        // adding to the payout list
        int newID = 0;
        for(Payout p : payout_list){
            int currentID = Integer.parseInt(p.getPayoutId().split("-")[1]);
            if(currentID > newID){
                newID = currentID;
            }
        }
        newID++;
        Consignor owner = supplierHandler.getConsignorByName(t.getConsignorName());
        Payout p = new Payout(
                owner,
                t.getConsignorShare(),
                LocalDate.now(),
                "P-" + String.format("%07d", newID)
        );
        this.payout_list.add(p);

        // updating supplier's balance
        owner.updateBalance(-t.getConsignorShare());

        // returns the money transferred
        return t.getConsignorShare();
    }
}
