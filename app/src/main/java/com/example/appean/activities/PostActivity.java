package com.example.appean.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appean.R;
import com.example.appean.fragments.SeleccionFragment;
import com.example.appean.models.Post;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.ImageProvider;
import com.example.appean.providers.PostProvider;
import com.example.appean.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import io.github.florent37.shapeofview.shapes.CircleView;
//Clase para crear publicaciones
public class PostActivity extends AppCompatActivity {

    //Objetos
    private CircleView mCircleView;
    private ImageButton btn_pFiltros;
    private Button btn_publicar;
    private ImageView iv_subir;
    private EditText et_descripcion;

    //Variables para el manejo de las imágenes
    private Intent data;
    private File mImageFile;
    private String mAbsolutePhotoPath, mPhotoPath;

    //Providers
    private ImageProvider mImageProvider;
    private PostProvider mPostProvider;
    private Authprovider mAuthProvider;

    //Variables para completar acciones
    private String category = "";
    private String descripcion = "";
    private CharSequence options[];
    private int valor = 0;
    private AlertDialog.Builder mBuilderSelector;

    //Ventana emergente para escoger un filtro
    private SeleccionFragment seleccionFragment = new SeleccionFragment();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //Instancia del AlertDialog
        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[]{"Abrir galeria", "Tomar foto"};

        //Instancia Providers
        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new Authprovider();

        //Conexión con el front
        et_descripcion = findViewById(R.id.et_descripcion);
        mCircleView = findViewById(R.id.c_iv_back);
        iv_subir = findViewById(R.id.iv_subir);
        btn_pFiltros = findViewById(R.id.btn_pFiltros);
        btn_publicar = findViewById(R.id.btn_publicar);

        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irHome();
            }
        });

        iv_subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage();
            }
        });

        btn_pFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionFragment.show(getSupportFragmentManager(), "Dialogo de seleccion");
            }
        });

        btn_publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPost();
            }
        });
    }

    //Publicar la imagen
    private void clickPost() {
        category = seleccionFragment.getCategory();
        descripcion = et_descripcion.getText().toString();
        if(!category.isEmpty() && !descripcion.isEmpty()){
            saveImage();
        }else{
            Toast.makeText(PostActivity.this, "Completa los campos", Toast.LENGTH_SHORT).show();
        }
    }

    //Guardar la publilcación
    private void saveImage() {
        //Si escogió galeria
        if (valor == 1){
            mImageProvider.saveFromGallery(PostActivity.this, data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Recuperar la dirección de la imagen
                                String url = uri.toString();

                                //Crear una publicación y completar sus atributos
                                Post post = new Post();
                                post.setImage(url);
                                post.setCategory(category);
                                post.setDescription(descripcion);
                                post.setIdUser(mAuthProvider.getUid());
                                post.setTimestamp(new Date().getTime());

                                //Indicar quien hace la publicación
                                mPostProvider.setId(mAuthProvider.getUid());

                                //Guardar en firebase
                                mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                        //Si está bien va directo al home
                                        if (taskSave.isSuccessful()){
                                            irHome();
                                            Toast.makeText(PostActivity.this, "Se almacenaron correctamente los datos", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(PostActivity.this, "Error en el almacenamiento de datos", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                    }else{
                        Toast.makeText(PostActivity.this, "Error almacenado la imágen", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //Si escogió cámara
        else if (valor == 2){
            mImageProvider.saveFromCamera(PostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Recuperar la dirección de la imagen
                                String url = uri.toString();

                                //Crear una publicación y completar sus atributos
                                Post post = new Post();
                                post.setImage(url);
                                post.setCategory(category);
                                post.setDescription(descripcion);
                                post.setIdUser(mAuthProvider.getUid());
                                post.setTimestamp(new Date().getTime());

                                //Indicar quien hace la publicación
                                mPostProvider.setId(mAuthProvider.getUid());

                                //Guardar en firebase
                                mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                        //Si está bien va directo al home
                                        if (taskSave.isSuccessful()){
                                            irHome();
                                            Toast.makeText(PostActivity.this, "Se almacenaron correctamente los datos", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(PostActivity.this, "Error en el almacenamiento de datos", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                    }else{
                        Toast.makeText(PostActivity.this, "Error almacenado la imágen", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PostActivity.this, "Hubo un error con el archivo" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            //Si hay un archivo photoFile lanza el launcher
            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.example.appean", photoFile);
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
                            mImageFile = FileUtil.from(PostActivity.this, data.getData());

                            //Remplaza el contenedor con la imagen
                            iv_subir.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                        } catch (Exception e) {
                            Toast.makeText(PostActivity.this, "Se produjo un error en la apertura de galeria", Toast.LENGTH_LONG).show();
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

                            //Remplaza el contenedor con la imagen
                            Picasso.with(PostActivity.this).load(mPhotoPath).into(iv_subir);
                        } catch (Exception e) {
                            Toast.makeText(PostActivity.this, "Se produjo un error en la apertura de galeria", Toast.LENGTH_LONG).show();
                            Log.d("ERRROR", "Se produjo un error" + e.getMessage());
                        }
                    }
                }
            }
    );

    //Método que lleva a la activity home
    private void irHome(){
        Intent intent = new Intent(PostActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    //Método para desplegar el AlertDialog
    private void selectOptionImage(){
        mBuilderSelector.setItems(options,new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    openGallery();
                    valor = 1;
                }else if (i==1){
                    openCamera();
                    valor = 2;
                }
            }
        });
        mBuilderSelector.show();
    }
}