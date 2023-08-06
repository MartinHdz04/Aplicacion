package com.example.appean.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appean.R;
import com.example.appean.models.User;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.UserProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

//Clase de inicios de sesión y registros
public class MainActivity extends AppCompatActivity {
    //Variable para referenciar algún mensaje en el log
    private static final String TAG = "GoogleActivity";

    //Objetos
    private TextView mTextViewRegister;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputPassword;
    private Button mBtn_inSesion;
    private SignInButton mBtn_google;

    //Providers
    private Authprovider mAuthProvider;
    private GoogleSignInClient mGoogleSignInClient;
    private UserProvider mUserProvider;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Conectar los objetos con el front
        mTextViewRegister = findViewById(R.id.tv_register);
        mTextInputEmail = findViewById(R.id.ti_et_correo);
        mTextInputPassword = findViewById(R.id.ti_et_password);
        mBtn_inSesion = findViewById(R.id.btn_inSesion);
        mBtn_google = findViewById(R.id.btn_loginGoogle);

        //Inicio de sesión con google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //Creación del cliente para iniciar con google
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Instancia para los provider
        mAuthProvider = new Authprovider();
        mUserProvider = new UserProvider();

        mBtn_inSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        mBtn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Launcher que intenta iniciar sesión con google
                resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
            }
        });

        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irRegistro();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Si ya hay una sesión iniciada, ir directamente al home
        if (mAuthProvider.getUserSession() != null){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    //Sign In
    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            Log.w(TAG, "Google sign in failed", e);
                        }
                    }
                }
            }
    );

    //Ingresar mediante una cuenta de google
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        mAuthProvider.googleLogin(account).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String id = mAuthProvider.getUid();
                    checkUserExist(id);
                }else{
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                }
            }
        });
    }

    //Método para ir a la activity de registro
    private void irRegistro (){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    //Ingresar mediante credenciales
    private void login(){
        //Recuperar los datos de los campos de texto
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        //Ingresar con email y contraseña
        mAuthProvider.login(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Si existe entra
                if(task.isSuccessful()){
                    //Ir a home
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    //Limpiar historial de activities para no poder volver
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this, "Las credenciales no son correctas", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Verificar que el usuario exista
    private void checkUserExist(final String id){

        //Obtener un usuario si existe con el id con el cual quedó guardado en firebase
        mUserProvider.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override

            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Si exite ir al home activity
                if(documentSnapshot.exists()){
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                //De lo contrario crearlo
                else{
                    //Obtener el email del auth
                    String email = mAuthProvider.getEmail();

                    //Crear un usuario y completar con los datos disponibles aun
                    User user = new User();
                    user.setEmail(email);
                    user.setId(id);

                    //Crear el usuario en firebase
                    mUserProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Si salió bien, entrar al home
                            if(task.isSuccessful()){
                                Intent intent = new Intent(MainActivity.this, CompleteProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(MainActivity.this, "No se pudo almacenar la información del usuario", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }
        );
    }
}