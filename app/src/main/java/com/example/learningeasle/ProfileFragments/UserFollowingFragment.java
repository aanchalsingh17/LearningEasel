package com.example.learningeasle.ProfileFragments;

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
import com.example.learningeasle.model.AdapterUsers;
import com.example.learningeasle.model.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserFollowingFragment extends Fragment {

    RecyclerView follows;
    AdapterUsers adapterfollow;
    List<ModelUsers> usersList;
    String Uid;
    //Context context = this;
    DatabaseReference reference;
    ProgressBar progressBar,loading;
    String oldestfollowing ="";
    boolean first = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_following, container, false);
        follows = view.findViewById(R.id.followrecyclerview);
        progressBar = view.findViewById(R.id.progressBar_followers);
        //Through Intent Getting the info that we need to show the followers list or following list
        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        follows.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        follows.setLayoutManager(layoutManager);
        loading = view.findViewById(R.id.progressBar_loading);
        usersList = new ArrayList<>();
        loadList(new OnDataReceiveCallback() {
            @Override
            public void onDataReceived(List<ModelUsers> usersList) {
                progressBar.setVisibility(View.GONE);
                adapterfollow = new AdapterUsers(getActivity(), usersList);
                follows.setAdapter(adapterfollow);
            }
        });
        return view;
    }

    public interface OnDataReceiveCallback {
        void onDataReceived(List<ModelUsers> usersList);
    }


    private void loadList(final OnDataReceiveCallback callback) {


        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid).child("Following");
            progressBar.setVisibility(View.VISIBLE);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    usersList.clear();
                    if (snapshot.getChildrenCount() == 0) {
                        progressBar.setVisibility(View.GONE);
                        callback.onDataReceived(usersList);
                    }
                    System.out.println(snapshot.getChildrenCount() + " = size");
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        final String userid = (String) ds.getValue();
                        oldestfollowing = ds.getKey();
                        System.out.println(userid + " = user of list");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                HashMap<Object, String> hashMap = (HashMap<Object, String>) snapshot.getValue();
                                ModelUsers users = new ModelUsers(hashMap.get("Id"), hashMap.get("Name"), hashMap.get("Url"), hashMap.get("email"), hashMap.get("phone"), hashMap.get("status"));
                                usersList.add(users);
                                callback.onDataReceived(usersList);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });

    }
}