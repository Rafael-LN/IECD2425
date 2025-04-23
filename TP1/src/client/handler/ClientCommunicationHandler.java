package client.handler;

import common.*;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientCommunicationHandler {

    private final String serverIp;
    private final int serverPort;
    private final ClientMessageHandler messageHandler;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientCommunicationHandler(String serverIp, int serverPort, ClientMessageHandler handler) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.messageHandler = handler;
    }

    public void connect() {
        try {
            socket = new Socket(serverIp, serverPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("✅ Ligado ao servidor.");
        } catch (IOException e) {
            System.err.println("❌ Falha na ligação: " + e.getMessage());
        }
    }

    public void sendLogin(String username, String password) {
        try {
            String xml = XmlMessageBuilder.buildLoginRequest(username, password);
            out.writeObject(xml);
            receiveResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRegister(String username, String password, int age, String nationality, String photoPath) {
        try {
            String xml = XmlMessageBuilder.buildRegisterRequest(username, password, age, nationality, photoPath);
            out.writeObject(xml);
            receiveResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveResponse() {
        try {
            String xml = (String) in.readObject();
            Document doc = XmlMessageReader.parseXml(xml);
            messageHandler.handle(doc);
        } catch (Exception e) {
            System.err.println("❌ Erro ao ler resposta: " + e.getMessage());
        }
    }
}
