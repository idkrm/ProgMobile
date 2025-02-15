package iut.dam.powerhome;

import java.util.ArrayList;
import java.util.List;

public class Habitat {
    private int id;
    private String residentName;
    private int floor;
    private double area;
    private List<Appliance> appliances = new ArrayList<>();

    public Habitat(int id, String name, int floor, double area, List<Appliance> a){
        this.id = id;
        this.residentName = name;
        this.floor = floor;
        this.area = area;
        this.appliances.addAll(a);
    }

    public int getId() {
        return id;
    }

    public String getResidentName() {
        return residentName;
    }

    public int getFloor() {
        return floor;
    }

    public double getArea() {
        return area;
    }

    public List<Appliance> getAppliances() {
        return appliances;
    }
}
