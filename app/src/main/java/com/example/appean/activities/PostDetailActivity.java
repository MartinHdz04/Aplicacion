package com.example.appean.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appean.R;
import com.example.appean.adapters.CommentAdapter;
import com.example.appean.adapters.SliderAdapter;
import com.example.appean.models.Comment;
import com.example.appean.models.SliderItem;
import com.example.appean.providers.CommentProvider;
import com.example.appean.providers.LikeProvider;
import com.example.appean.providers.PostProvider;
import com.example.appean.providers.UserProvider;
import com.example.appean.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.florent37.shapeofview.shapes.CircleView;

public class PostDetailActivity extends AppCompatActivity {

    //Variables del front
    private SliderView mSliderView;
    private TextView tv_category, tv_description, tv_phone, tv_username, tv_relativeTime, tv_likes;
    private ImageView iv_profile;
    private Button btn_irPerfil;
    private CircleView mCircleViewBack;
    private FloatingActionButton mFabComment;
    private RecyclerView mRecyclerView;

    //Providers
    private PostProvider mPostProvider;
    private UserProvider mUserProvider;
    private CommentProvider mCommentProvider;
    private LikeProvider mLikeProvider;

    //Adapters
    private SliderAdapter mSliderAdapter;
    private CommentAdapter mCommentAdapter;

    //Variables para complementar el código
    private List<SliderItem> mSliderItems;
    private String mExtraPostID, idUser;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //Instancio providers
        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();
        mCommentProvider = new CommentProvider();
        mLikeProvider = new LikeProvider();

        //Instancio la lista de cada item del slider
        mSliderItems = new ArrayList<>();

        //Instancia de los adapter
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);

        //Recuperamos el valor del PostId que enviamos desde la pantalla anterior
        mExtraPostID = getIntent().getStringExtra("PostId");

        //Conexión con el front
        mCircleViewBack = findViewById(R.id.iv_back_APD);
        mSliderView = findViewById(R.id.imageSlider);
        tv_category = findViewById(R.id.tv_category_APD);
        tv_username = findViewById(R.id.tv_username_APD);
        tv_phone = findViewById(R.id.tv_phone_ADP);
        tv_description = findViewById(R.id.tv_description_APD);
        tv_relativeTime = findViewById(R.id.tv_relaTime_APD);
        tv_likes = findViewById(R.id.tv_likes_APD);
        iv_profile = findViewById(R.id.iv_profile_APD);
        btn_irPerfil = findViewById(R.id.btn_ir_perfil_APD);
        mFabComment = findViewById(R.id.fab_comments_APD);
        mRecyclerView = findViewById(R.id.recycleViewComments);

        //Definir el campo de acción del RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //Listener para ir atrás
        mCircleViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Método para que se pueda mover dentro del cuadro de texto cuanto este es muy grande
        tv_description.setMovementMethod(new ScrollingMovementMethod());

        //Instanciación de imágenes del slider
        getPost();

        btn_irPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irPerfil();
            }
        });

        mFabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComment();
            }
        });

        //Obtener número de likes de la publicación
        getNumberLikes();
    }

    private void getNumberLikes() {
        mLikeProvider.getLikesByPost(mExtraPostID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberLikes = queryDocumentSnapshots.size();
                tv_likes.setText(String.valueOf(numberLikes) + "Me gusta");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Consulta a firebase y devuelve los comments
        //Query query = mCommentProvider.getAll();
        Query query = mCommentProvider.getCommentByPost(getIntent().getStringExtra("PostId"));

        //Pone los comment en un formato para el adapter
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        //Le pasamos el formato al adapter
        mCommentAdapter = new CommentAdapter(options, PostDetailActivity.this);

        //Ponemos los post en el recyclerView
        mRecyclerView.setAdapter(mCommentAdapter);

        //Comienza a detectar cambios en la base de datos
        mCommentAdapter.startListening();
    }



    @Override
    protected void onStop() {
        super.onStop();
        mCommentAdapter.stopListening();
    }

    //Método que al oprimir el botón de comentario me crea un dialog para insertar un comentario
    private void showDialogComment() {
        //Creo un alert dialog con builder para configurarlo
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);

        //Se instancia un título y un mensaje
        alert.setTitle("¡Comentario!");
        alert.setMessage("Ingresa tu comentario");

        //Creo una view de EditText para ponerla en el dialog y que el usuario pueda editarla
        EditText editText = new EditText(PostDetailActivity.this);
        //Se pone un hint
        editText.setHint("Texto");

        //Creamos un LinearLayout.params para configurar algunos parámetros del editText
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                //Parámetros básico para instanciar, un largo hasta encontrar borde y ancho según el tamaño de lo que contenga
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //Le configuramos los parámetros al editText
        editText.setLayoutParams(params);

        params.setMargins(36,0, 36,36);

        //Para términos visuales, vamos a meter el EditText en un contenedor, y se instancia de forma tal que se puede configurar
        RelativeLayout container = new RelativeLayout(PostDetailActivity.this);

        //Se instancian unos parámetros específicos para ese tipo de contenedor
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        //Se le asigna los parámetros al container
        container.setLayoutParams(relativeParams);

        //se le asigna el editText al contenedor
        container.addView(editText);

        //Se le asigna el contenedor a el alerDialog
        alert.setView(container);

        //Añadimos un botón postivo
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Guardamos en una variable el contenido del editText
                String comentario = editText.getText().toString();

                //Se valida que no este vacio
                if (!comentario.isEmpty()){
                    //Método para crear un comentario
                    createComment(comentario);
                }else{
                    Toast.makeText(PostDetailActivity.this,"Ingrese un comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Añadimos un botón negativo, al oprimirlo se cierra el dialog
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //Muestra el dialogo ya configurado
        alert.show();
    }

    //Método para subir un comentario y guardarlo
    private void createComment(String comentario) {
        //Creación e instanciación de un comment
        Comment comment = new Comment();
        comment.setComment(comentario);
        comment.setIdPost(mExtraPostID);
        comment.setIdUser(idUser);
        comment.setTimestamp(new Date().getTime());

        //Se sube el comment y se le añade un listenner
        mCommentProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PostDetailActivity.this,"Se creo el comentario correctamente", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(PostDetailActivity.this,"No se pudo crear el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Método que nos lleva al perfil del usuario que creo la publicación
    private void irPerfil() {
        if(idUser!=null){
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", idUser);
            startActivity(intent);
            finish();
        }
    }

    /*
    * Método para recuperar las imagenes y ponerlas en el modelo de item que va dentro del slider
    * Añade este item al array que contendrá las imagenes
     */
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

                    if (documentSnapshot.contains("category")){
                        //Si existe el campo, traemos la información del mismo, donde está la categoria del post
                        String category = documentSnapshot.getString("category");
                        tv_category.setText(category);
                    }

                    if (documentSnapshot.contains("description")){
                        //Si existe el campo, traemos la información del mismo, donde está la descripción del post
                        String description = documentSnapshot.getString("description");
                        tv_description.setText(description);
                    }

                    if (documentSnapshot.contains("timestamp")){
                        //Si existe el campo, traemos la información del mismo, donde está el timestamp
                        long timestamp = documentSnapshot.getLong("timestamp");

                        //Obtenemos el valor desde hace cuanto se creó un post
                        String relativeTime = RelativeTime.getTimeAgo(timestamp, PostDetailActivity.this);

                        tv_relativeTime.setText(relativeTime);
                    }

                    if (documentSnapshot.contains("idUser")){
                        //Si existe el campo, traemos la información del mismo, donde está el id del usuario
                        idUser = documentSnapshot.getString("idUser");

                        //Recuperamos datos del usuario
                        getUser();
                    }
                }
            }
        });
    }

    //Método para recuperar los datos del usuario
    private void getUser(){
        //Buscamos al usuario por su id
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            //Cuando lo encuentra verfica espacios y los recupera
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        tv_username.setText(username);
                    }

                    if(documentSnapshot.contains("phone")){
                        String phone = documentSnapshot.getString("phone");
                        tv_phone.setText(phone);
                    }

                    if(documentSnapshot.contains("imageProfile")){
                        String profile = documentSnapshot.getString("imageProfile");
                        Picasso.with(PostDetailActivity.this).load(profile).into(iv_profile);
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