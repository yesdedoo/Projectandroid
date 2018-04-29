package com.example.akarinzahotmailcom.projectandroid;


//Getter Setter class
//Class for reviewer to take an action from doing the method
public class Reviewer {
    private String Title;
    private String Description;
    private String ImageUrl;
    private String Username;

    public Reviewer(String title, String description, String imageUrl, String username){
        this.Title =title;
        this.Description = description;
        this.ImageUrl= imageUrl;
        this.Username = username;
    }
    public Reviewer(){

    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.ImageUrl = imageUrl;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getUsername() {
        return Username;
    }
}
