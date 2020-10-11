package com.example.learningeasle;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.HashMap;

public class PostFragment extends Fragment implements View.OnClickListener {

    View view;
    EditText et_title, et_desc;
    FloatingActionButton post_btn;
    ImageView img_post;
    String pName, url;
    Uri image_rui = null;
    ProgressDialog pd;
    ProgressBar progressBar;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    String[] cameraPermissions;
    String[] storagePermissions;
    ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_title = view.findViewById(R.id.title);
        et_desc = view.findViewById(R.id.desc);
        post_btn = view.findViewById(R.id.post_button);
        img_post = view.findViewById(R.id.img);
        progressBar = view.findViewById(R.id.progressBar_addPost);
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.
                WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.
                WRITE_EXTERNAL_STORAGE};
        getUserDeatils();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

//        imgclick(view);
//        postclick(view);
        trigger();

        // Inflate the layout for this fragment


    }

    public void getUserDeatils() {
        final String pId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot db:snapshot.getChildren()) {
                    HashMap<Object, String> hashMap = (HashMap<Object, String>) db.getValue();
                    if (hashMap.get("Id").equals(pId)) {
                        pName = hashMap.get("Name");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        }, 3000);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        StorageReference reference = FirebaseStorage.getInstance().getReference();
        StorageReference fileref = reference.child("Users/" + pId + "/Images.jpeg");
        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url = uri.toString();
//                System.out.println(uri);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                url = "empty";

            }
        });

    }

    private void trigger() {
        post_btn.setOnClickListener(this);
        img_post.setOnClickListener(this);
    }

    private void Post_Data(final String title, final String description, final String uri) {

//        pd=new ProgressDialog(this);
//        pd.setMessage("Publishing Post..");
//        pd.show();
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // String UserId = firebaseUser.getUid();
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_" + timeStamp;
        if (!uri.equals("noImage")) {
            // with image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    String downloadUri = uriTask.getResult().toString();
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());


                    if (uriTask.isSuccessful()) {
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("pId", firebaseUser.getUid());
                        hashMap.put("pImage", downloadUri);
                        hashMap.put("pTitle", title);
                        hashMap.put("pDesc", description);
                        hashMap.put("pTime", timeStamp);
                        hashMap.put("pName", pName);
                        hashMap.put("url", url);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                        ref.child(timeStamp).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                pd.dismiss();
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), "Post published!", Toast.LENGTH_SHORT)
                                                .show();
                                        et_desc.setText("");
                                        et_title.setText("");
                                        img_post.setImageURI(null);
                                        image_rui = null;
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                                pd.dismiss();
                                progressBar.setVisibility(View.GONE);
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

        } else {


            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("pId", firebaseUser.getUid());
            hashMap.put("pImage", "noImage");
            hashMap.put("pTitle", title);
            hashMap.put("pDesc", description);
            hashMap.put("pTime", timeStamp);
            hashMap.put("pName", pName);
            hashMap.put("url", url);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            pd.dismiss();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Post published!", Toast.LENGTH_SHORT)
                                    .show();
                            et_desc.setText("");
                            et_title.setText("");
                            img_post.setImageURI(null);
                            image_rui = null;
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    pd.dismiss();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Whyyy", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private void Image_dialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                    if (!checkStoragePermission())
                        requestStoragePermission();
                    else
                        pickFromGallery();
                }
            }
        });
        builder.create().show();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String pId = firebaseUser.getUid();


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(pId);


        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    pName = documentSnapshot.getString("fName");
//                    holder.uName.setText(pName);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        StorageReference reference = FirebaseStorage.getInstance().getReference();
        StorageReference fileref = reference.child("Users/" + pId + "/Images.jpeg");
        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url = uri.toString();
//                System.out.println(uri);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                url = "empty";

            }
        });

    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Desc");
        image_rui = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        boolean reuslt = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.
                WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return reuslt;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermissions,
                STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.
                CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.
                WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions,
                CAMERA_REQUEST_CODE);
    }

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
                        Toast.makeText(getActivity(), "Camera and Storage permissions are required...",
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
                        Toast.makeText(getActivity(), "Storage permission is required...",
                                Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == -1) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_rui = data.getData();
                img_post.setImageURI(image_rui);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                img_post.setImageURI(image_rui);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img:
                Image_dialog();
                break;
            case R.id.post_button:
                String title = et_title.getText().toString().trim();
                String description = et_desc.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(getActivity(), "Enter Title...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(description)) {
                    Toast.makeText(getActivity(), "Enter Description...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (image_rui != null) {
                    // post with image
                    Post_Data(title, description, String.valueOf(image_rui));
                } else {
//                     post without image
                    Post_Data(title, description, "noImage");
                }
                break;

        }
    }
}
