package handlers;

import classes.*;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PayoutsHandler {
    private String path;
    private List<Payout> payout_list;

    public PayoutsHandler(String entityID, TransactionsHandler transactionsHandler){
        path = "data/" + entityID + "/payouts.csv";
        payout_list = new ArrayList<>();

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
                String consignor = data[1];
                String amountPaid = data[2];
                String date = data[3];


//                Item item;
//                if(itemType.equals("Perishable")){
//                    item = new Perishable(
//                            itemID,
//                            itemName,
//                            owner,
//                            Integer.parseInt(quantity),
//                            Double.parseDouble(sellingPrice),
//                            dateReceived,
//                            (int) ChronoUnit.DAYS.between(LocalDate.parse(dateReceived), LocalDate.parse(dateReturn))
//                    );
//                }
//                else{
//                    item = new NonPerishable(
//                            itemID,
//                            itemName,
//                            owner,
//                            Integer.parseInt(quantity),
//                            Double.parseDouble(sellingPrice),
//                            dateReceived
//                    );
//                }
//                inventory_list.add(item);
            }
        } catch (IOException e) {
            System.out.println("This is an error");
        }
    }

    // writes from item array to csv file
    public void saveInventory(){
        File file = new File(path);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
//            for(Item i : inventory_list){
//                bw.write(i.toCSV());
//                bw.newLine();
//            }
        } catch (IOException e) {
            System.out.println("This is an error");
        }
    }

    // converts array of Item object to a primitive matrix with its raw data and returns
//    public Object[][] getAllItems(){
//        Object[][] matrix = new Object[inventory_list.size()][8];
//        for(int i = 0; i<inventory_list.size(); i++){
//            Item item = inventory_list.get(i);
//            matrix[i][0] = item.getName();
//            matrix[i][1] = item.getItemID();
//            matrix[i][2] = item.getOwner().getName();
//            matrix[i][3] = item.getQuantity();
//            matrix[i][4] = item.getSellingPrice();
//            matrix[i][5] = item.getDateReceived().toString();
//            matrix[i][6] = item.getReturnDate().toString();
//            matrix[i][7] = (item instanceof Perishable) ? "Perishable" : "Non-Perishable";
//        }
//
//        return matrix;
//    }

}
