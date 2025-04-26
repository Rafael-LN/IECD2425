package server;

import org.w3c.dom.Document;
import server.handler.ClientMessageProcessor;

import java.io.*;
import java.net.Socket;

public class ClientConnection extends Thread {

    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;

    public ClientConnection(Socket socket) {
        this.socket = socket;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void send(String xml) {
        try {
            out.writeObject(xml);
        } catch (IOException e) {
            System.err.println("Erro ao enviar XML para " + username + ": " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                String xml = (String) in.readObject();
                System.out.println("🔽 Recebido de " + (username != null ? username : "??") + ":\n" + xml);

                Document doc = common.XmlMessageReader.parseXml(xml);
                ClientMessageProcessor.process(doc, this);
            }

        } catch (Exception e) {
            System.err.println("Ligação terminada: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
