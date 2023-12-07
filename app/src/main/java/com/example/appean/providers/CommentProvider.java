package com.example.appean.providers;

import com.example.appean.models.Comment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommentProvider {

    //Atributos
    private CollectionReference mCollection;

    //Constructor
    public CommentProvider(){
        this.mCollection = FirebaseFirestore.getInstance().collection("Comments");
    }

    //Método para crear un comentario y guardarlo en firebase
    public Task<Void> create (Comment comment){
        //Ubicamos el documento y subimos el comment
        return this.mCollection.document().set(comment);
    }

    //Consultar la colección y recuperar los elementos que tengan el id de un post específico
    public Query getCommentByPost(String id){
        return this.mCollection.whereEqualTo("idPost", id);
    }

    public Query getAll (){
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING);
    }
}
