package com.example.appean.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.appean.R;
import com.example.appean.fragments.ChatsFragment;
import com.example.appean.fragments.FiltersFragment;
import com.example.appean.fragments.HomeFragment;
import com.example.appean.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

//Activity que muestra los principales espacios donde se va a interactuar
public class HomeActivity extends AppCompatActivity {
    //Se crea un navigationBar que es la barra de navegación que se ve abajo en la app
    private BottomNavigationView bottomNavigation;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Siempre que inicie Home se pone este fragment
        openFragment(new HomeFragment());

        //Se relaciona el objeto con el front
        bottomNavigation = findViewById(R.id.navigation_bar);

        //Listener para saber en que opción esta parado del navigationBar
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.it_home:
                        openFragment(new HomeFragment());
                        return true;
                    case R.id.it_chats:
                        openFragment(new ChatsFragment());
                        return true;
                    case R.id.it_profile:
                        openFragment(new ProfileFragment());
                        return true;
                    case R.id.it_filters:
                        openFragment(new FiltersFragment());
                        return true;
                }
                return false;
            }
        });
    }

    //Método para poner un fragment en la pantalla de la activity
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}