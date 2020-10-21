package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AudioVideo extends AppCompatActivity {
    TextView playerPosition,playerDuration;
    SeekBar seekBar;
    ImageView btn_pause,btn_play;
    MediaPlayer mediaPlayer;
    VideoView videoView;
    Handler handler = new Handler();
    Runnable runnable;
    Button pick;
   // Button video=null;
    Uri videouri;
    private int AUDIO_CODE = 101;
    private int VIDEO = 102;
   StorageReference reference;
   DatabaseReference databaseReference;
   UploadTask uploadTask;
   ImageView video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_video);
        reference = FirebaseStorage.getInstance().getReference("Video");
        databaseReference = FirebaseDatabase.getInstance().getReference("video");
       /* playerPosition = findViewById(R.id.player_position);
        playerDuration = findViewById(R.id.player_duration);
        seekBar = findViewById(R.id.seekbar);
        btn_pause= findViewById(R.id.btn_pause);
        btn_play = findViewById(R.id.btn_play);*/
        //pick = findViewById(R.id.button);
        //video = findViewById(R.id.video);
        video = findViewById(R.id.videopick);
        videoView = findViewById(R.id.videoView);
        if(videouri==null){
            videoView.setVisibility(View.INVISIBLE);
        }
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo(v);
                MediaController mediaController = new MediaController(AudioVideo.this);
                videoView.setMediaController(mediaController);
                videoView.start();
            }
        });
        /*pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,AUDIO_CODE);
            }
        });*/

    }
    public void  chooseVideo(View view){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,108);
    }
    //defineType;
    private  String getExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==108){

                videouri = data.getData();
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(videouri);
                uploadVideo(videouri);
            }
        }
    }

    private void uploadVideo(Uri videouri) {
        if(videouri!=null){
            final StorageReference des = reference.child(System.currentTimeMillis() + "." +getExt(videouri));
            uploadTask = des.putFile(videouri);
            des.putFile(videouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    des.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            databaseReference.child("Video").child(String.valueOf(System.currentTimeMillis())).setValue(uri.toString());
                        }
                    });
                }
            });
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return  reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri dowmloadurl = task.getResult();
                        String path = databaseReference.push().getKey();
                        databaseReference.child(path).setValue(dowmloadurl.toString());
                    }
                }
            });
        }
    }
}