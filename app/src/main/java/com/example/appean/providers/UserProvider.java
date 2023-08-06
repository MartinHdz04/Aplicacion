package com.example.appean.providers;

import com.example.appean.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProvider {
    //Atributos
    private CollectionReference mCollection;

    //Constructor
    public UserProvider () {
        //Al iniciar apunta a la colección Users
        this.mCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    //Getter del User
    public Task<DocumentSnapshot> getUser (String id){
        //Devuelve el usuario que encuentre con el id
        return this.mCollection.document(id).get();
    }

    //Crear un usuario
    public Task<Void> create(User user){
        //Crea un usuario en el storage y lo guarda con el id
        return this.mCollection.document(user.getId()).set(user);
    }

    //Actualizar un usuario
    public Task<Void> update (User user) {
        //Crea un map y lo rellena con los atributos del User
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("phone", user.getPhone());
        map.put("timestamp", new Date().getTime());
        map.put("imageProfile", user.getImageProfile());
        map.put("imageCover", user.getImageCover());

        //Reemplaza el usuario al que le corresponda el id
        return this.mCollection.document(user.getId()).update(map);
    }
}
