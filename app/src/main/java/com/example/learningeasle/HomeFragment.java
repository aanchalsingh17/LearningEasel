package com.example.learningeasle;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.learningeasle.model.AdapterPost;
import com.example.learningeasle.model.modelpost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    List<modelpost> modelpostList;
    AdapterPost adapterPost;
    ProgressBar progressBar;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public HomeFragment() {
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        progressBar = view.findViewById(R.id.progressBar_home);
//        progressBar.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.postsRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        modelpostList = new ArrayList<>();

        loadPosts();

        return view;
    }

    private void loadPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelpostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
                    modelpost post;
                    if (hashMap.get("pLikes")==null) {
                        post = new modelpost(hashMap.get("pId"), hashMap.get("pImage"), hashMap.get("pTitle"), hashMap.get("pDesc"),
                                hashMap.get("pTime"), hashMap.get("pName"), hashMap.get("url"), "0");
                    } else {
                        post = new modelpost(hashMap.get("pId"), hashMap.get("pImage"), hashMap.get("pTitle"), hashMap.get("pDesc"),
                                hashMap.get("pTime"), hashMap.get("pName"), hashMap.get("url"), hashMap.get("pLikes"));
                    }
                    modelpostList.add(post);

                }

                adapterPost = new AdapterPost(getActivity(), modelpostList);
                recyclerView.setAdapter(adapterPost);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(),"Error Loading",Toast.LENGTH_SHORT).show();
            }


        });
    }
}