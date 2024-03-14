package com.msa.testingmsa.Model;

public class User {
    private String Username;
    private String id;
    private String imageURL;
    private String status;
    private String search;
    private String emailID;

    public User(String Username, String id, String imageURL, String status, String search, String emailID) {
        this.Username = Username;
        this.id = id;
        this.imageURL = imageURL;
        this.status   = status;
        this.search   = search;
        this.emailID = emailID;
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
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getSearch() { return search; }

    public void setSearch(String search) { this.search = search; }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }
}
