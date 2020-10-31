package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learningeasle.model.AdapterComments;
import com.example.learningeasle.model.AdapterPost;
import com.example.learningeasle.model.ModelComment;
import com.example.learningeasle.model.modelpost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    String hisuId, myEmail, myName, myDp, postId, mylikes, hisDp, hisName, postType,pTitle,pDesc;
    boolean processLike = false;
    ProgressBar progressBar;
    ImageView uDpIV, pImageIV;
    TextView nameTV;
    TextView pTimeTV;
    TextView pTitleTV;
    TextView pDescriptionTV;
    TextView pLikesTV;
    TextView pCommentsTV;
    TextView pType;
    String pImage;
    RecyclerView recyclerView;
    Button likebtn, sharebtn;
    LinearLayout profileLayout;
    EditText commentET;
    ImageView avatarIV;
    ImageButton sendbtn;
    List<ModelComment> commentList;
    AdapterComments adapterComments;
    ProgressDialog progressDialog;
    String audiourl,videourl,pdfurl;
    boolean visibility = true;
    FloatingActionButton attach,pdf,audio,video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        final Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        profileLayout = findViewById(R.id.profileLayout);
        uDpIV = findViewById(R.id.uDp);

//        System.out.println(pLikesTV+"......"+pCommentsTV);

        pImageIV = findViewById(R.id.pImage);
        nameTV = findViewById(R.id.uname);
        pTimeTV = findViewById(R.id.time);
        pTitleTV = findViewById(R.id.ptitle);
        recyclerView = findViewById(R.id.Recyclerview);
        pDescriptionTV = findViewById(R.id.pdesc);
        pLikesTV = findViewById(R.id.totallikes);
        pCommentsTV = findViewById(R.id.totalcomments);
        pType = findViewById(R.id.pType);
        likebtn = findViewById(R.id.like);
        sharebtn = findViewById(R.id.share);
        attach = findViewById(R.id.attachement);
        commentET = findViewById(R.id.comment);
        sendbtn = findViewById(R.id.sendBtn);
        avatarIV = findViewById(R.id.avtar);
        pdf = findViewById(R.id.pdf_upload);
        video = findViewById(R.id.video_upload);
        audio = findViewById(R.id.audio_upload);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        loadPostInfo();

        loadUserInfo();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin").child("Id");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    //Current User is Admin Then Make send Button invisible so that admin wont be able to comment
                    sendbtn.setVisibility(View.GONE);
                    avatarIV.setVisibility(View.GONE);
                    likebtn.setVisibility(View.GONE);
                    commentET.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });

        pImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewImage.class);
                intent.putExtra("image", pImage);
                startActivity(intent);
            }
        });

        likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processLike = true;
                final DatabaseReference postsref = FirebaseDatabase.getInstance().getReference().child("Posts");
                final String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                postsref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (processLike)
                            if (snapshot.child(postId).child("Likes").hasChild(myId)) {
                                postsref.child(postId).child("pLikes").setValue("" +(Integer.parseInt(mylikes) - 1));
                                postsref.child(postId).child("Likes").child(myId).removeValue();
                                processLike = false;
                            } else {

                                postsref.child(postId).child("pLikes").setValue("" + (Integer.parseInt(mylikes) + 1));
                                postsref.child(postId).child("Likes").child(myId).setValue("Liked");
                                processLike = false;
                            }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        //When attach button is clicked mark visibility of those floating btn whose url is not empty
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!audiourl.equals("empty")){
                    audio.setVisibility(View.VISIBLE);
                }
                if(!videourl.equals("empty")){
                    video.setVisibility(View.VISIBLE);
                }
                if(!pdfurl.equals("empty")){
                    pdf.setVisibility(View.VISIBLE);
                }
            }
        });
        //pass the data of the clicked btn
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view_page = new Intent(PostDetailActivity.this,ViewAttachement.class);
                view_page.putExtra("audiourl","empty");
                view_page.putExtra("videourl","empty");
                view_page.putExtra("pdfurl",pdfurl);
                startActivity(view_page);
            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view_page = new Intent(PostDetailActivity.this,ViewAttachement.class);
                view_page.putExtra("audiourl",audiourl);
                view_page.putExtra("videourl","empty");
                view_page.putExtra("pdfurl","empty");
                startActivity(view_page);
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view_page = new Intent(PostDetailActivity.this,ViewAttachement.class);
                view_page.putExtra("audiourl","empty");
                view_page.putExtra("videourl",videourl);
                view_page.putExtra("pdfurl","empty");
                startActivity(view_page);
            }
        });
        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) pImageIV.getDrawable();
                if (bitmapDrawable == null) {
                    shareTextOnly(pTitle, pDesc);
                } else {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageAndText(pTitle, pDesc, bitmap);
                }
            }
        });
        setLikes();
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        commentList = new ArrayList<>();
        loadComments();
    }

    private void shareImageAndText(String pTitle, String pDescription, Bitmap bitmap) {
        String shareBody=pTitle+"\n"+pDescription;
        Uri uri=saveImageInCache(bitmap);
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent,"Share Via"));
    }

    private Uri saveImageInCache(Bitmap bitmap) {
        File imageFolder=new File(getCacheDir(),"images");
        Uri uri=null;
        try{
            imageFolder.mkdirs();
            File file=new File(imageFolder,"shared_image.png");
            FileOutputStream stream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
            stream.flush();
            stream.close();
            uri= FileProvider.getUriForFile(getApplicationContext(),"com.example.learningeasle.fileprovider",file);
        }catch (Exception e){
            e.printStackTrace();
        }
        return uri;
    }

    private void shareTextOnly(String pTitle, String pDescription) {
        String shareBody=pTitle+"\n"+pDescription;
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here"); // for sharing via email
        shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
        startActivity(Intent.createChooser(shareIntent,"Share Via"));
    }


    private void loadComments() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).
                child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
                    ModelComment modelComment;

                    modelComment = new ModelComment(hashMap.get("cId"), hashMap.get("comment"), hashMap.get("timeStamp"),
                            hashMap.get("uId"),
                            hashMap.get("uDp"), hashMap.get("uName"),hashMap.get("postId"));

                    commentList.add(modelComment);


                }
                System.out.println(commentList + ",.,.,.,..,.,.,.,.");
                adapterComments = new AdapterComments(getApplicationContext(), commentList);
                recyclerView.setAdapter(adapterComments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setLikes() {
        final DatabaseReference postsref = FirebaseDatabase.getInstance().getReference().child("Posts");
        final String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postId).hasChild("Likes") && snapshot.child(postId).child("Likes").hasChild(myId)) {
//                        System.out.println(ds.child("Likes")+".........."+myId);
                   likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favourite_border, 0, 0,
                            0);
                    likebtn.setText("Liked");
                }
                else {
                   likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favourite, 0, 0,
                            0);
                    likebtn.setText("Like");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void postComment() {

        String comment = commentET.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(getApplicationContext(), "Comment is empty..", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postId).child("Comments");

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("cId", timeStamp);
        hashMap.put("comment", comment);
        hashMap.put("timeStamp", timeStamp);
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        hashMap.put("uId", myId);
        hashMap.put("uDp", myDp);
        hashMap.put("uName", myName);
        hashMap.put("postId",postId);
        ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");


                Toast.makeText(getApplicationContext(), "Comment Added!!", Toast.LENGTH_SHORT)
                        .show();
                commentET.setText("");
                updateCommentCount();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    boolean mProcessComment = false;

    private void updateCommentCount() {
        mProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mProcessComment) {
                    String comments = (String) snapshot.child("pComments").getValue();
                    if (comments == null)
                        comments = "0";
                    int newCommentVal = Integer.parseInt(comments) + 1;
                    ref.child("pComments").setValue("" + (newCommentVal));
                    mProcessComment = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.child(myId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myName = (String) snapshot.child("Name").getValue();
                myDp = (String) snapshot.child("Url").getValue();

                try {
                    Picasso.get().load(myDp).placeholder(R.drawable.ic_default).into(avatarIV);
                } catch (Exception e) {
                    Picasso.get().load(R.drawable.ic_default).into(avatarIV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPostInfo() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(postId)){
                    ref.child(postId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pTitle = "" + snapshot.child("pTitle").getValue();
                            pDesc = "" + snapshot.child("pDesc").getValue();
                            mylikes = "" + snapshot.child("pLikes").getValue();
                            String pTimeStamp = (String) snapshot.child("pTime").getValue();
                            pImage = "" + snapshot.child("pImage").getValue();
                            hisuId = (String) snapshot.child("pId").getValue();
                            hisDp = "" + snapshot.child("url").getValue();
                            hisName = "" + snapshot.child("pName").getValue();
                            postType = (String) snapshot.child("type").getValue();
                            audiourl = (String) snapshot.child("audiourl").getValue();
                            videourl = (String) snapshot.child("videourl").getValue();
                            pdfurl = (String) snapshot.child("pdfurl").getValue();

                            if(!audiourl.equals("empty")||!(videourl.equals("empty"))||!(pdfurl.equals("empty"))){
                                attach.setVisibility(View.VISIBLE);
                            }

                            String CommentCount;
                            if (pTimeStamp != null) {
                                if (snapshot.child("pComments").getValue() == null)
                                    CommentCount = "0";
                                else
                                    CommentCount = "" + snapshot.child("pComments").getValue();

                                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                calendar.setTimeInMillis(Long.parseLong(pTimeStamp));

                                String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

                                pTitleTV.setText(pTitle);
                                pDescriptionTV.setText(pDesc);
                                pLikesTV.setText(mylikes + " Likes");
                                pTimeTV.setText(pTime);
                                nameTV.setText(hisName);
                                pCommentsTV.setText(CommentCount + " Comments");
                                pType.setText(postType);


                                if (pImage.equals("noImage")) {
                                    pImageIV.setVisibility(View.GONE);
                                } else {
                                    try {
                                        pImageIV.setVisibility(View.VISIBLE);
                                        Picasso.get().load(String.valueOf(pImage)).placeholder(R.drawable.ic_default).fit().centerCrop().
                                                into(pImageIV);
                                    } catch (Exception e) {
                                    }
                                }


                                // in comment , dp
                                try {
                                    Picasso.get().load(hisDp).placeholder(R.drawable.ic_default)
                                            .into(uDpIV);
                                } catch (Exception e) {
                                    Picasso.get().load(R.drawable.ic_default)
                                            .into(uDpIV);
                                }
                                progressDialog.dismiss();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}