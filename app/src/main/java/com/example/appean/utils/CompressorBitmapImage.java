package com.example.appean.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//Clase para convertir un File a un conjunto de Bytes
public class CompressorBitmapImage {

    //Método para pasar un File a Byte[]
    public static byte[] getImage(String path) {
        //Encuentra el File con el path
        final File file_temp = new File(path);

        //Arreglo de Byte con tamaño del archivo
        byte[] imageBytes = new byte[(int)file_temp.length()];

        try {
            //Abre el archivo
            FileInputStream inputStream = new FileInputStream(file_temp);
            //Lee el archivo y lo guarda en imageBytes
            inputStream.read(imageBytes);
            //Cierra el archivo
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBytes;
    }
}
