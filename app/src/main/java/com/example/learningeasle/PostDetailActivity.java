package com.example.learningeasle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    String hisuId, myEmail, myName, myDp, postId, mylikes, hisDp, hisName,postType;

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
    ImageButton sendbtn, morebtn;
    List<ModelComment> commentList;
    AdapterComments adapterComments;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        profileLayout = findViewById(R.id.profileLayout);
        uDpIV = findViewById(R.id.uDp);

//        System.out.println(pLikesTV+"......"+pCommentsTV);

        pImageIV = findViewById(R.id.pImage);
        nameTV = findViewById(R.id.uname);
        pTimeTV = findViewById(R.id.time);
        pTitleTV = findViewById(R.id.ptitle);
        recyclerView=findViewById(R.id.Recyclerview);
        pDescriptionTV = findViewById(R.id.pdesc);
        pLikesTV = findViewById(R.id.totallikes);
        pCommentsTV=findViewById(R.id.totalcomments);
        pType=findViewById(R.id.pType);
        morebtn = findViewById(R.id.more);
        likebtn = findViewById(R.id.like);
        sharebtn = findViewById(R.id.share);

        commentET = findViewById(R.id.comment);
        sendbtn = findViewById(R.id.sendBtn);
        avatarIV = findViewById(R.id.avtar);


        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        loadPostInfo();

        loadUserInfo();

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
                likePost();
            }
        });
        setLikes();
        LinearLayoutManager layoutManager=new LinearLayoutManager(
                getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        commentList=new ArrayList<>();
        loadComments();
    }

    private void loadComments() {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts").child(postId).
                child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final HashMap<Object, String> hashMap = (HashMap<Object, String>) dataSnapshot.getValue();
                    ModelComment modelComment;

                    modelComment=new ModelComment(hashMap.get("cId"),hashMap.get("comment"),hashMap.get("timeStamp"),
                            hashMap.get("uId"),
                            hashMap.get("uDp"),hashMap.get("uName"));

                    commentList.add(modelComment);



                }
                System.out.println(commentList+",.,.,.,..,.,.,.,.");
                adapterComments=new AdapterComments(getApplicationContext(),commentList);
                recyclerView.setAdapter(adapterComments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setLikes() {
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        final String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).hasChild(myId)) {
                    likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0,
                            0);
                   likebtn.setText("Liked");
                } else {
                   likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0,
                            0);
                    likebtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    boolean processLike = false;

    private void likePost() {
        processLike = true;
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postsref = FirebaseDatabase.getInstance().getReference().child("Posts");
        final String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (processLike)
                    if (snapshot.child(postId).hasChild(myId)) {
                        postsref.child(postId).child("pLikes").setValue("" + (Integer.parseInt(mylikes) - 1));
                        likesRef.child(postId).child(myId).removeValue();
                        processLike = false;

                    } else {
                        postsref.child(postId).child("pLikes").setValue("" + (Integer.parseInt(mylikes) + 1));
                        likesRef.child(postId).child(myId).setValue("Liked");
                        processLike = false;
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
Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
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
                    if(comments==null)
                        comments="0";
                    int newCommentVal = Integer.parseInt(comments)+1 ;
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        ref.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pTitle = "" + snapshot.child("pTitle").getValue();
                String pDesc = "" + snapshot.child("pDesc").getValue();
                mylikes = "" + snapshot.child("pLikes").getValue();
                String pTimeStamp = "" + snapshot.child("pTime").getValue();
                pImage = "" + snapshot.child("pImage").getValue();
                hisuId = (String) snapshot.child("pId").getValue();
                hisDp = "" + snapshot.child("url").getValue();
                hisName = "" + snapshot.child("pName").getValue();
                postType = (String) snapshot.child("type").getValue();
                String CommentCount;

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}