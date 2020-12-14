import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;


public class MultiServer {
    public static ConcurrentHashMap<Integer, Integer> userList;
    public static String[] game = new String[1];
    public static Integer currentTurn = 1;

    public static void main(String[] args) throws IOException {
        userList = new ConcurrentHashMap<>();
        ServerSocket serverSocket = null;
        boolean listening = true;
        game[0] = "";

        try {
            serverSocket = new ServerSocket(4999);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4999.");
            System.exit(-1);
        }

        while (listening) {
            Server RequestProcessor = new Server(serverSocket.accept(), userList, game, currentTurn);
            Thread Worker = new Thread(RequestProcessor);
            Worker.start();
        }
    }
}
