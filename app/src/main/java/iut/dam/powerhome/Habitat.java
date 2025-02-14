package iut.dam.powerhome;

import java.util.ArrayList;
import java.util.List;

public class Habitat {
    int id;
    String residentName;
    int floor;
    double area;
    List<Appliance> appliances = new ArrayList<>();

    public Habitat(int id, String name, int floor, double area, List<Appliance> a){
        this.id = id;
        this.residentName = name;
        this.floor = floor;
        this.area = area;
        this.appliances.addAll(a);
    }


}
