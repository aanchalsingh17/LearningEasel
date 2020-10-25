package com.example.learningeasle.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningeasle.PostDetailActivity;
import com.example.learningeasle.R;
import com.example.learningeasle.UserDetails.UserProfile;
import com.example.learningeasle.ViewAttachement;
import com.example.learningeasle.ViewImage;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
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
        final String pImage = postList.get(position).getpImage();
        final String pTimeStamp = postList.get(position).getpTime();
        final String pId = postList.get(position).getpId();
        final String pType = postList.get(position).getpType();
        String pLikes = postList.get(position).getpLikes();
        final String videourl = postList.get(position).getVideourl();
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

        System.out.println(pTitle+" "+pDescription+" "+pName);

        if(!videourl.equals("empty")||!(pdfurl.equals("empty"))){
            holder.attached.setVisibility(View.VISIBLE);
        }

        final String[] viewsCount = new String[1];

        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Views");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.getKey().equals(pTimeStamp)){
                        viewsCount[0] =ds.getValue().toString();
                        int viewsCnt = Integer.parseInt(viewsCount[0]);
                        viewsCnt++;

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Views");
                        ref.child(pTimeStamp).setValue(Integer.toString(viewsCnt));
                        holder.views.setText(viewsCount[0]);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.uName.setText(uName);
        holder.pType.setText(pType);
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
        holder.morebtn.setVisibility(View.GONE);
        holder.pTime.setText(pTime);
        holder.pTitle.setText(pTitle);
        holder.pDesc.setText(pDescription);


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
//
//                        if (processLike)
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
        holder.pTotalLikes.setText(pLikes + " Likes");
        setBookmark(holder, myId, pId, pTimeStamp);
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(pTimeStamp+" cmnt");
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
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(myId).child("Bookmarks");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(pTimeStamp)) {
                            reference.child(pTimeStamp).removeValue();
                            holder.boookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_bookmark_border_24));
                        } else {
                            reference.child(pTimeStamp).setValue(pId);
                            holder.boookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmarked));
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
        holder.attached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(context, ViewAttachement.class);
                intent.putExtra("videourl",videourl);
                intent.putExtra("pdfurl",pdfurl);
                context.startActivity(intent);
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
                    holder.boookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmarked));
                }else{

                    holder.boookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_bookmark_border_24));
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
       postsref.addListenerForSingleValueEvent(new ValueEventListener() {
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

        ImageView uDp,pImage,boookmark;
        TextView uName, pTime, pTitle, pDesc, pTotalLikes,pTotalComment,pType,views;
        ImageButton morebtn;
        Button like_btn, share_btn, comment_btn;
        FloatingActionButton attached;
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
            attached=itemView.findViewById(R.id.view_attached);

        }


    }
}
