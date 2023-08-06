package com.example.appean.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.appean.R;
import com.example.appean.models.User;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.UserProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//Clase para completar datos de un usuario que ingresó con google
public class CompleteProfileActivity extends AppCompatActivity {
    //Creación de variables del front
    private TextInputEditText mTextInputReguser;
    private TextInputEditText mTextInputPhone;
    private Button mBtn_registrarse;

    //Creación de Providers
    private Authprovider mAuthProvider;
    private UserProvider mUserProvider;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        //Relación entre los objetos de la clase y los del front
        mTextInputReguser = findViewById(R.id.ti_et_regUser);
        mTextInputPhone = findViewById(R.id.ti_et_phone);
        mBtn_registrarse = findViewById(R.id.btn_registrarse);

        //Intancia de los provider
        mAuthProvider = new Authprovider();
        mUserProvider = new UserProvider();

        mBtn_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });


    }

    private void register (){
        //Variables que recuperan el texto de la pantalla
        String username = mTextInputReguser.getText().toString();
        String phone = mTextInputPhone.getText().toString();

        //Si los campos estás llenos entra
        if(!username.isEmpty() && !phone.isEmpty()){
            UpdateUser(username, phone);
        }else{
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    //Método para actualizar los campos de un usuario
    private void UpdateUser(final String username, final String phone){
        //Recupera el id de la sesión abierta
        String id = mAuthProvider.getUid();

        //Creación de un usuario que será el actualizado
        User user = new User();

        //Completar los atributos del ususario
        user.setUsername(username);
        user.setId(id);
        user.setPhone(phone);
        user.setTimestamp(new Date().getTime());

        //Actualizar y añadir un listener cuando este esté completo
        mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Si salió bien lleva al home
                if (task.isSuccessful()) {
                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(CompleteProfileActivity.this, "No se pudo almacenar el usuario en la base de datos", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}