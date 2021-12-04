package com.example.proyectoappnativa.Models;

public class PeoppleAcepted {
    private String id;
    private String name;
    private String phone;
    private String postulation;
    private String imageURL;

    public PeoppleAcepted(String id, String name, String phone, String postulation, String imageURL) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.postulation = postulation;
        this.imageURL = imageURL;
    }

    public PeoppleAcepted() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostulation() {
        return postulation;
    }

    public void setPostulation(String postulation) {
        this.postulation = postulation;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
