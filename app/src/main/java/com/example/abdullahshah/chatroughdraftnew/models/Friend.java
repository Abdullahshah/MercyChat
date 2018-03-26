package com.example.abdullahshah.chatroughdraftnew.models;

/**
 * Created by abdullahshah on 3/18/18.
 */

public class Friend {

    public String username;
    public String status;
    public String image;
    public String thumb_image;
    public String date;

    public Friend() {

    }

    public Friend(String username, String status, String image, String thumb_image, String date) {
        this.username = username;
        this.status = status;
        this.image = image;
        this.thumb_image = thumb_image;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
