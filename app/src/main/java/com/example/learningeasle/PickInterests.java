package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.color.holo_red_dark;

public class PickInterests extends AppCompatActivity {
    Button btn;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_interests);
        btn = (Button) findViewById(R.id.science);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                String folder = sharedPreferences.getString("email_Id", "");
                int j = folder.length() - 4;
                final String username = folder.substring(0, j);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference().child(username);
                switch (view.getId()) {
                    case R.id.science:
                        System.out.println(",....................................................");
                        myRef.child("Science").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String data = snapshot.getValue().toString();
                                System.out.println(data+"qwerty");
                                if (data.equals("0")) {
                                    System.out.println(",aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                                    myRef.child("Science").setValue("1");
//                                    btn=(Button)findViewById(R.id.science);
                                    btn.setBackgroundResource(R.drawable.button_shaper_red);
                                } else {
                                    myRef.child("Science").setValue("0");
//                                    btn=(Button)findViewById(R.id.science);
                                    btn.setBackgroundResource(R.drawable.button_shaper_aqua);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                }

            }
        });
    }
}
