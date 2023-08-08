package com.example.appean.providers;

import com.example.appean.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

//Clase que ayudará al manejo de los Post
public class PostProvider {

    //Atributos
    private CollectionReference mCollection;
    private String id = "";

    //Constructor
    public PostProvider (){
        //Al iniciar apunta a la colección Post
        this.mCollection = FirebaseFirestore.getInstance().collection("Post");
    }

    //Método para guardar los Post el firebase storage
    public Task<Void> save (Post post){
        return this.mCollection.document().set(post);
    }

    //Setter de ID
    public void setId(String id) {
        this.id = id;
    }

    //Consultar a la base de datos y devolver los datos en orden de subida
    public Query getAll (){
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING);
    }

    //Consultar el número de publicaciones de un usuario por medio del id
    public Query getNumberPostByUser(String id){
        return mCollection.whereEqualTo("idUser", id);
    }

    //Consultar la colección y recuperar el documento con cierto id
    public Task<DocumentSnapshot> getPostById(String id){
        return mCollection.document(id).get();
    }
}
