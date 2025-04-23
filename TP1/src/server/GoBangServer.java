package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GoBangServer {

    public static final int DEFAULT_PORT = 8080;

    private final int port;
    private ServerSocket serverSocket;

    public GoBangServer() {
        this(DEFAULT_PORT);
    }

    public GoBangServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor a escutar no porto " + port + "...");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new ClientConnection(clientSocket).start();
        }
    }
}
