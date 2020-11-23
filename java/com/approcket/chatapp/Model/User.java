package com.approcket.chatapp.Model;

public class User {
    private String id;
    private String username;
    private String imagesurl;



    public User(String id, String username, String imagesurl) {
        this.id = id;
        this.username = username;
        this.imagesurl = imagesurl;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImagesUrl() {
        return imagesurl;
    }

    public void setImagesUrl(String imagesurl) {
        this.imagesurl = imagesurl;
    }
}
