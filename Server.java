import java.net.*;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(5000);
        System.out.println("Server started...");

        while(true){
            Socket client = server.accept();
            new Thread(new ClientHandler(client)).start();
        }
    }
}
