package client;

public class ClientSession {

    private String username;
    private boolean loggedIn;
    private String photoBase64;
    private int age;
    private String nationality;
    private int wins;
    private int losses;
    private long timePlayed;

    public void login(String username) {
        this.username = username;
        this.loggedIn = true;
    }

    public void logout() {
        this.username = null;
        this.photoBase64 = null;
        this.age = 0;
        this.nationality = null;
        this.wins = 0;
        this.losses = 0;
        this.timePlayed = 0;
        this.loggedIn = false;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public long getTimePlayed() {
        return timePlayed;
    }

    public void setTimePlayed(long timePlayed) {
        this.timePlayed = timePlayed;
    }
}
