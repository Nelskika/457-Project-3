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
    private String currentRequest = "";
    private Integer currentTurn;

    //Server object constructor. The Server class takes care of client requests to the server and then terminates.
    public Server(Socket connectionSocket, ConcurrentHashMap<Integer, Integer> userList, String game, Integer currentTurn) throws IOException {
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
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void processRequest() throws IOException, ClassNotFoundException {
        String request = (String) inputStream.readObject();
        String[] parsedRequest = parseReceivedData(request);
        int ID = Integer.parseInt(parsedRequest[1]);
        currentRequest = parsedRequest[0];

        switch (currentRequest) {
            case "connect":
                checkNewPlayer(ID);
                System.out.println("Added player " + ID + " position in turn order: " + userList.size());
                break;
            case "retrieve":
                System.out.println("User: " + ID + " retrieved");
                break;
            case "update":
                updateServerGameState(parsedRequest);
                incrementCurrentTurn(currentTurn);
                break;
            case "exit":
                updateServerGameState(parsedRequest);
                break;
            default:
                System.out.println("Invalid request was made client-side");
                System.out.println(currentRequest);
                break;
        }

        sendResponse(generateResponse(request));
        closeConnection();
    }

    private void incrementCurrentTurn(Integer currentTurn){
        currentTurn++;
        if(currentTurn.intValue() == 4)
            currentTurn = 0;
    }

    private void sendResponse(String response) throws IOException {
        outputStream.writeObject(response);
    }

    private String generateResponse(String request){
        String response = "";
        if(currentRequest == "connect"){
            response = request;
            response += ":" + userList.size();
        }else if(currentRequest == "retrieve"){
            response = request;
            response += ":" + currentTurn;
            response += game;
        }else if(currentRequest == "update"){
            response = request;
        }else if(currentRequest == "exit"){
            response = request;
        }
        return response;
    }

    private void checkNewPlayer(int activePlayerID){
        if(!userList.containsValue(activePlayerID))
            userList.put(userList.size(), activePlayerID);
    }

    private void updateServerGameState(String[] parsedRequest){
        game = "";
        for(int i = 2; i < parsedRequest.length; i++){
            game += parsedRequest[i] + ":";
        }
    }

    private String[] parseReceivedData(String message){
        String[] requestData = message.split(":");
        return requestData;
    }
}
