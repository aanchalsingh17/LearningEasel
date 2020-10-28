package com.example.learningeasle.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.learningeasle.Accounts.Login;
import com.example.learningeasle.MainFragments.HomeFragment;
import com.example.learningeasle.MainFragments.UsersFragment;
import com.example.learningeasle.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class AdminMainPage extends AppCompatActivity {
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main_page);
        BottomNavigationView navigationView;
        navigationView = findViewById(R.id.menu_nav);
        navigationView.setOnNavigationItemSelectedListener(listener);
        //By default in the starting load the main page
        getSupportFragmentManager().beginTransaction().replace(R.id.scroll,new AdminPostFragment(),"H").commit();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            userId = user.getUid();
            SharedPreferences sharedPreferences = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Current_USERID",userId);
            editor.apply();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedItem = null;
            //Get the item id then accordingly perform the action
            switch (item.getItemId()){
                case R.id.home:
                    selectedItem = new AdminPostFragment();
                    //if Current fragment is not home fragment than find the current fragment and replace accordingly
                    if(getSupportFragmentManager().findFragmentByTag("H")==null){
                        getSupportFragmentManager().beginTransaction().add(R.id.scroll, selectedItem, "H").commit();
                        if (getSupportFragmentManager().findFragmentByTag("U") != null)
                            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                        if (getSupportFragmentManager().findFragmentByTag("P") != null)
                            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("P")).commit();

                    }else{
                        getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("H")).commit();
                        if (getSupportFragmentManager().findFragmentByTag("U") != null)
                            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                        if (getSupportFragmentManager().findFragmentByTag("P") != null)
                            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("P")).commit();
                    }
                    break;
                case R.id.account:
                    selectedItem = new UsersFragment();
                    if (getSupportFragmentManager().findFragmentByTag("U") == null) {
                        getSupportFragmentManager().beginTransaction().add(R.id.scroll, selectedItem, "U").commit();
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
                case R.id.notification:
                    selectedItem = new PendingPost();
                    if (getSupportFragmentManager().findFragmentByTag("P") == null) {
                        getSupportFragmentManager().beginTransaction().add(R.id.scroll, selectedItem, "P").commit();
                        if (getSupportFragmentManager().findFragmentByTag("U") != null)
                            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                        if (getSupportFragmentManager().findFragmentByTag("H") != null)
                            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("H")).commit();

                    } else {
                        getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("P")).commit();
                        if (getSupportFragmentManager().findFragmentByTag("U") != null)
                            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("U")).commit();
                        if (getSupportFragmentManager().findFragmentByTag("H") != null)
                            getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("H")).commit();
                    }
                     break;
                    //Admin Logout
                case R.id.logout:
                    FirebaseAuth fba;
                    fba = FirebaseAuth.getInstance();
                    Intent intent1 = new Intent(getApplicationContext(), Login.class);
                    fba.signOut();
                    startActivity(intent1);
                    finish();
            }
            return true;
        }
    };
}