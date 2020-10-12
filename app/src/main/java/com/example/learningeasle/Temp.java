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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Temp extends AppCompatActivity {
    EditText username, userphone, useremail, userstatus;
    FirebaseFirestore fstore;
    FirebaseUser user;
    TextView displayname, changeimage;
    StorageReference storageReference;
    Button update;
    String userId;
    ImageView profileimage;
    Uri imageuri=null;
    String currentPhotoPath;
    String url;
    private int CAMERA_REQUEST_CODE = 10002;
    private int GALLERY_REQUEST_CODE = 10001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        username = findViewById(R.id.personname);
        userphone = findViewById(R.id.userphone);
        useremail = findViewById(R.id.useremail);
        userstatus = findViewById(R.id.userstatus);
        fstore = FirebaseFirestore.getInstance();
        profileimage = findViewById(R.id.profile);
        displayname = findViewById(R.id.displayname);
        storageReference = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        update = findViewById(R.id.update);
        userId = user.getUid();
        changeimage = findViewById(R.id.changeimage);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot db:snapshot.getChildren()){
                    HashMap<Object,String> hashMap = (HashMap<Object, String>) db.getValue();
                    if(hashMap.get("Id").equals(userId)){
                        username.setText(hashMap.get("Name"));
                        useremail.setText(hashMap.get("email"));
                        userstatus.setText(hashMap.get("status"));
                        userphone.setText(hashMap.get("phone"));
                        displayname.setText(hashMap.get("Name"));
                        url = hashMap.get("Url");
                        if(url.equals("empty"))
                            profileimage.setImageResource(R.drawable.ic_action_account);
                        else
                            Picasso.get().load(url).into(profileimage);
                        //url = hashMap.get("Url");
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToFirebase(imageuri);

            }
        });
        changeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Temp.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                imageuri = data.getData();
                url = imageuri.toString();
                //setting the image view to the user selected image using its URI
                profileimage.setImageURI(imageuri);
                //uplaod iamge to firebase by calling the below method and passing the image uri as parameter
               // uploadImageToFirebase(imageuri);

            }

        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                imageuri = Uri.fromFile(f);
                url = imageuri.toString();
                profileimage.setImageURI(imageuri);
                //uploadImageToFirebase(imageuri);

            }
        }
    }

    private void uploadImageToFirebase(final Uri image_uri) {
        if(image_uri!=null) {
            final StorageReference fileref = storageReference.child("Users/" + userId + "/Images.jpeg");
            fileref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(profileimage);
                            // url = uri.toString();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            profileimage.setImageResource(R.drawable.ic_action_account);
                            // url = "empty";
                        }
                    });
                }
            });
        }
            final String name = username.getText().toString().trim();
            String email = useremail.getText().toString().trim();
            String phone = userphone.getText().toString().trim();
            String status = userstatus.getText().toString().trim();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
            final Map<String, Object> hashmap = new HashMap<>();
            hashmap.put("Id", userId);
            hashmap.put("Name", name);
            hashmap.put("Url", url);
            hashmap.put("email", email);
            hashmap.put("phone", phone);
            hashmap.put("status", status);
            db.child(userId).updateChildren(hashmap);
           final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds:snapshot.getChildren()){
                        HashMap<String,Object> hashMap = (HashMap<String, Object>) ds.getValue();
                        if(hashMap.get("pId").equals(userId)){
                            hashMap.put("pName",name);
                            hashMap.put("url",url);
                            databaseReference.child(ds.getKey()).updateChildren(hashMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }
}