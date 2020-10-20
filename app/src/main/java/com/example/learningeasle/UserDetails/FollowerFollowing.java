package com.example.learningeasle.UserDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.learningeasle.R;
import com.example.learningeasle.model.AdapterUsers;
import com.example.learningeasle.model.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FollowerFollowing extends AppCompatActivity {
    RecyclerView follows;
    AdapterUsers adapterfollow;
    List<ModelUsers> usersList;
    String Uid;
    //Context context = this;
    DatabaseReference reference;
    String task;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_following);
        follows = findViewById(R.id.followrecyclerview);
        progressBar = findViewById(R.id.progressBar_followers);
        //Through Intent Getting the info that we need to show the followers list or following list
        task = getIntent().getStringExtra("Task");
        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        follows.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        follows.setLayoutManager(layoutManager);
        usersList = new ArrayList<>();
        loadList(new OnDataReceiveCallback() {
            @Override
            public void onDataReceived(List<ModelUsers> usersList) {
                progressBar.setVisibility(View.GONE);
                adapterfollow = new AdapterUsers(FollowerFollowing.this, usersList);
                follows.setAdapter(adapterfollow);
            }
        });

    }


    public interface OnDataReceiveCallback {
        void onDataReceived(List<ModelUsers> usersList);
    }


    private void loadList(final OnDataReceiveCallback callback) {
        progressBar.setVisibility(View.VISIBLE);
        if (task.equals("Follower")) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(Uid).child("Followers");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usersList.clear();

                    if(snapshot.getChildrenCount() == 0){
                        progressBar.setVisibility(View.GONE);
                        callback.onDataReceived(usersList);
                    }
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        final String uid = (String) ds.getValue();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                HashMap<Object, String> hashMap = (HashMap<Object, String>) snapshot.getValue();
                                ModelUsers users = new ModelUsers(hashMap.get("Id"), hashMap.get("Name"), hashMap.get("Url"), hashMap.get("email"), hashMap.get("phone"), hashMap.get("status"));
                                usersList.add(users);
                                callback.onDataReceived(usersList);
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid).child("Following");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    usersList.clear();
                    if(snapshot.getChildrenCount() == 0){
                        progressBar.setVisibility(View.GONE);
                        callback.onDataReceived(usersList);
                    }
                    System.out.println(snapshot.getChildrenCount()+" = size");
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        final String userid = (String) ds.getValue();

                        System.out.println(userid+" = user of list");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                HashMap<Object, String> hashMap = (HashMap<Object, String>) snapshot.getValue();
                                ModelUsers users = new ModelUsers(hashMap.get("Id"), hashMap.get("Name"), hashMap.get("Url"), hashMap.get("email"), hashMap.get("phone"), hashMap.get("status"));
                                usersList.add(users);
                                callback.onDataReceived(usersList);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }


}