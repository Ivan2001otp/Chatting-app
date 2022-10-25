package com.example.clonechat.Models;

public class User {
    private String profilePic,Name,Email,Id,Password,LastMessage,status;
    private boolean isMale = false;
    private boolean isFemale = false;

    public User(String profilePic, String name, String id, String password,String email, String lastMessage,String status) {
        this.profilePic = profilePic;
        Name = name;
        this.Email = email;
        Id = id;
        Password = password;
        this.status = status;
        LastMessage = lastMessage;
    }


    public User(){}

    public User(String name,String password,String email){
        this.Name = name;
        this.Email = email;
        this.Password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getMaleBoolean(){return isMale;}

    public void setMaleBoolean(boolean maleBoolean){
        this.isMale = maleBoolean;
    }

    public boolean getFemaleBoolean(){return isFemale;}

    public void setFemaleBoolean(boolean femaleBoolean){
        this.isFemale = femaleBoolean;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getLastMessage() {
        return LastMessage;
    }



    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }
}
