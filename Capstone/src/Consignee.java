import java.util.ArrayList;
import java.util.List;

public class Consignee extends Entity {

    private final String username;
    private final String password;
    private List<Consignor> consignorList;
    private List<Item> itemList;

    public Consignee(String name, String contactNumber, String username, String password) {
        super(name, contactNumber);

        if (username == null || username.isBlank())
            throw new NullPointerException("Username cannot be null or empty.");

        if (password == null || password.isBlank())
            throw new NullPointerException("Password cannot be null or empty.");

        this.username = username;
        this.password = password;

        this.consignorList = new ArrayList<>();
        this.itemList = new ArrayList<>();
    }

    public String getUsername() {
        return username;
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
