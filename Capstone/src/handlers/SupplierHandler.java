package handlers;

import classes.Consignor;

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

    }

    public Consignor getConsignorByID(String ownerID) {
    }
}
