//package com.example.project3;
import java.util.LinkedList;

public class Player {
    private String ID;
    private int reserveTroops;
    private LinkedList<Country> countries;

    public Player(String ID, int reserveTroops) {
        this.ID = ID;
        this.reserveTroops = reserveTroops;
        countries = new LinkedList<>();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getReserveTroops() {
        return reserveTroops;
    }

    public void setReserveTroops(int reserveTroops) {
        this.reserveTroops = reserveTroops;
    }

    public int getNumCountries() {
        return countries.size();
    }

    public Country getCountry(int index) {
        if(index >= countries.size()) {
            throw new IllegalArgumentException();
        }
        return countries.get(index);
    }

    public void addCountry(Country c) {
        countries.add(c);
    }

    public Country removeCountry(int index) {
        return countries.remove(index);
    }

    public boolean removeCountry(Country c) {
        return countries.remove(c);
    }

    public boolean controls(Country c) {
        return countries.contains(c);
    }
}
