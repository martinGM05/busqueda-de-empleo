package Models;

import java.sql.Blob;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String id;
    private String name;
    private String email;
    private String type;
    private String description;
    private String imageURL;


    public User() {
    }

    public User(String id, String name, String email, String type, String description, String imageURL) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
        this.description = description;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getEmail() {
        return email;
    }

    public String getImageURL() { return imageURL; }

    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public void setEmail(String email) {
        this.email = email;
    }

}
