package com.example.appean.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.appean.R;
import com.example.appean.models.Post;
import com.example.appean.models.User;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.ImageProvider;
import com.example.appean.providers.UserProvider;
import com.example.appean.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import io.github.florent37.shapeofview.shapes.CircleView;

//Clase para editar los datos de un usuario creado
public class EditProfileActivity extends AppCompatActivity {

    //Variables del front
    private CircleView mCircleViewBack;
    private ImageView mImageViewCover, mCircleViewProfile;
    private TextInputEditText mTextInputUsername, mTextInputPhone;
    private Button mBtnEditProfile;

    //Providers
    private ImageProvider mImageProvider;
    private UserProvider mUserProvider;
    private Authprovider mAuthProvider;

    //Variables para completar acciones
    private AlertDialog.Builder mBuilderSelectorProfile, mBuilderSelectorCover;
    private CharSequence options[];
    private String lugar = "";
    private int valor = 0, valorProfile = 0, valorCover = 0;

    //Variables para el manejo de las imágenes
    private String mAbsolutePhotoPath, mPhotoPath;
    private Intent data;
    private File mImageFile;

    //Atributos a actualizar
    private String mUsername="", mPhone="";
    private File imagenCover = null, imagenProfile = null;
    private Intent dataCover = null, dataProfile = null;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Providers
        mAuthProvider = new Authprovider();
        mUserProvider = new UserProvider();
        mImageProvider = new ImageProvider();

        //Poner los datos del usuario
        getUser();

        //AlertDialog
        mBuilderSelectorProfile = new AlertDialog.Builder(this);
        mBuilderSelectorProfile.setTitle("Selecciona una opción");
        options = new CharSequence[]{"Abrir galeria", "Tomar foto"};

        mBuilderSelectorCover = new AlertDialog.Builder(this);
        mBuilderSelectorCover.setTitle("Selecciona una opción");

        //Objetos de la clase con los del front
        mCircleViewBack = findViewById(R.id.c_iv_back);
        mCircleViewProfile = findViewById(R.id.CircleImageProfile);
        mBtnEditProfile = findViewById(R.id.btn_EditProfile);
        mTextInputUsername = findViewById(R.id.ti_et_regUser_);
        mTextInputPhone = findViewById(R.id.ti_et_phone_);
        mImageViewCover = findViewById(R.id.ImageViewCover);

        mCircleViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mImageViewCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lugar = "cover";
                selectOptionImage(lugar);
            }
        });

        mCircleViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lugar = "profile";
                selectOptionImage(lugar);
            }
        });

        mBtnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEditProfile();
            }
        });



    }

    //Método para actualizar datos
    private void clickEditProfile() {
        //Recupera los datos de los campos de texto
        mUsername = mTextInputUsername.getText().toString();
        mPhone = mTextInputPhone.getText().toString();

        if(!mUsername.isEmpty() && !mPhone.isEmpty()){
            saveImageProfile(imagenProfile, dataProfile);
            saveImageCover(imagenCover, dataCover);
        }else{
            Toast.makeText(EditProfileActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    //Método para guardar las imágenes del profile en firebase, ademas de completar la actualización de datos
    private void saveImageProfile(File imagen1, Intent imagen3) {
        /*
        Comprobamos y subimos todas las imágenes que no son nulas
         */

        if(imagen1 != null){
            mImageProvider.saveFromCamera(EditProfileActivity.this, imagen1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();

                                User user = new User();
                                user.setId(mAuthProvider.getUid());
                                user.setUsername(mUsername);
                                user.setPhone(mPhone);
                                user.setImageProfile(url);

                                mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(EditProfileActivity.this, "Se actualizaron los datos", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });

        }

        if(imagen3 != null){
            mImageProvider.saveFromGallery(EditProfileActivity.this, imagen3).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();

                                User user = new User();
                                user.setId(mAuthProvider.getUid());
                                user.setUsername(mUsername);
                                user.setPhone(mPhone);
                                user.setImageProfile(url);

                                mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(EditProfileActivity.this, "Se actualizaron los datos", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });

        }

    }

    //Método para guardar las imágenes del cover en firebase, además completar la actualización de datos
    private void saveImageCover (File imagen2, Intent imagen4){

        if(imagen2 != null){
            mImageProvider.saveFromCamera(EditProfileActivity.this, imagen2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();

                                User user = new User();
                                user.setId(mAuthProvider.getUid());
                                user.setUsername(mUsername);
                                user.setPhone(mPhone);
                                user.setImageCover(url);

                                mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(EditProfileActivity.this, "Se actualizaron los datos", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });

        }

        if(imagen4 != null){
            mImageProvider.saveFromGallery(EditProfileActivity.this, imagen4).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                String url;
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                url = uri.toString();

                                User user = new User();
                                user.setId(mAuthProvider.getUid());
                                user.setUsername(mUsername);
                                user.setPhone(mPhone);
                                user.setImageCover(url);

                                mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(EditProfileActivity.this, "Se actualizaron los datos", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });

        }
    }

    //Método para abrir la galeria
    private void openGallery() {
        //Intent para acceder a imágenes de galeria
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");

        //Launcher con el intent
        LauncherGaleria.launch(galleryIntent);

    }

    //Método para abrir la cámara
    private void openCamera (){

        //Intent para acceder a imágenes de galeria
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Verifica que halla alguna aplicación que corra la petición del Intent con el resolveActivity
        if (cameraIntent.resolveActivity(getPackageManager()) != null){

            //Creamos un File para guardar la imágen
            File photoFile = null;

            //Intenta darle valor al archivo dentro de un try catch
            try {
                photoFile = createPhotFile();
            }catch (Exception e){
                Toast.makeText(EditProfileActivity.this, "Hubo un error con el archivo" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            //Si hay un archivo photoFile lanza el launcher
            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(EditProfileActivity.this, "com.example.appean", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                LauncherCamara.launch(cameraIntent);
            }
        }
    }

    //Método que crea un archivo con una dirección en el almacenamiento
    private File createPhotFile() throws IOException {
        //Obtiene la ruta de galeria
        File storegeDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //Crea el archivo con un nombre, sufijo o tipo de archivo y una ruta o directorio
        File photoFile = File.createTempFile(
                new Date()+"_photo",".jpg", storegeDir
        );

        //Rutas del archivo
        mPhotoPath = "file:" + photoFile.getAbsolutePath();
        mAbsolutePhotoPath = photoFile.getAbsolutePath();

        return photoFile;
    }

    //Lanzar la galeria
    private ActivityResultLauncher<Intent> LauncherGaleria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //Entra si lanza el intent y no hay error
                    if(result.getResultCode() == Activity.RESULT_OK){
                        try {
                            //Recupera información del Intent
                            data = result.getData();

                            //Crea el archivo de la imagen con la uri del intent
                            mImageFile = FileUtil.from(EditProfileActivity.this, data.getData());

                            //Coloca la imágen en el profile o en el cover
                            putImage(lugar,valor);

                            /*
                            La lógica es: Si o si cuando escoja algo del alertDialog cambia una de las dos variables a 1
                            por lo que así se sabrá en cual variable se va a guardar el intent de data para así poder subir la imagen
                             */
                            if(valorProfile==1){
                                dataProfile = data;
                            }
                            if (valorCover==1){
                                dataCover = data;
                            }

                            //Reinicio de variables
                            valorProfile = 0;
                            valorCover = 0;
                            data = null;
                        } catch (Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Se produjo un error en la apertura de galeria", Toast.LENGTH_LONG).show();
                            Log.d("ERRROR", "Se produjo un error" + e.getMessage());
                        }
                    }
                }
            }
    );

    //Lanzar cámara
    private ActivityResultLauncher<Intent> LauncherCamara = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        try {
                            //Crea el archivo con la dirección que se obtuvo en createPhotoFile()
                            mImageFile = new File(mAbsolutePhotoPath);
                            putImage(lugar,valor);

                            /*
                            La lógica es: Si o si cuando escoja algo del alertDialog cambia una de las dos variables a 2
                            por lo que así se sabrá en cual variable se va a guardar la imágen
                             */
                            if(valorProfile==2){
                                imagenProfile = mImageFile;
                            }
                            if (valorCover==2){
                                imagenCover = mImageFile;
                            }

                            //Reinicio de variables
                            valorProfile = 0;
                            valorCover = 0;
                            mImageFile=null;

                        } catch (Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Se produjo un error en la apertura de galeria", Toast.LENGTH_LONG).show();
                            Log.d("ERRROR", "Se produjo un error" + e.getMessage());
                        }
                    }
                }
            }
    );

    //Método para reemplazar las imágenes en sus respectivos contenedores
    private void putImage(String lugar, int valor){
        //Si se desplegló el menu en el cover
        if (lugar=="cover"){
            //Si se tomo por galeria
            if(valor == 1){
                mImageViewCover.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));


                //Reinicio de variables
                this.lugar = "";
                this.valor = 0;
            }
            //Si se tomó por cámara
            else if(valor == 2){
                Picasso.with(EditProfileActivity.this).load(mPhotoPath).into(mImageViewCover);

                //Reinicio de variables
                this.lugar = "";
                this.valor = 0;
            }else{
                Toast.makeText(EditProfileActivity.this, "No se pudo subir la imágen", Toast.LENGTH_LONG).show();
            }
        }
        //Si se desplegló el menu en el imageProfile
        else if(lugar == "profile"){
            //Si se tomo por galeria
            if(valor == 1){
                mCircleViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));

                //Reinicio de variables
                this.lugar = "";
                this.valor = 0;
            }
            //Si se tomó por cámara
            else if(valor == 2){
                Picasso.with(EditProfileActivity.this).load(mPhotoPath).into(mCircleViewProfile);

                //Reinicio de variables
                this.lugar = "";
                this.valor = 0;
            }else{
                Toast.makeText(EditProfileActivity.this, "No se pudo subir la imágen", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Método para desplegar el AlertDialog
    private void selectOptionImage(String lugar){
        //Desplega el alertDialog de profile
        if(lugar=="profile"){
            mBuilderSelectorProfile.setItems(options,new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i==0){
                        openGallery();
                        valorProfile = 1;
                        valor = valorProfile;
                        imagenProfile = null;
                    }else if (i==1){
                        openCamera();
                        valorProfile = 2;
                        valor = valorProfile;
                        dataProfile = null;
                    }
                }
            });
            mBuilderSelectorProfile.show();
        }
        //Desplega el AlertDialog del cover
        else if(lugar=="cover"){
            mBuilderSelectorCover.setItems(options,new DialogInterface.OnClickListener(){
                //Método onClick según la opción
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i==0){
                        openGallery();
                        valorCover = 1;
                        valor = valorCover;
                        imagenCover = null;
                    }else if (i==1){
                        openCamera();
                        valorCover = 2;
                        valor = valorCover;
                        dataProfile = null;
                    }
                }
            });
            mBuilderSelectorCover.show();
        }
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
                        mTextInputUsername.setText(mUsername);
                    }

                    //Recuperar telefono y rellenar campo
                    if(documentSnapshot.contains("phone")){
                        mPhone = documentSnapshot.getString("phone");
                        mTextInputPhone.setText(mPhone);
                    }

                    //Recuperar link de la imágen del profile y ponerla a través de internet
                    if(documentSnapshot.contains("imageProfile")){
                        String imProfile = documentSnapshot.getString("imageProfile");
                        Picasso.with(EditProfileActivity.this).load(imProfile).into(mCircleViewProfile);
                    }
                    //Recuperar link de la imágen del cover y ponerla a través de internet
                    if(documentSnapshot.contains("imageProfile")){
                        String imCover = documentSnapshot.getString("imageCover");
                        Picasso.with(EditProfileActivity.this).load(imCover).into(mImageViewCover);
                    }
                }
            }
        });
    }
}