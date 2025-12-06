package handlers;

import classes.Consignor;
import classes.Item;
import classes.NonPerishable;
import classes.Perishable;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SupplierHandler {
    private final String path;
    private List<Consignor> suppliers_list;

    public SupplierHandler(String entityID){
        path = "Capstone/data/" + entityID + "/consignors.csv";
        suppliers_list = new ArrayList<>();

        loadSuppliers();
    }

    private void loadSuppliers() {
        suppliers_list.clear();
        File file = new File(path);

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                String[] data = line.split(",");

                String ownerID = data[0];
                String name = data[1];
                double amount = Double.parseDouble(data[2]);
                boolean active = Boolean.parseBoolean(data[3]);

                Consignor supplier = new Consignor(name,ownerID, active);
                supplier.setPayableBalance(amount);
                suppliers_list.add(supplier);

            }
        } catch (IOException e) {
            System.out.println("This is an error");
        }
    }

    // writes from item array to csv file
    public void saveSuppliers(){
        File file = new File(path);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            for(Consignor c : suppliers_list){
                bw.write(c.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("This is an error");
        }
    }

    public Consignor getConsignorByID(String ownerID) {
        for(Consignor s : suppliers_list){
            if(s.getID().equals(ownerID))
                return s;
        }
        return null;
    }

    public Consignor getConsignorByName(String name) {
        for(Consignor s : suppliers_list){
            if(s.getName().equals(name))
                return s;
        }
        return null;
    }

    public int getActiveConsignors(){
        int counter = 0;
        for(Consignor c : suppliers_list){
            if (c.IsActive()){
                counter++;
            }
        }

        return counter;
    }

    public List<Consignor> getSupplierList() {
        return suppliers_list;
    }

    public Consignor addConsignor(String name) {
        int newID = 0;
        for(Consignor c : suppliers_list){
            int currentID = Integer.parseInt(c.getID().split("-")[1]);
            if(currentID > newID){
                newID = currentID;
            }
        }
        newID++;

        Consignor newConsignor = new Consignor(name, "S-" + String.format("%07d", newID), true);

        suppliers_list.add(newConsignor);
        return newConsignor;
    }

    public void inactiveConsignor(Consignor c){
        suppliers_list.get(suppliers_list.indexOf(c)).changeActive();
    }
}
