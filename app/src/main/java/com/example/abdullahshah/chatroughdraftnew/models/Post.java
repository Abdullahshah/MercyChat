package com.example.abdullahshah.chatroughdraftnew.models;

public class Post {
    public String uID;
    public String author;
    public String title;
    public String description;
    public String image;
    public String profileImage;

    public Post(){}

    public Post(String uID, String author, String title, String description, String image, String profileImage) {
        this.uID = uID;
        this.author = author;
        this.title = title;
        this.description = description;
        this.image = image;
        this.profileImage = profileImage;
    }


    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
