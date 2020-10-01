package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import com.google.firebase.storage.FirebaseStorage;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Profile extends AppCompatActivity {
    Button upload, register, capture;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    String currentPhotoPath;
    Uri imageuri;
    ImageView image;
    StorageReference storagereference;
    FirebaseStorage storage;
    FirebaseFirestore fStore;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        upload = findViewById(R.id.upload);
        register = findViewById(R.id.register_reg);
        capture = findViewById(R.id.capture);
        storage = FirebaseStorage.getInstance();
        image = findViewById(R.id.imageView);
       //use this storage reference to upload the image
        storagereference = storage.getReference();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent opengalleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //picking and extracting the data in the same intent
                startActivityForResult(opengalleryintent, GALLERY_REQUEST_CODE);
            }
        });
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //permission for opening the camera
                askCameraPermission();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
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
                imageuri = FileProvider.getUriForFile(this, "com.example.tagtodo.fileprovider", PhotoFile);
                TakePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
                startActivityForResult(TakePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                imageuri = data.getData();
                //setting the image view to the user selected image using its URI
                image.setImageURI(imageuri);
                //upload image to firebase by calling the below method and passing the image uri as parameter
                //uploadImageToFirebase(imageuri);
            }

        }
        //ignore the below commented code its for the camera result after opening camera permission is granted
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                imageuri = Uri.fromFile(f);
                image.setImageURI(imageuri);

               // uploadImageToFirebase(Uri.fromFile(f));
            }
        }
    }


    private void uploadImageToFirebase(Uri imageuri) {
        if (imageuri != null) {

             final StorageReference fileref = storagereference.child("Users/" + fUser.getUid()  + "/Images.jpeg");

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //Choose image quality factor
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();

                fileref.putBytes(fileInBytes).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Toast.makeText(AddNote.this, "voila", Toast.LENGTH_SHORT).show();
                        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                               Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(image);
                            }
                        });
                    }
                });
            }
            onBackPressed();
        }


    }
