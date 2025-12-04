package classes;

import java.util.ArrayList;
import java.util.List;

public class Consignee extends Entity {

    private final String password;
    private List<Consignor> consignorList;
    private List<Item> itemList;

    public Consignee(String name, String password, String entityID) {
        super(name, entityID);

        if (password == null || password.isBlank())
            throw new NullPointerException("Password cannot be null or empty.");

        this.password = password;

        this.consignorList = new ArrayList<>();
        this.itemList = new ArrayList<>();
    }

    public String getPassword() {
        return password;
    }

    public void addConsignor(Consignor consignor) {
        if (consignor == null)
            throw new NullPointerException("Consignor cannot be null.");
        consignorList.add(consignor);
    }

    public void removeConsignor(Consignor consignor) {
        consignorList.remove(consignor);
    }

    public void addItem(Item item) {
        if (item == null)
            throw new NullPointerException("Item cannot be null.");
        itemList.add(item);
    }

    public void removeItem(Item item) {
        itemList.remove(item);
    }

    public List<Consignor> getConsignorList() {
        return consignorList;
    }

    public List<Item> getItemList() {
        return itemList;
    }
}
