package server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : GoBangServer.DEFAULT_PORT;

        try {
            GoBangServer server = new GoBangServer(port);
            server.start();
        } catch (IOException e) {
            System.err.println("❌ Porto " + port + " está ocupado.");
        }
    }
}

