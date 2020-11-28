package com.example.risk;

import java.util.ArrayList;
import java.util.Arrays;

public class RiskGame {

    Player activePlayer;
    int phase;
    ArrayList<Player> players;
    Country attackingCount;
    Country defendingCount;

    public RiskGame() {
        players = new ArrayList<Player>();
        for (int i = 1; i < 5; ++i) {
            players.add(new Player(i));
        }
        activePlayer = players.get(0);
        phase = 1;
        attackingCount = new Country();
        defendingCount = new Country();

    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void nextPlayer(){
        if(activePlayer.getPlayerNum() == 4){
            activePlayer = players.get(0);
        }else {
            activePlayer = players.get(activePlayer.getPlayerNum());
        }
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public Country getAttackingCount() {
        return attackingCount;
    }

    public void setAttackingCount(Country attackingCount) {
        this.attackingCount = attackingCount;
    }

    public Country getDefendingCount() {
        return defendingCount;
    }

    public void setDefendingCount(Country defendingCount) {
        this.defendingCount = defendingCount;
    }

    public void attack(){
        if(phase == 2){
            if (!Arrays.asList(attackingCount.getNeighborCIDs()).contains(defendingCount.getcID())){
                System.out.println("not vaild attack"); //delete
                System.out.println(attackingCount.getcID() + "attack cid" + defendingCount.getcID()); //delete
                attackingCount = new Country();
                defendingCount = new Country();
            }else {
               System.out.println( attackingCount.getcID() + " is attacking " + defendingCount.getcID()); //delete
            }
        }
    }

    public void phaseChange(){
        if(phase != 3){ //if not phase 3 increment phase
            phase+= 1;

        }else { //otherwise set phase to one and change active player
          phase = 1;
          nextPlayer();
        }
    }

    public Country longClick(Country c) {
        if (activePlayer.getPlayerNum() == c.getPlayerNum() && phase == 1) {
            c.setArmiesHeld(c.getArmiesHeld() - 1);

        }
       return c;
    }

}
