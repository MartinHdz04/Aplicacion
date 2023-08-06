package com.example.appean.providers;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

//Clase para manejar la autenticación
public class Authprovider {

    //Objetos
    private FirebaseAuth mAuth;

    //Constructor
    public Authprovider(){
        //Apenas se crea, obtiene la instancia de la autenticación
        this.mAuth = FirebaseAuth.getInstance();
    }

    //Método para iniciar sesión con credenciales
    public Task<AuthResult> login (String email, String password) {
        return this.mAuth.signInWithEmailAndPassword(email, password);
    }

    //Método para iniciar sesión con google
    public Task<AuthResult> googleLogin (GoogleSignInAccount googleSignInAccount) {
        //Se obtiene un id a partir de la cuenta
        String idToken = googleSignInAccount.getIdToken();

        //Se obtiene una credencial si el id existe
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        //Ingresa con la credencial
        return this.mAuth.signInWithCredential(credential);
    }

    //Obtiene el id del usuario
    public String getUid(){
        //Si el usuario existe
        if (this.mAuth.getCurrentUser() != null){
            return this.mAuth.getCurrentUser().getUid();
        }else{
            return null;
        }
    }

    //Obtiene el email
    public String getEmail () {
        //Si el usuario existe
        if (this.mAuth.getCurrentUser() != null){
            return this.mAuth.getCurrentUser().getEmail();
        }else{
            return null;
        }
    }

    //Registrar en firebase Auth con credenciales
    public Task<AuthResult> register (String email, String password){
        return this.mAuth.createUserWithEmailAndPassword(email, password);
    }

    //Método para salir de la sesión
    public void logOut() {
        if (mAuth != null){
            mAuth.signOut();
        }
    }

    //Getter del la sesión
    public FirebaseUser getUserSession(){
        if (this.mAuth.getCurrentUser() != null){
            return this.mAuth.getCurrentUser();
        }else{
            return null;
        }
    }
}

