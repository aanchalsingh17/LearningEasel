package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.learningeasle.model.AdapterChat;
import com.example.learningeasle.model.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatDetailsActivity extends AppCompatActivity {


    androidx.appcompat.widget.Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIV;
    TextView nameTV,statusTV;
    EditText messageET;
    ImageButton sendbtn;
    FirebaseAuth firebaseAuth;

    String hisUid;
    String myUid;
    String hisImage;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    List<ModelChat>chatList;
    AdapterChat  adapterChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView=findViewById(R.id.chat_recyclerView);
        profileIV=findViewById(R.id.profileIV);
        nameTV=findViewById(R.id.nameTV);
        statusTV=findViewById(R.id.status);
        messageET=findViewById(R.id.messageET);
        sendbtn=findViewById(R.id.send);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();

        hisUid = intent.getStringExtra("Id");

        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        myUid=user.getUid();


        firebaseDatabase=FirebaseDatabase.getInstance();
        usersDbRef=firebaseDatabase.getReference("Users");


        usersDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    System.out.println(ds.getKey()+"{{{{{{{{{{{");
                    if(ds.getKey().equals(hisUid)) {
                        String name = "" + ds.child("Name").getValue();
                        hisImage = "" + ds.child("Url").getValue();
                        nameTV.setText(name);
                        String onlineStatus=""+ds.child("onlineStatus").getValue();
                        if(onlineStatus.equals("online"))
                        {
                            statusTV.setText(onlineStatus);
                        }
                        else
                        {
                            Calendar calendar = Calendar.getInstance(Locale.getDefault());
                            calendar.setTimeInMillis(Long.parseLong(onlineStatus));

                            String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                            statusTV.setText("Last seen at: "+pTime);
                        }

                        try {
                            Picasso.get().load(hisImage).placeholder(R.drawable.ic_action_profile).into(profileIV);
                        } catch (Exception e) {
                            Picasso.get().load(R.drawable.ic_action_profile).into(profileIV);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
sendbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String message=messageET.getText().toString().trim();
        if(TextUtils.isEmpty(message)){
            Toast.makeText(getApplicationContext(),"Cannot send the empty message",Toast.LENGTH_SHORT
            ).show();
            return;
        }
        else{
            sendMessage(message);

        }

    }
});

readMessages();
seenMessage();

    }
    private void checkOnlineStatus(String status){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("onlineStatus",status);
        dbRef.updateChildren(hashMap);
    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelChat chat ;
                    final HashMap<Object, String> hashMap =  (HashMap<Object, String>) ds.getValue();
                    chat=new ModelChat(hashMap.get("message"),hashMap.get("receiver"),
                            hashMap.get("sender"),hashMap.get("timestamp"),hashMap.get("isSeen"));
                    if (chat.getSender().equals(hisUid) && chat.getReceiver().equals(myUid)) {
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen","1");
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void readMessages() {
        chatList=new ArrayList<>();
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelChat chat ;
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) ds.getValue();
                    chat=new ModelChat(hashMap.get("message"),hashMap.get("receiver"),
                    hashMap.get("sender"),hashMap.get("timestamp"),hashMap.get("isSeen"));
                    if(chat.getSender().equals(hisUid) && chat.getReceiver().equals(myUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    adapterChat=new AdapterChat(getApplicationContext(),chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
        String timestamp=String.valueOf(System.currentTimeMillis());
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen","0");
        databaseReference.child("Chats").push().setValue(hashMap);

        messageET.setText("");
    }

    @Override
    protected void onStart() {
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }
}