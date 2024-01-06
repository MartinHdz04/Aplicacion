package com.example.appean.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appean.R;
import com.example.appean.models.Comment;
import com.example.appean.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import io.github.florent37.shapeofview.shapes.CircleView;

//Clase para tomar la información de un Comment y pasarla a un CardView
public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder> {

    private Context contexto;

    private UserProvider mUserProvider;

    //Constructor CommentAdapter
    public CommentAdapter(FirestoreRecyclerOptions<Comment> options, Context contexto){
        super(options);
        this.contexto = contexto;
        mUserProvider = new UserProvider();
    }

    //Asocia el viewHolder con los datos
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment comment) {

        //Me toma el documento de la base de datos según la posición que este tenga
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);

        holder.tv_comment.setText(comment.getComment());

        String idUser = documentSnapshot.getString("idUser");

        getUserInfo(idUser, holder);
    }

    private void getUserInfo(String id, ViewHolder holder){
        mUserProvider.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.tv_username.setText(username);
                    }
                    if (documentSnapshot.contains("imageProfile")){
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        if(imageProfile!=null){
                            if(!imageProfile.isEmpty()){
                                Picasso.with(contexto).load(imageProfile).into(holder.iv_comment);
                            }
                        }
                    }
                }
            }
        });
    }

    //Crea el viewHolder y lo asocia con el cardView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent, false);
        return new ViewHolder(view);
    }

    //Clase view holder, que será cada comment
    public class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_username;
        private final TextView tv_comment;
        private final ImageView iv_comment;
        private final View viewHolder;

        public ViewHolder(View view){
            super(view);
            this.tv_username = view.findViewById(R.id.tv_username_CVC);
            this.tv_comment = view.findViewById(R.id.tv_comment_CVC);
            this.iv_comment = view.findViewById(R.id.c_iv_comment);
            this.viewHolder = view;
        }
    }
}
