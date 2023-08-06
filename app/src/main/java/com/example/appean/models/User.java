package com.example.appean.models;

//Clase que servirá de plantilla de la forma de los usuarios
public class User {

    //Atributos
    private String id;
    private String email;
    private String username;
    private String phone;
    private String password;
    private String imageProfile;
    private String imageCover;
    private long timestamp;

    //Constructor vacio
    public  User(){}

    //Constructor
    public User(String id, String email, String username, String phone, String password, String imageProfile, String imageCover, long timestamp) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.imageProfile = imageProfile;
        this.imageCover = imageCover;
        this.timestamp = timestamp;
    }

    //Getter y Setter
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getImageProfile() {return imageProfile;}
    public void setImageProfile(String imageProfile) {this.imageProfile = imageProfile;}

    public String getImageCover() {return imageCover;}
    public void setImageCover(String imageCover) {this.imageCover = imageCover;}

    public long getTimestamp() {return timestamp;}
    public void setTimestamp(long timestamp) {this.timestamp = timestamp;}
}
