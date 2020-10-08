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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learningeasle.model.AdapterPost;
import com.example.learningeasle.model.modelpost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class UpdateProfile extends AppCompatActivity {
    EditText username, userphone, useremail, userstatus;
    FirebaseFirestore fstore;
    FirebaseUser user;
    TextView displayname, changeimage;
    StorageReference storageReference;
    Button update;
    String userId;
    ImageView profileimage;
    Uri imageuri;
    String currentPhotoPath;
    private int CAMERA_REQUEST_CODE = 10002;
    private int GALLERY_REQUEST_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
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
        final DocumentReference docref = fstore.collection("users").document(userId);
        docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username.setText((CharSequence) documentSnapshot.get("fName"));
                userphone.setText((CharSequence) documentSnapshot.get("phone"));
                useremail.setText((CharSequence) documentSnapshot.get("email"));
                userstatus.setText((CharSequence) documentSnapshot.get("status"));
                String name = (String) documentSnapshot.get("fName");
                displayname.setText(name);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = username.getText().toString().trim();
                String email = useremail.getText().toString().trim();
                String phone = userphone.getText().toString().trim();
                String status = userstatus.getText().toString().trim();

                if (TextUtils.isEmpty(fullName)) {
                    username.setError("Username is Required.");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    useremail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    userphone.setError("Phone Number is Required.");
                    return;
                }

                if (phone.length() < 10) {
                    userphone.setError("Enter valid Phone Number");
                    return;
                }
                Map<String, Object> user = new HashMap<>();                                                             //user data stored in HashMap
                user.put("fName", fullName);
                user.put("email", email);
                user.put("phone", phone);
                user.put("status", status);
                docref.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        Log.d("tag", "onSuccess: User Status Updated ");
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("tag", "onFailure: " + e.toString());
                    }
                });
            }
        });
        StorageReference fileref = storageReference.child("Users/" + userId + "/Images.jpeg");
        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(profileimage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profileimage.setImageResource(R.drawable.ic_action_account);
            }
        });
        changeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfile.this);
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
                //setting the image view to the user selected image using its URI
                profileimage.setImageURI(imageuri);
                //uplaod iamge to firebase by calling the below method and passing the image uri as parameter
                uploadImageToFirebase(imageuri);
            }

        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                imageuri = Uri.fromFile(f);
                profileimage.setImageURI(imageuri);

                uploadImageToFirebase(Uri.fromFile(f));
            }
        }
    }


    private void uploadImageToFirebase(final Uri imageuri) {


        final StorageReference fileref = storageReference.child("Users/" + userId + "/Images.jpeg");
        fileref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileimage);
                        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        StorageReference reference = FirebaseStorage.getInstance().getReference();
                        final String[] url = new String[1];

                        StorageReference fileref1 = reference.child("Users/" + firebaseUser.getUid() + "/Images.jpeg");
                        fileref1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                url[0] = uri.toString();
                                System.out.println(uri + " == uri");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
                                            if (hashMap.get("pId").equals(firebaseUser.getUid())) {
                                                hashMap.put("url", url[0]);
                                                System.out.println(url[0] + " = imageuri");
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                                ref.child(dataSnapshot.getKey()).setValue(hashMap);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(),"Error Loading",Toast.LENGTH_SHORT).show();
                                    }


                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                url[0] = "empty";
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
                                            if (hashMap.get("pId").equals(firebaseUser.getUid())) {
                                                hashMap.put("url", url[0]);
                                                System.out.println(url[0] + " = imageuri");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(),"Error Loading",Toast.LENGTH_SHORT).show();
                                    }


                                });

                            }
                        });


                    }
                });
            }
        });


    }
}