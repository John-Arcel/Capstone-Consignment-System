abstract class Entity {
    private static int counter = 1;

    private final String name;
    private String contactNumber;
    private final String entityID;

    public  Entity(String name, String contactNumber){
        this.name = name;
        this.contactNumber = contactNumber;
        this.entityID = String.format("%07d", counter++);
    }

    public String getName(){
        return  name;
    }

    public String getContactNumber(){
        return  contactNumber;
    }

    public String getID(){
        return entityID;
    }

    public void setContactNumber(String contactNumber){
        this.contactNumber = contactNumber;
    }
}
