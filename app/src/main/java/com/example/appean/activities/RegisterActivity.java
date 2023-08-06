package com.example.appean.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appean.R;
import com.example.appean.models.User;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.UserProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.florent37.shapeofview.shapes.CircleView;

//Clase para registrarse con correo y demás
public class RegisterActivity extends AppCompatActivity {
    //Objetos
    private CircleView mCircleView;
    private TextInputEditText mTextInputReguser;
    private TextInputEditText mTextInputRegcorreo;
    private TextInputEditText mTextInputRegpass;
    private TextInputEditText mTextInputRegconf;
    private TextInputEditText mTextInputPhone;
    private Button mBtn_registrarse;

    //Providers
    private Authprovider mAuthProvider;
    private UserProvider mUserprovider;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Conexión con el front
        mCircleView = findViewById(R.id.c_iv_back);
        mTextInputReguser = findViewById(R.id.ti_et_regUser);
        mTextInputRegcorreo = findViewById(R.id.ti_et_regCorreo);
        mTextInputRegpass = findViewById(R.id.ti_et_regPass);
        mTextInputRegconf = findViewById(R.id.ti_et_confPass);
        mTextInputPhone = findViewById(R.id.ti_et_phone);
        mBtn_registrarse = findViewById(R.id.btn_registrarse);

        //Instanciación de los provider
        mAuthProvider = new Authprovider();
        mUserprovider = new UserProvider();

        mBtn_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPrincipal();
            }
        });
    }

    //Método para ir al la pantalla principal
    private void irPrincipal (){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Método para registrar un usuario
    private void register (){

        //Recuperar los datos de entrada
        String username = mTextInputReguser.getText().toString();
        String email = mTextInputRegcorreo.getText().toString();
        String password = mTextInputRegpass.getText().toString();
        String Conf_password = mTextInputRegconf.getText().toString();
        String phone = mTextInputPhone.getText().toString();

        //Validación de campos
        if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !Conf_password.isEmpty() && !phone.isEmpty()){
            if(isEmailValid(email)){
                if(password.equals(Conf_password)){
                    if(password.length() >= 6){
                        createUser(username, email, password, phone);
                    }else{
                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(this, "Has llenado todos los campos, y el email es válido", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Has llenado todos los campos, pero email no es válido", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    //Método para verificar si el texto es un email, osea caracteres, números, @ y .compañia
    public static boolean isEmailValid (String email){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //Método para crear un usuario
    private void createUser (final String username, final String email, String password, final String phone){
        //Registrar el usuario en firebaseAuth
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Recupera el id de la sesión que se acaba de iniciar
                    String id = mAuthProvider.getUid();

                    //Crea un usuario y completa los atributos
                    User user = new User();
                    user.setId(id);
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setPhone(phone);
                    user.setTimestamp(new Date().getTime());

                    //Crea el usuario en firebaseStorage
                    mUserprovider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Si salió bien, lleva al home
                            if(task.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                //Limpia el historial de activities
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(RegisterActivity.this, "No se pudo almacenar el usuario en la base de datos", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    Toast.makeText(RegisterActivity.this, "Se registró el usuario", Toast.LENGTH_SHORT);
                }else{
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}