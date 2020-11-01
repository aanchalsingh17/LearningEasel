package com.example.learningeasle.Interests;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.learningeasle.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddChannel extends AppCompatActivity {
    ImageView coverImage;
    EditText title,Des;
    Button add_channel;
    Uri imageuri = null;
    boolean admin = false;
    String uid;
    String channelName,description,imageurl="empty";
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    String[] cameraPermissions;
    String[] storagePermissions;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_channel);
        coverImage = findViewById(R.id.coverImage);
        title = findViewById(R.id.title);
        Des = findViewById(R.id.des);

        progressDialog=new ProgressDialog(AddChannel.this);

        add_channel = findViewById(R.id.add_channel);
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.
                WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.
                WRITE_EXTERNAL_STORAGE};

        //Check if User is admin or not;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            uid = user.getUid();
        }
        //Id user is admin then set text to add channel instead od send request
        DatabaseReference adminref = FirebaseDatabase.getInstance().getReference("admin").child("Id");
        adminref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(uid)){
                    admin = true;
                    add_channel.setText("Add Channel");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Whenever add channel btn is clicked check if any of the required field is empty or not if not then create a new channel or
        //send request to admin
        add_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                channelName = title.getText().toString();
                description = Des.getText().toString();
                if(imageuri==null||channelName.isEmpty()||description.isEmpty()) {
                    if (imageuri == null) {
                        Toast.makeText(AddChannel.this, "Add Cover Image", Toast.LENGTH_SHORT).show();
                    }

                    if (channelName.isEmpty()) {
                        Toast.makeText(AddChannel.this, "Add Channel Name", Toast.LENGTH_SHORT).show();
                    }
                    if (description.isEmpty()) {
                        Toast.makeText(AddChannel.this, "Add Channel Description", Toast.LENGTH_SHORT).show();
                    }
                }
                    //send ChannelRequest
                    else {

                    progressDialog.setMessage("Applying Changes...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    DatabaseReference allChannels = FirebaseDatabase.getInstance().getReference("admin").child("channel");
                    allChannels.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(channelName)) {
                                Toast.makeText(AddChannel.this, "Channel Name Exists", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                uploadImagetoFirebase();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                        }
                    });

                }
            }
        });
        //Cover Image is clicked Open the image dialog to choose the image
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image_Dialog();
            }
        });
    }

    //If image uri is not null then upload the image from the firebase and download its url to add to realtime database
    private void uploadImagetoFirebase() {
        if(imageuri!=null){
            String path = "Channel/"+channelName;
            final StorageReference image = FirebaseStorage.getInstance().getReference().child(path);
            image.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageurl = uri.toString();
                            sendChannelRequest();
                        }
                    });
                }
            });

        }
    }


    private void Image_Dialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddChannel.this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //If camera permission is not granted then ask for it otherwise open camera
                if (i == 0) {
                    if (!checkCameraPermission())
                        requestCameraPermission();
                    else
                        pickFromCamera();
                }

                //If storage permission is not granted then ask for it otherwise open gallery
                if (i == 1) {
                    if (!checkStoragePermission())
                        requestStoragePermission();
                    else
                        pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    //Intent to pick a image from gallery
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    //REquest for the storage permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions,
                STORAGE_REQUEST_CODE);
    }

    //Checking is permission to read the storage is given or not
    private boolean checkStoragePermission() {
        boolean reuslt = ContextCompat.checkSelfPermission(this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return reuslt;
    }

    //Pick an image from the camera
    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Desc");
        imageuri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        //Start activity for result with request code
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    //Checking for the camera permission
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(AddChannel.this, Manifest.permission.
                CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(AddChannel.this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    //Request for the camera permission
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions,
                CAMERA_REQUEST_CODE);
    }

    //After the request is made and action is taken check if permission is granted or not if not granted then show a toast
    //If granted then open camera or gallery whtever be the action
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted)
                        pickFromCamera();
                    else
                        Toast.makeText(this, "Camera and Storage permissions are required...",
                                Toast.LENGTH_SHORT).show();
                } else {


                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted)
                        pickFromGallery();
                    else
                        Toast.makeText(this, "Storage permission is required...",
                                Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    //After image is taken from any of the source set it onto the image view and save its uri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            //If image is picked from gallery then create is uri and then set it into image view
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                imageuri = data.getData();
                coverImage.setImageURI(imageuri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                coverImage.setImageURI(imageuri);
            }
        }
    }

    //Sending the channel request to the admin with details as an request
    //and if current user is admin then simply add the channel
    private void sendChannelRequest() {
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("cName", channelName);
        hashMap.put("cDes", description);
        hashMap.put("cUrl", imageurl);
        if (admin) {
            final DatabaseReference channelReference = FirebaseDatabase.getInstance().getReference("admin").child("channel");
            channelReference.child(channelName).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddChannel.this,"New Channel Added",Toast.LENGTH_SHORT).show();
                    //Whenever Admin Add new Channel  add the channel into the users profile
                    final DatabaseReference user_profile = FirebaseDatabase.getInstance().getReference("Users");
                    user_profile.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot user:snapshot.getChildren()){
                                String user_email = (String) user.child("email").getValue();
                                String Id = (String) user.child("Id").getValue();
                                int j=user_email.length()-4;
                                final String username=user_email.substring(0,j);
                                user_profile.child(Id).child(username).child(channelName).setValue("0");
                                progressDialog.dismiss();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                        }
                    });
                    Des.setText("");
                    title.setText("");
                    imageuri = null;
                    coverImage.setImageResource(R.drawable.ic_image);
                }
            });

        } else {
            final DatabaseReference channelReference = FirebaseDatabase.getInstance().getReference("admin").child("pendingchannel");
            channelReference.child(channelName).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddChannel.this, "New Channel Request Send to Admin", Toast.LENGTH_SHORT).show();
                    Des.setText("");
                    title.setText("");
                    imageuri = null;
                    coverImage.setImageResource(R.drawable.ic_image);
                    progressDialog.dismiss();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddChannel.this, "Unable to send Request", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }
}