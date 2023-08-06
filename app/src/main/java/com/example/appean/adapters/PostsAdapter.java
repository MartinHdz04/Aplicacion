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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

//Clase para tomar la información de un Post y pasarla a un CardView
public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

    private Context contexto;

    //Constructor PostAdapter
    public PostsAdapter (FirestoreRecyclerOptions<Post> options, Context contexto){
        super(options);
        this.contexto = contexto;
    }

    //Asocia el viewHolder con los datos
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {
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
                contexto.startActivity(intent);
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
        private TextView tv_title, tv_description;
        private ImageView iv_post;
        private View viewHolder;

        public ViewHolder(View view){
            super(view);
            this.tv_title = view.findViewById(R.id.tv_titlePostCard);
            this.tv_description = view.findViewById(R.id.tv_descripctionPostCard);
            this.iv_post = view.findViewById(R.id.iv_postCard);
            this.viewHolder = view;
        }
    }
}
