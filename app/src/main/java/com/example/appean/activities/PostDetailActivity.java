package com.example.appean.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.appean.R;
import com.example.appean.adapters.SliderAdapter;
import com.example.appean.models.SliderItem;
import com.example.appean.providers.PostProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    //Variables del front
    private SliderView mSliderView;

    //Providers
    private PostProvider mPostProvider;

    //Adapters
    private SliderAdapter mSliderAdapter;

    //Variables para complementar el código
    private List<SliderItem> mSliderItems;
    private String mExtraPostID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //Instancio providers
        mPostProvider = new PostProvider();

        //Instancio la lista de cada item del slider
        mSliderItems = new ArrayList<>();

        //Instancia de los adapter
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);

        //Recuperamos el valor del PostId que enviamos desde la pantalla anterior
        mExtraPostID = getIntent().getStringExtra("PostId");

        //Conexión con el front
        mSliderView = findViewById(R.id.imageSlider);

        //Instanciación de imágenes del slider
        getPost();
    }

    //Método para recuperar las imagenes y ponerlas en el modelo de item que va dentro del slider, añade este item al array que contendrá las imagenes
    private void getPost(){

        //Busca el post con el id que mandamos desde el post provider con el click
        mPostProvider.getPostById(this.mExtraPostID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Confirmamos si tal archivo exite
                if(documentSnapshot.exists()){
                    if (documentSnapshot.contains("image")){
                        //Si existe el campo, traemos la información del mismo, donde está el url de la foto
                        String urlImage1 = documentSnapshot.getString("image");

                        //Creamos un modelo de item
                        SliderItem sliderItem = new SliderItem();
                        //Le seteamos el url
                        sliderItem.setImageUrl(urlImage1);
                        //Lo añadimos al array de los item por si hay más imágenes
                        mSliderItems.add(sliderItem);
                        //Instanciamos el sliderView
                        instanceSlider();
                    }
                }
            }
        });
    }

    //Método para instanciar al SliderView
    private void instanceSlider() {
        //Instanciación y configuración del SlliderView, que será aquel donde reposen las imágenes
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.ZOOMOUTTRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(R.color.verde);
        mSliderView.setIndicatorUnselectedColor(R.color.gris);
    }
}