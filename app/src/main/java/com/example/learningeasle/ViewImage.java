package com.example.learningeasle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ViewImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        String image=getIntent().getStringExtra("image");
        ImageView imageView=findViewById(R.id.imageView);
        if (image.equals("noImage")) {
            imageView.setImageResource(R.drawable.ic_pic);
        } else {
            try {
                Picasso.get().load(image).placeholder(R.drawable.ic_default).into(imageView);
            } catch (Exception e) {

            }
        }
    }
}