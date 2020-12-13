package com.example.risk;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends Thread{

    RiskGame g;
    int ID;
    boolean playerExited = false;
    Socket connectionSocket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    public Client(RiskGame g){
        this.g = g;
        this.ID = generateID();
    }

    public String writeGameStateToString(){
        String gamestate = "";
        gamestate += this.ID + ":";
        for(Country countries: g.countries){
            gamestate += countries.getcID() + " " + countries.getPlayerNum() + " " + countries.getArmyValue() + ":";
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

    @Override
    public synchronized void run() {

        while(!playerExited) {
            try {
                Thread.sleep(5000);
                connectToServer();
                sendData();
                receiveData();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
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
        outputStream.writeObject(writeGameStateToString());
        outputStream.flush();
    }


    private String[] parseReceivedData(String message){
        String gameState[] = message.split(":");
        String ID = gameState[0];
        System.out.println("Player: " + ID + " sent gamestate: ");
        for(int i = 1; i < gameState.length; i++){
            String countryInfo[] = gameState[i].split(" ");
            System.out.println("Country " + countryInfo[0] + " owned by player " + countryInfo[1] + " has " + countryInfo[2] + " armies.");
        }
        return gameState;
    }

    private void receiveData() throws IOException {
        boolean listening = true;
        openInputStream();
        while(listening){
            try {
                String temp = (String) inputStream.readObject();
                int identifierFromReceivedData = Integer.parseInt(parseReceivedData(temp)[0]);
                if(identifierFromReceivedData == ID) {
                    listening = false;
                }else{
                    System.out.println(identifierFromReceivedData + ": Updated local gamestate");
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Received incompatible Object Type from Server");
            }
        }
    }
}
