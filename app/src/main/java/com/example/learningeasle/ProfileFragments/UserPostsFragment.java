package com.example.learningeasle.ProfileFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learningeasle.MainFragments.PostFragment;
import com.example.learningeasle.R;
import com.example.learningeasle.model.Adapter;
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


public class UserPostsFragment extends Fragment {

    RecyclerView postlist;
    List<modelpost> modelpostList;
    Adapter adapterPost;
    String userID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user_posts, container, false);
        postlist = view.findViewById(R.id.posts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postlist.setLayoutManager(layoutManager);
        modelpostList = new ArrayList<>();
        loadPosts();
        return  view;
    }


    private void loadPosts() {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelpostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
                    modelpost post = null;
                    if (hashMap.get("pLikes") == null && hashMap.get("pId").equals(userID)) {
                        post = new modelpost(hashMap.get("pId"), hashMap.get("pImage"), hashMap.get("pTitle"), hashMap.get("pDesc"),
                                hashMap.get("pTime"), hashMap.get("pName"), hashMap.get("url"), "0", hashMap.get("pComments"),hashMap.get("type"));

                    } else if (hashMap.get("pId").equals(userID)) {
                        post = new modelpost(hashMap.get("pId"), hashMap.get("pImage"), hashMap.get("pTitle"), hashMap.get("pDesc"),
                                hashMap.get("pTime"), hashMap.get("pName"), hashMap.get("url"), hashMap.get("pLikes"), hashMap.get("pComments"),hashMap.get("type"));
                    }
                    if (post != null)
                        modelpostList.add(post);
                }

                adapterPost = new Adapter(getActivity(), modelpostList, editClick);
                postlist.setAdapter(adapterPost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
    Adapter.EditClick editClick = new Adapter.EditClick() {
        @Override
        public void onEditClick(int position, String Uid, String pTimeStamp,String edit,String title,String pDescription,String pImage,String like,String comment) {
            PostFragment fragment = new PostFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Id", Uid);
            bundle.putString("pTime", pTimeStamp);
            bundle.putString("Edit",edit);
            bundle.putString("Title",title);
            bundle.putString("Des",pDescription);
            bundle.putString("Url",pImage);
            bundle.putString("Likes",like);
            bundle.putString("Comments",comment);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.rec,fragment).commit();

        }
    };
}