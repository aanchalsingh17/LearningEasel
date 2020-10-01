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


    }
   public void func(View v)
    {

        sharedPreferences=getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String folder = sharedPreferences.getString("email_Id", "");


        int j=folder.length()-4;
        System.out.println(folder);
        final String username=folder.substring(0,j);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef=database.getReference().child(username);

        switch(v.getId()) {
            case R.id.science:
               myRef.child("Science").addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       String data=snapshot.getValue().toString();
                       if(data=="1")
                           myRef.child("Science").setValue("0");
                       else
                           myRef.child("Science").setValue("1");
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
                break;
            case R.id.medication:
                myRef.child("Medication").addValueEventListener(new ValueEventListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data == "1") {
                            myRef.child("Medication").setValue("0");
//                            btn=(Button)findViewById(R.id.medication);
//                            btn.setText("Follow us");
//                            btn.setBackgroundResource(Integer.parseInt("@drawable/button_shaper_aqua"));
                        } else {
                            myRef.child("Medication").setValue("1");
//                            btn.setBackgroundResource(Integer.parseInt("@drawable/button_shaper_aqua"));
//                            btn.setText("Unfollow");
                        }
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.computers:
                myRef.child("Computers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data == "1") {
                            myRef.child("Computers").setValue("0");
//                            btn.setText("Follow us");
//                            btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_aqua"));
                        } else {
                            myRef.child("Computers").setValue("1");
//                            btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_red"));
//                            btn.setText("Unfollow");}
                        }

                    }
                        @Override
                        public void onCancelled (@NonNull DatabaseError error){

                        }

                });
                break;
            case R.id.business:
                myRef.child("Business").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data == "1") {
                            myRef.child("Business").setValue("0");
//                        btn.setText("Follow us");
//                            btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_aqua"));}
                        } else {
                            myRef.child("Business").setValue("1");
//                        btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_red"));
//                            btn.setText("Unfollow");}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.architecture:
                myRef.child("Architecture").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data=snapshot.getValue().toString();
                        if(data=="1") {
                            myRef.child("Architecture").setValue("0");
//                            btn.setText("Follow us");
//                            btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_aqua"));}
                        }
                        else
                        {myRef.child("Artitechure").setValue("1");
//                        btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_red"));
//                            btn.setText("Unfollow");}
                    }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.arts:
                myRef.child("Arts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data == "1") {
                            myRef.child("Arts").setValue("0");
//                        btn.setText("Follow us");
//                            btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_aqua"));
//
                        } else {
                            myRef.child("Arts").setValue("1");
//                        btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_red"));
//                            btn.setText("Unfollow");}
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.environment:
                myRef.child("Environment").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data == "1") {
                            myRef.child("Environment").setValue("0");
//                        btn.setText("Follow us");
//                            btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_aqua"));}
                        } else {
                            myRef.child("Environment").setValue("1");
//                        btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_red"));
//                            btn.setText("Unfollow");}
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.sports:
                myRef.child("Sports").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data == "1") {
                            myRef.child("Sports").setValue("0");
//                        btn.setText("Follow us");
//                            btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_aqua"));}
                        } else {
                            myRef.child("Sports").setValue("1");
//                        btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_red"));
//                            btn.setText("Unfollow");}
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.economics:
                myRef.child("Economics").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String data = snapshot.getValue().toString();
                        if (data == "1") {
                            myRef.child("Economics").setValue("0");
//                        btn.setText("Follow us");
//                            btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_aqua"));}
                        } else {
                            myRef.child("Economics").setValue("1");
//                        btn.setBackgroundColor(Integer.parseInt("@drawable/button_shaper_red"));
//                            btn.setText("Unfollow");}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;

        }
    }
}