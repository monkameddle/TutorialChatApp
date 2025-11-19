package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    public static final HashMap<String, ClientHandler> CLIENTS = new HashMap<>();
    private static final int PORT = 12345;

    static void main() {
        try {
            var serverSocket = new ServerSocket(PORT);
            System.out.println("Chat Server gestartet auf Port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                var handler = new ClientHandler(socket);
                new Thread(handler).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
