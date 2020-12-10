package com.example.risk;

import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.widget.AppCompatButton;

import java.io.Serializable;
import java.util.ArrayList;

public class Country implements Serializable {

    private int playerNum;
    private int armiesHeld;
    private int armyValue;
    private int cID;



    private int[] neighborCIDs;

    public Country(){
         playerNum = 0;
         armiesHeld = 0;
         armyValue = 0;
         cID = 0;
         neighborCIDs = new int[]{};

    }

    public Country(int player, int armies,int cID, int [] neighbors){

         playerNum = player;
         armiesHeld = armies;
         armyValue = neighbors.length + 1;
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

    public int[] getNeighborCIDs() {
        return neighborCIDs;
    }

    public void setNeighborCIDs(int[] neighborCIDs) {
        this.neighborCIDs = neighborCIDs;
    }

    public void addArmy(){
        armiesHeld +=1;
    }

    public void removeArmy(){
        armiesHeld -=1;
    }

}
