package com.example.learningeasle.MainFragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.learningeasle.MainActivity;
import com.example.learningeasle.R;
import com.example.learningeasle.ViewAttachement;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

//import net.dankito.richtexteditor.android.RichTextEditor;
//import net.dankito.richtexteditor.android.toolbar.AllCommandsEditorToolbar;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class PostFragment extends Fragment implements View.OnClickListener {

    View view;
    UploadTask uploadTask;
     EditText et_title, et_desc;
    FloatingActionButton post_btn,video_btn,view_attached,pdf_btn,audio_btn,view_btn;
    ImageView img_post;
    String pName, url="empty";
    Uri image_rui = null,videouri=null,pdfuri=null,audiouri=null;
    ProgressDialog pd;
    ProgressBar progressBar;
    String edit,id,time,title,des,image,email;
    String pLikes="0",pComments="0";
    Spinner spinner;
    ProgressDialog postDialog;
    String videourl="empty",pdfUrl="empty",audiourrl="empty",imageurl= "empty";
     String timeStamp;
    String audio_ref="empty",video_ref = "empty",pdf_ref = "empty";
    ArrayAdapter<String> adapter;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    StorageReference reference;
    String[] cameraPermissions;
    String[] storagePermissions;
    ArrayList<String> interests;
    ProgressDialog progressDialog;
    private int PDF_REQUEST = 1004;
    private int AUDIO_REQUEST=104;
    boolean visible = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_post, container, false);
        edit = getArguments().getString("Edit");
        if(edit.equals("EditPost")) {
            id = getArguments().getString("Id");
            time = getArguments().getString("pTime");
            title = getArguments().getString("Title");
            des = getArguments().getString("Des");
            image = getArguments().getString("Url");
            pLikes = getArguments().getString("Likes");
            pComments = getArguments().getString("Comments");
        }
        // Inflate the layout for this fragment
        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_title = view.findViewById(R.id.title);
        et_desc = view.findViewById(R.id.desc);
        post_btn = view.findViewById(R.id.post_button);
        img_post = view.findViewById(R.id.img);
        progressBar = view.findViewById(R.id.progressBar_addPost);
        video_btn = view.findViewById(R.id.video_upload);
        view_attached = view.findViewById(R.id.view_attached);
        pdf_btn = view.findViewById(R.id.pdf_upload);
        view_btn = view.findViewById(R.id.view);
        audio_btn = view.findViewById(R.id.audio_upload);
        //Setting the url null so that when user posted continuosly we dont hold the last value of url for this post
        audiouri = null;
        pdfuri = null;
        videouri = null;
        //Initialise timestamp here

        //When attached button is clicked make visiblity of video n pdf button visible

        view_attached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If views are visible make them invisible and vice-versa
                if(!visible) {
                    video_btn.setVisibility(View.VISIBLE);
                    pdf_btn.setVisibility(View.VISIBLE);
                   view_btn.setVisibility(View.VISIBLE);
                    audio_btn.setVisibility(View.VISIBLE);
                    visible = true;
                }else{
                    video_btn.setVisibility(View.INVISIBLE);
                    pdf_btn.setVisibility(View.INVISIBLE);
                    view_btn.setVisibility(View.INVISIBLE);
                    audio_btn.setVisibility(View.INVISIBLE);
                    visible = false;
                }
            }
        });
        //pdfbtn is clicked pdf attachment work started
        pdf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent,PDF_REQUEST);
            }
        });
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.
                WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.
                WRITE_EXTERNAL_STORAGE};
        interests=new ArrayList<>();

        adapter= new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1);

        reference = FirebaseStorage.getInstance().getReference();
        spinner = (Spinner) view.findViewById(R.id.spinner);

        //When view btn is clicked then pass the url of files and show them in viewattached class
        view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit.equals("EditPost")) {
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(time);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            audio_ref = (String) snapshot.child("audiourl").getValue();
                            video_ref = (String) snapshot.child("videourl").getValue();
                            pdf_ref = (String) snapshot.child("pdfurl").getValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                viewAttachedFile();
            }
        });

        getUserDetails();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //Video btn is clicked choose video from the external source and upload it to firebase
        video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo(v);
            }
        });
        //If its edit post means we already have some title and content retrieved from the intent
        if(edit.equals("EditPost")){
            et_title.setText(title);
            et_desc.setText(des);
            if(!image.equals("noImage"))
                Picasso.get().load(image).into(img_post);
        }
//        imgclick(view);
//        postclick(view);
        //audio_btn is clicked upload audio file
        audio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,AUDIO_REQUEST);
            }
        });
        trigger();

        // Inflate the layout for this fragment


    }


    private void viewAttachedFile() {
        String[] options = {"Audio", "Video","Pdf"};
        //If Edit Post then go and check if file is already attached on firebase or not

        final Intent view_page = new Intent(getActivity(), ViewAttachement.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        if(audiouri!=null) {
                            view_page.putExtra("videourl","empty");
                            view_page.putExtra("audiourl",audiouri.toString());
                            view_page.putExtra("pdfurl","empty");
                            startActivity(view_page);
                        }
                        else if(!audio_ref.equals("empty")){
                              view_page.putExtra("videourl","empty");
                              view_page.putExtra("audiourl",audio_ref);
                              view_page.putExtra("pdfurl","empty");
                              startActivity(view_page);
                          }else{
                              Toast.makeText(getContext(),"No Audio File Attached!!",Toast.LENGTH_SHORT).show();
                          }
                          break;
                    case 1:
                        if(videouri!=null){
                            view_page.putExtra("videourl",videouri.toString());
                            view_page.putExtra("audiourl","empty");
                            view_page.putExtra("pdfurl","empty");
                            startActivity(view_page);
                        }
                        else if(!video_ref.equals("empty")){
                            view_page.putExtra("videourl",video_ref);
                            view_page.putExtra("audiourl","empty");
                            view_page.putExtra("pdfurl","empty");
                            startActivity(view_page);
                        }else{
                            Toast.makeText(getContext(),"No Video File Attached!!",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        if(pdfuri!=null){
                            view_page.putExtra("videourl","empty");
                            view_page.putExtra("audiourl","empty");
                            view_page.putExtra("pdfurl",pdfuri.toString());
                            view_page.putExtra("type","local_url");
                            startActivity(view_page);
                        }
                        else if(!pdf_ref.equals("empty")){
                            view_page.putExtra("videourl","empty");
                            view_page.putExtra("audiourl","empty");
                            view_page.putExtra("pdfurl",pdf_ref);
                            startActivity(view_page);
                        }else{
                            Toast.makeText(getContext(),"No Pdf File Attached!!",Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
            }
        });
        builder.create().show();

    }

    //Choose video using intent
    private void chooseVideo(View v) {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,108);

    }

    //Get user details to show only those channels which user follow show that he will be able to post in only those channels
    public void getUserDetails() {
        final String pId = FirebaseAuth.getInstance().getCurrentUser().getUid();



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot db:snapshot.getChildren()) {
                    HashMap<Object, String> hashMap = (HashMap<Object, String>) db.getValue();
                    if (hashMap.get("Id").equals(pId)) {
                        pName = hashMap.get("Name");
                        url = hashMap.get("Url");
                        email=hashMap.get("email");
                        String folder=email.substring(0,email.length()-4);
                        interests.clear();
                        DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("Users").child(db.getKey()).child(folder);
                        ref1.addValueEventListener(new ValueEventListener() {
                            @Override

                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    System.out.println(dataSnapshot.getKey()+"///////////////");
                                    if(dataSnapshot.getValue().equals("1"))
                                        interests.add(dataSnapshot.getKey());
                                }

                                adapter.addAll(interests);
                                spinner.setAdapter(adapter);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                }, 3000);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                }, 3000);

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void trigger() {
        post_btn.setOnClickListener(this);
        img_post.setOnClickListener(this);
    }

    private void Post_Data(final String title, final String description) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        //If post is to be edited then timestamp is going to be the time stamp of the post to be edited not the current timestamp
        if(edit.equals("EditPost"))
            timeStamp = time;
        final String type=spinner.getSelectedItem().toString();
        if(edit.equals("EditPost")){
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Views");
            databaseReference.child(timeStamp).setValue("0");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(timeStamp);
            ref.child("pTitle").setValue(title);
            ref.child("pDesc").setValue(description);
            ref.child("type").setValue(type);
//                ref.child("views").setValue("0");
            if(videouri!=null){
                ref.child("videourl").setValue(videourl);
            }
            if(pdfuri!=null){
                ref.child("pdfurl").setValue(pdfUrl);
            }
            if(audiouri!=null){
                ref.child("audiourl").setValue(audiourrl);
            }
            if(image_rui!=null){
                ref.child("pImage").setValue(imageurl);
            }
            Toast.makeText(getActivity(), "Post Edited!", Toast.LENGTH_SHORT)
                    .show();
            et_desc.setText("");
            et_title.setText("");
            img_post.setImageURI(null);
            image_rui = null;
            postDialog.dismiss();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }else{
            HashMap<Object, Object> hashMap = new HashMap<>();
            hashMap.put("pId", firebaseUser.getUid());
            hashMap.put("pImage",imageurl);
            hashMap.put("pTitle", title);
            hashMap.put("pDesc", description);
            hashMap.put("pTime", timeStamp);
            hashMap.put("pName", pName);
            hashMap.put("url", url);
            hashMap.put("pLikes", "0");
            hashMap.put("pComments", "0");
            hashMap.put("type", type);
            hashMap.put("audiourl",audiourrl);
            hashMap.put("videourl",videourl);
            hashMap.put("pdfurl",pdfUrl);
            hashMap.put("order",-System.currentTimeMillis());
//                                hashMap.put("views","0");

            //Reference of the admin pending post when user post for the first time then it should pass through the admin
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin").child("pendingpost");
            reference.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                                pd.dismiss();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Post Passed to Admin!!", Toast.LENGTH_SHORT)
                                    .show();
                            et_desc.setText("");
                            et_title.setText("");
                            img_post.setImageURI(null);
                            image_rui = null;
                            postDialog.dismiss();
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
   //When image view is clicked show the image dialog show that user will be able to pick the image either from the camera or gallery
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
        //Getting the person name and udp url to upload on the firebase
       DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
       db.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot ds:snapshot.getChildren()){
                   HashMap<String ,Object> hashMap = (HashMap<String, Object>) ds.getValue();
                   if(hashMap.get("Id").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                       pName = (String) hashMap.get("Name");
                       url = (String) hashMap.get("Url");
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
   //Clicking the image from the camera when camera option is selected from the dialog
    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Desc");
        image_rui = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        //Start activity for result with request code
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }
   //Pick image from the gallery with image view in the intent and with request code of the gallery
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    //Check if permission is granted to select from EXTERNAL_STORAGE and then perform accordingly
    private boolean checkStoragePermission() {
        boolean reuslt = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.
                WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return reuslt;
    }

    //If permission is not granted ask for the permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermissions,
                STORAGE_REQUEST_CODE);
    }

    //Check for camera permission
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.
                CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.
                WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    //If camera permission in not given ask for camera permission
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions,
                CAMERA_REQUEST_CODE);
    }

    //After request is made and some action is performed then perform accordingly
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
            //If image is picked from gallery then create is uri and then set it into image view
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_rui = data.getData();
                img_post.setImageURI(image_rui);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                img_post.setImageURI(image_rui);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        //IF request code is of video then retrieve video uri form data and upload it onto the storage
        if(resultCode==RESULT_OK){
            if(requestCode==108){

                videouri = data.getData();
                Toast.makeText(getContext(),"File Attached",Toast.LENGTH_SHORT).show();
                //uploadVideo(videouri);
            }
        }
        //IF request code is of pdf then retrieve pdfuri from data and upload it onto the storage
        if(resultCode==RESULT_OK){
            if(requestCode==PDF_REQUEST){
                pdfuri = data.getData();
                Toast.makeText(getContext(),"File Attached",Toast.LENGTH_SHORT).show();
                //uploadPdf(pdfuri);
            }
        }
        //If request code is of audio then retrieve the audiouri from data and upload it onto storage
        if(resultCode==RESULT_OK){
            if(requestCode==AUDIO_REQUEST){
                audiouri = data.getData();
                Toast.makeText(getContext(),"File Attached",Toast.LENGTH_SHORT).show();
                //uploadAudio(audiouri);
            }
        }

    }

    private void uploadAudio(Uri audiouri) {
        //If storage Permission in not given then go for that first
       if(audiouri==null){
           audiourrl = "empty";
           uploadImage(image_rui);
       }
        if(audiouri!=null){
            //If post is to be edited then timestamp is going to be the time stamp of the post to be edited not the current timestamp

            if (edit.equals("EditPost"))
                timeStamp = time;
            //Upload the audio file onto the storage then download the file and save its uri upload onto the realtimedatabse
            final StorageReference des = reference.child("Audio/"+ timeStamp);
            des.putFile(audiouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    des.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                             audiourrl= uri.toString();
                             uploadImage(image_rui);
                        }
                    });
                }
            });
        }
    }

    private void uploadImage(Uri image_rui) {
        if(image_rui==null){
            imageurl = "noImage";
            uploadPdf(pdfuri);
        }else{
            if(edit.equals("EditPost"))
                timeStamp = time;
            String filePathAndName = "Posts/" + "post_" + timeStamp;
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(image_rui).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           imageurl = uri.toString();
                           uploadPdf(pdfuri);
                        }
                    });
                }
            });

        }
    }


    private void uploadPdf(Uri pdfuri) {
        if(pdfuri==null){
            pdfUrl = "empty";
            String title = et_title.getText().toString().trim();
            String description = et_desc.getText().toString().trim();
            Post_Data(title,description);

        }
        if(pdfuri!=null) {
            //If post is to be edited then timestamp is going to be the time stamp of the post to be edited not the current timestamp
            if (edit.equals("EditPost"))
                timeStamp = time;


            final StorageReference des = reference.child("Pdf/"+ timeStamp);
            //Upload the pdf file onto the storage then download the file and save its uri to upload onto the realtimedatabse
            des.putFile(pdfuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    des.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            pdfUrl = uri.toString();
                            String title = et_title.getText().toString().trim();
                            String description = et_desc.getText().toString().trim();
                            Post_Data(title,description);
                        }
                    });
                }
            });
        }
    }

    private void uploadVideo(Uri videouri) {
        if(videouri==null){
            videourl = "empty";
            uploadAudio(audiouri);
        }
        if(videouri!=null){
            //If post is to be edited then timestamp is going to be the time stamp of the post to be edited not the current timestamp
            if(edit.equals("EditPost"))
                timeStamp = time;
            final StorageReference des = reference.child("Video/"+ timeStamp);
            //Upload the video file onto the storage then download the file and save its uri upload onto the realtimedatabse
            des.putFile(videouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    des.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            videourl = uri.toString();
                            uploadAudio(audiouri);
                        }
                    });
                }
            });
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img:
                Image_dialog();
                break;
                //IF post btn is clicked then post the post in different cases
            case R.id.post_button:
                postDialog = new ProgressDialog(getActivity());
                postDialog.setMessage("Please Wait...");
                postDialog.setCanceledOnTouchOutside(false);
                postDialog.show();
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
                timeStamp= String.valueOf(System.currentTimeMillis());
                uploadVideo(videouri);
                break;

        }
    }
    //On Back Pressed remove all the attached files if any

}
