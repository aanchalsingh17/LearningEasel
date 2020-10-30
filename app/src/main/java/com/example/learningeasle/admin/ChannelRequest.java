package com.example.learningeasle.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learningeasle.Interests.AddChannel;
import com.example.learningeasle.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChannelRequest extends Fragment {
    RecyclerView recyclerView;
    ModelPendingChannel modelPendingChannel;
    List<ModelPendingChannel> modelPendingChannelList;
    View view;
    FloatingActionButton add_channel;
    AdapterPendingChannel adapterPendingChannel;
    ShimmerFrameLayout shimmerFrameLayout;
    public ChannelRequest() {
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
        view =  inflater.inflate(R.layout.fragment_channel_request, container, false);
        recyclerView = view.findViewById(R.id.pendingchannel);
        add_channel = view.findViewById(R.id.add_channel);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        modelPendingChannelList = new ArrayList<>();
        setHasOptionsMenu(true);

        shimmerFrameLayout.startShimmer();
        add_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChannel();
            }
        });
        LoadAllChannels();
        return  view;
    }

    private void addChannel() {
        Intent channel = new Intent(getContext(), AddChannel.class);
        startActivity(channel);

    }

    private void LoadAllChannels() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin").child("pendingchannel");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelPendingChannelList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()) {
                    HashMap<Object,Object> hashMap = (HashMap<Object, Object>) snapshot1.getValue();

                            modelPendingChannel = new ModelPendingChannel((String)hashMap.get("cName"),(String) hashMap.get("cDes"),(String)hashMap.get("cUrl"));
                    modelPendingChannelList.add(modelPendingChannel);
                }
                adapterPendingChannel = new AdapterPendingChannel(getContext(),modelPendingChannelList);
                recyclerView.setAdapter(adapterPendingChannel);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}