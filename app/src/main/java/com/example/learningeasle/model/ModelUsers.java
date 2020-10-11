package com.example.learningeasle.model;

public class ModelUsers {
    String Id;
    String Name;
    String Url;
    String email;
    String phone;
    String status;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ModelUsers(String id, String name, String url, String email, String phone, String status) {
        Id = id;
        Name = name;
        Url = url;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }
}
