package com.example.learningeasle.UserDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningeasle.MainActivity;
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
    TextView username, useremail, userstatus,postsCnt, followersCnt, followingCnt;
    FirebaseFirestore fstore;
    RecyclerView recyclerView;
    List<modelpost> modelpostList;
    AdapterPost adapterPost;
    Button follow;
    private int cnt=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Id = getIntent().getStringExtra("Id");
        profile = findViewById(R.id.image);
        reference = FirebaseStorage.getInstance().getReference();
        username = findViewById(R.id.username);
        useremail = findViewById(R.id.email);
        userstatus = findViewById(R.id.status);
        fstore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.posts);
        follow = findViewById(R.id.follow);
        postsCnt = findViewById(R.id.postsCnt);
        followersCnt = findViewById(R.id.followersCnt);
        followingCnt = findViewById(R.id.followingCnt);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        modelpostList = new ArrayList<>();

        //If current user is admin then he cant follow any user make the visibility of the follow field gone
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("admin").child("Id");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    follow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Load the profile of the current user with all the basic info
        loadprofile();

        //Load all the posts of the user
        loadposts();
        final String curruid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //Set if the current user follow this user or not
        setfollower(curruid,Id);


        //Whenever follow button is clicked set the follower of the user
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
                              //If Currentuser follow this user then remove the user from the current user Following section
                              //and from the currentuser  followers section of the user whose profile is viewed
                              reffollowing.child("Following").child(Id).removeValue();
                              reffollowers.child("Followers").child(curruid).removeValue();
                          }else {
                              //If Currentuser  dont follow this user then add the user into the current user Following section
                              //and into the currentuser  followers section of the user whose profile is viewed
                              reffollowing.child("Following").child(Id).setValue(Id);
                              reffollowers.child("Followers").child(curruid).setValue(curruid);
                          }

                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {

                      }
                  });
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }

    private void setfollower(final String curruid, final String id) {
        DatabaseReference reffollowing = FirebaseDatabase.getInstance().getReference("Users")
                .child(curruid);
        reffollowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //If current user following section has the userid then set value to following otherwise to follow
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

    //Load all the post of the user
    private void loadposts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clearing the modelpost list in the starting
                modelpostList.clear();

                //To store the counts of number of posts of the user
                cnt = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Getting all the posts
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
                    modelpost post = null;
                    //If pLikes is null means there are zero likes and if the pid is equal to the current user id
                    //Then its user post
                    if (hashMap.get("pLikes") == null && hashMap.get("pId").equals(Id)) {
                        post = new modelpost(hashMap.get("pId"), hashMap.get("pImage"), hashMap.get("pTitle"), hashMap.get("pDesc"),
                                hashMap.get("pTime"), hashMap.get("pName"), hashMap.get("url"), "0",hashMap.get("pComments"),hashMap.get("type"),
                                hashMap.get("videourl"),hashMap.get("pdfurl"),hashMap.get("audiourl"));
                        cnt++;
                    } else if (hashMap.get("pId").equals(Id)) {
                        post = new modelpost(hashMap.get("pId"), hashMap.get("pImage"), hashMap.get("pTitle"), hashMap.get("pDesc"),
                                hashMap.get("pTime"), hashMap.get("pName"), hashMap.get("url"), hashMap.get("pLikes"),
                                hashMap.get("pComments"),hashMap.get("type"),hashMap.get("videourl"),hashMap.get("pdfurl"),hashMap.get("audiourl"));
                        cnt++;
                    }
                    //Id post is not null means new post is created
                    if(post!=null)
                        modelpostList.add(post);
                }
                //After all the posts are traversed set the count of the post to the posts count view
                postsCnt.setText(String.valueOf(cnt));
                adapterPost = new AdapterPost(UserProfile.this, modelpostList);
                recyclerView.setAdapter(adapterPost);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

    }

    private void loadprofile() {
        //Load the user profile details from the database that's the username the image the status and many more
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

                        setTitle((String) hashMap.get("Name"));
                        //Setting the profile image from the downloaded url if url is
                        //empty then the default image
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


        //Setting the count of followers and following of the user
        FirebaseDatabase.getInstance().getReference("Users").child(Id).child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followersCnt.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Users").child(Id).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingCnt.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}