package server.database;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserDatabase implements Serializable {

    private static final String FILE_PATH = "../data/users.ser";

    private ConcurrentHashMap<String, PlayerRecord> users;

    public UserDatabase() {
        users = loadFromFile();
        if (users == null) {
            users = new ConcurrentHashMap<>();
        }
    }

    public synchronized boolean register(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, new PlayerRecord(username, password));
        saveToFile();
        return true;
    }

    public boolean login(String username, String password) {
        PlayerRecord player = users.get(username);
        return player != null && player.password().equals(password);
    }

    private void saveToFile() {
        try {
            File dir = new File("data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
            oos.writeObject(users);
            oos.close();
            System.out.println("💾 Base de dados de utilizadores guardada.");
        } catch (IOException e) {
            System.err.println("❌ Falha ao guardar utilizadores: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<String, PlayerRecord> loadFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return null;

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            ConcurrentHashMap<String, PlayerRecord> loadedUsers = (ConcurrentHashMap<String, PlayerRecord>) ois.readObject();
            ois.close();
            System.out.println("✅ Base de dados de utilizadores carregada.");
            return loadedUsers;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("⚠️ Não foi possível carregar utilizadores: " + e.getMessage());
            return null;
        }
    }

    public record PlayerRecord(String username, String password) implements Serializable {}
}
