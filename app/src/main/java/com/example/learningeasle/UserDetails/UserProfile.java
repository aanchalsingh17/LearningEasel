package com.example.learningeasle.UserDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningeasle.R;
import com.example.learningeasle.model.AdapterPost;
import com.example.learningeasle.model.modelpost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserProfile extends AppCompatActivity {
   String Id;
    ImageView profile;
    StorageReference reference;
    TextView username, useremail, userstatus;
    FirebaseFirestore fstore;
    RecyclerView recyclerView;
    List<modelpost> modelpostList;
    AdapterPost adapterPost;
    Button follow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Id = getIntent().getStringExtra("Id");
        profile = findViewById(R.id.image);
        reference = FirebaseStorage.getInstance().getReference();
        username = findViewById(R.id.username);
        useremail = findViewById(R.id.email);
        userstatus = findViewById(R.id.status);
        fstore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.posts);
        follow = findViewById(R.id.follow);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        modelpostList = new ArrayList<>();
        loadprofile();
        loadposts();
        final String curruid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setfollower(curruid,Id);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference reffollowing = FirebaseDatabase.getInstance().getReference("Users")
                        .child(curruid);
                final DatabaseReference reffollowers = FirebaseDatabase.getInstance().getReference("Users")
                        .child(Id);
                  reffollowing.addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                          if(snapshot.child("Following").hasChild(Id)){
                              reffollowing.child("Following").child(Id).removeValue();
                              reffollowers.child("Followers").child(curruid).removeValue();
                          }else
                              reffollowing.child("Following").child(Id).setValue(Id);
                             reffollowers.child("Followers").child(curruid).setValue(curruid);

                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {

                      }
                  });
            }
        });
    }

    private void setfollower(final String curruid, final String id) {
        DatabaseReference reffollowing = FirebaseDatabase.getInstance().getReference("Users")
                .child(curruid);
        reffollowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Following").hasChild(id)){
                    follow.setText("Following");
                    follow.setBackgroundResource(R.drawable.button_unfollow);
                }else{
                    follow.setText("Follow");
                    follow.setBackgroundResource(R.drawable.button_follow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadposts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelpostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
                    modelpost post = null;
                    if (hashMap.get("pLikes") == null && hashMap.get("pId").equals(Id)) {
                        post = new modelpost(hashMap.get("pId"), hashMap.get("pImage"), hashMap.get("pTitle"), hashMap.get("pDesc"),
                                hashMap.get("pTime"), hashMap.get("pName"), hashMap.get("url"), "0",hashMap.get("pComments"),hashMap.get("type"),
                                hashMap.get("videourl"),hashMap.get("pdfurl"),hashMap.get("audiourl"));
                    } else if (hashMap.get("pId").equals(Id)) {
                        post = new modelpost(hashMap.get("pId"), hashMap.get("pImage"), hashMap.get("pTitle"), hashMap.get("pDesc"),
                                hashMap.get("pTime"), hashMap.get("pName"), hashMap.get("url"), hashMap.get("pLikes"),
                                hashMap.get("pComments"),hashMap.get("type"),hashMap.get("videourl"),hashMap.get("pdfurl"),hashMap.get("audiourl"));
                    }
                    if(post!=null)
                        modelpostList.add(post);
                }

                adapterPost = new AdapterPost(UserProfile.this, modelpostList);
                recyclerView.setAdapter(adapterPost);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

    }

    private void loadprofile() {
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference("Users");
        ds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) ds.getValue();
                    if(hashMap.get("Id").equals(Id)){
                        username.setText((String) hashMap.get("Name"));
                        useremail.setText((CharSequence) hashMap.get("email"));
                        userstatus.setText((CharSequence) hashMap.get("status"));
                        String url = (String) hashMap.get("Url");
                        if(url.equals("empty"))
                            profile.setImageResource(R.drawable.ic_action_account);
                        else
                            Picasso.get().load(url).into(profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}