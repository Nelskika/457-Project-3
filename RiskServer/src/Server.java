import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


public class Server extends Thread{
    private Socket connectionSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String game;
    ConcurrentHashMap<Integer, Integer> userList;
    private String[] requestTypes = {"connect", "retrieve", "update", "exit"};

    //Server object constructor. The Server class takes care of client requests to the server and then terminates.
    public Server(Socket connectionSocket, ConcurrentHashMap<Integer, Integer> userList, String game) throws IOException {
        this.userList = userList;
        this.connectionSocket = connectionSocket;
        this.game = game;
        openInputStream();
        openOutputStream();
    }

    public void openInputStream() throws IOException {
        inputStream = new ObjectInputStream(connectionSocket.getInputStream());
    }

    public void openOutputStream() throws IOException{
        outputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
    }

    public void closeConnection() throws IOException {
        outputStream.close();
        inputStream.close();
        connectionSocket.close();
    }

    public void run(){
        try {
            processRequest();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void processRequest() throws IOException, ClassNotFoundException {
        String updatedGameState = (String) inputStream.readObject();
        String ID = parseReceivedData(updatedGameState)[0];
        int activePlayerID = Integer.parseInt(ID);

        checkNewPlayer(activePlayerID);

        updateServerGameState(updatedGameState);
        updateClientGameState(updatedGameState);
        closeConnection();
    }

    private void checkNewPlayer(int activePlayerID){
        if(!userList.containsValue(activePlayerID))
            userList.put(userList.size(), activePlayerID);
    }

    private void updateServerGameState(String gamestate){
        this.game = gamestate;
    }

    private void updateClientGameState(String gamestate){
        try {
            outputStream.writeObject(gamestate);
        } catch (Exception e) {
            System.out.println("Client not listening to server.");
        }
    }

    private String[] parseReceivedData(String message){
        String[] gameState = message.split(":");
        String ID = gameState[0];
        System.out.println("Player: " + ID + " sent gamestate: ");
        for(int i = 1; i < gameState.length; i++){
            String[] countryInfo = gameState[i].split(" ");
            System.out.println("Country " + countryInfo[0] + " owned by player " + countryInfo[1] + " has " + countryInfo[2] + " armies.");
        }
        return gameState;
    }
}
