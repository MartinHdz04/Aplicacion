package com.example.appean.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appean.R;
import com.example.appean.adapters.MyPostsAdapter;
import com.example.appean.adapters.PostsAdapterView;
import com.example.appean.models.Post;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.ImageProvider;
import com.example.appean.providers.PostProvider;
import com.example.appean.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.github.florent37.shapeofview.shapes.CircleView;

public class UserProfileActivity extends AppCompatActivity {

    //Variables del front
    private TextView tv_username, tv_phone, tv_mail, tv_publicaciones, tv_cantPost;
    private ImageView im_profile, im_cover;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;

    //Providers
    private ImageProvider mImageProvider;
    private UserProvider mUserProvider;
    private Authprovider mAuthProvider;
    private PostProvider mPostProvider;

    //Atributos a actualizar
    private String mUsername="", mPhone="", mEmail="";
    private File imagenCover = null, imagenProfile = null;

    //IntentExtra
    private String idUserExtra;

    //Adapter
    private PostsAdapterView myPostsAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //Recuperamos el id del usuario
        idUserExtra = getIntent().getStringExtra("idUser");

        //Providers
        mAuthProvider = new Authprovider();
        mUserProvider = new UserProvider();
        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();

        //Objetos de la clase con el front
        tv_mail = findViewById(R.id.tv_mail_AUP);
        tv_phone = findViewById(R.id.num_phone_AUP);
        tv_publicaciones = findViewById(R.id.num_publicaciones_AUP);
        tv_username = findViewById(R.id.tv_username_AUP);
        tv_cantPost = findViewById(R.id.tv_cantPost_AUP);
        im_cover = findViewById(R.id.im_cover_AUP);
        im_profile = findViewById(R.id.im_profile_AUP);
        mRecyclerView = findViewById(R.id.recycleViewMyPost_AUP);
        mToolbar = findViewById(R.id.toolbar_AUP);

        //Definir el campo de acción del RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //Instanciación del Toolbar
        setSupportActionBar(mToolbar);
        //Le colocamos un texto en vacio para no mostrar nada
        getSupportActionBar().setTitle("");
        //Añade el botón que va hacia la actividad padre de donde está el actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getUser();
        getNumberPost();
    }

    /*
    Se sobreescribre esta función debido que al volver al PostDeatailActivity
    no funciona ya que se neceitan unos IntentExtra que vienen desde el home y se pierden al
    volver a cargar la actividad de esta manera, es por eso que la acción que hace para recargar la actividad
    es un finish, así no se pierden los extra
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Si al oprimir la actividad a la que se dirige es la actividad padre de la actividad
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Consulta a firebase y devuelve los Post
        Query query = mPostProvider.getPostByUser(idUserExtra);

        //Pone los post en un formato para el adapter
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        //Le pasamos el formato al adapter
        myPostsAdapter = new PostsAdapterView(options, UserProfileActivity.this);

        //Ponemos los post en el recyclerView
        mRecyclerView.setAdapter(myPostsAdapter);

        //Comienza a detectar cambio en la base de datos
        myPostsAdapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        //Deja de pedir datos a la base de datos
        myPostsAdapter.stopListening();
    }


    //Método para recuperar la información del usuario
    private void getUser(){
        mUserProvider.getUser(idUserExtra).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    //Recuperar nombre y rellenar campo
                    if(documentSnapshot.contains("username")){
                        mUsername = documentSnapshot.getString("username");
                        tv_username.setText(mUsername);
                    }

                    //Recuperar telefono y rellenar campo
                    if(documentSnapshot.contains("phone")){
                        mPhone = documentSnapshot.getString("phone");
                        tv_phone.setText(mPhone);
                    }

                    //Recuperar el correo y rellenar campo
                    if(documentSnapshot.contains("email")){
                        mEmail = documentSnapshot.getString("email");
                        tv_mail.setText(mEmail);
                    }

                    //Recuperar link de la imágen del profile y ponerla a través de internet
                    if(documentSnapshot.contains("imageProfile")){
                        String imProfile = documentSnapshot.getString("imageProfile");
                        Picasso.with(UserProfileActivity.this).load(imProfile).into(im_profile);
                    }
                    //Recuperar link de la imágen del cover y ponerla a través de internet
                    if(documentSnapshot.contains("imageProfile")){
                        String imCover = documentSnapshot.getString("imageCover");
                        Picasso.with(UserProfileActivity.this).load(imCover).into(im_cover);
                    }
                }
            }
        });
    }

    private void getNumberPost(){
        String id = mAuthProvider.getUid();
        mPostProvider.getPostByUser(id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            //Hace una consulta en tiempo real, lo que significa que los cambio los hace de forma instantanea
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Asigna el número del tamaño de la busqueda en la base de datos
                int numberPost = queryDocumentSnapshots.size();

                //colocar el número en la pantalla
                tv_publicaciones.setText(String.valueOf(numberPost));

                //Valor para el textView de la cantidad de Posts
                if(numberPost>0){
                    tv_cantPost.setText(String.valueOf(numberPost) + " publicaciones");
                }else{
                    tv_cantPost.setText("No hay publicaciones");
                }
            }
        });
    }
}