package com.example.learningeasle;

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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.in;

public class Add_Post extends AppCompatActivity {
    EditText et_title, et_desc;
    Button post_btn;
    ImageView img_post;
    Uri image_rui=null;
    ProgressDialog pd;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE =400;

    String[] cameraPermissions;
    String[] storagePermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__post);
        et_title = findViewById(R.id.title);
        et_desc = findViewById(R.id.desc);
        post_btn = findViewById(R.id.post_button);
        img_post = findViewById(R.id.img);
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.
                WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.
                WRITE_EXTERNAL_STORAGE};

        img_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Image_dialog();
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = et_title.getText().toString().trim();
                String description = et_desc.getText().toString().trim();
//                System.out.println(title+description+".....................");
                if (TextUtils.isEmpty(title))
                {Toast.makeText(getApplicationContext(),"Enter Title...",Toast.LENGTH_SHORT).show();
                return;}
                if (TextUtils.isEmpty(description))
                {Toast.makeText(getApplicationContext(),"Enter Description...",Toast.LENGTH_SHORT).show();
                    return;}
//System.out.println(image_rui);
                if(image_rui!=null)
                {
                    // post with image
                    Post_Data(title,description,String.valueOf(image_rui));
                }
                else
                {
//                     post without image
                    Post_Data(title,description,"noImage");
                }
            }
        });

    }

    private void Post_Data(final String title, final String description, String uri) {
        pd=new ProgressDialog(this);
        pd.setMessage("Publishing Post..");
        pd.show();
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
       // String UserId = firebaseUser.getUid();
        final String timeStamp= String.valueOf(System.currentTimeMillis());
        String filePathAndName="Posts/"+ "post_"+timeStamp;
        if(!uri.equals("noImage"))
        {
            // with image
            StorageReference ref= FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                        String downloadUri = uriTask.getResult().toString();
                    if(uriTask.isSuccessful())
                    {
                        HashMap<Object,String>hashMap=new HashMap<>();
                        hashMap.put("pId",firebaseUser.getUid());
                        hashMap.put("pTitle",title);
                        hashMap.put("pDesc",description);
                        hashMap.put("pImage",downloadUri);
                        hashMap.put("pTime",timeStamp);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                        ref.child(timeStamp).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(),"Post published!",Toast.LENGTH_SHORT)
                                        .show();
                                et_desc.setText("");
                                et_title.setText("");
                                img_post.setImageURI(null);
                                image_rui=null;
                                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
//                                Toast.makeText(getApplicationContext(),"Whyyy",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            pd.dismiss();
                        }
                    });

        }
        else{
//            System.out.println("................................");
            HashMap<Object,String>hashMap=new HashMap<>();
            hashMap.put("pId",firebaseUser.getUid());
            hashMap.put("pTitle",title);
            hashMap.put("pDesc",description);
            hashMap.put("pImage","noImage");
            hashMap.put("pTime",timeStamp);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"Post published!",Toast.LENGTH_SHORT)
                                    .show();
                            et_desc.setText("");
                            et_title.setText("");
                            img_post.setImageURI(null);
                            image_rui=null;
                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);                                               
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Whyyy",Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void Image_dialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Add_Post.this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (!checkCameraPermission())
                        requestCameraPermission();
                    else
                        pickFromCamera();
                }

                if (i == 1) {
                    if(!checkStoragePermission())
                        requestStoragePermission();
                    else
                        pickFromGallery();
                }
            }
        });
        builder.create().show();

    }

    private void pickFromCamera() {
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp Desc");
        image_rui=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_rui);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        boolean reuslt = ContextCompat.checkSelfPermission(Add_Post.this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return reuslt;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(Add_Post.this, storagePermissions,
                STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(Add_Post.this, Manifest.permission.
                CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(Add_Post.this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(Add_Post.this, cameraPermissions,
                CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted)
                        pickFromCamera();
                    else
                        Toast.makeText(Add_Post.this,"Camera and Storage permissions are required...",
                                Toast.LENGTH_SHORT).show();
                }
                else{


                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted)
                        pickFromGallery();
                    else
                        Toast.makeText(Add_Post.this,"Storage permission is required...",
                                Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK)
        {
            if(requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                image_rui= data.getData();
                img_post.setImageURI(image_rui);
            }
            else if( requestCode== IMAGE_PICK_CAMERA_CODE){
                img_post.setImageURI(image_rui);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
