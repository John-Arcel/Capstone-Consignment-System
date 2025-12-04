package handlers;

import classes.Consignor;
import classes.Item;
import classes.NonPerishable;
import classes.Perishable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SupplierHandler {
    private String path;
    private List<Consignor> suppliers_list;

    public SupplierHandler(String entityID){
        path = "data/" + entityID + "/consignors.csv";
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
                String contactNumber = data[2];
                String location = data[3];
                double amount = Double.parseDouble(data[4]);

                Consignor Supplier = new Consignor(name,contactNumber,location,ownerID);
                Supplier.setPayableBalance(amount);
                suppliers_list.add(Supplier);

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

    public Consignor addConsignor(String name) {
        Consignor newConsignor = new Consignor(name, )
    }
}
