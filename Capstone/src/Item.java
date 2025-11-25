public abstract class Item {
    private static int ctr = 0;
    private final String itemID;
    private String name;
    private double sellingPrice;
    private final String dateReceived;
    private final Consignee owner;


    public Item(String name, double sellingPrice, String dateReceived, Consignee owner){
        //super if extended sa entity
        this.name = name;
        this.sellingPrice = sellingPrice;
        this.dateReceived = dateReceived;
        this.owner = owner;

        ctr++;
        this.itemID = String.format("I-%07d", ctr); // sets it so that between the string ang the ctr naay 0 na mo buffer

    }

    public Consignee getOwner(){
        return owner;
    }

    public String getItemID(){
        return itemID;
    }

    public String getName() {
        return name;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setSellingPrice(double sellingPrice){
        this.sellingPrice = sellingPrice;
    }

    public double calculateOwnerShare(){
        return sellingPrice * 0.75; // supplier/owner gets 75% of the sale
    }

    public void returnItem(Item i){
        owner.removeItem(i); // calls remove item from the consignee
    }
}