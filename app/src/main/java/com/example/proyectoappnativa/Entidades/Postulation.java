package com.example.proyectoappnativa.Entidades;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Postulation implements Serializable {
    private String id;
    private String name;
    private String image;
    private String description;
    private String company;
    private double lat;
    private double lont;
    private List<String> postulantes;

    public Postulation() {
    }

    public Postulation(String id, String name, String image, String description, double lat, double lont, List<String> postulantes) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.lat = lat;
        this.lont = lont;
        this.company = company;
        this.postulantes = postulantes;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLont() {
        return lont;
    }

    public void setLont(double lont) {
        this.lont = lont;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<String> getPostulantes() {
        return postulantes;
    }

    public void setPostulantes(List<String> postulantes) {
        this.postulantes = postulantes;
    }
}
