package com.example.appean.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.appean.R;
import com.example.appean.activities.EditProfileActivity;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.ImageProvider;
import com.example.appean.providers.PostProvider;
import com.example.appean.providers.UserProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.File;


public class ProfileFragment extends Fragment {

    //Objetos
    LinearLayout mLinearLayouyEditProfile;
    View mView;

    //Variables del front
    private TextView tv_username, tv_phone, tv_mail, tv_publicaciones;
    private ImageView im_profile, im_cover;

    //Providers
    private ImageProvider mImageProvider;
    private UserProvider mUserProvider;
    private Authprovider mAuthProvider;
    private PostProvider mPostProvider;

    //Atributos a actualizar
    private String mUsername="", mPhone="", mEmail="";
    private File imagenCover = null, imagenProfile = null;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflar la vista con el fragment profile
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        //Conectar con el front
        mLinearLayouyEditProfile = mView.findViewById(R.id.layoutEditProfile);

        mLinearLayouyEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });

        //Providers
        mAuthProvider = new Authprovider();
        mUserProvider = new UserProvider();
        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();

        //Objetos de la clase con el front
        tv_mail = mView.findViewById(R.id.tv_mail);
        tv_phone = mView.findViewById(R.id.num_phone);
        tv_publicaciones = mView.findViewById(R.id.num_publicaciones);
        tv_username = mView.findViewById(R.id.tv_username);
        im_cover = mView.findViewById(R.id.im_cover);
        im_profile = mView.findViewById(R.id.im_profile);

        //Poner los datos del usuario
        getUser();
        getNumberPost();
        return mView;
    }
    //Ir a editar el perfil
    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    //Método para recuperar la información del usuario
    private void getUser(){
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                        Picasso.with(getContext()).load(imProfile).into(im_profile);
                    }
                    //Recuperar link de la imágen del cover y ponerla a través de internet
                    if(documentSnapshot.contains("imageProfile")){
                        String imCover = documentSnapshot.getString("imageCover");
                        Picasso.with(getContext()).load(imCover).into(im_cover);
                    }
                }
            }
        });
    }

    private void getNumberPost(){
        String id = mAuthProvider.getUid();
        mPostProvider.getNumberPostByUser(id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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