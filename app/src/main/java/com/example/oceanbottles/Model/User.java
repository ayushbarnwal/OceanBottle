package com.example.oceanbottles.Model;

public class User {

    String Name;
    String profilePic;
    String UserId;

    public User() {
    }

    public User(String Name, String profilePic) {
        this.Name = Name;
        this.profilePic = profilePic;
    }


    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }
}
