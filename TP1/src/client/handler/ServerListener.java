package client.handler;

import common.XmlMessageReader;
import org.w3c.dom.Document;

import java.io.BufferedReader;

/**
 * Classe dedicada a escutar continuamente mensagens do servidor.
 * Encaminha cada mensagem recebida para o handler apropriado.
 */
public class ServerListener extends Thread {
    private final BufferedReader in;
    private final ClientMessageHandler messageHandler;

    public ServerListener(BufferedReader in, ClientMessageHandler handler) {
        this.in = in;
        this.messageHandler = handler;
        setDaemon(true); // Termina quando a aplicação termina
    }

    @Override
    public void run() {
        try {
            while (true) {
                String xml = in.readLine();
                System.out.println("🔽 Recebido:\n \t" + xml);
                Document doc = XmlMessageReader.parseXml(xml);
                messageHandler.handle(doc);
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao ler resposta: " + e.getMessage());
        }
    }
}

