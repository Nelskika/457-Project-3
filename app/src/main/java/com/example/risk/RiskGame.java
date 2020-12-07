package com.example.risk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RiskGame implements Serializable {

    Player activePlayer;
    int phase;
    ArrayList<Player> players;
    Country attackingCount;
    Country defendingCount;
    ArrayList<Country> countries;
    Random rand;



    int moveFromcID;
    int moveTocID;
    int aDie1;
    int aDie2;
    int aDie3;
    int dDie1;
    int dDie2;

    public RiskGame() {
        players = new ArrayList<Player>();
        for (int i = 1; i < 5; ++i) {
            players.add(new Player(i));
        }
        activePlayer = players.get(0);
        phase = 1;
        attackingCount = new Country();
        defendingCount = new Country();
        countries = new ArrayList<>();
        rand = new Random();
        aDie1 =0;
        aDie2 =0;
        aDie3 =0;
        dDie1 =0;
        dDie2 =0;
        moveFromcID =0;
        moveTocID =0;
    }

    public  void addCountry(Country country){
        countries.add(country);
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

    public RiskGame nextPlayer(){
        if(activePlayer.getPlayerNum() == 4){
            activePlayer = players.get(0);
        }else {
            activePlayer = players.get(activePlayer.getPlayerNum());
        }
        return this;
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

    public RiskGame attack(){
        if(phase == 2){
            boolean validAttack = false;
            for (int i =0 ; i < attackingCount.getNeighborCIDs().length; i++){
                System.out.println(attackingCount.getNeighborCIDs()[i]);
                if (attackingCount.getNeighborCIDs()[i] == defendingCount.getcID() && attackingCount.getArmiesHeld() >1){
                    validAttack = true;
                }
            }


            if (!validAttack){
                System.out.println("not vaild attack"); //delete
                System.out.println(attackingCount.getcID() + "attack cid" + defendingCount.getcID()); //delete
                System.out.println("attacking cids" + Arrays.asList(attackingCount.getNeighborCIDs()).toString() + " cid: "+ attackingCount.getcID());
               for (int i =0 ; i < attackingCount.getNeighborCIDs().length; i++){
                   System.out.println(attackingCount.getNeighborCIDs()[i]);
               }

                System.out.println("defending cids" + Arrays.asList(defendingCount.getNeighborCIDs()).toString() + " cid: "+ defendingCount.getcID());
                attackingCount = new Country();
                defendingCount = new Country();
            }else {
              rollAttack();
              rollDefence();
              int cId;

              if(aDie1 > dDie1 || aDie2 >dDie1 || aDie3 > aDie1){
                cId  = defendingCount.getcID();
                  countries.get(cId - 1).removeArmy();
              }else if((dDie1 >  aDie1 && dDie1 > aDie2 && dDie1 > aDie3)) {
                   cId = attackingCount.getcID();
                   countries.get(cId - 1).removeArmy();
              }

              if (dDie2 != 0 && attackingCount.getArmiesHeld() > 1){
                    if(aDie1 > dDie2 || aDie2 >dDie2 || aDie3 > aDie2){
                        cId  = defendingCount.getcID();
                        countries.get(cId - 1).removeArmy();
                    }else if((dDie2 > aDie1 &&  dDie2 > aDie2 && dDie2 > aDie3)) {
                        cId = attackingCount.getcID();
                        countries.get(cId - 1).removeArmy();
                  }
              }

              if(defendingCount.getArmiesHeld() <= 0){
                  cId = defendingCount.getcID();
                  countries.get(cId - 1).setPlayerNum(attackingCount.getPlayerNum());
                  countries.get(cId -1).setArmiesHeld(1);

                  cId = attackingCount.getcID();
                  countries.get(cId -1).setArmiesHeld(countries.get(cId -1).getArmiesHeld() - 1);
                  attackingCount = new Country();
                  defendingCount = new Country();
              }
              diceToZero();
              System.out.println( attackingCount.getcID() + " is attacking " + defendingCount.getcID()); //delete
            }

        }
        return this;
    }

    private void diceToZero(){
        aDie1 =0;
        aDie2 =0;
        aDie3 =0;
        dDie1 =0;
        dDie2 =0;
    }

    public RiskGame phaseChange(){
        if(phase != 3){ //if not phase 3 increment phase
            phase+= 1;

        }else { //otherwise set phase to one and change active player
          phase = 1;
          nextPlayer();
          activePlayer.setPlaceablearmies(0);
          for(Country c: countries) {
              if (c.getPlayerNum() == activePlayer.getPlayerNum() ) {
                  activePlayer.setPlaceablearmies(activePlayer.getPlaceablearmies() + c.getArmyValue());
              }
          }
          moveTocID =0;
          moveFromcID =0;
          if(activePlayer.getPlaceablearmies() ==0){
              phaseChange();
              phaseChange();
              phaseChange();
          }

        }
        return this;
    }

    public RiskGame addArmy(int cid){
        countries.get(cid - 1).addArmy();
        return this;
    }



    private void rollAttack(){
        if(attackingCount.getArmiesHeld() > 3){
            aDie1 = rand.nextInt(7 )+1;
            aDie2 = rand.nextInt(7 )+1;
            aDie3 = rand.nextInt(7 )+1;

        }else if(attackingCount.getArmiesHeld() > 2){
            aDie1 = rand.nextInt(7 )+1;
            aDie2 = rand.nextInt(7 )+1;
        }else {
            aDie1 = rand.nextInt(7 ) +1;
        }
        System.out.println(aDie1 +" " + aDie2 +" " +aDie3);
    }

    private void rollDefence(){
        if(defendingCount.getArmiesHeld() > 1){
            dDie1 = rand.nextInt(7 )+1;
            dDie2 = rand.nextInt(7 )+1;
        }else {
            dDie1 = rand.nextInt(7 )+1;
        }
        System.out.println(dDie1 +"  "+ dDie2);
    }

    public RiskGame move(){


        if(phase == 3){
            boolean validMove = false;
            for (int i =0 ; i < countries.get(moveFromcID -1).getNeighborCIDs().length; i++){
//                System.out.println(attackingCount.getNeighborCIDs()[i]);
                if (countries.get(moveFromcID -1).getNeighborCIDs()[i] == moveTocID && countries.get(moveFromcID -1).getArmiesHeld() >1){
                    validMove = true;
                }
            }
            if(moveTocID != 0 && moveTocID !=moveFromcID && moveFromcID !=0 && validMove){
                addArmy(moveTocID);
                countries.get(moveFromcID -1).removeArmy();
            }
        }
        return this;
    }

    public Country getCountry(int cid){
        return countries.get(cid - 1);
    }



    public int getMoveFromcID() {
        return moveFromcID;
    }

    public void setMoveFromcID(int moveFromcID) {
        this.moveFromcID = moveFromcID;
    }

    public int getMoveTocID() {
        return moveTocID;
    }

    public void setMoveTocID(int moveTocID) {
        this.moveTocID = moveTocID;
    }
}
