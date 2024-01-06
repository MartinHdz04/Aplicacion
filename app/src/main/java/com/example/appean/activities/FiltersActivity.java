package com.example.appean.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appean.R;
import com.example.appean.adapters.PostsAdapter;
import com.example.appean.models.Post;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FiltersActivity extends AppCompatActivity {

    //Extras
    private String mExtracategory;

    //Objetos
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private TextView tv_cantResultados;

    //Provider
    private Authprovider mAuthProvider;
    private PostProvider mPostProvider;

    //Adapter
    private PostsAdapter mPostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        mExtracategory = getIntent().getStringExtra("category");

        //Instanciación de los provider
        mAuthProvider = new Authprovider();
        mPostProvider = new PostProvider();

        //Conexión con el front
        mRecyclerView = findViewById(R.id.recyclerView_AF);
        mToolbar = findViewById(R.id.toolbar);
        tv_cantResultados = findViewById(R.id.tv_resultados_AF);

        //Definir el campo de acción del RecyclerView
        //En este caso será a dos columnas
        GridLayoutManager gridLayoutManager = new GridLayoutManager(FiltersActivity.this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //Instanciación del Toolbar
        setSupportActionBar(mToolbar);
        //Le colocamos un texto en vacio para no mostrar nada
        getSupportActionBar().setTitle("Filtros");
        //Añade el botón que va hacia la actividad padre de donde está el actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Método que indica la cantidad de resultados
        getNumberPost();
    }

    //Cuando inicia
    @Override
    public void onStart() {
        super.onStart();
        //Consulta a firebase y devuelve los Post
        Query query = mPostProvider.getPostByCategory(this.mExtracategory);

        //Pone los post en un formato para el adapter
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        //Le pasamos el formato al adapter
        mPostAdapter = new PostsAdapter(options, FiltersActivity.this);

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

    //Método que actualiza el texto con la cantidad de resultados
    private void getNumberPost(){
        mPostProvider.getPostByCategory(mExtracategory).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int cant = queryDocumentSnapshots.size();
                tv_cantResultados.setText(String.valueOf(cant));
            }
        });

    }

}