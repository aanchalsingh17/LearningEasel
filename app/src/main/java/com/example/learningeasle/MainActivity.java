package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
   ActionBar actionBar;
   FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Home");
       /* HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container,homeFragment,"");*/
        firebaseAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                                  actionBar.setTitle("Home");
                                  HomeFragment homeFragment = new HomeFragment();
                                  FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                  fragmentTransaction.replace(R.id.container,homeFragment,"");
                                  break;
                    case R.id.account:
                                  actionBar.setTitle("Account");
                                  ProfileFragment accountFragment = new ProfileFragment();
                                  FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                  transaction.replace(R.id.container,accountFragment,"");
                                  break;
                    case R.id.profile:
                                  actionBar.setTitle("Profile");
                                  ProfileFragment profileFragment = new ProfileFragment();
                                  FragmentTransaction fragment = getSupportFragmentManager().beginTransaction();
                                  fragment.replace(R.id.container,profileFragment,"");
                                  break;
                }
            }
        });
    }
}