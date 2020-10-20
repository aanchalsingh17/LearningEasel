package com.example.learningeasle.UserDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;

import com.example.learningeasle.R;
import com.example.learningeasle.model.AdapterPost;
import com.example.learningeasle.model.ModelUsers;
import com.example.learningeasle.model.modelpost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bookmark extends AppCompatActivity {
    RecyclerView recyclerView;
    List<modelpost> bookmarkList;
    AdapterPost adapterPost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        recyclerView = findViewById(R.id.bookmarkRecyclerview);
        bookmarkList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        loadBookmarks(new OnDataReceiveCallback() {
            @Override
            public void onDataReceived(List<modelpost> postsList) {

            }
        });
    }

    public interface OnDataReceiveCallback {
        void onDataReceived(List<modelpost> postsList);
    }

    private void loadBookmarks(OnDataReceiveCallback callback) {
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Bookmarks");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    final String timestamp = ds.getKey();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(timestamp);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            bookmarkList.clear();

                                final HashMap<Object, String> hashMap = (HashMap<Object, String>) snapshot.getValue();
//                    if(FirebaseDatabase.getInstance().getReference(""))
                                modelpost post;
                                if (hashMap.get("pLikes") == null&&hashMap.get("pTime").equals(timestamp)) {
                                    post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                            hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0", hashMap.get("pComments").toString(),hashMap.get("type").toString());
                                    bookmarkList.add(post);
                                } else if(hashMap.get("pTime").equals(timestamp)) {
                                    post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                            hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(), hashMap.get("pComments").toString(),hashMap.get("type").toString());
                                    bookmarkList.add(post);
                                }



                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            },500);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(),"Error Loading",Toast.LENGTH_SHORT).show();
                        }


                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}