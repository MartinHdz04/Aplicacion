package com.example.appean.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appean.R;
import com.example.appean.activities.PostDetailActivity;
import com.example.appean.models.Post;
import com.example.appean.providers.Authprovider;
import com.example.appean.providers.LikeProvider;
import com.example.appean.providers.PostProvider;
import com.example.appean.providers.UserProvider;
import com.example.appean.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

//Clase para tomar la información de un Post y pasarla a un CardView
public class PostsAdapterView extends FirestoreRecyclerAdapter<Post, PostsAdapterView.ViewHolder> {

    private Context contexto;
    //Providers
    private UserProvider mUserProvider;
    private LikeProvider mLikeProvider;
    private Authprovider mAuthProvider;
    private PostProvider mPostProvider;

    //Constructor PostAdapter
    public PostsAdapterView(FirestoreRecyclerOptions<Post> options, Context contexto){
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
        mPostProvider = new PostProvider();

        //Me toma el documento de la base de datos según la posición que este tenga
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        final String postId = documentSnapshot.getId();

        //Llamo a la clase relative para convertir el timestamp a un texto
        final String relativeTime = RelativeTime.getTimeAgo(post.getTimestamp(), contexto);

        //Añado el título o categoria
        holder.tv_title.setText(post.getCategory());

        //Añado el texto del relativeTime
        holder.tv_relativeTime.setText(relativeTime);

        //Si exite una imágen guardada en el post, se carga al imageView
        if (post.getImage() != null && !post.getImage().isEmpty()){
            Picasso.with(contexto).load(post.getImage()).into(holder.iv_post);
        }

        holder.iv_delete.setVisibility(View.GONE);

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
    }


    //Crea el viewHolder y lo asocia con el cardView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_post, parent, false);
        return new ViewHolder(view);
    }

    //Clase view holder, que será cada post
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_title, tv_relativeTime;
        private ImageView iv_post, iv_delete;
        private View viewHolder;

        public ViewHolder(View view){
            super(view);
            this.tv_title = view.findViewById(R.id.tv_titlePost_CVMP);
            this.tv_relativeTime = view.findViewById(R.id.tv_relaTime_CVMP);
            this.iv_post = view.findViewById(R.id.iv_post_CVMP);
            this.iv_delete = view.findViewById(R.id.iv_delete_CVMP);
            this.viewHolder = view;

        }
    }
}
