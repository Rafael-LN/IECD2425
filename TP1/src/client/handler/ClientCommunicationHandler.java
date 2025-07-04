package client.handler;

import common.XmlMessageBuilder;

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
            // Iniciar thread dedicada à escuta de mensagens do servidor
            new ServerListener(in, messageHandler).start();
        } catch (IOException e) {
            System.err.println("❌ Falha na ligação: " + e.getMessage());
        }
    }

    public void sendLogin(String username, String password) {
        try {
            String xml = XmlMessageBuilder.buildLoginRequest(username, password);
            out.writeObject(xml);
            System.out.println("🔼 Enviado:\n \t" + xml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRegister(String username, String password, int age, String nationality, String photoPath) {
        try {
            String xml = XmlMessageBuilder.buildRegisterRequest(username, password, age, nationality, photoPath);
            out.writeObject(xml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUpdateProfile(String username, String photoBase64) {
        try {
            String xml = XmlMessageBuilder.buildUpdateProfileRequest(username, photoBase64);
            System.out.println("🔼 Enviar updateProfile para o servidor:\n" + xml);
            out.writeObject(xml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendFindMatch(String username) {
        try {
            String xml = XmlMessageBuilder.buildFindMatchRequest(username);
            System.out.println("🔼 Enviar findMatch para o servidor:\n \t" + xml);
            out.writeObject(xml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCancelMatch(String username) {
        try {
            String xml = XmlMessageBuilder.buildCancelMatchRequest(username);
            System.out.println("🔼 Enviar cancelMatch para o servidor:\n \t" + xml);
            out.writeObject(xml);
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


    // O método receiveResponse pode ser removido ou mantido apenas para operações síncronas específicas,
    // mas a partir de agora a receção de mensagens será feita pelo ServerListener.
}
