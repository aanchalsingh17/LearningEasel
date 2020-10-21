package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.learningeasle.Accounts.Login;
import com.example.learningeasle.MainFragments.HomeFragment;
import com.example.learningeasle.MainFragments.PostFragment;
import com.example.learningeasle.MainFragments.ProfileFragment;
import com.example.learningeasle.MainFragments.UsersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigationView1;
        navigationView1 = findViewById(R.id.menu_nav);
        navigationView1.setOnNavigationItemSelectedListener(listener1);

        getSupportFragmentManager().beginTransaction().replace(R.id.scrollable, new HomeFragment(), "H").commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener1 =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedfragment = null;
                    Bundle bundle = new Bundle();
                    switch (item.getItemId()) {
                        case R.id.home:
                            selectedfragment = new HomeFragment();
                            if (getSupportFragmentManager().findFragmentByTag("H") == null) {
                                getSupportFragmentManager().beginTransaction().add(R.id.scrollable, selectedfragment, "H").commit();
                                if (getSupportFragmentManager().findFragmentByTag("P") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("P")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("U") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("A") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("A")).commit();
                            } else {
                                getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("H")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("P") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("P")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("U") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("A") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("A")).commit();
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
                                if (getSupportFragmentManager().findFragmentByTag("A") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("A")).commit();
                            } else {
                                getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("P")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("U") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("H") != null) {
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("H")).commit();
                                }
                                if (getSupportFragmentManager().findFragmentByTag("A") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("A")).commit();
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
                                if (getSupportFragmentManager().findFragmentByTag("A") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("A")).commit();
                            } else {
                                getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("U")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("P") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("P")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("H") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("H")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("A") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("A")).commit();
                            }

                            break;
                        case R.id.post:
                            selectedfragment = new PostFragment();
                            bundle.putString("Edit","AddPost");
                            selectedfragment.setArguments(bundle);
                            if (getSupportFragmentManager().findFragmentByTag("A") == null) {
                                getSupportFragmentManager().beginTransaction().add(R.id.scrollable, selectedfragment, "A").commit();
                                if (getSupportFragmentManager().findFragmentByTag("P") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("P")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("H") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("H")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("U") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                            } else {
                                getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("A")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("P") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("P")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("H") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("H")).commit();
                                if (getSupportFragmentManager().findFragmentByTag("U") != null)
                                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                            }
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