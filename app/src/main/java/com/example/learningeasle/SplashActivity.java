package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.learningeasle.Accounts.Login;
import com.example.learningeasle.admin.AdminMainPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final ImageView imageView = findViewById(R.id.logo_reg1);
        final LottieAnimationView lottieAnimationView = findViewById(R.id.lottie);

        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();


        if (fUser != null) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin").child("Id");


            //If Current User is Admin then open the AdminPage otherwise main Activity

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(fUser.getUid())) {
                        imageView.animate().translationY(-1600).setDuration(1000).setStartDelay(4000);
                        lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                startActivity(new Intent(getApplicationContext(), AdminMainPage.class));
                                finish();

                            }

                        }, 4700);

                    } else {

                        imageView.animate().translationY(-1600).setDuration(1000).setStartDelay(4000);
                        lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();

                            }

                        }, 4700);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    imageView.animate().translationY(-1600).setDuration(1000).setStartDelay(4000);
                    lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }, 4700);
        }
    }
}