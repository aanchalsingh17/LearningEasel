package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigationView;
        navigationView = findViewById(R.id.nav);
        navigationView.setOnNavigationItemSelectedListener(listener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            break;
                        case R.id.post:
                            Intent intent = new Intent(getApplicationContext(), Add_Post.class);
                            startActivity(intent);
                            break;
                        case R.id.logout:
                            FirebaseAuth fba;
                            fba=FirebaseAuth.getInstance();
                            FirebaseUser user =fba.getCurrentUser();
                            Intent intent1 = new Intent(getApplicationContext(),Register.class);
                            fba.signOut();
                            startActivity(intent1);
                            break;
                    }

                    return false;
                }


            };
}