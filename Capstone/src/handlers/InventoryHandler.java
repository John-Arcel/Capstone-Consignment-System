package handlers;

import classes.*;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class InventoryHandler {
    private final String path;
    private List<Item> inventory_list;
    private SupplierHandler supplierHandler;
    private double commissionRate;

    public InventoryHandler(String entityID, SupplierHandler supplierHandler, double commissionRate){
        path = "Capstone/data/" + entityID + "/inventory.csv";
        inventory_list = new ArrayList<>();
        this.supplierHandler = supplierHandler;
        this.commissionRate = commissionRate;

        loadInventory();
    }

    // reads from csv file to array of items
    private void loadInventory(){
        inventory_list.clear();
        File file = new File(path);

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                String[] data = line.split(",");

                String itemName= data[0];
                String itemID = data[1];
                String ownerName = data[2];
                Consignor owner = supplierHandler.getConsignorByName(ownerName);
                String quantity = data[3];
                String sellingPrice = data[4];
                String dateReceived = data[5];
                String dateReturn = data[6];
                String status = data[7];
                String itemType = data[8];

                Item item;
                if(itemType.equals("Perishable")){
                    item = new Perishable(
                            itemID,
                            itemName,
                            owner,
                            Integer.parseInt(quantity),
                            Double.parseDouble(sellingPrice),
                            commissionRate,
                            dateReceived,
                            (int) ChronoUnit.DAYS.between(LocalDate.parse(dateReceived), LocalDate.parse(dateReturn)),
                            status
                    );
                }
                else{
                    item = new NonPerishable(
                            itemID,
                            itemName,
                            owner,
                            Integer.parseInt(quantity),
                            Double.parseDouble(sellingPrice),
                            commissionRate,
                            dateReceived,
                            status
                    );
                }
                inventory_list.add(item);
            }
        } catch (IOException e) {
            System.out.println("Error: Loading inventory");
        }
    }

    // writes from item array to csv file
    public void saveInventory(){
        File file = new File(path);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            for(Item i : inventory_list){
                bw.write(i.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: Saving inventory");
        }
    }

    // converts array of Item object to a primitive matrix with its raw data and returns
    public Object[][] getAllItems(){
        int size = getAvailableItems();

        Object[][] matrix = new Object[size][8];
        int ctr = 0;
        for(int i = 0; i<inventory_list.size(); i++){
            Item item = inventory_list.get(i);
            if(item.getStatus().equals("AVAILABLE")){
                matrix[ctr][0] = item.getName();
                matrix[ctr][1] = item.getItemID();
                matrix[ctr][2] = item.getOwner().getName();
                matrix[ctr][3] = item.getQuantity();
                matrix[ctr][4] = item.getSellingPrice();
                matrix[ctr][5] = item.getDateReceived().toString();
                matrix[ctr][6] = item.getReturnDate().toString();
                matrix[ctr][7] = (item instanceof Perishable) ? "Perishable" : "Non-Perishable";
                ctr++;
            }
        }

        return matrix;
    }

    public int getAvailableItems(){
        int counter = 0;
        for(Item i : inventory_list){
            if(i.getStatus().equals("AVAILABLE")){
                counter++;
            }
        }
        return counter;
    }

    public void addItem(String name, String owner, String quantity, String price, String dateReceived, String daysToSell, boolean isPerishable){
        Consignor consignor = supplierHandler.getConsignorByName(owner);
        if(consignor == null){
            consignor = supplierHandler.addConsignor(owner);
        }

        supplierHandler.changeConsignorStatus(consignor);

        int newID = 0;
        for(Item i : inventory_list){
            int currentID = Integer.parseInt(i.getItemID().split("-")[1]);
            if(currentID > newID){
                newID = currentID;
            }
        }
        newID++;

        Item item;
        if(isPerishable){
            item = new Perishable(
                    "I-" + String.format("%07d", newID),
                    name,
                    consignor,
                    Integer.parseInt(quantity),
                    Double.parseDouble(price),
                    commissionRate,
                    dateReceived,
                    Integer.parseInt(daysToSell),
                    "AVAILABLE"
            );
        }
        else{
            item = new NonPerishable(
                    "I-" + String.format("%07d", newID),
                    name,
                    consignor,
                    Integer.parseInt(quantity),
                    Double.parseDouble(price),
                    commissionRate,
                    dateReceived,
                    "AVAILABLE"
            );
        }
        inventory_list.add(item);
    }

    public void deleteItem(String itemID){
        boolean isPresent = false;

        Item item = getItemFromID(itemID);
        inventory_list.remove(item);

        for(Item i : inventory_list){
            if(i.getOwner().equals(item.getOwner())){
                isPresent = true;
                break;
            }
        }
        if(!isPresent){
            supplierHandler.changeConsignorStatus(item.getOwner());
        }
    }

    protected Item getItemFromID(String itemID) {
        for(Item i : inventory_list) {
            if(i.getItemID().equals(itemID)) return i;
        }
        return null;
    }

    public List<Item> getInventoryList(){
        return inventory_list;
    }
}
