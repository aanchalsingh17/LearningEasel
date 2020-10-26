package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

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
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;
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
                        String image = "" + ds.child("Url").getValue();
                        nameTV.setText(name);
                        try {
                            Picasso.get().load(image).placeholder(R.drawable.ic_action_profile).into(profileIV);
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
    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        databaseReference.child("Chats").push().setValue(hashMap);

        messageET.setText("");
    }
}