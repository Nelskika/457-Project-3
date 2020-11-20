package com.example.risk;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {


    RiskGame game; //game instance
    Button next; //next button
    Button Attack; // attack button
    ArrayList<Button> countyButtons; // array list of country buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //used by android
        setContentView(R.layout.activity_main);

       countyButtons = new ArrayList<>(); //initialize arrayList

        game = new RiskGame();  //new game instance
        next = findViewById(R.id.nextButt); //next button
        Attack= findViewById(R.id.attackBut); // attack button

        ArrayList <Integer>  neighbors = new ArrayList<>(); //Arraylist of cIDs for countrys

        neighbors.add(2); //first arraylist


        Button button1 = findViewById(R.id.c1); //find first button
        button1.setTag(new Country(1,5,5,1,neighbors)); //set data of country button
        countyButtons.add(button1); //add button to countryButtons

        neighbors = new ArrayList<>(); //need new arraylist otherwise all will point to the same

        neighbors.add(1); //second country buttons neighbors
        neighbors.add(3);

        Button button2 = findViewById(R.id.c2); //second button
        button2.setTag(new Country(2,50,5,2,neighbors)); //set data of second button
        countyButtons.add(button2); //add button to countryButtons

        neighbors = new ArrayList<>();
        neighbors.add(2);
        neighbors.add(4);

        Button button3 = findViewById(R.id.c3);
        button3.setTag(new Country(3,10,5,3,neighbors));
        countyButtons.add(button3);

        neighbors = new ArrayList<>();
        neighbors.add(3);

        Button button4 = findViewById(R.id.c4);
        button4.setTag(new Country(4,99,5,4,neighbors));
        countyButtons.add(button4);

        countryButtonSetUp(countyButtons); //sets up button behavior
        nextButtonSetup();// sets up next button

        Attack.setOnClickListener(v ->{ //sets attack button behavior
            game.attack();
        });


    }

    /**
     * Updates a country button's display features
     * @param button
     */
    public void updateButton(Button button){
            Country c = (Country) button.getTag(); //get country data
            button.setText(c.getArmiesHeld() + ""); //sets the text to proper number of armies
            System.out.println(c.getPlayerNum() + "Player Num"); // delete
            int val = c.getPlayerNum(); //switch statement to set color
            switch (val) {
                case 1:
                    button.setBackgroundColor(Color.RED);
                    break;
                case 2:
                    button.setBackgroundColor(Color.WHITE);
                    break;
                case 3:
                    button.setBackgroundColor(Color.YELLOW);
                    break;
                case 4:
                    button.setBackgroundColor(Color.BLUE);
                    break;
                default:
                    break;
            }
        }


    /**
     * sets up country button behavior
     * @param buttons
     */
    public void countryButtonSetUp(ArrayList<Button> buttons){
        for(Button button:buttons) { //iterate over each button
            Country c = (Country) button.getTag(); //get country data
            button.setOnClickListener(v ->{ //set on click behavior

                if(game.getPhase() == 1) { //place troop phase of turn
                    if (game.getActivePlayer().getPlayerNum() == c.getPlayerNum()) { //if active player holds country
                        c.setArmiesHeld(c.getArmiesHeld() + 1); //add a troop
                        button.setTag(c); //update buttons data
                        updateButton(button); //update display
                    }
                }else if(game.getPhase() ==2){ // attack phase
                    if(game.getActivePlayer().getPlayerNum() == c.getPlayerNum()){ //player controls country
                        game.setAttackingCount(c); //sets the country as attacking
                    }else { //else set to defending
                        game.setDefendingCount(c);
                    }

                }//else if(game.getPhase() == 3){
                   // if(game.getActivePlayer().getPlayerNum() == c.getPlayerNum()){
                     //   game.setAttackingCount(c);
                    //}
                //}
              });

            button.setOnLongClickListener(v ->{ //on hold removes troop
                if(game.getActivePlayer().getPlayerNum() == c.getPlayerNum() && game.getPhase() == 1) {
                    c.setArmiesHeld(c.getArmiesHeld() - 1);
                    button.setTag(c);
                    updateButton(button);
                }
                return true; //returns true so onClick doesn't activate
            });
            updateButton(button); //update the buttons display
            System.out.println(c.getNeighborCIDs() +"");//delete
            }
        }

        public void nextButtonSetup(){//change game phase and player
        next.setOnClickListener(v ->{
            if(game.getPhase() != 3){ //if not phase 3 increment phase
                game.setPhase(game.getPhase() + 1);
                next.setText(game.getPhase() + ""); //delete

            }else { //otherwise set phase to one and change active player
                game.setPhase(1);
                game.nextPlayer();
            }
        });
        }
}


