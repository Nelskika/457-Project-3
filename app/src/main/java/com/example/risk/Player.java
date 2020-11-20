package com.example.risk;

import java.util.ArrayList;

public class Player {
    private int playerNum;
    private int numArmies;
    private int placeablearmies;
    private ArrayList <Country> heldCountries;

    public Player(){

    }

    public Player(int playerNum){
        this.playerNum = playerNum;
        this.numArmies = 0;
        this.placeablearmies =0;
        this.heldCountries = new ArrayList<Country>();
    }


    public int getPlayerNum() {
        return playerNum;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public int getNumArmies() {
        return numArmies;
    }

    public void setNumArmies(int numArmies) {
        this.numArmies = numArmies;
    }

    public int getPlaceablearmies() {
        return placeablearmies;
    }

    public void setPlaceablearmies(int placeablearmies) {
        this.placeablearmies = placeablearmies;
    }

    public ArrayList<Country> getHeldCountries() {
        return heldCountries;
    }

    public void setHeldCountries(ArrayList<Country> heldCountries) {
        this.heldCountries = heldCountries;
    }

    public void addCounty(Country newCountry){
        this.heldCountries.add(newCountry);
    }

    public void loseCountry(Country lostCountry){
        this.heldCountries.remove(lostCountry);
    }
}
