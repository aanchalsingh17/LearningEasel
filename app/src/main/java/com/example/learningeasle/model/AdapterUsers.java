package com.example.learningeasle.model;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.R;
import com.example.learningeasle.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.UserHolder>{
   Context context;
   List<ModelUsers> userList;
   String Url = null;
    public AdapterUsers(Context context, List<ModelUsers> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user,parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserHolder holder, int position) {
          final String userName = userList.get(position).getName();
          final String email = userList.get(position).getEmail();
          final String url = userList.get(position).getUrl();
          final String Uid = userList.get(position).getId();
          holder.name.setText(userName);
          holder.email.setText(email);
         if(url.equals("empty"))
           holder.profile.setImageResource(R.drawable.ic_action_account);
         else
           Picasso.get().load(url).into(holder.profile);



          holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(context, UserProfile.class);
                  intent.putExtra("Id",Uid);
                  context.startActivity(intent);
              }
          });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {


        ImageView profile;
        TextView name,email;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
        }
    }

}
