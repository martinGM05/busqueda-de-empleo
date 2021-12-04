package com.example.proyectoappnativa.Models.Offline;

import java.io.Serializable;

public class PostulationOff implements Serializable {

    private String id;
    private String name;
    private String company;
    private String description;
    private String kerywords;

    public PostulationOff(String id, String name, String company, String description, String kerywords) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.description = description;
        this.kerywords = kerywords;
    }

    public PostulationOff() {
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKerywords() {
        return kerywords;
    }

    public void setKerywords(String kerywords) {
        this.kerywords = kerywords;
    }

}
