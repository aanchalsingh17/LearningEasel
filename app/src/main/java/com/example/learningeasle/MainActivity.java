package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userid;
    StorageReference storageReference;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigationView;
       image = findViewById(R.id.profileimage);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userid = user.getUid();
        navigationView = findViewById(R.id.nav);
        navigationView.setOnNavigationItemSelectedListener(listener);
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileref = storageReference.child("Users/" + userid + "/Images.jpeg");
       fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               image.setImageResource(R.drawable.user);
           }
       });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,EditProfile.class));
            }
        });
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
                            finish();
                            break;
                    }

                    return false;
                }


            };
}