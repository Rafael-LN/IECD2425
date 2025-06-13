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
            out.writeObject(xml);System.out.println("🔼 Enviado:\n" + xml);
            System.out.println("🔼 Enviado:\n \t" + xml);
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

    public void sendUpdateProfile(String username, String photoBase64) {
        try {
            String xml = XmlMessageBuilder.buildUpdateProfileRequest(username, photoBase64);
            System.out.println("🔼 Enviar updateProfile para o servidor:\n" + xml);
            out.writeObject(xml);
            receiveResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendFindMatch(String username) {
        try {
            String xml = XmlMessageBuilder.buildFindMatchRequest(username);
            System.out.println("🔼 Enviar findMatch para o servidor:\n \t" + xml);
            out.writeObject(xml);
            receiveResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCancelMatch(String username) {
        try {
            String xml = XmlMessageBuilder.buildCancelMatchRequest(username);
            System.out.println("🔼 Enviar cancelMatch para o servidor:\n \t" + xml);
            out.writeObject(xml);
            receiveResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(String username, int row, int col) {
        try {
            String xml = XmlMessageBuilder.buildMoveRequest(username, row, col);
            out.writeObject(xml);
            // move não requer resposta imediata
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void receiveResponse() {
        try {
            String xml = (String) in.readObject();
            System.out.println("🔽 Recebido:\n \t" + xml);
            Document doc = XmlMessageReader.parseXml(xml);
            messageHandler.handle(doc);
        } catch (Exception e) {
            System.err.println("❌ Erro ao ler resposta: " + e.getMessage());
        }
    }
}
