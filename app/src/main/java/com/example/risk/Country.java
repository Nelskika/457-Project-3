package com.example.risk;

import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;

public class Country{

    private int playerNum;
    private int armiesHeld;
    private int armyValue;
    private int cID;



    private ArrayList<Integer> neighborCIDs;

    public Country(){
         playerNum = 0;
         armiesHeld = 0;
         armyValue = 0;
         cID = 0;
         neighborCIDs = new ArrayList<>();

    }

    public Country(int player, int armies, int value,int cID, ArrayList<Integer> neighbors){

         playerNum = player;
         armiesHeld = armies;
         armyValue = value;
         this.cID = cID;
        neighborCIDs =neighbors;
    }



    public int getPlayerNum() {
        return playerNum;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public int getArmiesHeld() {
        return armiesHeld;
    }

    public void setArmiesHeld(int armiesHeld) {
        this.armiesHeld = armiesHeld;
    }

    public int getArmyValue() {
        return armyValue;
    }

    public void setArmyValue(int armyValue) {
        this.armyValue = armyValue;
    }

    public int getcID() {
        return cID;
    }

    public void setcID(int cID) {
        this.cID = cID;
    }

    public ArrayList<Integer> getNeighborCIDs() {
        return neighborCIDs;
    }

    public void setNeighborCIDs(ArrayList<Integer> neighborCIDs) {
        this.neighborCIDs = neighborCIDs;
    }

}
