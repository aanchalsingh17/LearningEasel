package com.example.learningeasle;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewAttachement extends AppCompatActivity {
    VideoView videoView;
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attachement);
        videoView = findViewById(R.id.videoView2);
        pdfView = (PDFView) findViewById(R.id.PDFView);
        String url = getIntent().getStringExtra("videourl");
        String pdfurl = getIntent().getStringExtra("pdfurl");
        if (!url.equals("empty")) {
            MediaController mediaController = new MediaController(ViewAttachement.this);
            videoView.setMediaController(mediaController);
            videoView.start();
            Uri uri = Uri.parse(url);
            videoView.setVideoURI(uri);
        }
        if (!pdfurl.equals("empty")) {
            Uri pdfuri = Uri.parse(pdfurl);
            pdfView.fromUri(pdfuri);

        }
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