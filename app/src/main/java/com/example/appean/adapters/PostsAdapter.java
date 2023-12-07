package com.example.appean.adapters;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appean.R;
import com.example.appean.activities.PostDetailActivity;
import com.example.appean.models.Like;
import com.example.appean.models.Post;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.LikeProvider;
import com.example.appean.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

//Clase para tomar la información de un Post y pasarla a un CardView
public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

    private Context contexto;
    //Providers
    private UserProvider mUserProvider;
    private LikeProvider mLikeProvider;
    private Authprovider mAuthProvider;

    //Constructor PostAdapter
    public PostsAdapter (FirestoreRecyclerOptions<Post> options, Context contexto){
        super(options);
        this.contexto = contexto;
    }

    //Asocia el viewHolder con los datos
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        //Instancia los providers
        mUserProvider = new UserProvider();
        mLikeProvider = new LikeProvider();
        mAuthProvider = new Authprovider();

        //Me toma el documento de la base de datos según la posición que este tenga
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        final String postId = documentSnapshot.getId();

        holder.tv_title.setText(post.getCategory());
        holder.tv_description.setText(post.getDescription());
        if (post.getImage() != null && !post.getImage().isEmpty()){
            Picasso.with(contexto).load(post.getImage()).into(holder.iv_post);
        }

        //Añade un evento para que al oprimir cada tarjeta te lleve a una pantalla que la profundize
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(contexto, PostDetailActivity.class);
                //Mandar la info del Id del Post para recuperarlo después
                intent.putExtra("PostId", postId);
                contexto.startActivity(intent);
            }
        });

        //Añade un evento al oprimir el botón de like
        holder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un like con su información respectiva
                Like like = new Like();
                like.setIdUser(mAuthProvider.getUid());
                like.setIdPost(postId);
                like.setTimestamp(new Date().getTime());

                //Método para interactuar con el like en la base de datos y cambiar la imágenes
                like(like, holder);
            }
        });

        //Obtener información del usuario
        getUserInfo(post.getIdUser(), holder);

        //Verificamos si hay algun like en la publicación por parte del usuario
        chekIfLikeExist(postId, mAuthProvider.getUid(), holder);

        //Obtenemos la cantidad de likes y las mostramos al usuario
        getNumberLikesInPost(postId, holder);
    }

    //Método para encontrar la cantidad de likes que ha recibido un Post
    private void getNumberLikesInPost(String postId, ViewHolder holder) {
        mLikeProvider.getLikesByPost(postId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                //Try debido a que me aparecio un error al cerrar sesión en esta linea, sin embargo no afecta el programa
                try{
                    int numberLikes = value.size();
                    holder.tv_likes.setText(String.valueOf(numberLikes) + "Me gustas");
                }catch (Exception e){

                }
            }
        });
    }

    //Método que verifica y cambia la imágen si previamente existia un like
    private void chekIfLikeExist(String postId, String idUser, ViewHolder holder) {
        //La consulta del like de un usuario en un post
        mLikeProvider.getLikeByPostAndUser(postId, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Mira el numero de veces que ha dado like en una publicación. (En teoria solo se podrá una vez)
                int numberDocuments = queryDocumentSnapshots.size();
                //Condicional que revisa si ya le habia dado like
                if(numberDocuments > 0){
                    holder.iv_like.setImageResource(R.drawable.like);
                }else{
                    holder.iv_like.setImageResource(R.drawable.like_gris);
                }
            }
        });
    }

    private void like(Like like, ViewHolder holder) {
        //La consulta del like de un usuario en un post
        mLikeProvider.getLikeByPostAndUser(like.getIdPost(), mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Mira el numero de veces que ha dado like en una publicación. (En teoria solo se podrá una vez)
                int numberDocuments = queryDocumentSnapshots.size();

                //Condicional que revisa si ya le habia dado like
                if(numberDocuments > 0){
                    //Recuperamos el id del documento que encontró
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();

                    //Borra el documento con ese id, osea un like
                    mLikeProvider.delete(idLike).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //Al borrar el like cambiará la imágen a gris
                            holder.iv_like.setImageResource(R.drawable.like_gris);
                        }
                    });
                }else{
                    mLikeProvider.create(like).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //Al crear el like, cambiará la foto de like
                            holder.iv_like.setImageResource(R.drawable.like);
                        }
                    });
                }
            }
        });
    }

    //Método para obtener Información del usuario que creó el Post
    private void getUserInfo(String idUser, ViewHolder holder) {
        //Obtenemos el usuario gracias al ID
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        //Llenamos dicho campo con el nombre usuario
                        holder.tv_username.setText("By: " + username.toUpperCase());
                    }
                }
            }
        });
    }

    //Crea el viewHolder y lo asocia con el cardView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    //Clase view holder, que será cada post
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_title, tv_description, tv_username, tv_likes;
        private ImageView iv_post, iv_like;
        private View viewHolder;

        public ViewHolder(View view){
            super(view);
            this.tv_title = view.findViewById(R.id.tv_titlePostCard);
            this.tv_description = view.findViewById(R.id.tv_descripctionPostCard);
            this.tv_username = view.findViewById(R.id.tv_usernamePostCard);
            this.tv_likes = view.findViewById(R.id.tv_likes_CVP);
            this.iv_post = view.findViewById(R.id.iv_postCard);
            this.iv_like = view.findViewById(R.id.iv_like_CVP);
            this.viewHolder = view;
        }
    }
}
