package com.example.learningeasle.admin;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.example.learningeasle.R;
import com.example.learningeasle.model.AdapterPost;
import com.example.learningeasle.model.modelpost;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminPostFragment extends Fragment {
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    List<modelpost> modelpostList;
    AdapterAdminPost adapteradminPost;
    View view;
    ProgressBar progressBar;
    String oldestpost = "";
    ShimmerFrameLayout shimmerFrameLayout;
    public AdminPostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_admin_post, container, false);
        recyclerView = view.findViewById(R.id.postsRecyclerview);
        progressBar = view.findViewById(R.id.progressBar_loading);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        modelpostList = new ArrayList<>();
        setHasOptionsMenu(true);
        shimmerFrameLayout.startShimmer();
        loadStartingPost();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    progressBar.setVisibility(View.VISIBLE);
                    //After all the posts are scrolled load the few more posts from the firebase
                    getAllPost();
                }
            }
        });

        return  view;
    }

    private void loadStartingPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.limitToFirst(4).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelpostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
                    modelpost post;
                    oldestpost = dataSnapshot.getKey();
                    if (hashMap.get("pLikes") == null) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0",
                                hashMap.get("pComments").toString(), hashMap.get("type").toString());
                        modelpostList.add(post);
                    } else  {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(),
                                hashMap.get("pComments").toString(), hashMap.get("type").toString());
                        modelpostList.add(post);
                    }


                }

                adapteradminPost = new AdapterAdminPost(getContext(),modelpostList);
                recyclerView.setAdapter(adapteradminPost);
                //progressDialog.dismiss();
                recyclerView.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        final boolean[] first = {true};
        reference.orderByKey().startAt(oldestpost).limitToFirst(4).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
                    modelpost post;
                    oldestpost = dataSnapshot.getKey();
                    if(!first[0]) {
                        if (hashMap.get("pLikes") == null) {
                            post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                    hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0",
                                    hashMap.get("pComments").toString(), hashMap.get("type").toString());
                            modelpostList.add(post);
                        } else {
                            post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                    hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(),
                                    hashMap.get("pComments").toString(), hashMap.get("type").toString());
                            modelpostList.add(post);
                        }
                    }else{
                        first[0] = false;
                    }


                }

                adapteradminPost = new AdapterAdminPost(getContext(),modelpostList);
                recyclerView.setAdapter(adapteradminPost);
                adapteradminPost.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}