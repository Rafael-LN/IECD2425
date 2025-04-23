package server.handlers;

import common.XmlMessageBuilder;
import common.XmlMessageReader;
import org.w3c.dom.Document;

import java.io.ObjectOutputStream;

public class ClientMessageProcessor {

    public static void process(Document doc, ObjectOutputStream out) {
        try {
            String type = XmlMessageReader.getRootElement(doc);

            switch (type) {
                case "loginRequest":
                    String username = XmlMessageReader.getTextValue(doc, "username");
                    String password = XmlMessageReader.getTextValue(doc, "password");
                    System.out.println("[LOGIN] " + username);

                    // Simulação de resposta
                    String loginResponse = XmlMessageBuilder.buildGameStart("oponenteFalso", true);
                    out.writeObject(loginResponse);
                    break;

                case "registerRequest":
                    String regUser = XmlMessageReader.getTextValue(doc, "username");
                    int age = XmlMessageReader.getIntValue(doc, "age");
                    String photo = XmlMessageReader.getTextValue(doc, "photo");

                    System.out.println("[REGISTO] " + regUser + " (" + age + ")");

                    // Simular resposta
                    String registerAck = XmlMessageBuilder.buildGameStart("espera", false);
                    out.writeObject(registerAck);
                    break;

                case "move":
                    String player = XmlMessageReader.getTextValue(doc, "player");
                    int x = XmlMessageReader.getIntValue(doc, "x");
                    int y = XmlMessageReader.getIntValue(doc, "y");
                    System.out.println("[JOGADA] " + player + ": (" + x + ", " + y + ")");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
