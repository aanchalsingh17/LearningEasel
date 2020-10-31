package com.example.learningeasle.Interests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.learningeasle.MainActivity;
import com.example.learningeasle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PickInterests extends AppCompatActivity {
    Button btn_science, btn_medication, btn_computers, btn_business, btn_environment, btn_arts, btn_sports, btn_economics, btn_arch;
    DatabaseReference myRef;

    RecyclerView recyclerView;
    ModelInterest interest;
    AdapterInterest adapterInterest;
    List<ModelInterest> modelInterestList;
    String email, username;
    String userId = null;
    ProgressDialog Waiting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_interests);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String folder = sharedPreferences.getString("email_Id", "");
        int j = folder.length() - 4;
        recyclerView = findViewById(R.id.interestRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(PickInterests.this);
        recyclerView.setLayoutManager(layoutManager);
        modelInterestList = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userId = user.getUid();
        }
        //Getting hte reference where all the fields of interest are stored
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("email");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = (String) snapshot.getValue();
                username = email.substring(0, email.length() - 4);
                //After name is retrieved load all the channels with its value create a model of type interest
                //and add this into the list later creater adapter using that list and then set that adapter into our
                //Recycler view
                Waiting = new ProgressDialog(PickInterests.this);
                Waiting.setMessage("Please Wait.....");
                Waiting.setCanceledOnTouchOutside(false);
                Waiting.show();
                loadChannels();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadChannels() {
        DatabaseReference channel = FirebaseDatabase.getInstance().getReference("Users").child(userId).child(username);
        channel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelInterestList.clear();
                for (DataSnapshot value : snapshot.getChildren()) {
                    String name = value.getKey();
                    String follow = (String) value.getValue();
                    interest = new ModelInterest(name, follow);
                    modelInterestList.add(interest);
                }
                adapterInterest = new AdapterInterest(PickInterests.this, modelInterestList);
                recyclerView.setAdapter(adapterInterest);
                Waiting.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }

    //When menu item is clicked if its save then redirect to mainactivity otherwise go to addChannel class
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        MenuItem menuItem = menu.findItem(R.id.save);
        MenuItem addItem = menu.findItem(R.id.add);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        addItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addChannel();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //User want to sendChannel Request go to AddChannel class
    private void addChannel() {
        Intent intent = new Intent(PickInterests.this, AddChannel.class);
        startActivity(intent);


    }
    //Whenever back btn is pressed redirect to mainactivity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}