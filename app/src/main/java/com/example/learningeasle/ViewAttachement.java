package com.example.learningeasle;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
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
    ImageView imageplaypause;
    TextView textCurrenttime,textTotalDuration;
    SeekBar playerSeekbar;
    Handler handler = new Handler();
    FrameLayout frameLayout;
    String audiourl;
    LinearLayout player;
    final MediaPlayer mediaPlayer = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attachement);
        videoView = findViewById(R.id.videoView2);
        pdfView = (PDFView) findViewById(R.id.PDFView);
        frameLayout = findViewById(R.id.frameLayout2);
        imageplaypause = findViewById(R.id.imageplaypause);
        textCurrenttime = findViewById(R.id.text_currenttime);
        textTotalDuration = findViewById(R.id.total_duration);
        playerSeekbar = findViewById(R.id.seekbar_player);
        playerSeekbar.setMax(100);
        player = findViewById(R.id.player);
        String url = getIntent().getStringExtra("videourl");
        String pdfurl = getIntent().getStringExtra("pdfurl");
         audiourl = getIntent().getStringExtra("audiourl");
        if (!url.equals("empty")) {
            frameLayout.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.VISIBLE);
            MediaController mediaController = new MediaController(ViewAttachement.this);
            videoView.setMediaController(mediaController);
            videoView.start();
            Uri uri = Uri.parse(url);
            videoView.setVideoURI(uri);
        }
        if (!pdfurl.equals("empty")) {
            pdfView.setVisibility(View.VISIBLE);
           // Uri pdfuri = Uri.parse(pdfurl);
            new RetrievePDFStream().execute(pdfurl);

        }
        if(!audiourl.equals("empty")){
            player.setVisibility(View.VISIBLE);
        }
        imageplaypause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    imageplaypause.setImageResource(R.drawable.ic_pause);
                }else{
                    mediaPlayer.start();
                    imageplaypause.setImageResource(R.drawable.ic_play);
                    updateSeekBar();

                }
            }
        });
      preparemediaplayer();

    }
    private  void preparemediaplayer(){
        try{
            mediaPlayer.setDataSource(audiourl);
            mediaPlayer.prepare();
            textTotalDuration.setText(millisecondsToTimer(mediaPlayer.getDuration()));
        }catch (Exception exception){

        }
    }
    private  Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textCurrenttime.setText(millisecondsToTimer(currentDuration));
        }
    };
    private  void updateSeekBar(){
        if(mediaPlayer.isPlaying()){
            playerSeekbar.setProgress((int)(((float) mediaPlayer.getCurrentPosition()/ mediaPlayer.getDuration())*100));
            handler.postDelayed(updater,1000);
        }
    }
    private String millisecondsToTimer(long milliseconds){
        String timerString = "";
        String secondsString;
        int hours = (int)(milliseconds/(1000*60*60));
        int minutes = (int)(milliseconds%(1000*60*60))/(1000*60);
        int seconds = (int)((milliseconds%(1000*60*60))%(1000*60))/1000;
        if(hours>0){
            timerString = hours+":";
        }
        if(seconds<10){
            secondsString = "0"+seconds;
        }else{
            secondsString = "" +seconds;
        }
        timerString = timerString + minutes + ":" +secondsString;
        return  timerString;
    }
     class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {
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
            pdfView.fromStream(inputStream).load();
        }
    }
}