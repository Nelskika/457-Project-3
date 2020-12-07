import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;


public class Server extends Thread{
    private Socket connectionSocket;
    int port;
    int count = 1;

    public Server(Socket connectionSocket){
        this.connectionSocket = connectionSocket;
    }

    public void run(){
        if (count == 1)
            System.out.println("User connected " + connectionSocket.getInetAddress());
        count++;

        try{
            processRequest();
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception{

    }
}
