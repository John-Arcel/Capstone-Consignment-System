package classes;

public abstract class Entity {

    private final String name;

    private final String entityID;

    public  Entity(String name, String entityID){
        this.name = name;
        this.entityID = entityID;
    }

    public String getName(){
        return name;
    }

    public String getID(){
        return entityID;
    }


}
