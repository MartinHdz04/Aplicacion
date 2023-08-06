package com.example.appean.models;

//Clase que servirá de plantilla para los Post
public class Post {

    //Atributos
    private String description, image, category, idUser;
    private long timestamp;

    //Constructor vacio
    public Post(){}

    //Constructor
    public Post(String description, String image, String category, String idUser, long timestamp) {
        this.description = description;
        this.image = image;
        this.category = category;
        this.idUser = idUser;
        this.timestamp = timestamp;
    }

    //Getter y Setter
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
