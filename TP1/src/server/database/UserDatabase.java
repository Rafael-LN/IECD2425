package server.database;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserDatabase implements Serializable {

    private static final String FILE_PATH = "TP1/data/users.ser";

    private ConcurrentHashMap<String, PlayerRecord> users;

    public UserDatabase() {
        users = loadFromFile();
        if (users == null) {
            users = new ConcurrentHashMap<>();
        }
    }

    public synchronized boolean register(String username, String password, int age, String nationality, String photoBase64) {
        if (users.containsKey(username)) return false;
        users.put(username, new PlayerRecord(username, password, age, nationality, photoBase64, 0, 0, 0));
        saveToFile();
        return true;
    }

    public boolean login(String username, String password) {
        PlayerRecord player = users.get(username);
        return player != null && player.password().equals(password);
    }

    private void saveToFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
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

    public synchronized boolean updatePhoto(String username, String photoBase64) {
        PlayerRecord player = users.get(username);
        if (player == null) return false;

        users.put(username, new PlayerRecord(
                player.username(),
                player.password(),
                player.age(),
                player.nationality(),
                photoBase64,
                player.wins(),
                player.losses(),
                player.timePlayed()
        ));
        saveToFile();
        return true;
    }

    public synchronized PlayerRecord getPlayer(String username) {
        return users.get(username);
    }
}
