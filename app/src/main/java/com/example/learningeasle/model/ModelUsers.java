package com.example.learningeasle.model;

public class ModelUsers {
    String Id;
    String Name;
    String Url;
    String email;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public ModelUsers(String id, String name, String url, String email) {
        Id = id;
        Name = name;
        Url = url;
        this.email = email;

    }
}
