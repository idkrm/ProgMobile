package iut.dam.powerhome;

public class Appliance {
    private int id;
    private String name;
    private String reference;
    private int wattage;

    public Appliance(int id, String name, String reference, int wat){
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.wattage = wat;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getReference() {
        return reference;
    }

    public int getWattage() {
        return wattage;
    }
}
