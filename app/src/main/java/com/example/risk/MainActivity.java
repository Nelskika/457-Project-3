package com.example.risk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {


    RiskGame game; //game instance
    Button next; //next button
    Button Attack; // attack button
    ArrayList<Button> countryButtons; // array list of country buttons
    Random rand;
    TextView army;
    Client client;
    int clientTurn = -1;
    int currentTurn = 0;
    //new listener
    PropertyChangeListener listener;
    Button close;
    TextView gameOverText;
    ImageView bg;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
            getSupportActionBar().hide();
            super.onCreate(savedInstanceState); //used by android
            setContentView(R.layout.activity_main);

            army = findViewById(R.id.armyLabel);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //force landscape mode

            rand = new Random(); //random object

            countryButtons = new ArrayList<>(); //initialize arrayList

            next = findViewById(R.id.nextButt); //next button
            Attack = findViewById(R.id.attackBut); // attack button





            int orentation = getResources().getConfiguration().orientation;
            if (Configuration.ORIENTATION_LANDSCAPE == orentation) {
                game = new RiskGame();  //new game instance
                for (int i = 0; i < 16; i++) {

                    String name = "c" + (i + 1);
                    Button button = findViewById(getResources().getIdentifier(name, "id", getPackageName()));
                    button.setTag(game.getCountry(i + 1));
                    countryButtons.add(button);

                }

                countryButtonSetUp(countryButtons); //sets up button behavior
                nextButtonSetup();// sets up next button
                listener = new UpdateListener();
                game.addListener(listener);
                Attack.setOnClickListener(v -> { //sets attack button behavior
                    if (game.getPhase() == 2) {
                        game = game.attack();
                    } else if (game.getPhase() == 3) {
                        game = game.move();
                    }
                    updateButtons();
                });

                for (int i = 0; i < 12; i++) {
                    next.performClick();
                }

                gameOverText = findViewById(R.id.gameOverText2);
                gameOverText.setVisibility(View.INVISIBLE);
                close = findViewById(R.id.close2);
                close.setOnClickListener(v -> {
                    MainActivity.this.finish();
                    System.exit(0);
                });
                close.setVisibility(View.INVISIBLE);
                close.setEnabled(false);

            } else {

            }

        }

    /**
     * Updates a country button's display features
     */
    public void updateButtons(){
        try {
            for (Button button : this.countryButtons) {
                if(currentTurn != clientTurn) {
                    button.setEnabled(false);
                }
                else {
                    button.setEnabled(true);
                }
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
                if(currentTurn != clientTurn) {
                    Attack.setEnabled(false);
                    next.setEnabled(false);
                }
                else {
                    Attack.setEnabled(true);
                    next.setEnabled(true);
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

        //Create the Client object and pass the RiskGame object to it

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

                }else if(game.getPhase() == 3){ //move phase

                    //sets countries that are being moved from and to, if reclicked deselect the country
                    if(game.getActivePlayer().getPlayerNum() == c.getPlayerNum()){
                        if(game.getMoveFromcID() == 0){
                            game.setMoveFromcID(c.getcID());
                        }else if(game.getMoveTocID() == 0){
                            game.setMoveTocID(c.getcID());
                        }else if(game.getMoveTocID() == c.getcID()){
                            game.setMoveTocID(0);
                        }else if(game.getMoveFromcID() == c.getcID()){
                            game.setMoveFromcID(0);
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
            if(game.getGameOver()){
                for(Button button : countryButtons){
                    button.setEnabled(false);
                    button.setVisibility(View.INVISIBLE);
                }
                Attack.setVisibility(View.INVISIBLE);
                Attack.setEnabled(false);
                next.setEnabled(false);
                next.setVisibility(View.INVISIBLE);

                close.setVisibility(View.VISIBLE);
                close.setEnabled(true);

                gameOverText.setVisibility(View.VISIBLE);

                bg = findViewById(R.id.map);
                bg.setVisibility(View.INVISIBLE);
            }

            if(game.getPhase() == 1) {
                currentTurn = 0;
            }
            updateButtons();
        });
        }
        //listen for state changes
        private class UpdateListener implements PropertyChangeListener {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                clientTurn = (int)e.getOldValue();
                currentTurn = (int)e.getNewValue();
                updateButtons();
            }
        }
}


