import java.net.*;
import java.io.*;

public class RiskMulti {

    public static void main(String[] args) throws IOException {
        int portNum = 1234;
        ServerSocket serverSocket = null;
        boolean listening = true;
        Server server;

        try {
            serverSocket = new ServerSocket(portNum);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + portNum + ".");
            System.exit(-1);
        }

        while (listening) {
            server = new Server(serverSocket.accept());
            Thread t = new Thread(server);
            t.start();
        }
    }
}
