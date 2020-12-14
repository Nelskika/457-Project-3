import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


public class Server extends Thread{
    private Socket connectionSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String[] game;
    ConcurrentHashMap<Integer, Integer> userList;
    private String[] requestTypes = {"connect", "retrieve", "update", "exit"};
    private String currentRequest = "";
    private int[] currentTurn;

    //Server object constructor. The Server class takes care of client requests to the server and then terminates.
    public Server(Socket connectionSocket, ConcurrentHashMap<Integer, Integer> userList, String[] game, int[] currentTurn) throws IOException {
        this.userList = userList;
        this.connectionSocket = connectionSocket;
        this.game = game;
        this.currentTurn = currentTurn;
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
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processRequest() throws IOException, ClassNotFoundException, InterruptedException {
        String request = (String) inputStream.readObject();
        String[] parsedRequest = parseReceivedData(request);
        int ID = Integer.parseInt(parsedRequest[1]);
        int delayTime = 2;
        currentRequest = parsedRequest[0];
        if(currentTurn[0] == 5)
            checkGameStart();

        switch (currentRequest) {
            case "connect":
                checkNewPlayer(ID);
                System.out.println("Added player " + ID + " position in turn order: " + (userList.size()-1));
                if(userList.size() == 1){
                    System.out.println("Instantiated Game");
                    updateServerGameState(parsedRequest);
                }
                break;
            case "retrieve":
                Thread.sleep(delayTime);
                System.out.println("User: " + ID + " retrieved " + "current turn: " + currentTurn[0]);
                break;
            case "update":
                Thread.sleep(delayTime);
                updateServerGameState(parsedRequest);
                incrementCurrentTurn(currentTurn);
                break;
            case "exit":
                Thread.sleep(delayTime);
                updateServerGameState(parsedRequest);
                break;
            default:
                System.out.println("Invalid request was made client-side");
                System.out.println(currentRequest);
                break;
        }

        sendResponse(generateResponse(parsedRequest));
        closeConnection();
    }

    private void checkGameStart(){
        //Start the game
        if(userList.size() == 4) {
            currentTurn[0] = 0;
        }
    }

    private void incrementCurrentTurn(int[] currentTurn){
        System.out.println("Incremented");
        currentTurn[0] = currentTurn[0]+1;
        if (currentTurn[0] >= 4)
            currentTurn[0] = 0;
    }

    private void sendResponse(String response) throws IOException {
        outputStream.writeObject(response);
    }

    private String generateResponse(String[] parsedRequest){
        String request = currentRequest;
        String response = "";
        if(currentRequest.equals("connect")){
            response = request;
            response += ":" + parsedRequest[1];
            response += ":" + (userList.size()-1);
        }else if(currentRequest.equals("retrieve")){
            response = request;
            response += ":" + userList.get(currentTurn[0]);
            response += ":" + currentTurn[0];
            response += ":" + game[0];
            System.out.println(game[0]);
        }else if(currentRequest.equals("update")){
            response = request;
        }else if(currentRequest.equals("exit")){
            response = request;
        }
        return response;
    }

    private void checkNewPlayer(int activePlayerID){
        if(!userList.containsValue(activePlayerID))
            userList.put(userList.size(), activePlayerID);
    }

    private void updateServerGameState(String[] parsedRequest){
        game[0] = "";
        for(int i = 2; i < parsedRequest.length; i++){
            game[0] += parsedRequest[i] + ":";
        }
    }

    private String[] parseReceivedData(String message){
        String[] requestData = message.split(":");
        return requestData;
    }
}
