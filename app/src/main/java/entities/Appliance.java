package entities;

import java.util.ArrayList;
import java.util.List;

import entities.Booking;

public class Appliance {
    private int id;
    private String name;
    private String reference;
    private int wattage;
    public List<Booking> bookings;

    public Appliance() {
        bookings = new ArrayList<>();
    }

    public Appliance(int id, String name, String reference, int wattage) {
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.wattage = wattage;
        bookings = new ArrayList<>();
    }

    public Appliance(String name, String reference, int wattage) {
        this.name = name;
        this.reference = reference;
        this.wattage = wattage;
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
