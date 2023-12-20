package com.example.appean.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appean.R;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.ImageProvider;
import com.example.appean.providers.PostProvider;
import com.example.appean.providers.UserProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.github.florent37.shapeofview.shapes.CircleView;

public class UserProfileActivity extends AppCompatActivity {

    //Variables del front
    private TextView tv_username, tv_phone, tv_mail, tv_publicaciones;
    private ImageView im_profile, im_cover;
    private CircleView mCircleViewBack;

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
        im_cover = findViewById(R.id.im_cover_AUP);
        im_profile = findViewById(R.id.im_profile_AUP);
        mCircleViewBack = findViewById(R.id.iv_back_AUP);

        //Listener para ir atrás
        mCircleViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUser();
        getNumberPost();
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
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Asigna el número del tamaño de la busqueda en la base de datos
                int numberPost = queryDocumentSnapshots.size();

                //colocar el número en la pantalla
                tv_publicaciones.setText(String.valueOf(numberPost));
            }
        });
    }
}