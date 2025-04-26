package client;

import client.handler.ClientCommunicationHandler;
import client.handler.ClientMessageHandler;
import gui.MainWindow;

public class GoBangClient {

    private final ClientSession session;
    private final ClientCommunicationHandler communication;
    private final MainWindow gui;

    public GoBangClient(String ip, int port) {
        this.session = new ClientSession();
        this.gui = new MainWindow(this);
        ClientMessageHandler handler = new ClientMessageHandler(session, gui);
        this.communication = new ClientCommunicationHandler(ip, port, handler);
    }

    public void start() {
        communication.connect();
        gui.setVisible(true);
    }

    public void login(String username, String password) {
        session.login(username); // guarda temporariamente para que o handler saiba quem é
        communication.sendLogin(username, password);
    }

    public void register(String username, String password, int age, String nationality, String photo) {
        session.login(username); // também queremos saber quem se está a registar
        communication.sendRegister(username, password, age, nationality, photo);
    }

    public void findMatch(String username) {
        communication.sendFindMatch(username);
    }


    public MainWindow getGui() {
        return gui;
    }

    public static void main(String[] args) {
        String ip = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

        GoBangClient client = new GoBangClient(ip, port);
        client.start();
    }

}
