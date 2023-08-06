package com.example.appean.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.appean.R;

public class SeleccionFragment extends DialogFragment {

    //Objetos
    private ImageView iv_1, iv_2, iv_3, iv_4;

    //Complementos
    private String category = "";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogoseleccion();
    }

    //Crea y personaliza el AlertDialog
    @SuppressLint("MissingInflatedId")
    private AlertDialog crearDialogoseleccion() {
        //Creamos el builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Un inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //Creamos una vista que tenga el fragment_seleccion en su interior
        View view = inflater.inflate(R.layout.fragment_seleccion, null);

        //Le incorporamos la vista al builder y un botón
        builder.setView(view);
        builder.setPositiveButton("ACEPTAR",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        //Recuperamos los datos del view wn el AlertDialog
        iv_1 = view.findViewById(R.id.iv_1);
        iv_2 = view.findViewById(R.id.iv_2);
        iv_3 = view.findViewById(R.id.iv_3);
        iv_4 = view.findViewById(R.id.iv_4);

        eventosBotones();
        //Devolvemos el alert Dialog
        return builder.create();
    }

    //Método para poner las funcionalidades a las imágenes del Dialog de selección
    private void eventosBotones() {

        iv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "message";
                iv_1.setBackgroundColor(Color.parseColor("#22B04C"));
                iv_2.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                iv_3.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                iv_4.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }
        });

        iv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "meet";
                iv_2.setBackgroundColor(Color.parseColor("#22B04C"));
                iv_1.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                iv_3.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                iv_4.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }
        });

        iv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "location";
                iv_3.setBackgroundColor(Color.parseColor("#22B04C"));
                iv_2.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                iv_1.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                iv_4.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }
        });

        iv_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "Date";
                iv_4.setBackgroundColor(Color.parseColor("#22B04C"));
                iv_2.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                iv_3.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                iv_1.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }
        });
    }

    //Getter de category
    public String getCategory(){return this.category;}

    //Cuando se usa la clase infla la vista con el fragment_seleccion
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seleccion, container, false);
    }

}