package com.example.learningeasle.model;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.R;
import com.example.learningeasle.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
          final String curruid = FirebaseAuth.getInstance().getCurrentUser().getUid();
          holder.name.setText(userName);
          holder.email.setText(email);
          System.out.println(userName+" jsr "+email);
         if(url.equals("empty"))
           holder.profile.setImageResource(R.drawable.ic_action_account);
         else
           Picasso.get().load(url).into(holder.profile);
         //Setting the follower i.e if current user is following the user of holder or not
          setFollower(holder,curruid,Uid);
          //Following and unfollowing the user from the holder view
           holder.follow.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   final DatabaseReference reffollowing = FirebaseDatabase.getInstance().getReference("Users")
                           .child(curruid);
                   final DatabaseReference reffollowers = FirebaseDatabase.getInstance().getReference("Users")
                           .child(Uid);
                   reffollowing.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if(snapshot.child("Following").hasChild(Uid)){
                               reffollowing.child("Following").child(Uid).removeValue();
                               reffollowers.child("Followers").child(curruid).removeValue();
                           }else{
                               reffollowing.child("Following").child(Uid).setValue(Uid);
                               reffollowers.child("Followers").child(curruid).setValue(curruid);
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
               }
           });

          holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(context, UserProfile.class);
                  intent.putExtra("Id",Uid);
                  context.startActivity(intent);
              }
          });
    }

    private void setFollower(final UserHolder holder, final String curruid, final String uid) {
        final DatabaseReference reffollowing = FirebaseDatabase.getInstance().getReference("Users")
                .child(curruid);
        reffollowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Following").hasChild(uid)){
                    holder.follow.setText("Following");
                    holder.follow.setBackgroundColor(R.drawable.following);
                }else{
                    holder.follow.setText("Follow");
                    holder.follow.setBackgroundColor(R.drawable.follow_button);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        Button follow;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            follow = itemView.findViewById(R.id.follow);
        }
    }

}
