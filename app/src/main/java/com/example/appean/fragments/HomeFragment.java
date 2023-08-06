package com.example.appean.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.appean.R;
import com.example.appean.activities.MainActivity;
import com.example.appean.activities.PostActivity;
import com.example.appean.adapters.PostsAdapter;
import com.example.appean.models.Post;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

//Clase donde aparecerán las publicaciones
public class HomeFragment extends Fragment {
    //Objetos
    View mView;
    FloatingActionButton mFab;
    Toolbar mToolbar;
    RecyclerView mRecyclerView;

    //Provider
    Authprovider mAuthProvider;
    PostProvider mPostProvider;

    //Adapter
    PostsAdapter mPostAdapter;



    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Instanciación de los provider
        mAuthProvider = new Authprovider();
        mPostProvider = new PostProvider();

        //Rellenar la vista con el fragmentHome
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        //Conexión con el front
        mRecyclerView = mView.findViewById(R.id.recycleViewHome);
        mFab = mView.findViewById(R.id.fab);
        mToolbar = mView.findViewById(R.id.toolbar);

        //Definir el campo de acción del RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PostActivity.class);
                startActivity(intent);
            }
        });

        //Instanciación del Toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Publicaciones");
        setHasOptionsMenu(true);

        return mView;
    }

    //Cuando inicia
    @Override
    public void onStart() {
        super.onStart();
        //Consulta a firebase y devuelve los Post
        Query query = mPostProvider.getAll();

        //Pone los post en un formato para el adapter
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        //Le pasamos el formato al adapter
        mPostAdapter = new PostsAdapter(options, getContext());

        //Ponemos los post en el recyclerView
        mRecyclerView.setAdapter(mPostAdapter);

        //Comienza a detectar cambio en la base de datos
        mPostAdapter.startListening();
    }

    //Cuando salga de la app para de pedir información al firestore Database
    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
    }

    //Infla el toolbar con el archivo main_menu creado
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Método para seleccionar la opción del toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_logout){
            logOut();
        }
        return true;
    }

    //Método para salir de la sesión
    private void logOut() {
        mAuthProvider.logOut();
        //Volver al inicio
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}