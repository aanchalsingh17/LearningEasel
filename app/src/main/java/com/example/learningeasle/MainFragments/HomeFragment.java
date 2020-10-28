package com.example.learningeasle.MainFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.example.learningeasle.BottomSheetDialog;
import com.example.learningeasle.PickInterests;
import com.example.learningeasle.PushNotifications.Data;
import com.example.learningeasle.PushNotifications.Token;
import com.example.learningeasle.R;
import com.example.learningeasle.chats.ChatActivity;
import com.example.learningeasle.model.AdapterPost;
import com.example.learningeasle.model.modelpost;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment implements BottomSheetDialog.BottomSheetListener {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<modelpost> modelpostList;
    AdapterPost adapterPost;
    String email;
    ArrayList<String> interest, following;
    long oldestPost;
    ShimmerFrameLayout shimmerFrameLayout;
    Query query;
    private BottomSheetDialog.BottomSheetListener mListener;

    private int BATTERY_OPTIMIZATIONS_REQUEST_CODE = 7;

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
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.postsRecyclerview);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        //Set the layout manager of the recycler view
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        modelpostList = new ArrayList<>();
        setHasOptionsMenu(true);
        interest = new ArrayList<>();
        following = new ArrayList<>();
        mListener=this;

        //Check for battery optimizations if not enabled than show alert dialog
        checkForBatteryOptimizations();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(firebaseUser.getUid()).setValue(token);

        //Order the post according to the timestamp to get the latest  post on top
        query = FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("order");
        getUserDetails();


        return view;
    }

    private void checkForBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            if (!powerManager.isIgnoringBatteryOptimizations(getContext().getPackageName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Warning");
                builder.setMessage("Battery optimization is enabled. It may interrupt running background services. ");
                builder.setPositiveButton("Disable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        startActivityForResult(intent, BATTERY_OPTIMIZATIONS_REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BATTERY_OPTIMIZATIONS_REQUEST_CODE) {
            checkForBatteryOptimizations();
        }
    }

    //Get the few starting post from the query and then on demand load more
    private void getStartingPost() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Clear the model post list and load the post from the starting
                modelpostList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, Object> hashMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    //Get the oldest post id to acheive lazy loading
                    oldestPost = (long) hashMap.get("order");
                    modelpost post;
                    System.out.println(hashMap.get("pId"));
                    //Create new Model Post of the loads and add it to the list
                    if (hashMap.get("pLikes") == null && (interest.contains(hashMap.get("type")) || following.contains(hashMap.get("pId")))) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0",
                                hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                hashMap.get("videourl").toString(), hashMap.get("pdfurl").toString(), hashMap.get("audiourl").toString());
                        modelpostList.add(post);
                    } else if (interest.contains(hashMap.get("type")) || following.contains(hashMap.get("pId"))) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(),
                                hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                hashMap.get("videourl").toString(), hashMap.get("pdfurl").toString(), hashMap.get("audiourl").toString());
                        modelpostList.add(post);
                    }


                }
                //Create adapter set the recycler view and make recycler view visible and shimmerframelayout gone
                adapterPost = new AdapterPost(getActivity(), modelpostList);
                recyclerView.setAdapter(adapterPost);
                recyclerView.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(),"Error Loading",Toast.LENGTH_SHORT).show();
            }


        });
    }

    //Get the user details to Filter the posts
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
                                following.clear();
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

    //Load few more post starting from the oldest posts
    public void loadPosts() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelpostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, Object> hashMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    modelpost post;
                    if (hashMap.get("pLikes") == null && (interest.contains(hashMap.get("type")) || following.contains(hashMap.get("pId")))) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0",
                                hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                hashMap.get("videourl").toString(), hashMap.get("pdfurl").toString(), hashMap.get("audiourl").toString());
                        modelpostList.add(post);
                    } else if ((interest.contains(hashMap.get("type")) || following.contains(hashMap.get("pId")))) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(),
                                hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                hashMap.get("videourl").toString(), hashMap.get("pdfurl").toString(), hashMap.get("audiourl").toString());
                        modelpostList.add(post);
                    }


                }
                Collections.sort(modelpostList, new CustomComparator());
                adapterPost = new AdapterPost(getActivity(), modelpostList);
                recyclerView.setAdapter(adapterPost);
                adapterPost.notifyDataSetChanged();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }


        });
    }

    public void loadPostsInterests() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelpostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, Object> hashMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    modelpost post;
                    if (hashMap.get("pLikes") == null && interest.contains(hashMap.get("type")) ) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0",
                                hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                hashMap.get("videourl").toString(), hashMap.get("pdfurl").toString(), hashMap.get("audiourl").toString());
                        modelpostList.add(post);
                    } else if (interest.contains(hashMap.get("type"))) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(),
                                hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                hashMap.get("videourl").toString(), hashMap.get("pdfurl").toString(), hashMap.get("audiourl").toString());
                        modelpostList.add(post);
                    }


                }
                adapterPost = new AdapterPost(getActivity(), modelpostList);
                recyclerView.setAdapter(adapterPost);
                adapterPost.notifyDataSetChanged();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }


        });
    }

    public void loadPostsFollowers() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelpostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, Object> hashMap = (HashMap<Object, Object>) dataSnapshot.getValue();
                    modelpost post;
                    if (hashMap.get("pLikes") == null && following.contains(hashMap.get("pId"))) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), "0",
                                hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                hashMap.get("videourl").toString(), hashMap.get("pdfurl").toString(), hashMap.get("audiourl").toString());
                        modelpostList.add(post);
                    } else if (following.contains(hashMap.get("pId"))) {
                        post = new modelpost(hashMap.get("pId").toString(), hashMap.get("pImage").toString(), hashMap.get("pTitle").toString(), hashMap.get("pDesc").toString(),
                                hashMap.get("pTime").toString(), hashMap.get("pName").toString(), hashMap.get("url").toString(), hashMap.get("pLikes").toString(),
                                hashMap.get("pComments").toString(), hashMap.get("type").toString(),
                                hashMap.get("videourl").toString(), hashMap.get("pdfurl").toString(), hashMap.get("audiourl").toString());
                        modelpostList.add(post);
                    }


                }
                adapterPost = new AdapterPost(getActivity(), modelpostList);
                recyclerView.setAdapter(adapterPost);
                adapterPost.notifyDataSetChanged();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
                return true;
            case R.id.interests:
                startActivity(new Intent(getContext(), PickInterests.class));
                getActivity().finish();
                return true;
            case R.id.sort:
                BottomSheetDialog bottomSheetDialog = BottomSheetDialog.getInstance(mListener);
                bottomSheetDialog.show(getActivity().getSupportFragmentManager(),"BottomSheet");

        }

        return super.onOptionsItemSelected(item); // important line
    }

    @Override
    public void onTextSelected(String type) {
        modelpostList.clear();
        if (type.equals("pop")) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            loadPosts();
        }
        if (type.equals("aoi")) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            loadPostsInterests();
        }
        if (type.equals("upd")) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            getStartingPost();
        }
        if (type.equals("fol")) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            loadPostsFollowers();
        }
    }

    public class CustomComparator implements Comparator<modelpost> {

        @Override
        public int compare(modelpost o1, modelpost o2) {
            if(o1.getpLikes() == o2.getpLikes())
                return o2.getpTime().compareTo(o1.getpTime());
            return o2.getpLikes().compareTo(o1.getpLikes());
        }
    }
}