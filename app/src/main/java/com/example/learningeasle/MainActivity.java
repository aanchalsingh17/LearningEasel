package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView  navigationView1;
        navigationView1 = findViewById(R.id.menu_nav);
        navigationView1.setOnNavigationItemSelectedListener(listener1);
        getSupportFragmentManager().beginTransaction().replace(R.id.scrollable, new HomeFragment(), "H").commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener1 =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedfragment = null;
                    switch (item.getItemId()) {
                        case R.id.home:
                            selectedfragment = new HomeFragment();
                            if (getSupportFragmentManager().findFragmentByTag("H") == null) {
                                getSupportFragmentManager().beginTransaction().add(R.id.scrollable, selectedfragment, "H").commit();
                                if (getSupportFragmentManager().findFragmentByTag("P") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("P")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("U") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                            } else {
                                getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("H")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("P") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("P")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("U") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                            }

                            break;
                        case R.id.profile:
                           selectedfragment = new ProfileFragment();
                             if (getSupportFragmentManager().findFragmentByTag("P") == null) {
                                getSupportFragmentManager().beginTransaction().add(R.id.scrollable, selectedfragment, "P").commit();
                                if (getSupportFragmentManager().findFragmentByTag("U") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("H") != null) {

                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("H")).commit();

                                }
                            } else {
                                getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("P")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("U") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("H") != null) {
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("H")).commit();
                                }
                            }
                            //startActivity(new Intent(MainActivity.this,EditProfile.class));
                            break;
                        case R.id.account:
                            selectedfragment = new UsersFragment();
                            if (getSupportFragmentManager().findFragmentByTag("U") == null) {
                                getSupportFragmentManager().beginTransaction().add(R.id.scrollable, selectedfragment, "U").commit();
                                if (getSupportFragmentManager().findFragmentByTag("P") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("P")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("H") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("H")).commit();
                            } else {
                                getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("U")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("P") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("P")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("H") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("H")).commit();
                            }

                            break;
                        case R.id.post:
                            Intent intent = new Intent(getApplicationContext(), Add_Post.class);
                            startActivity(intent);
                            break;
                        case R.id.logout:
                            FirebaseAuth fba;
                            fba = FirebaseAuth.getInstance();
                            FirebaseUser user = fba.getCurrentUser();
                            Intent intent1 = new Intent(getApplicationContext(), Login.class);
                            fba.signOut();
                            startActivity(intent1);
                            finish();
                            break;
                    }

                    return true;
                }
            };




}