package com.example.learningeasle.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.example.learningeasle.MainActivity;
import com.example.learningeasle.R;
import com.example.learningeasle.chats.AdapterChatDisplay;
import com.example.learningeasle.model.ModelUsers;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    RecyclerView users;
    AdapterChatDisplay adapterChatDisplay;
    List<ModelUsers> usersList;
    View view;
    ProgressBar progressBar;
    String oldestUSer = "";
    ShimmerFrameLayout shimmerFrameLayout;
    int CurrentItems, totalItems, ViewedItems;
    int start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        users = findViewById(R.id.usersrecyclerview);
        users.setHasFixedSize(true);
        final LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        users.setLayoutManager(manager);
        usersList = new ArrayList<>();
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        progressBar = findViewById(R.id.progressBar_loading);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getAllUsers();
        adapterChatDisplay = new AdapterChatDisplay(getApplicationContext(), usersList);
        users.setAdapter(adapterChatDisplay);
        //Get starting users
        shimmerFrameLayout.startShimmer();
        getFirstUsers();
        users.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    progressBar.setVisibility(View.VISIBLE);
                    //After all the users are scrolled load the few more users from the firebase
                    getAllUsers();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                CurrentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                ViewedItems = manager.findLastVisibleItemPosition();
            }


        });


    }

    private void getFirstUsers() {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot db : snapshot.getChildren()) {
                    oldestUSer = db.getKey();

                    HashMap<Object, String> hashMap = (HashMap<Object, String>) db.getValue();
                    ModelUsers users = new ModelUsers(hashMap.get("Id"), hashMap.get("Name"), hashMap.get("Url"), hashMap.get("email"), hashMap.get("phone"), hashMap.get("status"));
                    if (!hashMap.get("Id").equals(fuser.getUid()))
                        usersList.add(users);

                }
                adapterChatDisplay = new AdapterChatDisplay(getApplicationContext(), usersList);
                users.setAdapter(adapterChatDisplay);
                users.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllUsers() {
        //current user;

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        final boolean[] first = {true};
        ref.orderByKey().startAt(oldestUSer).limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot db : snapshot.getChildren()) {
                    oldestUSer = db.getKey();
                    if (!first[0]) {
                        HashMap<Object, String> hashMap = (HashMap<Object, String>) db.getValue();
                        ModelUsers users = new ModelUsers(hashMap.get("Id"), hashMap.get("Name"), hashMap.get("Url"), hashMap.get("email"), hashMap.get("phone"), hashMap.get("status"));
                        if (!hashMap.get("Id").equals(fuser.getUid()))
                            usersList.add(users);
                    } else {
                        first[0] = false;
                    }

                }
                adapterChatDisplay = new AdapterChatDisplay(getApplicationContext(), usersList);
                users.setAdapter(adapterChatDisplay);
                adapterChatDisplay.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

}


