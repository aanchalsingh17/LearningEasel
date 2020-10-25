package com.example.learningeasle.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.PostDetailActivity;
import com.example.learningeasle.R;
import com.example.learningeasle.ViewAttachement;
import com.example.learningeasle.model.AdapterPost;
import com.example.learningeasle.model.modelpost;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterAdminPost extends RecyclerView.Adapter<AdapterAdminPost.MyHolder> {
    View view;
    Context context;
    List<modelpost> postList;

    public AdapterAdminPost(Context context, List<modelpost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adminrowpost, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        String uName = postList.get(position).getpName();
        String pComments = postList.get(position).getpComments();
        String url = postList.get(position).getuImage();
        final String pTitle = postList.get(position).getpTitle();
        final String pDescription = postList.get(position).getpDesc();
        final String pImage = postList.get(position).getpImage();
        final String pTimeStamp = postList.get(position).getpTime();
        final String pId = postList.get(position).getpId();
        final String pType = postList.get(position).getpType();
        String pLikes = postList.get(position).getpLikes();
        final String[] viewsCount = new String[1];
        final String videourl = postList.get(position).getVideourl();
        final String audiourl = postList.get(position).getAudiourl();
        final String pdfurl = postList.get(position).getPdfurl();
        //Initialise Shimmer
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();
        //Initialise shimmer Drawable
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

       if(!videourl.equals("empty")||!(audiourl.equals("empty"))||!(pdfurl.equals("empty"))){
           holder.attachement.setVisibility(View.VISIBLE);
       }

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Views");
        databaseReference.child(pTimeStamp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                viewsCount[0] =snapshot.getValue().toString();
                System.out.println(viewsCount[0]+" idhar");
                int viewsCnt = Integer.parseInt(viewsCount[0]);
                System.out.println(viewsCnt + "= views");
                viewsCnt++;

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Views");
                ref.child(pTimeStamp).setValue(Integer.toString(viewsCnt));
                holder.views.setText(viewsCount[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.uName.setText(uName);
        holder.pType.setText(pType);
        holder.views.setText(viewsCount[0]);
        if (url.equals("empty"))
            holder.uDp.setImageResource(R.drawable.ic_action_account);
        else
            Picasso.get().load(url).placeholder(shimmerDrawable).into(holder.uDp);
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));

        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        if (pComments == null)
            pComments = "0";
        holder.pTotalComment.setText(pComments + " Comments");



        if (pImage.equals("noImage")) {
            System.out.println(pTitle + "  . " + pDescription);
            holder.pImage.setVisibility(View.GONE);
        } else {
            try {
                holder.pImage.setVisibility(View.VISIBLE);
                Picasso.get().load(pImage).placeholder(shimmerDrawable).fit().centerCrop().into(holder.pImage);
            } catch (Exception e) {
            }
        }
        holder.pTime.setText(pTime);
        holder.pTitle.setText(pTitle);
        holder.pDesc.setText(pDescription);
        holder.pTotalLikes.setText(pLikes + " Likes");
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pTimeStamp);
                context.startActivity(intent);
            }
        });
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pTimeStamp);
                context.startActivity(intent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Before deleting the post delete it from the bookmarks section of the users;
                final DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String path = ds.getKey();
                            if(ds.child("Bookmarks").hasChild(pTimeStamp)){
                                ref.child(path).child("Bookmarks").child(pTimeStamp).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if (pImage.equals("noImage")) {
                    final ProgressDialog pd = new ProgressDialog(context);
                    pd.setMessage("Deleting....");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(pTimeStamp);
                    if (!reference.child("pComments").equals("0"))
                        reference.child("Comments").removeValue();
                    reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Unable to Delete", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }else{
                    final ProgressDialog pd = new ProgressDialog(context);
                    pd.setMessage("Deleting....");

                    StorageReference picref = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
                    picref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot ds:snapshot.getChildren()){
                                        HashMap<String,Object> hashMap = (HashMap<String, Object>) ds.getValue();
                                        if(hashMap.get("pTime").equals(pTimeStamp))
                                            ds.getRef().removeValue();
                                        Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                    pd.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(context,"Unable to delete Post",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
        }

        });
        holder.attachement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewAttachement.class);
                intent.putExtra("videourl",videourl);
                intent.putExtra("audiourl",audiourl);
                intent.putExtra("pdfurl",pdfurl);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        ImageView uDp,pImage,delete;
        TextView uName, pTime, pTitle, pDesc, pTotalLikes,pTotalComment,pType,views;
        Button comment_btn;
        FloatingActionButton attachement;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            uDp = itemView.findViewById(R.id.uDp);
            pImage = itemView.findViewById(R.id.pImage);
            uName = itemView.findViewById(R.id.uname);
            pTime = itemView.findViewById(R.id.time);
            pTitle = itemView.findViewById(R.id.ptitle);
            pDesc = itemView.findViewById(R.id.pdesc);
            pTotalLikes = itemView.findViewById(R.id.totallikes);
            comment_btn = itemView.findViewById(R.id.comment);
            pTotalComment=itemView.findViewById(R.id.totalcomments);
            pType=itemView.findViewById(R.id.pType);
            views=itemView.findViewById(R.id.viewCount);
            delete = itemView.findViewById(R.id.delete);
            attachement = itemView.findViewById(R.id.view_attached);

        }


    }
}
