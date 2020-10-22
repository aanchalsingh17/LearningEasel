package com.example.learningeasle.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.PostDetailActivity;
import com.example.learningeasle.R;
import com.example.learningeasle.UserDetails.UserProfile;
import com.example.learningeasle.ViewImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {
     Context context;
    List<modelpost> postList;
    private String pName;
    DatabaseReference postsref;
    String myId;
    View view;
    boolean processLike = false;
    String pId;
     String pImage;
     String pTimeStamp;
    public AdapterPost(Context context, List<modelpost> postList) {
        this.context = context;
        this.postList = postList;
        myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postsref = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        //String uId=postList.get(position).getuId();
//        String uEmail=postList.get(position).getuEmail();
        String uName = postList.get(position).getpName();
        String pComments = postList.get(position).getpComments();
        String url = postList.get(position).getuImage();
        final String pTitle = postList.get(position).getpTitle();
        final String pDescription = postList.get(position).getpDesc();
        pImage = postList.get(position).getpImage();
        pTimeStamp = postList.get(position).getpTime();
        pId = postList.get(position).getpId();
        final String pType = postList.get(position).getpType();
        String viewsCount = postList.get(position).getViews();
        String pLikes = postList.get(position).getpLikes();

//
//        int viewsCnt = Integer.parseInt(viewsCount);
//        System.out.println(viewsCnt + "= views");
//        viewsCnt++;
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
//        ref.child(pTimeStamp).child("views").setValue(Integer.toString(viewsCnt));


        holder.uName.setText(uName);
        holder.pType.setText(pType);
        holder.views.setText(viewsCount);
        holder.uName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(pId)) {
                    Intent intent = new Intent(context, UserProfile.class);
                    intent.putExtra("Id", pId);
                    context.startActivity(intent);
                } else {
                    /*AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment myFragment = new ProfileFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.home, myFragment).addToBackStack(null).commit();*/
                    Toast.makeText(context, "Go to Profile to view your profile", Toast.LENGTH_SHORT).show();
                }

            }
        });
        if (url.equals("empty"))
            holder.uDp.setImageResource(R.drawable.ic_action_account);
        else
            Picasso.get().load(url).into(holder.uDp);
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
                Picasso.get().load(pImage).placeholder(R.drawable.ic_default).fit().centerCrop().into(holder.pImage);
            } catch (Exception e) {
            }
        }
        //Checking if the current user is admin if it is so then mark more btn visible n bookmark invisible and viceversa
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("admin").child("Id");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(myId)) {
                    holder.boookmark.setVisibility(View.GONE);
                    holder.morebtn.setVisibility(View.GONE);
                } else {
                    holder.morebtn.setVisibility(View.GONE);
                    holder.delete.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.pTime.setText(pTime);
        holder.pTitle.setText(pTitle);
        holder.pDesc.setText(pDescription);
        holder.pTotalLikes.setText(pLikes + " Likes");

        setLikes(holder, pTimeStamp);


        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
                final int pLikes;
                String likes = postList.get(position).getpLikes();
                if (likes == null)
                    pLikes = 0;
                else
                    pLikes = Integer.parseInt(postList.get(position).getpLikes());
                processLike = true;
                final String stamp = postList.get(position).getpTime();
                postsref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (processLike)
                            if (snapshot.child(stamp).child("Likes").hasChild(myId)) {
                                postsref.child(stamp).child("pLikes").setValue("" + (pLikes - 1));
                                postsref.child(stamp).child("Likes").child(myId).removeValue();
                                processLike = false;
                            } else {

                                postsref.child(stamp).child("pLikes").setValue("" + (pLikes + 1));
                                postsref.child(stamp).child("Likes").child(myId).setValue("Liked");
                                processLike = false;
                            }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
        setBookmark(holder, myId, pId, pTimeStamp);
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pTimeStamp);
                context.startActivity(intent);
            }
        });
        holder.share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.pImage.getDrawable();
                if (bitmapDrawable == null) {
                    shareTextOnly(pTitle, pDescription);
                } else {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(pTitle, pDescription, bitmap);
                }
            }
        });
        holder.boookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myId);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("Bookmarks").hasChild(pTimeStamp)) {
                            reference.child("Bookmarks").child(pTimeStamp).removeValue();
                        } else {
                            reference.child("Bookmarks").child(pTimeStamp).setValue(pId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        holder.pImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewImage.class);
                intent.putExtra("image", pImage);
                context.startActivity(intent);

            }
        });
       holder.delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(context,"Delete",Toast.LENGTH_SHORT).show();
               if(pImage.equals("noImage")){
                   final ProgressDialog pd = new ProgressDialog(context);
                   pd.setMessage("Deleting....");
                   //If there in no image than we need to delete this only from the realtime database
                   DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts").child(pTimeStamp);
                   reference1.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(context, "Unable to Complete Task", Toast.LENGTH_SHORT).show();
                       }
                   });
                   pd.dismiss();
                  /* Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                   query.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           for(DataSnapshot ds:snapshot.getChildren()){
                               HashMap<String,Object> hashMap = (HashMap<String, Object>) ds.getValue();
                               if (hashMap.get("pTime").equals(pTimeStamp)) {
                                   ds.getRef().removeValue();

                               }






                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });*/
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
    }

    private void setBookmark(final MyHolder holder, String myId, final String pId, final String pTimeStamp) {
        final DatabaseReference Posts = FirebaseDatabase.getInstance().getReference("Posts")
                .child(pTimeStamp);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myId);//.child("Bookmarks");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Bookmarks").hasChild(pTimeStamp)){
                    holder.boookmark.setBackgroundColor(R.drawable.swipe_button_background);
                }else{
                    holder.boookmark.setBackgroundColor(R.drawable.ic_bookmarks);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void shareImageAndText(String pTitle, String pDescription, Bitmap bitmap) {
        String shareBody=pTitle+"\n"+pDescription;
        Uri uri=saveImageInCache(bitmap);
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        shareIntent.setType("image/png");
        context.startActivity(Intent.createChooser(shareIntent,"Share Via"));
    }

    private Uri saveImageInCache(Bitmap bitmap) {
        File imageFolder=new File(context.getCacheDir(),"images");
        Uri uri=null;
        try{
            imageFolder.mkdirs();
            File file=new File(imageFolder,"shared_image.png");
            FileOutputStream stream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
            stream.flush();
            stream.close();
            uri= FileProvider.getUriForFile(context,"com.example.learningeasle.fileprovider",file);
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
        context.startActivity(Intent.createChooser(shareIntent,"Share Via"));
    }

    private void setLikes(final MyHolder holder, final String pTimeStamp) {
       postsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.child(pTimeStamp).hasChild("Likes") && snapshot.child(pTimeStamp).child("Likes").hasChild(myId)) {
//                        System.out.println(ds.child("Likes")+".........."+myId);
                        holder.like_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0,
                                0);
                        holder.like_btn.setText("Liked");
                    }
                     else {
                        holder.like_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0,
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

    static class MyHolder extends RecyclerView.ViewHolder {

        ImageView uDp,pImage,boookmark,delete;
        TextView uName, pTime, pTitle, pDesc, pTotalLikes,pTotalComment,pType,views;
        ImageButton morebtn;
        Button like_btn, share_btn, comment_btn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            uDp = itemView.findViewById(R.id.uDp);
            pImage = itemView.findViewById(R.id.pImage);
            uName = itemView.findViewById(R.id.uname);
            pTime = itemView.findViewById(R.id.time);
            pTitle = itemView.findViewById(R.id.ptitle);
            pDesc = itemView.findViewById(R.id.pdesc);
            pTotalLikes = itemView.findViewById(R.id.totallikes);
            morebtn = (ImageButton) itemView.findViewById(R.id.more);
            like_btn = itemView.findViewById(R.id.like);
            share_btn = itemView.findViewById(R.id.share);
            comment_btn = itemView.findViewById(R.id.comment);
            pTotalComment=itemView.findViewById(R.id.totalcomments);
            boookmark = itemView.findViewById(R.id.bookmarks);
            pType=itemView.findViewById(R.id.pType);
            views=itemView.findViewById(R.id.viewCount);
            delete = itemView.findViewById(R.id.delete);

        }


    }
}
