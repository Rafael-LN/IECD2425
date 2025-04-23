package client;

import client.handler.*;

public class GoBangClient {

    private final ClientCommunicationHandler communicationHandler;

    public GoBangClient(String serverIp, int serverPort) {
        ClientMessageHandler messageHandler = new ClientMessageHandler();
        communicationHandler = new ClientCommunicationHandler(serverIp, serverPort, messageHandler);
    }

    public void start() {
        communicationHandler.connect();
        // Aqui podes chamar menu ou iniciar GUI
    }

    public void login(String username, String password) {
        communicationHandler.sendLogin(username, password);
    }

    public void register(String username, String password, int age, String nationality, String photoPath) {
        communicationHandler.sendRegister(username, password, age, nationality, photoPath);
    }

    public static void main(String[] args) {
        String ip = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

        GoBangClient client = new GoBangClient(ip, port);
        client.start();

        // Testes temporários:
        client.register("rafa", "1234", 25, "Portugal", "/img/rafa.jpg");
        client.login("rafa", "1234");
    }
}
