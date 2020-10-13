package com.example.learningeasle.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.PostFragment;
import com.example.learningeasle.ProfileFragment;
import com.example.learningeasle.R;
import com.example.learningeasle.ViewImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.PostHolder> {
    Context context;
    List<modelpost> postList;
    private String pName;
    private DatabaseReference likesRef;
    DatabaseReference postsref;
    String myId;
    boolean processLike=false;
    public Adapter(Context context, List<modelpost> postList) {
        this.context = context;
        this.postList = postList;
        myId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        postsref= FirebaseDatabase.getInstance().getReference().child("Posts");
        //
    }

    @NonNull
    @Override
    public Adapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, final int position) {
        final String uName=postList.get(position).getpName();
        final String url=postList.get(position).getuImage();
        final String pTitle = postList.get(position).getpTitle();
        final String pDescription = postList.get(position).getpDesc();
        final String pImage = postList.get(position).getpImage();
        final String pTimeStamp = postList.get(position).getpTime();
        final String pId = postList.get(position).getpId();
        final String pLikes=postList.get(position).getpLikes();




        holder.pName.setText(uName);

        if(url.equals("empty"))
            holder.url.setImageResource(R.drawable.ic_action_account);
        else
            Picasso.get().load(url).into(holder.url);
        System.out.println(pDescription+"  ..  "+pImage);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));

        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();


        if (pImage.equals("noImage")) {
            System.out.println(pTitle+"  . "+pDescription);
            holder.pImage.setVisibility(View.GONE);
        } else {
            try {
                holder.pImage.setVisibility(View.VISIBLE);
                Picasso.get().load(pImage).placeholder(R.drawable.ic_default).fit().centerCrop().into(holder.pImage);
            }catch (Exception e){}
        }

        holder.pTime.setText(pTime);
        holder.pTitle.setText(pTitle);
        holder.pDesc.setText(pDescription);
        holder.pTotalLikes.setText(pLikes +" Likes");

        setLikes(holder,pTimeStamp);

        holder.morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                        String[] options = {"Edit", "Delete","Cancel"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {

                                  /*  PostFragment postFragment = new PostFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("Name",uName);
                                    bundle.putString("url",url);
                                    bundle.putString("Title",pTitle);
                                    bundle.putString("Description",pDescription);
                                    bundle.putString("Image",pImage);
                                    bundle.putString("TimeStamp",pTimeStamp);
                                    bundle.putString("Likes",pLikes);
                                    postFragment.setArguments(bundle);*/
                                }

                                if (i == 1) {
                                   String imagePath = "Posts/" + "post_"+ pTimeStamp;
                                    if (pImage.equals("noImage")) {

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts" + pTimeStamp);
                                        databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("tag", "Error" + e);
                                            }
                                        });

                                    }else{
                                        StorageReference reference = FirebaseStorage.getInstance().getReference().child(imagePath);
                                        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText( context,"Image Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts" + pTimeStamp);
                                        databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("tag", "Error" + e);
                                            }
                                        });

                                    }
                                }

                            }
                        });
                        builder.create().show();

            }
        });
        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
                final int pLikes;
                String likes=postList.get(position).getpLikes();
                if(likes==null)
                    pLikes=0;
                else
                    pLikes= Integer.parseInt(postList.get(position).getpLikes());
                processLike=true;
                final String stamp=postList.get(position).getpTime();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(processLike)
                            if(snapshot.child(stamp).hasChild(myId)) {
                                postsref.child(stamp).child("pLikes").setValue(""+(pLikes - 1));
                                likesRef.child(stamp).child(myId).removeValue();
                                processLike=false;
                            }
                            else{
                                postsref.child(stamp).child("pLikes").setValue(""+(pLikes+1));
                                likesRef.child(stamp).child(myId).setValue("Liked");
                                processLike=false;
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });
        holder.share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
            }
        });

        holder.pImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ViewImage.class);
                intent.putExtra("image",pImage);
                context.startActivity(intent);

            }
        });
    }

    private void setLikes(final PostHolder holder, final String pTimeStamp) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(pTimeStamp).hasChild(myId)){
                    holder.like_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked,0,0,
                            0);
                    holder.like_btn.setText("Liked");
                }
                else
                {
                    holder.like_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,0,0,
                            0);
                    holder.like_btn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder {

        ImageView url;
        ImageView pImage;
        TextView pName, pTime, pTitle, pDesc, pTotalLikes;
        ImageButton morebtn;
        Button like_btn, share_btn, comment_btn;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            url=itemView.findViewById(R.id.uDp);
            pImage = itemView.findViewById(R.id.pImage);
            pName=itemView.findViewById(R.id.uname);
            pTime = itemView.findViewById(R.id.time);
            pTitle = itemView.findViewById(R.id.ptitle);
            pDesc = itemView.findViewById(R.id.pdesc);
            pTotalLikes = itemView.findViewById(R.id.totallikes);
            morebtn = (ImageButton) itemView.findViewById(R.id.more);
            like_btn = itemView.findViewById(R.id.like);
            share_btn = itemView.findViewById(R.id.share);
            comment_btn = itemView.findViewById(R.id.comment);
        }
    }

}
