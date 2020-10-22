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

import com.example.learningeasle.R;
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

public class AdminPostFragment extends Fragment {
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    List<modelpost> modelpostList;
    AdapterAdminPost adapteradminPost;
    View view;
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
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        recyclerView = view.findViewById(R.id.postsRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        modelpostList = new ArrayList<>();
        setHasOptionsMenu(true);
        getAllPost();
        return  view;
    }

    private void getAllPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelpostList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
                            modelpost post;
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
                        progressDialog.dismiss();
                        // progressBar.setVisibility(View.INVISIBLE);
                    }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}