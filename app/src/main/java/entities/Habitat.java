package entities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

    public static Habitat getFromJson(String json){
        Gson gson = new Gson();
        Habitat obj = gson.fromJson(json, Habitat.class);
        return obj;
    }

    public static List<Habitat> getListFromJson(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Habitat>>() {
        }.getType();
        List<Habitat> list = gson.fromJson(json, type);
        return list;
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
