package server.handler;

import common.XmlMessageBuilder;
import server.ClientConnection;

public class GameMatch {

    public static void startMatch(ClientConnection p1, ClientConnection p2) {
        String xml1 = XmlMessageBuilder.buildGameStart(p1.getUsername(), p2.getUsername(), true);
        String xml2 = XmlMessageBuilder.buildGameStart(p2.getUsername(), p1.getUsername(), false);

        p1.send(xml1);
        p2.send(xml2);

        ActiveGamesManager.registerGame(p1, p2);

        System.out.println("✅ Partida iniciada entre " + p1.getUsername() + " e " + p2.getUsername());
    }
}
