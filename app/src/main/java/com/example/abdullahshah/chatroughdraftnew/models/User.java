package com.example.abdullahshah.chatroughdraftnew.models;


import java.util.HashMap;
import java.util.Map;

public class User {

    public String username;

    public String email;
    public String status;
    public String image;
    public String thumb_image;
    public String firstname;
    public String lastname;
    public int followers;
    public int following;

    public User(String username, String email, String status, String image, String thumb_image,
                String firstname, String lastname, int followers, int following) {
        this.username = username;
        this.email = email;
        this.status = status;
        this.image = image;
        this.thumb_image = thumb_image;
        this.firstname = firstname;
        this.lastname = lastname;
        this.followers = followers;
        this.following = following;
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("username", username);
        result.put("email", email);
        result.put("status", status);
        result.put("image", image);
        result.put("thumb_image", thumb_image);
        result.put("first_name", firstname);
        result.put("last_name", lastname);
        result.put("followers", followers);
        result.put("following", following);

        return result;
    }

}
