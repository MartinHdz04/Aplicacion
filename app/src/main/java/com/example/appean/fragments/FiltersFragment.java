package com.example.appean.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appean.R;
import com.example.appean.activities.FiltersActivity;

//Clase donde se harán los respectivos filtros
public class FiltersFragment extends Fragment {

    //Objetos
    private View mView;
    private CardView cv_ubicacion, cv_reunion, cv_evento, cv_mensaje;

    public FiltersFragment() {

    }


    public static FiltersFragment newInstance(String param1, String param2) {
        FiltersFragment fragment = new FiltersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Rellenar la vista con el fragment Filters
        mView = inflater.inflate(R.layout.fragment_filters, container, false);

        //conexión con el front
        cv_evento = mView.findViewById(R.id.cv_evento_FF);
        cv_reunion = mView.findViewById(R.id.cv_reunion_FF);
        cv_ubicacion = mView.findViewById(R.id.cv_ubicacion_FF);
        cv_mensaje = mView.findViewById(R.id.cv_mensaje_FF);

        cv_ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilter("ubicacion");
            }
        });

        cv_reunion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilter("reunion");
            }
        });

        cv_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilter("evento");
            }
        });

        cv_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilter("mensaje");
            }
        });


        return mView;
    }

    //Método para ir a la actividad de filtros
    private void goToFilter (String categoria){
        Intent intent = new Intent(getContext(), FiltersActivity.class);
        intent.putExtra("category", categoria);
        startActivity(intent);

    }
}