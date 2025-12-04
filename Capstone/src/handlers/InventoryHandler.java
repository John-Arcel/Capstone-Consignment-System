package handlers;

import classes.*;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class InventoryHandler {
    private String path;
    private List<Item> inventory_list;
    private SupplierHandler supplierHandler;

    public InventoryHandler(String entityID, SupplierHandler supplierHandler){
        path = "data/" + entityID + "/inventory.csv";
        inventory_list = new ArrayList<>();
        this.supplierHandler = supplierHandler;

        loadInventory();
    }

    private void loadInventory(){
        inventory_list.clear();
        File file = new File(path);

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                String[] data = line.split(",");

                String ownerID = data[2];
                Consignor owner = supplierHandler.getConsignorByID(ownerID);

                Item item;
                if(data[7].equals("Perishable")){
                    item = new Perishable(
                            data[0],
                            data[1],
                            owner,
                            Integer.parseInt(data[3]),
                            Double.parseDouble(data[4]),
                            data[5],
                            (int) ChronoUnit.DAYS.between(LocalDate.parse(data[5]), LocalDate.parse(data[6]))
                    );
                }
                else{
                    item = new NonPerishable(
                            data[0],
                            data[1],
                            owner,
                            Integer.parseInt(data[3]),
                            Double.parseDouble(data[4]),
                            data[5]
                    );
                }
                inventory_list.add(item);
            }
        } catch (IOException e) {
            System.out.println("This is an error");
        }
    }

    public void saveInventory(){
        File file = new File(path);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            for(Item i : inventory_list){
                bw.write(i.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("This is an error");
        }
    }

    public Object[][] getAllItems(){
        Object[][] matrix = new Object[inventory_list.size()][8];
        for(int i = 0; i<inventory_list.size(); i++){
            Item item = inventory_list.get(i);
            matrix[i][0] = item.getName();
            matrix[i][1] = item.getItemID();
            matrix[i][2] = item.getOwner().getName();
            matrix[i][3] = item.getQuantity();
            matrix[i][4] = item.getSellingPrice();
            matrix[i][5] = item.getDateReceived().toString();
            matrix[i][6] = item.getReturnDate().toString();
            matrix[i][7] = (item instanceof Perishable) ? "Perishable" : "Non-Perishable";
        }

        return matrix;
    }
}
