package com.example.learningeasle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    ImageView profile;
    FirebaseUser user;
    String userid;
    StorageReference reference;
    TextView username, useremail, userstatus;
    FirebaseFirestore fstore;
    String userID;
    FirebaseAuth fAuth;
    Activity context;
    Button editprofile;
    DrawerLayout drawerLayout;
    RecyclerView postlist;
    FirebaseFirestore fStore;
    //FirestoreRecyclerAdapter<Layout,NoteViewHolder> noteAdapter;      //  Takes model class and viewholder
    FirebaseStorage mFirebaseStorage;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profile = view.findViewById(R.id.image);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();//
        reference = FirebaseStorage.getInstance().getReference();
        editprofile = view.findViewById(R.id.editprofile);
        username = view.findViewById(R.id.username);
        useremail = view.findViewById(R.id.email);
        userstatus = view.findViewById(R.id.status);
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,UpdateProfile.class));
            }
        });
        setProfile();

        return view;

    }

    private void setProfile() {
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
                startActivity(new Intent(context, FullView.class));
            }
        });
        userID = fAuth.getCurrentUser().getUid();                                                           //user id stored

        final DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = (String) documentSnapshot.get("fName");
                String email = (String) documentSnapshot.get("email");
                String status = (String) documentSnapshot.get("status");
                username.setText(name);
                useremail.setText(email);
                userstatus.setText(status);
            }
        });


    }
}