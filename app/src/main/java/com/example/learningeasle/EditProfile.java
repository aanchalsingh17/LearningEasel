package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProfile extends AppCompatActivity {
    ImageView profile;
    FirebaseUser user;
    String userid;
    StorageReference reference;
    TextView username;
    ImageView ivcamera;
    Integer GALLERY_REQUEST_CODE = 101;
    Integer CAMERA_REQUEST_CODE = 102;
    Uri imageuri;
    String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        profile = findViewById(R.id.image);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        reference = FirebaseStorage.getInstance().getReference();
        username = findViewById(R.id.username);
        ivcamera = findViewById(R.id.iv_camera);
        StorageReference fileref = reference.child("Users/" + userid + "/Images.jpeg");
        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profile.setImageResource(R.drawable.ic_action_account);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this,FullView.class));
            }
        });
        ivcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            askCameraPermission();
                        }

                        if (i == 1) {
                            Intent opengalleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            //picking and extracting the data in the same intent
                            startActivityForResult(opengalleryintent, GALLERY_REQUEST_CODE);
                        }
                    }
                });
                builder.create().show();
            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                imageuri = data.getData();
                //setting the image view to the user selected image using its URI
                profile.setImageURI(imageuri);
                //uplaod iamge to firebase by calling the below method and passing the image uri as parameter
                uploadImageToFirebase(imageuri);
            }

        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                imageuri = Uri.fromFile(f);
                profile.setImageURI(imageuri);

                uploadImageToFirebase(Uri.fromFile(f));
            }
        }
    }
    private void askCameraPermission() {
        //check if permission is granted or not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            dispatchTakePictureIntent();
        }
    }

    //check if camera permission is granted or not then accordingly perform the action
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }
        } else {
            Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show();
        }
    }
    private File createImageFile() throws IOException {
        //create n image file name
        String TimeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String ImageFileName = "JPEG_" + TimeStamp + "_";
        File storagedir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // File storagedir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File profileimage = File.createTempFile(
                ImageFileName, ".jpg", storagedir
        );
        currentPhotoPath = profileimage.getAbsolutePath();
        return profileimage;
    }


    private void dispatchTakePictureIntent() {
        Intent TakePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (TakePictureIntent.resolveActivity(getPackageManager()) != null) {
            File PhotoFile = null;
            try {
                PhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (PhotoFile != null) {
                imageuri = FileProvider.getUriForFile(this, "com.example.learningeasle.fileprovider", PhotoFile);
                TakePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
                startActivityForResult(TakePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageuri) {
        final StorageReference fileref = reference.child("Users/" + userid + "/Images.jpeg");
        fileref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profile);
                    }
                });
            }
        });
    }
}