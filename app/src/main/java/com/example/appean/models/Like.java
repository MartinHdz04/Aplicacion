package com.example.appean.models;

public class Like {
    private String idPost, idUser, id;
    private Long timestamp;

    public Like(){};

    public Like(String idPost, String idUser, String id, Long timestamp) {
        this.idPost = idPost;
        this.idUser = idUser;
        this.id = id;
        this.timestamp = timestamp;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
