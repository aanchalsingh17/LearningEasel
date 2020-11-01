package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.learningeasle.Accounts.Login;
import com.example.learningeasle.MainFragments.HomeFragment;
import com.example.learningeasle.MainFragments.PostFragment;
import com.example.learningeasle.MainFragments.ProfileFragment;
import com.example.learningeasle.MainFragments.UsersFragment;
import com.example.learningeasle.PushNotifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigationView1;
        BottomSheetDialog.bottomSheetDialog=null;
        navigationView1 = findViewById(R.id.menu_nav);
        navigationView1.setOnNavigationItemSelectedListener(listener1);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(firebaseUser.getUid()).setValue(token);

        getSupportFragmentManager().beginTransaction().replace(R.id.scrollable, new HomeFragment(), "H").commit();

    }



    private BottomNavigationView.OnNavigationItemSelectedListener listener1 =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    BottomSheetDialog.bottomSheetDialog=null;
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
                            SharedPreferences sharedPreferences=getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();

//                            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);

//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.putExtra("EXIT", true);
//                            startActivity(intent);
//                            finish();
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