package com.example.risk;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

public class Client extends Thread{

    private RiskGame g;
    private int ID;
    private boolean playerExited = false;
    private Socket connectionSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String[] requestTypes = {"connect", "retrieve", "update", "exit"};
    private String currentRequest;
    private String receivedGameState = "";
    private int playerTurn = 0;

    public Client(RiskGame g){
        this.g = g;
        this.ID = generateID();
        this.currentRequest = requestTypes[0];
    }

    @Override
    public synchronized void run() {
        boolean playerWon;
        int delayTimer = 2000;

        while (!playerExited) {
            try {
                this.sleep(delayTimer);
                connectToServer();
                switch (currentRequest) {
                    case "connect":
                        sendData();
                        currentRequest = requestTypes[1];
                        break;
                    case "retrieve":
                        sendData();
                        int receivedID = receiveData();
                        if (receivedID == playerTurn)
                            currentRequest = requestTypes[2];
                        updateRiskGameByReceivedString();
                        g.notifyListener(ID, receivedID);
                        break;
                    case "update":
                        getPlayerMove();
                        sendData();
                        playerWon = checkPlayerWon();
                        if (playerWon)
                            currentRequest = requestTypes[3];
                        else
                            currentRequest = requestTypes[1];
                        break;
                    case "exit":
                        sendData();
                        playerExited = true;
                        break;
                    default:
                        System.out.println("Invalid request was made client-side");
                        break;
                }
                closeConnection();
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeConnection() throws IOException {
        if(!Objects.isNull(outputStream))
            outputStream.close();
        if(!Objects.isNull(inputStream))
            inputStream.close();
        connectionSocket.close();
    }

    private String generateRequest(){
        String gamestate = "";
        gamestate += currentRequest + ":";
        gamestate += this.ID + ":";
        if(currentRequest.equals("connect")  || currentRequest.equals("update") || currentRequest.equals("exit")) {
            for (Country countries : g.countries) {
                gamestate += countries.getcID() + " " + countries.getPlayerNum() + " " + countries.getArmyValue() + ":";
            }
        }
        return gamestate;
    }

    private void connectToServer() throws IOException {
        connectionSocket = new Socket("10.0.2.2", 4999);
        openOutputStream();
    }

    private void openInputStream() throws IOException {
        inputStream = new ObjectInputStream(connectionSocket.getInputStream());
    }

    private void openOutputStream() throws IOException{
        outputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
    }

    //NEED TO IMPLEMENT FUNCTIONALITY
    private boolean checkPlayerWon(){
        //Check if the player won the game (or exited.)
        if(playerExited) {
            return true;
        }
        int playersLeft = 0;
        for(int x = 0; x < 4; x++) {
            if(g.getPlayers().get(x).getNumCountries() != 0) {
                playersLeft++;
            }
        }
        return playersLeft == 1;
    }

    //NEED TO IMPLEMENT FUNCTIONALITY
    private void getPlayerMove(){
        String playerUpdatedGameState = "";
        //Allow the player attached to this client to do their turn convert the updated values to
        // a String and then send it to the server.
        receivedGameState = playerUpdatedGameState;
    }

    //NEED TO IMPLEMENT FUNCTIONALITY
    private void updateRiskGameByReceivedString(){
        //Use the "receivedGameState" string to update the values of the RiskGame.
        String gameState[] = receivedGameState.split(":");
        for(int i = 3; i < gameState.length; i++){
            String countryInfo[] = gameState[i].split(" ");

            int cID = Integer.parseInt(countryInfo[0]);
            int player = Integer.parseInt(countryInfo[1]);
            int armies = Integer.parseInt(countryInfo[2]);

            g.getCountry(cID).setPlayerNum(player);
            g.getCountry(cID).setArmiesHeld(armies);
        }
    }

    private static int generateID(){
        ZonedDateTime nowZoned = ZonedDateTime.now();
        Instant midnight = nowZoned.toLocalDate().atStartOfDay(nowZoned.getZone()).toInstant();
        Duration duration = Duration.between(midnight, Instant.now());
        long seconds = duration.getSeconds();
        return (int) (seconds%(65535-4999))+4999;
    }

    private void sendData() throws IOException {
        outputStream.writeObject(generateRequest());
        outputStream.flush();
    }

    private int receiveData() throws IOException, ClassNotFoundException {
        openInputStream();
        String serverResponse = (String) inputStream.readObject();
        String[] parsedServerResponse = parseReceivedData(serverResponse);
        String responseType = parsedServerResponse[0];
        int identifierFromReceivedData = 0;
        if(!parsedServerResponse[1].equals("null"))
            identifierFromReceivedData = Integer.parseInt(parsedServerResponse[1]);

        if(responseType == "connect"){
            //Need to tell client/game their order in the turn queue
            playerTurn = Integer.parseInt(parsedServerResponse[2]);
        }
        else if(responseType == "retrieve"){
            if(!(parsedServerResponse.length < 10))
                updateGameStateString(parsedServerResponse);
        }else if(responseType == "exit"){
            //Alert them that player 1-4 has won
        }
        return Integer.parseInt(parsedServerResponse[2]);
    }

    private void updateGameStateString(String[] parsedResponse){
        receivedGameState = "";
        for(int i = 3; i < parsedResponse.length; i++){
            receivedGameState += parsedResponse[i] + ":";
        }
    }

    //This method will have to be overhauled.
    private String[] parseReceivedData(String message){
        String[] requestData = message.split(":");
        return requestData;
    }
}