package com.example.learningeasle.ProfileFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learningeasle.R;
import com.example.learningeasle.model.AdapterBookmark;
import com.example.learningeasle.model.AdapterPost;
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


public class UserBookmarkFragment extends Fragment {

    RecyclerView recyclerView;
    List<modelpost> bookmarkList, filteredList;
    AdapterBookmark adapterBookmark;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_bookmark, container, false);
        recyclerView = view.findViewById(R.id.bookmarkRecyclerview);
        bookmarkList = new ArrayList<>();
        filteredList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);


        final HashMap<String, Boolean> hashMap = new HashMap<>();




        loadBookmarks(new OnDataReceiveCallback() {
            @Override
            public void onDataReceived(List<modelpost> bookmarkList) {
                for(int i=0;i<bookmarkList.size();i++){
                    modelpost post=bookmarkList.get(i);
                    if(hashMap.get(post.getpTime()) == null){
                        filteredList.add(post);
                        hashMap.put(post.getpTime(),true);
                    }
                }
                adapterBookmark = new AdapterBookmark(getContext(), filteredList);
                recyclerView.setAdapter(adapterBookmark);
            }
        });


        return view;
    }

    public interface OnDataReceiveCallback {
        void onDataReceived(List<modelpost> bookmarkList);
    }

    private void loadBookmarks(final OnDataReceiveCallback callback) {
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("Bookmarks");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookmarkList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    final String timestamp = ds.getKey();


                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(timestamp);

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

//                            if(bookmarkList.size()>=1)
//                            bookmarkList.remove(bookmarkList.size()-1);

                            final HashMap<Object, String> hashMap = (HashMap<Object, String>) snapshot.getValue();
                            modelpost post;
                            if (hashMap.get("pLikes") == null && hashMap.get("pTime").equals(timestamp)) {
                                post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                        hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0", hashMap.get("pComments").toString(), hashMap.get("type").toString());
                                bookmarkList.add(post);
                                callback.onDataReceived(bookmarkList);
                            } else if (hashMap.get("pTime").equals(timestamp)) {
                                post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                        hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(), hashMap.get("pComments").toString(), hashMap.get("type").toString());
                                bookmarkList.add(post);
                                callback.onDataReceived(bookmarkList);
                                System.out.println("bookmarks size " + bookmarkList.size());
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
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