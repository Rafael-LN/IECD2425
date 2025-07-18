package client;

import client.handler.ClientCommunicationHandler;
import client.handler.ClientMessageHandler;
import common.UserProfileData;
import gui.MainWindow;

import java.util.Map;

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
        communication.sendLogin(username, password);
    }

    public void register(String username, String password, int age, String nationality, String photoBase64) {
        communication.sendRegister(username, password, age, nationality, photoBase64);
    }

    public void updateProfile(String username, String photoBase64) {
        communication.sendUpdateProfile(username, photoBase64);
    }

    public void requestProfile(String username) {
        communication.sendRequestProfile(username);
    }

    public void findMatch(String username) {
        communication.sendFindMatch(username);
    }

    public void sendMove(Map<String, String> moveData) {
        String username = session.getProfile().username();
        int row = Integer.parseInt(moveData.get("row"));
        int col = Integer.parseInt(moveData.get("col"));
        communication.sendMove(username, row, col);
    }

    public void cancelMatch(String username) {
        communication.sendCancelMatch(username);
    }

    public void logout(String username) {
        communication.sendLogout(username);
    }

    public void quitMatch(String username) {
        communication.sendQuitMatch(username);
    }

    public UserProfileData getProfile() {
        return session.getProfile();
    }

    public MainWindow getGui() {
        return gui;
    }

    public static void main(String[] args) {
        String ip = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8082;

        GoBangClient client = new GoBangClient(ip, port);
        client.start();
    }
}
