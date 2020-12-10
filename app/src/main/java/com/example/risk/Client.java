package com.example.risk;
import android.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends Thread{

    RiskGame g;
    int ID;
    boolean playerExited = false;
    ReentrantLock lock = new ReentrantLock();
    AtomicBoolean started = new AtomicBoolean(false);

    public Client(RiskGame g) throws IOException {
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

    @Override
    public synchronized void run() {
        try {
            lock.lock();
            this.sendData();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public synchronized static int generateID(){
        ZonedDateTime nowZoned = ZonedDateTime.now();
        Instant midnight = nowZoned.toLocalDate().atStartOfDay(nowZoned.getZone()).toInstant();
        Duration duration = Duration.between(midnight, Instant.now());
        long seconds = duration.getSeconds();
        return (int) (seconds%(65535-4999))+4999;
    }

    public synchronized void sendData() throws IOException {
        if(!started.getAndSet(true)) {
            System.out.println(ID);
            Socket sendingSocket = new Socket("10.0.2.2", 4999);
            OutputStream os;
            BufferedOutputStream bos;
            ObjectOutputStream oos;

            os = sendingSocket.getOutputStream();
            bos = new BufferedOutputStream(os);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(writeGameStateToString());

            oos.flush();

            while (!this.playerExited) {
                //Get gamestate back from server
                receiveData();
                os = sendingSocket.getOutputStream();
                bos = new BufferedOutputStream(os);
                oos = new ObjectOutputStream(bos);
                oos.writeObject(writeGameStateToString());

                oos.flush();

            }
            sendingSocket.close();
        }
    }

    public synchronized void receiveData() throws IOException {
        Socket receivingSocket;
        InputStream is;
        BufferedInputStream bis;
        ObjectInputStream ois;
        boolean listening = true;
        while(listening){
            ServerSocket clientListening = new ServerSocket(this.ID);
            receivingSocket = clientListening.accept();
            is = receivingSocket.getInputStream();
            bis = new BufferedInputStream(is);
            ois = new ObjectInputStream(bis);
            try {
                RiskGame temp = (RiskGame) ois.readObject();
                if(temp.getActivePlayerID() == ID) {
                    listening = false;
                    this.g = temp;
                }else{
                    System.out.println(temp.getActivePlayer() + ": Updated local gamestate");
                    this.g = temp;
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Received incompatible Object Type from Server");
            }
            ois.close();
            bis.close();
            is.close();
            clientListening.close();
        }
    }
}
