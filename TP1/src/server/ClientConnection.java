package server;

import server.handler.ClientMessageProcessor;

import java.io.*;
import java.net.Socket;

public class ClientConnection extends Thread {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientConnection(Socket socket) {
        try {
            this.socket = socket;
            out = new PrintWriter(this.socket.getOutputStream(), true);

            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        }
        catch (IOException e) {
            System.err.println("❌ Erro ao iniciar ligação: " + e.getMessage());
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void send(String xml) {
        if (out != null) {
            out.println(xml);
        }
    }

    public void closeConnection() {
        try {
            if (out != null) out.close();
        } catch (Exception ignored) {}
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }

    @Override
    public void run() {
        try {
            ClientMessageProcessor processor = new ClientMessageProcessor(this, in);
            processor.start();
        } catch (Exception e) {
            System.err.println("❌ Ligacão terminada para " + username + ": " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
}
