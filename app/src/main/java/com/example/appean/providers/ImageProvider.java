package com.example.appean.providers;

import android.content.Context;
import android.content.Intent;

import com.example.appean.utils.CompressorBitmapImage;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

//Clase para manejar las imágens
public class ImageProvider {

    //Objetos
    private StorageReference mStorage;

    //Constructor
    public ImageProvider (){
        //Al crease se obtiene la referencia
        this.mStorage = FirebaseStorage.getInstance().getReference();
    }

    //Método para guardar imágenes si fueron tomadas desde la galeria
    public UploadTask saveFromGallery(Context context, Intent data){

        //Nombre del archivo
        StorageReference storage = this.mStorage.child(new Date() + ".jpg");
        this.mStorage = storage;
        //Poner un archivo en esa referencia
        return storage.putFile(data.getData());
    }

    //Método para guardar imágenes si fueron tomadas desde la cámara
    public UploadTask saveFromCamera(Context context, File file){

        //Nombre archivo
        StorageReference storage = this.mStorage.child(new Date() + ".jpg");
        this.mStorage = storage;
        //Leer el archivo y pasarlo a un conjunto de Bytes
        byte[] imageByte = CompressorBitmapImage.getImage(file.getAbsolutePath());
        //Poner un archivo en esa referencia
        return storage.putBytes(imageByte);
    }

    //Getter del storageReference
    public StorageReference getStorage() {
        return this.mStorage;
    }
}
