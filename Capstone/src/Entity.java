abstract class Entity {


    private final String name;
    private String contactNumber;
    private final String entityID;

    public  Entity(String name, String contactNumber, String entityID){
        this.name = name;
        this.contactNumber = contactNumber;
        this.entityID = entityID;
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
