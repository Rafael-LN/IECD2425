package client.handler;

import common.XmlMessageBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientCommunicationHandler {

    private final String serverIp;
    private final int serverPort;
    private final ClientMessageHandler messageHandler;

    private PrintWriter out;

    public ClientCommunicationHandler(String serverIp, int serverPort, ClientMessageHandler handler) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.messageHandler = handler;
    }

    public void connect() {
        try {
            Socket socket = new Socket(serverIp, serverPort);
                out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           
            System.out.println("✅ Ligado ao servidor.");

            new ServerListener(in, messageHandler).start();
        } catch (IOException e) {
            System.err.println("❌ Falha na ligação: " + e.getMessage());
        }
    }

    public void sendLogin(String username, String password) {
        String xml = XmlMessageBuilder.buildLoginRequest(username, password);
        out.println(xml);
        System.out.println("🔼 Enviado:\n \t" + xml);
    }

    public void sendRegister(String username, String password, int age, String nationality, String photoPath) {
        String xml = XmlMessageBuilder.buildRegisterRequest(username, password, age, nationality, photoPath);
        out.println(xml);
    }

    public void sendUpdateProfile(String username, String photoBase64) {
        String xml = XmlMessageBuilder.buildUpdateProfileRequest(username, photoBase64);
        System.out.println("🔼 Enviar updateProfile para o servidor:\n" + xml);
        out.println(xml);
    }


    public void sendFindMatch(String username) {
        String xml = XmlMessageBuilder.buildFindMatchRequest(username);
        System.out.println("🔼 Enviar findMatch para o servidor:\n \t" + xml);
        out.println(xml);
    }

    public void sendCancelMatch(String username) {
        String xml = XmlMessageBuilder.buildCancelMatchRequest(username);
        System.out.println("🔼 Enviar cancelMatch para o servidor:\n \t" + xml);
        out.println(xml);
    }

    public void sendMove(String username, int row, int col) {
        String xml = XmlMessageBuilder.buildMoveRequest(username, row, col);
        out.println(xml);
    }

    public void sendLogout(String username) {
        String xml = common.XmlMessageBuilder.buildLogoutRequest(username);
        System.out.println("🔼 Enviar logout para o servidor:\n\t" + xml);
        out.println(xml);
    }

}
