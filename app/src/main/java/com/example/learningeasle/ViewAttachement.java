package com.example.learningeasle;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewAttachement extends AppCompatActivity {
    VideoView videoView;
    PDFView pdfView;
    FloatingActionButton play,pause,play2;
    final MediaPlayer mediaPlayer = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attachement);
        videoView = findViewById(R.id.videoView2);
        pdfView = (PDFView) findViewById(R.id.PDFView);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        play2 = findViewById(R.id.play2);
        String url = getIntent().getStringExtra("videourl");
        String pdfurl = getIntent().getStringExtra("pdfurl");
        final String audiourl = getIntent().getStringExtra("audiourl");
        if (!url.equals("empty")) {
            MediaController mediaController = new MediaController(ViewAttachement.this);
            videoView.setMediaController(mediaController);
            videoView.start();
            Uri uri = Uri.parse(url);
            videoView.setVideoURI(uri);
        }else{
            videoView.setVisibility(View.GONE);
        }
        if (!pdfurl.equals("empty")) {
            Uri pdfuri = Uri.parse(pdfurl);
            pdfView.fromUri(pdfuri);

        }else{
            pdfView.setVisibility(View.GONE);
        }
        if(audiourl.equals("empty")){
            play.setVisibility(View.GONE);
        }
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audiourl!=null&&!audiourl.equals("empty")){

                    try {
                        mediaPlayer.setDataSource(audiourl);
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mediaPlayer.start();
                            }
                        });
                        mediaPlayer.prepare();

                    }catch (IOException io){
                        io.printStackTrace();
                    }
                }
                play.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);
            }
        });
        play2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                play2.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                pause.setVisibility(View.INVISIBLE);
                play2.setVisibility(View.VISIBLE);
            }
        });

    }

     /*class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {

                URL urlx = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) urlx.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;

        }
        @Override
        protected void onPostExecute(InputStream inputStream) {
           // pdfView.fromStream.(inputStream).load();
        }
    }*/
}