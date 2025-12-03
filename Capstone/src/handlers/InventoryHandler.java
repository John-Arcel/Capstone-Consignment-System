package handlers;

import classes.Item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InventoryHandler {
    private String path;
    private List<Item> inventory_list;

    public InventoryHandler(String entityID){
        path = "data/" + entityID + "/inventory.csv";
        inventory_list = new ArrayList<>();

        loadInventory();
    }

    private void loadInventory(){
        inventory_list.clear();
        File file = new File(path);

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            String[] data;
            while((line = br.readLine()) != null){
                data = line.split(",");

                // TO-DO: make data[2] a consignor
//                Item item = new Item(
//                        data[0],
//                        data[1],
//                        data[2],
//                );
            }
        } catch (IOException e) {
            System.out.println("This is an error");
        }
    }
}
