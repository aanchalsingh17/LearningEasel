package com.example.learningeasle.MainFragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningeasle.ProfileFragments.UserBookmarkFragment;
import com.example.learningeasle.ProfileFragments.UserFollowersFragment;
import com.example.learningeasle.ProfileFragments.UserFollowingFragment;
import com.example.learningeasle.ProfileFragments.UserPostsFragment;
import com.example.learningeasle.R;

import com.example.learningeasle.UserDetails.UpdateProfile;
import com.example.learningeasle.ViewImage;
import com.example.learningeasle.model.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileFragment extends Fragment {
    ImageView profile;
    FirebaseUser user;
    String userid;
    StorageReference reference;
    TextView username, useremail, userstatus, postsCnt, followersCnt, followingCnt;
    FirebaseFirestore fstore;
    String userID;
    FirebaseAuth fAuth;
    Activity context;
    Button editprofile;
    String url = null;
    View view;
    ViewPager viewPager;
    TabLayout tabLayout;

    private int cnt = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        profile = view.findViewById(R.id.image);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();//
        reference = FirebaseStorage.getInstance().getReference();
        editprofile = view.findViewById(R.id.editprofile);
        username = view.findViewById(R.id.username);
        useremail = view.findViewById(R.id.email);
        userstatus = view.findViewById(R.id.status);
        postsCnt = view.findViewById(R.id.postsCnt);
        followersCnt = view.findViewById(R.id.followersCnt);
        followingCnt = view.findViewById(R.id.followingCnt);
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        setProfile();
        //Edit profile btn is clicked go to update profile Activity
        editprofile.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(context, UpdateProfile.class));
                    }
                });
        return view;

    }

    //On activity created load the fragments of the profile
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_post_add_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_followers);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_done_24);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_bookmarks);


        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.allTabs), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.allTabs), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.allTabs), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.allTabs), PorterDuff.Mode.SRC_IN);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                tab.getIcon().setColorFilter(getResources().getColor(R.color.text), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                tab.getIcon().setColorFilter(getResources().getColor(R.color.allTabs), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getChildFragmentManager());
        sectionPagerAdapter.addFragment(new UserPostsFragment(), "");
        sectionPagerAdapter.addFragment(new UserFollowersFragment(), "");
        sectionPagerAdapter.addFragment(new UserFollowingFragment(), "");
        sectionPagerAdapter.addFragment(new UserBookmarkFragment(), "");

        viewPager.setAdapter(sectionPagerAdapter);
    }


    //Set the User profile with all the posts of user and personal info
    private void setProfile() {
        profile.setImageResource(R.drawable.ic_action_account);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
        //set profile image from the url
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) ds.getValue();
                    if (hashMap.get("Id").equals(userid)) {
                        String url = (String) hashMap.get("Url");
                        if (url.equals("empty"))
                            profile.setImageResource(R.drawable.ic_action_account);
                        else
                            Picasso.get().load(url).into(profile);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ViewImage.class);
                intent.putExtra("image", url);
                startActivity(intent);
            }
        });
        userID = fAuth.getCurrentUser().getUid();                                                           //user id stored
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //Set user details on the profile
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot db : snapshot.getChildren()) {
                    HashMap<Object, String> hashMap = (HashMap<Object, String>) db.getValue();
                    if (hashMap.get("Id").equals(userID)) {
                        String name = hashMap.get("Name");
                        String email = hashMap.get("email");
                        String status = hashMap.get("status");
                        username.setText(hashMap.get("Name"));
                        useremail.setText(email);
                        userstatus.setText(status);
                        url = hashMap.get("Url");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseDatabase.getInstance().getReference("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cnt = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) ds.getValue();

                    if (hashMap.get("pId").equals(userID))
                        cnt++;
                }
                postsCnt.setText(String.valueOf(cnt));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followersCnt.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingCnt.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}

