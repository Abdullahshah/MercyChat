package com.example.abdullahshah.chatroughdraftnew.models;

public class Community {

    public String title;
    public String description;

    public Community() {}

    public Community(String title, String description) {
        this.title = title;
        this.description = description;
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
}
