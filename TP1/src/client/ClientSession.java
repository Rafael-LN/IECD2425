package client;

import common.UserProfileData;

public class ClientSession {

    private boolean loggedIn;
    private UserProfileData profile;

    public void login(UserProfileData profile) {
        this.profile = profile;
        this.loggedIn = true;
    }

    public void logout() {
        this.profile = null;
        this.loggedIn = false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public UserProfileData getProfile() {
        return profile;
    }

    public void updatePhoto(String newPhotoBase64) {
        if (profile != null) {
            profile = new UserProfileData(
                    profile.username(),
                    profile.age(),
                    profile.nationality(),
                    profile.wins(),
                    profile.losses(),
                    profile.timePlayed(),
                    newPhotoBase64
            );
        }
    }
}
