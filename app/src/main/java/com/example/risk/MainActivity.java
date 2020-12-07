package com.example.risk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {


    RiskGame game; //game instance
    Button next; //next button
    Button Attack; // attack button
    ArrayList<Button> countryButtons; // array list of country buttons
    Random rand;
    TextView army;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState); //used by android
        setContentView(R.layout.activity_main);

        army = findViewById(R.id.armyLabel);

       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        rand = new Random();

        ArrayList<int []> neighbors = new ArrayList<int[]>(); // array of neighbor arrays
        neighbors.add(new int[]{2});
        neighbors.add( new int[]{1, 3, 14,15});
        neighbors.add(new int[]{2, 4, 16});
        neighbors.add(new int[]{3, 5, 16});
        neighbors.add(new int[]{4, 6, 16});
        neighbors.add(new int[]{5, 7, 8});
        neighbors.add( new int[]{6, 8});
        neighbors.add(new int[]{6, 7, 9, 16,15});
        neighbors.add(new int[]{8, 10});
        neighbors.add(new int[]{9, 11, 12});
        neighbors.add(new int[]{10});
        neighbors.add( new int[]{10, 13});
        neighbors.add( new int[]{12, 14});
        neighbors.add(new int[]{13, 2});
        neighbors.add( new int[]{2, 8, 16});
        neighbors.add(new int[]{3, 4, 5, 8, 15});



       countryButtons = new ArrayList<>(); //initialize arrayList

        game = new RiskGame();  //new game instance
        next = findViewById(R.id.nextButt); //next button
        Attack= findViewById(R.id.attackBut); // attack button

        int orentation = getResources().getConfiguration().orientation;
        if(Configuration.ORIENTATION_LANDSCAPE == orentation){
        for(int i = 0; i < neighbors.size(); i++) {
            //System.out.println(i + " ");
            String name = "c" + (i + 1);
            Button button = findViewById(getResources().getIdentifier(name, "id", getPackageName()));
            button.setTag(new Country(rand.nextInt(4) + 1, 5, i + 1, neighbors.get(i)));
            game.addCountry((Country)button.getTag());
            countryButtons.add(button);
        }

            countryButtonSetUp(countryButtons); //sets up button behavior
            nextButtonSetup();// sets up next button

            Attack.setOnClickListener(v ->{ //sets attack button behavior
                if(game.getPhase() ==2 ) {
                    game = game.attack();
                }else if(game.getPhase() == 3){
                    game = game.move();
                }
                updateButtons();
            });

            for(int i = 0; i < 12; i++){
                next.performClick();
            }
        }else{

                Button button1 = findViewById(R.id.c1); //find first button
                button1.setTag(new Country(1, 5, 1, (neighbors.get(0)))); //set data of country button
                countryButtons.add(button1); //add button to countryButtons

                Button button2 = findViewById(R.id.c2); //second button
                button2.setTag(new Country(2, 50, 2, neighbors.get(1))); //set data of second button
                countryButtons.add(button2); //add button to countryButtons

                Button button3 = findViewById(R.id.c3);
                button3.setTag(new Country(3, 10, 3, neighbors.get(2)));
                countryButtons.add(button3);

                Button button4 = findViewById(R.id.c4);
                button4.setTag(new Country(4, 99, 4, neighbors.get(3)));
                countryButtons.add(button4);
            }




    }

    /**
     * Updates a country button's display features
     */
    public void updateButtons(){
        try {


            for (Button button : this.countryButtons) {
                Country c = (Country) button.getTag();
                button.setTag(game.getCountry(c.getcID()));
                button.setText(c.getcID() + ": " + c.getArmiesHeld()); //sets the text to proper number of armies
                // System.out.println(c.getPlayerNum() + "Player Num"); // delete
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
                if(game.getPhase() == 1){
                    Attack.setVisibility(View.INVISIBLE);
                }else if(game.getPhase() == 2){
                    Attack.setVisibility(View.VISIBLE);
                    Attack.setText("Attack");
                }else if (game.getPhase() == 3){
                    Attack.setText("Move");
                }

            }
            army.setText(game.getActivePlayer().getPlaceablearmies() + "  ");

        }catch (Exception e){

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
                       Player active = game.getActivePlayer();
                       if(active.getPlaceablearmies() >0 ) {
                           this.game = game.addArmy(c.getcID());
                           button.setTag(game.getCountry(((Country) button.getTag()).getcID())); //update buttons data
                           active.setPlaceablearmies(active.getPlaceablearmies() -1);
                           updateButtons(); //update display
                       }
                    }
                }else if(game.getPhase() ==2){ // attack phase
                    if(game.getActivePlayer().getPlayerNum() == c.getPlayerNum()){ //player controls country
                        game.setAttackingCount(c); //sets the country as attacking
                    }else { //else set to defending
                        game.setDefendingCount(c);
                    }

                }else if(game.getPhase() == 3){
                    if(game.getActivePlayer().getPlayerNum() == c.getPlayerNum()){
                        if(game.getMoveFromcID() == 0){
                            game.setMoveFromcID(c.getcID());
                        }else if(game.getMoveTocID() == 0){
                            game.setMoveTocID(c.getcID());
                        }else if(game.getMoveTocID() == c.getcID()){
                            game.setMoveTocID(0);
                        }else if(game.getMoveFromcID() == c.getcID()){
                            game.setMoveTocID(0);
                        }
                    }
                }
              });
          //  System.out.println(c.getNeighborCIDs() +"");//delete
            }
        updateButtons(); //update the buttons display
    }

        public void nextButtonSetup(){//change game phase and player
        next.setOnClickListener(v ->{
            game.phaseChange();
            next.setText(game.getPhase() + ""); //delete
            updateButtons();
        });
        }
}


