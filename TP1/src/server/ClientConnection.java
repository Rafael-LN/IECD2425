package server;

import common.XmlMessageReader;
import org.w3c.dom.Document;
import server.handlers.ClientMessageProcessor;

import java.io.*;
import java.net.Socket;

public class ClientConnection extends Thread {

    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                String xml = (String) in.readObject();
                Document doc = XmlMessageReader.parseXml(xml);
                ClientMessageProcessor.process(doc, out);
            }
        } catch (EOFException e) {
            System.out.println("Cliente desligou-se.");
        } catch (Exception e) {
            System.err.println("Erro na ligação: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
            }
        }
    }
}
