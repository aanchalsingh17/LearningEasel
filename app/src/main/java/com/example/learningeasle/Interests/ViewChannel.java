package com.example.learningeasle.Interests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningeasle.MainActivity;
import com.example.learningeasle.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ViewChannel extends AppCompatActivity {
    TextView channelName,channelDes;
    ImageView coverImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_channel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        channelName = findViewById(R.id.name);
        channelDes = findViewById(R.id.desciption);
        coverImage = findViewById(R.id.coverImage);
        //Getting the channel name from the intent
        String channelname= getIntent().getStringExtra("channel");
        setChannelDes(channelname);

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

    private void setChannelDes(String channelname) {
        //Setting the channel details from the channel name
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin").child("channel").child(channelname);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<Object,String> channel_info = (HashMap<Object, String>) snapshot.getValue();
                setTitle(channel_info.get("cName"));
                channelName.setText(channel_info.get("cName"));
                channelDes.setText(channel_info.get("cDes"));
                String url = channel_info.get("cUrl");
                Picasso.get().load(url).into(coverImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}