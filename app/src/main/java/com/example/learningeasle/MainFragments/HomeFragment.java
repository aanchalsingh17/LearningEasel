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

import com.example.learningeasle.ChatActivity;
import com.example.learningeasle.PickInterests;
import com.example.learningeasle.PushNotifications.Token;
import com.example.learningeasle.R;
import com.example.learningeasle.model.AdapterPost;
import com.example.learningeasle.model.modelpost;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.internal.$Gson$Preconditions;
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
    ArrayList<String> interest, following;
    ProgressBar progressBar;
    long oldestPost;
    ShimmerFrameLayout shimmerFrameLayout;
    Query query;
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
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        progressBar = view.findViewById(R.id.progressBar_loading);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.postsRecyclerview);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //layoutManager.setStackFromEnd(true);
        //layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        modelpostList = new ArrayList<>();
        setHasOptionsMenu(true);
        interest = new ArrayList<>();
        following = new ArrayList<>();


        System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        System.out.println(refreshToken+" refresh");
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(firebaseUser.getUid()).setValue(token);

        query = FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("order");
        getUserDetails();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    progressBar.setVisibility(View.VISIBLE);
                    loadPosts();
                }
            }
        });


        return view;
    }


    private void getStartingPost() {
        query.limitToFirst(15).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelpostList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, Object> hashMap = (HashMap<Object, Object>) dataSnapshot.getValue();
//                    if(FirebaseDatabase.getInstance().getReference(""))
                    oldestPost = (long) hashMap.get("order");
                    modelpost post;
                    System.out.println(hashMap.get("pId"));
                    if (hashMap.get("pLikes") == null && (interest.contains(hashMap.get("type")) || following.contains(hashMap.get("pId")))) {
//                        System.out.println(hashMap.get("pId"));
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0",
                                hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                hashMap.get("videourl").toString(),hashMap.get("pdfurl").toString(),hashMap.get("audiourl").toString());
                        modelpostList.add(post);
                    } else if (interest.contains(hashMap.get("type")) || following.contains(hashMap.get("pId"))) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(),
                                hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                hashMap.get("videourl").toString(),hashMap.get("pdfurl").toString(),hashMap.get("audiourl").toString());
                        modelpostList.add(post);
                    }


                }
                if(modelpostList.size()==0){
                    loadPosts();
                }
                adapterPost = new AdapterPost(getActivity(), modelpostList);
                recyclerView.setAdapter(adapterPost);
                recyclerView.setVisibility(View.VISIBLE);
                // progressDialog.dismiss();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
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
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot db : snapshot.getChildren()) {
                    HashMap<Object, String> hashMap = (HashMap<Object, String>) db.getValue();
                    if (hashMap.get("Id").equals(pId)) {
                        email = hashMap.get("email");
                        email = email.substring(0, email.length() - 4);

                        interest.clear();
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Users").child(db.getKey()).child("Following");
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    following.add(dataSnapshot.getKey());
                                }

                                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users").child(db.getKey()).child(email);
                                ref1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            if (dataSnapshot.getValue().equals("1"))
                                                interest.add(dataSnapshot.getKey());
                                        }
                                        //loadPosts();


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                shimmerFrameLayout.startShimmer();
                                getStartingPost();

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
        query.startAt(oldestPost).limitToFirst(15).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, Object> hashMap = (HashMap<Object, Object>) dataSnapshot.getValue();
//                    if(FirebaseDatabase.getInstance().getReference(""))
                    oldestPost = (long) hashMap.get("order");
                    modelpost post;
                    if (!start[0]) {
                        if (hashMap.get("pLikes") == null && (interest.contains(hashMap.get("type")) || following.contains(hashMap.get("pId")))) {
                            post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                    hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0",
                                    hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                    hashMap.get("videourl").toString(),hashMap.get("pdfurl").toString(),hashMap.get("audiourl").toString());
                            modelpostList.add(post);
                        } else if ((interest.contains(hashMap.get("type")) || following.contains(hashMap.get("pId")))) {
                            post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                    hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(),
                                    hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                    hashMap.get("videourl").toString(),hashMap.get("pdfurl").toString(),hashMap.get("audiourl").toString());
                            modelpostList.add(post);
                        }
                    } else {
                        start[0] = false;
                    }

                }
                if(modelpostList.size()==0){
                    loadPosts();
                }
                adapterPost = new AdapterPost(getActivity(), modelpostList);
                recyclerView.setAdapter(adapterPost);
                adapterPost.notifyDataSetChanged();
                //progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(),"Error Loading",Toast.LENGTH_SHORT).show();
            }


        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.interests, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.chat:
                startActivity(new Intent(getContext(), ChatActivity.class));
                getActivity().finish();
                return true;
            case R.id.interests:
                startActivity(new Intent(getContext(), PickInterests.class));
                getActivity().finish();
                return true;
        }

        return super.onOptionsItemSelected(item); // important line
    }
}