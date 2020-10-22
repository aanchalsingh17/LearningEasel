package com.example.learningeasle.MainFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.learningeasle.PickInterests;
import com.example.learningeasle.R;
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
    String email;
    ArrayList<String> interest;
    ProgressBar progressBar;
    String oldestPost = "";
    int CurrentItems, totalItems, ViewedItems;
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
         progressBar = view.findViewById(R.id.progressBar_loading);
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
        setHasOptionsMenu(true);
        interest=new ArrayList<>();
        getUserDetails();



        return view;
    }
    private void getStartingPost() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.limitToFirst(3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelpostList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
//                    if(FirebaseDatabase.getInstance().getReference(""))
                    oldestPost = dataSnapshot.getKey();
                    modelpost post;
                    if (hashMap.get("pLikes") == null && interest.contains(hashMap.get("type"))) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0",
                                hashMap.get("pComments").toString(),hashMap.get("type").toString());
                        modelpostList.add(post);
                    } else if(interest.contains(hashMap.get("type"))) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(),
                                hashMap.get("pComments").toString(),hashMap.get("type").toString());
                        modelpostList.add(post);
                    }


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

    public void getUserDetails() {
        final String pId = FirebaseAuth.getInstance().getCurrentUser().getUid();



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot db:snapshot.getChildren()) {
                    HashMap<Object, String> hashMap = (HashMap<Object, String>) db.getValue();
                    if (hashMap.get("Id").equals(pId)) {
                        email=hashMap.get("email");
                        email=email.substring(0,email.length()-4);

                        interest.clear();
                        DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("Users").child(db.getKey()).child(email);
                        ref1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    if(dataSnapshot.getValue().equals("1"))
                                        interest.add(dataSnapshot.getKey());
                                }
                                loadPosts();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPosts() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        final boolean[] start = {true};
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelpostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
//                    if(FirebaseDatabase.getInstance().getReference(""))
                    oldestPost = dataSnapshot.getKey();
                        modelpost post;
                        if (hashMap.get("pLikes") == null && interest.contains(hashMap.get("type"))) {
                            post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                    hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0",
                                    hashMap.get("pComments").toString(), hashMap.get("type").toString());
                            modelpostList.add(post);
                        } else if (interest.contains(hashMap.get("type"))) {
                            post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                    hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(),
                                    hashMap.get("pComments").toString(), hashMap.get("type").toString());
                            modelpostList.add(post);
                        }


                }

                adapterPost = new AdapterPost(getActivity(), modelpostList);
                recyclerView.setAdapter(adapterPost);
                progressDialog.dismiss();
               // progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(),"Error Loading",Toast.LENGTH_SHORT).show();
            }


        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.interests,menu);
        MenuItem menuItem=menu.findItem(R.id.interests);
//        Button button= (Button) menuItem.getActionView();
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getContext(), PickInterests.class));
                getActivity().finish();
                return false;
            }
        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }
}