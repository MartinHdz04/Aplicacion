package com.example.appean.providers;

import com.example.appean.models.Like;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LikeProvider {
    private CollectionReference mCollection;

    public LikeProvider(){
        this.mCollection = FirebaseFirestore.getInstance().collection("Likes");
    }

    public Task<Void> create (Like like){
        DocumentReference document = mCollection.document();

        //Ingresar el atributo de id, si este no existe
        if(like.getId()==null){
            like.setId(document.getId());
        }

        return document.set(like);
    }

    public Task<Void> delete (String id){
        return mCollection.document(id).delete();
    }

    public Query getLikeByPostAndUser(String idPost, String idUser){
        return mCollection.whereEqualTo("idPost", idPost).whereEqualTo("idUser", idUser);
    }

    public Query getLikesByPost(String idPost){
        return mCollection.whereEqualTo("idPost", idPost);
    }
}
