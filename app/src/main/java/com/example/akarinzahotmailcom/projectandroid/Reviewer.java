package com.example.akarinzahotmailcom.projectandroid;

public class Reviewer {
    private String title;
    private String description;
    private String imageUrl;
    private String username;

    public Reviewer(String title, String description, String imageUrl, String username){
        this.title =title;
        this.description = description;
        this.imageUrl= imageUrl;
        this.username = username;
    }
    public Reviewer(){

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsername() {
        return username;
    }
}
